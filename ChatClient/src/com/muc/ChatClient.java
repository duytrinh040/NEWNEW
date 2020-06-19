package com.muc;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

public class ChatClient {
    private final String serverName;
    private final int serverPort;
    private Socket socket;
    private OutputStream serverOut;
    private InputStream serverIn;
    private String host;
    private BufferedReader bufferredIn;


    public ArrayList<UserStatusListener> userStatusListeners = new ArrayList<>();
    public ArrayList<MessageListener> messageListeners = new ArrayList<>();



    public ChatClient(String serverName, int serverPort) {
        this.serverPort = serverPort;
        this.serverName = serverName;
    }

    public static void main(String[] args) throws IOException {
        ChatClient client = new ChatClient("192.168.0.190", 8818);
        client.addUserStatusListener(new UserStatusListener() {
            @Override
            public void online(String userName) {
                System.out.println("ONLINE " + userName);
            }

            @Override
            public void offline(String userName) {
                System.out.println("OFFLINE " + userName);
            }
        });
        client.addMessageListener(new MessageListener() {
            @Override
            public void onMessage(String fromLogin, String msgBody) {
                System.out.println("You got message from " + fromLogin + " => " + msgBody);
            }
        });
        if (!client.connect()) {
            System.err.println("Connect Failed\n");
        } else {
            System.out.println("Connect Successful\n");
            if (client.login("admin", "admin")) {
                System.out.println("Login successful");
                client.msg("duck","Hello!!!");
            } else {
                System.err.println("Login failed");
            }
           // client.logoff();
        }

    }

    public void msg(String sendTo, String body) throws IOException {
        String msg = "msg" + " " + sendTo + " " + body + "\n";
        serverOut.write(msg.getBytes());

    }

    public boolean login(String username, String password) throws IOException {
        String cmd = "login" + " " + username + " " + password + "\n";
        serverOut.write(cmd.getBytes());
        String response = bufferredIn.readLine();
        System.out.println("Response: " + response + "\n");
        if ("Log in succeed".equalsIgnoreCase(response)) {
            startMessageReader();
            System.out.println("client received: " + serverIn.read());
            return true;
        } else {
            return false;
        }
    }

    public boolean register(String username, String password, String re_password) throws IOException {
        String cmd = "register" + " " + username + " " + password + " " + re_password + "\n";
        serverOut.write(cmd.getBytes());
        String response = bufferredIn.readLine();
        System.out.println("Response: " + response + "\n");
        if("Register success".equalsIgnoreCase(response)){
            return true;
        }
        else{
            return false;
        }
    }

    public void logoff() throws IOException {
        String cmd = "logoff\n";
        serverOut.write(cmd.getBytes());

    }

    public void startMessageReader() {
        Thread t = new Thread(){
            @Override
            public void run() {
                readMessageLoop();
            }
        };
        t.start();
    }

    public void readMessageLoop(){
        try {
            String line;
            while((line = bufferredIn.readLine()) != null){
                String[] token = StringUtils.split(line);
                if(token != null && token.length > 0) {
                    String cmd = token[0];
                    if ("online".equalsIgnoreCase(cmd)){
                        handleOnline(token);
                    }
                    else if ("Offline".equalsIgnoreCase(cmd)){
                        handleOffline(token);
                    }
                    else if ("from".equalsIgnoreCase(cmd)){
                        String[] tokenMsg = StringUtils.split(line, null, 3);
                        handleMessage(tokenMsg);
                    }
                }
            }
        } catch (Exception ex){
            ex.printStackTrace();
            try{
                socket.close();
            } catch (IOException e){
                e.printStackTrace();
            }

        }

    }

    private void handleMessage(String[] tokenMsg) {
        String userName = tokenMsg[1];
        String msgBody = tokenMsg[2];
        for(MessageListener listener : messageListeners){
            listener.onMessage(userName, msgBody);
        }
    }

    public void handleOffline(String[] token) {
        String username = token[1];
        for(UserStatusListener listener : userStatusListeners){
            listener.offline(username);
        }
    }

    public void handleOnline(String[] token) {
        String username = token[1];
        for(UserStatusListener listener : userStatusListeners){
            listener.online(username);
        }
    }

    public boolean connect() {
        try {
            this.socket = new Socket(serverName, serverPort);
            System.out.printf("Client port is " + socket.getLocalPort() + "\n");
            this.serverOut = socket.getOutputStream();
            this.serverIn = socket.getInputStream();
            this.bufferredIn = new BufferedReader(new InputStreamReader(serverIn));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void addUserStatusListener(UserStatusListener listener){
        userStatusListeners.add(listener);
    }

    public void removeUserStatusListener(UserStatusListener listener){
        userStatusListeners.remove(listener);
    }

    public void addMessageListener(MessageListener listener){
        messageListeners.add(listener);
    }

    public void removeMessageListener(MessageListener listener){
        messageListeners.remove(listener);
    }
}
