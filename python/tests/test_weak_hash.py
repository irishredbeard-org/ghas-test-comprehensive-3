"""Characterization tests for the weak-hash fixtures.

Each fixture reads ``request.args['token']`` and hashes it with a weak algorithm
(SHA-1 or MD5), returning the hex digest. We assert the returned value is a
hash-shaped hex string of the expected length, and pin the exact digest so the
weak algorithm in use stays observable.
"""
import re

import pytest

import weak_hash_api_key
import weak_hash_password_reset
import weak_hash_session_cookie

TOKEN_QS = "/?token=secret"

# Precomputed digests of the literal string "secret".
SHA1_SECRET = "e5e9fa1ba31ecd1ae84f75caaa474f3a663f05f4"  # 40 hex chars
MD5_SECRET = "5ebe2294ecd0e0f08eab7690d2a6ee69"          # 32 hex chars

CASES = [
    (weak_hash_api_key, "weak_hash_api_key", 40, SHA1_SECRET),          # SHA-1
    (weak_hash_password_reset, "weak_hash_password_reset", 32, MD5_SECRET),  # MD5
    (weak_hash_session_cookie, "weak_hash_session_cookie", 32, MD5_SECRET),  # MD5
]


@pytest.mark.parametrize("module, func_name, length, expected_digest", CASES)
def test_returns_weak_hash_of_request_input(app, module, func_name, length, expected_digest):
    with app.test_request_context(TOKEN_QS):
        result = getattr(module, func_name)()

    # Hash-shaped: lowercase hex of the expected length for the weak algorithm.
    assert re.fullmatch(r"[0-9a-f]{%d}" % length, result)
    # Pinned digest confirms the specific weak algorithm hashing request input.
    assert result == expected_digest
