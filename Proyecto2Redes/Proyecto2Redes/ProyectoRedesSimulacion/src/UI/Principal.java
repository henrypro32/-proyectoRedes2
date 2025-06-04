import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class Principal extends JFrame {

    private static JPanel panelCentral;
    private JScrollPane scrollPaneCentral;
    private Point lastPoint;
    private double zoomFactor = 1.0;
    private List<JPanel[]> conexiones = new ArrayList<>();

   

    public void start() {
        setTitle("Simulador de Redes de Computadores");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 249, 255));
        setContentPane(mainPanel);

        panelCentral = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setStroke(new BasicStroke(2));
                g2.setColor(Color.BLUE);
                for (JPanel[] par : conexiones) {
                    Point p1 = getPanelCenter(par[0]);
                    Point p2 = getPanelCenter(par[1]);
                    g2.drawLine(p1.x, p1.y, p2.x, p2.y);
                }
            }

            private Point getPanelCenter(JPanel panel) {
                Point panelPos = SwingUtilities.convertPoint(panel.getParent(), panel.getLocation(), this);
                int x = panelPos.x + panel.getWidth() / 2;
                int y = panelPos.y + panel.getHeight() / 2;
                return new Point(x, y);
            }
        };
        panelCentral.setPreferredSize(new Dimension(2000, 1200));
        scrollPaneCentral = new JScrollPane(panelCentral);
        scrollPaneCentral.setPreferredSize(new Dimension(900, 600)); 

        JPanel toolbarPanel = new BuildMenuSuperior(this, conexiones, panelCentral).build();

        SwingUtilities.invokeLater(() -> {
            int centerX = panelCentral.getPreferredSize().width / 2 - scrollPaneCentral.getViewport().getWidth() / 2;
            int centerY = panelCentral.getPreferredSize().height / 2 - scrollPaneCentral.getViewport().getHeight() / 2;
            scrollPaneCentral.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            scrollPaneCentral.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        });

        panelCentral.addMouseWheelListener(new MouseWheelListener() {
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (e.getPreciseWheelRotation() < 0) {
                    zoomFactor *= 1.1; 
                } else {
                    zoomFactor /= 1.1; 
                }
                zoomFactor = Math.max(0.2, Math.min(zoomFactor, 5.0));
                for (Component comp : panelCentral.getComponents()) {
                    if (comp instanceof JPanel) {
                        JPanel panel = (JPanel) comp;
                        String imagePath = (String) panel.getClientProperty("imagePath");
                        Integer baseW = (Integer) panel.getClientProperty("baseW");
                        Integer baseH = (Integer) panel.getClientProperty("baseH");
                        Integer baseX = (Integer) panel.getClientProperty("baseX");
                        Integer baseY = (Integer) panel.getClientProperty("baseY");
                        if (imagePath != null && baseW != null && baseH != null && baseX != null && baseY != null) {
                            int newW = (int) (baseW * zoomFactor);
                            int newH = (int) (baseH * zoomFactor);
                            int newX = (int) (baseX * zoomFactor);
                            int newY = (int) (baseY * zoomFactor);
                            panel.setBounds(newX, newY, newW, newH + 20);

                            for (Component c : panel.getComponents()) {
                                if (c instanceof JLabel label) {
                                    if (label.getIcon() != null) {
                                        ImageIcon originalIcon = new ImageIcon(imagePath);
                                        Image scaledImage = originalIcon.getImage().getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
                                        label.setIcon(new ImageIcon(scaledImage));
                                    } else {
                                        float baseFontSize = 10f; 
                                        label.setFont(label.getFont().deriveFont(baseFontSize * (float)zoomFactor));
                                    }
                                }
                            }
                        }
                    }
                }
                panelCentral.revalidate();
                panelCentral.repaint();
            }
        });

        panelCentral.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                lastPoint = e.getPoint();
            }
        });
        panelCentral.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (lastPoint == null) return;
                Point current = e.getPoint();
                int dx = current.x - lastPoint.x;
                int dy = current.y - lastPoint.y;
                for (Component comp : panelCentral.getComponents()) {
                    Point loc = comp.getLocation();
                    comp.setLocation(loc.x + dx, loc.y + dy);
                }
                panelCentral.repaint();
                lastPoint = current;
            }
        });

        BuildPrincipal panelBulidThings = new BuildPrincipal(this, panelCentral, conexiones);

        mainPanel.add(toolbarPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPaneCentral, BorderLayout.CENTER); 
        mainPanel.add(panelBulidThings.build(), BorderLayout.SOUTH);

        setVisible(true);
    }

    public JPanel getPanelCentral() {
        return panelCentral;
    }

    public void setPanelCentral(JPanel panel) {
        this.panelCentral = panel;
    }

    public double getZoomFactor() {
        return zoomFactor;
    }
}
