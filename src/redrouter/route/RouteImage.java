/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package redrouter.route;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author marco
 */
public class RouteImage extends RouteEntryInfo {
    
    private String URI;

    public RouteImage(String title, String URI) {
        super(title);
        this.URI = URI;
    }
    
    public BufferedImage getImage() {
        try {
            return ImageIO.read(new File(URI));
        } catch (IOException ex) {
            Logger.getLogger(RouteImage.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public String toString() {
        return super.toString();
    }
    
}
