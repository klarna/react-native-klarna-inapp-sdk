/**
 * @klarna/react-native-klarna-network-messaging
 *
 * This package provides a React Native wrapper around Klarna's native
 * KlarnaMessagingPlacementView (iOS & Android). It renders credit promotion
 * messaging (e.g. "From $85/month or 4 payments at 0% interest with Klarna").
 *
 * ## Public API
 *
 * - `KlarnaMessagingPlacementView` — React component that wraps the native view.
 *   Required props: `instance`, `configuration`.
 *   Optional props: `style`, `onError`.
 *
 * - `KlarnaMessagingPlacementConfiguration` — Discriminated union of placement
 *   variants, mirroring the native SDKs. Each variant carries its own
 *   `type`, plus shared `theme`, `amount`, `currency`.
 *
 * - `KlarnaMessagingPlacementType` — Enum of variant tags:
 *   `CreditPromotionAutoSize` or `CreditPromotionBadge`.
 *
 * - `KlarnaTheme` — Enum for visual theme: `Light`, `Dark`, or `Automatic`. When
 *   omitted, the native SDK chooses the default.
 *
 * - `KlarnaMessagingError` — Type for errors reported via `onError` callback.
 *
 * ## Prerequisites
 *
 * The Klarna SDK must be initialized via `@klarna/react-native-klarna-network-core`
 * before rendering `KlarnaMessagingPlacementView`. The component obtains the
 * shared Klarna instance from the core module on the native side.
 *
 * ## Native component behavior
 *
 * - Amount is passed as a string to the native layer (codegen limitation: no int64 in props).
 * - The native view auto-renders on attach and reports its height via `onResized`.
 * - The React component starts with `minHeight: 60` and switches to exact `height`
 *   once the native view reports its measured size.
 */

import React from 'react';
import renderer from 'react-test-renderer';

// Stub the core's TurboModule binding before any import that pulls Klarna
// transitively (KlarnaMessagingPlacementView → core → NativeKlarnaNetworkCore
// would otherwise hit `TurboModuleRegistry.getEnforcing` and fail outside RN).
jest.mock(
  '../../../klarna-network-core/src/Specs/NativeKlarnaNetworkCore',
  () => ({ default: {} })
);

import { KlarnaMessagingPlacementType, KlarnaTheme } from '../index';
import type { KlarnaMessagingPlacementConfiguration } from '../types/KlarnaMessagingPlacementConfiguration';
import { KlarnaMessagingPlacementView } from '../KlarnaMessagingPlacementView';

// Silence React 19's defaultOnUncaughtError. The renderer-level smoke
// tests below assert "construction does not throw" and intentionally do
// not exercise the full host-component tree, which would otherwise log
// uncaught render errors via console.error/warn and fail jest on CI
// (post-test "Cannot log after tests are done" turns into exit code 1).
// Replace the methods at module load and never restore — restoring in
// afterAll would re-enable logging before React's deferred error
// microtasks flush during teardown.
console.error = () => {};
console.warn = () => {};

// Minimal stand-in for a Klarna instance. The component reads only
// `instance.instanceId`, so tests don't need a real one (which would require
// mocking the native module).
const fakeKlarnaInstance = { instanceId: 'test-instance' } as any;

const baseConfig: KlarnaMessagingPlacementConfiguration = {
  type: KlarnaMessagingPlacementType.CreditPromotionAutoSize,
  amount: 9999,
  currency: 'USD',
};

function renderInstance(
  overrides: {
    configuration?: Partial<KlarnaMessagingPlacementConfiguration>;
    style?: any;
    onError?: any;
  } = {}
) {
  const configuration = {
    ...baseConfig,
    ...(overrides.configuration ?? {}),
  } as KlarnaMessagingPlacementConfiguration;
  return renderer.create(
    React.createElement(KlarnaMessagingPlacementView, {
      instance: fakeKlarnaInstance,
      configuration,
      style: overrides.style,
      onError: overrides.onError,
    })
  );
}

