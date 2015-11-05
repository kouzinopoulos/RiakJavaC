#include <iostream>
#include <string>
#include <zmq.hpp>

#include "MessageSender.h"

MessageSender::MessageSender()
{
  // Initialize ZeroMQ
  context = new zmq::context_t(1);
  socket = new zmq::socket_t(*context, ZMQ_REQ);
  socket->connect("tcp://localhost:5559");
}

MessageSender::~MessageSender()
{
  delete socket;
  delete context;
}

void MessageSender::send(string message)
{
  zmq::message_t request(message.length());
  memcpy((void*)request.data(), message.c_str(), message.length());

  std::cout << "Sending message to the broker" << std::endl;

  socket->send(request);
}

string MessageSender::receive()
{
  zmq::message_t reply;
  socket->recv(&reply);
  std::cout << "Received reply from broker" << std::endl;

  string repstr((char*)reply.data(), reply.size());

  return repstr;
}
