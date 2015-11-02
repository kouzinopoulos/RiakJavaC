# RiakJavaC

To allow easy and efficient communication between the C/C++ application (or another language) and the RiakJavaClient, ZeroMQ is used as a communication protocol. So, messages are generated in the application (for example written in C/C++), send to the RiakJavaClient via ZeroMQ, which performs the command within the message (e.g. a put or get) on the Riak database via the official Riak Java library. To see example code for the C/C++ application, you could look at the "CSrc" folder in the RiakJavaC repository or at the repository for KeyValueClusterPerf (RiakJavaKeyValueDB.cc) in which this is actually used.

The protocol buffer schema has to be created for dummy.proto and request.proto. The generated files should be present at the CSrc/src/ subdirectory (FIXME: remove the actuall cc files from the repository and add their generation to the repository)

protoc --cpp_out=../CSrc/src/ dummy.proto
protoc --cpp_out=../CSrc/src/ request.proto

The java bindings to zmq have to be compiled and installed:

git clone https://github.com/zeromq/jzmq.git

To compile and pack the hwbrocker application, use Apache Ant:

ant
ant compile
ant jar

Then, to execute the hwbrocker, use the following:

/usr/lib/jvm/ja-7-openjdk-amd64/jre/bin/java -cp /opt/alice/external/share/java/zmq.jar:/opt/alice/RiakJavaC/hwbroker/dist/hwbroker.jar hwbroker.Hwbroker

If SHA1 checking should be simulated, append "secure" to the command. If debugging information should be printed, append "debug"
