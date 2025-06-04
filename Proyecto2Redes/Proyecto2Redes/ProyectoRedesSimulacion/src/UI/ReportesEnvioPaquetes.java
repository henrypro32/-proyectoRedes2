import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ReportesEnvioPaquetes {
    public void mostrarReportePaquetes(int enviados, int recibidos, int perdidos, String tiempoEnvio, String tiempoRecepcion, String latencia, String tamano, String ruta) {
        JFrame resumenFrame = new JFrame("Resumen de Envío de Paquetes");
        resumenFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        resumenFrame.setSize(400, 350);
        resumenFrame.setLocationRelativeTo(null);

        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Arial", Font.PLAIN, 14));
        area.setText(
            "Número de paquetes enviados: " + enviados + "\n" +
            "Número de paquetes recibidos: " + recibidos + "\n" +
            "Número de paquetes perdidos: " + perdidos + "\n" +
            "Tiempos de envío y recepción: " + tiempoEnvio + " / " + tiempoRecepcion + "\n" +
            "Latencia: " + latencia + "\n" +
            "Tamaño de los paquetes: " + tamano + "\n" +
            "Ruta seguida por el paquete: " + ruta
        );
        resumenFrame.add(new JScrollPane(area));
        resumenFrame.setVisible(true);
    }
}
