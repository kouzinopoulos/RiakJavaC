CC=g++
CFLAGS=-c -Wall -pedantic
LDFLAGS=-std=c++0x -lprotobuf -lzmq
SOURCES=src/*
OBJECTS=$(SOURCES:.cxx=.o)
EXECUTABLE=CMockup

all: $(SOURCES) $(EXECUTABLE)

$(EXECUTABLE): $(OBJECTS)
	$(CC) $(LDFLAGS) $(OBJECTS) -o $@

.cxx.o:
	$(CC) $(CFLAGS) $< -o $@