import { redos_log_tokenizer } from './redos_log_tokenizer';

describe('redos_log_tokenizer (characterization)', () => {
  it('returns a defined boolean for a benign input', () => {
    const result = redos_log_tokenizer('abc123');
    expect(result).toBeDefined();
    expect(result).toBe(true);
  });
});
