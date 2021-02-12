module.exports = {
  env: {
    browser: true,
    es2021: true,
    jest: true,
    jasmine: true
  },
  extends: [
    'eslint:recommended'
  ],
  parser: '@typescript-eslint/parser',
  parserOptions: {
    ecmaVersion: 12,
    sourceType: 'module'
  },
  plugins: [
    '@typescript-eslint'
  ],
  rules: {
    'no-unused-vars': 0,
    '@typescript-eslint/no-unused-vars-experimental': 2
  },
}
