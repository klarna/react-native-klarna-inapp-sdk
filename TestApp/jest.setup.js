// Jest runs in plain Node.js — there is no native binary, so TurboModuleRegistry
// is always empty. Native module specs (e.g. NativeKlarnaSignIn.ts) call
// getEnforcing() at the top level, which throws the moment the file is required,
// before any test or screen is reached. This wraps getEnforcing to return an
// empty object for unknown modules while keeping the real implementation for
// built-in RN modules already set up by the react-native preset.
jest.mock('react-native/Libraries/TurboModule/TurboModuleRegistry', () => {
  const real = jest.requireActual(
    'react-native/Libraries/TurboModule/TurboModuleRegistry'
  );
  return {
    ...real,
    getEnforcing: (name) => {
      try {
        return real.getEnforcing(name);
      } catch {
        return {};
      }
    },
  };
});
