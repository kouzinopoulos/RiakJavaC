package com.cern.riakjavaclient;

import com.cern.messaging.MessageListener;
import com.cern.messaging.MessageParser;
import com.cern.riak.RiakConnection;
import java.util.LinkedList;
import java.util.List;
import java.lang.Throwable;
import java.net.UnknownHostException;

public
class Main {
  static RiakConnection riakConnection;
  static MessageListener messageListener;
  static MessageParser messageParser;
  static CommandParser commandParser;

public
  static void main(String[] args) throws InterruptedException
  {
    // Flags that when set to "true" disable consistency verification read/write in Riak
    boolean readQuorumOff = false;
    boolean writeQuorumOff = false;

    if (args.length > 0) {
      for (String s : args) {
        if (s.compareToIgnoreCase("readQuorumOff") == 0) {
          readQuorumOff = true;
          System.out.println("No read quorum");
        }
        if (s.compareToIgnoreCase("writeQuorumOff") == 0) {
          writeQuorumOff = true;
          System.out.println("No write quorum");
        }
      }
    }

    // Provide the list of addresses for the nodes of the Riak Cluster
    List<String> addresses = new LinkedList<>();
    addresses.add("localhost");
    // addresses.add("cernvm12");
    // addresses.add("cernvm13");
    // addresses.add("cernvm14");
    // addresses.add("cernvm15");
    // addresses.add("cernvm16");
    // addresses.add("cernvm17");
    // addresses.add("cernvm18");

    // Connect to the Riak cluster nodes
    System.out.println("Connecting to the Riak cluster nodes");
    riakConnection = new RiakConnection(addresses, readQuorumOff, writeQuorumOff);
    try  {
      riakConnection.connect();
    } catch (UnknownHostException e) {
        e.printStackTrace();
    }

    // Start message handling
    System.out.println("Starting the message listener");
    messageParser = new MessageParser(riakConnection);
    messageListener = new MessageListener(messageParser);
    messageListener.start();

    // Start parsing user commands
    commandParser = new CommandParser(riakConnection, messageListener);
    commandParser.run();

    // Shut everything down
    messageListener.stop();
    riakConnection.disconnect();
  }
}
