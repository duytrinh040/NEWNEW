package com.muc;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class ServerMain {
    public static void main(String[] args)  {
        int port = 8818;
        Server server = new Server(port);
        server.start();

    }
}
