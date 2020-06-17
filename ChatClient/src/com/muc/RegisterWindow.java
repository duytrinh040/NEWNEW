package com.muc;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class RegisterWindow extends JFrame {
    private ChatClient client;
    JTextField userName = new JTextField();
    JPasswordField passwordField_1 = new JPasswordField();
    JPasswordField passwordField_2 = new JPasswordField();
    JButton button1 = new JButton("Register");

    public RegisterWindow(ChatClient client){
        super("Register");
        this.client = client;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel p = new JPanel();
        p.setPreferredSize(new Dimension(400,100));
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.add(userName);
        p.add(passwordField_1);
        p.add(passwordField_2);
        p.add(button1);
        p.setAlignmentX(Component.CENTER_ALIGNMENT);
        getContentPane().add(p, BorderLayout.CENTER);
        pack();
        setVisible(true);

        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doRegister();
                setVisible(false);
            }
        });

    }

    private void doRegister() {
        String username = userName.getText();
        String password = passwordField_1.getText();
        String re_password = passwordField_2.getText();

        if(password.equalsIgnoreCase(re_password)){
            try {
                if(client.register(username, password, re_password)){
                    LoginWindow loginWindow = new LoginWindow(client);
                }
                else{
                    JOptionPane.showMessageDialog(this, "Somehow this Fail! Life is tough!");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            JOptionPane.showMessageDialog(this, "Passwords do not match. Please re-enter!");
        }

    }

    //public static void main(String[] args) {
    //    RegisterWindow registerWindow = new RegisterWindow();
    //}
}
