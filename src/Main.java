import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main {
    // Valores para segmentar el color azul en el espacio HSV
    private static final double blueHueMin = 100;   // Rango mínimo de tono para el azul
    private static final double blueHueMax = 130;   // Rango máximo de tono para el azul
    private static final double cyanHueMin = 80;    // Rango mínimo de tono para el celeste
    private static final double cyanHueMax = 100;   // Rango máximo de tono para el celeste
    private static final double saturationMin = 100; // Mínima saturación (puede ajustarse)
    private static final double saturationMax = 255; // Máxima saturación (puede ajustarse)
    private static final double valueMin = 50;       // Valor mínimo (brillo) (puede ajustarse)
    private static final double valueMax = 255;      // Valor máximo (brillo) (puede ajustarse)

    public static void main(String[] args) {

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        String folderPath = "C:\\Users\\perez\\IdeaProjects\\ContarObjetos\\assets\\photos";
        processImages(folderPath);
    }

    private static void processImages(String folderPath) {
        File folder = new File(folderPath);

        if (!folder.exists() || !folder.isDirectory()) {
            System.out.println("La carpeta no existe o no es una carpeta válida.");
            return;
        }

        File[] files = folder.listFiles();
        int count = 0;

        for (File file : files) {
            if (file.isFile() && count < 5) {
                processSingleImage(file);
                count++;
            }
        }
    }

    private static void processSingleImage(File file) {
        try {
            Mat image = Imgcodecs.imread(file.getAbsolutePath());

            if (!image.empty()) {
                Imgproc.resize(image, image, new Size(640, 480));
                Mat hsvImage = new Mat();
                Imgproc.cvtColor(image, hsvImage, Imgproc.COLOR_BGR2HSV);

                Mat combinedMask = createCombinedMask(hsvImage);
                List<MatOfPoint> contours = findContours(combinedMask);
                List<MatOfPoint> blueContours = new ArrayList<>();
                List<MatOfPoint> cyanContours = new ArrayList<>();

                for (MatOfPoint contour : contours) {
                    double contourArea = Imgproc.contourArea(contour);

                    if (contourArea >= 100) {
                        classifyContours(contour, blueContours, cyanContours, hsvImage);
                    }
                }

                drawContoursAndText(image, blueContours, cyanContours, file.getName());
            } else {
                System.out.println("No se pudo cargar la imagen: " + file.getName());
            }
        } catch (Exception e) {
            System.out.println("Error al cargar la imagen: " + file.getName());
            e.printStackTrace();
        }
    }

    private static Mat createCombinedMask(Mat hsvImage) {
        Scalar blueLowerBound = new Scalar(100, 100, 50);
        Scalar blueUpperBound = new Scalar(130, 255, 255);
        Scalar cyanLowerBound = new Scalar(80, 100, 50);
        Scalar cyanUpperBound = new Scalar(100, 255, 255);

        Mat blueMask = new Mat();
        Core.inRange(hsvImage, blueLowerBound, blueUpperBound, blueMask);

        Mat cyanMask = new Mat();
        Core.inRange(hsvImage, cyanLowerBound, cyanUpperBound, cyanMask);

        Mat combinedMask = new Mat();
        Core.add(blueMask, cyanMask, combinedMask);

        return combinedMask;
    }

    private static List<MatOfPoint> findContours(Mat mask) {
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(mask, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        return contours;
    }

    private static void classifyContours(MatOfPoint contour, List<MatOfPoint> blueContours, List<MatOfPoint> cyanContours, Mat hsvImage) {
        double contourArea = Imgproc.contourArea(contour);

        if (contourArea >= 100) {
            MatOfPoint2f contour2f = new MatOfPoint2f(contour.toArray());
            double contourLength = Imgproc.arcLength(contour2f, true);
            MatOfPoint2f approxCurve = new MatOfPoint2f();
            Imgproc.approxPolyDP(contour2f, approxCurve, 0.02 * contourLength, true);

            Mat roi = new Mat(hsvImage, Imgproc.boundingRect(contour));
            Scalar contourHsvMean = Core.mean(roi);
            double hueValue = contourHsvMean.val[0];
            double saturationValue = contourHsvMean.val[1];
            double value = contourHsvMean.val[2];

            if (hueValue >= blueHueMin && hueValue <= blueHueMax
                    && saturationValue >= saturationMin && saturationValue <= saturationMax
                    && value >= valueMin && value <= valueMax) {
                blueContours.add(contour);
            } else if (hueValue >= cyanHueMin && hueValue <= cyanHueMax
                    && saturationValue >= saturationMin && saturationValue <= saturationMax
                    && value >= valueMin && value <= valueMax) {
                cyanContours.add(contour);
            }
        }
    }

    private static void drawContoursAndText(Mat image, List<MatOfPoint> blueContours, List<MatOfPoint> cyanContours, String fileName) {
        Imgproc.drawContours(image, blueContours, -1, new Scalar(0, 0, 255), 2); // Draw in red for blues
        Imgproc.drawContours(image, cyanContours, -1, new Scalar(255, 0, 0), 2); // Draw in blue for cyans

        String blueText = "Azules: " + blueContours.size();
        String cyanText = "Celestes: " + cyanContours.size();
        Imgproc.putText(image, blueText, new Point(10, 30), Imgproc.FONT_HERSHEY_SIMPLEX, 1, new Scalar(0, 0, 255), 2);
        Imgproc.putText(image, cyanText, new Point(10, 60), Imgproc.FONT_HERSHEY_SIMPLEX, 1, new Scalar(255, 0, 0), 2);

        MatOfByte contourBuffer = new MatOfByte();
        Imgcodecs.imencode(".jpg", image, contourBuffer);
        ImageIcon contourIcon = new ImageIcon(contourBuffer.toArray());
        JLabel contourLabel = new JLabel(contourIcon);

        JFrame contourFrame = new JFrame("Contornos de Piezas Azules y Celestes: " + fileName);
        contourFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        contourFrame.add(contourLabel);
        contourFrame.pack();
        contourFrame.setVisible(true);
    }

}
