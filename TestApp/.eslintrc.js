module.exports = {
  parser: '@typescript-eslint/parser',
  parserOptions: {
    project: 'tsconfig.json',
    tsconfigRootDir: __dirname,
    sourceType: 'module',
  },
  plugins: [
    '@typescript-eslint/eslint-plugin'
  ],
  root: true,
  env: {
    jest: true,
  },
  ignorePatterns: [
    '.eslintrc.js'
  ],
  extends: [
    '@react-native',
    'prettier',
  ],
};
