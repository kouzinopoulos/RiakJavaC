/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cern.riak;

import com.basho.riak.client.api.RiakClient;
import com.basho.riak.client.api.commands.kv.FetchValue;
import com.basho.riak.client.api.commands.kv.ListKeys;
import com.basho.riak.client.api.commands.kv.StoreValue;
import com.basho.riak.client.core.RiakCluster;
import com.basho.riak.client.core.RiakNode;
import com.basho.riak.client.core.query.Location;
import com.basho.riak.client.core.query.Namespace;
import com.basho.riak.client.core.query.RiakObject;
import com.basho.riak.client.core.util.BinaryValue;

import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tvanstee
 */
public class RiakConnection {
    List<String> addresses;
    
    RiakClient client;
    RiakCluster cluster;
    Namespace ns;
    
    public RiakConnection(List<String> addresses){
        this.addresses = addresses;
        ns = new Namespace("default", "RiakJavaClientBucket");
    }
    
    public void connect(){
        RiakNode.Builder builder = new RiakNode.Builder();
            builder.withMinConnections(10);
            builder.withMaxConnections(50);
        List<RiakNode> nodes;
        try {
            nodes = RiakNode.Builder.buildNodes(builder, addresses);
            cluster = new RiakCluster.Builder(nodes).build();
            cluster.start();
            client = new RiakClient(cluster);
        } catch (UnknownHostException ex) {
            Logger.getLogger(RiakConnection.class.getName()).log(Level.SEVERE, null, ex);
            // Throw exception
        }
    }
    
    public void disconnect(){
        client.shutdown();;
    }
    
    public void put(String key, String value){
        System.out.println("PUT");
        Location location = new Location(ns, key);
        RiakObject riakObject = new RiakObject();
        riakObject.setValue(BinaryValue.create(value));
        StoreValue store = new StoreValue.Builder(riakObject)
                .withLocation(location).build();
                //.withOption(Option.W, new Quorum(3)).build();
        try {
            client.execute(store);
        } catch (ExecutionException ex) {
            Logger.getLogger(RiakConnection.class.getName()).log(Level.SEVERE, null, ex);
            // Throw exception
        } catch (InterruptedException ex) {
            Logger.getLogger(RiakConnection.class.getName()).log(Level.SEVERE, null, ex);
            // Throw exception
        }
    }
    
    public String get(String key){
        System.out.println("GET");
        String retVal = null;
        
        Location location = new Location(ns, key);
        
        try {
            FetchValue fv = new FetchValue.Builder(location).build();
            FetchValue.Response response = client.execute(fv);
            RiakObject obj = response.getValue(RiakObject.class);
            retVal = obj.getValue().toString();
        } catch (ExecutionException ex) {
            Logger.getLogger(RiakConnection.class.getName()).log(Level.SEVERE, null, ex);
            // Throw exception
        } catch (InterruptedException ex) {
            Logger.getLogger(RiakConnection.class.getName()).log(Level.SEVERE, null, ex);
            // Throw exception
        }
        
        return retVal;
    }
    
}
