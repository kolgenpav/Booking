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
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class InputTest {

    private static AndroidDriver driver;

    @BeforeAll
    public static void setUp() throws MalformedURLException {
        UiAutomator2Options options = new UiAutomator2Options()
                .setPlatformVersion("10.0")
                .setDeviceName("Android10Phone")
                .setApp(System.getProperty("user.dir") + "\\apps\\Booking_31.2.apk")
                .eventTimings();
        URL remoteUrl = new URL("http://127.0.0.1:4723/wd/hub");
        driver = new AndroidDriver(remoteUrl, options);

        /*Close registration prompt window*/
        WebElement registrationCloseButton = new InputTest().getElementWithWait(AppiumBy.accessibilityId("Перейти вгору"));
        registrationCloseButton.click();
    }

    /**
     * Check data entering for accommodation destination.
     */
//    @Order(1)
    @Test
    public void accommodationDestinationTest() {
        String accommodationDestination = "Лондон";
        WebElement accommodationDestinationField = getElementWithWait(By.id("com.booking:id/facet_search_box_accommodation_destination"));
        accommodationDestinationField.click();
        WebElement accommodationDestinationInput = getElementWithWait(By.id("com.booking:id/facet_with_bui_free_search_booking_header_toolbar_content"));
        accommodationDestinationInput.sendKeys(accommodationDestination);
        /*Use search by xpath because parent element has same id with another elements in view*/
        WebElement accommodationDestinationPropose = getElementWithWait(By.xpath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/androidx.recyclerview.widget.RecyclerView/android.view.ViewGroup[1]"));
        accommodationDestinationPropose.click();
        /*Close calendar view and return to main window*/
        if (getAllElementsWithWait(By.id("com.booking:id/facet_date_picker_calendar")).size() != 0) {
            getElementWithWait(By.id("com.booking:id/facet_date_picker_calendar"));
            driver.navigate().back();
        }
        accommodationDestinationField = getElementWithWait(By.id("com.booking:id/facet_search_box_accommodation_destination"));
        /*Nested element search by id instead search of the parent element by xpath*/
        WebElement accommodationDestinationFieldTextView = accommodationDestinationField.findElement(By.id("com.booking:id/facet_search_box_basic_field_label"));
        String destination = accommodationDestinationFieldTextView.getText();
        Assertions.assertEquals(accommodationDestination, destination);
    }

    /**
     * Check accommodation start and end dates entering.
     */
//    @Disabled
//    @Order(2)
    @Test
    public void accommodationDatesTest() {
        WebElement accommodationDatesField = getElementWithWait(By.id("com.booking:id/facet_search_box_accommodation_dates"));
        accommodationDatesField.click();
        LocalDate currentDate = LocalDate.now();
        String currentDateString = currentDate.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"));
        WebElement startDate = getElementWithWait(AppiumBy.accessibilityId(currentDateString));
        startDate.click();
        LocalDate currentDatePlus5 = currentDate.plusDays(5);
        String currentDatePlus5String = currentDatePlus5.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"));
        WebElement endDate = getElementWithWait(AppiumBy.accessibilityId(currentDatePlus5String));
        endDate.click();
        WebElement accommodationDatesConfirmButton = getElementWithWait(By.id("com.booking:id/facet_date_picker_confirm"));
        accommodationDatesConfirmButton.click();

        /*Assert accommodation start date is now and end date is now + 5 days*/
        accommodationDatesField = getElementWithWait(By.id("com.booking:id/facet_search_box_accommodation_dates"));
        /*Nested element search by id instead search of the parent element by xpath*/
        WebElement accommodationDatesFieldTextView = accommodationDatesField.findElement(By.id("com.booking:id/facet_search_box_basic_field_label"));
        String accommodationDates = accommodationDatesFieldTextView.getText();
        String expectedCurrentDateString = currentDate.format(DateTimeFormatter.ofPattern("E, dd MMMM"));
        String expectedCurrentDatePlus5String = currentDatePlus5.format(DateTimeFormatter.ofPattern("E, dd MMMM"));
        String expectedAccommodationDates = expectedCurrentDateString + " - " + expectedCurrentDatePlus5String;
        Assertions.assertEquals(expectedAccommodationDates, accommodationDates);
    }

    /**
     * Check rooms number, adults number, children number and first child age entering.
     */
//    @Disabled
//    @Order(3)
    @Test
    public void occupancyTest() {
        String expectedRoomAndGuestNumber = "2 номери " + '\u00b7' + " 3 дорослих " + '\u00b7' + " 1 дитина";
        String expectedChildAge = "1 рік";

        WebElement accommodationOccupancyField = getElementWithWait(By.id("com.booking:id/facet_search_box_accommodation_occupancy"));
        accommodationOccupancyField.click();
        /*Use search by xpath because parent element has same id with another elements in view*/
        WebElement adultsNumberInput = getElementWithWait(By.xpath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.view.ViewGroup/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.LinearLayout/android.widget.LinearLayout[2]/android.widget.LinearLayout[2]/android.widget.TextView[3]"));
        adultsNumberInput.click();
        /*Use search by xpath because parent element has same id with another elements in view*/
        WebElement childrenNumberInput = getElementWithWait(By.xpath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.view.ViewGroup/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.LinearLayout/android.widget.LinearLayout[3]/android.widget.LinearLayout[2]/android.widget.TextView[3]"));
        childrenNumberInput.click();

        /*Child age NumberPicker swipe*/
        WebElement source = getElementWithWait(By.className("android.widget.NumberPicker"));
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

        WebElement childAgeInputConfirmButton = getElementWithWait(By.id("android:id/button1"));
        childAgeInputConfirmButton.click();

        /*Assert child age is  "1 рік"*/
        WebElement childAgeInfo = getElementWithWait(By.id("com.booking:id/group_config_child_age_row_button"));
        String childAge = childAgeInfo.getText();
        Assertions.assertEquals(expectedChildAge, childAge);
        /*Use search by xpath because parent element has same id with another elements in view*/
        WebElement roomNumberInput = getElementWithWait(By.xpath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.view.ViewGroup/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.LinearLayout[1]/android.widget.LinearLayout[1]/android.widget.LinearLayout[2]/android.widget.TextView[3]"));
        roomNumberInput.click();
        WebElement occupancyConfirmButton = getElementWithWait(By.id("com.booking:id/group_config_apply_button"));
        occupancyConfirmButton.click();

        /*Assert accommodation occupancy is "2 номери " + '\u00b7' + " 3 дорослих " + '\u00b7' + " 1 дитина"*/
        accommodationOccupancyField = getElementWithWait(By.id("com.booking:id/facet_search_box_accommodation_occupancy"));
        /*Nested element search by id instead search of the parent element by xpath*/
        WebElement accommodationOccupancyFieldTextView = accommodationOccupancyField.findElement(By.id("com.booking:id/facet_search_box_basic_field_label"));
        String roomAndGuestNumber = accommodationOccupancyFieldTextView.getText();
        Assertions.assertEquals(expectedRoomAndGuestNumber, roomAndGuestNumber);
    }

    /**
     * Find element by locator with explicit wait.
     *
     * @param locator UI element locator
     * @return UI element found
     */
    private WebElement getElementWithWait(By locator) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        return wait.until(ExpectedConditions
                .presenceOfElementLocated(locator));
    }

    /**
     * Find all elements by locator with explicit wait.
     * @param locator UI element locator
     * @return List with UI elements found
     */
    private List<WebElement> getAllElementsWithWait(By locator) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        return wait.until(ExpectedConditions
                .presenceOfAllElementsLocatedBy(locator));
    }

    @AfterAll
    public static void tearDown() {
        driver.quit();
    }
}
