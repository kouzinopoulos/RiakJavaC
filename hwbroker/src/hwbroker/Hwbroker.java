package hwbroker;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Poller;
import org.zeromq.ZMQ.Socket;

/**
 *
 * code from ZeroMQ example implementation with some additions
 */
public
class Hwbroker {

  /**
   * @param args the command line arguments
   */
public
  static void main(String[] args) throws IOException, NoSuchAlgorithmException
  {

    boolean security = false;
    boolean debug = false;

    if (args.length > 0) {
      for (String s : args) {
        if (s.compareToIgnoreCase("secure") == 0) {
          security = true;
        }
        if (s.compareToIgnoreCase("debug") == 0) {
          debug = true;
        }
      }
    }

    if (security) {
      System.out.println("Security enabled");
    } else {
      System.out.println("Security not enabled");
    }
    if (debug) {
      System.out.println("Debug enabled");
    } else {
      System.out.println("Debug not enabled");
    }

    Context context = ZMQ.context(1);

    Socket frontend = context.socket(ZMQ.ROUTER);
    Socket backend = context.socket(ZMQ.DEALER);
    frontend.bind("tcp://*:5559");
    backend.bind("tcp://*:5560");

    System.out.println("Launch and connect broker");

    Poller items = new Poller(2);
    items.register(frontend, Poller.POLLIN);
    items.register(backend, Poller.POLLIN);

    boolean more = false;
    byte[] message;

    // switch messages between sockets
    while (!Thread.currentThread().isInterrupted()) {
      items.poll();

      if (items.pollin(0)) {
        while (true) {
          message = frontend.recv(0);
          more = frontend.hasReceiveMore();

          if (security) {
            // Calculate sha1 of message
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(message);
          }

          if (debug) {
            System.out.println("Received message from c++ client, forwarding to a riak-java-client");
          }

          backend.send(message, more ? ZMQ.SNDMORE : 0);
          if (!more) {
            break;
          }
        }
      }
      if (items.pollin(1)) {
        while (true) {
          message = backend.recv(0);
          more = backend.hasReceiveMore();

          if (debug) {
            System.out.println("Received message from a riak-java-client, forwarding to c++ client");
          }

          frontend.send(message, more ? ZMQ.SNDMORE : 0);
          if (!more) {
            break;
          }
        }
      }
    }

    frontend.close();
    backend.close();
    context.term();
  }
}
