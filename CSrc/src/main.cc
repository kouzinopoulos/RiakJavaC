#include <iostream>

#include "MessageSender.h"
#include "KeyValueDB.h"

using namespace std;

#define LOG_DEBUG(msg)    cout << "DEBUG: " << msg << endl

int main(int argc, char *argv[])
{
	// Start message sender
	MessageSender messageSender;
	// Initialise the keyValueDB
	KeyValueDB keyValueDB(&messageSender);

	// Put and get a value
	keyValueDB.putValue("testKey", "testValue");
	string value = keyValueDB.getValue("testKey");

	// Print out received value
	LOG_DEBUG(value);
}