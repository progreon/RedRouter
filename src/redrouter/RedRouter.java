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
package redrouter;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import redrouter.view.RouterFrame;

/**
 *
 * @author Marco Willems
 */
public class RedRouter {

//    private RouteFactory routeFactory;

    public RedRouter() {
//        routeFactory = new RouteFactory();
//        List<Pokemon> pokedex = routeFactory.getPokedexByID();
        GameChooserDialog gcd = new GameChooserDialog(null);
        gcd.setVisible(true);
        if (gcd.settings != null) {
            RouterFrame routerFrame = new RouterFrame(gcd.settings);
            routerFrame.setVisible(true);
        }
//        System.out.println(routeFactory.getExaNidoRoute());
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        RedRouter r = new RedRouter();
    }

    private class GameChooserDialog extends JDialog {

        Settings settings;

        public GameChooserDialog(JFrame owner) {
            super(owner, "Choose your game", true);
            init();
//            pack();
            setSize(400, 300);
            setResizable(false);
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            setLocationRelativeTo(owner);
        }

        private void init() {
            JPanel buttonPanel = new JPanel(new GridLayout(0, 1));
            JButton btnRed = new JButton(Settings.GAME_RED);
            JButton btnBlue = new JButton(Settings.GAME_BLUE);
            JButton btnYellow = new JButton(Settings.GAME_YELLOW);
            btnRed.setBackground(new Color(200, 30, 20));
            btnBlue.setBackground(new Color(20, 30, 200));
            btnYellow.setBackground(new Color(210, 210, 20));
            btnRed.setForeground(new Color(240, 50, 40));
            btnBlue.setForeground(new Color(40, 100, 250));
            btnYellow.setForeground(new Color(240, 240, 50));
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
    }

}
