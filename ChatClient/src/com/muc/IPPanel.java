package com.muc;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class IPPanel extends JFrame {
    JTextField IPField = new JTextField();
    private ChatClient client;

    JButton inputIP = new JButton("Confirm");

    public IPPanel(){
        super("Connect!");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel p = new JPanel();
        p.setPreferredSize(new Dimension(200,80));
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.add(IPField);
        p.add(inputIP);
        getContentPane().add(p, BorderLayout.CENTER);
        pack();
        setVisible(true);

        inputIP.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doConnect();
                setVisible(false);
            }
        });


    }

    private void doConnect() {
        String IP = IPField.getText();
        this.client = new ChatClient(IP, 8818);
        client.connect();
        if(client.connect()){
            ChoosingPanel choosingPanel = new ChoosingPanel(client);
        }
        else{
            JOptionPane.showMessageDialog(this, "Successfully Failed");
        }
    }

    public static void main(String[] args) {
        IPPanel ipPanel = new IPPanel();
    }
}
