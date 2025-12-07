package main.gui.components;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class ImagePanel extends JPanel {
    private Image backgroundImage;

    public ImagePanel(String path) {
        try{
            URL url = getClass().getResource(path);
            if(url != null){
                this.backgroundImage = new ImageIcon(url).getImage();
            }else{
                System.err.println("Image not found: " + path);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void paintComponent(Graphics g) {
        if(backgroundImage != null){
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }else{
            super.paintComponent(g);
        }
    }
}
