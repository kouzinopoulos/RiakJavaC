#ifndef __MESSAGESENDER_H__
#define __MESSAGESENDER_H__

#include <string>
#include <zmq.hpp>

using namespace std;

class MessageSender {
public:
  MessageSender();
  ~MessageSender();

  void send(string message);
  string receive();

private:
  zmq::context_t* context;
  zmq::socket_t* socket;
};

#endif