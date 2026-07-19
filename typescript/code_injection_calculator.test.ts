import { code_injection_calculator } from './code_injection_calculator';

describe('code_injection_calculator (characterization)', () => {
  it('evaluates the request expression param as code', () => {
    const result = code_injection_calculator({ query: { expression: '2 + 3' } });
    expect(result).toBe(5);
  });
});
