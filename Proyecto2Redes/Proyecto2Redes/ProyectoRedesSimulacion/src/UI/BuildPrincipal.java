import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.swing.*;

public class BuildPrincipal {
    private Principal principal;
    private JPanel centralPanel;
    private List<JPanel[]> conexiones;
    private int routerCount = 1;
    private int serverCount = 1;
    private int computerCount = 1;
    private Dimension routerCurrentSize = null;
    private Dimension computerCurrentSize = null;
    private Dimension serverCurrentSize = null;
    private JPanel firstSelectedPanel = null;
    private double zoomFactor = 1.0;
    private ReportesEnvioPaquetes reportesEnvioPaquetes = new ReportesEnvioPaquetes();

    public BuildPrincipal(Principal principal, JPanel centralPanel, List<JPanel[]> conexiones) {
        this.principal = principal;
        this.centralPanel = centralPanel;
        this.conexiones = conexiones;
        this.centralPanel.setLayout(null);
    }

    

    public JPanel build() {
        JPanel panelTotal = new JPanel(new BorderLayout());
        panelTotal.setBackground(new Color(245, 249, 255));
        panelTotal.setPreferredSize(new Dimension(0, 100));

        JLabel label = new JLabel("Componentes");
        label.setFont(label.getFont().deriveFont(16f));
        label.setForeground(Color.BLACK);
        label.setHorizontalAlignment(JLabel.LEFT); 
        label.setAlignmentX(Component.LEFT_ALIGNMENT); 

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        panel.setBackground(new Color(245, 249, 255));

        JPanel panelCentro = new JPanel();
        panelCentro.setBackground(new Color(245, 249, 255));
        panelCentro.setLayout(new FlowLayout(FlowLayout.LEFT, 450, 10)); 

        JLabel labelCaja = new JLabel("Inciar entrega de paquetes");
        labelCaja.setFont(label.getFont().deriveFont(16f));
        labelCaja.setForeground(Color.BLACK);
        labelCaja.setHorizontalAlignment(JLabel.CENTER); 
        labelCaja.setAlignmentX(Component.CENTER_ALIGNMENT); 

        String boxImagePath = "Proyecto2Redes\\ProyectoRedesSimulacion\\src\\Images\\caja.png"; 
        JButton boxButton = new JButton();
        boxButton.setIcon(new ImageIcon(new ImageIcon(boxImagePath).getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH)));
        boxButton.setToolTipText("Iniciar entrega de paquetes");
        boxButton.setPreferredSize(new Dimension(60, 60));
        boxButton.setFocusPainted(false);
        boxButton.setContentAreaFilled(false);
        boxButton.setBorderPainted(false);

