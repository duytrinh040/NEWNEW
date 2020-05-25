package com.muc;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server extends Thread{
    private final int serverPort;
    private ArrayList<serverWorker> workerlist = new ArrayList<>();

    public Server(int serverPort) {
        this.serverPort = serverPort;

    }
    public List<serverWorker> getWorkerList(){
        return workerlist;
    }
    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(serverPort);
            while(true) {
                System.out.println("About to accept client connection ...");
                Socket clientSocket = serverSocket.accept();
                System.out.println("Accept connection from: " + clientSocket);
                serverWorker worker = new serverWorker(this, clientSocket);
                workerlist.add(worker);
                worker.start();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeWorker(serverWorker serverWorker) {
        workerlist.remove(serverWorker);
    }
}
