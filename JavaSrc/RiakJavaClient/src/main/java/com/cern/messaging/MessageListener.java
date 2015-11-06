package com.cern.messaging;

import org.zeromq.ZMQ;
import org.zeromq.ZMQException;

public
class MessageListener implements Runnable {

  Thread t;
  MessageParser messageParser;

  ZMQ.Socket responder;
  ZMQ.Context context;

  /**
   * Constructor
   * @param messageParser The object that will parse received messages
   */
public
  MessageListener(MessageParser messageParser) { this.messageParser = messageParser; }

  /**
   * The MessageListerner thread method
   *
   */
  @Override public void run()
  {
    System.out.println("Message listener thread running");

    context = ZMQ.context(1);

    //  Socket to connect to the broker
    responder = context.socket(ZMQ.REP);
    responder.connect("tcp://localhost:5560");

    byte[] req = responder.recv(0);
    byte[] rep = messageParser.parseRequest(req);
    responder.send(rep, 0);
    System.out.println("Got first message");

    while (!Thread.currentThread().isInterrupted()) {
      try {
        byte[] request = responder.recv(0);
        byte[] reply = messageParser.parseRequest(request);
        responder.send(reply, 0);
        System.out.println("Got subsequent message");
      }
      catch (ZMQException e) {
        if (e.getErrorCode() == ZMQ.Error.ETERM.getCode()) {
          break;
        }
      }
    }
    responder.close();
  }

  /**
   * Starts the MessageListener thread
   */
public
  void start()
  {
    if (t == null) {
      t = new Thread(this);
      t.start();
    }
  }

  /**
   * Gives the stop signal to the MessageListener thread and waits for it to stop before returning
   */
public
  void stop()
  {
    if (t != null) {
      context.term();
      try {
        t.interrupt();
        t.join();
      }
      catch (InterruptedException e) {
      }
    }
  }
}
