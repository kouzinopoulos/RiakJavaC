#include <iostream>
#include <sstream>

#include "MessageSender.h"
#include "KeyValueDB.h"

#include "dummy.pb.h"

using namespace std;

int main(int argc, char* argv[])
{
  // Start message sender
  MessageSender messageSender;

  // Initialise the keyValueDB
  KeyValueDB keyValueDB(&messageSender);

  // Put and get a value
  keyValueDB.putValue("testKey", "testValue");
  string value = keyValueDB.getValue("testKey");

  // Print out received value
  cout << "Received value: " << value << endl;

  // Create a dummy object
  objects::DummyObject* obj = new objects::DummyObject;
  obj->set_firststring("testString");
  obj->set_secondstring("secondteststring");
  obj->set_firstint(10);

  // Serialize dummy object to a serialized object
  string serializedObject;
  obj->SerializeToString(&serializedObject);

  // Put and get the serialized object
  keyValueDB.putValue("serializedObjectKey", serializedObject);
  string returnedString = keyValueDB.getValue("serializedObjectKey");

  // De-serialize object and print the contained values
  objects::DummyObject* returnObj = new objects::DummyObject;
  returnObj->ParseFromString(returnedString);

  cout << "Received value: " << returnObj->firststring() << endl;
  cout << "Received value: " << returnObj->secondstring() << endl;

  stringstream ss;
  ss << returnObj->firstint();

  cout << "Received value: " << ss.str() << endl;
}
