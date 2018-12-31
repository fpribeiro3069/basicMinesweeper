package GUI;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainMenu extends JFrame {
    private JPanel pnlMenu;
    private JLabel lblWelcome, lblDimensions, lblNumMines;
    private JSpinner spnWidth, spnHeight, spnNumMines;
    private JButton btnStart, btnHighScores, btnStartEasy, btnStartMedium, btnStartHard;

    private ArrayList<Pontuacao> pontuations;

    public MainMenu() {
        this.setTitle("MineSweeper do Xico");
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setSize(300, 310);
        this.setResizable(false);

        try {
            pontuations = getPontsFromFile();
        } catch (IOException e) {
            System.out.println("Problem getting pontuations from file!");
        }

        // region Component Initializations
        pnlMenu = new JPanel();
        lblWelcome = new JLabel("Welcome to MineSweeper!");
        lblDimensions = new JLabel("Width and Height:", SwingConstants.CENTER);
        spnWidth = new JSpinner(new SpinnerNumberModel(10, 8, 30, 1));
        JFormattedTextField txt = ((JSpinner.NumberEditor)spnWidth.getEditor()).getTextField();
        ((NumberFormatter) txt.getFormatter()).setAllowsInvalid(false);
        spnHeight = new JSpinner(new SpinnerNumberModel(10, 8, 30, 1));
        txt = ((JSpinner.NumberEditor) spnHeight.getEditor()).getTextField();
        ((NumberFormatter) txt.getFormatter()).setAllowsInvalid(false);
        lblNumMines = new JLabel("Number of mines:", SwingConstants.CENTER);
        spnNumMines = new JSpinner(new SpinnerNumberModel(10, 10, 80, 5));
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
                    game = new Game(menu, width, height, numMines);
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
                game = new Game(menu, 8, 8, 15);
            }
            else if (selected == btnStartMedium) {
                game = new Game(menu, 12, 12, 60);
            }
            else if (selected == btnStartHard) {
                game = new Game(menu, 16, 16, 99);
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

    public void addCompletedTime(Pontuacao pont) {
        this.pontuations.add(pont);
    }

    private String showTop10() {
        // TODO: Mostrar nomes também
        StringBuilder str = new StringBuilder();
        try {
            Collections.sort(pontuations, new Comparator<Pontuacao>() {
                @Override
                public int compare(Pontuacao o1, Pontuacao o2) {
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

    private ArrayList<Pontuacao> getPontsFromFile() throws IOException {
        ArrayList<Pontuacao> pontuations = null;

        File file = new File("minesweeper.data");
        if(file.createNewFile()) {
            pontuations = new ArrayList<Pontuacao>();
            return pontuations;
        }

        // It already exists and let's load.
        try {
            FileInputStream fos = new FileInputStream(file);
            ObjectInputStream oos = new ObjectInputStream(fos);

            pontuations = (ArrayList<Pontuacao>) oos.readObject();

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
            oos.close();
        } catch (FileNotFoundException ex) {
            System.out.println("Erro a criar ficheiro.");
        } catch (IOException ex) {
            System.out.println("Erro a escrever para o ficheiro.");
        }
    }

    private MainMenu getSelf() {
        return this;
    }
}
