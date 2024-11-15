{
  description = "Check the class file version of all class files within a JAR file";

  # github:NixOS/nixpkgs/dc460ec76cbff0e66e269457d7b728432263166c
  inputs.nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";

  outputs = {
    self,
    nixpkgs,
  }: let
    supportedSystems = ["x86_64-linux" "x86_64-darwin" "aarch64-linux" "aarch64-darwin"];
    forAllSystems = nixpkgs.lib.genAttrs supportedSystems;
    nixpkgsFor = forAllSystems (system:
      import nixpkgs {
        inherit system;
        overlays = [
          self.overlays.default
        ];
      });
  in {
    formatter = forAllSystems (system: (nixpkgsFor.${system}).alejandra);
    overlays.default = final: prev: {
      check-jar-versions = prev.callPackage ./derivation.nix {};
    };
    packages = forAllSystems (system: {
      default = (nixpkgsFor.${system}).check-jar-versions;
    });
    devShells = forAllSystems (system:
      with nixpkgsFor.${system}; {
        default = mkShellNoCC {
          packages = [
          ];
          inputsFrom = [check-jar-versions];
        };
      });
  };
}
