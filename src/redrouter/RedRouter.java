/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package redrouter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import javax.swing.JFrame;
import redrouter.data.Pokemon;
import redrouter.data.RouteFactory;
import redrouter.view.RouterFrame;

/**
 *
 * @author marco
 */
public class RedRouter extends JFrame {
    
    private RouterFrame routerFrame;
    private RouteFactory routeFactory;

    public RedRouter() {
        routeFactory = new RouteFactory();
//        List<Pokemon> pokedex = routeFactory.getPokedexByID();
        
        routerFrame = new RouterFrame();
        routerFrame.setVisible(true);
        
        System.out.println(routeFactory.getExaNidoRoute());
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
//        InputStream fis = ClassLoader.getSystemResourceAsStream("test.txt");
//        BufferedReader br = new BufferedReader(new InputStreamReader(fis));
//        String line = br.readLine();
//        while (line != null) {
//            System.out.println(line);
//            line = br.readLine();
//        }
//        br.close();
//        fis.close();
        
        RedRouter r = new RedRouter();
    }
    
}
