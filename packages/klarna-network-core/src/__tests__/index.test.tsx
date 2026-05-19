import { Klarna } from '../Klarna';
import NativeKlarnaNetworkCore from '../Specs/NativeKlarnaNetworkCore';
import type { KlarnaConfiguration } from '../types/KlarnaConfiguration';
import type { KlarnaIntegrationMetadata } from '../types/KlarnaIntegrationMetadata';

jest.mock('../Specs/NativeKlarnaNetworkCore', () => ({
  __esModule: true,
  default: {
    initialize: jest.fn(),
    getSessionToken: jest.fn(),
    clearSession: jest.fn(),
    handleReturnUrl: jest.fn(),
    setIntegrationMetadata: jest.fn(),
    dispose: jest.fn(),
  },
}));

const mockedNative = jest.mocked(NativeKlarnaNetworkCore);

const baseConfig: KlarnaConfiguration = {
  clientId: 'client_123',
  appReturnUrl: 'app://return',
};

const fullConfig: KlarnaConfiguration = {
  clientId: 'client_123',
  appReturnUrl: 'app://return',
  accountId: 'account_456',
  locale: 'en-US',
  klarnaNetworkSessionToken: 'token_789',
};

beforeEach(() => {
  jest.clearAllMocks();
  (Klarna as any)._cache.clear();
  mockedNative.initialize.mockResolvedValue(undefined);
  mockedNative.getSessionToken.mockResolvedValue('session_token');
  mockedNative.clearSession.mockResolvedValue(undefined);
  mockedNative.handleReturnUrl.mockResolvedValue(true);
  mockedNative.dispose.mockResolvedValue(undefined);
});