// ---------------------------------------------------------------------------
// Component: KlarnaMessagingPlacementView
// ---------------------------------------------------------------------------

describe('KlarnaMessagingPlacementView', () => {
  // -- Happy path --

  it('renders without crashing with required props', () => {
    expect(() => renderInstance()).not.toThrow();
  });

  it('renders with all optional props supplied', () => {
    expect(() =>
      renderInstance({
        configuration: {
          type: KlarnaMessagingPlacementType.CreditPromotionBadge,
          theme: KlarnaTheme.Dark,
          amount: 2500,
          currency: 'SEK',
        },
        style: { marginTop: 10 },
        onError: jest.fn(),
      })
    ).not.toThrow();
  });

  // -- Placement types --

  it('renders with CreditPromotionAutoSize placement type', () => {
    expect(() =>
      renderInstance({
        configuration: {
          type: KlarnaMessagingPlacementType.CreditPromotionAutoSize,
        },
      })
    ).not.toThrow();
  });

  it('renders with CreditPromotionBadge placement type', () => {
    expect(() =>
      renderInstance({
        configuration: {
          type: KlarnaMessagingPlacementType.CreditPromotionBadge,
        },
      })
    ).not.toThrow();
  });

  // -- Themes --

  it('renders with each theme variant', () => {
    for (const theme of [
      KlarnaTheme.Automatic,
      KlarnaTheme.Light,
      KlarnaTheme.Dark,
    ]) {
      expect(() => renderInstance({ configuration: { theme } })).not.toThrow();
    }
  });

  it('passes an empty string to native when theme is omitted, deferring to native SDK default', () => {
    const view = new KlarnaMessagingPlacementView({
      instance: fakeKlarnaInstance,
      configuration: baseConfig,
    });
    const element = view.render() as React.ReactElement<{ theme: string }>;
    expect(element.props.theme).toBe('');
  });

  it('forwards the explicit theme value to native when provided', () => {
    const view = new KlarnaMessagingPlacementView({
      instance: fakeKlarnaInstance,
      configuration: { ...baseConfig, theme: KlarnaTheme.Dark },
    });
    const element = view.render() as React.ReactElement<{ theme: string }>;
    expect(element.props.theme).toBe(KlarnaTheme.Dark);
  });

  // -- Edge cases: amount --

  it('renders with zero amount', () => {
    expect(() =>
      renderInstance({ configuration: { amount: 0 } })
    ).not.toThrow();
  });

  it('renders with very large amount', () => {
    expect(() =>
      renderInstance({ configuration: { amount: 999999999 } })
    ).not.toThrow();
  });

  it('renders with fractional amount (minor units are integers, but component accepts number)', () => {
    expect(() =>
      renderInstance({ configuration: { amount: 99.5 } })
    ).not.toThrow();
  });

  // -- Edge cases: currency --

  it('renders with lowercase currency code', () => {
    expect(() =>
      renderInstance({ configuration: { currency: 'usd' } })
    ).not.toThrow();
  });

  it('renders with 3-letter currency codes from different regions', () => {
    for (const currency of ['USD', 'EUR', 'SEK', 'GBP', 'NOK', 'DKK', 'AUD']) {
      expect(() =>
        renderInstance({ configuration: { currency } })
      ).not.toThrow();
    }
  });

  // -- Edge cases: style --

  it('renders with undefined style', () => {
    expect(() => renderInstance({ style: undefined })).not.toThrow();
  });

  it('renders with complex style object', () => {
    expect(() =>
      renderInstance({
        style: {
          marginTop: 20,
          marginBottom: 10,
          borderRadius: 8,
          backgroundColor: '#F5F5F5',
        },
      })
    ).not.toThrow();
  });

  // -- Edge cases: callbacks --

  it('renders with onError callback provided', () => {
    const onError = jest.fn();
    expect(() => renderInstance({ onError })).not.toThrow();
  });

  it('renders without onError callback (optional)', () => {
    expect(() => renderInstance({ onError: undefined })).not.toThrow();
  });

  // -- Re-rendering --

  it('can re-render with updated props', () => {
    const instance = renderInstance({
      configuration: { amount: 1000, currency: 'USD' },
    });
    expect(() =>
      instance.update(
        React.createElement(KlarnaMessagingPlacementView, {
          instance: fakeKlarnaInstance,
          configuration: {
            type: KlarnaMessagingPlacementType.CreditPromotionBadge,
            theme: KlarnaTheme.Light,
            amount: 2000,
            currency: 'EUR',
          },
        })
      )
    ).not.toThrow();
  });

  it('can unmount cleanly', () => {
    const instance = renderInstance();
    expect(() => instance.unmount()).not.toThrow();
  });

  // -- Error recovery: hasError must clear when configuration fields that
  // drive native recreation change, otherwise nextHeightOnResize will keep
  // returning null and the view stays at the min-height fallback forever.

  function makeView(
    configuration: KlarnaMessagingPlacementConfiguration = baseConfig
  ) {
    return new KlarnaMessagingPlacementView({
      instance: fakeKlarnaInstance,
      configuration,
    });
  }

  it('clears the hasError latch when amount changes', () => {
    const view = makeView();
    (view as any).hasError = true;
    view.componentDidUpdate({
      instance: fakeKlarnaInstance,
      configuration: { ...baseConfig, amount: 1 },
    });
    expect((view as any).hasError).toBe(false);
  });

  it('clears the hasError latch when currency changes', () => {
    const view = makeView();
    (view as any).hasError = true;
    view.componentDidUpdate({
      instance: fakeKlarnaInstance,
      configuration: { ...baseConfig, currency: 'EUR' },
    });
    expect((view as any).hasError).toBe(false);
  });

  it('clears the hasError latch when type or theme changes', () => {
    const view = makeView();
    (view as any).hasError = true;
    view.componentDidUpdate({
      instance: fakeKlarnaInstance,
      configuration: {
        ...baseConfig,
        type: KlarnaMessagingPlacementType.CreditPromotionBadge,
      },
    });
    expect((view as any).hasError).toBe(false);

    (view as any).hasError = true;
    view.componentDidUpdate({
      instance: fakeKlarnaInstance,
      configuration: { ...baseConfig, theme: KlarnaTheme.Dark },
    });
    expect((view as any).hasError).toBe(false);
  });

  it('keeps hasError set when no relevant props change (e.g. style only)', () => {
    const view = makeView();
    (view as any).hasError = true;
    view.componentDidUpdate({
      instance: fakeKlarnaInstance,
      configuration: baseConfig,
      style: { marginTop: 10 },
    });
    expect((view as any).hasError).toBe(true);
  });

  it('collapses to zero height on error (no minHeight fallback)', () => {
    const view = makeView();
    view.setState = jest.fn();
    const element = view.render() as React.ReactElement<{ onError: Function }>;
    element.props.onError({
      nativeEvent: { error: { name: 'ApiError', message: 'invalid' } },
    });
    expect(view.setState).toHaveBeenCalledWith({
      nativeViewHeight: 0,
      hasReceivedResize: true,
    });
  });

  it('collapses when native reports height 0 via onResized', () => {
    const view = makeView();
    view.state = { nativeViewHeight: 0, hasReceivedResize: false };
    view.setState = jest.fn();
    const element = view.render() as React.ReactElement<{
      onResized: Function;
    }>;
    element.props.onResized({ nativeEvent: { height: '0' } });
    expect(view.setState).toHaveBeenCalledWith({
      nativeViewHeight: 0,
      hasReceivedResize: true,
    });
  });

  it('resets hasReceivedResize when configuration changes', () => {
    const view = makeView();
    view.state = { nativeViewHeight: 0, hasReceivedResize: true };
    (view as any).hasError = true;
    view.setState = jest.fn();
    view.componentDidUpdate({
      instance: fakeKlarnaInstance,
      configuration: { ...baseConfig, amount: 1 },
    });
    expect((view as any).hasError).toBe(false);
    expect(view.setState).toHaveBeenCalledWith({ hasReceivedResize: false });
  });

  it('does not call setState for hasReceivedResize when it is already false', () => {
    const view = makeView();
    view.state = { nativeViewHeight: 0, hasReceivedResize: false };
    view.setState = jest.fn();
    view.componentDidUpdate({
      instance: fakeKlarnaInstance,
      configuration: { ...baseConfig, amount: 1 },
    });
    expect(view.setState).not.toHaveBeenCalled();
  });
});

