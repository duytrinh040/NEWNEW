package com.muc;

import org.apache.commons.lang3.StringUtils;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class serverWorker extends Thread{
    private final Socket clientSocket;
    private final Server server;
    private String login = "placeHolder";
    private OutputStream outputStream;
    private InputStream inputStream;
    private HashSet<String> topicSet = new HashSet<>();
    private static  ArrayList<String> userList = new ArrayList<>();
    private static  ArrayList<String> passwordList = new ArrayList<>();
    private static  ArrayList<String> userListOnline = new ArrayList<>();



    public serverWorker(Server server, Socket clientSocket) {
        this.server = server;
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            /*passwordList.add("admin");
            userList.add("admin");
            passwordList.add("duck");
            userList.add("duck");*/
            handleClientSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClientSocket() throws IOException {
        this.inputStream = clientSocket.getInputStream();
        this.outputStream = clientSocket.getOutputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while((line=reader.readLine())!=null){
            String[] token = StringUtils.split(line);
            if(token != null && token.length > 0){
                String cmd = token[0];
                if("logoff".equals(cmd)||"quit".equalsIgnoreCase(cmd)){
                    logoffHandle();
                    break;
                }
                else if ("login".equalsIgnoreCase(cmd)){
                    handleLogin(outputStream, token);
                }
                else if("msg".equalsIgnoreCase(cmd)){
                    String[] tokenMsg = StringUtils.split(line, null, 3);
                    handleMessage(tokenMsg);
                }
                else if("join".equalsIgnoreCase(cmd)){
                    handleJoin(token);
                }
                else if("leave".equalsIgnoreCase(cmd)){
                    handleLeave(token);
                }
                else if("register".equalsIgnoreCase(cmd)){
                    handleRegister(token);
                }
                else if("print".equalsIgnoreCase(cmd)){
                    printInfo();
                }
                else if("attach".equalsIgnoreCase(cmd)){
                    handleFile(token);
                }
                else {
                    String msg = "Error command\n";
                    outputStream.write(msg.getBytes());
                }
            }
        }
       // clientSocket.close();
    }

    private void handleFile(String[] token) throws IOException {
        String user = token[1];
        String direction = token[2];
        File toSend = new File(direction);
        OutputStream os = clientSocket.getOutputStream();
        boolean isTopic = user.charAt(0) == '#';
        byte[] mybytearray = new byte[(int) toSend.length()];
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(toSend));
        bis.read(mybytearray, 0, mybytearray.length);

        List<serverWorker> workerList = server.getWorkerList();
        for(serverWorker worker : workerList){
            if(isTopic){
                if(worker.isMemberOfTopic(user)){
                    String outMsg = "from " + user + "\n";
                    os.write(mybytearray, 0, mybytearray.length);
                    worker.send(outMsg);
                    os.flush();
                }
            }
            if(user.equalsIgnoreCase(worker.getLogin())){
                String outMsg = "from " + login + "\n";
                os.write(mybytearray, 0, mybytearray.length);
                worker.send(outMsg);
                os.flush();
            }
        }

    }

    private void printInfo() throws IOException {
        System.out.println(userList + "\n");
        System.out.println(passwordList);
    }

    private void handleRegister(String[] token) {
        if(token.length == 4){
            String username = token[1];
            String password = token[2];
            String confirmPass = token[3];
            if(!isRegisted(username)) {
                if (password.equals(confirmPass)) {
                    userList.add(username);
                    passwordList.add(password);
                    String msgConfirm = "Register success" + "\n";
                    try {
                        outputStream.write(msgConfirm.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    String error = "Passwords do not match. Please retry\n";
                    try {
                        outputStream.write(error.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            else {
                String error = "Already a member\n";
                try {
                    outputStream.write(error.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean isRegisted(String username){
        return userList.contains(username);
    }

    private void handleLeave(String[] token) {
        if(token.length > 1){
            String topic = token[1];
            topicSet.remove(topic);
        }
    }

    public boolean isMemberOfTopic(String topic){
        return topicSet.contains(topic);
    }

    private void handleJoin(String[] token) {
        if(token.length > 1){
            String topic = token[1];
            topicSet.add(topic);
        }
    }



    private void handleMessage(String[] token) throws IOException {
        String sendTo = token[1];
        String body = token[2];

        boolean isTopic = sendTo.charAt(0) == '#';

        List<serverWorker> workerList = server.getWorkerList();
        for(serverWorker worker : workerList){
            if(isTopic){
                if(worker.isMemberOfTopic(sendTo)){
                    String outMsg = "from " + sendTo + " " + "by " + login + ":" + " " + body + "\n";
                    worker.send(outMsg);
                }
            }
            if(sendTo.equalsIgnoreCase(worker.getLogin())){
                String outMsg = "from " + login + ":" + " " + body + "\n";
                worker.send(outMsg);
            }
        }

    }

    private void logoffHandle() throws IOException {
        server.removeWorker(this);
        List<serverWorker> workerList = server.getWorkerList();
        String offlmsg = "Offline " + login + "\n";
        for(serverWorker worker : workerList){
            if(!login.equals(worker.getLogin()) && !login.contentEquals("placeHolder")) {
                worker.send(offlmsg);
            }
        }
        clientSocket.close();
    }

    public String getLogin() {
        return login;
    }

    private void handleLogin(OutputStream outputStream, String[] token) throws IOException {
        if(token.length == 3){
            String login = token[1];
            String password = token[2];
            String msg;
            if(isRegisted(login)){
                if(password.equals(passwordList.get(userList.indexOf(token[1])))) {
                    msg = "Log in succeed\n";
                    this.login = login;
                    outputStream.write(msg.getBytes());
                    System.out.println(login + " logged in");

                    userListOnline.add(login);
                    outputStream.write(userListOnline.toArray().toString().getBytes());
                    System.out.println(userListOnline.toArray().toString().getBytes());

                    List<serverWorker> workerList = server.getWorkerList();
                    for (serverWorker worker : workerList) {
                        if (worker.getLogin() != null) {
                            if (!login.equals(worker.getLogin()) && login.contentEquals("placeHolder")) {
                                String msg_1 = "online " + worker.getLogin() + "\n";
                                send(msg_1);



                            }
                        }
                    }
                    String onlmsg = "online " + login + "\n";
                    for (serverWorker worker : workerList) {
                        if (!login.equals(worker.getLogin()) && !login.contentEquals("placeHolder")) {
                            worker.send(onlmsg);
                        }
                    }
                }
                else{
                    msg = "Error\n";
                    outputStream.write(msg.getBytes());
                    System.err.println("Login failed: " + login);
                }
            }
            else{
                msg = "Error\n";
                outputStream.write(msg.getBytes());
            }
        }
    }

    private void send(String msg) throws IOException {
        if(login != null){
            outputStream.write(msg.getBytes());
        }
    }
}