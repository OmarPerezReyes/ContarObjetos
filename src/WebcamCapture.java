import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.imgproc.Imgproc;
import org.opencv.highgui.HighGui;

public class WebcamCapture {
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        VideoCapture capture = new VideoCapture(0); // Abre la cámara web (usualmente 0 es la cámara por defecto)

        if (!capture.isOpened()) {
            System.out.println("Error al abrir la cámara.");
            return;
        }

        Mat frame = new Mat();

        while (true) {
            if (capture.read(frame)) {
                // Realiza algún procesamiento en el frame si es necesario
                // Por ejemplo, puedes aplicar filtros o detección de objetos aquí

                // Muestra el frame en una ventana
                HighGui.imshow("Webcam", frame);

                if (HighGui.waitKey(1) == 27) { // Presiona la tecla Esc para salir
                    break;
                }
            }
        }

        capture.release();
        HighGui.destroyAllWindows();
    }
}