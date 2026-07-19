const mysql = require('mysql2');
const pool = mysql.createPool({ host: process.env.DB_HOST || 'localhost', user: 'app', database: 'app' });
// VULN SINK: raw SQL string is passed straight to the driver.
module.exports = { query: (sql, cb) => pool.query(sql, cb) };
