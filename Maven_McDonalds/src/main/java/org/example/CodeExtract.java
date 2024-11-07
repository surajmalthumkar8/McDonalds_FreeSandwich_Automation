package org.example;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.io.File;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.example.McDonaldsSurveyAutomation.logger;

public class CodeExtract {
    static String extractSurveyCode(String imagePath) {
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath("C:\\Program Files\\Tesseract-OCR\\tessdata"); // path of tessdata
        tesseract.setLanguage("eng");

        try {
            String extractedText = tesseract.doOCR(new File(imagePath));
            logger.info("Extracted Text: " + extractedText);

            // Clean up of the extracted text
            extractedText = extractedText.replaceAll("\\s+", "").replaceAll("-", "");
            logger.info("Cleaned Extracted Text: " + extractedText);

            // Regex for survey code pattern
            Pattern pattern = Pattern.compile("\\d{5}\\d{5}\\d{5}\\d{5}\\d{5}\\d");
            Matcher matcher = pattern.matcher(extractedText);
            if (matcher.find()) {
                return matcher.group();
            } else {
                logger.warning("No survey code found in the image.");
            }
        } catch (TesseractException e) {
            logger.log(Level.SEVERE, "Error during OCR processing: " + e.getMessage(), e);
        }
        return null;
    }


}
