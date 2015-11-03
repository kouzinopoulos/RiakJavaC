#include <iostream>
#include <string>

#include "KeyValueDB.h"
#include "request.pb.h"
#include "MessageSender.h"

#define LOG_DEBUG(msg) cout << "DEBUG: " << msg << endl

KeyValueDB::KeyValueDB(MessageSender* msgSender) { messageSender = msgSender; }

KeyValueDB::~KeyValueDB()
{
  // Nothing to destroy
}

void KeyValueDB::putValue(string key, string value)
{
  // generate the PUT message
  messaging::RequestMessage* msg = new messaging::RequestMessage;
  msg->set_command("PUT");
  msg->set_key(key);
  msg->set_value(value);
  // convert the message to sendable string
  string msgString;
  msg->SerializeToString(&msgString);
  // send the message
  messageSender->send(msgString);

  // check reply for OK
  // receive message as string
  string repString = messageSender->receive();
  // convert to actual message
  messaging::RequestMessage* msgReply = new messaging::RequestMessage;
  msgReply->ParseFromString(repString);
  // check the replyCommand
  if (msgReply->command().compare("OK") != 0) {
    // There was an error
    LOG_DEBUG(msgReply->error());
  }

  delete msg;
  delete msgReply;
}

string KeyValueDB::getValue(string key)
{
  // generate the GET message
  messaging::RequestMessage* msg = new messaging::RequestMessage;
  msg->set_command("GET");
  msg->set_key(key);
  // convert the message to sendable string
  string getmsg;
  msg->SerializeToString(&getmsg);
  // send the message
  messageSender->send(getmsg);

  // check reply for OK
  string repString = messageSender->receive();
  // convert to actual message
  messaging::RequestMessage* msgReply = new messaging::RequestMessage;
  msgReply->ParseFromString(repString);
  string replyValue = "";
  // check the replyCommand
  if (msgReply->command().compare("OK") != 0) {
    // There was an error
    LOG_DEBUG(msgReply->error());
  } else {
    // read out the returned value
    replyValue = msgReply->value();
  }

  delete msg;
  delete msgReply;

  return replyValue;
}