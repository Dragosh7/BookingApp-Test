import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;
//--
import io.opentelemetry.exporter.logging.SystemOutLogRecordExporter;
import org.openqa.selenium.By;
//--
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
//--
import org.openqa.selenium.WebElement;
//--
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
//--
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
//--
import org.testng.annotations.AfterClass;
//--
import org.testng.annotations.BeforeClass;
//--
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;


public class ContactPage {

    private WebDriver driver;

    @BeforeClass
    public void beforeClass() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-search-engine-choice-screen");
        // options.addArguments("--remote-allow-origins=*");
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
    }

    @AfterClass
    public void afterClass() throws InterruptedException{

        Thread.sleep(1000);
        driver.quit();

    }

    @Test
    public void submitFunction() throws InterruptedException {

        SoftAssert softAssert = new SoftAssert();
        driver.get("https://ancabota09.wixsite.com/intern");

        WebElement button = driver.findElement(By.id("i6kl732v3label"));
        driver.manage().timeouts().implicitlyWait(Duration.of(10, ChronoUnit.SECONDS));

        button.click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlContains("https://ancabota09.wixsite.com/intern/contact"));

        String currentUrl = driver.getCurrentUrl();
        softAssert.assertEquals(currentUrl, "https://ancabota09.wixsite.com/intern/contact", "Bad redirect");

        WebElement nameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"input_comp-jxbsa1e9\"]")));
        WebElement emailField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"input_comp-jxbsa1em\"]")));
        WebElement phoneField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"input_comp-jxbsa1ev\"]")));
        WebElement messageField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"textarea_comp-jxbsa1f7\"]")));
        WebElement submitButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"comp-jxbsa1fi\"]/button")));

        softAssert.assertTrue(nameField.isDisplayed(), "Name field is not displayed");
        softAssert.assertTrue(emailField.isDisplayed(), "Email field is not displayed");
        softAssert.assertTrue(phoneField.isDisplayed(), "Phone field is not displayed");
        softAssert.assertTrue(messageField.isDisplayed(), "Message field is not displayed");
        softAssert.assertTrue(submitButton.isDisplayed(), "Submit button is not displayed");

        Thread.sleep(1000);
        nameField.sendKeys("John");
        wait.until(ExpectedConditions.textToBePresentInElementValue(nameField, "John"));

        emailField.sendKeys("john@mail.com");
        wait.until(ExpectedConditions.textToBePresentInElementValue(emailField, "john@mail.com"));

        phoneField.sendKeys("078975685");
        wait.until(ExpectedConditions.textToBePresentInElementValue(phoneField, "078975685"));

        messageField.sendKeys("Message!");
        wait.until(ExpectedConditions.textToBePresentInElementValue(messageField, "Message!"));

        //before clicking submit
        WebElement submitFeedback = driver.findElement(By.xpath("//*[@id=\"comp-jxbsa1fv\"]/p/span"));
        softAssert.assertTrue(submitFeedback.getText().isEmpty() || submitFeedback.getText().isBlank(), "Thanks for submitting message is visible before clicking submit");

        submitButton.click();

        softAssert.assertFalse(submitFeedback.getText().isEmpty() || submitFeedback.getText().isBlank(), "Thanks for submitting message must be visible after clicking submit");
        softAssert.assertAll();
    }

    @Test
    public void emailValidation() throws InterruptedException {

        SoftAssert softAssert = new SoftAssert();
        driver.get("https://ancabota09.wixsite.com/intern");

        WebElement button = driver.findElement(By.id("i6kl732v3label"));
        driver.manage().timeouts().implicitlyWait(Duration.of(10, ChronoUnit.SECONDS));

        button.click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlContains("https://ancabota09.wixsite.com/intern/contact"));

        String currentUrl = driver.getCurrentUrl();
        softAssert.assertEquals(currentUrl, "https://ancabota09.wixsite.com/intern/contact", "Bad redirect");

        WebElement nameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"input_comp-jxbsa1e9\"]")));
        WebElement emailField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"input_comp-jxbsa1em\"]")));
        WebElement phoneField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"input_comp-jxbsa1ev\"]")));
        WebElement messageField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"textarea_comp-jxbsa1f7\"]")));
        WebElement submitButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"comp-jxbsa1fi\"]/button")));

        softAssert.assertTrue(nameField.isDisplayed(), "Name field is not displayed");
        softAssert.assertTrue(emailField.isDisplayed(), "Email field is not displayed");
        softAssert.assertTrue(phoneField.isDisplayed(), "Phone field is not displayed");
        softAssert.assertTrue(messageField.isDisplayed(), "Message field is not displayed");
        softAssert.assertTrue(submitButton.isDisplayed(), "Submit button is not displayed");

        Thread.sleep(1000);
        nameField.sendKeys("John");
        wait.until(ExpectedConditions.textToBePresentInElementValue(nameField, "John"));

        emailField.sendKeys("johnmail.com");
        wait.until(ExpectedConditions.textToBePresentInElementValue(emailField, "johnmail.com"));

        phoneField.sendKeys("078975685");
        wait.until(ExpectedConditions.textToBePresentInElementValue(phoneField, "078975685"));

        messageField.sendKeys("Message!");
        wait.until(ExpectedConditions.textToBePresentInElementValue(messageField, "Message!"));

        submitButton.click();

        softAssert.assertEquals(emailField.getAttribute("aria-invalid"), "true","The form accepted an invalid email");

        softAssert.assertAll();
    }

    @Test
    public void phoneValidation() throws InterruptedException {

        SoftAssert softAssert = new SoftAssert();
        driver.get("https://ancabota09.wixsite.com/intern");

        WebElement button = driver.findElement(By.id("i6kl732v3label"));
        driver.manage().timeouts().implicitlyWait(Duration.of(10, ChronoUnit.SECONDS));

        button.click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlContains("https://ancabota09.wixsite.com/intern/contact"));

        String currentUrl = driver.getCurrentUrl();
        softAssert.assertEquals(currentUrl, "https://ancabota09.wixsite.com/intern/contact", "Bad redirect");

        Thread.sleep(1000);
        WebElement nameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"input_comp-jxbsa1e9\"]")));
        WebElement emailField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"input_comp-jxbsa1em\"]")));
        WebElement phoneField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"input_comp-jxbsa1ev\"]")));
        WebElement messageField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"textarea_comp-jxbsa1f7\"]")));
        WebElement submitButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"comp-jxbsa1fi\"]/button")));

        softAssert.assertTrue(nameField.isDisplayed(), "Name field is not displayed");
        softAssert.assertTrue(emailField.isDisplayed(), "Email field is not displayed");
        softAssert.assertTrue(phoneField.isDisplayed(), "Phone field is not displayed");
        softAssert.assertTrue(messageField.isDisplayed(), "Message field is not displayed");
        softAssert.assertTrue(submitButton.isDisplayed(), "Submit button is not displayed");


        nameField.sendKeys("John");
        wait.until(ExpectedConditions.textToBePresentInElementValue(nameField, "John"));

        emailField.sendKeys("john@mail.com");
        wait.until(ExpectedConditions.textToBePresentInElementValue(emailField, "john@mail.com"));

        phoneField.sendKeys("00000");
        wait.until(ExpectedConditions.textToBePresentInElementValue(phoneField, "00000"));

        messageField.sendKeys("Message!");
        wait.until(ExpectedConditions.textToBePresentInElementValue(messageField, "Message!"));

        submitButton.click();

        softAssert.assertEquals(phoneField.getAttribute("aria-invalid"), "true","The form accepted an invalid phone");

        Thread.sleep(1000);
        phoneField.clear();
        phoneField.sendKeys(Keys.CONTROL + "a");
        phoneField.sendKeys(Keys.DELETE);
        phoneField.sendKeys("qwerty");

        wait.until(ExpectedConditions.textToBePresentInElementValue(phoneField, "qwerty"));
        submitButton.click();
        softAssert.assertEquals(phoneField.getAttribute("aria-invalid"), "true","The form accepted an invalid phone, using letters");

        softAssert.assertAll();
    }

}
