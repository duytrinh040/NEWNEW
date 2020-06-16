package com.muc;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChoosingPanel extends JFrame{
    private final ChatClient client;
    JButton registerButton = new JButton("New User!");
    JButton loginButton = new JButton("Already a member!");

    public ChoosingPanel(ChatClient client){
        super("Welcome!");
        this.client = client;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel p = new JPanel();
        p.setPreferredSize(new Dimension(150,80));
        p.setAlignmentY(Component.CENTER_ALIGNMENT);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.add(registerButton);
        p.add(loginButton);
        getContentPane().add(p, BorderLayout.CENTER);
        pack();
        setVisible(true);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayLogin();
                setVisible(false);
            }
        });
    }

    private void displayLogin() {
        LoginWindow loginWindow = new LoginWindow(client);

    }

    //public static void main(String[] args) {
       // ChoosingPanel choosingPanel = new ChoosingPanel();
   // }
}
