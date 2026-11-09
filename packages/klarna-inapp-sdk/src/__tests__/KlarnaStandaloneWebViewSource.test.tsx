import {
  KlarnaStandaloneWebView,
  type KlarnaWebViewProps,
} from '../KlarnaStandaloneWebView';
import { Commands } from '../specs/KlarnaStandaloneWebViewNativeComponent';

jest.mock('../specs/KlarnaStandaloneWebViewNativeComponent', () => ({
  __esModule: true,
  default: 'RNKlarnaStandaloneWebView',
  Commands: {
    load: jest.fn(),
    goForward: jest.fn(),
    goBack: jest.fn(),
    reload: jest.fn(),
    stopLoading: jest.fn(),
    setCookies: jest.fn(),
  },
}));

const uri = 'https://www.example.com/checkout';

function mount(source?: KlarnaWebViewProps['source']) {
  const onError = jest.fn();
  const props: KlarnaWebViewProps = {
    returnUrl: 'returnUrl://',
    source,
    onError,
  };
  const view = new KlarnaStandaloneWebView(props);
  (view.standaloneWebViewRef as { current: unknown }).current = {};
  view.componentDidMount();
  return {
    view,
    onError,
    update: (next: KlarnaWebViewProps['source']) => {
      const previous = view.props;
      (view as any).props = { ...previous, source: next };
      view.componentDidUpdate(previous);
    },
    storeCookies: (call = 0, error = '') => {
      const [, requestId] = (Commands.setCookies as jest.Mock).mock.calls[call];
      (view.render() as any).props.onSetCookiesResult({
        nativeEvent: { setCookiesResult: { requestId, error } },
      });
    },
  };
}

const flush = () =>
  new Promise<void>((resolve) => setImmediate(() => resolve()));

function loadedHeaders(call = 0) {
  const [, , optionsJson] = (Commands.load as jest.Mock).mock.calls[call];
  return optionsJson === '' ? undefined : JSON.parse(optionsJson).headers;
}

function sentCookies(call = 0) {
  const [, , cookiesJson] = (Commands.setCookies as jest.Mock).mock.calls[call];
  return JSON.parse(cookiesJson);
}

beforeEach(() => {
  jest.clearAllMocks();
});

