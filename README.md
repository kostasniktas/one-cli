# ONE CLI

This is yet another Open Nebula CLI.  I wanted to write this so I didn't have to worry about ruby or anything else.  The original motivation is just mapping a node name to an IP because the host names may not always propagate through DNS as fast as we would like.

I recommend you use the shadow jar to package up all the dependencies.

```console
kniktas:~/git/one-cli (master) $ ☺  ./gradlew shadowJar
:compileJava
:processResources UP-TO-DATE
:classes
:shadowJar

BUILD SUCCESSFUL

Total time: 6.687 secs

niktas:~/git/one-cli (master✗) $ ಠ_ಠ  ONE_XMLRPC=https://myserver.example.com/RPC2 ONE_AUTH=~/.oneauth java -jar ./build/libs/one-cli-master-14-g08c43c439c9a-all.jar --help
usage: one-cli [--help] [--search-name <arg>] [-v <arg>]
    --help                Print help information
    --search-name <arg>   Search for ONE nodes by name
 -v,--view <arg>          The information to display for information found
```
