#!/usr/bin/env bash
# Claude Code PostToolUse hook (Write|Edit).
# Formats and lints a single file under frontend/src/ right after Claude edits it.
# Exit 0: nothing to report. Exit 2: stderr is fed back to Claude (unfixable problems).
set -u

# Read the hook payload and extract the edited file path (no jq on Windows; node is always there).
file=$(node -e '
  let data = "";
  process.stdin.on("data", (c) => (data += c)).on("end", () => {
    try {
      const j = JSON.parse(data);
      process.stdout.write((j.tool_response && j.tool_response.filePath) || (j.tool_input && j.tool_input.file_path) || "");
    } catch {
      process.stdout.write("");
    }
  });
')

[ -n "$file" ] || exit 0
file=${file//\\//}

case "$file" in
  */frontend/src/*) ;;
  *) exit 0 ;;
esac
[ -f "$file" ] || exit 0

frontend_dir=${file%%/frontend/src/*}/frontend
cd "$frontend_dir" || exit 0

problems=""

case "$file" in
  *.ts | *.tsx | *.js | *.jsx | *.mjs)
    if ! lint_out=$(./node_modules/.bin/eslint --fix "$file" 2>&1); then
      problems+="ESLint found problems it could not fix automatically:"$'\n'"$lint_out"$'\n'
    fi
    ;;
esac

case "$file" in
  *.ts | *.tsx | *.js | *.jsx | *.mjs | *.css | *.json | *.md)
    if ! fmt_out=$(./node_modules/.bin/prettier --write --log-level warn "$file" 2>&1); then
      problems+="Prettier could not format the file:"$'\n'"$fmt_out"$'\n'
    fi
    ;;
esac

if [ -n "$problems" ]; then
  printf '%s' "$problems" >&2
  exit 2
fi
exit 0
