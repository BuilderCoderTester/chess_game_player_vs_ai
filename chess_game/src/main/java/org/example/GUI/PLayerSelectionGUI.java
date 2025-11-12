package org.example.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PLayerSelectionGUI extends JFrame {

    private JComboBox<String> whitePlayerDropdown;
    private JComboBox<String> blackPlayerDropdown;
    private JButton startButton;

    public PLayerSelectionGUI() {
        setTitle("Select Player Type");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(350, 220);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Main panel
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Dropdown options
        String[] playerOptions = {"Manual", "AI"};
        whitePlayerDropdown = new JComboBox<>(playerOptions);
        blackPlayerDropdown = new JComboBox<>(playerOptions);

        // Labels
        panel.add(new JLabel("White Player: "));
        panel.add(whitePlayerDropdown);

        panel.add(new JLabel("Black Player: "));
        panel.add(blackPlayerDropdown);

        // Start button
        startButton = new JButton("Start Game");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String whiteChoice = (String) whitePlayerDropdown.getSelectedItem();
                String blackChoice = (String) blackPlayerDropdown.getSelectedItem();

                System.out.println("White: " + whiteChoice + " | Black: " + blackChoice);

                // Example: Launch chess game with these options
                startChessGame(whiteChoice, blackChoice);
            }
        });

        // Add components
        add(panel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(startButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    // Stub for your main game launch logic
    private void startChessGame(String whiteType, String blackType) {
        JOptionPane.showMessageDialog(this,
                "Starting Game:\nWhite: " + whiteType + "\nBlack: " + blackType);
    }


}
