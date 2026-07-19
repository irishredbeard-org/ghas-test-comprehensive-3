package main

import (
	"net/http"
	"net/http/httptest"
	"testing"
)

// Characterization tests for the intentionally vulnerable open-redirect handlers.
// These document (do NOT fix) the CWE-601 behavior: unvalidated request input
// controls the redirect destination via http.Redirect's Location header.

const evilTarget = "https://evil.example.com"

// assertRedirect drives a handler with a malicious query param and asserts the
// resulting Location header reflects the attacker-controlled target.
func assertRedirect(t *testing.T, handler http.HandlerFunc, param string) {
	t.Helper()

	req := httptest.NewRequest("GET", "/?"+param+"="+evilTarget, nil)
	rr := httptest.NewRecorder()

	handler(rr, req)

	if got := rr.Code; got < 300 || got >= 400 {
		t.Fatalf("expected a 3xx redirect status, got %d", got)
	}

	if got := rr.Header().Get("Location"); got != evilTarget {
		t.Fatalf("expected Location header %q, got %q", evilTarget, got)
	}
}

func TestOpenRedirectLoginFollowsUserInput(t *testing.T) {
	assertRedirect(t, OpenRedirectLogin, "next")
}

func TestOpenRedirectLogoutFollowsUserInput(t *testing.T) {
	assertRedirect(t, OpenRedirectLogout, "next")
}

func TestOpenRedirectInviteFollowsUserInput(t *testing.T) {
	assertRedirect(t, OpenRedirectInvite, "next")
}

func TestOpenRedirectSupportFollowsUserInput(t *testing.T) {
	assertRedirect(t, OpenRedirectSupport, "target")
}
