import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InterfazGrafica {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Detector de Objetos de Colores");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setPreferredSize(new Dimension(400, 200)); // Tamaño personalizado

            JPanel panel = new JPanel();
            panel.setLayout(new GridLayout(3, 1, 10, 10)); // Diseño en forma de cuadrícula

            JButton preselectedButton = new JButton("Imagenes preseleccionadas");
            JButton selectImageButton = new JButton("Seleccionar imagen");
            JButton openCameraButton = new JButton("Abrir cámara");

            panel.add(preselectedButton);
            panel.add(selectImageButton);
            panel.add(openCameraButton);

            preselectedButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Crea una instancia de Main
                    Main main = new Main();

                    // Crea o carga una instancia 'preselectedImage' de tipo Mat que contiene la imagen preseleccionada

                    main.processImages("C:\\Users\\perez\\IdeaProjects\\ContarObjetos\\assets\\photos");

                    }
            });
            selectImageButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Acción cuando se presiona el botón "Seleccionar imagen"
                    JOptionPane.showMessageDialog(frame, "Seleccionar imagen elegida");
                }
            });

            openCameraButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Acción cuando se presiona el botón "Abrir cámara"
                    JOptionPane.showMessageDialog(frame, "Abrir cámara elegida");
                }
            });

            frame.getContentPane().add(panel);
            frame.pack();
            frame.setVisible(true);
        });
    }
}