        boxButton.addActionListener(e -> {
            if (conexiones == null || conexiones.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Debe existir al menos una conexión para iniciar la entrega de paquetes.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                Map<JPanel, Integer> paquetesPorPanel = new HashMap<>();

                // --- Selección múltiple para destinos directos (sin router) ---
                List<JPanel> destinosDirectos = new ArrayList<>();
                List<String> nombresDestinos = new ArrayList<>();

                for (JPanel[] par : conexiones) {
                    JPanel servidor = null, destino = null;
                    boolean routerIntermedio = false;

                    for (int i = 0; i < 2; i++) {
                        String nombre = "";
                        for (Component c : par[i].getComponents()) {
                            if (c instanceof JLabel) {
                                JLabel labell = (JLabel) c;
                                if (labell.getText() != null) {
                                    nombre = labell.getText().toLowerCase();
                                    break;
                                }
                            }
                        }
                        if (nombre.contains("servidor")) {
                            servidor = par[i];
                            JPanel posibleRouter = par[1 - i];
                            for (Component c : posibleRouter.getComponents()) {
                                if (c instanceof JLabel) {
                                    JLabel labell = (JLabel) c;
                                    if (labell.getText() != null && labell.getText().toLowerCase().contains("router")) {
                                        routerIntermedio = true;
                                        break;
                                    }
                                }
                            }
                            if (!routerIntermedio) {
                                destino = par[1 - i];
                            }
                            break;
                        }
                    }

                    if (servidor != null && !routerIntermedio && destino != null && !destinosDirectos.contains(destino)) {
                        String nombreDestino = "";
                        for (Component c : destino.getComponents()) {
                            if (c instanceof JLabel) {
                                JLabel labell = (JLabel) c;
                                if (labell.getText() != null && !labell.getText().isEmpty()) {
                                    nombreDestino = labell.getText();
                                    break;
                                }
                            }
                        }
                        destinosDirectos.add(destino);
                        nombresDestinos.add(nombreDestino);
                    }
                }

                if (!destinosDirectos.isEmpty()) {
                    String[] opciones = nombresDestinos.toArray(new String[0]);
                    JList<String> list = new JList<>(opciones);
                    list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                    int result = JOptionPane.showConfirmDialog(null, new JScrollPane(list), "Seleccione los destinos directos", JOptionPane.OK_CANCEL_OPTION);
                    if (result == JOptionPane.OK_OPTION) {
                        int[] seleccionados = list.getSelectedIndices();
                        if (seleccionados.length > 0) {
                            int cantidad = 1;
                            boolean valido = false;
                            while (!valido) {
                                String input = JOptionPane.showInputDialog(null, "¿Cuántos paquetes desea entregar a los destinos seleccionados?", "Entrega de paquetes", JOptionPane.QUESTION_MESSAGE);
                                if (input == null) break;
                                try {
                                    cantidad = Integer.parseInt(input);
                                    if (cantidad < 1) throw new NumberFormatException();
                                    valido = true;
                                } catch (NumberFormatException ex) {
                                    JOptionPane.showMessageDialog(null, "Ingrese un número entero mayor a 0.", "Error", JOptionPane.ERROR_MESSAGE);
                                }
                            }
                            if (valido) {
                                final long[] tiempoInicio = {System.currentTimeMillis()};
                                final long[] tiempoFin = {0};
                                final boolean[] resumenMostrado = {false};
                                final int cantidadFinal = cantidad;
                                final int totalEnvios = cantidadFinal * seleccionados.length;
                                final int[] enviados = {0};
                                Random rand = new Random();
                                final int x = 1;
                                final int paquetesPerdidos = (totalEnvios >= x) ? rand.nextInt(totalEnvios - x + 1) + x : 0;
                                final Set<Integer> perdidosSet = new HashSet<>();
                                while (perdidosSet.size() < paquetesPerdidos) {
                                    perdidosSet.add(rand.nextInt(totalEnvios));
                                }
                                for (int idx : seleccionados) {
                                    paquetesPorPanel.put(destinosDirectos.get(idx), cantidadFinal);
                                }
                                // --- Animación de paquetes desde servidor (directo) ---
                                for (int idx : seleccionados) {
                                    JPanel destino = destinosDirectos.get(idx);
                                    Point p1 = null, p2 = null;
                                    final JPanel[] servidorPanelHolder = {null};
                                    for (JPanel[] par : conexiones) {
                                        if ((par[0] == destino || par[1] == destino)) {
                                            for (int i = 0; i < 2; i++) {
                                                String nombre = "";
                                                for (Component c : par[i].getComponents()) {
                                                    if (c instanceof JLabel) {
                                                        JLabel labell = (JLabel) c;
                                                        if (labell.getText() != null) {
                                                            nombre = labell.getText().toLowerCase();
                                                            break;
                                                        }
                                                    }
                                                }
                                                if (nombre.contains("servidor")) {
                                                    servidorPanelHolder[0] = par[i];
                                                    break;
                                                }
                                            }
                                            if (servidorPanelHolder[0] != null) {
                                                p1 = servidorPanelHolder[0].getLocation();
                                                p2 = destino.getLocation();
                                                break;
                                            }
                                        }
                                    }
                                    int startX = p1.x + servidorPanelHolder[0].getWidth() / 2;
                                    int startY = p1.y + servidorPanelHolder[0].getHeight() / 2;
                                    int endX = p2.x + destino.getWidth() / 2;
                                    int endY = p2.y + destino.getHeight() / 2;

                                    Timer timer = new Timer(150, null);
                                    final int[] enviadosPorDestino = {0};
                                    timer.addActionListener(ev -> {
                                        int globalIndex = enviados[0];
                                        if (enviadosPorDestino[0] < cantidadFinal) {
                                            if (!perdidosSet.contains(globalIndex)) {
                                                Animacion paquete = new Animacion(startX, startY, endX, endY, centralPanel, new ImageIcon("src\\Images\\caja.png").getImage());
                                                centralPanel.add(paquete, 0);
                                                centralPanel.setComponentZOrder(paquete, 0);
                                                centralPanel.repaint();
                                            }
                                            enviadosPorDestino[0]++;
                                            enviados[0]++;
                                        } else {
                                            ((Timer)ev.getSource()).stop();
                                            // Mostrar resumen cuando todos los envíos hayan terminado
                                            if (enviados[0] == totalEnvios && !resumenMostrado[0]) {
                                                resumenMostrado[0] = true;
                                                tiempoFin[0] = System.currentTimeMillis();
                                                long tiempoEnvio = tiempoFin[0] - tiempoInicio[0];
                                                long tiempoRecepcion = tiempoEnvio;
                                                String tiempoEnvioStr = tiempoEnvio + " ms";
                                                String tiempoRecepcionStr = tiempoRecepcion + " ms";
                                                int paquetesRecibidos = totalEnvios - paquetesPerdidos;
                                                String latenciaStr = (paquetesRecibidos > 0) ? (tiempoEnvio / paquetesRecibidos) + " ms" : "0 ms";

                                                // Construir rutas para cada PC seleccionada
                                                String nombreServidor = "";
                                                for (Component c : servidorPanelHolder[0].getComponents()) {
                                                    if (c instanceof JLabel lbl && lbl.getText() != null && !lbl.getText().isEmpty()) {
                                                        nombreServidor = lbl.getText();
                                                        break;
                                                    }
                                                }
                                                StringBuilder rutas = new StringBuilder();
                                                for (int idxRuta : seleccionados) {
                                                    String nombrePC = "";
                                                    JPanel pcDestinoRuta = destinosDirectos.get(idxRuta);
                                                    for (Component c : pcDestinoRuta.getComponents()) {
                                                        if (c instanceof JLabel lbl && lbl.getText() != null && !lbl.getText().isEmpty()) {
                                                            nombrePC = lbl.getText();
                                                            break;
                                                        }
                                                    }
                                                    rutas.append(nombreServidor)
                                                         .append(" -> ")
                                                         .append(nombrePC)
                                                         .append("\n");
                                                }
                                                String ruta = rutas.toString().trim();

                                                reportesEnvioPaquetes.mostrarReportePaquetes(
                                                    totalEnvios,
                                                    paquetesRecibidos,
                                                    paquetesPerdidos,
                                                    tiempoEnvioStr,
                                                    tiempoRecepcionStr,
                                                    latenciaStr,
                                                    "512 bytes",
                                                    ruta
                                                );
                                            }
                                        }
                                    });
                                    timer.start();
                                }
                            }
                        }
                    }
                }

                // --- Animación de paquetes desde servidor (con router) ---
                Image cajaImg = new ImageIcon("Proyecto2Redes\\ProyectoRedesSimulacion\\src\\Images\\caja.png").getImage();

                for (JPanel[] par : conexiones) {
                    final JPanel[] servidorHolder = {null};
                    JPanel destino = null;
                    boolean routerIntermedio = false;
                    final JPanel[] routerPanel = {null};

                    for (int i = 0; i < 2; i++) {
                        String nombre = "";
                        for (Component c : par[i].getComponents()) {
                            if (c instanceof JLabel) {
                                JLabel labell = (JLabel) c;
                                if (labell.getText() != null) {
                                    nombre = labell.getText().toLowerCase();
                                    break;
                                }
                            }
                        }
                        if (nombre.contains("servidor")) {
                            servidorHolder[0] = par[i];
                            JPanel posibleRouter = par[1 - i];
                            for (Component c : posibleRouter.getComponents()) {
                                if (c instanceof JLabel) {
                                    JLabel labell = (JLabel) c;
                                    if (labell.getText() != null && labell.getText().toLowerCase().contains("router")) {
                                        routerIntermedio = true;
                                        routerPanel[0] = posibleRouter;
                                        break;
                                    }
                                }
                            }
                            if (!routerIntermedio) {
                                destino = par[1 - i];
                            }
                            break;
                        }
                    }
                    final JPanel servidor = servidorHolder[0];

                    if (servidor != null && routerIntermedio && routerPanel[0] != null) {
                        List<JPanel> pcsConectados = new ArrayList<>();
                        for (JPanel[] par2 : conexiones) {
                            if (par2[0] == routerPanel[0] || par2[1] == routerPanel[0]) {
                                JPanel otro = (par2[0] == routerPanel[0]) ? par2[1] : par2[0];
                                for (Component c : otro.getComponents()) {
                                    if (c instanceof JLabel) {
                                        JLabel labell = (JLabel) c;
                                        if (labell.getText() != null && labell.getText().toLowerCase().contains("pc")) {
                                            pcsConectados.add(otro);
                                        }
                                    }
                                }
                            }
                        }
                        if (!pcsConectados.isEmpty()) {
                            String[] opciones = new String[pcsConectados.size()];
                            for (int i = 0; i < pcsConectados.size(); i++) {
                                String nombre = "";
                                for (Component c : pcsConectados.get(i).getComponents()) {
                                    if (c instanceof JLabel) {
                                        JLabel labele = (JLabel) c;
                                        if (labele.getText() != null && !labele.getText().isEmpty()) {
                                            nombre = labele.getText();
                                            break;
                                        }
                                    }
                                }
                                opciones[i] = nombre;
                            }
                            JList<String> list = new JList<>(opciones);
                            list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                            int result = JOptionPane.showConfirmDialog(null, new JScrollPane(list), "Seleccione los PCs destino", JOptionPane.OK_CANCEL_OPTION);
                            if (result == JOptionPane.OK_OPTION) {
                                int[] seleccionados = list.getSelectedIndices();
                                if (seleccionados.length > 0) {
                                    int cantidad = 1;
                                    boolean valido = false;
                                    while (!valido) {
                                        String input = JOptionPane.showInputDialog(null, "¿Cuántos paquetes desea entregar a los PCs seleccionados?", "Entrega de paquetes", JOptionPane.QUESTION_MESSAGE);
                                        if (input == null) break;
                                        try {
                                            cantidad = Integer.parseInt(input);
                                            if (cantidad < 1) throw new NumberFormatException();
                                            valido = true;
                                        } catch (NumberFormatException ex) {
                                            JOptionPane.showMessageDialog(null, "Ingrese un número entero mayor a 0.", "Error", JOptionPane.ERROR_MESSAGE);
                                        }
                                    }
                                    if (valido) {
                                        Point p1 = servidor.getLocation();
                                        Point pRouter = routerPanel[0].getLocation();
                                        int startX = p1.x + servidor.getWidth() / 2;
                                        int startY = p1.y + servidor.getHeight() / 2;
                                        int endX = pRouter.x + routerPanel[0].getWidth() / 2;
                                        int endY = pRouter.y + routerPanel[0].getHeight() / 2;

                                        final int cantidadFinal = cantidad;
                                        final int totalEnvios = cantidadFinal * seleccionados.length;
                                        final int[] enviadosTotales = {0};
                                        Random rand = new Random();
                                        final int paquetesPerdidos = rand.nextInt(totalEnvios + 1);

                                        final Set<Integer> perdidosSet = new HashSet<>();
                                        while (perdidosSet.size() < paquetesPerdidos) {
                                            perdidosSet.add(rand.nextInt(totalEnvios));
                                        }

                                        final int[] globalIndex = {0};
                                        final long[] tiempoInicio = {0};
                                        final long[] tiempoFin = {0};
                                        final boolean[] resumenMostrado = {false};
                                        tiempoInicio[0] = System.currentTimeMillis();

                                        final int[] enviadosAlRouter = {0};
                                        Timer timerAlRouter = new Timer(200, null);
                                        timerAlRouter.addActionListener(ev -> {
                                            if (enviadosAlRouter[0] < cantidadFinal) {
                                                Animacion paquete = new Animacion(startX, startY, endX, endY, centralPanel, cajaImg);
                                                centralPanel.add(paquete, 0);
                                                centralPanel.setComponentZOrder(paquete, 0);
                                                centralPanel.repaint();
                                                enviadosAlRouter[0]++;
                                            } else {
                                                ((Timer)ev.getSource()).stop();
                                                for (int idx : seleccionados) {
                                                    JPanel pcDestino = pcsConectados.get(idx);
                                                    Point p2 = pcDestino.getLocation();
                                                    int endPcX = p2.x + pcDestino.getWidth() / 2;
                                                    int endPcY = p2.y + pcDestino.getHeight() / 2;

                                                    final int[] enviados = {0};
                                                    Timer timerAlPc = new Timer(200, null);
                                                    timerAlPc.addActionListener(ev2 -> {
                                                        if (enviados[0] < cantidadFinal) {
                                                            if (!perdidosSet.contains(globalIndex[0])) {
                                                                Animacion paquete = new Animacion(endX, endY, endPcX, endPcY, centralPanel, cajaImg);
                                                                centralPanel.add(paquete, 0);
                                                                centralPanel.setComponentZOrder(paquete, 0);
                                                                centralPanel.repaint();
                                                            }
                                                            enviados[0]++;
                                                            enviadosTotales[0]++;
                                                            globalIndex[0]++;
                                                        } else {
                                                            ((Timer)ev2.getSource()).stop();
                                                            if (enviadosTotales[0] == totalEnvios && !resumenMostrado[0]) {
                                                                resumenMostrado[0] = true;
                                                                tiempoFin[0] = System.currentTimeMillis();
                                                                long tiempoEnvio = tiempoFin[0] - tiempoInicio[0];
                                                                long tiempoRecepcion = tiempoEnvio;
                                                                String tiempoEnvioStr = tiempoEnvio + " ms";
                                                                String tiempoRecepcionStr = tiempoRecepcion + " ms";
                                                                int paquetesRecibidos = totalEnvios - paquetesPerdidos;
                                                                String latenciaStr = (paquetesRecibidos > 0) ? (tiempoEnvio / paquetesRecibidos) + " ms" : "0 ms";

                                                                // Construir rutas para cada PC seleccionada
                                                                String nombreServidor = "";
                                                                for (Component c : servidor.getComponents()) {
                                                                    if (c instanceof JLabel lbl && lbl.getText() != null && !lbl.getText().isEmpty()) {
                                                                        nombreServidor = lbl.getText();
                                                                        break;
                                                                    }
                                                                }
                                                                String nombreRouter = "";
                                                                for (Component c : routerPanel[0].getComponents()) {
                                                                    if (c instanceof JLabel lbl && lbl.getText() != null && !lbl.getText().isEmpty()) {
                                                                        nombreRouter = lbl.getText();
                                                                        break;
                                                                    }
                                                                }
                                                                StringBuilder rutas = new StringBuilder();
                                                                for (int idxRuta : seleccionados) {
                                                                    String nombrePC = "";
                                                                    JPanel pcDestinoRuta = pcsConectados.get(idxRuta);
                                                                    for (Component c : pcDestinoRuta.getComponents()) {
                                                                        if (c instanceof JLabel lbl && lbl.getText() != null && !lbl.getText().isEmpty()) {
                                                                            nombrePC = lbl.getText();
                                                                            break;
                                                                        }
                                                                    }
                                                                    rutas.append(nombreServidor)
                                                                         .append(" -> ")
                                                                         .append(nombreRouter)
                                                                         .append(" -> ")
                                                                         .append(nombrePC)
                                                                         .append("\n");
                                                                }
                                                                String ruta = rutas.toString().trim();

                                                                reportesEnvioPaquetes.mostrarReportePaquetes(
                                                                    totalEnvios,
                                                                    paquetesRecibidos,
                                                                    paquetesPerdidos,
                                                                    tiempoEnvioStr,
                                                                    tiempoRecepcionStr,
                                                                    latenciaStr,
                                                                    "512 bytes",
                                                                    ruta
                                                                );
                                                            }
                                                        }
                                                    });
                                                    timerAlPc.start();
                                                }
                                            }
                                        });
                                        timerAlRouter.start();
                                    }
                                }
                            }
                        }
                    } else if (servidor != null && destino != null && paquetesPorPanel.containsKey(destino)) {
                        int cantidad = paquetesPorPanel.get(destino);
                        Point p1 = servidor.getLocation();
                        Point p2 = destino.getLocation();
                        int startX = p1.x + servidor.getWidth() / 2;
                        int startY = p1.y + servidor.getHeight() / 2;
                        int endX = p2.x + destino.getWidth() / 2;
                        int endY = p2.y + destino.getHeight() / 2;

                        final int[] enviados = {0};
                        Timer timer = new Timer(150, null);
                        timer.addActionListener(ev -> {
                            if (enviados[0] < cantidad) {
                                Animacion paquete = new Animacion(startX, startY, endX, endY, centralPanel, cajaImg);
                                centralPanel.add(paquete, 0);
                                centralPanel.setComponentZOrder(paquete, 0);
                                centralPanel.repaint();
                                enviados[0]++;
                            } else {
                                timer.stop();
                            }
                        });
                        timer.start();
                    }
                }
            }
        });

