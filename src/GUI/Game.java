package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Date;
import java.util.Random;

public class Game extends JFrame {
    private JPanel pnlContainer;
    private JPanel pnlGame, pnlInfo;
    private JButton[][] btnBoard;
    private JLabel lblMinas;
    private JLabel lblTimer;
    private Timer timer;

    MainMenu owner;
    long startTime;
    int width, height, numMines, numMinesSelected = 0;
    boolean[][] gameBoard;
    ImageIcon mineIcon, flagIcon;

    public Game(MainMenu owner, int width, int height, int numMines) {
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.setTitle("Basic Mineseeper");
        this.setSize(750, 750);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.owner = owner;

        startTime = new Date().getTime();
        this.width = width;
        this.height = height;
        this.numMines = numMines;

        generateBoard(); // Create the buttons, and the array of booleans with mines already distributed
        pnlContainer = new JPanel(new BorderLayout());
        pnlGame = new JPanel(new GridLayout(width, height));
        pnlInfo = new JPanel();
        lblMinas = new JLabel("0/" + numMines + " Mines");
        // TODO: Time Elapsed ( Don't forget to add to panel)
        //lblTimer = new JLabel("Time Elapsed: ");


        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                e.getWindow().dispose();
                owner.setVisible(true);
            }

            @Override
            public void windowOpened(WindowEvent e) {
                super.windowOpened(e);
                // When the program has loaded the components, create the ImageIcons
                try {
                    mineIcon = new ImageIcon("images/mine.png");
                    flagIcon = new ImageIcon("images/flag.png");
                    Image img = mineIcon.getImage() ;
                    Image newimg = img.getScaledInstance( btnBoard[0][0].getWidth(), btnBoard[0][0].getHeight(),  java.awt.Image.SCALE_SMOOTH );
                    mineIcon = new ImageIcon( newimg );
                    img = flagIcon.getImage();
                    newimg = img.getScaledInstance( btnBoard[0][0].getWidth(), btnBoard[0][0].getHeight(),  java.awt.Image.SCALE_SMOOTH );
                    flagIcon = new ImageIcon(newimg);
                } catch (Exception ex) {
                    System.out.println("Problem with the icons");
                    mineIcon = new ImageIcon();
                    flagIcon = new ImageIcon();
                }
            }
        });

        for(JButton[] rowOfButtons : btnBoard) {
            for(JButton button: rowOfButtons) {
                button.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        super.mousePressed(e);
                        if(e.getButton() == MouseEvent.BUTTON1)
                            buttonClick((JButton) e.getSource());
                        else if (e.getButton() == MouseEvent.BUTTON3)
                            buttonRightClick((JButton) e.getSource());
                    }
                });

                button.setMargin(new Insets(0,0,0,0));
                button.setFont(new Font("Arial", Font.PLAIN, 15));
                pnlGame.add(button);
            }
        }

        pnlInfo.add(lblMinas);
        //pnlInfo.add(lblTimer);
        pnlContainer.add(pnlGame, BorderLayout.CENTER);
        pnlContainer.add(pnlInfo, BorderLayout.EAST);
        this.add(pnlContainer);
    }

    private void buttonRightClick(JButton button) {
        if (!button.isEnabled())
            return;

//        if(!button.getText().equals("F")) {
        if(button.getIcon() != flagIcon) {
            if(numMinesSelected == numMines)
                return;
            //button.setText("F");
            button.setIcon(flagIcon);
            lblMinas.setText(++numMinesSelected + "/" + numMines + " Mines");
            try {
                button.removeMouseListener(button.getMouseListeners()[1]);
            } catch (Exception e) { /* Continue */ }

            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    super.mousePressed(e);
                    if(e.getButton() == MouseEvent.BUTTON3)
                        buttonRightClick((JButton) e.getSource());
                }
            });
        }
        else {
            //button.setText("");
            button.setIcon(null);
            lblMinas.setText(--numMinesSelected + "/" + numMines + " Mines");
            try {
                button.removeMouseListener(button.getMouseListeners()[1]);
            } catch (Exception e) { /* Continue */ }

            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    super.mousePressed(e);
                    if(e.getButton() == MouseEvent.BUTTON1)
                        buttonClick((JButton) e.getSource());
                    else if (e.getButton() == MouseEvent.BUTTON3)
                        buttonRightClick((JButton) e.getSource());
                }
            });
        }

    }

    private void buttonClick(JButton source) {
        int wid, hei;

        // Parse the index
        String[] parsedStr = source.getName().substring(3).split("_");
        try {
            wid = Integer.parseInt(parsedStr[0]);
            hei = Integer.parseInt(parsedStr[1]);
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            System.out.println("********** Erro em buttonClick()");
            return;
        }

        if (!btnBoard[wid][hei].isEnabled())
            return;
        btnBoard[wid][hei].setEnabled(false);

        // Evaluate the output of the button clicked
        if(gameBoard[wid][hei] == false) {
            // Didn't hit a mine!
            int minesNear = minesNear(wid, hei);
            if(minesNear == 0) {
                // Open neighbour cells
                if (getButtonByIndex(wid - 1, hei + 1) != null)
                    buttonClick(getButtonByIndex(wid - 1, hei + 1));
                if(getButtonByIndex(wid, hei + 1) != null)
                    buttonClick(getButtonByIndex(wid, hei + 1));
                if(getButtonByIndex(wid + 1, hei + 1) != null)
                    buttonClick(getButtonByIndex(wid + 1, hei + 1));
                if(getButtonByIndex(wid - 1, hei) != null)
                    buttonClick(getButtonByIndex(wid - 1, hei));
                if(getButtonByIndex(wid + 1, hei) != null)
                    buttonClick(getButtonByIndex(wid + 1, hei));
                if(getButtonByIndex(wid - 1, hei - 1) != null)
                    buttonClick(getButtonByIndex(wid - 1, hei - 1));
                if(getButtonByIndex(wid, hei - 1) != null)
                    buttonClick(getButtonByIndex(wid, hei - 1));
                if(getButtonByIndex(wid + 1, hei - 1) != null)
                    buttonClick(getButtonByIndex(wid + 1, hei - 1));
            } else {
                btnBoard[wid][hei].setText(String.valueOf(minesNear(wid, hei)));
            }
            checkWinCondition();
        } else {
            // Ups! Hit a mine
            JOptionPane.showMessageDialog(null, "You hit a Mine! Game Over",
                    "Game Over", JOptionPane.WARNING_MESSAGE);
            disableAllButtons();
            showAllMines();
            source.setBackground(Color.red);
        }
    }

    private void checkWinCondition() {
        int totalButtons = width*height;
        int disabledButtons = 0;
        for (JButton[] row : btnBoard)
            for (JButton btn : row)
                if (!btn.isEnabled())
                    disabledButtons++;

        if (disabledButtons + numMines == totalButtons) {
            // User has won!
            long endTime = new Date().getTime();
            float timeElapsed = (float)(endTime - startTime) / 1000;

            System.out.println("It took " + timeElapsed + " seconds to finish the game!");

            String name = JOptionPane.showInputDialog("You WON! You made it in + " + timeElapsed + "seconds! What's your name?");
            Pontuation pontuation = new Pontuation(name, timeElapsed);
            owner.addCompletedTime(pontuation);

            disableAllButtons();
            showAllMines();
        }
    }

    private int minesNear(int wid, int hei) {
        int counter = 0;

        try {
            // TOP LEFT NEAR
            if (gameBoard[wid - 1][hei + 1])
                counter++;
        } catch (IndexOutOfBoundsException e) { /* Continue */ }

        try {
            // TOP NEAR
            if (gameBoard[wid][hei + 1])
                counter++;
        } catch (IndexOutOfBoundsException e) { /* Continue */ }

        try {
            // TOP RIGHT NEAR
            if (gameBoard[wid + 1][hei + 1])
                counter++;
        } catch (IndexOutOfBoundsException e) { /* Continue */ }

        try {
            // LEFT NEAR
            if (gameBoard[wid - 1][hei])
                counter++;
        } catch (IndexOutOfBoundsException e) { /* Continue */ }

        try {
            // CENTER NEAR
            if (gameBoard[wid][hei])
                counter++;
        } catch (IndexOutOfBoundsException e) { /* Continue */ }

        try {
            // RIGHT NEAR
            if (gameBoard[wid + 1][hei])
                counter++;
        } catch (IndexOutOfBoundsException e) { /* Continue */ }

        try {
            // BOTTOM LEFT NEAR
            if (gameBoard[wid - 1][hei - 1])
                counter++;
        } catch (IndexOutOfBoundsException e) { /* Continue */ }

        try {
            // BOTTOM NEAR
            if (gameBoard[wid][hei - 1])
                counter++;
        } catch (IndexOutOfBoundsException e) { /* Continue */ }

        try {
            // BOTTOM RIGHT NEAR
            if (gameBoard[wid + 1][hei - 1])
                counter++;
        } catch (IndexOutOfBoundsException e) { /* Continue */ }

        return counter;
    }

    private void showAllMines() {
        for (int i = 0; i < width; i++)
            for (int a = 0; a < height; a++) {
                if(gameBoard[i][a]) {
                    // TODO: Depois tentar meter imagem em vez de "M"
                    JButton button = getButtonByIndex(i, a);
                    if (button.getIcon() == flagIcon)
                        button.setBackground(Color.green);
                    else
                        button.setBackground(Color.yellow);
                    button.setIcon(mineIcon);
                }
            }
    }

    private JButton getButtonByIndex(int i, int a) {
        for(JButton[] row : btnBoard)
            for(JButton btn : row) {
                String[] parsedBtn = btn.getName().substring(3).split("_");
                try {
                    if (Integer.parseInt(parsedBtn[0]) == i &&
                            Integer.parseInt(parsedBtn[1]) == a)
                        return btn;
                } catch (NumberFormatException e) {
                    System.out.println("****** Erro em getButtonByIndex()");
                }
            }
        return null;
    }

    private void disableAllButtons() {
        for(JButton[] row : btnBoard)
            for(JButton btn : row)
                btn.setEnabled(false);
    }

    private void generateBoard() {
        int minesGiven = 0;
        gameBoard = new boolean[width][height];
        btnBoard = new JButton[width][height];

        // Setting ID's for the buttons
        for (int i = 0; i < width; i++)
            for (int a = 0; a < height; a++) {
                btnBoard[i][a] = new JButton();
                btnBoard[i][a].setName("btn" + i + "_" + a);
            }

        // Generating Mines...
        Random r = new Random();
        while (minesGiven != numMines) {
            int line = r.nextInt(width);
            int column = r.nextInt(height);

            if(!gameBoard[line][column]) {
                gameBoard[line][column] = true;
                minesGiven++;
            }
        }

        // Printing current btnBoard
        for (boolean[] line : gameBoard) {
            for (boolean place : line)
                System.out.print(place ? "-X-" : "-O-");
            System.out.println();
        }
    }
}
