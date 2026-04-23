const { getDefaultConfig, mergeConfig } = require('@react-native/metro-config');

/**
 * Metro configuration
 * https://reactnative.dev/docs/metro
 *
 * @type {import('metro-config').MetroConfig}
 */
const path = require('path');
const fs = require('fs');

const projectRoot = __dirname;
const workspaceRoot = path.resolve(projectRoot, '..');
const workspaceNodeModules = path.resolve(workspaceRoot, 'node_modules');

const packagesDir = path.resolve(workspaceRoot, 'packages');
const packagesWatchFolders = fs
  .readdirSync(packagesDir, { withFileTypes: true })
  .filter((entry) => entry.isDirectory())
  .map((entry) => path.join(packagesDir, entry.name));

const config = {
  // Watch packages for changes
  watchFolders: [workspaceNodeModules, ...packagesWatchFolders],
  resolver: {
    // Look for modules in both local and workspace root node_modules
    nodeModulesPaths: [
      path.resolve(projectRoot, 'node_modules'),
      path.resolve(workspaceRoot, 'node_modules'),
    ],
    // This fix is needed to prevent duplicate module resolution of react/react-native from the nested node_modules inside each library package.
    // Since both the TestApp and the library packages live in the same workspace and share the same node_modules,
    // Metro can get confused and resolve react/react-native from the nested node_modules in each library package instead of the workspace root, leading to runtime errors due to incompatible versions.
    // Removing this will cause a TurboModuleRegistry "module not found" error when running the test app.
    // The blockList forces Metro to fall through to the workspace root's version for all react/react-native imports.
    blockList: [
      /packages\/.*\/node_modules\/react-native\/.*/,
      /packages\/.*\/node_modules\/react\/.*/,
    ],
  },
};

module.exports = mergeConfig(getDefaultConfig(__dirname), config);
