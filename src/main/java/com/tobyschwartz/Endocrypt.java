package com.tobyschwartz;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Endocrypt {
    public final static String version = "1.0.0 BETA";

    public static void main(String[] args ) {
        GUI mainGUI = new GUI();
    }
}

class GUI implements ActionListener {
    JFrame jFrame;
    JButton openFileButton, closeFileButton;
    public GUI() {
        initGUI();
    }

    public void initGUI() {
        jFrame = new JFrame("Endocrypt Version " + Endocrypt.version);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setSize(800, 500);
        jFrame.setResizable(false);
        jFrame.setVisible(true);

        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(100, 100, 80, 100));
        panel.setLayout(new GridLayout());

        openFileButton = new JButton("Open File");
        openFileButton.setBounds(jFrame.getWidth() / 2, jFrame.getHeight() / 2, 100, 150);
        openFileButton.addActionListener(this);
        panel.add(openFileButton);

        closeFileButton = new JButton("Save File");
        closeFileButton.setBounds(jFrame.getWidth() / 2, jFrame.getHeight() / 2, 100, 150);
        closeFileButton.addActionListener(this);
        //closeFileButton.setVisible(false);
        panel.add(closeFileButton);

        jFrame.add(panel, BorderLayout.CENTER);
        jFrame.pack();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        switch (command) {
            case "Open File":
                System.out.println("Opened!");
                break;
            case "Save File":
                System.out.println("Closed!");
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + command);
        }

    }
}
