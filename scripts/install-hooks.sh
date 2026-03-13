#!/bin/bash
#
# Install git hooks for Factor Craft
#

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
GIT_DIR="$(git rev-parse --git-dir 2>/dev/null)"

if [ -z "$GIT_DIR" ]; then
    echo "❌ Not a git repository"
    exit 1
fi

HOOKS_DIR="$GIT_DIR/hooks"
SOURCE_DIR="$SCRIPT_DIR/hooks"

# Install commit-msg hook
cp "$SOURCE_DIR/commit-msg" "$HOOKS_DIR/commit-msg"
chmod +x "$HOOKS_DIR/commit-msg"

echo "✅ Git hooks installed:"
echo "   - commit-msg (commit format checker)"
echo ""
echo "Run 'scripts/install-hooks.sh' after cloning to set up hooks."