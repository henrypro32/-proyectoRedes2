import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Conexiones {
    private final List<Connection> connections = new ArrayList<>();

    public void addConnection(AuxComponentes a, AuxComponentes b) {
        connections.add(new Connection(a, b));
    }

    public void drawConnections(Graphics g) {
        for (Connection c : connections) {
            int x1 = c.a.getX();
            int y1 = c.a.getY();
            int x2 = c.b.getX();
            int y2 = c.b.getY();
            g.setColor(Color.BLACK);
            g.drawLine(x1, y1, x2, y2);
        }
    }

    private static class Connection {
        AuxComponentes a, b;
        Connection(AuxComponentes a, AuxComponentes b) {
            this.a = a;
            this.b = b;
        }
    }
}
