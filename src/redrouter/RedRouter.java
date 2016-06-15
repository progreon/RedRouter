/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package redrouter;

import redrouter.view.RouterFrame;

/**
 *
 * @author marco
 */
public class RedRouter {
    
    private final RouterFrame routerFrame;
//    private RouteFactory routeFactory;

    public RedRouter() {
//        routeFactory = new RouteFactory();
//        List<Pokemon> pokedex = routeFactory.getPokedexByID();
        
        routerFrame = new RouterFrame();
        routerFrame.setVisible(true);
        
//        System.out.println(routeFactory.getExaNidoRoute());
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
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
