import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
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


public class Explore {

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
    public void explorePage() {
            SoftAssert softAssert = new SoftAssert();
            driver.get("https://ancabota09.wixsite.com/intern");

            WebElement button = driver.findElement(By.id("i6kl732v1label"));
            driver.manage().timeouts().implicitlyWait(Duration.of(10, ChronoUnit.SECONDS));

            button.click();

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.urlContains("https://ancabota09.wixsite.com/intern/explore"));

            String currentUrl = driver.getCurrentUrl();
            softAssert.assertEquals(currentUrl, "https://ancabota09.wixsite.com/intern/explore", "Bad redirect");

        WebElement brushIcon = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//wow-image[@data-image-info[contains(., '9c608a_40c6a63735ab47b096691cfd25e22168.png')]]/img")));
        WebElement cleaningServicesText = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//p[@class='font_8 wixui-rich-text__text']/span[text()='Cleaning Services']")));

        WebElement parkingIcon = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//wow-image[@data-image-info[contains(., '9c608a_faef9c2646824b4cb7d694d28e246dae.png')]]/img")));
        WebElement freeParkingText = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//p[@class='font_8 wixui-rich-text__text']/span[text()='Free Parking']")));

        WebElement lampIcon = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//wow-image[@data-image-info[contains(., '9c608a_3c58fe1f4ad24cac8823dbfb3445a4e6.png')]]/img")));
        WebElement furnishedText = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//p[@class='font_8 wixui-rich-text__text']/span[text()='Fully Furnished']")));

        WebElement wifiIcon = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//wow-image[@data-image-info[contains(., '9c608a_b504533992514b198819a54f27520449.png')]]/img")));
        WebElement wifiText = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//p[@class='font_8 wixui-rich-text__text']/span[text()='Free WiFi']")));

        WebElement airplaneIcon = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//wow-image[@data-image-info[contains(., '9c608a_b7451c0859164f889f85d82de735148e.png')]]/img")));
        WebElement airportText = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//p[@class='font_8 wixui-rich-text__text']/span[text()='Airport Transfers']")));



        softAssert.assertTrue((brushIcon.isDisplayed()), "brush icon is not displayed");
        softAssert.assertTrue(cleaningServicesText.isDisplayed(), "cleaningServices text is not displayed");

        softAssert.assertTrue((parkingIcon.isDisplayed()), "parking icon is not displayed");
        softAssert.assertTrue(freeParkingText.isDisplayed(), "parking text is not displayed");

        softAssert.assertTrue((lampIcon.isDisplayed()), "lamp icon is not displayed");
        softAssert.assertTrue(furnishedText.isDisplayed(), "furnished text is not displayed");

        softAssert.assertTrue((wifiIcon.isDisplayed()), "wifi icon is not displayed");
        softAssert.assertTrue(wifiText.isDisplayed(), "wifi text is not displayed");

        softAssert.assertTrue((airplaneIcon.isDisplayed()), "airplane icon is not displayed");
        softAssert.assertTrue(airportText.isDisplayed(), "airport text is not displayed");

        WebElement paragraph = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"i6kvh3dl\"]/p/span")));
        WebElement chinaParagraph = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"i6kv3ge8\"]/p[4]")));
        WebElement haightParagraph = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"i6kvbhmb\"]/p[4]")));
        WebElement goldenGateParagraph = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"i6kvbkw0\"]/p[4]")));

        softAssert.assertNotEquals(paragraph.getText(),"I'm a paragraph. Click here to add your own text and edit me. It’s easy. Just click “Edit Text” or double click me to add your own content and make changes to the font. Feel free to drag and drop me anywhere you like on your page. I’m a great place for you to tell a story and let your users know a little more about you.",
                "it's a default template paragraph");

        softAssert.assertNotEquals(chinaParagraph.getText(),"I'm a paragraph. Click here to add your own text and edit me. I’m a great place for you to tell a story and let your users know a little more about you.",
                "it's a default template paragraph for Chinatown");

        softAssert.assertNotEquals(haightParagraph.getText(),"I'm a paragraph. Click here to add your own text and edit me. I’m a great place for you to tell a story and let your users know a little more about you.",
                "it's a default template paragraph for Haight & Ashbury ");

        softAssert.assertNotEquals(goldenGateParagraph.getText(),"I'm a paragraph. Click here to add your own text and edit me. I’m a great place for you to tell a story and let your users know a little more about you.",
                "it's a default template paragraph for Golden Gate Bridge ");

        softAssert.assertAll();


    }



}
