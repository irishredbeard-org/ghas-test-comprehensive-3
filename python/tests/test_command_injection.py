"""Characterization tests for the command-injection fixtures.

Each fixture reads ``request.args['target']`` and interpolates it into a shell
command run via ``subprocess.call(..., shell=True)``. To avoid actually spawning
a shell we monkeypatch ``subprocess.call`` to record its arguments, then assert
the tainted request input reaches the command string with ``shell=True`` intact.
"""
import pytest

import command_injection_archive
import command_injection_backup
import command_injection_converter

# URL-encoded  ->  foo; rm -rf /
PAYLOAD_QS = "/?target=foo%3B%20rm%20-rf%20%2F"
DECODED_PAYLOAD = "foo; rm -rf /"

CASES = [
    (command_injection_archive, "command_injection_archive"),
    (command_injection_backup, "command_injection_backup"),
    (command_injection_converter, "command_injection_converter"),
]


@pytest.mark.parametrize("module, func_name", CASES)
def test_request_input_reaches_shell_command(app, monkeypatch, module, func_name):
    calls = []

    def fake_call(cmd, **kwargs):
        calls.append((cmd, kwargs))
        return 0

    # subprocess is a shared module object, so patching its ``call`` attribute
    # covers the reference held by the fixture module.
    monkeypatch.setattr(module.subprocess, "call", fake_call)

    with app.test_request_context(PAYLOAD_QS):
        result = getattr(module, func_name)()

    assert result == "done"
    assert len(calls) == 1
    cmd, kwargs = calls[0]
    # Tainted input is embedded, unescaped, in the shell command...
    assert DECODED_PAYLOAD in cmd
    assert cmd == "convert " + DECODED_PAYLOAD + " output.png"
    # ...and it is executed with shell=True (the actual vulnerability).
    assert kwargs.get("shell") is True
