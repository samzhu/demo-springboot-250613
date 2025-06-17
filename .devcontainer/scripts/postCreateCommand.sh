#! /usr/bin/env bash
set -eu
# e: (Exit on error) Exit the script immediately if any command returns a non-zero status
# u: (Undefined variable) Error out and exit when using undefined variables
# x: (eXtrace) Output each command before execution, helpful for debugging
set -o pipefail
# In pipe commands (command1 | command2), if any command fails, the entire pipe is considered failed
# By default, only the exit status of the last command is checked

# Output current username
echo "User: $USER"
# Get and output current working directory path
current_dir=$(pwd)
echo "Current directory: $current_dir"

# Install java formatter
bash ./.devcontainer/scripts/install-java-formatter.sh

# Install Git hooks
bash ./.devcontainer/scripts/install-git-hooks.sh

# Verify installations
echo "Verifying installations..."
echo "Java version:"
java -version || echo "Java not installed."
echo "Gradle version:"
gradle -v || echo "Gradle not installed."
echo "google-java-format version:"
google-java-format --version || echo "google-java-format not installed correctly."

echo "Installation successful! Environment is ready."
