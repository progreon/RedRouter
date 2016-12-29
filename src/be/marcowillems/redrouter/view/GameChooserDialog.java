/*
 * Copyright (C) 2016 Marco Willems
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package be.marcowillems.redrouter.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import be.marcowillems.redrouter.Settings;

/**
 *
 * @author Marco Willems
 */
public class GameChooserDialog extends JDialog {

    public Settings settings;

    public GameChooserDialog(JFrame owner) {
        super(owner, "Choose your game", true);
        init();
        setSize(400, 300);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(owner);
    }

    private void init() {
        Color redDefault = new Color(200, 30, 20);
        Color blueDefault = new Color(20, 30, 200);
        Color yellowDefault = new Color(210, 210, 20);
        Color redPressed = new Color(250, 130, 100);
        Color bluePressed = new Color(130, 170, 250);
        Color yellowPressed = new Color(250, 250, 120);
        JPanel buttonPanel = new JPanel(new GridLayout(0, 1));
        JButton btnRed = new MyButton(redDefault, redPressed, Settings.GAME_RED);
        JButton btnBlue = new MyButton(blueDefault, bluePressed, Settings.GAME_BLUE);
        JButton btnYellow = new MyButton(yellowDefault, yellowPressed, Settings.GAME_YELLOW);
        btnRed.setForeground(new Color(240, 50, 40));
        btnBlue.setForeground(new Color(40, 100, 250));
        btnYellow.setForeground(new Color(230, 230, 50));
        Font currFont = btnRed.getFont();
        Font newFont = new Font(currFont.getFontName(), currFont.getStyle(), 24);
        btnRed.setFont(newFont);
        btnBlue.setFont(newFont);
        btnYellow.setFont(newFont);
        btnRed.setFocusable(false);
        btnBlue.setFocusable(false);
        btnYellow.setFocusable(false);
        btnRed.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                selectGame(Settings.GAME_RED);
            }
        });
        btnBlue.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                selectGame(Settings.GAME_BLUE);
            }
        });
        btnYellow.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                selectGame(Settings.GAME_YELLOW);
            }
        });
        buttonPanel.add(btnRed);
        buttonPanel.add(btnBlue);
        buttonPanel.add(btnYellow);
        setContentPane(buttonPanel);
    }

    private void selectGame(String game) {
        this.settings = new Settings(game);
        this.setVisible(false);
    }

    private class MyButton extends JButton {

        private final Color defaultColor;
        private final Color pressedColor;

        public MyButton(Color defaultColor, Color pressedColor, String text) {
            super(text);
            super.setContentAreaFilled(false);
            this.defaultColor = defaultColor;
            this.pressedColor = pressedColor;
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (getModel().isPressed()) {
                g.setColor(pressedColor);
            } else {
                g.setColor(defaultColor);
            }
            g.fillRect(0, 0, getWidth(), getHeight());
            super.paintComponent(g); //To change body of generated methods, choose Tools | Templates.
        }
    }

}
