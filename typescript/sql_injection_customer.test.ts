import { sql_injection_customer } from './sql_injection_customer';

describe('sql_injection_customer (characterization)', () => {
  it('concatenates raw request input directly into the SQL string', async () => {
    const calls: string[] = [];
    const db = { query: (s: string) => { calls.push(s); return Promise.resolve([]); } };
    await sql_injection_customer(db, { query: { value: "x' OR '1'='1" } });
    expect(calls).toHaveLength(1);
    expect(calls[0]).toContain("x' OR '1'='1");
  });
});
