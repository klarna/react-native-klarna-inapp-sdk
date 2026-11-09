import {
  KlarnaStandaloneWebView,
  type KlarnaWebViewProps,
} from '../KlarnaStandaloneWebView';
import type {
  KlarnaWebViewCookie,
  KlarnaWebViewLoadOptions,
} from '../KlarnaStandaloneWebViewLoadOptions';
import { Commands } from '../specs/KlarnaStandaloneWebViewNativeComponent';
import { KlarnaTheme } from '../types/common/KlarnaTheme';

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

const nativeEvent = {
  url: 'https://example.com',
  loading: true,
  title: 'Example',
  canGoBack: false,
  canGoForward: false,
};

function setUp(
  propOverrides: Partial<KlarnaWebViewProps> = {},
  { mounted = true }: { mounted?: boolean } = {}
) {
  const props: KlarnaWebViewProps = {
    returnUrl: 'returnUrl://',
    onLoadStart: jest.fn(),
    onLoadEnd: jest.fn(),
    onError: jest.fn(),
    onLoadProgress: jest.fn(),
    onKlarnaMessage: jest.fn(),
    onRenderProcessGone: jest.fn(),
    ...propOverrides,
  };
  const view = new KlarnaStandaloneWebView(props);
  // Stands in for the mounted native view that the commands are sent to.
  const nativeView = mounted ? {} : null;
  (view.standaloneWebViewRef as { current: unknown }).current = nativeView;

  const nativeProps = (view.render() as any).props;

  return {
    ...props,
    nativeView,
    nativeProps,
    load: (url: string, options?: KlarnaWebViewLoadOptions) =>
      (view as any).loadWithOptions(url, options),
    goForward: () => view.goForward(),
    goBack: () => view.goBack(),
    reload: () => view.reload(),
    stopLoading: () => view.stopLoading(),
    setCookies: (cookies: ReadonlyArray<KlarnaWebViewCookie>) =>
      (view as any).storeCookies(cookies),
    setCookiesResult: (requestId: number, error: string) =>
      nativeProps.onSetCookiesResult({
        nativeEvent: { setCookiesResult: { requestId, error } },
      }),
    unmount: () => view.componentWillUnmount(),
    klarnaMessage: (action: string, params: string) =>
      nativeProps.onKlarnaMessage({
        nativeEvent: { klarnaMessageEvent: { action, params } },
      }),
    renderProcessGone: (didCrash: boolean) =>
      nativeProps.onRenderProcessGone({
        nativeEvent: { renderProcessGoneEvent: { didCrash } },
      }),
    loadStart: () =>
      nativeProps.onLoadStart({
        nativeEvent: { navigationEvent: nativeEvent },
      }),
    loadEnd: () =>
      nativeProps.onLoadEnd({ nativeEvent: { navigationEvent: nativeEvent } }),
    error: (code: number) =>
      nativeProps.onError({
        nativeEvent: {
          error: { ...nativeEvent, code, description: 'failed' },
        },
      }),
    progress: (progress: number) =>
      nativeProps.onLoadProgress({
        nativeEvent: { progressEvent: { ...nativeEvent, progress } },
      }),
  };
}

beforeEach(() => {
  jest.clearAllMocks();
});

