#!/usr/bin/env bash
# Smoke test of a RUNNING backend over real HTTP, as the frontend would talk to it.
# Each stage adds its own checks below (docs/stages/).
#
# Usage: ./scripts/smoke.sh [base-url]        default: http://localhost:8080
# Exit code: 0 when every check passes, 1 otherwise.
set -euo pipefail

BASE_URL="${1:-http://localhost:8080}"
BASE_URL="${BASE_URL%/}"

failures=0
STATUS=""
BODY=""

pass() { printf 'OK   %s\n' "$1"; }
fail() {
  printf 'FAIL %s\n' "$1" >&2
  failures=$((failures + 1))
}

# request METHOD PATH [JSON_BODY]
# Sets STATUS (HTTP code, or 000 when there was no response) and BODY.
request() {
  local method="$1" path="$2" data="${3:-}"
  local args=(--silent --connect-timeout 3 --max-time 15 -X "$method" -H "Accept: application/json"
    --write-out $'\n%{http_code}')
  if [[ -n "$data" ]]; then
    args+=(-H "Content-Type: application/json" --data "$data")
  fi
  local out
  if ! out=$(curl "${args[@]}" "$BASE_URL$path"); then
    STATUS="000"
    BODY=""
    return 0
  fi
  STATUS="${out##*$'\n'}"
  BODY="${out%$'\n'*}"
}

# --- Stage 1: health ---------------------------------------------------------

request GET /api/health
if [[ "$STATUS" == "000" ]]; then
  fail "GET /api/health -> no response from $BASE_URL (is the backend running?)"
  echo "Smoke test failed." >&2
  exit 1
elif [[ "$STATUS" != "200" ]]; then
  fail "GET /api/health -> $STATUS, expected 200"
elif [[ "$BODY" != *'"status":"ok"'* ]]; then
  fail "GET /api/health -> 200, but the body has no \"status\":\"ok\": $BODY"
else
  pass "GET /api/health -> 200, status ok"
fi

# --- Result ------------------------------------------------------------------

if ((failures > 0)); then
  echo "Smoke test failed: $failures check(s)." >&2
  exit 1
fi
echo "Smoke test passed."
