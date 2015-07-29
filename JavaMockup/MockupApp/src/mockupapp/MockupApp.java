/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mockupapp;

import static java.lang.Thread.sleep;
import org.zeromq.ZMQ;
import com.cern.messaging.Request.RequestMessage;

/**
 *
 * @author tvanstee
 */
public class MockupApp {
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException {
        // Generating a request PUT message
        RequestMessage msg = RequestMessage.newBuilder()
            .setCommand("PUT")
            .setKey("sleutel")
            .setValue("Waarde")
            .build();
        
        byte[] msgPUTBytes = msg.toByteArray();
        
        // Generating a request GET message
        msg = RequestMessage.newBuilder()
                .setCommand("GET")
                .setKey("sleutel")
                .build();
        
        byte[] msgGETBytes = msg.toByteArray();
        
        ZMQ.Context context = ZMQ.context(1);

        //  Socket to talk to server
        System.out.println("Connecting to hello world server…");

        ZMQ.Socket requester = context.socket(ZMQ.REQ);
        requester.connect("tcp://localhost:5555");

        // Send the messages
        // PUT
        System.out.println("Sending PUT");
        requester.send(msgPUTBytes, 0);
        
        byte[] reply = requester.recv(0);
        System.out.println("Received " + new String(reply));
        
        // GET
        sleep(1000);
        System.out.println("Sending GET");
        requester.send(msgGETBytes,0);
        
        reply = requester.recv(0);
        System.out.println("Received" + new String(reply));
        
        requester.close();
        context.term();
        
    }
    
}
