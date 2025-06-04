import java.awt.*;
import javax.swing.*;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public class MenuSuperior {

    private static JButton createButton(String text, JPopupMenu menu) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        btn.setForeground(new Color(40, 40, 40));
        btn.setBackground(new Color(250, 250, 250));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(0, 11, 0, 11));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> menu.show(btn, 0, btn.getHeight()));
        return btn;
    }

    public static JButton createFileButton(List<JPanel[]> conexiones, JPanel centralPanel) {
        JPopupMenu menu = new JPopupMenu();

        JMenuItem Nuevo = new JMenuItem("Nuevo");
        menu.add(Nuevo);

        JMenuItem Abrir = new JMenuItem("Abrir");
        menu.add(Abrir);

        JMenu guardarMenu = new JMenu("Guardar como");
        JMenuItem guardarJSON = new JMenuItem("JSON");
        
        /*JMenuItem guardarPDF = new JMenuItem("PDF");
        guardarMenu.add(guardarPDF);
        guardarPDF.addActionListener(e -> {
        });*/
        
        guardarMenu.add(guardarJSON);
        menu.add(guardarMenu);

        guardarJSON.addActionListener(e -> {
            guardarJson(conexiones, centralPanel);
        });

        menu.addSeparator();

        JMenuItem salir = new JMenuItem("Salir");
        menu.add(salir);

        salir.addActionListener(e -> System.exit(0));
        Abrir.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                if (file.getName().toLowerCase().endsWith(".json")) {
                    importarJson(file, conexiones, centralPanel);
                } else {
                    JOptionPane.showMessageDialog(null, "Seleccione un archivo JSON válido.", "Archivo no válido", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        return createButton("File", menu);
    }

    public static void guardarJson(List<JPanel[]> conexiones, JPanel centralPanel) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar estructura como JSON");
        int userSelection = fileChooser.showSaveDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".json")) {
                filePath += ".json";
            }

            JSONArray componentesArray = new JSONArray();
            for (Component comp : centralPanel.getComponents()) {
                if (comp instanceof JPanel panel) {
                    JSONObject obj = new JSONObject();
                    obj.put("nombre", obtenerNombrePanel(panel));
                    obj.put("tipo", obtenerTipoPanel(panel));
                    obj.put("x", panel.getX());
                    obj.put("y", panel.getY());
                    obj.put("w", panel.getWidth());
                    obj.put("h", panel.getHeight());
                    obj.put("imagen", panel.getClientProperty("imagePath"));
                    componentesArray.put(obj);
                }
            }

            JSONArray conexionesArray = new JSONArray();
            for (JPanel[] par : conexiones) {
                JSONObject obj = new JSONObject();
                obj.put("componente1", obtenerNombrePanel(par[0]));
                obj.put("componente2", obtenerNombrePanel(par[1]));
                conexionesArray.put(obj);
            }

            JSONObject root = new JSONObject();
            root.put("componentes", componentesArray);
            root.put("conexiones", conexionesArray);

            try (FileWriter file = new FileWriter(filePath)) {
                file.write(root.toString(4));
                JOptionPane.showMessageDialog(null, "Estructura guardada como JSON correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Error al guardar JSON: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void importarJson(File file, List<JPanel[]> conexiones, JPanel centralPanel) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            JSONObject root = new JSONObject(sb.toString());
            JSONArray componentesArray = root.getJSONArray("componentes");
            JSONArray conexionesArray = root.getJSONArray("conexiones");

            centralPanel.removeAll();
            conexiones.clear();

            Map<String, JPanel> panelesPorNombre = new HashMap<>();

            for (int i = 0; i < componentesArray.length(); i++) {
                JSONObject obj = componentesArray.getJSONObject(i);
                String nombre = obj.getString("nombre");
                String tipo = obj.getString("tipo");
                int x = obj.getInt("x");
                int y = obj.getInt("y");
                int w = obj.getInt("w");
                int h = obj.getInt("h");
                String imagePath = obj.optString("imagen", null);
                JPanel panel = crearPanelDesdeTipo(nombre, tipo, w, h, imagePath);
                panel.setBounds(x, y, w, h);
                centralPanel.add(panel);
                panelesPorNombre.put(nombre, panel);
            }

            for (int i = 0; i < conexionesArray.length(); i++) {
                JSONObject obj = conexionesArray.getJSONObject(i);
                String nombre1 = obj.getString("componente1");
                String nombre2 = obj.getString("componente2");
                JPanel panel1 = panelesPorNombre.get(nombre1);
                JPanel panel2 = panelesPorNombre.get(nombre2);
                if (panel1 != null && panel2 != null) {
                    conexiones.add(new JPanel[]{panel1, panel2});
                }
            }

            centralPanel.revalidate();
            centralPanel.repaint();
            JOptionPane.showMessageDialog(null, "Estructura importada correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error al importar JSON: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static String obtenerNombrePanel(JPanel panel) {
        for (Component c : panel.getComponents()) {
            if (c instanceof JLabel lbl && lbl.getText() != null && !lbl.getText().isEmpty()) {
                return lbl.getText();
            }
        }
        return "Componente";
    }

    private static String obtenerTipoPanel(JPanel panel) {
        String imagePath = (String) panel.getClientProperty("imagePath");
        if (imagePath != null) {
            if (imagePath.contains("server")) return "servidor";
            if (imagePath.contains("router")) return "router";
            if (imagePath.contains("computer")) return "pc";
        }
        return "otro";
    }

    private static JPanel crearPanelDesdeTipo(String nombre, String tipo, int w, int h, String imagePath) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        if (imagePath != null && !imagePath.isEmpty()) {
            ImageIcon icon = new ImageIcon(imagePath);
            JLabel imgLabel = new JLabel(new ImageIcon(icon.getImage().getScaledInstance(w, h - 20, Image.SCALE_SMOOTH)));
            imgLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
            panel.add(imgLabel);
            panel.putClientProperty("imagePath", imagePath);
        }

        JLabel label = new JLabel(nombre);
        label.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        panel.add(label);
        panel.setSize(w, h);

        panel.addMouseListener(new java.awt.event.MouseAdapter() {
            Point offset;
            public void mousePressed(java.awt.event.MouseEvent evt) {
                offset = evt.getPoint();
            }
        });
        panel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                Point parentOnScreen = panel.getParent().getLocationOnScreen();
                Point mouseOnScreen = evt.getLocationOnScreen();
                int x = mouseOnScreen.x - parentOnScreen.x - panel.getWidth() / 2;
                int y = mouseOnScreen.y - parentOnScreen.y - panel.getHeight() / 2;
                panel.setLocation(x, y);
                panel.getParent().repaint();
            }
        });

        return panel;
    }

    public static JButton createHerramientasButton(JFrame frame) {
        JPopupMenu menu = new JPopupMenu();
        JMenu cambiarPaletaMenu = new JMenu("Cambiar paleta de colores");
        JMenuItem clara = new JMenuItem("Paleta clara");
        JMenuItem oscura = new JMenuItem("Paleta oscura");

        oscura.addActionListener(e -> {
            aplicarPaletaOscura(frame);
            frame.repaint();
        });

        clara.addActionListener(e -> {
            aplicarPaletaClara(frame);
            frame.repaint();
        });

        cambiarPaletaMenu.add(clara);
        cambiarPaletaMenu.add(oscura);
        menu.add(cambiarPaletaMenu);
        return createButton("Herramientas", menu);
    }

    public static void aplicarPaletaOscura(Component comp) {
        Color fondo = new Color(34, 34, 34);
        Color texto = new Color(220, 220, 220);
        Color boton = new Color(50, 50, 50);

        if (comp instanceof JPanel || comp instanceof JPopupMenu) {
            comp.setBackground(fondo);
        }
        if (comp instanceof JLabel) {
            comp.setForeground(texto);
        }
        if (comp instanceof JButton) {
            comp.setBackground(boton);
            comp.setForeground(texto);
            ((JButton) comp).setBorder(BorderFactory.createLineBorder(new Color(70, 70, 70)));
        }
        if (comp instanceof JMenuBar || comp instanceof JMenu || comp instanceof JMenuItem) {
            comp.setBackground(boton);
            comp.setForeground(texto);
        }
        if (comp instanceof JTextField) {
            comp.setBackground(new Color(44, 44, 44));
            comp.setForeground(texto);
            ((JTextField) comp).setCaretColor(texto);
        }
        if (comp instanceof JScrollPane) {
            comp.setBackground(fondo);
            ((JScrollPane) comp).getViewport().setBackground(fondo);
        }
        if (comp instanceof JFrame) {
            comp.setBackground(fondo);
        }
        if (comp instanceof JToolBar) {
            comp.setBackground(fondo);
            comp.setForeground(texto);
            ((JToolBar) comp).setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(70, 70, 70)));
        }
        if (comp instanceof Container) {
            for (Component child : ((Container) comp).getComponents()) {
                aplicarPaletaOscura(child);
            }
        }
    }

    public static void aplicarPaletaClara(Component comp) {
        Color fondo = new Color(245, 249, 255);
        Color texto = new Color(30, 30, 30);
        Color boton = new Color(250, 250, 250);

        if (comp instanceof JPanel || comp instanceof JPopupMenu) {
            comp.setBackground(fondo);
        }
        if (comp instanceof JLabel) {
            comp.setForeground(texto);
        }
        if (comp instanceof JButton) {
            comp.setBackground(boton);
            comp.setForeground(texto);
            ((JButton) comp).setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        }
        if (comp instanceof JMenuBar || comp instanceof JMenu || comp instanceof JMenuItem) {
            comp.setBackground(boton);
            comp.setForeground(texto);
        }
        if (comp instanceof JTextField) {
            comp.setBackground(Color.WHITE);
            comp.setForeground(texto);
            ((JTextField) comp).setCaretColor(texto);
        }
        if (comp instanceof JScrollPane) {
            comp.setBackground(fondo);
            ((JScrollPane) comp).getViewport().setBackground(fondo);
        }
        if (comp instanceof JFrame) {
            comp.setBackground(fondo);
        }
        if (comp instanceof JToolBar) {
            comp.setBackground(fondo);
            comp.setForeground(texto);
            ((JToolBar) comp).setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)));
        }
        if (comp instanceof Container) {
            for (Component child : ((Container) comp).getComponents()) {
                aplicarPaletaClara(child);
            }
        }
    }
}
