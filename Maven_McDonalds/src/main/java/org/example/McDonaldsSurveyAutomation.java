package org.example;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;


import static org.example.CodeExtract.extractSurveyCode;
import static org.example.ImageProcess.preprocessImage;

public class McDonaldsSurveyAutomation {
    static {
        try {
            // Actual path of OpenCV library
            System.load("C:\\Users\\Suraj\\Downloads\\opencv\\build\\java\\x64\\opencv_java451.dll");
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Failed to load OpenCV native library: " + e.getMessage());
            System.exit(1);
        }
    }


    static final Logger logger = Logger.getLogger(McDonaldsSurveyAutomation.class.getName());

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("No file path provided");
            return;
        }

        // Get the file path from the command-line argument
        String imagePath = args[0];
        logger.info("Received file path: " + imagePath);

        // Rest of your existing logic
        logger.info("Starting image preprocessing...");
        String preprocessedImagePath = preprocessImage(imagePath);
        if (preprocessedImagePath == null) {
            logger.severe("Image preprocessing failed.");
            return;
        }

        logger.info("Starting OCR extraction...");
        String surveyCode = extractSurveyCode(preprocessedImagePath);

        if (surveyCode == null || surveyCode.isEmpty()) {
            logger.severe("Failed to extract a valid survey code from the image.");
            return;
        }

        // Output the extracted validation code
        logger.info("Extracted Validation Code: " + surveyCode);
        System.out.println("Validation Code: " + surveyCode);


        // Web automation with Selenium
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\Suraj\\Downloads\\chromedriver-win64 (2)\\chromedriver-win64\\chromedriver.exe");        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--disable-web-security");
        WebDriver driver = new ChromeDriver(options);

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(1));
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        Random random = new Random();

        try {
            driver.get("https://www.mcdvoice.com/");
            logger.info("Navigated to McDonald's survey page.");

            logger.info("Entering survey code: " + surveyCode);
            for (int i = 0; i < 5; i++) {
                WebElement codeInput = wait.until(ExpectedConditions.elementToBeClickable(By.id("CN" + (i + 1))));
                codeInput.sendKeys(surveyCode.substring(i * 5, (i + 1) * 5));
            }
            WebElement codeInput6 = wait.until(ExpectedConditions.elementToBeClickable(By.id("CN6")));
            codeInput6.sendKeys(surveyCode.substring(25));
            logger.info("Entered final part of the survey code.");

            WebElement startButton = wait.until(ExpectedConditions.elementToBeClickable(By.name("NextButton")));
            startButton.click();
            logger.info("Clicked 'Start' button.");

            while (true) {
                List<WebElement> radioButtons = driver.findElements(By.cssSelector("input[type='radio']"));
                if (!radioButtons.isEmpty()) {
                    String id = radioButtons.get(random.nextInt(radioButtons.size())).getAttribute("id");
                    WebElement associatedLabel = driver.findElement(By.cssSelector("label[for='" + id + "']"));
                    wait.until(ExpectedConditions.elementToBeClickable(associatedLabel)).click();
                    logger.info("Selected an option by clicking its associated label.");
                } else {
                    logger.info("No radio buttons found on the page.");
                }

                List<WebElement> checkboxes = driver.findElements(By.cssSelector("input[type='checkbox']"));
                if (!checkboxes.isEmpty()) {
                    WebElement checkbox = checkboxes.get(random.nextInt(checkboxes.size()));
                    String checkboxId = checkbox.getAttribute("id");
                    WebElement associatedLabel = driver.findElement(By.cssSelector("label[for='" + checkboxId + "']"));
                    wait.until(ExpectedConditions.elementToBeClickable(associatedLabel)).click();
                    logger.info("Selected a checkbox by clicking its associated label.");
                }

                List<WebElement> nextButtons = driver.findElements(By.id("NextButton"));
                if (!nextButtons.isEmpty()) {
                    WebElement nextButton = nextButtons.get(0);
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", nextButton);
                    wait.until(ExpectedConditions.elementToBeClickable(nextButton));
                    try {
                        nextButton.click();
                        logger.info("Clicked 'Next' button.");
                    } catch (Exception e) {
                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", nextButton);
                        logger.info("Clicked 'Next' button using JavaScript.");
                    }
                } else {
                    logger.info("No 'Next' button found. Exiting survey navigation loop.");
                    break;
                }

                Thread.sleep(500); // Reduced sleep for faster navigation
            }

            try {
                // Wait for the page content to be visible
                WebElement contentElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("content"))); // Adjust based on your element
                String pageText = contentElement.getText();

                // Extract 7-digit code using regex
                String validationCode = pageText.replaceAll("[^0-9]", "").substring(0, 7);
                logger.info("Extracted Validation Code: " + validationCode);
                System.out.println("Validation Code: " + validationCode);

            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error extracting validation code: " + e.getMessage(), e);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error during form submission: " + e.getMessage(), e);
        }
    }
}
