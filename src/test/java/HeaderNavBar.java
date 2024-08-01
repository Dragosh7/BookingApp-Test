import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;
//--
import io.opentelemetry.exporter.logging.SystemOutLogRecordExporter;
import org.openqa.selenium.By;
//--
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


public class HeaderNavBar {

    private WebDriver driver;

    @BeforeClass
    public void beforeClass() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-search-engine-choice-screen");
        // options.addArguments("--remote-allow-origins=*");
        driver = new ChromeDriver(options);
    }

    @AfterClass
    public void afterClass() throws InterruptedException{

        Thread.sleep(1000);
        driver.quit();

    }

    @Test
    public void verifyBookNowButton() {


        driver.get("https://ancabota09.wixsite.com/intern");

        String search_text = "BOOK NOW";
        WebElement button = driver.findElement(By.xpath("//*[@id=\"i6tj0u8x\"]/a"));

        String text = button.getText();

        Assert.assertEquals(text, search_text, "Text not correct!");

        driver.manage().timeouts().implicitlyWait(Duration.of(10, ChronoUnit.SECONDS));

        button.click();
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.urlContains("https://ancabota09.wixsite.com/booknow"));
        }
        catch (Exception e) {
            System.out.println("Error. Not redirected correctly.: " + e.getMessage());
        }
        String currentUrl = driver.getCurrentUrl();
        Assert.assertEquals(currentUrl, "https://ancabota09.wixsite.com/booknow", "Bad redirect");

     }

    @Test
    public void verifyExploreButton() {


        driver.get("https://ancabota09.wixsite.com/intern");

        String search_text = "EXPLORE";
        WebElement button = driver.findElement(By.id("i6kl732v1label"));

        String text = button.getText();

        Assert.assertEquals(text, search_text, "Text not correct!");

        driver.manage().timeouts().implicitlyWait(Duration.of(10, ChronoUnit.SECONDS));

        button.click();
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.urlContains("https://ancabota09.wixsite.com/intern/explore"));
        }
        catch (Exception e) {
            System.out.println("Error. Not redirected correctly.: " + e.getMessage());
        }
        String currentUrl = driver.getCurrentUrl();
        Assert.assertEquals(currentUrl, "https://ancabota09.wixsite.com/intern/explore", "Bad redirect");

    }

    @Test
    public void verifyRoomsButton() {


        driver.get("https://ancabota09.wixsite.com/intern");

        String search_text = "ROOMS";
        WebElement button = driver.findElement(By.id("i6kl732v2label"));

        String text = button.getText();

        Assert.assertEquals(text, search_text, "Text not correct!");

        driver.manage().timeouts().implicitlyWait(Duration.of(10, ChronoUnit.SECONDS));

        button.click();
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.urlContains("https://ancabota09.wixsite.com/intern/rooms"));
        }
        catch (Exception e) {
            System.out.println("Error. Not redirected correctly.: " + e.getMessage());
        }
        String currentUrl = driver.getCurrentUrl();
        Assert.assertEquals(currentUrl, "https://ancabota09.wixsite.com/intern/rooms", "Bad redirect");

    }

    @Test
    public void verifyContactButton() {


        driver.get("https://ancabota09.wixsite.com/intern");

        String search_text = "CONTACT";
        WebElement button = driver.findElement(By.id("i6kl732v3label"));

        String text = button.getText();

        Assert.assertEquals(text, search_text, "Text not correct!");

        driver.manage().timeouts().implicitlyWait(Duration.of(10, ChronoUnit.SECONDS));

        button.click();
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.urlContains("https://ancabota09.wixsite.com/intern/contact"));
        }
        catch (Exception e) {
            System.out.println("Error. Not redirected correctly.: " + e.getMessage());
        }
        String currentUrl = driver.getCurrentUrl();
        Assert.assertEquals(currentUrl, "https://ancabota09.wixsite.com/intern/contact", "Bad redirect");

    }

}
