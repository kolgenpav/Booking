package ua.edu.znu.booking;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Point;
import org.openqa.selenium.TimeoutException;
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
public class ParameterizedInputTest {

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

        WebElement registrationCloseButton = new ParameterizedInputTest().getElementWithWait(AppiumBy.accessibilityId("Перейти вгору"));
        registrationCloseButton.click();
    }

    @BeforeEach
    public void clearFields() {
        WebElement accommodationDestinationField = getElementWithWait(By.id("com.booking:id/facet_search_box_accommodation_destination"));
        accommodationDestinationField.clear();
        WebElement accommodationDatesField = getElementWithWait(By.id("com.booking:id/facet_search_box_accommodation_dates"));
        accommodationDatesField.clear();

    }

    /**
     * Check data entering for accommodation destination.
     */
    @Disabled
    @ParameterizedTest
    @ValueSource(strings = {"Лондон", "Стокгольм", "Гданськ"})
    public void accommodationDestinationTest(String accommodationDestination) {
        /*Accommodation destination input*/
        WebElement accommodationDestinationField = getElementWithWait(By.id("com.booking:id/facet_search_box_accommodation_destination"));
        accommodationDestinationField.click();
        WebElement accommodationDestinationInput = getElementWithWait(By.id("com.booking:id/facet_with_bui_free_search_booking_header_toolbar_content"));
        accommodationDestinationInput.sendKeys(accommodationDestination);
        WebElement accommodationDestinationPropose = getElementWithWait(By.xpath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/androidx.recyclerview.widget.RecyclerView/android.view.ViewGroup[1]"));
        accommodationDestinationPropose.click();
        /*Close calendar view* and return to main window*/
        /*Implicit wait before calendar appearing*/
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
        if (driver.findElements(By.id("com.booking:id/facet_date_picker_calendar")).size() != 0) {
            getElementWithWait(By.id("com.booking:id/facet_date_picker_calendar"));
            driver.navigate().back();
        }
        accommodationDestinationField = getElementWithWait(By.id("com.booking:id/facet_search_box_accommodation_destination"));
        WebElement accommodationDestinationFieldTextView = accommodationDestinationField.findElement(By.id("com.booking:id/facet_search_box_basic_field_label"));
        String destination = accommodationDestinationFieldTextView.getText();
        Assertions.assertEquals(accommodationDestination, destination);
    }

    /**
     * Check accommodation start and end dates entering.
     */
    @Disabled
    @ParameterizedTest
    @ValueSource(ints = {1, 5, 10})
    public void accommodationDatesTest(int daysShift) {
        WebElement accommodationDatesField = getElementWithWait(By.id("com.booking:id/facet_search_box_accommodation_dates"));
        accommodationDatesField.click();
        /*Implicit wait before calendar appearing*/
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
        LocalDate currentDate = LocalDate.now();
        String currentDateString = currentDate.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"));
        WebElement startDate = driver.findElement(AppiumBy.accessibilityId(currentDateString));
        startDate.click();
        LocalDate DaysShift = currentDate.plusDays(daysShift);
        String currentDatePlusDaysShiftString = DaysShift.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"));
        WebElement endDate = driver.findElement(AppiumBy.accessibilityId(currentDatePlusDaysShiftString));
        endDate.click();
        WebElement accommodationDatesConfirmButton = getElementWithWait(By.id("com.booking:id/facet_date_picker_confirm"));
        accommodationDatesConfirmButton.click();

        /*Assert accommodation start date is now and end date is now + daysShift*/
        accommodationDatesField = getElementWithWait(By.id("com.booking:id/facet_search_box_accommodation_dates"));
        WebElement accommodationDatesFieldTextView = accommodationDatesField.findElement(By.id("com.booking:id/facet_search_box_basic_field_label"));
        String accommodationDates = accommodationDatesFieldTextView.getText();
        String expectedCurrentDateString = currentDate.format(DateTimeFormatter.ofPattern("E, dd MMMM"));
        String expectedCurrentDatePlusDaysShiftString = DaysShift.format(DateTimeFormatter.ofPattern("E, dd MMMM"));
        String expectedAccommodationDates = expectedCurrentDateString + " - " + expectedCurrentDatePlusDaysShiftString;
        Assertions.assertEquals(expectedAccommodationDates, accommodationDates);
    }

    /**
     * Check rooms number, adults number, children number and first child age entering.
     */
//    @Disabled
    @ParameterizedTest
    @CsvSource({
            "1, 1, 1",
            "2, 4, 2",
            "5, 5, 5",
            "21, 21, 3",
            "24, 24, 2",
            "30, 30, 10"
    })
    public void occupancyTest(int roomsNumber, int adultsNumber, int childrenNumber) {
        String expectedRoomAndGuestNumber;
        //TODO Replace by extract numbers and compare.
        if (roomsNumber == 1 || roomsNumber % 20 == 1 || childrenNumber % 20 == 1) {
            expectedRoomAndGuestNumber = roomsNumber + " номер " + '\u00b7' + " " + adultsNumber + " дорослий " + '\u00b7' + " " + childrenNumber + " дитина";
        } else if (roomsNumber < 5 || (roomsNumber > 21 && roomsNumber < 25)) {
            expectedRoomAndGuestNumber = roomsNumber + " номери " + '\u00b7' + " " + adultsNumber + " дорослих " + '\u00b7' + " " + childrenNumber + " дітей";
        } else {
            expectedRoomAndGuestNumber = roomsNumber + " номерів " + '\u00b7' + " " + adultsNumber + " дорослих " + '\u00b7' + " " + childrenNumber + " дітей";
        }
        String expectedChildAge = "1 рік";

        WebElement accommodationOccupancyField = getElementWithWait(By.id("com.booking:id/facet_search_box_accommodation_occupancy"));
        accommodationOccupancyField.click();
        WebElement adultsNumberAdd;
        WebElement adultsNumberSubtract = getElementWithWait(By.xpath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.view.ViewGroup/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.LinearLayout/android.widget.LinearLayout[2]/android.widget.LinearLayout[2]/android.widget.TextView[1]"));
        adultsNumberSubtract.click();
        for (int i = 0; i < adultsNumber - 1; i++) {
            adultsNumberAdd = getElementWithWait(By.xpath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.view.ViewGroup/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.LinearLayout/android.widget.LinearLayout[2]/android.widget.LinearLayout[2]/android.widget.TextView[3]"));
            adultsNumberAdd.click();
        }
        for (int i = 0; i < childrenNumber; i++) {
            WebElement childrenNumberAdd = getElementWithWait(By.xpath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.view.ViewGroup/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.LinearLayout/android.widget.LinearLayout[3]/android.widget.LinearLayout[2]/android.widget.TextView[3]"));
            childrenNumberAdd.click();

            /*Child age NumberPicker swipe*/
            WebElement source = getElementWithWait(By.xpath("/hierarchy/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.NumberPicker"));
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
        }

        /*Assert child age is  "1 рік"*/
        WebElement childAgeInfo = getElementWithWait(By.id("com.booking:id/group_config_child_age_row_button"));
        String childAge = childAgeInfo.getText();
        Assertions.assertEquals(expectedChildAge, childAge);

        WebElement roomsNumberAdd;
        for (int i = 0; i < roomsNumber - 1; i++) {
            roomsNumberAdd = getElementWithWait(By.xpath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.view.ViewGroup/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.LinearLayout[1]/android.widget.LinearLayout[1]/android.widget.LinearLayout[2]/android.widget.TextView[3]"));
            roomsNumberAdd.click();
        }

        WebElement occupancyConfirmButton = getElementWithWait(By.id("com.booking:id/group_config_apply_button"));
        occupancyConfirmButton.click();

        /*Assert accommodation occupancy is "2 номери " + '\u00b7' + " 3 дорослих " + '\u00b7' + " 1 дитина"*/
        accommodationOccupancyField = getElementWithWait(By.id("com.booking:id/facet_search_box_accommodation_occupancy"));
        WebElement accommodationOccupancyFieldTextView = accommodationOccupancyField.findElement(By.id("com.booking:id/facet_search_box_basic_field_label"));
        String roomAndGuestNumber = accommodationOccupancyFieldTextView.getText();
//        Assertions.assertEquals(expectedRoomAndGuestNumber, roomAndGuestNumber);  //Здесь заканчивается параметризированный метод

        /*Subtract addition for adults number, children number and rooms number*/
        accommodationOccupancyField.click();
        WebElement roomsNumberSubtract;
        for (int i = 0; i < roomsNumber - 1; i++) {
            roomsNumberSubtract = getElementWithWait(By.xpath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.view.ViewGroup/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.LinearLayout/android.widget.LinearLayout[1]/android.widget.LinearLayout[2]/android.widget.TextView[1]"));
            roomsNumberSubtract.click();
        }
        adultsNumberAdd = getElementWithWait(By.xpath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.view.ViewGroup/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.LinearLayout/android.widget.LinearLayout[2]/android.widget.LinearLayout[2]/android.widget.TextView[3]"));
        adultsNumberAdd.click();
        for (int i = 0; i < adultsNumber - 1; i++) {
            adultsNumberSubtract = getElementWithWait(By.xpath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.view.ViewGroup/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.LinearLayout/android.widget.LinearLayout[2]/android.widget.LinearLayout[2]/android.widget.TextView[1]"));
            adultsNumberSubtract.click();
        }
        WebElement childrenNumberSubtract;
        for (int i = 0; i < childrenNumber; i++) {
            childrenNumberSubtract = getElementWithWait(By.xpath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.view.ViewGroup/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.LinearLayout/android.widget.LinearLayout[3]/android.widget.LinearLayout[2]/android.widget.TextView[1]"));
            childrenNumberSubtract.click();
        }

        occupancyConfirmButton = getElementWithWait(By.id("com.booking:id/group_config_apply_button"));
        occupancyConfirmButton.click();

        Assertions.assertEquals(expectedRoomAndGuestNumber, roomAndGuestNumber);  //Здесь заканчивается параметризированный метод
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

    @AfterAll
    public static void tearDown() {
        driver.quit();
    }
}
