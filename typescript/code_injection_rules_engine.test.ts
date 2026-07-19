import { code_injection_rules_engine } from './code_injection_rules_engine';

describe('code_injection_rules_engine (characterization)', () => {
  it('evaluates the request expression param as code', () => {
    const result = code_injection_rules_engine({ query: { expression: '2 + 3' } });
    expect(result).toBe(5);
  });
});
