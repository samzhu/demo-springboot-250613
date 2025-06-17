#!/bin/bash
set -euo pipefail

# Check if google-java-format command exists
if ! command -v google-java-format &> /dev/null; then
    echo "Warning: google-java-format command not found. Skipping format."
    exit 0
fi

# Define the file extension to format
TARGET_EXTENSION=".java"

# Find staged files matching the target extension (staged files only)
staged_files=$(git diff --cached --name-only --diff-filter=ACM | grep "${TARGET_EXTENSION}$" || true)

# Exit if no matching files are found
if [ -z "$staged_files" ]; then
    echo "No staged ${TARGET_EXTENSION} files to format."
    exit 0
fi

echo "Formatting staged ${TARGET_EXTENSION} files..."

# Format each file and re-add it to the staging area
for file in ${staged_files}; do
    if [ -f "$file" ]; then
        google-java-format -i "$file"
        git add "$file"
        echo "Formatted and staged: $file"
    fi
done

# Test
# echo "Test mode: Block the commit to verify the hook. Remove 'exit 1' or replace it with 'exit 0' to allow commits in production."
# exit 1

# 
exit 0