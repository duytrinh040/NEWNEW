package com.muc;

import javax.swing.*;

public class RegisterWindow extends JFrame {
    JTextField userName = new JTextField();
    JPasswordField passwordField_1 = new JPasswordField();
    JPasswordField passwordField_2 = new JPasswordField();
    JButton button1 = new JButton("Register");

    public RegisterWindow(){
        super("Register");

    }

    public static void main(String[] args) {
        RegisterWindow registerWindow = new RegisterWindow();
    }
}
