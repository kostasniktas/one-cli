# ONE CLI

This is yet another Open Nebula CLI.  I wanted to write this so I didn't have to worry about ruby or anything else.  The original motivation is just mapping a node name to an IP because the host names may not always propagate through DNS as fast as we would like.

I recommend you use the shadow jar to package up all the dependencies.

```console
$ ./gradlew  shadowJar
// Stuff
$ java -jar ./build/libs/one-cli-all-VERSION.jar --help
usage: one-cli [--help] --search-name <arg> [-v <arg>]
    --help                Print help information
    --search-name <arg>   Search for ONE nodes by name
 -v,--view <arg>          The information to display for information found
```
