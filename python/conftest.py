"""Shared pytest fixtures for the Python SAST characterization tests.

These tests are *characterization* tests: they lock in the CURRENT (intentionally
vulnerable) behaviour of the fixtures under ``python/`` so that the CodeQL SAST
baseline stays observable. They must never "fix" the fixtures.

The fixture functions rely on the Flask ``request`` global, so each test runs
inside ``app.test_request_context(...)``. Flask 0.12.2 is pinned, so only its
0.12-era API (a plain ``Flask`` app + ``test_request_context``) is used.
"""
import os
import sys

import pytest
from flask import Flask

# The fixture modules live directly in ``python/`` (this file's directory), not in
# an importable package, so make that directory importable for the test modules.
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))


@pytest.fixture
def app():
    """A minimal Flask app used only to provide a request context."""
    return Flask(__name__)
