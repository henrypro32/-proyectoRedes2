import java.awt.*;
import javax.swing.*;

public class BuildPanel {
    public JPanel build() {
        JPanel panel = new JPanel(null); 
        panel.setBackground(Color.WHITE); 
        panel.setPreferredSize(new Dimension(800, 500)); 
        return panel;
    }
}
