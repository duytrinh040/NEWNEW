package com.muc;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class MessagePane extends JPanel implements MessageListener {

    private final ChatClient client;
    private final String username;
    private DefaultListModel<String> listModel = new DefaultListModel<>();
    private JList<String> messageList = new JList<>(listModel);
    private JTextField inputField = new JTextField();


    public MessagePane(ChatClient client, String username) {
        this.client = client;
        this.username = username;

        client.addMessageListener(this);

        setLayout(new BorderLayout());
        add(new JScrollPane(messageList), BorderLayout.CENTER);
        add(inputField, BorderLayout.SOUTH);

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

    }

    @Override
    public void onMessage(String fromLogin, String msgBody) {
        if(username.equalsIgnoreCase(fromLogin)) {
            String line = fromLogin + " " + msgBody;
            listModel.addElement(line);
        }
    }
}
