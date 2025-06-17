#!/bin/bash

# Define version and download URLs
VERSION="1.25.0"
JAR_URL="https://github.com/google/google-java-format/releases/download/v${VERSION}/google-java-format-${VERSION}-all-deps.jar"
MAC_URL="https://github.com/google/google-java-format/releases/download/v${VERSION}/google-java-format_darwin-arm64"
LINUX_X86_64_URL="https://github.com/google/google-java-format/releases/download/v${VERSION}/google-java-format_linux-x86-64"

echo "Installing google-java-format..."

# Check if already installed
if command -v google-java-format &> /dev/null; then
    echo "google-java-format is already installed."
    exit 0
fi

install_jar_version() {
    echo "Installing JAR version for cross-platform compatibility..."
    
    # Download JAR file
    curl -f -L -o /usr/local/lib/google-java-format.jar "${JAR_URL}" || {
        echo "Failed to download google-java-format JAR"
        exit 1
    }

    # Create wrapper script
    cat << 'EOF' > /usr/local/bin/google-java-format
#!/bin/bash
java -jar "/usr/local/lib/google-java-format.jar" "$@"
EOF

    # Make executable
    chmod +x /usr/local/bin/google-java-format
}

install_native_linux_x86_64() {
    echo "Installing native version for Linux x86_64..."
    
    # Create temp directory
    TMP_DIR=$(mktemp -d)
    cd "$TMP_DIR"

    # Download binary
    curl -f -L -o google-java-format "${LINUX_X86_64_URL}"

    # Make executable
    chmod +x google-java-format

    # Move to bin directory
    mv google-java-format /usr/local/bin/

    # Clean up
    cd - > /dev/null
    rm -rf "$TMP_DIR"
}

# Install based on system and architecture
if [ "$(uname)" = "Linux" ] && [ "$(uname -m)" = "x86_64" ]; then
    # Linux x86_64
    install_native_linux_x86_64
else
    # Default to JAR version
    echo "Unsupported architecture or OS. Using JAR version instead."
    install_jar_version
fi

# Verify installation
if command -v google-java-format &> /dev/null; then
    echo "Installation complete! Testing google-java-format..."
    google-java-format --version || true
    echo "You can now use 'google-java-format' command."
else
    echo "Installation failed. Please check the error messages above."
    exit 1
fi
