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


public class RoomsPage {

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
    public void allRooms() throws InterruptedException {
        SoftAssert softAssert = new SoftAssert();
        driver.get("https://ancabota09.wixsite.com/intern");

        WebElement button = driver.findElement(By.id("i6kl732v2label"));
        driver.manage().timeouts().implicitlyWait(Duration.of(10, ChronoUnit.SECONDS));

        softAssert.assertTrue(button.isDisplayed());

        button.click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlContains("https://ancabota09.wixsite.com/intern/rooms"));

        String currentUrl = driver.getCurrentUrl();
        softAssert.assertEquals(currentUrl, "https://ancabota09.wixsite.com/intern/rooms", "Bad redirect");

        Thread.sleep(10000);

        WebElement iframe = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("nKphmK")));
        driver.switchTo().frame(iframe);

        List<WebElement> rooms = driver.findElements(By.cssSelector("li.room.s-separator"));

        for (WebElement room : rooms) {
            WebElement titleElement = room.findElement(By.cssSelector("h3 a.s-title .strans"));
            String title = titleElement.getText();

            WebElement thumbnailElement = room.findElement(By.cssSelector("img.thumbnail"));
            String thumbnailUrl = thumbnailElement.getAttribute("src");
            softAssert.assertTrue(thumbnailElement.isDisplayed(),String.format("image not displayed for the room '%s'", title));
            softAssert.assertNotNull(thumbnailUrl,String.format("image link is null for the room '%s'", title));

            WebElement sizeElement = room.findElement(By.cssSelector("li.size abbr"));
            String size = sizeElement.getText();

            WebElement descriptionElement = room.findElement(By.cssSelector("p.text .strans"));
            String description = descriptionElement.getText();

            List<WebElement> bedElements = room.findElements(By.cssSelector("li.beds span.bed .strans"));
            StringBuilder beds = new StringBuilder();
            for (WebElement bedElement : bedElements) {
                beds.append(bedElement.getText()).append(", ");
            }

            List<WebElement> amenitiesElements = room.findElements(By.cssSelector("ul.amenities.s-separator li .amenity"));
            StringBuilder amenities = new StringBuilder();
            for (WebElement amenityElement : amenitiesElements) {
                amenities.append(amenityElement.getAttribute("tooltip")).append(", ");
            }
            if (!amenities.isEmpty()) {
                amenities.deleteCharAt(amenities.lastIndexOf(", "));}
            if (!beds.isEmpty()) {
                beds.deleteCharAt(beds.lastIndexOf(", "));}

            WebElement priceElement = room.findElement(By.cssSelector(".s-separator.price .value"));
            String price = priceElement.getText();

            softAssert.assertNotNull(title,"room title does not exist");
            softAssert.assertNotNull(size,String.format("size field does not exist for room '%s'", title));
            softAssert.assertTrue(description.length() > 5,String.format("description not found or too short for the room '%s'", title));
            softAssert.assertTrue(beds.length()>1,String.format("room '%s' does not have beds", title));
            softAssert.assertTrue( Integer.parseInt(price.replaceAll("[^\\d]", "")) > 1,String.format("price too low, probably a mistake 😁 '%s'", title));
            softAssert.assertNotNull(amenities,String.format("room does not have any amenities set '%s'", title));

            System.out.println("Room Title: " + title);
            System.out.println("Description: " + description);
            System.out.println("Image url: " + thumbnailUrl);
            System.out.println("Size: " + size);
            System.out.println("Beds: " + beds);
            System.out.println("Price: " + price);
            System.out.println("Amenities: " + amenities);
            System.out.println("------------------------");
        }


        softAssert.assertAll();


    }

    @Test
    public void accomodatesFilter() throws InterruptedException {
        SoftAssert softAssert = new SoftAssert();
        driver.get("https://ancabota09.wixsite.com/intern");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        WebElement button = driver.findElement(By.id("i6kl732v2label"));
        button.click();

        int maxAdults = getMaxAccomodates();
        //int maxAdults = 6;

        driver.get("https://ancabota09.wixsite.com/intern/rooms");
        driver.switchTo().defaultContent();
        WebElement iframe = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("nKphmK")));
        driver.switchTo().frame(iframe);

        Thread.sleep(5000);

        driver.findElement(By.cssSelector("#check-in > .calendar-button")).click();

        LocalDate today = LocalDate.now();
        LocalDate fiveDaysFromNow = today.plusDays(5);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d, EEEE MMMM yyyy", Locale.ENGLISH);
        String formattedDate = today.format(formatter);

        String xpath = String.format("//button[@aria-label='%s']", formattedDate);
        System.out.println(xpath);
        clickWithRetry(xpath); //check in date button to set

        driver.switchTo().defaultContent(); //back to main
        iframe = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("nKphmK")));
        driver.switchTo().frame(iframe);

        Thread.sleep(3000);
        formattedDate = fiveDaysFromNow.format(formatter);
        xpath = String.format("//button[@aria-label='%s']", formattedDate);
        System.out.println(xpath);
        //clickWithRetry(xpath); //check out date button to set
        driver.findElements(By.xpath(xpath)).get(1).click();

        driver.switchTo().defaultContent();
        iframe = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("nKphmK")));
        driver.switchTo().frame(iframe);
        WebElement adultsButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"adults\"]")));


        WebElement adultsButtonIncr = driver.findElement(By.cssSelector("#adults > .up"));

        for (int i = 0; i < maxAdults+1; i++) {
            adultsButtonIncr.click();
        }

        driver.findElement(By.cssSelector("ul li.search-btn button.search.s-button")).click();
        Thread.sleep(3000);
        List<WebElement> rooms = driver.findElements(By.cssSelector("li.room.s-separator"));

        softAssert.assertTrue(rooms.isEmpty(),"No rooms and try another search should have been displayed");



        WebElement adultsButtonDecr = driver.findElement(By.cssSelector("#adults > .down"));

        for (int i = 0; i < maxAdults; i++) {
            adultsButtonDecr.click();
        }

        driver.findElement(By.cssSelector("ul li.search-btn button.search.s-button")).click();
        Thread.sleep(3000);
        rooms = driver.findElements(By.cssSelector("li.room.s-separator"));

        softAssert.assertFalse(rooms.isEmpty(),"There are rooms available with the current number of adults, but they were not found");


        //search with kids
        WebElement kidsButtonIncr = driver.findElement(By.cssSelector("#children > .up"));
        kidsButtonIncr.click();


        driver.findElement(By.cssSelector("ul li.search-btn button.search.s-button")).click();
        Thread.sleep(3000);
        rooms = driver.findElements(By.cssSelector("li.room.s-separator"));

        softAssert.assertFalse(rooms.isEmpty(),"There are rooms available with the current number of accomodates, but they were not found");



        softAssert.assertAll();

    }

    @Test
    public void roomsDetails() throws InterruptedException {
        SoftAssert softAssert = new SoftAssert();
        driver.get("https://ancabota09.wixsite.com/intern");

        WebElement button = driver.findElement(By.id("i6kl732v2label"));
        driver.manage().timeouts().implicitlyWait(Duration.of(10, ChronoUnit.SECONDS));

        softAssert.assertTrue(button.isDisplayed());

        button.click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlContains("https://ancabota09.wixsite.com/intern/rooms"));

        String currentUrl = driver.getCurrentUrl();
        softAssert.assertEquals(currentUrl, "https://ancabota09.wixsite.com/intern/rooms", "Bad redirect");

        Thread.sleep(10000);

        WebElement iframe = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("nKphmK")));
        driver.switchTo().frame(iframe);

        List<WebElement> rooms = driver.findElements(By.cssSelector("li.room.s-separator"));

        for (int i = 0; i < rooms.size(); i++) {
            // because DOM changes it needs to re-fetch the list of rooms each time
            rooms = driver.findElements(By.cssSelector("li.room.s-separator"));
            WebElement room = rooms.get(i);

            WebElement titleElement = room.findElement(By.cssSelector("h3 a.s-title .strans"));
            String title = titleElement.getText();

            WebElement roomPageButton = room.findElement(By.cssSelector("button.fancy-btn.s-button"));
            roomPageButton.click();

            Thread.sleep(5000);
            driver.switchTo().defaultContent();
            iframe = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("nKphmK")));
            driver.switchTo().frame(iframe);

            WebElement roomTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("h2.s-title span")));


            System.out.println("Room Title: " + title);
            System.out.println("Room Title on the more info page: " + roomTitle.getText());
            System.out.println("------------------------");


            softAssert.assertEquals(title, roomTitle.getText(), "The room page opened did not correspond with what the user clicked");

            driver.get("https://ancabota09.wixsite.com/intern/rooms");
            iframe = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("nKphmK")));
            driver.switchTo().frame(iframe);

            Thread.sleep(5000);


        }
        softAssert.assertAll();
    }

    public int getMaxAccomodates() throws InterruptedException {

        driver.get("https://ancabota09.wixsite.com/intern/rooms");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        Thread.sleep(10000);

        WebElement iframe = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("nKphmK")));
        driver.switchTo().frame(iframe);

        int maxAccommodates = 0;

        List<WebElement> rooms = driver.findElements(By.cssSelector("li.room.s-separator"));

        for (int i = 0; i < rooms.size(); i++) {
            // because DOM changes it needs to re-fetch the list of rooms each time
            rooms = driver.findElements(By.cssSelector("li.room.s-separator"));
            WebElement room = rooms.get(i);

            WebElement titleElement = room.findElement(By.cssSelector("h3 a.s-title .strans"));
            String title = titleElement.getText();

            WebElement roomPageButton = room.findElement(By.cssSelector("button.fancy-btn.s-button"));
            roomPageButton.click();

            driver.switchTo().defaultContent();
            iframe = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("nKphmK")));
            driver.switchTo().frame(iframe);

            WebElement roomAccommodates = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("li.accomodates > span[ng-bind='room.maxPersons']")));
            String accommodatesText = roomAccommodates.getText();

            int accommodates = Integer.parseInt(accommodatesText);
            if (accommodates > maxAccommodates) {
                maxAccommodates = accommodates;
            }

            System.out.println("Room Title: " + title);
            System.out.println("Room accommodates: " + accommodates);
            System.out.println("------------------------");


            driver.get("https://ancabota09.wixsite.com/intern/rooms");
            iframe = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("nKphmK")));
            driver.switchTo().frame(iframe);

            Thread.sleep(5000);
        }

        return maxAccommodates;

    }



}
