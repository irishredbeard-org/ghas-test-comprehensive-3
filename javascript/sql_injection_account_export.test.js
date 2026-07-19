jest.mock('./db');
const db = require('./db');
const handler = require('./sql_injection_account_export');

describe('sql_injection_account_export', () => {
  beforeEach(() => jest.clearAllMocks());

  test('concatenates raw input into the SQL string (injection preserved)', () => {
    const marker = "x' OR '1'='1";
    const res = { json: jest.fn() };
    handler({ query: { accountId: marker } }, res);

    expect(db.query).toHaveBeenCalledTimes(1);
    const sql = db.query.mock.calls[0][0];
    expect(sql).toBe("SELECT * FROM exports WHERE account_id = '" + marker + "'");
    expect(sql).toContain(marker);
  });

  test('passes driver rows through to res.json', () => {
    const rows = [{ id: 4 }];
    db.query.mockImplementation((sql, cb) => cb(null, rows));
    const res = { json: jest.fn() };
    handler({ query: { accountId: 'a' } }, res);
    expect(res.json).toHaveBeenCalledWith(rows);
  });
});
