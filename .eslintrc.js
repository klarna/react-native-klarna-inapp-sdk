module.exports = {
  parser: '@typescript-eslint/parser',
  parserOptions: {
    project: ['tsconfig.base.json', 'packages/*/tsconfig.json'],
    tsconfigRootDir: __dirname,
    sourceType: 'module',
  },
  plugins: ['@typescript-eslint/eslint-plugin'],
  root: true,
  env: {
    jest: true,
  },
  ignorePatterns: [
    '.eslintrc.js',
    'lib/**/*',
    'android/**/*',
    'ios/**/*',
    'node_modules/**/*',
    'TestApp/**/*',
  ],
  extends: ['@react-native', 'prettier'],
};
