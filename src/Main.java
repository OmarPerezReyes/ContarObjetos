import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main {
    // Valores para segmentar los colores en el espacio HSV
    private static final double blueHueMin = 100;
    private static final double blueHueMax = 130;
    private static final double cyanHueMin = 80;
    private static final double cyanHueMax = 100;
    private static final double orangeHueMin = 5;
    private static final double orangeHueMax = 25;
    private static final double redHueMin = 160;
    private static final double redHueMax = 180;
    private static final double greenHueMin = 35;
    private static final double greenHueMax = 60;
    private static final double yellowHueMin = 25;
    private static final double yellowHueMax = 35;
    private static final double saturationMin = 100;
    private static final double saturationMax = 255;
    private static final double valueMin = 50;
    private static final double valueMax = 255;

    public static void main(String[] args) {

    }

    static void processImages(String folderPath) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

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
    void processSingleImage(Mat image) {
        try {
            if (!image.empty()) {
                Imgproc.resize(image, image, new Size(640, 480));
                Mat hsvImage = new Mat();
                Imgproc.cvtColor(image, hsvImage, Imgproc.COLOR_BGR2HSV);

                Mat combinedMask = createCombinedMask(hsvImage);
                List<MatOfPoint> contours = findContours(combinedMask);
                List<MatOfPoint> blueContours = new ArrayList<>();
                List<MatOfPoint> cyanContours = new ArrayList<>();
                List<MatOfPoint> orangeContours = new ArrayList<>();
                List<MatOfPoint> redContours = new ArrayList<>();
                List<MatOfPoint> greenContours = new ArrayList<>();
                List<MatOfPoint> yellowContours = new ArrayList<>();

                for (MatOfPoint contour : contours) {
                    double contourArea = Imgproc.contourArea(contour);

                    if (contourArea >= 100) {
                        classifyContours(contour, blueContours, cyanContours, orangeContours, redContours,
                                greenContours, yellowContours, hsvImage);
                    }
                }

                drawContoursAndText(image, blueContours, cyanContours, orangeContours, redContours,
                        greenContours, yellowContours, image.toString());
            } else {
                System.out.println("No se pudo cargar la imagen: " + image.toString());
            }
        } catch (Exception e) {
            System.out.println("Error al cargar la imagen: " + image.toString());
            e.printStackTrace();
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
                List<MatOfPoint> orangeContours = new ArrayList<>();
                List<MatOfPoint> redContours = new ArrayList<>();
                List<MatOfPoint> greenContours = new ArrayList<>();
                List<MatOfPoint> yellowContours = new ArrayList<>();

                for (MatOfPoint contour : contours) {
                    double contourArea = Imgproc.contourArea(contour);

                    if (contourArea >= 100) {
                        classifyContours(contour, blueContours, cyanContours, orangeContours, redContours,
                                greenContours, yellowContours, hsvImage);
                    }
                }

                drawContoursAndText(image, blueContours, cyanContours, orangeContours, redContours,
                        greenContours, yellowContours, file.getName());
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
        Scalar orangeLowerBound = new Scalar(5, 100, 50);
        Scalar orangeUpperBound = new Scalar(25, 255, 255);
        Scalar redLowerBound1 = new Scalar(0, 100, 50);
        Scalar redUpperBound1 = new Scalar(10, 255, 255);
        Scalar redLowerBound2 = new Scalar(160, 100, 50);
        Scalar redUpperBound2 = new Scalar(180, 255, 255);
        Scalar greenLowerBound = new Scalar(35, 100, 50);
        Scalar greenUpperBound = new Scalar(60, 255, 255);
        Scalar yellowLowerBound = new Scalar(25, 100, 50);
        Scalar yellowUpperBound = new Scalar(35, 255, 255);

        Mat blueMask = new Mat();
        Core.inRange(hsvImage, blueLowerBound, blueUpperBound, blueMask);

        Mat cyanMask = new Mat();
        Core.inRange(hsvImage, cyanLowerBound, cyanUpperBound, cyanMask);

        Mat orangeMask = new Mat();
        Core.inRange(hsvImage, orangeLowerBound, orangeUpperBound, orangeMask);

        Mat redMask1 = new Mat();
        Core.inRange(hsvImage, redLowerBound1, redUpperBound1, redMask1);
        Mat redMask2 = new Mat();
        Core.inRange(hsvImage, redLowerBound2, redUpperBound2, redMask2);
        Mat redMask = new Mat();
        Core.add(redMask1, redMask2, redMask);

        Mat greenMask = new Mat();
        Core.inRange(hsvImage, greenLowerBound, greenUpperBound, greenMask);

        Mat yellowMask = new Mat();
        Core.inRange(hsvImage, yellowLowerBound, yellowUpperBound, yellowMask);

        Mat combinedMask = new Mat();
        Core.add(blueMask, cyanMask, combinedMask);
        Core.add(combinedMask, orangeMask, combinedMask);
        Core.add(combinedMask, redMask, combinedMask);
        Core.add(combinedMask, greenMask, combinedMask);
        Core.add(combinedMask, yellowMask, combinedMask);

        return combinedMask;
    }

    private static List<MatOfPoint> findContours(Mat mask) {
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(mask, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        return contours;
    }

    private static void classifyContours(MatOfPoint contour, List<MatOfPoint> blueContours, List<MatOfPoint> cyanContours,
                                         List<MatOfPoint> orangeContours, List<MatOfPoint> redContours,
                                         List<MatOfPoint> greenContours, List<MatOfPoint> yellowContours,
                                         Mat hsvImage) {
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

            if ((hueValue >= redHueMin && hueValue <= redHueMax)
                    && saturationValue >= saturationMin && saturationValue <= saturationMax
                    && value >= valueMin && value <= valueMax) {
                redContours.add(contour);
            } else if (hueValue >= greenHueMin && hueValue <= greenHueMax
                    && saturationValue >= saturationMin && saturationValue <= saturationMax
                    && value >= valueMin && value <= valueMax) {
                greenContours.add(contour);
            } else if (hueValue >= cyanHueMin && hueValue <= cyanHueMax
                    && saturationValue >= saturationMin && saturationValue <= saturationMax
                    && value >= valueMin && value <= valueMax) {
                cyanContours.add(contour);
            } else if (hueValue >= blueHueMin && hueValue <= blueHueMax
                    && saturationValue >= saturationMin && saturationValue <= saturationMax
                    && value >= valueMin && value <= valueMax) {
                blueContours.add(contour);
            } else if (hueValue >= yellowHueMin && hueValue <= yellowHueMax
                    && saturationValue >= saturationMin && saturationValue <= saturationMax
                    && value >= valueMin && value <= valueMax) {
                yellowContours.add(contour);
            } else if (hueValue >= orangeHueMin && hueValue <= orangeHueMax
                    && saturationValue >= saturationMin && saturationValue <= saturationMax
                    && value >= valueMin && value <= valueMax) {
                orangeContours.add(contour);
            }
        }
    }

    private static void drawContoursAndText(Mat image, List<MatOfPoint> blueContours, List<MatOfPoint> cyanContours,
                                            List<MatOfPoint> orangeContours, List<MatOfPoint> redContours,
                                            List<MatOfPoint> greenContours, List<MatOfPoint> yellowContours,
                                            String fileName) {
        Imgproc.drawContours(image, blueContours, -1, new Scalar(255, 0, 0), 2);  // Azul
        Imgproc.drawContours(image, cyanContours, -1, new Scalar(255, 255, 0), 2); // Cyan
        Imgproc.drawContours(image, orangeContours, -1, new Scalar(0, 165, 255), 2); // Naranja
        Imgproc.drawContours(image, redContours, -1, new Scalar(0, 0, 255), 2);   // Rojo
        Imgproc.drawContours(image, greenContours, -1, new Scalar(0, 255, 0), 2);   // Verde
        Imgproc.drawContours(image, yellowContours, -1, new Scalar(0 , 255, 255), 2); // Amarillo

        String blueText = "Azules: " + blueContours.size();
        String cyanText = "Celestes: " + cyanContours.size();
        String orangeText = "Naranjas: " + orangeContours.size();
        String redText = "Rojos: " + redContours.size();
        String greenText = "Verdes: " + greenContours.size();
        String yellowText = "Amarillos: " + yellowContours.size();

        Imgproc.putText(image, blueText, new Point(10, 30), Imgproc.FONT_HERSHEY_SIMPLEX, 1, new Scalar(255, 0, 0), 2);
        Imgproc.putText(image, cyanText, new Point(10, 60), Imgproc.FONT_HERSHEY_SIMPLEX, 1, new Scalar(255, 255, 0), 2);
        Imgproc.putText(image, orangeText, new Point(10, 90), Imgproc.FONT_HERSHEY_SIMPLEX, 1, new Scalar(0, 165, 255), 2);
        Imgproc.putText(image, redText, new Point(10, 120), Imgproc.FONT_HERSHEY_SIMPLEX, 1, new Scalar(0, 0, 255), 2);
        Imgproc.putText(image, greenText, new Point(10, 150), Imgproc.FONT_HERSHEY_SIMPLEX, 1, new Scalar(0, 255, 0), 2);
        Imgproc.putText(image, yellowText, new Point(10, 180), Imgproc.FONT_HERSHEY_SIMPLEX, 1, new Scalar(0, 255, 255), 2);

        MatOfByte contourBuffer = new MatOfByte();
        Imgcodecs.imencode(".jpg", image, contourBuffer);
        ImageIcon contourIcon = new ImageIcon(contourBuffer.toArray());
        JLabel contourLabel = new JLabel(contourIcon);

        JFrame contourFrame = new JFrame("Contornos de Bloques de Construcción: " + fileName);
        contourFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        contourFrame.add(contourLabel);
        contourFrame.pack();
        contourFrame.setVisible(true);
    }
}
