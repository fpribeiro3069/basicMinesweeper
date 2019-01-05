package GUI;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Properties;

public class MainMenu extends JFrame {
    private JPanel pnlMenu;
    private JLabel lblWelcome, lblDimensions, lblNumMines;
    private JSpinner spnWidth, spnHeight, spnNumMines;
    private JButton btnStart, btnHighScores, btnStartEasy, btnStartMedium, btnStartHard;

    private ArrayList<Pontuation> pontuations;

    public MainMenu() {

        this.setTitle("Basic Minesweeper");
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setSize(300, 310);
        this.setResizable(false);

        try {
            pontuations = getPontsFromFile();
        } catch (IOException e) {
            System.out.println("Problem getting pontuations from file!");
            pontuations = new ArrayList<Pontuation>();
        }

        // region Component Initializations
        pnlMenu = new JPanel();
        lblWelcome = new JLabel("Welcome to MineSweeper!");
        lblDimensions = new JLabel("Width and Height:", SwingConstants.CENTER);
        spnWidth = new JSpinner(new SpinnerNumberModel(10, 7, 20, 1));
        JFormattedTextField txt = ((JSpinner.NumberEditor)spnWidth.getEditor()).getTextField();
        ((NumberFormatter) txt.getFormatter()).setAllowsInvalid(false);
        spnHeight = new JSpinner(new SpinnerNumberModel(10, 7, 20, 1));
        txt = ((JSpinner.NumberEditor) spnHeight.getEditor()).getTextField();
        ((NumberFormatter) txt.getFormatter()).setAllowsInvalid(false);
        lblNumMines = new JLabel("Number of mines:", SwingConstants.CENTER);
        spnNumMines = new JSpinner(new SpinnerNumberModel(10, 8, 50, 5));
        txt = ((JSpinner.NumberEditor)spnNumMines.getEditor()).getTextField();
        ((NumberFormatter) txt.getFormatter()).setAllowsInvalid(false);
        btnStart = new JButton("Start");
        btnHighScores = new JButton("High Scores");
        btnStartEasy = new JButton("Easy");
        btnStartMedium = new JButton("Medium");
        btnStartHard = new JButton("Hard");
        // endregion

        // region Preferred Sizes
        Dimension dimSpinner = new Dimension(140, 25);
        Dimension dimLabels = new Dimension(this.getSize().width - 20, 25);
        Dimension dimButtons = new Dimension((this.getSize().width / 3) - 5, 30);
        lblDimensions.setPreferredSize(dimLabels);
        lblNumMines.setPreferredSize(dimLabels);
        spnWidth.setPreferredSize(dimSpinner);
        spnHeight.setPreferredSize(dimSpinner);
        spnNumMines.setPreferredSize(dimSpinner);
        btnStart.setPreferredSize(new Dimension(this.getSize().width- 20, 30));
        btnHighScores.setPreferredSize(new Dimension(this.getSize().width- 20, 30));
        btnStartEasy.setPreferredSize(dimButtons);
        btnStartMedium.setPreferredSize(dimButtons);
        btnStartHard.setPreferredSize(dimButtons);
        // endregion

        // region Listeners
        ButtonListener buttonListener = new ButtonListener();
        btnStart.addActionListener(buttonListener);
        btnHighScores.addActionListener(buttonListener);
        btnStartEasy.addActionListener(buttonListener);
        btnStartMedium.addActionListener(buttonListener);
        btnStartHard.addActionListener(buttonListener);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                setPontsOnFile();
                e.getWindow().dispose();
                System.exit(0);
            }
        });
        // endregion

        // region Add to Panels
        pnlMenu.add(lblWelcome);
        pnlMenu.add(lblDimensions);
        pnlMenu.add(spnWidth);
        pnlMenu.add(spnHeight);
        pnlMenu.add(lblNumMines);
        pnlMenu.add(spnNumMines);
        pnlMenu.add(btnStart);
        pnlMenu.add(btnHighScores);
        // TODO: Find alternative
        pnlMenu.add(new Label("------------------------------------------------------------"));
        pnlMenu.add(btnStartEasy);
        pnlMenu.add(btnStartMedium);
        pnlMenu.add(btnStartHard);
        this.add(pnlMenu);
        // endregion
    }

    private class ButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton selected = (JButton) e.getSource();
            MainMenu menu = getSelf();
            int width = (int) spnWidth.getValue();
            int height = (int) spnHeight.getValue();
            int numMines = (int) spnNumMines.getValue();

            Game game;

            if(selected == btnStart) {
                if((width*height) > numMines)
                    game = new Game(menu, height, width, numMines);
                else {
                    JOptionPane.showMessageDialog(null,
                            "Number of mines can't exceed the number of places on the board!");
                    return;
                }
            }
            else if(selected == btnHighScores) {
                JOptionPane.showMessageDialog(null,
                        showTop10(), "HIGH SCORES", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            else if (selected == btnStartEasy) {
                game = new Game(menu, 8, 8, 10);
            }
            else if (selected == btnStartMedium) {
                game = new Game(menu, 16, 16, 40);
            }
            else if (selected == btnStartHard) {
                game = new Game(menu, 16, 30, 99);
            }
            else {
                game = null;
            }

            if(game != null) {
                getSelf().setVisible(false);
                game.setVisible(true);
            }
            else
                JOptionPane.showMessageDialog(null, "Something went wrong!", "Error",
                        JOptionPane.ERROR_MESSAGE);
        }
    }

    public void addCompletedTime(Pontuation pont) {
        this.pontuations.add(pont);
    }

    private String showTop10() {
        StringBuilder str = new StringBuilder();
        try {
            Collections.sort(pontuations, new Comparator<Pontuation>() {
                @Override
                public int compare(Pontuation o1, Pontuation o2) {
                    return Float.compare(o1.getTime(), o2.getTime());
                }
            });
        } catch (Exception e) {
            System.out.println("Problem sorting the pontuations list");
            JOptionPane.showMessageDialog(null, "Problem getting the High Scores!");
        }
        for(int i = 1; i <= 10; i++) {
            try {
                str.append(i + ": \t" + pontuations.get(i - 1) + "\n");
            } catch (IndexOutOfBoundsException e) {
                str.append(i + ": \t------------\n");
            }
        }
        return str.toString();
    }

    private ArrayList<Pontuation> getPontsFromFile() throws IOException {
        ArrayList<Pontuation> pontuations = null;

        File file = new File("minesweeper.data");
        if(file.createNewFile()) {
            pontuations = new ArrayList<Pontuation>();
            return pontuations;
        }

        // It already exists and let's load.
        try {
            FileInputStream fos = new FileInputStream(file);
            ObjectInputStream oos = new ObjectInputStream(fos);

            pontuations = (ArrayList<Pontuation>) oos.readObject();
            Pontuation.setPLAYER_ID(oos.readLong());

            oos.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return pontuations;
    }

    private void setPontsOnFile() {
        File file = new File("minesweeper.data");

        try {
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(pontuations);
            oos.writeLong(Pontuation.getPLAYER_ID());
            oos.close();
        } catch (FileNotFoundException ex) {
            System.out.println("Error creating file.");
        } catch (IOException ex) {
            System.out.println("Error writing to file.");
        }
    }

    private MainMenu getSelf() {
        return this;
    }
}
