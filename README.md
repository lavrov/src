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
./mill cli.native.nativeLink
out/cli/native/nativeLink.dest/out --help
```

Executable java jar:
```shell
env VERSION=1 ./mill cli.jvm.assembly
java -jar out/cli/jvm/assembly.dest/out.jar --help
```

