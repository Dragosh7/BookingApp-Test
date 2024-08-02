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


public class Calendar {

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
    public void CheckOutAfter3Days() {


        driver.get("https://ancabota09.wixsite.com/intern");

        driver.manage().timeouts().implicitlyWait(Duration.of(10, ChronoUnit.SECONDS));

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement iframe = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("nKphmK")));
        // i frame       search widget id : i6kppi75
        driver.switchTo().frame(iframe);

        driver.findElement(By.id("check-in")).click();
        WebElement checkInField = driver.findElement(By.id("check-in"));

        WebElement checkOutField = driver.findElement(By.id("check-out"));

        checkInField.click();

        driver.switchTo().defaultContent(); //back to main
        WebElement checkinFrame = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("U73P_q")));
        driver.switchTo().frame(checkinFrame);

        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        LocalDate tomorrow = today.plusDays(1);
        LocalDate threeDaysFromNow = today.plusDays(3);

        LocalDate fourDaysFromNow = today.plusDays(4);
        LocalDate fiveDaysFromNow = today.plusDays(5);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d, EEEE MMMM yyyy", Locale.ENGLISH);
        String formattedDate = today.format(formatter);

        String xpath = String.format("//button[@aria-label='%s']", formattedDate);
        System.out.println(xpath);
        WebElement dateButtonCheckIn = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
        dateButtonCheckIn.click();

        /*driver.switchTo().frame(iframe);
        checkOutField.click();*/

        driver.switchTo().defaultContent(); //back to main
        WebElement checkOutFrame = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("U73P_q")));
        driver.switchTo().frame(checkOutFrame);


        LocalDate[] datesToCheck = {today, yesterday, tomorrow, threeDaysFromNow, fourDaysFromNow, fiveDaysFromNow};

        for (LocalDate date : datesToCheck) {
            String newFormattedDate = date.format(formatter);
            String newXPath = String.format("//button[@aria-label='%s']", newFormattedDate);

            WebElement dateButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(newXPath)));
            String ariaLabel = dateButton.getAttribute("aria-label");
            boolean isDisabled = dateButton.getAttribute("disabled") != null || dateButton.getAttribute("ng-disabled") != null;

            System.out.println("Button with aria-label '" + ariaLabel + "' disabled: " + isDisabled);

            if (date.isBefore(threeDaysFromNow)) {
                Assert.assertTrue(isDisabled, "Button with aria-label '" + ariaLabel + "' should be disabled but is not.");
            } else {
                Assert.assertFalse(isDisabled, "Button with aria-label '" + ariaLabel + "' should not be disabled but is.");
            }
        }




    }



}
