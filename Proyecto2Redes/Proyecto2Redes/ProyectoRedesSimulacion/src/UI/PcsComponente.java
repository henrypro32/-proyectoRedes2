import javax.swing.*;
import java.awt.*;

public class PcsComponente implements AuxComponentes {
    private final JPanel panel;
    private final String name;
    private final int x, y;

    public PcsComponente(String name, int x, int y) {
        this.name = name;
        this.x = x;
        this.y = y;
        panel = new JPanel();
        panel.setBounds(x, y, 80, 80);
        panel.setLayout(new BorderLayout());
        JLabel label = new JLabel(name, SwingConstants.CENTER);
        panel.add(label, BorderLayout.CENTER);
        panel.setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
    }

    @Override
    public JPanel getPanel() {
        return panel;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getX() {
        return x + 40;
    }

    @Override
    public int getY() {
        return y + 40;
    }
}
