import { sql_injection_invoice } from './sql_injection_invoice';

describe('sql_injection_invoice (characterization)', () => {
  it('concatenates raw request input directly into the SQL string', async () => {
    const calls: string[] = [];
    const db = { query: (s: string) => { calls.push(s); return Promise.resolve([]); } };
    await sql_injection_invoice(db, { query: { value: "x' OR '1'='1" } });
    expect(calls).toHaveLength(1);
    expect(calls[0]).toContain("x' OR '1'='1");
  });
});
