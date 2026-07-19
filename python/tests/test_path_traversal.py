"""Characterization tests for the path-traversal fixtures.

Each fixture reads ``request.args['file']`` and joins it onto a fixed base
directory with ``pathlib.Path``, then calls ``.read_text()``. To avoid touching
the real filesystem we monkeypatch ``pathlib.Path.read_text`` to record the path
it was called on, then assert the ``../`` traversal payload survives into the
resolved path -- i.e. request input still controls the filesystem path.
"""
import pathlib

import pytest

import path_traversal_archive_restore
import path_traversal_download
import path_traversal_log_viewer
import path_traversal_template

# URL-encoded  ->  ../../etc/passwd
PAYLOAD_QS = "/?file=..%2F..%2Fetc%2Fpasswd"
DECODED_PAYLOAD = "../../etc/passwd"

CASES = [
    (path_traversal_archive_restore, "path_traversal_archive_restore", "/var/app/archive"),
    (path_traversal_download, "path_traversal_download", "/var/app/data"),
    (path_traversal_log_viewer, "path_traversal_log_viewer", "/var/app/data"),
    (path_traversal_template, "path_traversal_template", "/var/app/data"),
]


@pytest.mark.parametrize("module, func_name, base_dir", CASES)
def test_request_input_controls_filesystem_path(app, monkeypatch, module, func_name, base_dir):
    recorded = {}

    def fake_read_text(self, *args, **kwargs):
        recorded["path"] = str(self)
        return "FILE_CONTENTS"

    monkeypatch.setattr(pathlib.Path, "read_text", fake_read_text)

    with app.test_request_context(PAYLOAD_QS):
        result = getattr(module, func_name)()

    assert result == "FILE_CONTENTS"
    # The traversal payload is joined onto the fixed base with no sanitisation.
    assert recorded["path"] == base_dir + "/" + DECODED_PAYLOAD
    assert DECODED_PAYLOAD in recorded["path"]
