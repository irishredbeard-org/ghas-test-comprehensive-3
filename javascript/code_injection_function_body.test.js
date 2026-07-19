const handler = require('./code_injection_function_body');

describe('code_injection_function_body', () => {
  test('turns user input into an executable function body (injection preserved)', () => {
    // This fixture builds `new Function('context', expression)`, so the body
    // must `return` to produce a value; the evaluated result is still 5.
    const res = { json: jest.fn() };
    handler({ query: { expression: 'return 2 + 3' }, user: {} }, res);
    expect(res.json).toHaveBeenCalledWith({ result: 5 });
  });
});
