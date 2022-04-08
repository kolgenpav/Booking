package ua.edu.znu.booking;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Test of input data entering.
 */
public class InputTest {

    private static AndroidDriver driver;

    @BeforeAll
    public static void setUp() throws MalformedURLException {

        UiAutomator2Options options = new UiAutomator2Options()
                .setDeviceName("Android10Phone")
                .setApp(System.getProperty("user.dir") + "\\apps\\Booking_31.2.apk")
                .eventTimings();
        URL remoteUrl = new URL("http://127.0.0.1:4723/wd/hub");
        driver = new AndroidDriver(remoteUrl, options);
    }

    @Test
    public void sampleTest() {
        String accommodationDestination = "Лондон";
        String expectedRoomAndGuestNumber = "2 номери " + '\u00b7' + " 3 дорослих " + '\u00b7' + " 1 дитина";

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        /*Close registration window*/
        WebElement registrationCloseButton = getElementWithWait(wait, By.xpath("//android.widget.ImageButton[@content-desc=\"Перейти вгору\"]"));
        registrationCloseButton.click();

        /*Accommodation destination input*/
        WebElement accommodationDestinationField = getElementWithWait(wait, By.id("com.booking:id/facet_search_box_accommodation_destination"));
        accommodationDestinationField.click();
        WebElement accommodationDestinationInput = getElementWithWait(wait, By.id("com.booking:id/facet_with_bui_free_search_booking_header_toolbar_content"));
        accommodationDestinationInput.sendKeys(accommodationDestination);
        WebElement accommodationDestinationPropose = getElementWithWait(wait, By.xpath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/androidx.recyclerview.widget.RecyclerView/android.view.ViewGroup[1]"));
        accommodationDestinationPropose.click();

        /*Accommodation dates input*/
        /*Implicit wait before calendar appearing*/
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
        LocalDate currentDate = LocalDate.now();
        String currentDateString = currentDate.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"));
        WebElement startDate = driver.findElement(AppiumBy.accessibilityId(currentDateString));
        startDate.click();
        LocalDate currentDatePlus5 = currentDate.plusDays(5);
        String currentDatePlus5String = currentDatePlus5.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"));
        WebElement endDate = driver.findElement(AppiumBy.accessibilityId(currentDatePlus5String));
        endDate.click();
        WebElement accommodationDatesConfirmButton = getElementWithWait(wait, By.id("com.booking:id/facet_date_picker_confirm"));
        accommodationDatesConfirmButton.click();

        /*Occupancy input*/
        WebElement accommodationOccupancyField = getElementWithWait(wait, By.id("com.booking:id/facet_search_box_accommodation_occupancy"));
        accommodationOccupancyField.click();
        WebElement adultsNumberInput = getElementWithWait(wait, By.xpath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.view.ViewGroup/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.LinearLayout/android.widget.LinearLayout[2]/android.widget.LinearLayout[2]/android.widget.TextView[3]"));
        adultsNumberInput.click();
        WebElement childrenNumberInput = getElementWithWait(wait, By.xpath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.view.ViewGroup/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.LinearLayout/android.widget.LinearLayout[3]/android.widget.LinearLayout[2]/android.widget.TextView[3]"));
        childrenNumberInput.click();
        /*Child age NumberPicker swipe*/
        WebElement source = getElementWithWait(wait, By.xpath("/hierarchy/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.NumberPicker"));
        Point numberPickerLocation = source.getLocation();   //99,433
        Point swipeStart = numberPickerLocation.moveBy(270, 350);   //370,780
        Point swipeEnd = numberPickerLocation.moveBy(270, 100);     //370,475
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipeAge = new Sequence(finger, 1);
        swipeAge.addAction(finger.createPointerMove(Duration.ofMillis(0), PointerInput.Origin.viewport(), swipeStart.x, swipeStart.y));
        swipeAge.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        swipeAge.addAction(finger.createPointerMove(Duration.ofMillis(700), PointerInput.Origin.viewport(), swipeEnd.x, swipeEnd.y));
        swipeAge.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(List.of(swipeAge));

        WebElement childAgeInputConfirmButton = getElementWithWait(wait, By.id("android:id/button1"));
        childAgeInputConfirmButton.click();
        WebElement roomNumberInput = getElementWithWait(wait, By.xpath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.view.ViewGroup/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.LinearLayout[1]/android.widget.LinearLayout[1]/android.widget.LinearLayout[2]/android.widget.TextView[3]"));
        roomNumberInput.click();
        WebElement occupancyConfirmButton = getElementWithWait(wait, By.id("com.booking:id/group_config_apply_button"));
        occupancyConfirmButton.click();

        /*Assert destination is "London"*/
        accommodationDestinationField = getElementWithWait(wait, By.id("com.booking:id/facet_search_box_accommodation_destination"));
        WebElement accommodationDestinationFieldTextView = accommodationDestinationField.findElement(By.id("com.booking:id/facet_search_box_basic_field_label"));
        String destination = accommodationDestinationFieldTextView.getText();
        Assertions.assertEquals(accommodationDestination, destination);

        /*Assert accommodation start date is now and end date is now + 5 days*/
        WebElement accommodationDatesField = getElementWithWait(wait, By.id("com.booking:id/facet_search_box_accommodation_dates"));
        WebElement accommodationDatesFieldTextView = accommodationDatesField.findElement(By.id("com.booking:id/facet_search_box_basic_field_label"));
        String accommodationDates = accommodationDatesFieldTextView.getText();
        String expectedCurrentDateString = currentDate.format(DateTimeFormatter.ofPattern("E, dd MMMM"));
        String expectedCurrentDatePlus5String = currentDatePlus5.format(DateTimeFormatter.ofPattern("E, dd MMMM"));
        String expectedAccommodationDates = expectedCurrentDateString + " - " + expectedCurrentDatePlus5String;
        Assertions.assertEquals(expectedAccommodationDates, accommodationDates);

        /*Assert accommodation occupancy is "2 номери " + '\u00b7' + " 3 дорослих " + '\u00b7' + " 1 дитина"*/
        accommodationOccupancyField = getElementWithWait(wait, By.id("com.booking:id/facet_search_box_accommodation_occupancy"));
        WebElement accommodationOccupancyFieldTextView = accommodationOccupancyField.findElement(By.id("com.booking:id/facet_search_box_basic_field_label"));
        String roomAndGuestNumber = accommodationOccupancyFieldTextView.getText();
        Assertions.assertEquals(expectedRoomAndGuestNumber, roomAndGuestNumber);
    }

    private WebElement getElementWithWait(WebDriverWait wait, By locator) {
        return wait.until(ExpectedConditions
                .presenceOfElementLocated(locator));
    }

    @AfterAll
    public static void tearDown() {
        driver.quit();
    }
}
