src
====

Utility to make my interactions with git repositories smoother.

Installation
------------

Distributed via [coursier](https://get-coursier.io/). Please refer to http://github.com/lavrov/channel for instructions.

Build
-----

Binary executable:
```shell
nix-shell
mill cli.native.nativeLink
out/cli/native/nativeLink.dest/out --help
```
