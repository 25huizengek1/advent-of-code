{
  description = "Bart Oostveen's advent of code solutions in Kotlin";

  inputs = {
    nixpkgs.url = "github:nixos/nixpkgs/nixpkgs-unstable";
    flake-parts.url = "github:hercules-ci/flake-parts";

    treefmt-nix = {
      url = "github:numtide/treefmt-nix";
      inputs.nixpkgs.follows = "nixpkgs";
    };
  };

  outputs =
    inputs@{ flake-parts, ... }:
    flake-parts.lib.mkFlake { inherit inputs; } {
      systems = [
        "x86_64-linux"
        "aarch64-linux"
        "aarch64-darwin"
        "x86_64-darwin"
      ];

      imports = [
        inputs.treefmt-nix.flakeModule
      ];

      perSystem =
        { pkgs, ... }:

        let
          gradle = pkgs.gradle_9;
          jdk = pkgs.jdk25_headless;
        in
        {
          treefmt = {
            programs.nixfmt.enable = true;
            programs.deadnix.enable = true;
            programs.ktlint.enable = true;
          };

          devShells.default = pkgs.mkShell {
            packages = [
              gradle
              jdk
            ];

            env = {
              JAVA_HOME = jdk.home;
              GRADLE_JAVA_HOME = jdk.home;
            };
          };
        };
    };
}