describe('KlarnaStandaloneWebView', () => {
  describe('props', () => {
    it('applies the default prop values', () => {
      const view = setUp();

      expect(view.nativeProps.overScrollMode).toBe('always');
      expect(view.nativeProps.bounces).toBe(true);
      expect(view.nativeProps.theme).toBe('light');
    });

    it('passes the given prop values through', () => {
      const view = setUp({ overScrollMode: 'never', bounces: false });

      expect(view.nativeProps.returnUrl).toBe('returnUrl://');
      expect(view.nativeProps.overScrollMode).toBe('never');
      expect(view.nativeProps.bounces).toBe(false);
    });

    // Native matches these exact strings and ignores anything else.
    it.each([
      [KlarnaTheme.Light, 'light'],
      [KlarnaTheme.Dark, 'dark'],
      [KlarnaTheme.Automatic, 'automatic'],
    ])('passes the %s theme to native as %s', (theme, expected) => {
      const view = setUp({ theme });

      expect(view.nativeProps.theme).toBe(expected);
    });

    it('falls back to an empty returnUrl', () => {
      const view = setUp({ returnUrl: undefined as unknown as string });

      expect(view.nativeProps.returnUrl).toBe('');
    });
  });

  describe('load', () => {
    it('sends the command with the url', () => {
      const view = setUp();

      view.load('https://example.com');

      expect(Commands.load).toHaveBeenCalledWith(
        view.nativeView,
        'https://example.com',
        ''
      );
    });

    it('does not send the command before the view is mounted', () => {
      const view = setUp({}, { mounted: false });

      view.load('https://example.com', { headers: { 'X-Store': '42' } });

      expect(Commands.load).not.toHaveBeenCalled();
    });
  });

  describe('load with headers', () => {
    const url = 'https://www.example.com/checkout?step=payment';

    function loadWith(loadUrl: string, options: KlarnaWebViewLoadOptions) {
      const view = setUp();
      view.load(loadUrl, options);
      return (Commands.load as jest.Mock).mock.calls[0][2];
    }

    it('sends nothing for empty options', () => {
      expect(loadWith(url, {})).toBe('');
      expect(loadWith(url, { headers: {} })).toBe('');
    });

    it('sends the headers', () => {
      expect(
        JSON.parse(loadWith(url, { headers: { 'X-Store': '42' } }))
      ).toEqual({
        headers: { 'X-Store': '42' },
      });
    });

    it('still loads a Klarna page without options', () => {
      expect(loadWith('https://js.playground.klarna.com/checkout', {})).toBe(
        ''
      );
    });

    it('rejects headers for Klarna pages', () => {
      [
        'https://js.playground.klarna.com/checkout',
        'https://klarna.com/',
        'https://payments.klarna.com/',
        'https://eu.klarnaevt.com/',
        'https://x.klarnacdn.net/',
        'https://us-assets.klarnaservices.com/',
        'https://login.nonprod.klarna.net/',
      ].forEach((klarnaUrl) =>
        expect(() =>
          loadWith(klarnaUrl, { headers: { 'X-Store': '42' } })
        ).toThrow('cannot be sent to Klarna pages')
      );
    });

    it('does not treat a host that only contains a Klarna name as Klarna', () => {
      expect(
        loadWith('https://notklarna.com/', { headers: { 'X-Store': '42' } })
      ).not.toBe('');
    });

    it('does not read an @ in the path as user info', () => {
      expect(() =>
        loadWith('https://www.example.com/path@klarna.com', {
          headers: { 'X-Store': '42' },
        })
      ).not.toThrow();
      expect(() =>
        loadWith('https://user@klarna.com/', { headers: { 'X-Store': '42' } })
      ).toThrow('cannot be sent to Klarna pages');
    });

    it('rejects Klarna pages written in a form the web view normalises', () => {
      [
        'https://klarna%2ecom/',
        'https://klarna%2Ecom/',
        'https://www.klarna%2ecom/',
        'https://klarna。com/',
        'https://klarna．com/',
        'https://klarna｡com/',
        'https://klarna.com./',
        'https://x.klarna.com.:443/',
        'https://KLARNA.COM/',
        'https://klarna.com\\@evil.com/',
        'https://klarna.com:8443/',
        'https://klarna.\tcom/',
        'https://kla\nrna.com/',
        'https://klarna.com\r/',
        '\thttps://klarna.com/',
        ' https://klarna.com/',
        'https://a@\tklarna.com/',
        'https://klarna.com\u0000',
        'https://pay.klarna.com\u0001',
        'https://klarna.com/\u001f',
        '\u0000https://klarna.com/',
      ].forEach((klarnaUrl) =>
        expect(() =>
          loadWith(klarnaUrl, { headers: { 'X-Store': '42' } })
        ).toThrow('cannot be sent to Klarna pages')
      );
    });

    it('rejects hosts the web view would map to another name', () => {
      [
        'https://ｋｌａｒｎａ.com/',
        'https://klarna.ｃｏｍ/',
        'https://kl­arna.com/',
        'https://kl​arna.com/',
        'https://klärna.com/',
      ].forEach((mappedUrl) =>
        expect(() =>
          loadWith(mappedUrl, { headers: { 'X-Store': '42' } })
        ).toThrow('ASCII')
      );
    });

    it('accepts a host written in punycode', () => {
      expect(() =>
        loadWith('https://xn--bcher-kva.example/', {
          headers: { 'X-Store': '42' },
        })
      ).not.toThrow();
    });

    it('reads the host after the last @ when the userinfo has several', () => {
      [
        'https://merchant.example@attacker@klarna.com/',
        'https://a@b@c@www.klarna.com:8443/path',
      ].forEach((klarnaUrl) =>
        expect(() =>
          loadWith(klarnaUrl, { headers: { 'X-Store': '42' } })
        ).toThrow('cannot be sent to Klarna pages')
      );
      expect(() =>
        loadWith('https://klarna.com@a@www.example.com/', {
          headers: { 'X-Store': '42' },
        })
      ).not.toThrow();
    });

    it('does not read a backslash as part of the host', () => {
      expect(() =>
        loadWith('https://www.example.com\\@klarna.com/', {
          headers: { 'X-Store': '42' },
        })
      ).not.toThrow();
    });

    it('rejects a Cookie header', () => {
      expect(() =>
        loadWith(url, { headers: { cookie: 'session=abc' } })
      ).toThrow('source.headers');
    });

    it('rejects headers with line breaks', () => {
      expect(() =>
        loadWith(url, { headers: { 'X-Store': '42\r\nX-Other: 1' } })
      ).toThrow('invalid header "X-Store"');
    });

    it('rejects header names that are not valid tokens', () => {
      [
        'X Store',
        'X:Store',
        'X/Store',
        'X(Store)',
        'X=Store',
        'X-Störe',
      ].forEach((name) =>
        expect(() => loadWith(url, { headers: { [name]: '42' } })).toThrow(
          `invalid header "${name}"`
        )
      );
    });

    it('accepts every character a header name token allows', () => {
      expect(() =>
        loadWith(url, { headers: { "X-Store_1.a!#$%&'*+^`|~": '42' } })
      ).not.toThrow();
    });

    it('rejects headers for a URL that is not http or https', () => {
      expect(() =>
        loadWith('file:///tmp/index.html', { headers: { 'X-Store': '42' } })
      ).toThrow('http or https URL');
    });

    it('does not send the command when the options are invalid', () => {
      const view = setUp();

      expect(() =>
        view.load(url, { headers: { Cookie: 'session=abc' } })
      ).toThrow();
      expect(Commands.load).not.toHaveBeenCalled();
    });
  });

  describe('setCookies', () => {
    const session = {
      name: 'session',
      value: 'abc',
      domain: 'www.example.com',
    };

    function sentCookies(call = 0) {
      const [, requestId, cookiesJson] = (Commands.setCookies as jest.Mock).mock
        .calls[call];
      return { requestId, cookies: JSON.parse(cookiesJson) };
    }

    it('sends the cookies with their defaults', () => {
      const view = setUp();

      view.setCookies([session]);

      expect(Commands.setCookies).toHaveBeenCalledWith(
        view.nativeView,
        expect.any(Number),
        expect.any(String)
      );
      expect(sentCookies().cookies).toEqual([
        {
          name: 'session',
          value: 'abc',
          domain: 'www.example.com',
          path: '/',
          secure: true,
          httpOnly: false,
          hostOnly: false,
        },
      ]);
    });

    it('lets a caller opt out of secure explicitly', () => {
      const view = setUp();

      view.setCookies([{ ...session, secure: false }]);

      expect(sentCookies().cookies[0].secure).toBe(false);
    });

    it('rejects secure and httpOnly values that are not booleans', async () => {
      const view = setUp();

      for (const attributes of [
        { secure: 'false' },
        { secure: 0 },
        { httpOnly: 'true' },
        { httpOnly: 1 },
      ]) {
        await expect(
          view.setCookies([{ ...session, ...attributes } as any])
        ).rejects.toThrow('needs a boolean');
      }
      expect(Commands.setCookies).not.toHaveBeenCalled();
    });

    it('rejects a Klarna domain written with a trailing dot', async () => {
      const view = setUp();

      await expect(
        view.setCookies([{ ...session, domain: 'klarna.com.' }])
      ).rejects.toThrow();
      expect(Commands.setCookies).not.toHaveBeenCalled();
    });

    it('keeps the cookie attributes and normalises the domain', () => {
      const view = setUp();

      view.setCookies([
        {
          name: 'store',
          value: '42',
          domain: '.Example.com',
          path: '/checkout',
          secure: true,
          httpOnly: true,
          expires: 1893456000000,
        },
      ]);

      expect(sentCookies().cookies).toEqual([
        {
          name: 'store',
          value: '42',
          domain: 'example.com',
          path: '/checkout',
          secure: true,
          httpOnly: true,
          hostOnly: false,
          expires: 1893456000000,
        },
      ]);
    });

    it('resolves only once native reports the cookies as stored', async () => {
      const view = setUp();
      const onStored = jest.fn();

      const result = view.setCookies([session]).then(onStored);
      await Promise.resolve();
      expect(onStored).not.toHaveBeenCalled();

      view.setCookiesResult(sentCookies().requestId, '');
      await result;
      expect(onStored).toHaveBeenCalled();
    });

    it('rejects with the native error', async () => {
      const view = setUp();

      const result = view.setCookies([session]);
      view.setCookiesResult(sentCookies().requestId, 'invalid cookies');

      await expect(result).rejects.toThrow('invalid cookies');
    });

    it('settles each request with its own result', async () => {
      const view = setUp();

      const first = view.setCookies([session]);
      const second = view.setCookies([{ ...session, name: 'store' }]);
      view.setCookiesResult(sentCookies(1).requestId, 'failed');
      view.setCookiesResult(sentCookies(0).requestId, '');

      await expect(first).resolves.toBeUndefined();
      await expect(second).rejects.toThrow('failed');
    });

    it('ignores a result for an unknown request', () => {
      const view = setUp();

      expect(() => view.setCookiesResult(999, '')).not.toThrow();
    });

    it('resolves right away for no cookies', async () => {
      const view = setUp();

      await expect(view.setCookies([])).resolves.toBeUndefined();
      expect(Commands.setCookies).not.toHaveBeenCalled();
    });

    it('rejects invalid cookies with the validation error before the view is mounted', async () => {
      const view = setUp({}, { mounted: false });

      await expect(
        view.setCookies([{ ...session, domain: 'klarna.com' }])
      ).rejects.toThrow('cannot be set for a Klarna domain');
    });

    it('rejects before the view is mounted', async () => {
      const view = setUp({}, { mounted: false });

      await expect(view.setCookies([session])).rejects.toThrow(
        'while the view is not mounted'
      );
    });

    it('rejects pending requests when the view unmounts', async () => {
      const view = setUp();

      const result = view.setCookies([session]);
      view.unmount();

      await expect(result).rejects.toThrow('unmounted');
    });

    it('requires a valid merchant domain', async () => {
      const view = setUp();
      const invalidDomains = [
        undefined,
        '',
        'localhost',
        '192.168.0.1',
        'example.com/checkout',
        'ex ample.com',
        '-example.com',
      ];

      for (const domain of invalidDomains) {
        await expect(
          view.setCookies([{ ...session, domain } as KlarnaWebViewCookie])
        ).rejects.toThrow('needs a valid merchant domain');
      }
      expect(Commands.setCookies).not.toHaveBeenCalled();
    });

    it('accepts merchant domains with a punycode label or top-level domain', () => {
      const view = setUp();

      ['xn--bcher-kva.example', 'shop.xn--p1ai', 'shop.example.xn--fiqs8s']
        .map((domain) => view.setCookies([{ ...session, domain }]))
        .forEach((pending) => pending.catch(() => {}));

      expect(Commands.setCookies).toHaveBeenCalledTimes(3);
      expect(sentCookies(1).cookies[0].domain).toBe('shop.xn--p1ai');
    });

    it('rejects domains whose top-level domain is not valid', async () => {
      const view = setUp();

      for (const domain of ['shop.123', 'shop.x', 'shop.-bad', 'shop.bad-']) {
        await expect(view.setCookies([{ ...session, domain }])).rejects.toThrow(
          'needs a valid merchant domain'
        );
      }
    });

    it('rejects Klarna domains', async () => {
      const view = setUp();
      const klarnaDomains = [
        'klarna.com',
        '.klarna.com',
        'js.playground.klarna.com',
        'payments.klarna.com',
        'klarna.net',
        'x.nonprod.us1.js.klarna.net',
        'x.klarnacdn.net',
        'us-assets.klarnaservices.com',
        'eu.klarnaevt.com',
      ];

      for (const domain of klarnaDomains) {
        await expect(view.setCookies([{ ...session, domain }])).rejects.toThrow(
          'cannot be set for a Klarna domain'
        );
      }
    });

    it('allows a domain that only contains a Klarna name', async () => {
      const view = setUp();

      view.setCookies([{ ...session, domain: 'notklarna.com' }]);

      expect(sentCookies().cookies[0].domain).toBe('notklarna.com');
    });

    it('rejects names, values and paths that could add attributes', async () => {
      const view = setUp();

      await expect(
        view.setCookies([{ ...session, value: 'abc; Domain=klarna.com' }])
      ).rejects.toThrow('invalid cookie "session"');
      await expect(
        view.setCookies([{ ...session, name: 'a=b' }])
      ).rejects.toThrow('invalid cookie');
      await expect(view.setCookies([{ ...session, name: '' }])).rejects.toThrow(
        'invalid cookie'
      );
      await expect(
        view.setCookies([{ ...session, value: 'a\nb' }])
      ).rejects.toThrow('invalid cookie');
      await expect(
        view.setCookies([{ ...session, path: '/; Domain=klarna.com' }])
      ).rejects.toThrow('invalid path');
      await expect(
        view.setCookies([{ ...session, path: 'checkout' }])
      ).rejects.toThrow('invalid path');
    });

    it('rejects an invalid expiry', async () => {
      const view = setUp();

      await expect(
        view.setCookies([{ ...session, expires: Number.NaN }])
      ).rejects.toThrow('invalid expiry');
    });

    it('sends none of the cookies when one of them is invalid', async () => {
      const view = setUp();

      await expect(
        view.setCookies([session, { ...session, domain: 'klarna.com' }])
      ).rejects.toThrow();
      expect(Commands.setCookies).not.toHaveBeenCalled();
    });
  });

  describe('goForward', () => {
    it('sends the command', () => {
      const view = setUp();

      view.goForward();

      expect(Commands.goForward).toHaveBeenCalledWith(view.nativeView);
    });
  });

  describe('goBack', () => {
    it('sends the command', () => {
      const view = setUp();

      view.goBack();

      expect(Commands.goBack).toHaveBeenCalledWith(view.nativeView);
    });
  });

  describe('reload', () => {
    it('sends the command', () => {
      const view = setUp();

      view.reload();

      expect(Commands.reload).toHaveBeenCalledWith(view.nativeView);
    });
  });

  describe('stopLoading', () => {
    it('sends the command', () => {
      const view = setUp();

      view.stopLoading();

      expect(Commands.stopLoading).toHaveBeenCalledWith(view.nativeView);
    });

    it('emits nothing for a stopped load on Android, where stopping reports a finished page', () => {
      const view = setUp();

      view.loadStart();
      view.stopLoading();
      // Android sends progress 100 more than once, and before onLoadEnd.
      view.progress(100);
      view.progress(100);
      view.loadEnd();

      expect(view.onLoadEnd).not.toHaveBeenCalled();
      expect(view.onLoadProgress).not.toHaveBeenCalled();
      expect(view.onError).not.toHaveBeenCalled();
    });

    it('keeps dropping completion updates until the next load starts', () => {
      const view = setUp();

      view.loadStart();
      view.stopLoading();
      // However many completion updates arrive, in any order relative to
      // onLoadEnd, none of them reach the integrator.
      view.progress(100);
      view.loadEnd();
      view.progress(100);
      view.progress(100);

      expect(view.onLoadEnd).not.toHaveBeenCalled();
      expect(view.onLoadProgress).not.toHaveBeenCalled();
    });

    it('emits nothing for a stopped load on iOS, where stopping reports nothing', () => {
      const view = setUp();

      view.loadStart();
      view.stopLoading();

      expect(view.onLoadEnd).not.toHaveBeenCalled();
      expect(view.onLoadProgress).not.toHaveBeenCalled();
      expect(view.onError).not.toHaveBeenCalled();
    });

    it('forwards the events of the load that follows a stop', () => {
      const view = setUp();

      view.loadStart();
      view.stopLoading();
      view.progress(100);
      view.progress(100);
      view.loadEnd();

      // Android sends the first progress update of a load before onLoadStart,
      // so this one still falls inside the suppression and is dropped. The
      // rest of the load is reported normally.
      view.progress(10);
      view.loadStart();
      view.progress(70);
      view.progress(100);
      view.loadEnd();

      expect(view.onLoadStart).toHaveBeenCalledTimes(2);
      expect(view.onLoadEnd).toHaveBeenCalledTimes(1);
      expect(view.onLoadProgress).toHaveBeenCalledTimes(2);
    });

    it('forwards the events of a new load that starts before the stopped one reports', () => {
      const view = setUp();

      view.loadStart();
      view.stopLoading();
      // The stop produced no terminal events and this load supersedes it.
      view.loadStart();
      view.progress(100);
      view.loadEnd();

      expect(view.onLoadEnd).toHaveBeenCalledTimes(1);
      expect(view.onLoadProgress).toHaveBeenCalledTimes(1);
    });

    it('forwards the events of the load that follows a stop while idle', () => {
      const view = setUp();

      view.stopLoading();
      view.loadStart();
      view.progress(100);
      view.loadEnd();

      expect(view.onLoadEnd).toHaveBeenCalledTimes(1);
      expect(view.onLoadProgress).toHaveBeenCalledTimes(1);
    });

    it('forwards the events of the load that follows a stop after a load ended', () => {
      const view = setUp();

      view.loadStart();
      view.loadEnd();
      view.stopLoading();
      view.loadStart();
      view.loadEnd();

      expect(view.onLoadEnd).toHaveBeenCalledTimes(2);
    });

    it('forwards the events of the load that follows a stop after a load failed', () => {
      const view = setUp();

      view.loadStart();
      view.error(-2);
      view.stopLoading();
      view.loadStart();
      view.loadEnd();

      expect(view.onError).toHaveBeenCalledTimes(1);
      expect(view.onLoadEnd).toHaveBeenCalledTimes(1);
    });

    it('drops a partial progress update that arrives after a stop', () => {
      const view = setUp();

      view.loadStart();
      view.stopLoading();
      // An update that was already in flight when the stop landed.
      view.progress(40);

      expect(view.onLoadProgress).not.toHaveBeenCalled();
    });

    it('emits nothing for a load stopped before it reported starting', () => {
      const view = setUp();

      // Android reports progress before onLoadStart, so a stop can land while
      // a load is in flight but has not been reported as started yet.
      view.progress(10);
      view.stopLoading();
      view.loadEnd();
      view.progress(100);

      expect(view.onLoadProgress).toHaveBeenCalledTimes(1);
      expect(view.onLoadProgress).toHaveBeenCalledWith(
        expect.objectContaining({ progress: 10 })
      );
      expect(view.onLoadEnd).not.toHaveBeenCalled();
    });

    it('drops the leading progress update of the load that follows a stop', () => {
      const view = setUp();

      view.stopLoading();
      // Android reports this update before onLoadStart, so it is still dropped.
      view.progress(10);
      view.loadStart();
      view.progress(50);

      expect(view.onLoadProgress).toHaveBeenCalledTimes(1);
      expect(view.onLoadProgress).toHaveBeenCalledWith(
        expect.objectContaining({ progress: 50 })
      );
    });

    it('forwards a genuine error that arrives while a stop is suppressing', () => {
      const view = setUp();

      view.loadStart();
      view.stopLoading();
      // A stop suppresses the cancellation it causes, but a real failure that
      // lands in the same window still has to reach the integrator.
      view.error(-1003);
      view.progress(40);
      view.loadEnd();

      expect(view.onError).toHaveBeenCalledTimes(1);
      expect(view.onError).toHaveBeenCalledWith(
        expect.objectContaining({ code: -1003 })
      );
      // The error ends the suppression, so what follows is reported normally.
      expect(view.onLoadProgress).toHaveBeenCalledTimes(1);
      expect(view.onLoadEnd).toHaveBeenCalledTimes(1);
    });
  });

  describe('load events', () => {
    // The sequence measured on Android: a progress update arrives before
    // onLoadStart, and progress 100 arrives twice, before onLoadEnd.
    it('forwards the events of a load that runs to completion', () => {
      const view = setUp();

      view.progress(10);
      view.loadStart();
      view.progress(70);
      view.progress(100);
      view.progress(100);
      view.loadEnd();

      expect(view.onLoadStart).toHaveBeenCalledTimes(1);
      expect(view.onLoadProgress).toHaveBeenCalledTimes(4);
      expect(view.onLoadEnd).toHaveBeenCalledTimes(1);
      expect(view.onError).not.toHaveBeenCalled();
    });

    it('forwards the navigation event of onLoadStart and onLoadEnd', () => {
      const view = setUp();

      view.loadStart();
      view.loadEnd();

      expect(view.onLoadStart).toHaveBeenCalledWith(nativeEvent);
      expect(view.onLoadEnd).toHaveBeenCalledWith(nativeEvent);
    });

    it('forwards errors', () => {
      const view = setUp();

      view.loadStart();
      view.error(-2);

      expect(view.onError).toHaveBeenCalledTimes(1);
      expect(view.onLoadEnd).not.toHaveBeenCalled();
    });
  });

  describe('onKlarnaMessage', () => {
    it('parses the params into an object', () => {
      const view = setUp();

      view.klarnaMessage('complete', '{"orderId":"abc","amount":42}');

      expect(view.onKlarnaMessage).toHaveBeenCalledWith({
        action: 'complete',
        params: { orderId: 'abc', amount: 42 },
      });
    });

    it('parses empty params', () => {
      const view = setUp();

      view.klarnaMessage('complete', '{}');

      expect(view.onKlarnaMessage).toHaveBeenCalledWith({
        action: 'complete',
        params: {},
      });
    });

    it('reports empty params when the params are not valid JSON', () => {
      const view = setUp();

      view.klarnaMessage('complete', 'not json');

      expect(view.onKlarnaMessage).toHaveBeenCalledWith({
        action: 'complete',
        params: {},
      });
    });
  });

  describe('onRenderProcessGone', () => {
    it('forwards the event', () => {
      const view = setUp();

      view.renderProcessGone(true);

      expect(view.onRenderProcessGone).toHaveBeenCalledWith({ didCrash: true });
    });
  });

  it('does not throw when no handlers are given', () => {
    const view = setUp({
      onLoadStart: undefined,
      onLoadEnd: undefined,
      onError: undefined,
      onLoadProgress: undefined,
      onKlarnaMessage: undefined,
      onRenderProcessGone: undefined,
    });

    expect(() => {
      view.loadStart();
      view.progress(50);
      view.loadEnd();
      view.error(-2);
      view.klarnaMessage('complete', '{}');
      view.renderProcessGone(true);
    }).not.toThrow();
  });

  it('validates load() options even before the view is mounted', () => {
    const view = setUp({}, { mounted: false });

    expect(() =>
      view.load('https://www.klarna.com/', { headers: { 'X-Store': '42' } })
    ).toThrow('cannot be sent to Klarna pages');
    expect(() =>
      view.load('https://example.com/', { headers: { Cookie: 'a=b' } })
    ).toThrow('source.headers');
    expect(() =>
      view.load('file:///tmp/a.html', { headers: { 'X-Store': '42' } })
    ).toThrow('http or https URL');
    expect(Commands.load).not.toHaveBeenCalled();
  });

  it('sends no commands while the native view is not mounted', () => {
    const view = setUp({}, { mounted: false });

    view.load('https://example.com');
    view.goForward();
    view.goBack();
    view.reload();
    view.stopLoading();

    expect(Commands.load).not.toHaveBeenCalled();
    expect(Commands.goForward).not.toHaveBeenCalled();
    expect(Commands.goBack).not.toHaveBeenCalled();
    expect(Commands.reload).not.toHaveBeenCalled();
    expect(Commands.stopLoading).not.toHaveBeenCalled();
  });
});
