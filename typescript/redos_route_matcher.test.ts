import { redos_route_matcher } from './redos_route_matcher';

describe('redos_route_matcher (characterization)', () => {
  it('returns a defined boolean for a benign input', () => {
    const result = redos_route_matcher('abc123');
    expect(result).toBeDefined();
    expect(result).toBe(true);
  });
});
