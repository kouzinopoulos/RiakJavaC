CC=g++
CFLAGS=-Wall -pedantic -I/opt/alice/external/include/
LDFLAGS=-std=c++0x -L/opt/alice/external/lib64/ -lprotobuf -lzmq
SOURCES=src/*
OBJECTS=$(SOURCES:.cxx=.o)
EXECUTABLE=CMockup

all: $(SOURCES) $(EXECUTABLE)

$(EXECUTABLE): $(OBJECTS)
	$(CC) $(CFLAGS) $(OBJECTS) -o $@ $(LDFLAGS)

.cxx.o:
	$(CC) $(CFLAGS) $< -o $@
