import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;  // Importa la clase VideoCapture
import org.opencv.videoio.VideoWriter;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class Video {
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

        // Ruta de la carpeta que contiene el video
        String videoPath = "assets\\video\\Video_2.mp4"; // detalles entre amarillo y naranja, si detecta rojo

        // Captura el video
        VideoCapture videoCapture = new VideoCapture(videoPath);

        // Verifica si el video se ha abierto correctamente
        if (!videoCapture.isOpened()) {
            System.out.println("No se pudo abrir el video.");
            return;
        }

        Mat frame = new Mat();
        int frameCount = 0;
        int maxFrames = 2000;  // ---------------------- >> Número máximo de cuadros a procesar (ajusta según necessario) Video1 = 2000     Video2 =

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
        int frameSkip = 5;  // Procesar cada 5to cuadro

        // Procesa los cuadros del video
        while (videoCapture.read(frame) && frameCount < maxFrames) {
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

    // Método para procesar un cuadro del video
    private static void processSingleFrame(Mat frame) {
        try {
            // Verifica si el cuadro se cargó correctamente
            if (!frame.empty()){
                // Redimensiona el cuadro a un tamaño específico si es necesario
                Imgproc.resize(frame, frame, new Size(640, 480));

                // Convierte el cuadro a espacio de color HSV
                Mat hsvFrame = new Mat();
                Imgproc.cvtColor(frame, hsvFrame, Imgproc.COLOR_BGR2HSV);

                // Crea la máscara combinada usando los umbrales de color
                Mat combinedMask = createCombinedMask(hsvFrame);

                // Encuentra los contornos en la máscara combinada
                List<MatOfPoint> contours = findContours(combinedMask);

                // Inicializa listas para almacenar contornos de diferentes colores
                List<MatOfPoint> blueContours = new ArrayList<>();
                List<MatOfPoint> cyanContours = new ArrayList<>();
                List<MatOfPoint> orangeContours = new ArrayList<>();
                List<MatOfPoint> redContours = new ArrayList<>();
                List<MatOfPoint> greenContours = new ArrayList<>();
                List<MatOfPoint> yellowContours = new ArrayList<>();

                // Clasifica los contornos en las listas de contornos de diferentes colores
                for (MatOfPoint contour : contours) {
                    double contourArea = Imgproc.contourArea(contour);

                    if (contourArea >= 100) {
                        classifyContours(contour, blueContours, cyanContours, orangeContours, redContours,
                                greenContours, yellowContours, hsvFrame);
                    }
                }

                // Dibuja contornos y muestra información en una ventana emergente (o guarda resultados en un video)
                drawContoursAndText(frame, blueContours, cyanContours, orangeContours, redContours,
                        greenContours, yellowContours);
            }
        } catch (Exception e) {
            System.out.println("Error al procesar el cuadro.");
            e.printStackTrace();
        }
    }


    // Método para crear la máscara combinada utilizando umbrales de color
    private static Mat createCombinedMask(Mat hsvImage) {
        // Definición de umbrales de color para diferentes colores
        Scalar blueLowerBound = new Scalar(100, 100, 50);
        Scalar blueUpperBound = new Scalar(130, 255, 255);
        Scalar cyanLowerBound = new Scalar(80, 100, 50); // Ajusta el límite inferior para el tono celeste
        Scalar cyanUpperBound = new Scalar(100, 255, 255); // Ajusta el límite superior para el tono celeste
        Scalar orangeLowerBound = new Scalar(5, 100, 50);
        Scalar orangeUpperBound = new Scalar(25, 255, 255);
        Scalar redLowerBound1 = new Scalar(160, 100, 50); // Ajusta el límite inferior para el tono rojo
        Scalar redUpperBound1 = new Scalar(180, 255, 255); // Ajusta el límite superior para el tono rojo
        Scalar redLowerBound2 = new Scalar(0, 100, 50); // Ajusta el límite inferior para el tono rojo
        Scalar redUpperBound2 = new Scalar(10, 255, 255); // Ajusta el límite superior para el tono rojo
        Scalar greenLowerBound = new Scalar(35, 100, 50);
        Scalar greenUpperBound = new Scalar(60, 255, 255);
        Scalar yellowLowerBound = new Scalar(25, 100, 50);
        Scalar yellowUpperBound = new Scalar(35, 255, 255);
        // Creación de máscaras individuales para cada color
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

        // Combinación de máscaras individuales en una máscara combinada
        Mat combinedMask = new Mat();
        Core.add(blueMask, cyanMask, combinedMask);
        Core.add(combinedMask, orangeMask, combinedMask);
        Core.add(combinedMask, redMask, combinedMask);
        Core.add(combinedMask, greenMask, combinedMask);
        Core.add(combinedMask, yellowMask, combinedMask);

        return combinedMask;
    }

    // Método para encontrar contornos en una máscara
    private static List<MatOfPoint> findContours(Mat mask) {
        List<MatOfPoint> contours = new ArrayList<>();
        // Encuentra los contornos en la máscara
        Mat hierarchy = new Mat();
        Imgproc.findContours(mask, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        return contours;
    }

    // Método para clasificar contornos en diferentes listas de contornos basados en el color
    private static void classifyContours(MatOfPoint contour, List<MatOfPoint> blueContours, List<MatOfPoint> cyanContours,
                                         List<MatOfPoint> orangeContours, List<MatOfPoint> redContours,
                                         List<MatOfPoint> greenContours, List<MatOfPoint> yellowContours,
                                         Mat hsvImage) {
        // Calcula características del contorno y del color en el área del contorno
        double contourArea = Imgproc.contourArea(contour);

        // Clasifica el contorno en la lista de contornos adecuada según su color
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

            if ((hueValue >= redHueMin && hueValue <= redHueMax) &&
                    saturationValue >= saturationMin && saturationValue <= saturationMax &&
                    value >= valueMin && value <= valueMax) {

                // Separate detection for the two ranges of red hue
                if (hueValue >= redHueMin && hueValue <= redHueMax) {
                    redContours.add(contour);
                } else if (hueValue >= 0 && hueValue <= 10) {
                    redContours.add(contour);
                }         // (Clasificación para otros colores)
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


    // Método para dibujar contornos y mostrar información en una ventana emergente
    private static void drawContoursAndText(Mat image, List<MatOfPoint> blueContours, List<MatOfPoint> cyanContours,
                                            List<MatOfPoint> orangeContours, List<MatOfPoint> redContours,
                                            List<MatOfPoint> greenContours, List<MatOfPoint> yellowContours) {

        // Dibuja los contornos en la imagen
        Imgproc.drawContours(image, blueContours, -1, new Scalar(255, 0, 0), 2);  // Azul
        Imgproc.drawContours(image, cyanContours, -1, new Scalar(255, 255, 0), 2); // Cyan
        Imgproc.drawContours(image, orangeContours, -1, new Scalar(0, 165, 255), 2); // Naranja
        Imgproc.drawContours(image, redContours, -1, new Scalar(0, 0, 255), 2);   // Rojo
        Imgproc.drawContours(image, greenContours, -1, new Scalar(0, 255, 0), 2);   // Verde
        Imgproc.drawContours(image, yellowContours, -1, new Scalar(0 , 255, 255), 2); // Amarillo

        // Agrega información de conteo de objetos en la imagen
        String blueText = "Azules: " + blueContours.size();
        String cyanText = "Celestes: " + cyanContours.size();
        String orangeText = "Naranjas: " + orangeContours.size();
        String redText = "Rojos: " + redContours.size();
        String greenText = "Verdes: " + greenContours.size();
        String yellowText = "Amarillos: " + yellowContours.size();

        // Agrega el texto en la imagen
        Imgproc.putText(image, blueText, new Point(10, 30), Imgproc.FONT_HERSHEY_SIMPLEX, 1, new Scalar(255, 0, 0), 2);
        Imgproc.putText(image, cyanText, new Point(10, 60), Imgproc.FONT_HERSHEY_SIMPLEX, 1, new Scalar(255, 255, 0), 2);
        Imgproc.putText(image, orangeText, new Point(10, 90), Imgproc.FONT_HERSHEY_SIMPLEX, 1, new Scalar(0, 165, 255), 2);
        Imgproc.putText(image, redText, new Point(10, 120), Imgproc.FONT_HERSHEY_SIMPLEX, 1, new Scalar(0, 0, 255), 2);
        Imgproc.putText(image, greenText, new Point(10, 150), Imgproc.FONT_HERSHEY_SIMPLEX, 1, new Scalar(0, 255, 0), 2);
        Imgproc.putText(image, yellowText, new Point(10, 180), Imgproc.FONT_HERSHEY_SIMPLEX, 1, new Scalar(0, 255, 255), 2);
    }

    // Método para actualizar la etiqueta con el cuadro procesado en la ventana
    private static void updateContourLabel(JLabel contourLabel, Mat image) {
        MatOfByte buffer = new MatOfByte();
        Imgcodecs.imencode(".jpg", image, buffer);
        ImageIcon icon = new ImageIcon(buffer.toArray());
        contourLabel.setIcon(icon);
    }
}
