/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cern.messaging;

import com.cern.riak.RiakConnection;
import com.google.protobuf.InvalidProtocolBufferException;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tvanstee
 */
public
class MessageParser {

  RiakConnection riakConnection;
  byte[] replyOK;

  /**
   * constructor
   * @param riakConnection to executed parsed commands on
   */
public
  MessageParser(RiakConnection riakConnection)
  {
    this.riakConnection = riakConnection;
    // pre-generate the ok-message for quick reply
    replyOK = Request.RequestMessage.newBuilder().setCommand("OK").build().toByteArray();
  }

  /**
   * Parses a message received by the MessageListener and generates a return message
   * @param request The raw data of the received message
   * @return The raw data of the reply message
   */
public
  byte[] parseRequest(byte[] request)
  {
    byte[] reply;
    String errorString;
    Request.RequestMessage replyMsg;

    try {
      Request.RequestMessage msg = Request.RequestMessage.parseFrom(request);
      // Select function to perform based on message command
      switch (msg.getCommand()) {
        case "PUT":
          return processPut(msg);
        case "GET":
          return processGet(msg);
        case "ERROR":
          // Todo
          errorString = "Received error message";
          break;
        default:
          errorString = "Unknown command";
      }
    }
    catch (InvalidProtocolBufferException | IllegalArgumentException ex) {
      Logger.getLogger(MessageParser.class.getName()).log(Level.SEVERE, null, ex);
      errorString = ex.toString();
    }

    // An error occured, reply the errorstring
    replyMsg = Request.RequestMessage.newBuilder().setCommand("Error").setError(errorString).build();
    reply = replyMsg.toByteArray();
    return reply;
  }

  /**
   * Perform a PUT operation on the Riak database
   * @param msg The received message containing all parameters
   * @return The generated reply message of the operation on the database
   * @throws IllegalArgumentException If no key or no value is specified
   */
private
  byte[] processPut(Request.RequestMessage msg) throws IllegalArgumentException
  {
    if ((!msg.getKey().isEmpty()) && (!msg.getValue().isEmpty())) {
      riakConnection.put(msg.getKey(), msg.getValue());
      return replyOK;
    } else {
      throw new IllegalArgumentException();
    }
  }

  /**
   * Perform a GET operation on the Riak database
   * @param msg The received message containing all parameters
   * @return The generated reply message of the operation on the database
   * @throws IllegalArgumentException if no key is specified
   */
private
  byte[] processGet(Request.RequestMessage msg) throws IllegalArgumentException
  {
    if (!msg.getKey().isEmpty()) {
      byte[] reply;

      String value = riakConnection.get(msg.getKey());

      // Generate reply message
      reply = Request.RequestMessage.newBuilder().setCommand("OK").setValue(value).build().toByteArray();

      return reply;
    } else {
      throw new IllegalArgumentException();
    }
  }
}
