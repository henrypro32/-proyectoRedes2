

import javax.swing.*;
import java.awt.*;

public class Animacion extends JComponent {
    private int x, y, destX, destY;
    private int pasos = 50;
    private int pasoActual = 0;
    private Timer timer;
    private Image cajaImg;

    public Animacion(int startX, int startY, int endX, int endY, JPanel parent, Image cajaImg) {
        this.x = startX;
        this.y = startY;
        this.destX = endX;
        this.destY = endY;
        this.cajaImg = cajaImg;
        setBounds(0, 0, parent.getWidth(), parent.getHeight());

        timer = new Timer(15, e -> {
            pasoActual++;
            x = startX + (destX - startX) * pasoActual / pasos;
            y = startY + (destY - startY) * pasoActual / pasos;
            repaint();
            if (pasoActual >= pasos) {
                timer.stop();
                parent.remove(this);
                parent.repaint();
            }
        });
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (cajaImg != null) {
            g.drawImage(cajaImg, x - 15, y - 15, 30, 30, null);
        } else {
            g.setColor(Color.ORANGE);
            g.fillRect(x - 10, y - 10, 20, 20);
        }
    }
}
