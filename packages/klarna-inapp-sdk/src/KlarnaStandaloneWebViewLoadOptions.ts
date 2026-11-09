// Internal types: the public API is the `source` prop, which builds these.
export interface KlarnaWebViewCookie {
  readonly name: string;
  readonly value: string;
  /** The merchant domain; the cookie also applies to its subdomains. Klarna domains are rejected. */
  readonly domain: string;
  /** Defaults to '/'. */
  readonly path?: string;
  /** Defaults to true. Pass false only for a non-production http host. */
  readonly secure?: boolean;
  readonly httpOnly?: boolean;
  /** Stores the cookie for the domain only, not its subdomains, like a browser does for a Cookie header. */
  readonly hostOnly?: boolean;
  /** Expiry in epoch milliseconds. Without it the cookie only lasts for the session. */
  readonly expires?: number;
  /** Removes same-name cookies for this host, at any path of `requestPath`, so this one is the only one sent. */
  readonly replaceConflicting?: boolean;
  /** The path of the page being loaded; used with `replaceConflicting`. Defaults to '/'. */
  readonly requestPath?: string;
}

export interface KlarnaWebViewLoadOptions {
  // Sent with the first request only. A Cookie header is rejected.
  readonly headers?: { readonly [name: string]: string };
}

// Merchant headers and cookies are never sent to these hosts or their subdomains.
const KLARNA_DOMAINS = [
  'klarna.com',
  'klarna.net',
  'klarnacdn.net',
  'klarnaservices.com',
  'klarnaevt.com',
];

// A registrable host name: dot-separated labels, ending in an alphabetic or punycode (xn--) TLD.
const DOMAIN =
  /^(?=.{1,253}$)(?:[a-z0-9](?:[a-z0-9-]{0,61}[a-z0-9])?\.)+(?:[a-z]{2,63}|xn--[a-z0-9-]{1,59}[a-z0-9])$/;

