/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package redrouter.view;

import javax.swing.JFrame;

/**
 *
 * @author marco
 */
public class RouterFrame extends JFrame {

    public static final String TITLE = "Red Router";

    public RouterFrame() {
        super(TITLE);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        setSize(new Dimension(400, 300));
        this.setContentPane(new DVCalculatorPanel(null));
        this.pack();
        this.setLocationRelativeTo(null);
//        this.setResizable(false);

    }

}