        panelCentro.add(labelCaja);
        panelCentro.add(boxButton);

        JButton routerButton = MenuInferior.createRouterButton();
        JButton clientButton = MenuInferior.createClientButton();
        JButton serverButton = MenuInferior.createServerButton();

        // ---- SERVER ----
        serverButton.addActionListener(e -> {
            int cantidad = 1;
            boolean valido = false;
            while (!valido) {
                String input = JOptionPane.showInputDialog(null, "¿Cuántos servidores desea agregar?", "Agregar Servidores", JOptionPane.QUESTION_MESSAGE);
                if (input == null) return; 
                try {
                    cantidad = Integer.parseInt(input);
                    if (cantidad < 1) throw new NumberFormatException();
                    valido = true;
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Ingrese un número entero mayor a 0.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            for (int i = 0; i < cantidad; i++) {
                String imagePath = "Proyecto2Redes\\ProyectoRedesSimulacion\\src\\Images\\server.png";
                int baseW = 60, baseH = 60;
                if (serverCurrentSize != null) {
                    baseW = serverCurrentSize.width;
                    baseH = serverCurrentSize.height;
                }
                int baseX = 20 * serverCount, baseY = 20 * serverCount;

                ImageIcon originalIcon = new ImageIcon(imagePath);
                System.out.println("Icon width: " + originalIcon.getIconWidth());

                JPanel serverPanel = new JPanel();
                serverPanel.setLayout(new BoxLayout(serverPanel, BoxLayout.Y_AXIS));
                serverPanel.setOpaque(false);

                JLabel serverLabel = new JLabel(new ImageIcon(originalIcon.getImage().getScaledInstance((int)(baseW * zoomFactor), (int)(baseH * zoomFactor), Image.SCALE_SMOOTH)));
                serverLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);

                JLabel nameLabel = new JLabel("Servidor " + serverCount);
                nameLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
                nameLabel.setForeground(Color.BLACK);
                float baseFontSize = 10f;
                nameLabel.setFont(nameLabel.getFont().deriveFont(baseFontSize * (float)zoomFactor));

                serverPanel.add(serverLabel);
                serverPanel.add(nameLabel);

                int textWidth = nameLabel.getPreferredSize().width;
                int panelWidth = Math.max((int)(baseW * zoomFactor), textWidth + 10);
                serverPanel.setBounds((int)(baseX * zoomFactor), (int)(baseY * zoomFactor), panelWidth, (int)(baseH * zoomFactor) + 20);

                serverPanel.putClientProperty("imagePath", imagePath);
                serverPanel.putClientProperty("baseW", baseW);
                serverPanel.putClientProperty("baseH", baseH);
                serverPanel.putClientProperty("baseX", baseX);
                serverPanel.putClientProperty("baseY", baseY);

                serverPanel.addMouseListener(new MouseAdapter() {
                    Point offset;
                    public void mousePressed(MouseEvent evt) {
                        offset = evt.getPoint();
                        centralPanel.revalidate();
                        centralPanel.repaint();
                        if (SwingUtilities.isRightMouseButton(evt)) {
                            JPopupMenu menu = new JPopupMenu();
                            JMenuItem eliminar = new JMenuItem("Eliminar");
                            JMenuItem conexion = new JMenuItem("Crear Conexión");
                            JMenuItem Econexion = new JMenuItem("Eliminar Conexión");

                            JMenu modificarDatos = new JMenu("Modificar Datos");
                            JMenuItem cambiarNombre = new JMenuItem("Cambiar nombre");
                            //JMenuItem cambiarLatencia = new JMenuItem("Cambiar latencia");

                            cambiarNombre.addActionListener(ae -> {
                                String nuevoNombre = JOptionPane.showInputDialog(null, "Nuevo nombre:", "Cambiar nombre", JOptionPane.QUESTION_MESSAGE);
                                if (nuevoNombre != null && !nuevoNombre.trim().isEmpty()) {
                                    int labelCount = 0;
                                    JLabel nameLabelRef = null;
                                    for (Component c : serverPanel.getComponents()) {
                                        if (c instanceof JLabel label) {
                                            labelCount++;
                                            if (labelCount == 2) { 
                                                label.setText(nuevoNombre);
                                                nameLabelRef = label;
                                                break;
                                            }
                                        }
                                    }
                                    if (nameLabelRef != null) {
                                        int textWidth2 = nameLabelRef.getPreferredSize().width;
                                        int panelWidth2 = Math.max(serverPanel.getWidth(), textWidth2 + 10);
                                        serverPanel.setSize(panelWidth2, serverPanel.getHeight());
                                        serverPanel.setPreferredSize(new Dimension(panelWidth2, serverPanel.getHeight()));
                                    }
                                    centralPanel.revalidate();
                                    centralPanel.repaint();
                                }
                            });

                            /*cambiarLatencia.addActionListener(ae -> {
                                String nuevaLatencia = JOptionPane.showInputDialog(null, "Nueva latencia (ms):", "Cambiar latencia", JOptionPane.QUESTION_MESSAGE);
                                if (nuevaLatencia != null && !nuevaLatencia.trim().isEmpty()) {
                                    try {
                                        int lat = Integer.parseInt(nuevaLatencia);
                                        serverPanel.putClientProperty("latencia", lat);
                                        JOptionPane.showMessageDialog(null, "Latencia cambiada a " + lat + " ms");
                                    } catch (NumberFormatException ex) {
                                        JOptionPane.showMessageDialog(null, "Valor no válido.", "Error", JOptionPane.ERROR_MESSAGE);
                                    }
                                }
                            });*/

                            modificarDatos.add(cambiarNombre);
                            //modificarDatos.add(cambiarLatencia);

                            Econexion.addActionListener(ae -> {
                                List<JPanel[]> conexionesDelPanel = new ArrayList<>();
                                for (JPanel[] par : conexiones) {
                                    if (par[0] == serverPanel || par[1] == serverPanel) {
                                        conexionesDelPanel.add(par);
                                    }
                                }
                                if (conexionesDelPanel.isEmpty()) {
                                    JOptionPane.showMessageDialog(null, "No hay conexiones para eliminar.", "Sin conexiones", JOptionPane.INFORMATION_MESSAGE);
                                    return;
                                }
                                String[] opciones = new String[conexionesDelPanel.size()];
                                for (int i = 0; i < conexionesDelPanel.size(); i++) {
                                    JPanel otro = (conexionesDelPanel.get(i)[0] == serverPanel) ? conexionesDelPanel.get(i)[1] : conexionesDelPanel.get(i)[0];
                                    String nombre = "";
                                    for (Component c : otro.getComponents()) {
                                        if (c instanceof JLabel label && label.getText() != null && !label.getText().isEmpty()) {
                                            nombre = label.getText();
                                            break;
                                        }
                                    }
                                    opciones[i] = "Conectado a: " + nombre;
                                }
                                String seleccion = (String) JOptionPane.showInputDialog(
                                    null,
                                    "Seleccione la conexión a eliminar:",
                                    "Eliminar Conexión",
                                    JOptionPane.QUESTION_MESSAGE,
                                    null,
                                    opciones,
                                    opciones[0]
                                );
                                if (seleccion != null) {
                                    for (int i = 0; i < opciones.length; i++) {
                                        if (opciones[i].equals(seleccion)) {
                                            conexiones.remove(conexionesDelPanel.get(i));
                                            centralPanel.repaint();
                                            break;
                                        }
                                    }
                                }
                            });
                            eliminar.addActionListener(ae -> {
                                conexiones.removeIf(par -> par[0] == serverPanel || par[1] == serverPanel);
                                centralPanel.remove(serverPanel);
                                centralPanel.revalidate();
                                centralPanel.repaint();
                            });
                            conexion.addActionListener(ae -> {
                                firstSelectedPanel = serverPanel;
                            });
                            menu.add(eliminar);
                            menu.add(conexion);
                            menu.add(Econexion);
                            menu.add(modificarDatos);
                            menu.show(serverPanel, evt.getX(), evt.getY());
                        } else if (firstSelectedPanel != null && firstSelectedPanel != serverPanel) {
                            conexiones.add(new JPanel[]{firstSelectedPanel, serverPanel});
                            firstSelectedPanel = null;
                            centralPanel.repaint();
                        }
                    }
                    public void mouseReleased(MouseEvent evt) {
                        int logicX = (int)(serverPanel.getX() / zoomFactor);
                        int logicY = (int)(serverPanel.getY() / zoomFactor);
                        serverPanel.putClientProperty("baseX", logicX);
                        serverPanel.putClientProperty("baseY", logicY);
                        centralPanel.revalidate();
                        centralPanel.repaint();
                    }
                });
                serverPanel.addMouseMotionListener(new MouseMotionAdapter() {
                    public void mouseDragged(MouseEvent evt) {
                        int x = serverPanel.getX() + evt.getX() - serverPanel.getWidth() / 2;
                        int y = serverPanel.getY() + evt.getY() - serverPanel.getHeight() / 2;
                        serverPanel.setLocation(x, y);
                        centralPanel.revalidate();
                        centralPanel.repaint();
                    }
                });

                centralPanel.add(serverPanel);
                centralPanel.revalidate();
                centralPanel.repaint();

                if (serverCurrentSize == null) {
                    serverCurrentSize = new Dimension(baseW, baseH);
                }
                serverCount++;
            }
        });

        // ---- ROUTER ----
        routerButton.addActionListener(e -> {
            int cantidad = 1;
            boolean valido = false;
            while (!valido) {
                String input = JOptionPane.showInputDialog(null, "¿Cuántos routers desea agregar?", "Agregar Routers", JOptionPane.QUESTION_MESSAGE);
                if (input == null) return; 
                try {
                    cantidad = Integer.parseInt(input);
                    if (cantidad < 1) throw new NumberFormatException();
                    valido = true;
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Ingrese un número entero mayor a 0.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            for (int i = 0; i < cantidad; i++) {
                String imagePath = "Proyecto2Redes\\ProyectoRedesSimulacion\\src\\Images\\router.png";
                int baseW = 60, baseH = 60;
                if (routerCurrentSize != null) {
                    baseW = routerCurrentSize.width;
                    baseH = routerCurrentSize.height;
                }
                int baseX = 20 * routerCount, baseY = 20 * routerCount;

                ImageIcon originalIcon = new ImageIcon(imagePath);

                JPanel routerPanel = new JPanel();
                routerPanel.setLayout(new BoxLayout(routerPanel, BoxLayout.Y_AXIS));
                routerPanel.setOpaque(false);

                JLabel routerLabel = new JLabel(new ImageIcon(originalIcon.getImage().getScaledInstance((int)(baseW * zoomFactor), (int)(baseH * zoomFactor), Image.SCALE_SMOOTH)));
                routerLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);

                JLabel nameLabel = new JLabel("Router " + routerCount);
                nameLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
                nameLabel.setForeground(Color.BLACK);
                float baseFontSize = 10f;
                nameLabel.setFont(nameLabel.getFont().deriveFont(baseFontSize * (float)zoomFactor));

                routerPanel.add(routerLabel);
                routerPanel.add(nameLabel);

                int textWidth = nameLabel.getPreferredSize().width;
                int panelWidth = Math.max((int)(baseW * zoomFactor), textWidth + 10);
                routerPanel.setBounds((int)(baseX * zoomFactor), (int)(baseY * zoomFactor), panelWidth, (int)(baseH * zoomFactor) + 20);

                routerPanel.putClientProperty("imagePath", imagePath);
                routerPanel.putClientProperty("baseW", baseW);
                routerPanel.putClientProperty("baseH", baseH);
                routerPanel.putClientProperty("baseX", baseX);
                routerPanel.putClientProperty("baseY", baseY);

                routerPanel.addMouseListener(new MouseAdapter() {
                    Point offset;
                    public void mousePressed(MouseEvent evt) {
                        offset = evt.getPoint();
                        centralPanel.revalidate();
                        centralPanel.repaint();
                        if (SwingUtilities.isRightMouseButton(evt)) {
                            JPopupMenu menu = new JPopupMenu();
                            JMenuItem eliminar = new JMenuItem("Eliminar");
                            JMenuItem conexion = new JMenuItem("Crear Conexión");
                            JMenuItem Econexion = new JMenuItem("Eliminar Conexión");

                            JMenu modificarDatos = new JMenu("Modificar Datos");
                            JMenuItem cambiarNombre = new JMenuItem("Cambiar nombre");
                            //JMenuItem cambiarLatencia = new JMenuItem("Cambiar latencia");

                            cambiarNombre.addActionListener(ae -> {
                                String nuevoNombre = JOptionPane.showInputDialog(null, "Nuevo nombre:", "Cambiar nombre", JOptionPane.QUESTION_MESSAGE);
                                if (nuevoNombre != null && !nuevoNombre.trim().isEmpty()) {
                                    int labelCount = 0;
                                    JLabel nameLabelRef = null;
                                    for (Component c : routerPanel.getComponents()) {
                                        if (c instanceof JLabel label) {
                                            labelCount++;
                                            if (labelCount == 2) {
                                                label.setText(nuevoNombre);
                                                nameLabelRef = label;
                                                break;
                                            }
                                        }
                                    }
                                    if (nameLabelRef != null) {
                                        int textWidth2 = nameLabelRef.getPreferredSize().width;
                                        int panelWidth2 = Math.max(routerPanel.getWidth(), textWidth2 + 10);
                                        routerPanel.setSize(panelWidth2, routerPanel.getHeight());
                                        routerPanel.setPreferredSize(new Dimension(panelWidth2, routerPanel.getHeight()));
                                    }
                                    centralPanel.revalidate();
                                    centralPanel.repaint();
                                }
                            });

                            /*cambiarLatencia.addActionListener(ae -> {
                                String nuevaLatencia = JOptionPane.showInputDialog(null, "Nueva latencia (ms):", "Cambiar latencia", JOptionPane.QUESTION_MESSAGE);
                                if (nuevaLatencia != null && !nuevaLatencia.trim().isEmpty()) {
                                    try {
                                        int lat = Integer.parseInt(nuevaLatencia);
                                        routerPanel.putClientProperty("latencia", lat);
                                        JOptionPane.showMessageDialog(null, "Latencia cambiada a " + lat + " ms");
                                    } catch (NumberFormatException ex) {
                                        JOptionPane.showMessageDialog(null, "Valor no válido.", "Error", JOptionPane.ERROR_MESSAGE);
                                    }
                                }
                            });*/

                            modificarDatos.add(cambiarNombre);
                            //modificarDatos.add(cambiarLatencia);

                            Econexion.addActionListener(ae -> {
                                List<JPanel[]> conexionesDelPanel = new ArrayList<>();
                                for (JPanel[] par : conexiones) {
                                    if (par[0] == routerPanel || par[1] == routerPanel) {
                                        conexionesDelPanel.add(par);
                                    }
                                }
                                if (conexionesDelPanel.isEmpty()) {
                                    JOptionPane.showMessageDialog(null, "No hay conexiones para eliminar.", "Sin conexiones", JOptionPane.INFORMATION_MESSAGE);
                                    return;
                                }
                                String[] opciones = new String[conexionesDelPanel.size()];
                                for (int i = 0; i < conexionesDelPanel.size(); i++) {
                                    JPanel otro = (conexionesDelPanel.get(i)[0] == routerPanel) ? conexionesDelPanel.get(i)[1] : conexionesDelPanel.get(i)[0];
                                    String nombre = "";
                                    for (Component c : otro.getComponents()) {
                                        if (c instanceof JLabel label && label.getText() != null && !label.getText().isEmpty()) {
                                            nombre = label.getText();
                                            break;
                                        }
                                    }
                                    opciones[i] = "Conectado a: " + nombre;
                                }
                                String seleccion = (String) JOptionPane.showInputDialog(
                                    null,
                                    "Seleccione la conexión a eliminar:",
                                    "Eliminar Conexión",
                                    JOptionPane.QUESTION_MESSAGE,
                                    null,
                                    opciones,
                                    opciones[0]
                                );
                                if (seleccion != null) {
                                    for (int i = 0; i < opciones.length; i++) {
                                        if (opciones[i].equals(seleccion)) {
                                            conexiones.remove(conexionesDelPanel.get(i));
                                            centralPanel.repaint();
                                            break;
                                        }
                                    }
                                }
                            });
                            eliminar.addActionListener(ae -> {
                                conexiones.removeIf(par -> par[0] == routerPanel || par[1] == routerPanel);
                                centralPanel.remove(routerPanel);
                                centralPanel.revalidate();
                                centralPanel.repaint();
                            });
                            conexion.addActionListener(ae -> {
                                firstSelectedPanel = routerPanel;
                            });
                            menu.add(eliminar);
                            menu.add(conexion);
                            menu.add(Econexion);
                            menu.add(modificarDatos);
                            menu.show(routerPanel, evt.getX(), evt.getY());
                        } else if (firstSelectedPanel != null && firstSelectedPanel != routerPanel) {
                            conexiones.add(new JPanel[]{firstSelectedPanel, routerPanel});
                            firstSelectedPanel = null;
                            centralPanel.repaint();
                        }
                    }
                    public void mouseReleased(MouseEvent evt) {
                        int logicX = (int)(routerPanel.getX() / zoomFactor);
                        int logicY = (int)(routerPanel.getY() / zoomFactor);
                        routerPanel.putClientProperty("baseX", logicX);
                        routerPanel.putClientProperty("baseY", logicY);
                        centralPanel.revalidate();
                        centralPanel.repaint();
                    }
                });
                routerPanel.addMouseMotionListener(new MouseMotionAdapter() {
                    public void mouseDragged(MouseEvent evt) {
                        int x = routerPanel.getX() + evt.getX() - routerPanel.getWidth() / 2;
                        int y = routerPanel.getY() + evt.getY() - routerPanel.getHeight() / 2;
                        routerPanel.setLocation(x, y);
                        centralPanel.revalidate();
                        centralPanel.repaint();
                    }
                });

                centralPanel.add(routerPanel);
                centralPanel.revalidate();
                centralPanel.repaint();

                if (routerCurrentSize == null) {
                    routerCurrentSize = new Dimension(baseW, baseH);
                }
                routerCount++;
            }
        });

        // ---- CLIENT ----
        clientButton.addActionListener(e -> {
            int cantidad = 1;
            boolean valido = false;
            while (!valido) {
                String input = JOptionPane.showInputDialog(null, "¿Cuántos clientes desea agregar?", "Agregar Clientes", JOptionPane.QUESTION_MESSAGE);
                if (input == null) return;
                try {
                    cantidad = Integer.parseInt(input);
                    if (cantidad < 1) throw new NumberFormatException();
                    valido = true;
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Ingrese un número entero mayor a 0.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

            // --- Buscar el primer router existente ---
            JPanel routerPanel = null;
            for (Component comp : centralPanel.getComponents()) {
                if (comp instanceof JPanel innerPanel) {
                    for (Component c : innerPanel.getComponents()) {
                        if (c instanceof JLabel lbl && lbl.getText() != null && lbl.getText().toLowerCase().contains("router")) {
                            routerPanel = innerPanel;
                            break;
                        }
                    }
                }
                if (routerPanel != null) break;
            }

            // Si hay router, calcular el centro
            int centerX = 400, centerY = 300;
            int baseW = 80, baseH = 80;
            if (computerCurrentSize != null) {
                baseW = computerCurrentSize.width;
                baseH = computerCurrentSize.height;
            }
            int minSeparation = (int)(Math.max(baseW, baseH) * zoomFactor) + 30; // separación mínima entre clientes

            if (routerPanel != null) {
                Point routerLoc = routerPanel.getLocation();
                int routerW = routerPanel.getWidth();
                int routerH = routerPanel.getHeight();
                centerX = routerLoc.x + routerW / 2;
                centerY = routerLoc.y + routerH / 2;
            }

            // --- Calcular cantidad total de clientes (existentes + nuevos) ---
            int totalClientes = computerCount - 1 + cantidad;

            // --- Calcular cuántos clientes caben por aro ---
            int maxPorAro = 1;
            int radio = 120;
            java.util.List<Integer> clientesPorAro = new ArrayList<>();
            int clientesRestantes = cantidad;
            int clientesYaExistentes = computerCount - 1;
            int clientesTotales = clientesYaExistentes + cantidad;
            int aro = 0;
            int clientesColocados = 0;

            // Calcular aros y distribución
            while (clientesColocados < cantidad) {
                int clientesEnEsteAro;
                if (aro == 0) {
                    // Primer aro: intentar poner hasta 8, si hay más, repartir en más aros
                    clientesEnEsteAro = Math.min(8, cantidad - clientesColocados);
                } else {
                    // Siguientes aros: más capacidad
                    clientesEnEsteAro = Math.min((int)Math.ceil(2 * Math.PI * (radio + aro * minSeparation) / minSeparation), cantidad - clientesColocados);
                }
                clientesPorAro.add(clientesEnEsteAro);
                clientesColocados += clientesEnEsteAro;
                aro++;
            }

            int clienteActual = 0;
            for (int aroIdx = 0, globalClienteIdx = 0; aroIdx < clientesPorAro.size(); aroIdx++) {
                int clientesEnEsteAro = clientesPorAro.get(aroIdx);
                double anguloInicial = Math.PI / 2; // Para que el primero quede arriba
                double anguloEntre = 2 * Math.PI / clientesEnEsteAro;
                int radioAro = radio + aroIdx * minSeparation;

                for (int i = 0; i < clientesEnEsteAro; i++, clienteActual++) {
                    // Solo crear los nuevos, no los ya existentes
                    if (clienteActual < (computerCount - 1)) continue;

                    double angle = anguloInicial + i * anguloEntre;
                    int baseX = (int) (centerX + radioAro * Math.cos(angle) - (baseW * zoomFactor) / 2);
                    int baseY = (int) (centerY - radioAro * Math.sin(angle) - (baseH * zoomFactor) / 2);

<<<<<<< HEAD
                    String imagePath = "Proyecto2Redes\\ProyectoRedesSimulacion\\src\\Images\\computer.png";
                    ImageIcon originalIcon = new ImageIcon(imagePath);
=======
                            JMenu modificarDatos = new JMenu("Modificar Datos");
                            JMenuItem cambiarNombre = new JMenuItem("Cambiar nombre");
                            //JMenuItem cambiarLatencia = new JMenuItem("Cambiar latencia");
>>>>>>> 3ab3cbe7fc3061717ac8fe8bc76d234309db66b2

                    JPanel clientPanel = new JPanel();
                    clientPanel.setLayout(new BoxLayout(clientPanel, BoxLayout.Y_AXIS));
                    clientPanel.setOpaque(false);

                    JLabel clientLabel = new JLabel(new ImageIcon(originalIcon.getImage().getScaledInstance((int)(baseW * zoomFactor), (int)(baseH * zoomFactor), Image.SCALE_SMOOTH)));
                    clientLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);

                    JLabel nameLabel = new JLabel("PC " + computerCount);
                    nameLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
                    nameLabel.setForeground(Color.BLACK);
                    float baseFontSize = 10f;
                    nameLabel.setFont(nameLabel.getFont().deriveFont(baseFontSize * (float)zoomFactor));

                    clientPanel.add(clientLabel);
                    clientPanel.add(nameLabel);

                    int textWidth = nameLabel.getPreferredSize().width;
                    int panelWidth = Math.max((int)(baseW * zoomFactor), textWidth + 10);
                    clientPanel.setBounds(baseX, baseY, panelWidth, (int)(baseH * zoomFactor) + 20);

                    clientPanel.putClientProperty("imagePath", imagePath);
                    clientPanel.putClientProperty("baseW", baseW);
                    clientPanel.putClientProperty("baseH", baseH);
                    clientPanel.putClientProperty("baseX", baseX);
                    clientPanel.putClientProperty("baseY", baseY);

                    // ...mouse listeners y lógica igual que antes...
                    clientPanel.addMouseListener(new MouseAdapter() {
                        Point offset;
                        public void mousePressed(MouseEvent evt) {
                            offset = evt.getPoint();
                            centralPanel.revalidate();
                            centralPanel.repaint();
                            if (SwingUtilities.isRightMouseButton(evt)) {
                                JPopupMenu menu = new JPopupMenu();
                                JMenuItem eliminar = new JMenuItem("Eliminar");
                                JMenuItem conexion = new JMenuItem("Crear Conexión");
                                JMenuItem Econexion = new JMenuItem("Eliminar Conexión");

                                JMenu modificarDatos = new JMenu("Modificar Datos");
                                JMenuItem cambiarNombre = new JMenuItem("Cambiar nombre");
                                JMenuItem cambiarLatencia = new JMenuItem("Cambiar latencia");

                                cambiarNombre.addActionListener(ae -> {
                                    String nuevoNombre = JOptionPane.showInputDialog(null, "Nuevo nombre:", "Cambiar nombre", JOptionPane.QUESTION_MESSAGE);
                                    if (nuevoNombre != null && !nuevoNombre.trim().isEmpty()) {
                                        int labelCount = 0;
                                        JLabel nameLabelRef = null;
                                        for (Component c : clientPanel.getComponents()) {
                                            if (c instanceof JLabel label) {
                                                labelCount++;
                                                if (labelCount == 2) {
                                                    label.setText(nuevoNombre);
                                                    nameLabelRef = label;
                                                    break;
                                                }
                                            }
                                        }
                                        if (nameLabelRef != null) {
                                            int textWidth2 = nameLabelRef.getPreferredSize().width;
                                            int panelWidth2 = Math.max(clientPanel.getWidth(), textWidth2 + 10);
                                            clientPanel.setSize(panelWidth2, clientPanel.getHeight());
                                            clientPanel.setPreferredSize(new Dimension(panelWidth2, clientPanel.getHeight()));
                                        }
                                        centralPanel.revalidate();
                                        centralPanel.repaint();
                                    }
                                });

                                cambiarLatencia.addActionListener(ae -> {
                                    String nuevaLatencia = JOptionPane.showInputDialog(null, "Nueva latencia (ms):", "Cambiar latencia", JOptionPane.QUESTION_MESSAGE);
                                    if (nuevaLatencia != null && !nuevaLatencia.trim().isEmpty()) {
                                        try {
                                            int lat = Integer.parseInt(nuevaLatencia);
                                            clientPanel.putClientProperty("latencia", lat);
                                            JOptionPane.showMessageDialog(null, "Latencia cambiada a " + lat + " ms");
                                        } catch (NumberFormatException ex) {
                                            JOptionPane.showMessageDialog(null, "Valor no válido.", "Error", JOptionPane.ERROR_MESSAGE);
                                        }
                                    }
                                });

                                modificarDatos.add(cambiarNombre);
                                modificarDatos.add(cambiarLatencia);

                                Econexion.addActionListener(ae -> {
                                    List<JPanel[]> conexionesDelPanel = new ArrayList<>();
                                    for (JPanel[] par : conexiones) {
                                        if (par[0] == clientPanel || par[1] == clientPanel) {
                                            conexionesDelPanel.add(par);
                                        }
                                    }
                                    if (conexionesDelPanel.isEmpty()) {
                                        JOptionPane.showMessageDialog(null, "No hay conexiones para eliminar.", "Sin conexiones", JOptionPane.INFORMATION_MESSAGE);
                                        return;
                                    }
                                    String[] opciones = new String[conexionesDelPanel.size()];
                                    for (int i = 0; i < conexionesDelPanel.size(); i++) {
                                        JPanel otro = (conexionesDelPanel.get(i)[0] == clientPanel) ? conexionesDelPanel.get(i)[1] : conexionesDelPanel.get(i)[0];
                                        String nombre = "";
                                        for (Component c : otro.getComponents()) {
                                            if (c instanceof JLabel label && label.getText() != null && !label.getText().isEmpty()) {
                                                nombre = label.getText();
                                                break;
                                            }
                                        }
                                        opciones[i] = "Conectado a: " + nombre;
                                    }
                                    String seleccion = (String) JOptionPane.showInputDialog(
                                        null,
                                        "Seleccione la conexión a eliminar:",
                                        "Eliminar Conexión",
                                        JOptionPane.QUESTION_MESSAGE,
                                        null,
                                        opciones,
                                        opciones[0]
                                    );
                                    if (seleccion != null) {
                                        for (int i = 0; i < opciones.length; i++) {
                                            if (opciones[i].equals(seleccion)) {
                                                conexiones.remove(conexionesDelPanel.get(i));
                                                centralPanel.repaint();
                                                break;
                                            }
                                        }
                                    }
                                });
                                eliminar.addActionListener(ae -> {
                                    conexiones.removeIf(par -> par[0] == clientPanel || par[1] == clientPanel);
                                    centralPanel.remove(clientPanel);
                                    centralPanel.revalidate();
                                    centralPanel.repaint();
<<<<<<< HEAD
                                });
                                conexion.addActionListener(ae -> {
                                    firstSelectedPanel = clientPanel;
                                });
                                menu.add(eliminar);
                                menu.add(conexion);
                                menu.add(Econexion);
                                menu.add(modificarDatos);
                                menu.show(clientPanel, evt.getX(), evt.getY());
                            } else if (firstSelectedPanel != null && firstSelectedPanel != clientPanel) {
                                conexiones.add(new JPanel[]{firstSelectedPanel, clientPanel});
                                firstSelectedPanel = null;
=======
                                }
                            });

                            /*cambiarLatencia.addActionListener(ae -> {
                                String nuevaLatencia = JOptionPane.showInputDialog(null, "Nueva latencia (ms):", "Cambiar latencia", JOptionPane.QUESTION_MESSAGE);
                                if (nuevaLatencia != null && !nuevaLatencia.trim().isEmpty()) {
                                    try {
                                        int lat = Integer.parseInt(nuevaLatencia);
                                        clientPanel.putClientProperty("latencia", lat);
                                        JOptionPane.showMessageDialog(null, "Latencia cambiada a " + lat + " ms");
                                    } catch (NumberFormatException ex) {
                                        JOptionPane.showMessageDialog(null, "Valor no válido.", "Error", JOptionPane.ERROR_MESSAGE);
                                    }
                                }
                            });*/

                            modificarDatos.add(cambiarNombre);
                            //modificarDatos.add(cambiarLatencia);

                            Econexion.addActionListener(ae -> {
                                List<JPanel[]> conexionesDelPanel = new ArrayList<>();
                                for (JPanel[] par : conexiones) {
                                    if (par[0] == clientPanel || par[1] == clientPanel) {
                                        conexionesDelPanel.add(par);
                                    }
                                }
                                if (conexionesDelPanel.isEmpty()) {
                                    JOptionPane.showMessageDialog(null, "No hay conexiones para eliminar.", "Sin conexiones", JOptionPane.INFORMATION_MESSAGE);
                                    return;
                                }
                                String[] opciones = new String[conexionesDelPanel.size()];
                                for (int i = 0; i < conexionesDelPanel.size(); i++) {
                                    JPanel otro = (conexionesDelPanel.get(i)[0] == clientPanel) ? conexionesDelPanel.get(i)[1] : conexionesDelPanel.get(i)[0];
                                    String nombre = "";
                                    for (Component c : otro.getComponents()) {
                                        if (c instanceof JLabel label && label.getText() != null && !label.getText().isEmpty()) {
                                            nombre = label.getText();
                                            break;
                                        }
                                    }
                                    opciones[i] = "Conectado a: " + nombre;
                                }
                                String seleccion = (String) JOptionPane.showInputDialog(
                                    null,
                                    "Seleccione la conexión a eliminar:",
                                    "Eliminar Conexión",
                                    JOptionPane.QUESTION_MESSAGE,
                                    null,
                                    opciones,
                                    opciones[0]
                                );
                                if (seleccion != null) {
                                    for (int i = 0; i < opciones.length; i++) {
                                        if (opciones[i].equals(seleccion)) {
                                            conexiones.remove(conexionesDelPanel.get(i));
                                            centralPanel.repaint();
                                            break;
                                        }
                                    }
                                }
                            });
                            eliminar.addActionListener(ae -> {
                                conexiones.removeIf(par -> par[0] == clientPanel || par[1] == clientPanel);
                                centralPanel.remove(clientPanel);
                                centralPanel.revalidate();
>>>>>>> 3ab3cbe7fc3061717ac8fe8bc76d234309db66b2
                                centralPanel.repaint();
                            }
                        }
                        public void mouseReleased(MouseEvent evt) {
                            int logicX = (int)(clientPanel.getX() / zoomFactor);
                            int logicY = (int)(clientPanel.getY() / zoomFactor);
                            clientPanel.putClientProperty("baseX", logicX);
                            clientPanel.putClientProperty("baseY", logicY);
                            centralPanel.revalidate();
                            centralPanel.repaint();
                        }
                    });
                    clientPanel.addMouseMotionListener(new MouseMotionAdapter() {
                        public void mouseDragged(MouseEvent evt) {
                            int x = clientPanel.getX() + evt.getX() - clientPanel.getWidth() / 2;
                            int y = clientPanel.getY() + evt.getY() - clientPanel.getHeight() / 2;
                            clientPanel.setLocation(x, y);
                            centralPanel.revalidate();
                            centralPanel.repaint();
                        }
                    });

                    centralPanel.add(clientPanel);
                    centralPanel.revalidate();
                    centralPanel.repaint();

                    if (computerCurrentSize == null) {
                        computerCurrentSize = new Dimension(baseW, baseH);
                    }
                    computerCount++;
                }
            }
        });

        panel.add(routerButton);
        panel.add(clientButton);
        panel.add(serverButton);

        JPanel panelNorte = new JPanel();
        panelNorte.setLayout(new BorderLayout());
        panelNorte.setBackground(new Color(245, 249, 255));
        panelNorte.add(label, BorderLayout.WEST);

        labelCaja.setHorizontalAlignment(SwingConstants.CENTER);
        panelNorte.add(labelCaja, BorderLayout.CENTER);

        panelTotal.add(panelNorte, BorderLayout.NORTH);
        panelTotal.add(panel, BorderLayout.WEST);     
        panelTotal.add(panelCentro, BorderLayout.CENTER); 

        return panelTotal;
    }
}