// A header name is an RFC 7230 token; anything else makes iOS raise an exception.
const HEADER_NAME = /^[!#$%&'*+\-.^_`|~0-9A-Za-z]+$/;

// Characters that would let a name, value or path add attributes to the cookie.
const FORBIDDEN_COOKIE_NAME = /[\s;,="()<>@:\\/[\]?{}]/;
const FORBIDDEN_COOKIE_VALUE = /[\s;,"\\]/;
const FORBIDDEN_COOKIE_PATH = /[\s;]/;

function hasControlCharacter(text: string): boolean {
  return [...text].some((c) => c.charCodeAt(0) < 0x20 || c === '\u007f');
}

// Reduces a host to the form the web view navigates to, so `klarna%2ecom` cannot slip past the check.
function canonicalHost(host: string): string {
  let decoded = host;
  try {
    decoded = decodeURIComponent(host);
  } catch {
    // Malformed escapes are left as they are; the web view will not resolve them to a Klarna host.
  }
  return decoded
    .replace(/[。．｡]/g, '.')
    .toLowerCase()
    .replace(/\.+$/, '');
}

// The URL parser drops tabs and newlines anywhere in the URL and trims surrounding C0 controls and spaces.
function preprocessURL(url: string): string {
  const chars = [...url].filter((c) => !'\t\n\r'.includes(c));
  const trimmed = (c: string | undefined) =>
    c != null && (c.charCodeAt(0) <= 0x20 || /\s/.test(c));
  while (trimmed(chars[0])) {
    chars.shift();
  }
  while (trimmed(chars[chars.length - 1])) {
    chars.pop();
  }
  return chars.join('');
}

// A port is optional. When present it has to be a number the web view can open.
function isValidPort(port: string): boolean {
  if (!/^\d{1,5}$/.test(port)) {
    return false;
  }
  return Number(port) <= 65535;
}

// Returns the host of `host`, `host:port`, or `[ipv6]:port` (userinfo already removed), or undefined for a bad port.
function hostOfHostPort(hostport: string): string | undefined {
  if (hostport.startsWith('[')) {
    const end = hostport.indexOf(']');
    if (end < 1) {
      return undefined;
    }
    const rest = hostport.slice(end + 1);
    if (
      rest.length > 0 &&
      !(rest.startsWith(':') && isValidPort(rest.slice(1)))
    ) {
      return undefined;
    }
    return hostport.slice(1, end);
  }
  const colon = hostport.indexOf(':');
  if (colon < 0) {
    return hostport;
  }
  return isValidPort(hostport.slice(colon + 1))
    ? hostport.slice(0, colon)
    : undefined;
}

// Reads the host the way the web view does: a backslash ends the authority and the host follows the last `@`.
function hostOf(url: string): string | undefined {
  const authority = /^https?:\/\/([^/\\?#]*)/i.exec(preprocessURL(url))?.[1];
  if (authority == null) {
    return undefined;
  }
  const rawHost = hostOfHostPort(
    authority.slice(authority.lastIndexOf('@') + 1)
  );
  if (rawHost == null) {
    return undefined;
  }
  const host = canonicalHost(rawHost);
  return host.length === 0 ? undefined : host;
}

// Throws unless the URL has a scheme and a host, which native needs before it loads anything.
export function validateSourceURL(url: unknown): void {
  const text = typeof url === 'string' ? preprocessURL(url) : '';
  const httpHostMissing =
    /^https?:\/\//i.test(text) && hostOf(url as string) == null;
  if (
    typeof url !== 'string' ||
    !/^[a-z][a-z0-9+.-]*:\/\/[^/\\?#]+/i.test(text) ||
    httpHostMissing
  ) {
    throw new Error(
      `KlarnaStandaloneWebView: source.uri is not a valid URL (${String(url)}).`
    );
  }
}

// Resolves `.` and `..` segments, literal or percent-encoded, as the web view does before requesting.
function resolveDotSegments(path: string): string {
  const out: string[] = [];
  const segments = path.split('/');
  segments.forEach((segment, i) => {
    const dots = segment.replace(/%2e/gi, '.');
    const last = i === segments.length - 1;
    if (dots === '..') {
      if (out.length > 1) {
        out.pop();
      }
      if (last) {
        out.push('');
      }
    } else if (dots === '.') {
      if (last) {
        out.push('');
      }
    } else {
      out.push(segment);
    }
  });
  return out.join('/');
}

// Percent-encodes a path character the way the URL parser does; existing escapes are left as they are.
function encodePathCharacter(c: string): string {
  const code = c.charCodeAt(0);
  if (code > 0x1f && code < 0x7f && !' "<>`{}'.includes(c)) {
    return c;
  }
  try {
    return encodeURIComponent(c);
  } catch {
    // A lone surrogate is sent as U+FFFD.
    return '%EF%BF%BD';
  }
}

// The path as the web view requests it (percent-encoded), cut before an unsafe segment.
function requestPathOf(url: string): string {
  const afterAuthority = /^https?:\/\/[^/\\?#]*([^?#]*)/i.exec(
    preprocessURL(url)
  )?.[1];
  const path = [
    ...resolveDotSegments((afterAuthority ?? '').replace(/\\/g, '/')),
  ]
    .map(encodePathCharacter)
    .join('');
  const unsafe = [...path].findIndex((c) => /[\s;]/.test(c));
  const safe =
    unsafe < 0 ? path : path.slice(0, path.lastIndexOf('/', unsafe) + 1);
  return safe.startsWith('/') ? safe : '/';
}

function isSameOrSubdomain(host: string, domain: string): boolean {
  return host === domain || host.endsWith(`.${domain}`);
}

function isKlarnaDomain(host: string): boolean {
  return KLARNA_DOMAINS.some((domain) => isSameOrSubdomain(host, domain));
}

// Validates the load options and serializes them for the native load command, or returns "".
export function serializeLoadOptions(
  url: string,
  options: KlarnaWebViewLoadOptions | undefined
): string {
  const headers = options?.headers;
  if (headers == null || Object.keys(headers).length === 0) {
    return '';
  }

  const host = hostOf(url);
  if (host == null) {
    throw new Error(
      'KlarnaStandaloneWebView: headers can only be sent with an http or https URL.'
    );
  }
  // The web view maps non-ASCII hosts to another name, so only the punycode form can be checked.
  if ([...host].some((c) => c.charCodeAt(0) > 0x7f)) {
    throw new Error(
      `KlarnaStandaloneWebView: headers need a host written in ASCII; use the punycode form (${host}).`
    );
  }
  if (isKlarnaDomain(host)) {
    throw new Error(
      `KlarnaStandaloneWebView: headers cannot be sent to Klarna pages (${host}).`
    );
  }
  Object.entries(headers).forEach(([name, value]) => {
    if (name.toLowerCase() === 'cookie') {
      throw new Error(
        'KlarnaStandaloneWebView: a Cookie header is only supported in source.headers.'
      );
    }
    if (
      !HEADER_NAME.test(name) ||
      typeof value !== 'string' ||
      hasControlCharacter(value)
    ) {
      throw new Error(`KlarnaStandaloneWebView: invalid header "${name}".`);
    }
  });
  return JSON.stringify({ headers });
}

// A cookie value may be wrapped in one pair of double quotes, which stay part of the value.
function unquoted(value: string): string {
  return value.length >= 2 && value.startsWith('"') && value.endsWith('"')
    ? value.slice(1, -1)
    : value;
}

function serializeCookie(cookie: KlarnaWebViewCookie) {
  const { name, value } = cookie;
  if (
    typeof name !== 'string' ||
    typeof value !== 'string' ||
    name.length === 0 ||
    FORBIDDEN_COOKIE_NAME.test(name) ||
    FORBIDDEN_COOKIE_VALUE.test(unquoted(value)) ||
    hasControlCharacter(name) ||
    hasControlCharacter(value)
  ) {
    throw new Error(`KlarnaStandaloneWebView: invalid cookie "${name}".`);
  }
  const domain =
    typeof cookie.domain === 'string'
      ? cookie.domain.replace(/^\./, '').toLowerCase()
      : '';
  if (!DOMAIN.test(domain)) {
    throw new Error(
      `KlarnaStandaloneWebView: cookie "${name}" needs a valid merchant domain.`
    );
  }
  if (isKlarnaDomain(domain)) {
    throw new Error(
      `KlarnaStandaloneWebView: cookie "${name}" cannot be set for a Klarna domain (${domain}).`
    );
  }
  // Native reads any non-true value as false, so a string like 'false' must not pass.
  (['secure', 'httpOnly', 'hostOnly', 'replaceConflicting'] as const).forEach(
    (attribute) => {
      const flag: unknown = cookie[attribute];
      if (flag != null && typeof flag !== 'boolean') {
        throw new Error(
          `KlarnaStandaloneWebView: cookie "${name}" needs a boolean ${attribute}.`
        );
      }
    }
  );
  const path = cookie.path ?? '/';
  const requestPath = cookie.requestPath ?? '/';
  if (
    typeof requestPath !== 'string' ||
    !requestPath.startsWith('/') ||
    FORBIDDEN_COOKIE_PATH.test(requestPath) ||
    hasControlCharacter(requestPath)
  ) {
    throw new Error(
      `KlarnaStandaloneWebView: cookie "${name}" has an invalid request path.`
    );
  }
  if (
    !path.startsWith('/') ||
    FORBIDDEN_COOKIE_PATH.test(path) ||
    hasControlCharacter(path)
  ) {
    throw new Error(
      `KlarnaStandaloneWebView: cookie "${name}" has an invalid path.`
    );
  }
  if (cookie.expires != null && !Number.isFinite(cookie.expires)) {
    throw new Error(
      `KlarnaStandaloneWebView: cookie "${name}" has an invalid expiry.`
    );
  }
  return {
    name,
    value,
    domain,
    path,
    secure: cookie.secure ?? true,
    httpOnly: cookie.httpOnly ?? false,
    hostOnly: cookie.hostOnly ?? false,
    ...(cookie.expires != null ? { expires: cookie.expires } : {}),
    ...(cookie.replaceConflicting === true
      ? { replaceConflicting: true, requestPath }
      : {}),
  };
}

// Moves every `Cookie` header (any letter case) out of the headers into cookies for the URL's host, like a browser would send them.
export function splitCookieHeader(
  url: string,
  headers: { readonly [name: string]: string } | undefined
): {
  headers: { [name: string]: string } | undefined;
  cookies: KlarnaWebViewCookie[];
} {
  const cookieNames = Object.keys(headers ?? {}).filter(
    (name) => name.toLowerCase() === 'cookie'
  );
  if (headers == null || cookieNames.length === 0) {
    return { headers: headers && { ...headers }, cookies: [] };
  }
  const host = hostOf(url);
  if (host == null) {
    throw new Error(
      'KlarnaStandaloneWebView: a Cookie header in source.headers needs an http or https URL.'
    );
  }
  const secure = /^https:/i.test(preprocessURL(url));
  const cookies = cookieNames
    .flatMap((name) => (headers[name] ?? '').split(';'))
    .map((pair) => pair.trim())
    .filter((pair) => pair.length > 0)
    .map((pair) => {
      const separator = pair.indexOf('=');
      return {
        name: separator < 0 ? pair : pair.slice(0, separator).trim(),
        value: separator < 0 ? '' : pair.slice(separator + 1).trim(),
        domain: host,
        path: '/',
        secure,
        hostOnly: true,
        // The header replaced whatever the request would have sent, so it must win in the cookie store too.
        replaceConflicting: true,
        requestPath: requestPathOf(url),
      };
    });
  const rest = { ...headers };
  cookieNames.forEach((name) => delete rest[name]);
  return { headers: rest, cookies };
}

// Validates the cookies and serializes them for the native setCookies command, or returns "".
export function serializeCookies(
  cookies: ReadonlyArray<KlarnaWebViewCookie>
): string {
  if (cookies.length === 0) {
    return '';
  }
  return JSON.stringify(cookies.map(serializeCookie));
}
