#!/bin/bash

# dev-resources/install-git-hooks.sh
set -euo pipefail

echo "Installing Git hooks..."


# Check if .git directory exists
if [ ! -d ".git" ]; then
    echo "Error: .git directory not found. Please run this script from a Git repository."
    exit 1
fi

# Ensure the hooks directory exists
mkdir -p .git/hooks

# Copy the format script to the hooks directory
echo "Installing format-staged-java-files.sh..."
cp .devcontainer/scripts/format-staged-java-files.sh .git/hooks/format-staged-java-files.sh
chmod +x .git/hooks/format-staged-java-files.sh

# Create or update pre-commit hook
PRE_COMMIT=".git/hooks/pre-commit"
HOOK_COMMAND='bash "$(git rev-parse --show-toplevel)/.git/hooks/format-staged-java-files.sh"'

# Create pre-commit if it doesn't exist
if [ ! -f "$PRE_COMMIT" ]; then
    echo "#!/bin/bash" > "$PRE_COMMIT"
    echo "$HOOK_COMMAND" >> "$PRE_COMMIT"
    chmod +x "$PRE_COMMIT"
    echo "Created new pre-commit hook."
else
    # Check if hook command already exists
    if grep -q "$HOOK_COMMAND" "$PRE_COMMIT"; then
        echo "Pre-commit hook already contains format-staged-java-files.sh execution."
    else
        # Add hook command to existing pre-commit
        echo "$HOOK_COMMAND" >> "$PRE_COMMIT"
        echo "Updated existing pre-commit hook."
    fi
fi

echo "Git hooks installation complete!"