describe('KlarnaStandaloneWebView source', () => {
  it('loads the uri with its headers when it mounts', () => {
    mount({ uri, headers: { 'X-Store': '42' } });

    expect(Commands.load).toHaveBeenCalledTimes(1);
    expect((Commands.load as jest.Mock).mock.calls[0][1]).toBe(uri);
    expect(loadedHeaders()).toEqual({ 'X-Store': '42' });
  });

  it('loads a uri without headers', () => {
    mount({ uri });

    expect((Commands.load as jest.Mock).mock.calls[0][1]).toBe(uri);
    expect(loadedHeaders()).toBeUndefined();
  });

  it('loads nothing without a source', () => {
    mount(undefined);

    expect(Commands.load).not.toHaveBeenCalled();
    expect(Commands.setCookies).not.toHaveBeenCalled();
  });

  it('loads again when the uri or the headers change', () => {
    const { update } = mount({ uri, headers: { 'X-Store': '42' } });

    update({
      uri: 'https://www.example.com/payment',
      headers: { 'X-Store': '42' },
    });
    update({
      uri: 'https://www.example.com/payment',
      headers: { 'X-Store': '7' },
    });

    expect(Commands.load).toHaveBeenCalledTimes(3);
  });

  it('does not load again for an equal source', () => {
    const { update } = mount({ uri, headers: { 'X-Store': '42' } });

    update({ uri, headers: { 'X-Store': '42' } });

    expect(Commands.load).toHaveBeenCalledTimes(1);
  });

  it('marks Cookie header cookies to replace same-name cookies on the page path', async () => {
    const { storeCookies } = mount({
      uri: 'https://www.example.com/checkout/payment?step=2#top',
      headers: { Cookie: 'session=new' },
    });

    expect(sentCookies()).toEqual([
      expect.objectContaining({
        name: 'session',
        path: '/',
        replaceConflicting: true,
        requestPath: '/checkout/payment',
      }),
    ]);
    storeCookies();
    await flush();
  });

  it('uses the whole page path, so a cookie at the page itself is replaced', async () => {
    const { storeCookies } = mount({
      uri: 'https://www.example.com/anything',
      headers: { Cookie: 'session=new' },
    });

    expect(sentCookies()).toEqual([
      expect.objectContaining({ requestPath: '/anything' }),
    ]);
    storeCookies();
    await flush();
  });

  it('keeps an unsafe path segment out of the request path', async () => {
    const { storeCookies } = mount({
      uri: 'https://www.example.com/checkout/a;c/x',
      headers: { Cookie: 'session=new' },
    });

    expect(sentCookies()).toEqual([
      expect.objectContaining({ requestPath: '/checkout/' }),
    ]);
    storeCookies();
    await flush();
  });

  it('percent-encodes spaces in the request path', async () => {
    const { storeCookies } = mount({
      uri: 'https://www.example.com/checkout/a b',
      headers: { Cookie: 'session=new' },
    });

    expect(sentCookies()).toEqual([
      expect.objectContaining({ requestPath: '/checkout/a%20b' }),
    ]);
    storeCookies();
    await flush();
  });

  it.each([
    ['https://www.example.com/checkout/café', '/checkout/caf%C3%A9'],
    ['https://www.example.com/checkout/caf%C3%A9', '/checkout/caf%C3%A9'],
    ['https://www.example.com/a"b<c>d`e{f}', '/a%22b%3Cc%3Ed%60e%7Bf%7D'],
    ['https://www.example.com/😀', '/%F0%9F%98%80'],
    ['https://www.example.com/a\ud800b', '/a%EF%BF%BDb'],
    ['https://www.example.com/checkout/a\u0001b', '/checkout/a%01b'],
    ['https://www.example.com/checkout/a\u007fb', '/checkout/a%7Fb'],
  ])('percent-encodes the request path of %s', async (u, path) => {
    const { storeCookies } = mount({
      uri: u,
      headers: { Cookie: 'session=new' },
    });

    expect(sentCookies()).toEqual([
      expect.objectContaining({ requestPath: path }),
    ]);
    storeCookies();
    await flush();
  });

  it.each([
    ['https://www.example.com/cart/../checkout', '/checkout'],
    ['https://www.example.com/cart/%2e%2e/checkout', '/checkout'],
    ['https://www.example.com/cart/.%2E/checkout', '/checkout'],
    ['https://www.example.com/a/./b/', '/a/b/'],
    ['https://www.example.com/a/b/..', '/a/'],
    ['https://www.example.com/../../x', '/x'],
  ])('normalizes dot segments in the request path of %s', async (u, path) => {
    const { storeCookies } = mount({
      uri: u,
      headers: { Cookie: 'session=new' },
    });

    expect(sentCookies()).toEqual([
      expect.objectContaining({ requestPath: path }),
    ]);
    storeCookies();
    await flush();
  });

  it('stores a Cookie header as cookies and loads after they are stored', async () => {
    const { storeCookies } = mount({
      uri,
      headers: { 'Cookie': 'session=abc; store=42', 'X-Store': '42' },
    });

    expect(Commands.load).not.toHaveBeenCalled();
    expect(sentCookies()).toEqual([
      {
        name: 'session',
        value: 'abc',
        domain: 'www.example.com',
        path: '/',
        secure: true,
        httpOnly: false,
        hostOnly: true,
        replaceConflicting: true,
        requestPath: '/checkout',
      },
      {
        name: 'store',
        value: '42',
        domain: 'www.example.com',
        path: '/',
        secure: true,
        httpOnly: false,
        hostOnly: true,
        replaceConflicting: true,
        requestPath: '/checkout',
      },
    ]);

    storeCookies();
    await flush();

    expect(Commands.load).toHaveBeenCalledTimes(1);
    expect(loadedHeaders()).toEqual({ 'X-Store': '42' });
  });

  it('reads the Cookie header whatever its letter case', () => {
    mount({ uri, headers: { cookie: 'a=1' } });

    expect(sentCookies()).toHaveLength(1);
  });

  it('merges Cookie headers that differ only in letter case', async () => {
    const { storeCookies } = mount({
      uri,
      headers: { 'Cookie': 'a=1', 'cookie': 'b=2', 'X-Store': '42' },
    });

    expect(sentCookies().map((c: { name: string }) => c.name)).toEqual([
      'a',
      'b',
    ]);
    storeCookies();
    await flush();
    expect(loadedHeaders()).toEqual({ 'X-Store': '42' });
  });

  it('marks cookies for an http uri as not secure', () => {
    mount({ uri: 'http://www.example.com/', headers: { Cookie: 'a=1' } });

    expect(sentCookies()[0].secure).toBe(false);
  });

  it('reads the scheme with tabs and newlines removed, as the web view does', () => {
    mount({
      uri: 'ht\ttps://www.example.com/checkout',
      headers: { Cookie: 'a=1' },
    });

    expect(sentCookies()[0].secure).toBe(true);
  });

  it('loads without a header when the Cookie header was the only one', async () => {
    const { storeCookies } = mount({ uri, headers: { Cookie: 'a=1' } });

    storeCookies();
    await flush();

    expect(loadedHeaders()).toBeUndefined();
  });

  it('keeps the quotes of a quoted Cookie header value and loads', async () => {
    const { storeCookies } = mount({
      uri,
      headers: { Cookie: 'session="abc"' },
    });

    expect(sentCookies()[0].value).toBe('"abc"');

    storeCookies();
    await flush();

    expect(Commands.load).toHaveBeenCalledTimes(1);
  });

  it.each(['a"b', '"a"b"', '"abc', 'abc"'])(
    'reports a Cookie header value with unbalanced or embedded quotes (%s)',
    async (value) => {
      const { onError } = mount({ uri, headers: { Cookie: `a=${value}` } });
      await flush();

      expect(onError.mock.calls[0][0].description).toContain('invalid cookie');
      expect(Commands.load).not.toHaveBeenCalled();
    }
  );

  it('reports invalid cookies to onError and loads nothing', async () => {
    const { onError } = mount({ uri, headers: { Cookie: 'a b=1' } });
    await flush();

    expect(onError).toHaveBeenCalledTimes(1);
    expect(onError.mock.calls[0][0].description).toContain('invalid cookie');
    expect(Commands.setCookies).not.toHaveBeenCalled();
    expect(Commands.load).not.toHaveBeenCalled();
  });

  it.each([
    '',
    'not-a-url',
    'https://',
    'https://shop.example:invalid/checkout',
    'https://shop.example:99999/checkout',
    'https://shop.example:/checkout',
  ])(
    'reports an invalid source uri "%s" to onError and loads nothing',
    (badUri) => {
      const { onError } = mount({ uri: badUri });

      expect(onError.mock.calls[0][0].code).toBe(-1);
      expect(onError.mock.calls[0][0].description).toContain('not a valid URL');
      expect(Commands.load).not.toHaveBeenCalled();
    }
  );

  it('loads an https uri that names its port', () => {
    const withPort = 'https://www.example.com:443/checkout';
    mount({ uri: withPort });

    expect((Commands.load as jest.Mock).mock.calls[0][1]).toBe(withPort);
  });

  it('stores no cookies when another header is invalid', async () => {
    const { onError } = mount({
      uri,
      headers: { 'Cookie': 'session=new', 'X Invalid': '1' },
    });
    await flush();

    expect(onError.mock.calls[0][0].description).toContain('invalid header');
    expect(Commands.setCookies).not.toHaveBeenCalled();
    expect(Commands.load).not.toHaveBeenCalled();
  });

  it('reports a failed cookie store to onError and loads nothing', async () => {
    const { onError, storeCookies } = mount({
      uri,
      headers: { Cookie: 'a=1' },
    });

    storeCookies(0, 'Could not store the cookies.');
    await flush();

    expect(onError.mock.calls[0][0].description).toContain('Could not store');
    expect(Commands.load).not.toHaveBeenCalled();
  });

  it('reports invalid load options to onError', () => {
    const { onError } = mount({
      uri: 'https://www.klarna.com/',
      headers: { 'X-Store': '42' },
    });

    expect(onError.mock.calls[0][0].description).toContain(
      'cannot be sent to Klarna pages'
    );
    expect(Commands.load).not.toHaveBeenCalled();
  });

  it('reports a Cookie header for a Klarna host', async () => {
    const { onError } = mount({
      uri: 'https://www.klarna.com/',
      headers: { Cookie: 'a=1' },
    });
    await flush();

    expect(onError).toHaveBeenCalledTimes(1);
    expect(Commands.setCookies).not.toHaveBeenCalled();
  });

  it('drops a stale load when the source changes while cookies are stored', async () => {
    const { update, storeCookies } = mount({ uri, headers: { Cookie: 'a=1' } });

    update({ uri: 'https://www.example.com/payment' });
    storeCookies(0);
    await flush();

    expect(Commands.load).toHaveBeenCalledTimes(1);
    expect((Commands.load as jest.Mock).mock.calls[0][1]).toBe(
      'https://www.example.com/payment'
    );
  });

  it('drops a pending load when the view unmounts', async () => {
    const { view, storeCookies } = mount({ uri, headers: { Cookie: 'a=1' } });

    storeCookies();
    view.componentWillUnmount();
    await flush();

    expect(Commands.load).not.toHaveBeenCalled();
  });

  it('drops a pending source load when load() runs while cookies are stored', async () => {
    const { view, storeCookies } = mount({ uri, headers: { Cookie: 'a=1' } });

    view.load('https://www.example.com/other');
    storeCookies();
    await flush();

    expect(Commands.load).toHaveBeenCalledTimes(1);
    expect((Commands.load as jest.Mock).mock.calls[0][1]).toBe(
      'https://www.example.com/other'
    );
  });

  it('drops a pending source load when stopLoading() runs while cookies are stored', async () => {
    const { view, storeCookies } = mount({ uri, headers: { Cookie: 'a=1' } });

    view.stopLoading();
    storeCookies();
    await flush();

    expect(Commands.load).not.toHaveBeenCalled();
  });

  it.each(['goBack', 'goForward', 'reload'] as const)(
    'drops a pending source load when %s() runs while cookies are stored',
    async (command) => {
      const { view, storeCookies } = mount({ uri, headers: { Cookie: 'a=1' } });

      view[command]();
      storeCookies();
      await flush();

      expect(Commands.load).not.toHaveBeenCalled();
      expect(Commands[command]).toHaveBeenCalledTimes(1);
    }
  );

  it('keeps load() and setCookies() working next to source', () => {
    const { view } = mount({ uri });

    view.load('https://www.example.com/other');

    expect(Commands.load).toHaveBeenCalledTimes(2);
  });

  it('passes sharedCookiesEnabled to the native view, off by default', () => {
    const off = new KlarnaStandaloneWebView({ returnUrl: 'returnUrl://' });
    const on = new KlarnaStandaloneWebView({
      returnUrl: 'returnUrl://',
      sharedCookiesEnabled: true,
    });

    expect((off.render() as any).props.sharedCookiesEnabled).toBe(false);
    expect((on.render() as any).props.sharedCookiesEnabled).toBe(true);
  });
});
