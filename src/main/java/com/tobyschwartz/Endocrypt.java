package com.tobyschwartz;


import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
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
    JLabel selectedFileLabel, passwordLabel;
    JButton openFileButton, closeFileButton;
    JFileChooser fileChooser;
    JPasswordField passwordField;
    JToggleButton encryptionToggleButton;
    public GUI() {
        initGUI();
    }

    public void initGUI() {
        jFrame = new JFrame("Endocrypt Version " + Endocrypt.version);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setLocationRelativeTo(null);
        jFrame.setSize(400, 400);
        jFrame.setResizable(false);
        jFrame.setVisible(true);

        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(100, 200, 80, 200));
        panel.setLayout(new BorderLayout());


        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        selectedFileLabel = new JLabel("No file selected");
        selectedFileLabel.setHorizontalAlignment(JLabel.CENTER);
        labelPanel.add(selectedFileLabel);


        encryptionToggleButton = new JToggleButton("Encrypt");
        encryptionToggleButton.addActionListener(e -> {
            // Toggle between Encrypt and Decrypt
            if (encryptionToggleButton.isSelected()) {
                encryptionToggleButton.setText("Decrypt");
            } else {
                encryptionToggleButton.setText("Encrypt");
            }
        });
        labelPanel.add(encryptionToggleButton);
        panel.add(labelPanel, BorderLayout.NORTH);

        JPanel passwordPanel = new JPanel();
        passwordPanel.setLayout(new FlowLayout());

        passwordLabel = new JLabel("Password:");
        passwordPanel.add(passwordLabel);
        passwordField = new JPasswordField(16);
        passwordField.setToolTipText("Encryption/Decryption Password");
        passwordField.setEnabled(true);
        //passwordField.setEchoChar((char) 0); // show password characters
        passwordPanel.add(passwordField);
        panel.add(passwordPanel, BorderLayout.CENTER);


        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        openFileButton = new JButton("Open File");
        openFileButton.setBounds(jFrame.getWidth() / 2, jFrame.getHeight() / 2, 100, 150);
        openFileButton.addActionListener(this);
        buttonPanel.add(openFileButton);

        closeFileButton = new JButton("Save File");
        closeFileButton.setBounds(jFrame.getWidth() / 2, jFrame.getHeight() / 2, 100, 150);
        closeFileButton.addActionListener(this);
        //closeFileButton.setVisible(false);
        buttonPanel.add(closeFileButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        jFrame.add(panel, BorderLayout.CENTER);
        jFrame.pack();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        switch (command) {
            case "Open File" -> {
                System.out.println("Opened!");
                fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
                int result = fileChooser.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    selectedFileLabel.setText(fileChooser.getSelectedFile().getPath());
                } else {
                    selectedFileLabel.setText("No file selected");
                }
            }
            case "Save File" -> System.out.println("Closed!");
            default -> throw new IllegalStateException("Unexpected value: " + command);
        }

    }
}
