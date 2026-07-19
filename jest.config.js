module.exports = {
  preset: 'ts-jest',
  testEnvironment: 'node',
  testMatch: ['**/typescript/**/*.test.ts', '**/javascript/**/*.test.js'],
  transform: { '^.+\\.ts$': ['ts-jest', { diagnostics: false }] }
};
