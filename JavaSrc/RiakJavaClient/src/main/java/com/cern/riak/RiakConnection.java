// The RiakConnection class connects to the Riak cluster nodes, and executes put/get commands on demand

package com.cern.riak;

import com.basho.riak.client.api.cap.Quorum;
import com.basho.riak.client.api.commands.buckets.StoreBucketProperties;
import com.basho.riak.client.api.commands.kv.DeleteValue;
import com.basho.riak.client.api.commands.kv.DeleteValue.Option;
import com.basho.riak.client.api.commands.kv.FetchValue;
import com.basho.riak.client.api.commands.kv.ListKeys;
import com.basho.riak.client.api.commands.kv.StoreValue;
import com.basho.riak.client.api.RiakClient;
import com.basho.riak.client.core.RiakCluster;
import com.basho.riak.client.core.RiakNode;
import com.basho.riak.client.core.query.Location;
import com.basho.riak.client.core.query.Namespace;
import com.basho.riak.client.core.query.RiakObject;
import com.basho.riak.client.core.util.BinaryValue;

import java.net.UnknownHostException;
import java.util.List;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

public
class RiakConnection {
  List<String> addresses;

  RiakClient client;
  RiakCluster cluster;
  Namespace ns;

  boolean readQuorumOff;
  boolean writeQuorumOff;

  /**
   * constructor
   * @param addresses Containing the hosts the Riak Cluster consists of
   * @param readQuorumOff Indicates whether to use quorum guarantees for GET
   * @param writeQuorumOff Indicates whether to use quorum guarantees for PUT
   */
public
  RiakConnection(List<String> addresses, boolean readQuorumOff, boolean writeQuorumOff)
  {
    this.addresses = addresses;
    ns = new Namespace("default", "RiakJavaClientBucket2");
    this.readQuorumOff = readQuorumOff;
    this.writeQuorumOff = writeQuorumOff;
  }

public
  void connect() throws UnknownHostException
  {
    // Keep 10-50 connections to each node.
    RiakNode.Builder nodeTemplate = new RiakNode.Builder()
             .withMinConnections(10)
             .withMaxConnections(50);

    // Create a list of 3 nodes to connect to
    LinkedList<RiakNode> nodes = new LinkedList<RiakNode>();
    nodes.add(nodeTemplate.withRemoteAddress("137.138.234.68").withRemotePort(8087).build());
    //nodes.add(nodeTemplate.withRemoteAddress("172.16.1.35").withRemotePort(8087).build());
    //nodes.add(nodeTemplate.withRemoteAddress("172.16.1.36").withRemotePort(8087).build());

    // Increase the number of execution attempts (retries) to 5, from the default of 3
    cluster = new RiakCluster.Builder(nodes)
             .withExecutionAttempts(5)
             .build();

    // RiakCluster must be started before given to RiakClient ctor.
    cluster.start();

    client = new RiakClient(cluster);
  }

  /**
   * Disconnect from the Riak cluster
   */
public
  void disconnect()
  {
    client.shutdown();
    ;
  }

  /**
   * Perform a put operation on the Riak cluster
   * @param key
   * @param value
   */
public
  void put(String key, byte[] value)
  {
//    System.out.println("Putting a key/value pair to the Riak db");

    Location location = new Location(ns, key);
    RiakObject riakObject = new RiakObject();
    riakObject.setValue(BinaryValue.create(value));

    StoreValue store;
    if (writeQuorumOff) {
      // Don't perform quorum guarantees
      store = new StoreValue.Builder(riakObject)
                .withLocation(location)
                .withOption(StoreValue.Option.W, new Quorum(-2))
                .build();
    } else {
      // perform quroum guarantees (default)
      store = new StoreValue.Builder(riakObject).withLocation(location).build();
    }
    try {
      client.execute(store);
    }
    catch (ExecutionException ex) {
      Logger.getLogger(RiakConnection.class.getName()).log(Level.SEVERE, null, ex);
      // Throw exception
    }
    catch (InterruptedException ex) {
      Logger.getLogger(RiakConnection.class.getName()).log(Level.SEVERE, null, ex);
      // Throw exception
    }
  }

  /**
   * Perform a get operation on the Riak cluster
   * @param key
   * @return the value string returned by the cluster
   */
public
  byte[] get(String key)
  {
//    System.out.println("Getting a key/value pair from the Riak db");

    byte[] retVal = null;

    Location location = new Location(ns, key);

    try {
      FetchValue fv;
      if (readQuorumOff) {
        // don't perform quorum guarantees
        fv = new FetchValue.Builder(location).withOption(FetchValue.Option.R, new Quorum(-2)).build();

      } else {
        // perform quroum guarantees (default)
        fv = new FetchValue.Builder(location).build();
      }

      FetchValue.Response response = client.execute(fv);
      RiakObject obj = response.getValue(RiakObject.class);
      retVal = obj.getValue().getValue();
    }
    catch (ExecutionException ex) {
      Logger.getLogger(RiakConnection.class.getName()).log(Level.SEVERE, null, ex);
    }
    catch (InterruptedException ex) {
      Logger.getLogger(RiakConnection.class.getName()).log(Level.SEVERE, null, ex);
    }

    return retVal;
  }

  /**
   * Delete a range of keys from the database, useful in testing
   * @param minKey The lowest key value to delete
   * @param maxKey The highest key value to delete
   */
public
  void delete (int minKey, int maxKey)
  {
    for (int i = minKey; i < maxKey + 1; i++) {
      String key = Integer.toString(i);
      Location loc = new Location(ns, key);
      DeleteValue dv = new DeleteValue.Builder(loc).build();
      try {
        client.execute(dv);
      }
      catch (ExecutionException ex) {
        Logger.getLogger(RiakConnection.class.getName()).log(Level.SEVERE, null, ex);
      }
      catch (InterruptedException ex) {
        Logger.getLogger(RiakConnection.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
  }
}
