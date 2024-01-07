package com.tobyschwartz;


import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

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
    JToggleButton encryptionToggleButton, showPasswordButton;
    JScrollPane fileScrollPane;
    public File fileToEncrypt;
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
        panel.setBorder(BorderFactory.createEmptyBorder(100, 100, 80, 100));
        panel.setBounds(new Rectangle(300, 250));
        panel.setLayout(new BorderLayout());


        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        selectedFileLabel = new JLabel("No file selected");
        selectedFileLabel.setHorizontalAlignment(JLabel.CENTER);
        fileScrollPane = new JScrollPane(selectedFileLabel);
        fileScrollPane.createHorizontalScrollBar();
        fileScrollPane.setPreferredSize(new Dimension(200, 40));
        fileScrollPane.setTransferHandler(new FileTransferHandler());

        labelPanel.add(fileScrollPane);


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
        passwordPanel.add(passwordField);

        showPasswordButton = new JToggleButton("Show");
        showPasswordButton.addActionListener(e -> {
            if (showPasswordButton.isSelected()) {
                passwordField.setEchoChar((char) 0); //show password
                showPasswordButton.setText("Hide");
            } else {
                passwordField.setEchoChar('●');
                showPasswordButton.setText("Show");
            }
        });
        showPasswordButton.setPreferredSize(showPasswordButton.getPreferredSize());
        passwordPanel.add(showPasswordButton);

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
        closeFileButton.setVisible(false);
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
                fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
                int result = fileChooser.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    fileToEncrypt = fileChooser.getSelectedFile();
                    selectedFileLabel.setText(fileToEncrypt.getPath());
                    closeFileButton.setVisible(true);
                } else {
                    selectedFileLabel.setText("No file selected");
                }
            }
            case "Save File" -> {
                fileChooser = new JFileChooser(fileToEncrypt.getParentFile());
                int result = fileChooser.showSaveDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    if (!encryptionToggleButton.isSelected()) { //encryption toggle is on so we are encrypting data
                        try {
                            Encrypt.encryptFile(passwordField.getPassword(), fileToEncrypt, fileChooser.getSelectedFile());
                            new Notification("File saved successfully!");
                        } catch (NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException |
                                 InvalidKeyException | IOException | IllegalBlockSizeException | BadPaddingException |
                                 InvalidAlgorithmParameterException ex) {
                            throw new RuntimeException(ex);
                        }
                    } else {
                        try {
                            Encrypt.decryptFile(passwordField.getPassword(), fileToEncrypt, fileChooser.getSelectedFile());
                            new Notification("File saved successfully!");
                        } catch (NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException |
                                 InvalidKeyException | IOException | IllegalBlockSizeException | BadPaddingException |
                                 InvalidAlgorithmParameterException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                }
            }
            default -> throw new IllegalStateException("Unexpected value: " + command);
        }

    }
    private class FileTransferHandler extends TransferHandler {
        @Override
        public boolean canImport(TransferSupport support) {
            return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
        }

        @Override
        public boolean importData(TransferSupport support) {
            if (!canImport(support)) {
                return false;
            }

            Transferable transferable = support.getTransferable();

            try {
                List<File> files = (List<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor);
                if (!files.isEmpty()) {
                    File file = files.get(0); // Take the first file
                    fileToEncrypt = file;
                    selectedFileLabel.setText(fileToEncrypt.getPath());
                    closeFileButton.setVisible(true);
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        }
    }
}

class Notification extends JFrame {

    public Notification() {
        super("Notification");
        setSize(300, 100);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel label = new JLabel("This is a notification.");
        add(label);

        JButton button = new JButton("Close");
        button.addActionListener(e -> dispose());
        add(button);

        setLayout(new FlowLayout());

        setVisible(true);
    }
    public Notification(String text) {
        super("Notification");
        setSize(300, 100);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel label = new JLabel(text);
        add(label);

        JButton button = new JButton("Close");
        button.addActionListener(e -> dispose());
        add(button);

        setLayout(new FlowLayout());
        setLocationRelativeTo(null);

        setVisible(true);
    }
}


