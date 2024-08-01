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


public class Footer {

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
    public void verifyAddressInfo() {


        driver.get("https://ancabota09.wixsite.com/intern");

        driver.manage().timeouts().implicitlyWait(Duration.of(10, ChronoUnit.SECONDS));

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        WebElement footer = wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("footer")));
        footer.findElement(By.xpath("//*[.='ADDRESS']"));
        WebElement addressContent = footer.findElement(By.id("i71wvfxg"));

        Assert.assertEquals(addressContent.getText(), "500 Terry Francois Street\n" +
                "San Francisco, CA 94158", "Text not correct!");
        /*
        if (addressContent.isDisplayed()) {
            System.out.println("Address is displayed: " + addressContent.getText());
        } else {
            System.out.println("Address is not displayed.");
        }

         */
    }

    @Test
    public void verifyContactInfo() {


        driver.get("https://ancabota09.wixsite.com/intern");

        driver.manage().timeouts().implicitlyWait(Duration.of(10, ChronoUnit.SECONDS));

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        WebElement footer = wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("footer")));
        footer.findElement(By.xpath("//*[.='CONTACT']"));
        WebElement addressContent = footer.findElement(By.id("i71ww6nk"));

        Assert.assertEquals(addressContent.getText(), "info@mysite.com\nTel: 123-456-7890", "Text not correct!");
    }

    @Test
    public void verifyBrandInfo() {


        driver.get("https://ancabota09.wixsite.com/intern");

        driver.manage().timeouts().implicitlyWait(Duration.of(10, ChronoUnit.SECONDS));

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        WebElement footer = wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("footer")));
        footer.findElement(By.xpath("//*[.='HOME & AWAY']"));
        WebElement addressContent = footer.findElement(By.id("i71wwqnj"));

        Assert.assertEquals(addressContent.getText(), "© 2023 by HOME & AWAY\n" +
                "Proudly created with Wix.com", "Text not correct!");

        String originalWindow = driver.getWindowHandle();

        WebElement wixLink = driver.findElement(By.xpath("//*[@id=\"i71wwqnj\"]/p[2]/span/a"));
        wixLink.click();

        wait.until(ExpectedConditions.numberOfWindowsToBe(2));
        //driver.switchTo().window(wixLink.getAttribute("href"));

        // first method but its not actually testing the redirect
        Assert.assertEquals(wixLink.getAttribute("href"), "http://wix.com/?utm_campaign=vir_created_with", "Bad Redirect");


        for (String windowHandle : driver.getWindowHandles()) {
            if (!windowHandle.equals(originalWindow)) {
                driver.switchTo().window(windowHandle);
                break;
            }
        }
        //second method but slower if I would have to iterate through a lot of windows
        Assert.assertEquals(driver.getCurrentUrl(), "https://www.wix.com/?utm_campaign=vir_created_with", "Bad Redirect");

    }


    @Test
    public void verifyPayInfo() {


        driver.get("https://ancabota09.wixsite.com/intern");

        driver.manage().timeouts().implicitlyWait(Duration.of(10, ChronoUnit.SECONDS));

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        WebElement footer = wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("footer")));
        footer.findElement(By.xpath("//*[.='WE ACCEPT']"));

        WebElement galleryContainer = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".QF4b9S.gN7JUw")));
        List<WebElement> galleryItems = galleryContainer.findElements(By.cssSelector(".YHQ52U.R7VW51"));

        Assert.assertFalse(galleryItems.isEmpty(), "No images found in the gallery");

        for (WebElement item : galleryItems) {
            WebElement image = item.findElement(By.cssSelector("img"));
            Assert.assertNotNull(image, "Image not found in gallery item");

            String src = image.getAttribute("src");
            Assert.assertTrue(src.startsWith("https://"), "Image URL is not shown: " + src);


    }}

}
