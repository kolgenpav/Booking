package ua.edu.znu.booking;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
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

import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

/**
 * Negative test of input data entering.
 */
public class DataEntryNegativeTest {

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
        WebElement registrationCloseButton = new DataEntryNegativeTest().getElementWithWait(AppiumBy.accessibilityId("Перейти вгору"));
        registrationCloseButton.click();
    }

    /**
     * Check data entering for accommodation destination.
     */
    @Test
    void accommodationDestinationTest() {
        String accommodationDestination = "Веселянка";
        WebElement accommodationDestinationField = getElementWithWait(By.id("com.booking:id/facet_search_box_accommodation_destination"));
        accommodationDestinationField.click();
        WebElement accommodationDestinationInput = getElementWithWait(By.id("com.booking:id/facet_with_bui_free_search_booking_header_toolbar_content"));
        accommodationDestinationInput.sendKeys(accommodationDestination);
        /*Need to avoid "The element not linked to the same object in DOM anymore" error*/
        getElementWithWait(By.id("com.booking:id/facet_disambiguation_content"));
        /*Use search by xpath because parent element has same id with another elements in view*/
        WebElement accommodationDestinationPropose = getElementWithWait(By.xpath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/androidx.recyclerview.widget.RecyclerView/android.view.ViewGroup[1]"));
        accommodationDestinationPropose.click();
        /*Close calendar view and return to main window*/
        if (isElementPresent(By.id("com.booking:id/facet_date_picker_calendar"))) {
            driver.navigate().back();
        }
        accommodationDestinationField = getElementWithWait(By.id("com.booking:id/facet_search_box_accommodation_destination"));
        /*Nested element search by id instead search of the parent element by xpath*/
        WebElement accommodationDestinationFieldTextView = accommodationDestinationField.findElement(By.id("com.booking:id/facet_search_box_basic_field_label"));
        String destination = accommodationDestinationFieldTextView.getText();

        //TODO REturn to main screen
        Exception exception = assertThrowsExactly(IllegalArgumentException.class, () -> {
            if (!accommodationDestination.equals(destination)) {
                throw new IllegalArgumentException("Destination not found");
            }
        });
        Assertions.assertEquals("Destination not found", exception.getMessage());
    }

    /**
     * Check accommodation start and end dates entering.
     */
    @Test
    void accommodationDatesTest() {
        WebElement accommodationDatesField = getElementWithWait(By.id("com.booking:id/facet_search_box_accommodation_dates"));
        accommodationDatesField.click();
        LocalDate startDate = LocalDate.now();
        dateSelect(startDate);
        LocalDate endDate = startDate.plusDays(31);
        dateSelect(endDate);

        /*Assert accommodation start date is now and end date is now + 31 days*/
        WebElement popupView = getElementWithWait(By.xpath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/androidx.appcompat.widget.LinearLayoutCompat/android.widget.FrameLayout/android.widget.ScrollView/android.widget.LinearLayout/android.widget.TextView"));
        String popupMessage = popupView.getText();
        Assertions.assertTrue(popupMessage.startsWith("Дата вашого виїзду настає через 30 ночей"));
        //TODO REturn to main screen
    }

    /**
     * Select accommodation date in calendar.
     *
     * @param date starting point for date select (current date plus DaysShift is recommended)
     */
    private void dateSelect(LocalDate date) {
        String dateString = date.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"));
        WebElement calendar = getElementWithWait(By.id("com.booking:id/facet_date_picker_calendar"));
        /* Swipe if date is absent*/
        while (driver.findElements(AppiumBy.accessibilityId(dateString)).size() == 0) {
            makeSwipe(calendar, 480, 560, 480, 198);
        }
        WebElement dateElement = getElementWithWait(AppiumBy.accessibilityId(dateString));
        dateElement.click();
    }

    /**
     * Find element by locator with explicit wait.
     *
     * @param locator UI element locator
     * @return UI element
     */
    private WebElement getElementWithWait(By locator) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        return wait.until(ExpectedConditions
                .presenceOfElementLocated(locator));
    }

    /**
     * Make swipe on element.
     *
     * @param element      element that swiped
     * @param startXOffset swipe start point X coordinate (offset on X axis in pixels from top left corner of element)
     * @param startYOffset swipe start point Y coordinate (offset on Y axis in pixels from top left corner of element)
     * @param endXOffset   swipe end point X coordinate (offset on X axis in pixels from top left corner of element)
     * @param endYOffset   swipe end point Y coordinate (offset on X axis in pixels from top left corner of element)
     */
    private void makeSwipe(WebElement element, int startXOffset, int startYOffset, int endXOffset, int endYOffset) {
        Point widgetLocation = element.getLocation(); //0,295
        Point swipeStart = widgetLocation.moveBy(startXOffset, startYOffset); //380, 1050
        Point swipeEnd = widgetLocation.moveBy(endXOffset, endYOffset); //380, 688
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipeAge = new Sequence(finger, 1);
        swipeAge.addAction(finger.createPointerMove(Duration.ofMillis(0), PointerInput.Origin.viewport(), swipeStart.x, swipeStart.y));
        swipeAge.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        swipeAge.addAction(finger.createPointerMove(Duration.ofMillis(700), PointerInput.Origin.viewport(), swipeEnd.x, swipeEnd.y));
        swipeAge.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(List.of(swipeAge));
    }

    /**
     * Check if element present.
     *
     * @param locator element locator
     * @return true if element present false otherwise
     */
    protected boolean isElementPresent(By locator) {
        try {
            Thread.sleep(3000);
            driver.findElement(locator);
            return true;
        } catch (NoSuchElementException | InterruptedException ex) {
            return false;
        }
    }

    @AfterAll
    public static void tearDown() {
        driver.quit();
    }
}
