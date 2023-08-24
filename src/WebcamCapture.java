import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class WebcamCapture extends Video{
    // Definición de umbrales de color para segmentación en el espacio HSV
    private static final double blueHueMin = 105;
    private static final double blueHueMax = 130;
    private static final double cyanHueMin = 95;
    private static final double cyanHueMax = 105;
    private static final double orangeHueMin = 5;
    private static final double orangeHueMax = 23.5;
    private static final double redHueMin = 160;
    private static final double redHueMax = 180;
    private static final double greenHueMin = 35;
    private static final double greenHueMax = 60;
    private static final double yellowHueMin = 23.5;
    private static final double yellowHueMax = 35;
    private static final double saturationMin = 100;
    private static final double saturationMax = 255;
    private static final double valueMin = 50;
    private static final double valueMax = 255;
    public static void main(String[] args) {
        // Carga la biblioteca OpenCV
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // Inicializa la captura de la cámara web
        VideoCapture videoCapture = new VideoCapture(0); // El argumento 0 indica el índice de la cámara

        // Verifica si la cámara web se ha abierto correctamente
        if (!videoCapture.isOpened()) {
            System.out.println("No se pudo abrir la cámara web.");
            return;
        }

        Mat frame = new Mat();
        int frameCount = 0;
        int maxFrames = 2000;  // Número máximo de cuadros a procesar (ajusta según necesario)
        int frameSkip = 4;  // Procesar cada 5to cuadro

        //----------------------------------------
        // Crear la ventana para mostrar los resultados
        JFrame contourFrame = new JFrame("Contornos de Bloques de Construcción");
        contourFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JLabel contourLabel = new JLabel();
        contourFrame.add(contourLabel);
        contourFrame.setSize(650, 520);  // Ajusta el tamaño de la ventana emergente
        contourFrame.setLocationRelativeTo(null);  // Centra la ventana en la pantalla
        contourFrame.setVisible(true);
        //----------------------------------------

        // Procesa los cuadros de la cámara web
        while (frameCount < maxFrames) {
            videoCapture.read(frame);

            if (frameCount % frameSkip == 0) {  // Saltar cuadros intermedios
                processSingleFrame(frame);

                // Actualiza la ventana con el cuadro procesado
                updateContourLabel(contourLabel, frame);
            }
            frameCount++;
        }

        // Libera recursos
        videoCapture.release();
    }

}