describe('Klarna', () => {
  describe('initialize', () => {
    it('returns a Klarna instance', async () => {
      const klarna = await Klarna.initialize(baseConfig);
      expect(klarna).toBeInstanceOf(Klarna);
    });

    it('calls native initialize with mapped config', async () => {
      await Klarna.initialize(baseConfig);
      expect(mockedNative.initialize).toHaveBeenCalledWith(expect.any(String), {
        clientId: 'client_123',
        appReturnUrl: 'app://return',
        accountId: null,
        locale: null,
        klarnaNetworkSessionToken: null,
      });
    });

    it('passes optional config fields when provided', async () => {
      await Klarna.initialize(fullConfig);
      expect(mockedNative.initialize).toHaveBeenCalledWith(expect.any(String), {
        clientId: 'client_123',
        appReturnUrl: 'app://return',
        accountId: 'account_456',
        locale: 'en-US',
        klarnaNetworkSessionToken: 'token_789',
      });
    });

    it('returns cached instance for identical configuration', async () => {
      const klarna1 = await Klarna.initialize(baseConfig);
      const klarna2 = await Klarna.initialize(baseConfig);
      expect(klarna2).toBe(klarna1);
    });

    it('rejects when native initialize rejects', async () => {
      const error = new Error('Native init failed');
      mockedNative.initialize.mockRejectedValueOnce(error);
      await expect(Klarna.initialize(baseConfig)).rejects.toThrow(
        'Native init failed'
      );
    });
  });

  describe('handleReturnUrl', () => {
    it('calls native handleReturnUrl with url', async () => {
      await Klarna.handleReturnUrl('app://return?code=abc');
      expect(mockedNative.handleReturnUrl).toHaveBeenCalledWith(
        'app://return?code=abc'
      );
    });

    it('returns the native result', async () => {
      mockedNative.handleReturnUrl.mockResolvedValueOnce(false);
      const result = await Klarna.handleReturnUrl('app://return');
      expect(result).toBe(false);
    });
  });

  describe('getIntegrationMetadata', () => {
    it('returns null before setIntegrationMetadata is called', async () => {
      const klarna = await Klarna.initialize(baseConfig);
      expect(klarna.getIntegrationMetadata()).toBeNull();
    });
  });

  describe('setIntegrationMetadata', () => {
    const metadata: KlarnaIntegrationMetadata = {
      integrator: {
        name: 'MyApp',
        sessionReference: 'ref_123',
      },
    };

    it('stores metadata returned by getIntegrationMetadata', async () => {
      const klarna = await Klarna.initialize(baseConfig);
      klarna.setIntegrationMetadata(metadata);
      expect(klarna.getIntegrationMetadata()).toEqual(metadata);
    });

    it('calls native setIntegrationMetadata with instanceId and mapped metadata', async () => {
      const klarna = await Klarna.initialize(baseConfig);
      klarna.setIntegrationMetadata(metadata);
      expect(mockedNative.setIntegrationMetadata).toHaveBeenCalledWith(
        expect.any(String),
        {
          integrator: {
            name: 'MyApp',
            sessionReference: 'ref_123',
            moduleName: null,
            moduleVersion: null,
          },
          originators: null,
        }
      );
    });

    it('maps optional integrator fields to null when not provided', async () => {
      const klarna = await Klarna.initialize(baseConfig);
      klarna.setIntegrationMetadata(metadata);
      const [, spec] = mockedNative.setIntegrationMetadata.mock.calls[0]!;
      expect(spec.integrator.moduleName).toBeNull();
      expect(spec.integrator.moduleVersion).toBeNull();
    });

    it('maps optional integrator fields when provided', async () => {
      const klarna = await Klarna.initialize(baseConfig);
      klarna.setIntegrationMetadata({
        integrator: {
          name: 'MyApp',
          sessionReference: 'ref_123',
          moduleName: 'react-native-klarna',
          moduleVersion: '1.0.0',
        },
      });
      const [, spec] = mockedNative.setIntegrationMetadata.mock.calls[0]!;
      expect(spec.integrator.moduleName).toBe('react-native-klarna');
      expect(spec.integrator.moduleVersion).toBe('1.0.0');
    });

    it('maps originators when provided', async () => {
      const klarna = await Klarna.initialize(baseConfig);
      klarna.setIntegrationMetadata({
        integrator: { name: 'MyApp', sessionReference: 'ref_123' },
        originators: [
          { name: 'OriginatorA', sessionReference: 'orig_ref_1' },
          {
            name: 'OriginatorB',
            sessionReference: 'orig_ref_2',
            moduleName: 'mod',
            moduleVersion: '2.0.0',
          },
        ],
      });
      const [, spec] = mockedNative.setIntegrationMetadata.mock.calls[0]!;
      expect(spec.originators).toEqual([
        {
          name: 'OriginatorA',
          sessionReference: 'orig_ref_1',
          moduleName: null,
          moduleVersion: null,
        },
        {
          name: 'OriginatorB',
          sessionReference: 'orig_ref_2',
          moduleName: 'mod',
          moduleVersion: '2.0.0',
        },
      ]);
    });

    it('updates stored metadata on subsequent calls', async () => {
      const klarna = await Klarna.initialize(baseConfig);
      klarna.setIntegrationMetadata(metadata);

      const updatedMetadata: KlarnaIntegrationMetadata = {
        integrator: { name: 'UpdatedApp', sessionReference: 'ref_456' },
      };
      klarna.setIntegrationMetadata(updatedMetadata);

      expect(klarna.getIntegrationMetadata()).toEqual(updatedMetadata);
    });
  });

  describe('dispose', () => {
    it('calls native dispose with instanceId', async () => {
      const klarna = await Klarna.initialize(baseConfig);
      await klarna.dispose();
      expect(mockedNative.dispose).toHaveBeenCalled();
    });
  });
});

describe('KlarnaNetworkSession', () => {
  describe('token', () => {
    it('calls native getSessionToken', async () => {
      const klarna = await Klarna.initialize(baseConfig);
      await klarna.network.session.token();
      expect(mockedNative.getSessionToken).toHaveBeenCalled();
    });

    it('returns the token from native', async () => {
      mockedNative.getSessionToken.mockResolvedValueOnce('tok_abc');
      const klarna = await Klarna.initialize(baseConfig);
      const token = await klarna.network.session.token();
      expect(token).toBe('tok_abc');
    });
  });

  describe('clear', () => {
    it('calls native clearSession', async () => {
      const klarna = await Klarna.initialize(baseConfig);
      await klarna.network.session.clear();
      expect(mockedNative.clearSession).toHaveBeenCalled();
    });
  });
});
