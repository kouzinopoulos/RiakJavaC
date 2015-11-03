RiakJavaC
=========

To allow easy and efficient communication between the C/C++ application (or another language) and the RiakJavaClient, ZeroMQ is used as a communication protocol. So, messages are generated in the application (for example written in C/C++), send to the RiakJavaClient via ZeroMQ, which performs the command within the message (e.g. a put or get) on the Riak database via the official Riak Java library. To see example code for the C/C++ application, you could look at the "CSrc" folder in the RiakJavaC repository or at the repository for KeyValueClusterPerf (RiakJavaKeyValueDB.cc) in which this is actually used.

### Compilation and execution instructions


### Prerequisites

Compile and install the java bindings to zmq:

    git clone https://github.com/zeromq/jzmq.git
    cd jzmq
    sh autogen.sh
    ./configure --with-zeromq=$ZMQ_PATH --prefix=$JZMQ_PATH
    make && make install
    export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$JZMQ_PATH/lib

### C framework

Before compilation of the framework, the schema of protocol buffers has to be created for dummy.proto and request.proto. The generated files should be present at the CSrc/src/ subdirectory.

    protoc --cpp_out=../CSrc/src/ dummy.proto
    protoc --cpp_out=../CSrc/src/ request.proto

Then, compile the framework by executing make

### hwbroker java broker application

To compile and pack the hwbrocker application, use Apache Ant:

    ant
    ant compile
    ant jar

Then, to execute the hwbrocker, use the following:

    java -cp /opt/alice/external/share/java/zmq.jar:/opt/alice/RiakJavaC/hwbroker/dist/hwbroker.jar hwbroker.Hwbroker

If SHA1 checking should be simulated, append "secure" to the command. If debugging information should be printed, append "debug"

### Interface to riak java client

To compile the interface to the riak java client, from the JavaSrc/RiakJavaClient subdirectory, execute:

    mvn compile
    mvn package
    mvn install

This command will build the interface, create a jar file and download all the java prerequisites, including the google protocol buffers, jzmq?? and the riak java client. Then, to execute the interface, use the following command:

java -cp ./lib/*:RiakJavaClient-1.0-SNAPSHOT.jar com.cern.riakjavaclient.Main


### Configuration of the components

