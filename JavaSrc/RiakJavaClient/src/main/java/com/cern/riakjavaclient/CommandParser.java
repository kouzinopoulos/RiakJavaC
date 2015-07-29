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
    
    public CommandParser(RiakConnection riakConnection, MessageListener messageListener){
        this.riakConnection = riakConnection;
        this.messageListener = messageListener;
    }
    
    public void run(){
        boolean stop = false;
        Scanner scanner = new Scanner(System.in);
        
        while(!stop){
            String input = scanner.next();
            switch(input.toLowerCase()){
                case "stop":
                    stop=true;
                    break;
            }
        }
    }
}