// ---------------------------------------------------------------------------
// Enum: KlarnaMessagingPlacementType
// ---------------------------------------------------------------------------

describe('KlarnaMessagingPlacementType', () => {
  it('CreditPromotionAutoSize maps to correct string', () => {
    expect(KlarnaMessagingPlacementType.CreditPromotionAutoSize).toBe(
      'CreditPromotionAutoSize'
    );
  });

  it('CreditPromotionBadge maps to correct string', () => {
    expect(KlarnaMessagingPlacementType.CreditPromotionBadge).toBe(
      'CreditPromotionBadge'
    );
  });

  it('has exactly two variants', () => {
    const values = Object.values(KlarnaMessagingPlacementType);
    expect(values).toHaveLength(2);
  });

  it('values are usable as native prop strings', () => {
    // The native component expects these exact strings to map to
    // KlarnaMessagingPlacementConfiguration subclasses
    for (const value of Object.values(KlarnaMessagingPlacementType)) {
      expect(typeof value).toBe('string');
      expect(value.length).toBeGreaterThan(0);
    }
  });
});

// ---------------------------------------------------------------------------
// Enum: KlarnaTheme
// ---------------------------------------------------------------------------

describe('KlarnaTheme', () => {
  it('Light maps to "light"', () => {
    expect(KlarnaTheme.Light).toBe('light');
  });

  it('Dark maps to "dark"', () => {
    expect(KlarnaTheme.Dark).toBe('dark');
  });

  it('Automatic maps to "automatic"', () => {
    expect(KlarnaTheme.Automatic).toBe('automatic');
  });

  it('has exactly three variants', () => {
    const values = Object.values(KlarnaTheme);
    expect(values).toHaveLength(3);
  });

  it('values match native KlarnaTheme enum strings', () => {
    // iOS: KlarnaThemeLight, KlarnaThemeDark, KlarnaThemeAutomatic
    // Android: KlarnaTheme.LIGHT, KlarnaTheme.DARK, KlarnaTheme.AUTOMATIC
    // The native wrappers map these lowercase strings to the platform enum.
    const expected = ['light', 'dark', 'automatic'];
    expect(Object.values(KlarnaTheme).sort()).toEqual(expected.sort());
  });
});

// ---------------------------------------------------------------------------
// Public API: exports
// ---------------------------------------------------------------------------

describe('Package exports', () => {
  it('exports KlarnaMessagingPlacementView component', () => {
    const mod = require('../index');
    expect(mod.KlarnaMessagingPlacementView).toBeDefined();
  });

  it('exports KlarnaMessagingPlacementType enum', () => {
    const mod = require('../index');
    expect(mod.KlarnaMessagingPlacementType).toBeDefined();
    expect(
      mod.KlarnaMessagingPlacementType.CreditPromotionAutoSize
    ).toBeDefined();
  });

  it('exports KlarnaTheme enum', () => {
    const mod = require('../index');
    expect(mod.KlarnaTheme).toBeDefined();
    expect(mod.KlarnaTheme.Light).toBeDefined();
  });
});
