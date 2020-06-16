package com.muc;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class serverStartPanel extends JFrame {
    JButton start = new JButton("Start Server");
    JLabel label = new JLabel("IP Address: ");
    JLabel IP = new JLabel("0.0.0.0");
    InetAddress inetAddress = InetAddress.getLocalHost();
    public int count;

    public serverStartPanel() throws UnknownHostException {
        super("Start Server");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel p = new JPanel();
        p.setPreferredSize(new Dimension(200,80));
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.add(start);
        p.add(label);
        p.add(IP);
        p.setAlignmentX(Component.CENTER_ALIGNMENT);
        getContentPane().add(p, BorderLayout.CENTER);
        pack();
        setVisible(true);

        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                count++;
                if(count == 1) {
                    doStart();
                    IP.setText(inetAddress.getHostAddress());
                    start.setText("Stop Server");
                }
                if(count == 2){
                    System.exit(1);
                }
            }
        });
    }

    private void doStart() {
        int port = 8818;
        Server server = new Server(port);
        server.start();
    }

    public static void main(String[] args) {
        try {
            serverStartPanel serverStartPanel = new serverStartPanel();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
