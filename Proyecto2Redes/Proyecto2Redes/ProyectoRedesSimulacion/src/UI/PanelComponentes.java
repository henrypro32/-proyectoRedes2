import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PanelComponentes extends JPanel {
    private final List<AuxComponentes> components = new ArrayList<>();
    private final Conexiones connectionManager = new Conexiones();

    public PanelComponentes() {
        setLayout(null);
        
        AuxComponentes router = new RouterComponente("Router 1", 100, 100);
        AuxComponentes client = new PcsComponente("Cliente 1", 300, 200);
        AuxComponentes server = new ServerComponente("Servidor 1", 500, 400);

        addComponent(router);
        addComponent(client);
        addComponent(server);

        connectionManager.addConnection(router, client);
        connectionManager.addConnection(router, server);
    }

    public void addComponent(AuxComponentes component) {
        components.add(component);
        this.add(component.getPanel());
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        connectionManager.drawConnections(g);
    }
}
