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
    load: (url: string) => view.load(url),
    goForward: () => view.goForward(),
    goBack: () => view.goBack(),
    reload: () => view.reload(),
    stopLoading: () => view.stopLoading(),
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
    });

    it('passes the given prop values through', () => {
      const view = setUp({ overScrollMode: 'never', bounces: false });

      expect(view.nativeProps.returnUrl).toBe('returnUrl://');
      expect(view.nativeProps.overScrollMode).toBe('never');
      expect(view.nativeProps.bounces).toBe(false);
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
        'https://example.com'
      );
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
