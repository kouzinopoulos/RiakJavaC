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

    Socket frontendPut = context.socket(ZMQ.PULL);
    frontendPut.bind("tcp://*:5558");

    Socket frontendGet = context.socket(ZMQ.ROUTER);
    frontendGet.bind("tcp://*:5559");

    Socket backendPut = context.socket(ZMQ.PUSH);
    backendPut.bind("tcp://*:5560");

    Socket backendGet = context.socket(ZMQ.DEALER);
    backendGet.bind("tcp://*:5561");

    Poller items = new Poller(3);
    items.register(frontendPut, Poller.POLLIN);
    items.register(frontendGet, Poller.POLLIN);
    items.register(backendGet, Poller.POLLIN);

    boolean more = false;
    byte[] message;

    int i = 0;

    // switch messages between sockets
    while (!Thread.currentThread().isInterrupted()) {
      items.poll();

      if (items.pollin(0)) {
        if (debug) {
            System.out.println("C++ message pollin on 0");
          }
        while (true) {
          message = frontendPut.recv(0);
          more = frontendPut.hasReceiveMore();

          if (security) {
            // Calculate sha1 of message
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(message);
          }

          if (debug) {
            System.out.println("Received PUT message " + i + " from the c++ client, forwarding to the riak-java-client");
          }

          backendPut.send(message, more ? ZMQ.SNDMORE : 0);
          if (!more) {
            break;
          }
        }
        i++;
      }
      if (items.pollin(1)) {
        if (debug) {
            System.out.println("C++ message pollin on 1");
          }
        while (true) {
          message = frontendGet.recv(0);
          more = frontendGet.hasReceiveMore();

          if (security) {
            // Calculate sha1 of message
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(message);
          }

          if (debug) {
            System.out.println("Received GET message " + i + " from the c++ client, forwarding to the riak-java-client");
          }

          backendGet.send(message, more ? ZMQ.SNDMORE : 0);
          if (!more) {
            break;
          }
        }
        i++;
      }
      if (items.pollin(2)) {
        if (debug) {
            System.out.println("Java message pollin on 2");
          }
        while (true) {
          message = backendGet.recv(0);
          more = backendGet.hasReceiveMore();

          if (debug) {
            System.out.println("Received message " + i + " from the riak-java-client, forwarding to the c++ client");
          }

          frontendGet.send(message, more ? ZMQ.SNDMORE : 0);
          if (!more) {
            break;
          }
        }
        i++;
      }
    }

    frontendPut.close();
    frontendGet.close();
    backendPut.close();
    backendGet.close();
    context.term();
  }
}
