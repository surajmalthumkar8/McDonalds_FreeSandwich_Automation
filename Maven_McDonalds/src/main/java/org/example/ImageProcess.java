package org.example;

import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.logging.Level;

import static org.example.McDonaldsSurveyAutomation.logger;

public class ImageProcess {
    static String preprocessImage(String imagePath) {
        try {
            Mat image = Imgcodecs.imread(imagePath);
            if (image.empty()) {
                logger.severe("Failed to load image: " + imagePath);
                return null;
            }

            int height = image.rows();
            int width = image.cols();
            Rect roi = new Rect(0, 0, width, (int) (height * 0.3));
            Mat croppedImage = new Mat(image, roi);

            Imgproc.cvtColor(croppedImage, croppedImage, Imgproc.COLOR_BGR2GRAY);
            Imgproc.threshold(croppedImage, croppedImage, 0, 255, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);

            String outputImagePath = "C:\\Users\\Suraj\\Downloads\\preprocessed_receipt.jpeg";
            Imgcodecs.imwrite(outputImagePath, croppedImage);
            logger.info("Image preprocessing completed: " + outputImagePath);

            return outputImagePath;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error during image preprocessing: " + e.getMessage(), e);
            return null;
        }
    }

}
