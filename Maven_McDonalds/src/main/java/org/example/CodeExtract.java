package org.example;

import net.sourceforge.tess4j.ITessAPI;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.example.McDonaldsSurveyAutomation.logger;

public class CodeExtract {

    static {
        // Load OpenCV library
        //System.load("C:\\Users\\Suraj\\Downloads\\opencv\\build\\java\\x64\\opencv_java451.dll");
        System.setProperty("java.library.path", "C:\\Users\\Suraj\\Downloads\\opencv\\build\\x64");

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    static String extractSurveyCode(String imagePath) {
        // Initialize Tesseract OCR instance
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath("C:\\Program Files\\Tesseract-OCR\\tessdata"); // Adjust path as per installation
        tesseract.setLanguage("eng");
        tesseract.setPageSegMode(ITessAPI.TessPageSegMode.PSM_SINGLE_BLOCK); // Treat as a block of text
        tesseract.setTessVariable("tessedit_char_whitelist", "0123456789-"); // Whitelist digits and hyphen

        try {
            // Perform OCR
            String extractedText = tesseract.doOCR(new File(imagePath));
            logger.info("Extracted Text: " + extractedText);

            // Additional cleaning and normalization
            extractedText = extractedText.replaceAll("[^\\d-]", "").replaceAll("\\s+", ""); // Remove unwanted characters
            logger.info("Cleaned Extracted Text: " + extractedText);

            // Improved regex pattern for capturing survey code
            Pattern pattern = Pattern.compile("\\d{5}-\\d{5}-\\d{5}-\\d{5}-\\d{5}-\\d");
            Matcher matcher = pattern.matcher(extractedText);
            if (matcher.find()) {
                return matcher.group().replace("-", ""); // Remove hyphens for easier processing
            } else {
                logger.warning("No survey code found in the image.");
            }
        } catch (TesseractException e) {
            logger.log(Level.SEVERE, "Error during OCR processing: " + e.getMessage(), e);
        }
        return null;
    }
}
