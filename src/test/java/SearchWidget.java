import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.Random;
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


public class SearchWidget {

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

    private void clickWithRetry(String xpath) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        int attempts = 0;
        boolean success = false;
        while (attempts < 5) {
            try {
                WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
                element.click();
                success = true;
                break;
            } catch (org.openqa.selenium.StaleElementReferenceException e) {
                attempts++;
                // Wait a bit before retrying
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        if (!success) {
            throw new org.openqa.selenium.WebDriverException("Failed to click element after multiple attempts: " + xpath);
        }
    }

    @Test
    public void Calendar() throws InterruptedException {

        driver.get("https://ancabota09.wixsite.com/intern");

        driver.manage().timeouts().implicitlyWait(Duration.of(10, ChronoUnit.SECONDS));

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement iframe = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("nKphmK")));
        driver.switchTo().frame(iframe);

        WebElement checkInField = driver.findElement(By.id("check-in"));
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
        clickWithRetry(xpath);

        driver.switchTo().defaultContent(); //back to main
        WebElement checkOutFrame = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("U73P_q")));
        driver.switchTo().frame(checkOutFrame); //to check-out frame

        LocalDate[] datesToCheck = {today, yesterday, tomorrow, threeDaysFromNow, fourDaysFromNow , fiveDaysFromNow};

        WebElement dateButtonCheckOut = null;

        for (LocalDate date : datesToCheck) {
            Thread.sleep(1000);
            String newFormattedDate = date.format(formatter);
            String newXPath = String.format("//button[@aria-label='%s']", newFormattedDate);

            WebElement dateButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(newXPath)));
            dateButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(newXPath)));
            String ariaLabel = null;
            try {
                ariaLabel = dateButton.getAttribute("aria-label");
            } catch (Exception e) {
                System.out.println("problem finding the date");
            }
            boolean isDisabled = dateButton.getAttribute("disabled") != null;// || dateButton.getAttribute("ng-disabled") != null;


            System.out.println("Button with aria-label '" + ariaLabel + "' disabled: " + isDisabled);

            if (date.isBefore(threeDaysFromNow)) {
                Assert.assertTrue(isDisabled, "Button with aria-label '" + ariaLabel + "' should be disabled but is not.");
            } else {
                Assert.assertFalse(isDisabled, "Button with aria-label '" + ariaLabel + "' should not be disabled but is.");
            }

            dateButtonCheckOut = dateButton;
        }

        //last date to check from the array is four days from now, a valid date
        if(dateButtonCheckOut != null) {
            dateButtonCheckOut.click();

        }
        else{
            System.out.println("Something went wrong with the check out button");
        }

        driver.switchTo().defaultContent();
        driver.switchTo().frame(iframe);
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.ENGLISH);

        String verifyCheckOut = driver.findElement(By.id("check-out-value")).getText();
        String verifyCheckIn = driver.findElement(By.id("check-in-value")).getText();

        Assert.assertEquals(today.format(timeFormatter),verifyCheckIn,"The date of check-in was not selected");
        Assert.assertEquals(fiveDaysFromNow.format(timeFormatter),verifyCheckOut,"The date of check-out was not selected");

    }

    @Test
    public void Adults() throws InterruptedException {


        driver.get("https://ancabota09.wixsite.com/intern");

        driver.manage().timeouts().implicitlyWait(Duration.of(10, ChronoUnit.SECONDS));

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement iframe = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("nKphmK")));
        driver.switchTo().frame(iframe);

        Random random = new Random();
        int randomClicks = 5 + random.nextInt(21);

        Thread.sleep(1000);
        WebElement adultsButtonIncr = driver.findElement(By.cssSelector("#adults > .up"));
        adultsButtonIncr.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated((By.xpath("//*[@id=\"adults\"]"))));
        Assert.assertEquals(driver.findElement(By.xpath("//*[@id=\"adults\"]")).getAttribute("aria-valuenow"), "2","Button did not increment the number of adults");

        WebElement adultsButtonDecr = driver.findElement(By.cssSelector("#adults > .down"));
        adultsButtonDecr.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated((By.xpath("//*[@id=\"adults\"]"))));
        Assert.assertEquals(driver.findElement(By.xpath("//*[@id=\"adults\"]")).getAttribute("aria-valuenow"), "1","Button did not decrement the number of adults");

        for (int i = 0; i < randomClicks; i++) {
            adultsButtonIncr.click();
        }

        wait.until(ExpectedConditions.visibilityOfElementLocated((By.xpath("//*[@id=\"adults\"]"))));
        Assert.assertEquals(driver.findElement(By.xpath("//*[@id=\"adults\"]")).getAttribute("aria-valuenow"), String.valueOf(randomClicks+1),"Button should not decrement the number of adults to 0");
        //System.out.println(driver.findElement(By.xpath("//*[@id=\"adults\"]")).getAttribute("aria-valuenow"));

        for (int i = 0; i < randomClicks*2; i++) {
            adultsButtonDecr.click();
        }
        wait.until(ExpectedConditions.visibilityOfElementLocated((By.xpath("//*[@id=\"adults\"]"))));
        Assert.assertEquals(driver.findElement(By.xpath("//*[@id=\"adults\"]")).getAttribute("aria-valuenow"), "1","Button should not decrement the number of adults to 0");


    }

    @Test
    public void Kids() throws InterruptedException {


        driver.get("https://ancabota09.wixsite.com/intern");

        driver.manage().timeouts().implicitlyWait(Duration.of(10, ChronoUnit.SECONDS));

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement iframe = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("nKphmK")));
        driver.switchTo().frame(iframe);

        Random random = new Random();
        int randomClicks = 5 + random.nextInt(21);

        Thread.sleep(1000);
        WebElement kidsButtonIncr = driver.findElement(By.cssSelector("#children > .up"));
        kidsButtonIncr.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated((By.xpath("//*[@id=\"children\"]"))));
        Assert.assertEquals(driver.findElement(By.xpath("//*[@id=\"children\"]")).getAttribute("aria-valuenow"), "1","Button did not increment the number of kids");
        //System.out.println(driver.findElement(By.xpath("//*[@id=\"adults\"]")).getAttribute("aria-valuenow"));

        WebElement kidsButtonDecr = driver.findElement(By.cssSelector("#children > .down"));
        kidsButtonDecr.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated((By.xpath("//*[@id=\"children\"]"))));
        Assert.assertEquals(driver.findElement(By.xpath("//*[@id=\"children\"]")).getAttribute("aria-valuenow"), "0","Button did not decrement the number of kids to 0");

        kidsButtonDecr.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated((By.xpath("//*[@id=\"children\"]"))));
        Assert.assertEquals(driver.findElement(By.xpath("//*[@id=\"children\"]")).getAttribute("aria-valuenow"), "0","Button should not decrement the number of kids to less than zero");

        for (int i = 0; i < randomClicks; i++) {
            kidsButtonIncr.click();
        }

        wait.until(ExpectedConditions.visibilityOfElementLocated((By.xpath("//*[@id=\"children\"]"))));
        Assert.assertEquals(driver.findElement(By.xpath("//*[@id=\"children\"]")).getAttribute("aria-valuenow"), String.valueOf(randomClicks),"Button should not decrement the number of adults to 0");
        //System.out.println(driver.findElement(By.xpath("//*[@id=\"adults\"]")).getAttribute("aria-valuenow"));

        for (int i = 0; i < randomClicks*2; i++) {
            kidsButtonDecr.click();
        }
        wait.until(ExpectedConditions.visibilityOfElementLocated((By.xpath("//*[@id=\"children\"]"))));
        Assert.assertEquals(driver.findElement(By.xpath("//*[@id=\"children\"]")).getAttribute("aria-valuenow"), "0","Button should not decrement the number of adults to 0");


    }

    @Test
    public void Search() throws InterruptedException {


        driver.get("https://ancabota09.wixsite.com/intern");

        driver.manage().timeouts().implicitlyWait(Duration.of(10, ChronoUnit.SECONDS));

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement iframe = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("nKphmK")));
        driver.switchTo().frame(iframe);

        WebElement checkInField = driver.findElement(By.id("check-in"));
        checkInField.click();

        driver.switchTo().defaultContent(); //back to main
        WebElement checkinFrame = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("U73P_q")));
        driver.switchTo().frame(checkinFrame);

        LocalDate today = LocalDate.now();
        LocalDate fiveDaysFromNow = today.plusDays(5);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d, EEEE MMMM yyyy", Locale.ENGLISH);
        String formattedDate = today.format(formatter);

        String xpath = String.format("//button[@aria-label='%s']", formattedDate);
        System.out.println(xpath);
        clickWithRetry(xpath); //check in date button to set

        driver.switchTo().defaultContent(); //back to main
        WebElement checkOutFrame = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("U73P_q")));
        driver.switchTo().frame(checkOutFrame); //to check-out frame

        formattedDate = fiveDaysFromNow.format(formatter);
        xpath = String.format("//button[@aria-label='%s']", formattedDate);
        System.out.println(xpath);
        clickWithRetry(xpath); //check out date button to set

        driver.switchTo().defaultContent();
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.xpath("//*[@id=\"i6kppi75\"]/iframe")));

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.ENGLISH);

        String verifyCheckOut = driver.findElement(By.id("check-out-value")).getText();
        String verifyCheckIn = driver.findElement(By.id("check-in-value")).getText();

        Assert.assertEquals(today.format(timeFormatter),verifyCheckIn,"The date of check-in was not selected");
        Assert.assertEquals(fiveDaysFromNow.format(timeFormatter),verifyCheckOut,"The date of check-out was not selected");

        driver.switchTo().defaultContent();
        driver.switchTo().frame(iframe);

        Random random = new Random();
        int randomClicks = 5 + random.nextInt(21);

        WebElement adultsButtonIncr = driver.findElement(By.cssSelector("#adults > .up"));

        for (int i = 0; i < randomClicks; i++) {
            adultsButtonIncr.click();
        }

        wait.until(ExpectedConditions.visibilityOfElementLocated((By.xpath("//*[@id=\"adults\"]"))));
        Assert.assertEquals(driver.findElement(By.xpath("//*[@id=\"adults\"]")).getAttribute("aria-valuenow"), String.valueOf(randomClicks+1),"Button did not increment the number of adults");

        WebElement kidsButtonIncr = driver.findElement(By.cssSelector("#children > .up"));

        for (int i = 0; i < randomClicks-4; i++) {
            kidsButtonIncr.click();
        }

        wait.until(ExpectedConditions.visibilityOfElementLocated((By.xpath("//*[@id=\"children\"]"))));
        Assert.assertEquals(driver.findElement(By.xpath("//*[@id=\"children\"]")).getAttribute("aria-valuenow"), String.valueOf(randomClicks-4),"Button did not increment the number of adults");

        String currentURL = driver.getCurrentUrl();

        driver.findElement(By.xpath("//*[@id=\"search-widget\"]/form/ul/li[6]/button/span")).click();

        Assert.assertNotEquals(currentURL,driver.getCurrentUrl(),"Search button did not redirect to rooms page");

        Thread.sleep(10000);
        driver.switchTo().defaultContent();
        iframe = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("nKphmK")));
        driver.switchTo().frame(iframe);

        Assert.assertEquals(today.format(timeFormatter),verifyCheckIn,"The date of check-in was not saved");
        Assert.assertEquals(fiveDaysFromNow.format(timeFormatter),verifyCheckOut,"The date of check-out was not saved");

        String verifyAdults = driver.findElement(By.xpath("//*[@id=\"adults\"]")).getAttribute("aria-valuenow");
        String verifyKids = driver.findElement(By.xpath("//*[@id=\"children\"]")).getAttribute("aria-valuenow");

        Assert.assertEquals(verifyAdults,String.valueOf(randomClicks+1),"The nr of adults was not saved");
        Assert.assertEquals(verifyKids,String.valueOf(randomClicks-4),"The nr of kids was not saved");


    }


}
