#ifndef __KEYVALUEDB_H__
#define __KEYVALUEDB_H__

#include <string>
#include "MessageSender.h"

using namespace std;

class KeyValueDB {
public:
  KeyValueDB(MessageSender* msgSender);
  ~KeyValueDB();

  void putValue(string key, string value);
  string getValue(string key);

private:
  MessageSender* messageSender;
};

#endif