#include <iostream>
#include <string>

#include "KeyValueDB.h"
#include "request.pb.h"
#include "MessageSender.h"

#define LOG_ERROR(msg) cout << "ERROR: " << msg << endl

KeyValueDB::KeyValueDB(MessageSender* msgSender) { messageSender = msgSender; }

KeyValueDB::~KeyValueDB()
{
}

void KeyValueDB::putValue(string key, string value)
{
  // Generate the PUT message
  messaging::RequestMessage* msg = new messaging::RequestMessage;
  msg->set_command("PUT");
  msg->set_key(key);
  msg->set_value(value);

  // Serialize the message as a string
  std::string msgString;
  msg->SerializeToString(&msgString);

  // Send the message to broker
  messageSender->send(msgString);

  // Receive message as a serialized string
  string repString = messageSender->receive();

  // Convert to a key/value pair
  messaging::RequestMessage* msgReply = new messaging::RequestMessage;

  // Check the reply command for an "OK"
  msgReply->ParseFromString(repString);

  if (msgReply->command().compare("OK") != 0) {
    LOG_ERROR(msgReply->error());
  }

  delete msg;
  delete msgReply;
}

string KeyValueDB::getValue(string key)
{
  // Generate the GET message
  messaging::RequestMessage* msg = new messaging::RequestMessage;
  msg->set_command("GET");
  msg->set_key(key);

  //  Serialize the message as a string
  std::string msgString;
  msg->SerializeToString(&msgString);

  // Send the message to broker
  messageSender->send(msgString);

  // Receive message as a serialized string
  string repString = messageSender->receive();

  // Convert to a key/value pair
  messaging::RequestMessage* msgReply = new messaging::RequestMessage;

  // Check the reply command for an "OK"
  // Return the received value in case of success or a NULL string otherwise
  msgReply->ParseFromString(repString);

  string replyValue = "";

  if (msgReply->command().compare("OK") != 0) {
    LOG_ERROR(msgReply->error());
  } else {
    replyValue = msgReply->value();
  }

  delete msg;
  delete msgReply;

  return replyValue;
}
