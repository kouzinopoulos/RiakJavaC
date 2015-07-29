#include <iostream>
#include <sstream>

#include "MessageSender.h"
#include "KeyValueDB.h"

#include "dummy.pb.h"

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

	// Create a dummy object
	objects::DummyObject* obj = new objects::DummyObject;
	obj->set_firststring("testString");
	obj->set_secondstring("secondteststring");
	obj->set_firstint(10);

	string serializedObject;
	obj->SerializeToString(&serializedObject);
	keyValueDB.putValue("testObjectKey", serializedObject);
	string returnedString = keyValueDB.getValue("testObjectKey");

	objects::DummyObject* returnObj = new objects::DummyObject;
	returnObj->ParseFromString(returnedString);
	LOG_DEBUG(returnObj->firststring());
	LOG_DEBUG(returnObj->secondstring());
	stringstream ss;
	ss << returnObj->firstint();
	LOG_DEBUG(ss.str());
}