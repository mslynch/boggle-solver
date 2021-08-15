{ pkgs ? import (fetchTarball "https://github.com/NixOS/nixpkgs/archive/1509e3cbaae31add55bd1e994db1f65c90926f5c.tar.gz") {} }:

pkgs.mkShell {
  buildInputs = [
    pkgs.maven
    pkgs.adoptopenjdk-bin
    pkgs.docker-compose
  ];
}