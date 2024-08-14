import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
//--
import org.openqa.selenium.By;
//--
import org.openqa.selenium.WebDriver;
//--
import org.openqa.selenium.WebElement;
//--
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
//--
import org.openqa.selenium.interactions.Actions;
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
    public void afterClass() throws InterruptedException {

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
    public void viewAllRooms() throws InterruptedException {
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
            softAssert.assertTrue(thumbnailElement.isDisplayed(), String.format("image not displayed for the room '%s'", title));
            softAssert.assertNotNull(thumbnailUrl, String.format("image link is null for the room '%s'", title));

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
                amenities.deleteCharAt(amenities.lastIndexOf(", "));
            }
            if (!beds.isEmpty()) {
                beds.deleteCharAt(beds.lastIndexOf(", "));
            }

            WebElement priceElement = room.findElement(By.cssSelector(".s-separator.price .value"));
            String price = priceElement.getText();

            softAssert.assertNotNull(title, "room title does not exist");
            softAssert.assertNotNull(size, String.format("size field does not exist for room '%s'", title));
            softAssert.assertTrue(description.length() > 5, String.format("description not found or too short for the room '%s'", title));
            softAssert.assertTrue(beds.length() > 1, String.format("room '%s' does not have beds", title));
            softAssert.assertTrue(Integer.parseInt(price.replaceAll("[^\\d]", "")) > 1, String.format("price too low, probably a mistake 😁 '%s'", title));
            softAssert.assertNotNull(amenities, String.format("room does not have any amenities set '%s'", title));

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

        for (int i = 0; i < maxAdults + 1; i++) {
            adultsButtonIncr.click();
        }

        driver.findElement(By.cssSelector("ul li.search-btn button.search.s-button")).click();
        Thread.sleep(3000);
        List<WebElement> rooms = driver.findElements(By.cssSelector("li.room.s-separator"));

        softAssert.assertTrue(rooms.isEmpty(), "No rooms and try another search should have been displayed");


        WebElement adultsButtonDecr = driver.findElement(By.cssSelector("#adults > .down"));

        for (int i = 0; i < maxAdults; i++) {
            adultsButtonDecr.click();
        }

        driver.findElement(By.cssSelector("ul li.search-btn button.search.s-button")).click();
        Thread.sleep(3000);
        rooms = driver.findElements(By.cssSelector("li.room.s-separator"));

        softAssert.assertFalse(rooms.isEmpty(), "There are rooms available with the current number of adults, but they were not found");


        //search with kids
        WebElement kidsButtonIncr = driver.findElement(By.cssSelector("#children > .up"));
        kidsButtonIncr.click();


        driver.findElement(By.cssSelector("ul li.search-btn button.search.s-button")).click();
        Thread.sleep(3000);
        rooms = driver.findElements(By.cssSelector("li.room.s-separator"));

        softAssert.assertFalse(rooms.isEmpty(), "There are rooms available with the current number of accomodates, but they were not found");


        softAssert.assertAll();

    }

    @Test
    public void roomsLinkPage() throws InterruptedException {
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

    @Test
    public void bookRoom() throws InterruptedException {
        SoftAssert softAssert = new SoftAssert();
        driver.get("https://ancabota09.wixsite.com/intern");

        WebElement button = driver.findElement(By.id("i6kl732v2label"));
        driver.manage().timeouts().implicitlyWait(Duration.of(10, ChronoUnit.SECONDS));

        Assert.assertTrue(button.isDisplayed());
        button.click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlContains("https://ancabota09.wixsite.com/intern/rooms"));

        String currentUrl = driver.getCurrentUrl();
        Assert.assertEquals(currentUrl, "https://ancabota09.wixsite.com/intern/rooms", "Bad redirect");

        Thread.sleep(10000);

        WebElement iframe = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("nKphmK")));
        driver.switchTo().frame(iframe);

        List<WebElement> rooms = driver.findElements(By.cssSelector("li.room.s-separator"));
        Actions actions = new Actions(driver);

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
            iframe = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"i6klgqap_0\"]/iframe")));
            driver.switchTo().frame(iframe);

            WebElement roomAccommodates = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("li.accomodates > span[ng-bind='room.maxPersons']")));
            String accommodatesText = roomAccommodates.getText();
            int accommodates = Integer.parseInt(accommodatesText);

            driver.findElement(By.id("check-in")).click();

            LocalDate today = LocalDate.now();
            LocalDate fiveDaysFromNow = today.plusDays(5);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d, EEEE MMMM yyyy", Locale.ENGLISH);

            WebElement dateButtonCheckIn = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(String.format("//button[@aria-label='%s']", today.format(formatter)))));
            dateButtonCheckIn.click();

            Thread.sleep(5000);


            List<WebElement> dateButtonCheckOut = driver.findElements(By.xpath(String.format("//button[@aria-label='%s']", fiveDaysFromNow.format(formatter))));
            dateButtonCheckOut.get(1).click();

            WebElement adultsButtonIncr = driver.findElement(By.cssSelector("#adults > .up"));
            if (accommodates > 2) {
                adultsButtonIncr.click();
            }

            WebElement bookNowBtn = driver.findElement(By.cssSelector("div button.fancy-btn.s-button.button"));
            softAssert.assertTrue(bookNowBtn.isDisplayed());

            //less adults than max capacity
            actions.moveToElement(bookNowBtn).perform();
            WebElement tooltip = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".tooltip.left.in.fade")));

            String tooltipText = tooltip.getText();
            softAssert.assertEquals(tooltipText, "Please contact the hotel directly to book your room.", "Tooltip text is incorrect!");

            Thread.sleep(2000);
            adultsButtonIncr = driver.findElement(By.cssSelector("#adults > .up"));
            while (accommodates >= 2) {
                adultsButtonIncr.click();
                accommodates--;
            }

            bookNowBtn = driver.findElement(By.cssSelector("div button.fancy-btn.s-button.button"));
            softAssert.assertTrue(bookNowBtn.isDisplayed());

            //selected adults at max capacity
            actions.moveToElement(bookNowBtn).perform();
            tooltip = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".tooltip.left.in.fade")));

            tooltipText = tooltip.getText();
            softAssert.assertEquals(tooltipText, "Please contact the hotel directly to book your room.", "Tooltip text is incorrect!");

            System.out.println("Room Title: " + title);
            System.out.println("------------------------");


            driver.get("https://ancabota09.wixsite.com/intern/rooms");
            iframe = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("nKphmK")));
            driver.switchTo().frame(iframe);

            Thread.sleep(5000);


        }
        softAssert.assertAll();
    }

    @Test
    public void roomsDetails() throws InterruptedException {
        SoftAssert softAssert = new SoftAssert();
        driver.get("https://ancabota09.wixsite.com/intern");

        WebElement button = driver.findElement(By.id("i6kl732v2label"));
        driver.manage().timeouts().implicitlyWait(Duration.of(10, ChronoUnit.SECONDS));

        Assert.assertTrue(button.isDisplayed());

        button.click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlContains("https://ancabota09.wixsite.com/intern/rooms"));

        String currentUrl = driver.getCurrentUrl();
        Assert.assertEquals(currentUrl, "https://ancabota09.wixsite.com/intern/rooms", "Bad redirect");

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

            WebElement roomTitle = null, roomAccommodates = null, size = null, image = null, contentBlock = null,
                    checkInTimeElement = null, checkOutTimeElement = null, featuresSection = null,
                    policies = null, bedsElement = null, desc = null, price = null, amenitiesDiv = null, terms = null, termsDays = null;
            List<WebElement> bedElements = null, amenitiesElements = null;
            StringBuilder beds = new StringBuilder();
            StringBuilder amenities = new StringBuilder();
            String checkInTime = "", checkOutTime = "";
            try {
                roomTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("h2.s-title span")));
            } catch (Exception e) {
                System.out.println("Room title not found: " + e.getMessage());
            }
            try {
                price = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.heading.s-separator > span.price")));
            } catch (Exception e) {
                System.out.println("Room price not found: " + e.getMessage());
            }
            try {
                amenitiesDiv = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.content-block.amenities.s-separator.clearfix")));
                amenitiesElements = amenitiesDiv.findElements(By.cssSelector("li.amenity .amenity-text span"));
                amenities = new StringBuilder();
                //System.out.println("Amenities: " + amenitiesElements.toString());
                //System.out.println(amenitiesDiv.getAttribute("innerHTML"));
                for (WebElement amenityElement : amenitiesElements) {
                    //amenities.append(amenityElement.getAttribute("aria-label")).append(", ");
                    amenities.append(amenityElement.getText()).append(", ");

                }
                if (!amenities.isEmpty()) {
                    amenities.deleteCharAt(amenities.lastIndexOf(", "));
                }
            } catch (Exception e) {
                System.out.println("Room amenities not found: " + e.getMessage());
            }

            try {
                roomAccommodates = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("li.accomodates > span[ng-bind='room.maxPersons']")));
            } catch (Exception e) {
                System.out.println("Room accommodates not found: " + e.getMessage());
            }

            try {
                desc = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.content-block.s-separator.description.s-description > p")));
            } catch (Exception e) {
                System.out.println("Room description not found: " + e.getMessage());
            }

            try {
                size = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("li.size > abbr")));
            } catch (Exception e) {
                System.out.println("Room size not found: " + e.getMessage());
            }

            try {
                //image = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.preview.single img")));
                // Updated CSS Selector to target the visible image
                image = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.preview.image.visible img")));

            } catch (Exception e) {
                System.out.println("Room image not found: " + e.getMessage());
            }

            try {
                contentBlock = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".content-block.s-separator.terms.clearfix")));
                checkInTimeElement = contentBlock.findElement(By.xpath(".//li[span[@stranslate='rooms.widget.CHECK_IN']]//span[@stranslate and not(@stranslate='rooms.widget.CHECK_IN')]"));
                checkInTime = checkInTimeElement.getText().trim();
                checkOutTimeElement = contentBlock.findElement(By.xpath(".//li[span[@stranslate='rooms.widget.CHECK_OUT']]//span[@stranslate and not(@stranslate='rooms.widget.CHECK_OUT')]"));
                checkOutTime = checkOutTimeElement.getText().trim();
            } catch (Exception e) {
                System.out.println("Check-In/Check-Out time not found: " + e.getMessage());
            }

            try {
                policies = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("li.policy-link a.policies")));
            } catch (Exception e) {
                System.out.println("Policies link not found: " + e.getMessage());
            }
            try {
                terms = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.min-stay span.strans")));
                termsDays = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.min-stay span[ng-bind='hotel.minimumStay']")));
            } catch (Exception e) {
                System.out.println("Terms not found: " + e.getMessage());
            }

            try {
                featuresSection = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".features")));
                bedsElement = featuresSection.findElement(By.cssSelector(".beds"));
                bedElements = bedsElement.findElements(By.cssSelector("span.bed"));
                beds = new StringBuilder();
                for (WebElement bedElement : bedElements) {
                    beds.append(bedElement.getText()).append(" ");
                }
            } catch (Exception e) {
                System.out.println("Beds information not found: " + e.getMessage());
            }

            softAssert.assertTrue(roomAccommodates != null && roomAccommodates.isDisplayed(),
                    "Room Title: " + (roomTitle != null ? roomTitle.getText() : "Unknown") + " - The number of accommodates is not shown");
            softAssert.assertTrue(price != null && price.isDisplayed(),
                    "Room Title: " + (roomTitle != null ? roomTitle.getText() : "Unknown") + " - The price is not shown");
            softAssert.assertTrue(size != null && size.isDisplayed(),
                    "Room Title: " + (roomTitle != null ? roomTitle.getText() : "Unknown") + " - The size is not shown");
            softAssert.assertTrue(!beds.toString().isEmpty(),
                    "Room Title: " + (roomTitle != null ? roomTitle.getText() : "Unknown") + " - The beds are not shown");
            softAssert.assertTrue(desc != null && desc.isDisplayed(),
                    "Room Title: " + (roomTitle != null ? roomTitle.getText() : "Unknown") + " - The description is not shown");
            softAssert.assertTrue(amenitiesElements != null && !amenities.isEmpty(),
                    "Room Title: " + (roomTitle != null ? roomTitle.getText() : "Unknown") + " - The amenities are not shown");
            softAssert.assertTrue(image != null && image.isDisplayed(),
                    "Room Title: " + (roomTitle != null ? roomTitle.getText() : "Unknown") + " - The image is not shown");
            softAssert.assertFalse(checkInTime.isEmpty(),
                    "Room Title: " + (roomTitle != null ? roomTitle.getText() : "Unknown") + " - Check-In time is not shown");
            softAssert.assertFalse(checkOutTime.isEmpty(),
                    "Room Title: " + (roomTitle != null ? roomTitle.getText() : "Unknown") + " - Check-Out time is not shown");
            softAssert.assertTrue(terms != null && terms.isDisplayed(),
                    "Room Title: " + (roomTitle != null ? roomTitle.getText() : "Unknown") + " - Terms are not shown");
            softAssert.assertTrue(termsDays != null && termsDays.isDisplayed(),
                    "Room Title: " + (roomTitle != null ? roomTitle.getText() : "Unknown") + " - Terms days are not shown");
            softAssert.assertTrue(policies != null && policies.isDisplayed(),
                    "Room Title: " + (roomTitle != null ? roomTitle.getText() : "Unknown") + " - The read policy link is not shown");

            if (roomTitle != null) {
                System.out.println("Room Title: " + roomTitle.getText());
            }
            if (price != null) {
                System.out.println("Room Price: " + price.getText());
            }
            if (roomAccommodates != null) {
                System.out.println("Room Accommodates: " + Integer.parseInt(roomAccommodates.getText()));
            }
            if (size != null) {
                System.out.println("Room Size: " + size.getText() + "sq m");
            }
            if (!beds.isEmpty()) {
                System.out.println("Room Beds: " + beds);
            }
            if (!amenities.isEmpty()) {
                System.out.println("Room Amenities: " + amenities);
            }
            if (desc != null) {
                System.out.println("Room Info: " + desc.getText());
            }
            if (image != null) {
                System.out.println("Room Image Link: " + image.getAttribute("src"));
            }
            if (checkInTime != null) {
                System.out.println("Room Check In: " + checkInTime);
            }
            if (checkOutTime != null) {
                System.out.println("Room Check Out: " + checkOutTime);
            }
            if (terms != null && termsDays != null) {
                System.out.println("Room Terms: " + terms.getText() + ": " + termsDays.getText());
            }
            if (policies != null) {
                System.out.println("Room Policies: " + policies.getAttribute("href"));
            }
            System.out.println("------------------------");


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
