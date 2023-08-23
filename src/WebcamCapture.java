import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;
import org.opencv.highgui.HighGui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WebcamCapture {

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        VideoCapture camera = new VideoCapture(0); // 0 representa la cámara web predeterminada

        if (!camera.isOpened()) {
            System.out.println("No se pudo conectar a la cámara.");
            return;
        }

        Mat frame = new Mat();

        while (true) {
            if (camera.read(frame)) {
                Mat hsvFrame = new Mat();
                Imgproc.cvtColor(frame, hsvFrame, Imgproc.COLOR_BGR2HSV);

                // Definir rangos de colores para cada pieza
                Scalar lowerRed = new Scalar(0, 100, 100);
                Scalar upperRed = new Scalar(10, 255, 255);

                Scalar lowerOrange = new Scalar(11, 100, 100);
                Scalar upperOrange = new Scalar(25, 255, 255);

                Scalar lowerYellow = new Scalar(26, 100, 100);
                Scalar upperYellow = new Scalar(35, 255, 255);

                Scalar lowerBlue = new Scalar(100, 100, 100);
                Scalar upperBlue = new Scalar(130, 255, 255);

                Scalar lowerCyan = new Scalar(80, 100, 100);
                Scalar upperCyan = new Scalar(100, 255, 255);

                // Crear máscaras para cada rango de color
                Mat redMask = new Mat();
                Mat orangeMask = new Mat();
                Mat yellowMask = new Mat();
                Mat blueMask = new Mat();
                Mat cyanMask = new Mat();

                Core.inRange(hsvFrame, lowerRed, upperRed, redMask);
                Core.inRange(hsvFrame, lowerOrange, upperOrange, orangeMask);
                Core.inRange(hsvFrame, lowerYellow, upperYellow, yellowMask);
                Core.inRange(hsvFrame, lowerBlue, upperBlue, blueMask);
                Core.inRange(hsvFrame, lowerCyan, upperCyan, cyanMask);

                // Combinar todas las máscaras para obtener una máscara general
                Mat combinedMask = new Mat();
                Core.add(redMask, orangeMask, combinedMask);
                Core.add(combinedMask, yellowMask, combinedMask);
                Core.add(combinedMask, blueMask, combinedMask);
                Core.add(combinedMask, cyanMask, combinedMask);

                Mat contoursFrame = frame.clone();
                List<MatOfPoint> contours = new ArrayList<>();
                Mat hierarchy = new Mat();
                Imgproc.findContours(combinedMask, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

                int totalColoredObjects = 0;

                for (MatOfPoint contour : contours) {
                    Imgproc.drawContours(contoursFrame, Arrays.asList(contour), -1, new Scalar(0, 0, 255), 2);
                    totalColoredObjects++;
                }

                // Mostrar el número de objetos de colores detectados
                String text = "Total de Objetos Coloreados: " + totalColoredObjects;
                Imgproc.putText(contoursFrame, text, new Point(10, 30), Imgproc.FONT_HERSHEY_SIMPLEX, 1, new Scalar(0, 0, 255), 2);

                // Mostrar el marco con las detecciones y el recuento
                HighGui.imshow("Detección y Recuento de Objetos Coloreados", contoursFrame);
                if (HighGui.waitKey(10) == 27) {
                    break; // Presionar Esc para salir del bucle
                }
            }
        }

        camera.release();
        HighGui.destroyAllWindows();
    }
}
