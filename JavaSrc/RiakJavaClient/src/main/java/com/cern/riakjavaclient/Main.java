/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cern.riakjavaclient;

import com.cern.messaging.MessageListener;
import com.cern.messaging.MessageParser;
import com.cern.riak.RiakConnection;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author tvanstee
 */
public class Main {
    static RiakConnection riakConnection;
    static MessageListener messageListener;
    static MessageParser messageParser;
    static CommandParser commandParser;
    
    /**
     * Main entry point of the program
     * @param args
     * @throws InterruptedException 
     */
    public static void main(String [] args) throws InterruptedException
    {
        // Give application online message
        System.out.println("RiakJavaClient launched");
        
        // Configuration of Riak Cluster
        List<String> addresses = new LinkedList<>();
        addresses.add("cernvm11");
        addresses.add("cernvm12");
        addresses.add("cernvm13");
        addresses.add("cernvm14");
        addresses.add("cernvm15");
        addresses.add("cernvm16");
        addresses.add("cernvm17");
        addresses.add("cernvm18");
        
        // Start the connection to Riak
        System.out.println("Starting riak Client");
        riakConnection = new RiakConnection(addresses);
        riakConnection.connect();

        // Start the message handling
        System.out.println("Starting message listener");
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
