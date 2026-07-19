// INTENTIONAL VULNERABILITY: SQL Injection (CWE-89 / js/sql-injection - Medium)
// DUPLICATE_GROUP: js-sqli-001
// This file exists for CodeQL SAST baseline testing only.
const db = require('./db');
function sql_injection_report_filter(req, res) {
  const value = req.query.value;
  // VULN: user input is concatenated directly into SQL.
  db.query("SELECT * FROM records WHERE owner = '" + value + "'", (err, rows) => {
    res.json(rows);
  });
}

module.exports = sql_injection_report_filter;
