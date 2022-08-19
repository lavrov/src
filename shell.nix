{ pkgs ? import <nixpkgs> {} }:
with pkgs;
stdenv.mkDerivation {
  name = "src-dev-env";
  buildInputs = [
    mill
    clang
  ];
}
