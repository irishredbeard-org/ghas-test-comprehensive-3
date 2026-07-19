"""Characterization tests for the SQL-injection fixtures.

Each fixture reads ``request.args['value']`` and concatenates it directly into a
SQL string that is handed to ``db.execute(...)``. We pass a stub ``db`` that
records the SQL it receives, then assert the raw (un-escaped) request input
reaches the SQL string verbatim -- i.e. the injection is still present.
"""
import pytest

from sql_injection_report_filter import sql_injection_report_filter
from sql_injection_test_fixture import sql_injection_test_fixture
from sql_injection_ticket_search import sql_injection_ticket_search
from sql_injection_user_lookup import sql_injection_user_lookup

# URL-encoded  ->  x' OR '1'='1
PAYLOAD_QS = "/?value=x%27%20OR%20%271%27%3D%271"
DECODED_PAYLOAD = "x' OR '1'='1"


class RecordingDB:
    """Stub DB that records the SQL passed to ``execute`` and returns a sentinel."""

    SENTINEL = object()

    def __init__(self):
        self.last_sql = None

    def execute(self, sql):
        self.last_sql = sql
        return self.SENTINEL


@pytest.mark.parametrize(
    "func",
    [
        sql_injection_report_filter,
        sql_injection_test_fixture,
        sql_injection_ticket_search,
        sql_injection_user_lookup,
    ],
)
def test_request_input_reaches_sql_verbatim(app, func):
    db = RecordingDB()
    with app.test_request_context(PAYLOAD_QS):
        result = func(db)

    # The function returns exactly what db.execute returned (no sanitisation layer).
    assert result is RecordingDB.SENTINEL
    # The raw injection payload is embedded, unescaped, in the executed SQL.
    assert db.last_sql is not None
    assert DECODED_PAYLOAD in db.last_sql
    assert db.last_sql == (
        "SELECT * FROM records WHERE owner = '" + DECODED_PAYLOAD + "'"
    )
