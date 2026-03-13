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

# Install all hooks
hooks=("commit-msg" "pre-commit" "pre-push")

for hook in "${hooks[@]}"; do
    if [ -f "$SOURCE_DIR/$hook" ]; then
        cp "$SOURCE_DIR/$hook" "$HOOKS_DIR/$hook"
        chmod +x "$HOOKS_DIR/$hook"
        echo "✅ Installed: $hook"
    fi
done

echo ""
echo "Git hooks installed successfully!"
echo ""
echo "📋 Hooks summary:"
echo "   pre-commit  - 编译检查（每次 commit）"
echo "   pre-push    - 快速测试（每次 push）"
echo "   commit-msg  - Commit 格式检查（每次 commit）"
echo ""
echo "💡 跳过检查:"
echo "   git commit --no-verify"
echo "   git push --no-verify"