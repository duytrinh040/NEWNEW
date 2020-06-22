package com.muc;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class MessagePane extends JPanel implements MessageListener{

    private final ChatClient client;
    private final String username;
    private DefaultListModel<String> listModel = new DefaultListModel<>();
    private JList<String> messageList = new JList<>(listModel);
    private JTextField inputField = new JTextField();

    private JButton fileOpener = new JButton("Select file");


    public MessagePane(ChatClient client, String username) {
        this.client = client;
        this.username = username;
        client.addMessageListener(this);

        setLayout(new BorderLayout());
        add(new JScrollPane(messageList), BorderLayout.CENTER);
        add(inputField, BorderLayout.PAGE_END);

        add(fileOpener, BorderLayout.PAGE_START);




        inputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String text = inputField.getText();
                    client.msg(username, text);
                    listModel.addElement("You: " + text);
                    inputField.setText("");
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });

        fileOpener.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileDialog dialog = new FileDialog((Frame)null, "Select File to Open");
                dialog.setMode(FileDialog.LOAD);
                dialog.setVisible(true);
                try {
                    File filePath = new File(dialog.getDirectory().concat(dialog.getFile())); //get file path
                    System.out.println(filePath + " selected");
                    //byte[] content = Files.readAllBytes(filePath.toPath()); //to read filepath content, then send...

                } catch (NullPointerException a){
                    a.printStackTrace();
                }
            }
        });

    }

    @Override
    public void onMessage(String fromLogin, String msgBody) {
        if(!username.equalsIgnoreCase(fromLogin)) {
            String line = fromLogin + " " + msgBody;
            listModel.addElement(line);
        }
    }
}
