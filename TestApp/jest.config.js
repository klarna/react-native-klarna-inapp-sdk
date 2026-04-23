module.exports = {
  preset: 'react-native',
  moduleFileExtensions: ['ts', 'tsx', 'js', 'jsx', 'json', 'node'],
  // Allow ESM packages (e.g. @react-navigation) to be transformed by Babel
  transformIgnorePatterns: [
    'node_modules/(?!(react-native|@react-native|@react-navigation/.*))',
  ],
  // Mock TurboModuleRegistry before any test modules are loaded
  setupFiles: ['./jest.setup.js'],
  moduleNameMapper: {
    // Force react-native to always resolve from workspace root, preventing
    // package-nested node_modules from being picked up when following moduleNameMapper redirects
    '^react-native$': '<rootDir>/../node_modules/react-native',
    '^react-native/(.*)': '<rootDir>/../node_modules/react-native/$1',
    // Resolve workspace packages to their source so Jest can find them without a build step
    'react-native-klarna-inapp-sdk':
      '<rootDir>/../packages/klarna-inapp-sdk/src/index',
    '@klarna/react-native-klarna-(.*)':
      '<rootDir>/../packages/klarna-$1/src/index',
  },
};
