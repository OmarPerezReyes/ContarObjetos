import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Valores para segmentar el color azul en el espacio HSV
        double hueMin = 90;     // Rango mínimo de tono para el azul
        double hueMax = 130;    // Rango máximo de tono para el azul

        double saturationMin = 100; // Mínima saturación (puede ajustarse)
        double saturationMax = 255; // Máxima saturación (puede ajustarse)

        double valueMin = 50;   // Valor mínimo (brillo) (puede ajustarse)
        double valueMax = 255;  // Valor máximo (brillo) (puede ajustarse)

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
            if (file.isFile() && count < 10) { // Limitar a las primeras 5 imágenes
                try {
                    // Cargar la imagen utilizando OpenCV
                    Mat image = Imgcodecs.imread(file.getAbsolutePath());
                    // Verificar si la imagen se cargó correctamente
                    if (!image.empty()) {
                        // Escalar la imagen a 640x480
                        Imgproc.resize(image, image, new Size(640, 480));

                        // Convertir la imagen a espacio de color HSV
                        Mat hsvImage = new Mat();
                        Imgproc.cvtColor(image, hsvImage, Imgproc.COLOR_BGR2HSV);

                        // Definir rangos de colores en HSV para la segmentación
                        Scalar lowerBound = new Scalar(hueMin, saturationMin, valueMin);
                        Scalar upperBound = new Scalar(hueMax, saturationMax, valueMax);

                        // Crear una máscara binaria utilizando inRange
                        Mat mask = new Mat();
                        Core.inRange(hsvImage, lowerBound, upperBound, mask);

// Definir un área mínima para filtrar contornos pequeños
                        double minContourArea = 100; // Puedes ajustar este valor
// Encontrar los contornos en la máscara
                        List<MatOfPoint> contours = new ArrayList<>();
                        Mat hierarchy = new Mat();
                        Imgproc.findContours(mask, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

// Filtrar los contornos por área mínima
                        List<MatOfPoint> filteredContours = new ArrayList<>();
                        for (MatOfPoint contour : contours) {
                            double contourArea = Imgproc.contourArea(contour);
                            if (contourArea >= minContourArea) {
                                filteredContours.add(contour);
                            }
                        }

// Dibujar los contornos filtrados en la imagen original
                        Imgproc.drawContours(image, filteredContours, -1, new Scalar(0, 255, 0), 2);

// Mostrar la imagen con los contornos en una nuevos ventana (opcional, solo para visualización)
                        MatOfByte contourBuffer = new MatOfByte();
                        Imgcodecs.imencode(".jpg", image, contourBuffer);
                        ImageIcon contourIcon = new ImageIcon(contourBuffer.toArray());
                        JLabel contourLabel = new JLabel(contourIcon);

                        JFrame contourFrame = new JFrame("Contornos de Piezas Azules: " + file.getName());
                        contourFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        contourFrame.add(contourLabel);
                        contourFrame.pack();
                        contourFrame.setVisible(true);

// Mostrar el número de piezas detectadas
                        System.out.println("Número de piezas detectadas en " + file.getName() + ": " + filteredContours.size());

                    } else {
                        System.out.println("No se pudo cargar la imagen: " + file.getName());
                    }
                } catch (Exception e) {
                    System.out.println("Error al cargar la imagen: " + file.getName());
                    e.printStackTrace();
                }
                count++;
            }
        }
    }
}
