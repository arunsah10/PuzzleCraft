/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package puzzlecraft;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;  
import java.util.ArrayList;
import java.util.Collections;  // Import Collections
import java.util.Arrays;  // Import Arrays
import java.io.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import javax.swing.Timer;


public class PuzzleCraft extends JFrame {

    private int gridSize = 3;  // Default grid size (3x3)
    private BufferedImage image;
    private List<BufferedImage> tiles = new ArrayList<>();
    private JButton[] buttons;
    private int[] correctOrder;
    private int[] currentOrder;
    private Timer timer;
    private int timeElapsed;
    private JLabel timeLabel;
    private JButton loadImageButton;
    
    public PuzzleCraft() {
        setTitle("Puzzle Craft");
        setLayout(new BorderLayout());
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Timer label
        timeLabel = new JLabel("Time: 0s", SwingConstants.CENTER);
        add(timeLabel, BorderLayout.NORTH);
        
        // Load Image Button
        loadImageButton = new JButton("Load Image");
        loadImageButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadImage();
            }
        });
        
        add(loadImageButton, BorderLayout.SOUTH);
        
        // Center Panel for Puzzle
        JPanel puzzlePanel = new JPanel();
        puzzlePanel.setLayout(new GridLayout(gridSize, gridSize));
        add(puzzlePanel, BorderLayout.CENTER);
        
        buttons = new JButton[gridSize * gridSize];
        
        // Initialize the puzzle board with empty buttons
        for (int i = 0; i < buttons.length; i++) {
            buttons[i] = new JButton();
            buttons[i].setEnabled(false);
            puzzlePanel.add(buttons[i]);
        }
    }
    
    public void loadImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Images", "jpg", "png", "gif"));
        int result = fileChooser.showOpenDialog(this);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                image = ImageIO.read(file);
                tiles.clear();
                splitImage(image);
                shuffleTiles();
                startGame();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error loading image", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void splitImage(BufferedImage img) {
        int tileWidth = img.getWidth() / gridSize;
        int tileHeight = img.getHeight() / gridSize;
        
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                tiles.add(img.getSubimage(j * tileWidth, i * tileHeight, tileWidth, tileHeight));
            }
        }
    }
    
    private void shuffleTiles() {
        correctOrder = new int[tiles.size()];
        currentOrder = new int[tiles.size()];
        
        for (int i = 0; i < tiles.size(); i++) {
            correctOrder[i] = i;
            currentOrder[i] = i;
        }
        
        Collections.shuffle(Arrays.asList(currentOrder));
    }
    
    private void startGame() {
        // Initialize buttons with images
        for (int i = 0; i < buttons.length; i++) {
            if (i < currentOrder.length - 1) {
                buttons[i].setIcon(new ImageIcon(tiles.get(currentOrder[i])));
                buttons[i].setEnabled(true);
                int index = i;
                buttons[i].addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        moveTile(index);
                    }
                });
            } else {
                buttons[i].setIcon(null);
                buttons[i].setEnabled(false);
            }
        }

        // Start the timer
        timeElapsed = 0;
        timer = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                timeElapsed++;
                timeLabel.setText("Time: " + timeElapsed + "s");
            }
        });
        timer.start();
    }

    private void moveTile(int index) {
        int blankIndex = getBlankTileIndex();
        if (isAdjacent(index, blankIndex)) {
            swapTiles(index, blankIndex);
            checkForCompletion();
        }
    }

    private int getBlankTileIndex() {
        for (int i = 0; i < currentOrder.length; i++) {
            if (currentOrder[i] == currentOrder.length - 1) {
                return i;
            }
        }
        return -1;
    }

    private boolean isAdjacent(int index1, int index2) {
        int row1 = index1 / gridSize;
        int col1 = index1 % gridSize;
        int row2 = index2 / gridSize;
        int col2 = index2 % gridSize;

        return Math.abs(row1 - row2) + Math.abs(col1 - col2) == 1;
    }

   private void swapTiles(int index1, int index2) {
    // Swap values in the current order array
    int temp = currentOrder[index1];
    currentOrder[index1] = currentOrder[index2];
    currentOrder[index2] = temp;

    // Swap the icons between the two buttons
    Icon icon1 = buttons[index1].getIcon();
    Icon icon2 = buttons[index2].getIcon();

    buttons[index1].setIcon(icon2); // Assign blank space icon
    buttons[index2].setIcon(icon1); // Move image into blank space

    // Refresh UI for smooth effect
    buttons[index1].repaint();
    buttons[index2].repaint();
}



    private void updateButtons() {
        for (int i = 0; i < buttons.length; i++) {
            if (i < currentOrder.length - 1) {
                buttons[i].setIcon(new ImageIcon(tiles.get(currentOrder[i])));
            } else {
                buttons[i].setIcon(null);
            }
        }
    }

    private void checkForCompletion() {
        boolean isCompleted = true;
        for (int i = 0; i < currentOrder.length; i++) {
            if (currentOrder[i] != correctOrder[i]) {
                isCompleted = false;
                break;
            }
        }
        
        if (isCompleted) {
            timer.stop();
            JOptionPane.showMessageDialog(this, "Puzzle solved in " + timeElapsed + " seconds!");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new PuzzleCraft().setVisible(true);
            }
        });
    }
}
