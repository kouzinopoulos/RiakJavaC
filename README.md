# RiakJavaC

To allow easy and efficient communication between the C/C++ application (or another language) and the RiakJavaClient, ZeroMQ is used as a communication protocol. So, messages are generated in the application (for example written in C/C++), send to the RiakJavaClient via ZeroMQ, which performs the command within the message (e.g. a put or get) on the Riak database via the official Riak Java library. To see example code for the C/C++ application, you could look at the "CSrc" folder in the RiakJavaC repository or at the repository for KeyValueClusterPerf (RiakJavaKeyValueDB.cc) in which this is actually used.

The protocol buffer schema has to be created for dummy.proto and request.proto. The generated files should be present at the CSrc/src/ subdirectory (FIXME: remove the actuall cc files from the repository and add their generation to the repository)

protoc --cpp_out=../CSrc/src/ dummy.proto
protoc --cpp_out=../CSrc/src/ request.proto
