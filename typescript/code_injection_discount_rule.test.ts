import { code_injection_discount_rule } from './code_injection_discount_rule';

describe('code_injection_discount_rule (characterization)', () => {
  it('evaluates the request expression param as code', () => {
    const result = code_injection_discount_rule({ query: { expression: '2 + 3' } });
    expect(result).toBe(5);
  });
});
