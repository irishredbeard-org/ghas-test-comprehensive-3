const handler = require('./code_injection_eval_admin');

describe('code_injection_eval_admin', () => {
  test('evaluates user expression as code (injection preserved)', () => {
    const res = { json: jest.fn() };
    handler({ query: { expression: '2 + 3' } }, res);
    expect(res.json).toHaveBeenCalledWith({ result: 5 });
  });
});
