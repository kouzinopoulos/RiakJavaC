/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cern.messaging;

import org.zeromq.ZMQ;
import org.zeromq.ZMQException;

/**
 *
 * @author tvanstee
 */
public class MessageListener implements Runnable {
    
    Thread t;
    MessageParser messageParser;
    
    ZMQ.Socket responder;
    ZMQ.Context context;
    
    public MessageListener(MessageParser messageParser){
        this.messageParser = messageParser;
    }
    
    @Override
    public void run() {
        System.out.println("Running the thread");
        context = ZMQ.context(1);

        //  Socket to talk to clients
        responder = context.socket(ZMQ.REP);
        responder.bind("tcp://*:5555");

        while (!Thread.currentThread().isInterrupted()) {
            // Wait for next request from the client
            try{
                byte[] request = responder.recv(0);      
                byte[] reply = messageParser.parseRequest(request);
                responder.send(reply, 0);
            } catch( ZMQException e) {
                if( e.getErrorCode() == ZMQ.Error.ETERM.getCode()){
                    break;
                }
            }
        }
        responder.close();
    }
    
    public void start(){
        if(t==null){
            t = new Thread(this);
            t.start();
        }
    }
    
    public void stop(){
        if(t!=null){
            context.term();
            try{
                t.interrupt();
                t.join();
            } catch(InterruptedException e){
            }
        }
    }
}
