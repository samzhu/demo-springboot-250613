# To learn more about how to use Nix to configure your environment
# see: https://developers.google.com/idx/guides/customize-idx-env
{ pkgs, ... }: {
  channel = "stable-24.11";

  # Use https://search.nixos.org/packages to find packages
  packages = [
    pkgs.jdk21_headless
    pkgs.gradle
  ];

  # Sets environment variables in the workspace
  env = {};

  idx = {
    # Search for the extensions you want on https://open-vsx.org/ and use "publisher.id"
    extensions = [
      "vscjava.vscode-java-pack"
      "cweijan.vscode-database-client2"
    ];

    workspace = {
      onCreate = {
        default.openFiles = [
          "README.md"
        ];
      };
    };
  };

  services = {
    docker = {
      enable = true;
    };

    postgres = {
      enable = false;
    };

    redis = {
      enable = false;
    };
  };
}