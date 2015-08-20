/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cern.riakjavaclient;

import com.cern.messaging.MessageListener;
import com.cern.riak.RiakConnection;
import java.util.Scanner;

/**
 *
 * @author tvanstee
 */
public class CommandParser {
    
    RiakConnection riakConnection;
    MessageListener messageListener;
    
    /**
     * Constructor
     * @param riakConnection To execute commands on
     * @param messageListener To execute commands on
     */
    public CommandParser(RiakConnection riakConnection, MessageListener messageListener){
        this.riakConnection = riakConnection;
        this.messageListener = messageListener;
    }
    
    /**
     * Thread listening to commands on system input
     */
    public void run(){
        boolean stop = false;
        Scanner scanner = new Scanner(System.in);
        
        while(!stop){
            String input = scanner.next();
            switch(input.toLowerCase()){
                case "stop":
                    stop=true;
                    break;
                case "delete":
                    System.out.println("Please provide minKey");
                    String minKey = scanner.next();
                    System.out.println("Please provide maxKey");
                    String maxKey = scanner.next();
                    System.out.println("Delete from " + minKey + " to " + maxKey + " starting...");
                    riakConnection.delete(Integer.parseInt(minKey), Integer.parseInt(maxKey));
                    System.out.println("Done!");
                    break;
            }
        }
    }
}
