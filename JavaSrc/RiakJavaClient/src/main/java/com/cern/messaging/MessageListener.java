package com.cern.messaging;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Poller;
import org.zeromq.ZMQException;

public
class MessageListener implements Runnable {

  Thread t;
  MessageParser messageParser;

  ZMQ.Socket responderGet;
  ZMQ.Socket responderPut;
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
    responderPut = context.socket(ZMQ.PULL);
    responderPut.connect("tcp://localhost:5560");

    responderGet = context.socket(ZMQ.REP);
    responderGet.connect("tcp://localhost:5561");

    Poller items = new Poller(2);
    items.register(responderPut, Poller.POLLIN);
    items.register(responderGet, Poller.POLLIN);

    while (!Thread.currentThread().isInterrupted()) {
      try {
        items.poll();

        if (items.pollin(0)) {
          //System.out.println("message pollin on 0");

          byte[] message = responderPut.recv(0);

          //System.out.println("Received PUT message from the broker");

          messageParser.parseRequest(message);
        }
        if (items.pollin(1)) {
          //System.out.println("message pollin on 1");

          byte[] message = responderGet.recv(0);

          //System.out.println("Received GET message from the broker");

          byte[] reply = messageParser.parseRequest(message);

          responderGet.send(reply, 0);
        }
      }
      catch (ZMQException e) {
        if (e.getErrorCode() == ZMQ.Error.ETERM.getCode()) {
          break;
        }
      }
    }

    responderGet.close();
    responderPut.close();
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
