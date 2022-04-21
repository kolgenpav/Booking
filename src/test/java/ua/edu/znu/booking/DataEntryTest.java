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

/**
 * Positive test of input data entering.
 */
public class DataEntryTest {

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
        WebElement registrationCloseButton = new DataEntryTest().getElementWithWait(AppiumBy.accessibilityId("Перейти вгору"));
        registrationCloseButton.click();
    }

    /**
     * Check data entering for accommodation destination.
     *
     * @param accommodationDestination accommodation destination
     */
    @ParameterizedTest(name = "accommodation destination ={0}")
    @ValueSource(strings = {"Лондон", "Вестерос"})
    void accommodationDestinationTest(String accommodationDestination) {
        WebElement accommodationDestinationField = getElementWithWait(By.id("com.booking:id/facet_search_box_accommodation_destination"));
        accommodationDestinationField.click();
        WebElement accommodationDestinationInput = getElementWithWait(By.id("com.booking:id/facet_with_bui_free_search_booking_header_toolbar_content"));
        accommodationDestinationInput.sendKeys(accommodationDestination);
        /*Need to avoid "The element not linked to the same object in DOM anymore" error*/
        getElementWithWait(By.id("com.booking:id/facet_disambiguation_content"));
        /*Use search by xpath because parent element has same id with another elements in view*/
        WebElement accommodationDestinationPropose = getElementWithWait(By.xpath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/androidx.recyclerview.widget.RecyclerView/android.view.ViewGroup[1]"));
        accommodationDestinationPropose.click();
        /*Close calendar view* and return to main window*/
        if (isElementPresent(By.id("com.booking:id/facet_date_picker_calendar"))) {
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
    @ParameterizedTest(name = "start date shift ={0}, end date shift={1}")
    @CsvSource({
            "0, 30",
            "479, 1"
    })
    void accommodationDatesTest(int startDateDaysShift, int endDateDaysShift) {
        WebElement accommodationDatesField = getElementWithWait(By.id("com.booking:id/facet_search_box_accommodation_dates"));
        accommodationDatesField.click();
        LocalDate startDate = LocalDate.now().plusDays(startDateDaysShift);
        dateSelect(startDate);
        LocalDate endDate = startDate.plusDays(endDateDaysShift);
        dateSelect(endDate);
        WebElement accommodationDatesConfirmButton = getElementWithWait(By.id("com.booking:id/facet_date_picker_confirm"));
        accommodationDatesConfirmButton.click();

        /*Assert accommodation start date is now and end date is now + daysShift*/
        accommodationDatesField = getElementWithWait(By.id("com.booking:id/facet_search_box_accommodation_dates"));
        /*Nested element search by id instead search of the parent element by xpath*/
        WebElement accommodationDatesFieldTextView = accommodationDatesField.findElement(By.id("com.booking:id/facet_search_box_basic_field_label"));
        String accommodationDates = accommodationDatesFieldTextView.getText();
        String expectedStartDateString = startDate.format(DateTimeFormatter.ofPattern("E, dd MMMM"));
        String expectedEndDateString = endDate.format(DateTimeFormatter.ofPattern("E, dd MMMM"));
        String expectedAccommodationDates = expectedStartDateString + " - " + expectedEndDateString;
        Assertions.assertEquals(expectedAccommodationDates, accommodationDates);
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
     * Check rooms number, adults number, children number and ages entering.
     * Children number set as children ages quantity.
     * Number of rooms must be less than or equal to the number of adults
     * to avoid automatically increasing the number of adults.
     *
     * @param roomsNumber number of rooms
     * @param adultsNumber number of adults
     * @param childrenAges number of children
     */
    @ParameterizedTest(name = "rooms: {0}, adults: {1}, children ages: {3}")
    @CsvSource({
            "1, 1, ''",
            "2, 2, '< 1 рік'",
            "30, 30, '17 років:3 роки:< 1 рік:4 роки:7 років:17 років:3 роки:< 1 рік:4 роки:7 років'"
    })
    void occupancyTest(int roomsNumber, int adultsNumber, String childrenAges) {
        WebElement accommodationOccupancyField = getElementWithWait(By.id("com.booking:id/facet_search_box_accommodation_occupancy"));
        accommodationOccupancyField.click();
        /*Enter adults number - we have to decrease adult number by 1 because of default value is 2*/
        /*Use search by xpath because parent element has same id with another elements in view*/
        WebElement adultsNumberSubtract = getElementWithWait(By.xpath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.view.ViewGroup/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.LinearLayout/android.widget.LinearLayout[2]/android.widget.LinearLayout[2]/android.widget.TextView[1]"));
        adultsNumberSubtract.click();
        /*Use search by xpath because parent element has same id with another elements in view*/
        WebElement adultsNumberAdd = getElementWithWait(By.xpath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.view.ViewGroup/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.LinearLayout/android.widget.LinearLayout[2]/android.widget.LinearLayout[2]/android.widget.TextView[3]"));
        for (int i = 0; i < adultsNumber - 1; i++) {
            adultsNumberAdd.click();
        }

        /*Enter children number and ages*/
        int childrenNumber = childrenDataEntry(childrenAges);

        /*Enter rooms number*/
        /*Use search by xpath because parent element has same id with another elements in view*/
        WebElement roomsNumberAdd = getElementWithWait(By.xpath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.view.ViewGroup/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.LinearLayout[1]/android.widget.LinearLayout[1]/android.widget.LinearLayout[2]/android.widget.TextView[3]"));
        for (int i = 0; i < roomsNumber - 1; i++) {
            roomsNumberAdd.click();
        }

        WebElement occupancyConfirmButton = getElementWithWait(By.id("com.booking:id/group_config_apply_button"));
        occupancyConfirmButton.click();

        /*Get from accommodation occupancy string rooms number, adults number and children number*/
        accommodationOccupancyField = getElementWithWait(By.id("com.booking:id/facet_search_box_accommodation_occupancy"));
        /*Nested element search by id instead search of the parent element by xpath*/
        WebElement accommodationOccupancyFieldTextView = accommodationOccupancyField.findElement(By.id("com.booking:id/facet_search_box_basic_field_label"));
        String roomAndGuestNumber = accommodationOccupancyFieldTextView.getText();
        String[] roomsAndGuestsNumberWords = roomAndGuestNumber.split(" ");
        int actualRoomsNumber = Integer.parseInt(roomsAndGuestsNumberWords[0]);
        int actualAdultsNumber = Integer.parseInt(roomsAndGuestsNumberWords[3]);
        int actualChildrenNumber = Integer.parseInt(roomsAndGuestsNumberWords[6]);

        accommodationOccupancyField.click();
        setInitialNumbers(roomsNumber, adultsNumber, childrenNumber);

        occupancyConfirmButton = getElementWithWait(By.id("com.booking:id/group_config_apply_button"));
        occupancyConfirmButton.click();

        Assertions.assertEquals(roomsNumber, actualRoomsNumber);
        Assertions.assertEquals(adultsNumber, actualAdultsNumber);
        Assertions.assertEquals(childrenNumber, actualChildrenNumber);
    }

    /**
     * Enter children data.
     *
     * @param childrenAges children ages string
     * @return number of children
     */
    private int childrenDataEntry(String childrenAges) {
        String[] childrenAgesArray = {"< 1 рік", "1 рік", "2 роки", "3 роки", "4 роки", "5 років", "6 років", "7 років", "8 років", "9 років", "10 років", "11 років", "12 років", "13 років", "14 років", "15 років", "16 років", "17 років"};
        int ageIndex = -1;
        /*If no children ages[] must be empty, not empty String ""*/
        String[] ages = new String[0];
        /*For childrenAges that not equal empty String ""*/
        if (!"".equals(childrenAges)) {
            ages = childrenAges.split(":");
        }
        int childrenNumber = ages.length;

        /*Enter children number and ages*/
        for (int i = 0; i < childrenNumber; i++) {
            for (int k = 0; k < childrenAgesArray.length; k++) {
                if (ages[i].equals(childrenAgesArray[k])) {
                    ageIndex = k;
                    break;
                }
            }
            /*Use search by xpath because parent element has same id with another elements in view*/
            WebElement childrenNumberAdd = getElementWithWait(By.xpath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.view.ViewGroup/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.LinearLayout/android.widget.LinearLayout[3]/android.widget.LinearLayout[2]/android.widget.TextView[3]"));
            childrenNumberAdd.click();
            for (int j = 0; j < ageIndex + 1; j++) {
                /*Child age NumberPicker swipe*/
                WebElement numberPicker = getElementWithWait(By.className("android.widget.NumberPicker"));
                makeSwipe(numberPicker, 270, 350, 270, 225);
            }

            WebElement childAgeInputConfirmButton = getElementWithWait(By.id("android:id/button1"));
            childAgeInputConfirmButton.click();

            /*Assert expected children ages to actual children ages from list*/
            WebElement childrenAgesList = getElementWithWait(By.id("com.booking:id/group_config_children_ages_section"));
            WebElement childAgeInfo;
            /*If child age is absent (only 3 child fit to screen), make swipe*/
            if (i < 4) {
                while (driver.findElements(By.xpath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.view.ViewGroup/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.LinearLayout[2]/androidx.recyclerview.widget.RecyclerView/android.widget.LinearLayout[" + (i + 1) + "]/android.widget.TextView[2]")).size() == 0) {
                    makeSwipe(childrenAgesList, 380, 500, 380, 277);
                }
                /*Use search by xpath because parent element has same id with another elements in view*/
                childAgeInfo = getElementWithWait(By.xpath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.view.ViewGroup/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.LinearLayout[2]/androidx.recyclerview.widget.RecyclerView/android.widget.LinearLayout[" + (i + 1) + "]/android.widget.TextView[2]"));
            } else {
                makeSwipe(childrenAgesList, 380, 500, 380, 277);
                /*Use search by xpath because parent element has same id with another elements in view*/
                childAgeInfo = getElementWithWait(By.xpath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.view.ViewGroup/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.LinearLayout[2]/androidx.recyclerview.widget.RecyclerView/android.widget.LinearLayout[4]/android.widget.TextView[2]"));
            }

            String actualChildAge = childAgeInfo.getText();
            Assertions.assertEquals(ages[i], actualChildAge);
        }
        return childrenNumber;
    }

    /**
     * Set numbers of rooms, adults and children to initial (default) values.
     *
     * @param roomsNumber    number of rooms
     * @param adultsNumber   number of adults
     * @param childrenNumber number of children
     */
    private void setInitialNumbers(int roomsNumber, int adultsNumber, int childrenNumber) {
        WebElement roomsNumberSubtract;
        for (int i = 0; i < roomsNumber - 1; i++) {
            /*Use search by xpath because parent element has same id with another elements in view*/
            roomsNumberSubtract = getElementWithWait(By.xpath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.view.ViewGroup/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.LinearLayout/android.widget.LinearLayout[1]/android.widget.LinearLayout[2]/android.widget.TextView[1]"));
            roomsNumberSubtract.click();
        }

        WebElement adultsNumberSubtract;
        for (int i = 0; i < adultsNumber - 1; i++) {
            /*Use search by xpath because parent element has same id with another elements in view*/
            adultsNumberSubtract = getElementWithWait(By.xpath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.view.ViewGroup/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.LinearLayout/android.widget.LinearLayout[2]/android.widget.LinearLayout[2]/android.widget.TextView[1]"));
            adultsNumberSubtract.click();
        }
        /*We have to increase adult number by 1 because of default value is 2*/
        /*Use search by xpath because parent element has same id with another elements in view*/
        WebElement adultsNumberAdd = getElementWithWait(By.xpath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.view.ViewGroup/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.LinearLayout/android.widget.LinearLayout[2]/android.widget.LinearLayout[2]/android.widget.TextView[3]"));
        adultsNumberAdd.click();

        WebElement childrenNumberSubtract;
        for (int i = 0; i < childrenNumber; i++) {
            /*Use search by xpath because parent element has same id with another elements in view*/
            childrenNumberSubtract = getElementWithWait(By.xpath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.view.ViewGroup/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.LinearLayout/android.widget.LinearLayout[3]/android.widget.LinearLayout[2]/android.widget.TextView[1]"));
            childrenNumberSubtract.click();
        }
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
