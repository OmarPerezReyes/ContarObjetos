import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.swing.*;
import java.io.File;

public class Main {
    public static void main(String[] args) {
        // Carga la biblioteca nativa de OpenCV
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // Ruta de la carpeta que contiene las imágenes
        String folderPath = "C:\\Users\\perez\\IdeaProjects\\ContarObjetos\\assets\\photos";

        // Crear un objeto File para representar la carpeta
        File folder = new File(folderPath);

        // Verificar si la carpeta existe
        if (!folder.exists() || !folder.isDirectory()) {
            System.out.println("La carpeta no existe o no es una carpeta válida.");
            return;
        }

        // Obtener una lista de archivos en la carpeta
        File[] files = folder.listFiles();

        // Contador para controlar el número de imágenes procesadas
        int count = 0;

        // Recorrer los archivos y cargar las imágenes usando OpenCV
        for (File file : files) {
            if (file.isFile() && count < 5) { // Limitar a las primeras 5 imágenes
                // Cargar la imagen utilizando OpenCV
                Mat image = Imgcodecs.imread(file.getAbsolutePath());

                // Verificar si la imagen se cargó correctamente
                if (!image.empty()) {
                    // Escalar la imagen a 640x480
                    Imgproc.resize(image, image, new Size(640, 480));

                    MatOfByte buffer = new MatOfByte();
                    Imgcodecs.imencode(".jpg", image, buffer); // Convertir Mat a byte buffer

                    ImageIcon icon = new ImageIcon(buffer.toArray()); // Convertir buffer a ImageIcon
                    JLabel label = new JLabel(icon);

                    // Crear una nueva ventana para cada imagen
                    JFrame frame = new JFrame("Imagen: " + file.getName());
                    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    frame.add(label);
                    frame.pack();
                    frame.setVisible(true);

                    count++; // Incrementar el contador
                } else {
                    System.out.println("No se pudo cargar la imagen: " + file.getName());
                }
            }
        }
    }
}
