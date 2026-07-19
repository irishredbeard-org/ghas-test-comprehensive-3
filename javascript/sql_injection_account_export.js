// INTENTIONAL VULNERABILITY: SQL Injection (CWE-89 / js/sql-injection - Medium)
// DUPLICATE_GROUP: js-sqli-002
// This file exists for CodeQL SAST baseline testing only.
const db = require('./db');
function sql_injection_account_export(req, res) {
  const accountId = req.query.accountId;
  // VULN: user input is concatenated directly into SQL.
  db.query("SELECT * FROM exports WHERE account_id = '" + accountId + "'", (err, rows) => {
    res.json(rows);
  });
}

module.exports = sql_injection_account_export;
