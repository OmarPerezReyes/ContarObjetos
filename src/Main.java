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
        double blueHueMin = 100;    // Rango mínimo de tono para el azul
        double blueHueMax = 130;    // Rango máximo de tono para el azul

        double cyanHueMin = 80;    // Rango mínimo de tono para el celeste
        double cyanHueMax = 100;    // Rango máximo de tono para el celeste

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
            if (file.isFile() && count < 5) { // Limitar a las primeras 5 imágenes
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

                        // Definir rangos de colores en HSV para la segmentación de azules
                        Scalar blueLowerBound = new Scalar(blueHueMin, saturationMin, valueMin);
                        Scalar blueUpperBound = new Scalar(blueHueMax, saturationMax, valueMax);

                        // Crear una máscara binaria para azules utilizando inRange
                        Mat blueMask = new Mat();
                        Core.inRange(hsvImage, blueLowerBound, blueUpperBound, blueMask);

                        // Definir rangos de colores en HSV para la segmentación de celestes
                        Scalar cyanLowerBound = new Scalar(cyanHueMin, saturationMin, valueMin);
                        Scalar cyanUpperBound = new Scalar(cyanHueMax, saturationMax, valueMax);

                        // Crear una máscara binaria para celestes utilizando inRange
                        Mat cyanMask = new Mat();
                        Core.inRange(hsvImage, cyanLowerBound, cyanUpperBound, cyanMask);

                        // Combinar las máscaras para obtener elementos azules y celestes
                        Mat combinedMask = new Mat();
                        Core.add(blueMask, cyanMask, combinedMask);

                        // Definir un área mínima para filtrar contornos pequeños
                        double minContourArea = 100; // Puedes ajustar este valor
                        // Encontrar los contornos en la máscara combinada
                        List<MatOfPoint> contours = new ArrayList<>();
                        Mat hierarchy = new Mat();
                        Imgproc.findContours(combinedMask, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
// Filtrar los contornos por área mínima y asignarlos a las listas de azules y celestes
                        List<MatOfPoint> blueContours = new ArrayList<>();
                        List<MatOfPoint> cyanContours = new ArrayList<>();
                        for (MatOfPoint contour : contours) {
                            double contourArea = Imgproc.contourArea(contour);
                            if (contourArea >= minContourArea) {
                                MatOfPoint2f contour2f = new MatOfPoint2f(contour.toArray());
                                double contourLength = Imgproc.arcLength(contour2f, true);
                                MatOfPoint2f approxCurve = new MatOfPoint2f();
                                Imgproc.approxPolyDP(contour2f, approxCurve, 0.02 * contourLength, true);

                                // Distinguir entre azul y celeste en función del rango de tono (hue)
                                Scalar contourHsvMean = Core.mean(hsvImage, blueMask);
                                double hueValue = contourHsvMean.val[0];

                                if (hueValue >= blueHueMin && hueValue <= blueHueMax) {
                                    blueContours.add(contour);
                                } else if (hueValue >= cyanHueMin && hueValue <= cyanHueMax) {
                                    cyanContours.add(contour);
                                }
                            }
                        }
// Dibujar los contornos filtrados en la imagen original
                        Imgproc.drawContours(image, blueContours, -1, new Scalar(0, 0, 255), 2); // Dibujar en rojo para azules
                        Imgproc.drawContours(image, cyanContours, -1, new Scalar(255, 0, 0), 2); // Dibujar en azul para celestes

// Agregar la cantidad de piezas azules y celestes como texto en la imagen
                        String blueText = "Azules: " + blueContours.size();
                        String cyanText = "Celestes: " + cyanContours.size();
                        Imgproc.putText(image, blueText, new Point(10, 30), Imgproc.FONT_HERSHEY_SIMPLEX, 1, new Scalar(0, 0, 255), 2);
                        Imgproc.putText(image, cyanText, new Point(10, 60), Imgproc.FONT_HERSHEY_SIMPLEX, 1, new Scalar(255, 0, 0), 2);

// Mostrar la imagen con los contornos y la información en una nueva ventana
                        MatOfByte contourBuffer = new MatOfByte();
                        Imgcodecs.imencode(".jpg", image, contourBuffer);
                        ImageIcon contourIcon = new ImageIcon(contourBuffer.toArray());
                        JLabel contourLabel = new JLabel(contourIcon);

                        JFrame contourFrame = new JFrame("Contornos de Piezas Azules y Celestes: " + file.getName());
                        contourFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        contourFrame.add(contourLabel);
                        contourFrame.pack();
                        contourFrame.setVisible(true);

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
