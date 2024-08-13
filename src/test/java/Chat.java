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


public class Chat {

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
    public void chatRoom() throws InterruptedException {
        SoftAssert softAssert = new SoftAssert();
        driver.get("https://ancabota09.wixsite.com/intern");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().timeouts().implicitlyWait(Duration.of(10, ChronoUnit.SECONDS));

        //wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.className("nKphmK")));
        Thread.sleep(2000);
        WebElement chatElement = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"comp-jr4sqg2g\"]/iframe")));
        driver.switchTo().frame(chatElement);

        WebElement button = driver.findElement(By.xpath("//*[@id=\"minimized-chat\"]"));
        button.click();

        Thread.sleep(2000);
        WebElement textField = driver.findElement(By.cssSelector("div.iQNgH textarea"));
        textField.sendKeys("Hello World");
        Thread.sleep(2000);
        WebElement send = driver.findElement(By.cssSelector("div.iQNgH button.sk1yM.S66IV"));
        softAssert.assertTrue(send.isDisplayed(),"The message was not typed, therefore the button is not displayed");
        send.click();
        Thread.sleep(2000);

        WebElement receivedMessage = driver.findElement(By.xpath("//*[@id=\"chat-messages-list\"]/div[3]/div/div/div/div/div/div/div/div/div[1]/div/div/div/div/div/div/div"));
        softAssert.assertTrue(receivedMessage.isDisplayed());


        softAssert.assertAll();


    }



}
