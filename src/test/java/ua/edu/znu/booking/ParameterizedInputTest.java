package ua.edu.znu.booking;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
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
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
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

    /**
     * Check data entering for accommodation destination.
     */
//    @Disabled
    @Order(1)
    @ParameterizedTest(name = "accommodation destination ={0}")
    @ValueSource(strings = {"Лондон", "Вестерос", "Варшава", "Париж", "Вашингтон"})
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
//    @Disabled
    @Order(2)
    @ParameterizedTest(name = "start date shift ={0}, end date shift={1}")
    @CsvSource({
            "0, 30",
            "479, 1"
    })
    public void accommodationDatesTest(int startDateDaysShift, int endDateDaysShift) {
        WebElement accommodationDatesField = getElementWithWait(By.id("com.booking:id/facet_search_box_accommodation_dates"));
        accommodationDatesField.click();
        /*Implicit wait before calendar appearing*/
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
        LocalDate startDate = LocalDate.now().plusDays(startDateDaysShift);
        String startDateString = startDate.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"));
        LocalDate endDate;
        int startDateElements = driver.findElements(AppiumBy.accessibilityId(startDateString)).size();
        while (startDateElements == 0) {   //swipe if start date is absent
            WebElement calendar = getElementWithWait(By.id("com.booking:id/facet_date_picker_calendar"));
            makeSwipe(calendar, 480, 560, 480, 198);
            startDateElements = driver.findElements(AppiumBy.accessibilityId(startDateString)).size();
        }
        WebElement startDateElement = driver.findElement(AppiumBy.accessibilityId(startDateString));
        startDateElement.click();
        endDate = startDate.plusDays(endDateDaysShift);
        String endDatePlusDaysShiftString = endDate.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"));
        int endDateElements = driver.findElements(AppiumBy.accessibilityId(endDatePlusDaysShiftString)).size();
        while (endDateElements == 0) { //swipe if end date is absent
            WebElement calendar = getElementWithWait(By.id("com.booking:id/facet_date_picker_calendar"));
            makeSwipe(calendar, 480, 560, 480, 198);
            endDateElements = driver.findElements(AppiumBy.accessibilityId(endDatePlusDaysShiftString)).size();
        }
        WebElement endDateElement = driver.findElement(AppiumBy.accessibilityId(endDatePlusDaysShiftString));
        endDateElement.click();
        WebElement accommodationDatesConfirmButton = getElementWithWait(By.id("com.booking:id/facet_date_picker_confirm"));
        accommodationDatesConfirmButton.click();

        /*Assert accommodation start date is now and end date is now + daysShift*/
        accommodationDatesField = getElementWithWait(By.id("com.booking:id/facet_search_box_accommodation_dates"));
        WebElement accommodationDatesFieldTextView = accommodationDatesField.findElement(By.id("com.booking:id/facet_search_box_basic_field_label"));
        String accommodationDates = accommodationDatesFieldTextView.getText();
        String expectedStartDateString = startDate.format(DateTimeFormatter.ofPattern("E, dd MMMM"));
        String expectedEndDateString = endDate.format(DateTimeFormatter.ofPattern("E, dd MMMM"));
        String expectedAccommodationDates = expectedStartDateString + " - " + expectedEndDateString;
        Assertions.assertEquals(expectedAccommodationDates, accommodationDates);
    }

    /**
     * Check rooms number, adults number, children number and first child age entering.
     */
//    @Disabled
    @Order(3)
    @ParameterizedTest(name = "rooms: {0}, adults: {1}, children: {2}, child age: {3}")
    @CsvSource({
            "1, 1, 0, ''",
            "30, 30, 2, '< 1 рік:1 рік'",
            "10, 10, 3, '17 років:3 роки:< 1 рік'"
    })
    public void occupancyTest(int roomsNumber, int adultsNumber, int childrenNumber, String childrenAges) {
        String[] ages = new String[0];
        if(!"".equals(childrenAges)) {
            ages = childrenAges.split(":");
        }
        if (childrenNumber != ages.length) {
            throw new IllegalArgumentException("The number of children should be equal to the number of their ages");
        }
        String[] childrenAgesArray = {"< 1 рік", "1 рік", "2 роки", "3 роки", "4 роки", "5 років", "6 років", "7 років", "8 років", "9 років", "10 років", "11 років", "12 років", "13 років", "14 років", "15 років", "16 років", "17 років"};
        int ageIndex = -1;

        WebElement accommodationOccupancyField = getElementWithWait(By.id("com.booking:id/facet_search_box_accommodation_occupancy"));
        accommodationOccupancyField.click();
        /*Enter adults number*/
        WebElement adultsNumberAdd;
        WebElement adultsNumberSubtract = getElementWithWait(By.xpath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.view.ViewGroup/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.LinearLayout/android.widget.LinearLayout[2]/android.widget.LinearLayout[2]/android.widget.TextView[1]"));
        adultsNumberSubtract.click();
        for (int i = 0; i < adultsNumber - 1; i++) {
            adultsNumberAdd = getElementWithWait(By.xpath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.view.ViewGroup/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.LinearLayout/android.widget.LinearLayout[2]/android.widget.LinearLayout[2]/android.widget.TextView[3]"));
            adultsNumberAdd.click();
        }
        /*Enter children number and age*/
        for (int i = 0; i < childrenNumber; i++) {
            for (int k = 0; k < childrenAgesArray.length; k++) {
                if (ages[i].equals(childrenAgesArray[k])) {
                    ageIndex = k;
                    break;
                }
            }
            WebElement childrenNumberAdd = getElementWithWait(By.xpath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.view.ViewGroup/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.LinearLayout/android.widget.LinearLayout[3]/android.widget.LinearLayout[2]/android.widget.TextView[3]"));
            childrenNumberAdd.click();
            for (int j = 0; j < ageIndex + 1; j++) {
                /*Child age NumberPicker swipe*/
                WebElement numberPicker = getElementWithWait(By.xpath("/hierarchy/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.NumberPicker"));
                makeSwipe(numberPicker, 270, 350, 270, 225);
            }

            WebElement childAgeInputConfirmButton = getElementWithWait(By.id("android:id/button1"));
            childAgeInputConfirmButton.click();

            /*Assert child age equals the first child age parameter*/
            if (childrenNumber > 0) {
                WebElement childAgeInfo = getElementWithWait(By.xpath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.view.ViewGroup/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.LinearLayout[2]/androidx.recyclerview.widget.RecyclerView/android.widget.LinearLayout[" + (i + 1) + "]/android.widget.TextView[2]"));
                String actualChildAge = childAgeInfo.getText();
                Assertions.assertEquals(ages[i], actualChildAge);
            }
        }


        WebElement roomsNumberAdd;
        for (int i = 0; i < roomsNumber - 1; i++) {
            roomsNumberAdd = getElementWithWait(By.xpath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.view.ViewGroup/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.LinearLayout[1]/android.widget.LinearLayout[1]/android.widget.LinearLayout[2]/android.widget.TextView[3]"));
            roomsNumberAdd.click();
        }

        WebElement occupancyConfirmButton = getElementWithWait(By.id("com.booking:id/group_config_apply_button"));
        occupancyConfirmButton.click();

        /*Get from accommodation occupancy string rooms number, adults number and children number*/
        accommodationOccupancyField = getElementWithWait(By.id("com.booking:id/facet_search_box_accommodation_occupancy"));
        WebElement accommodationOccupancyFieldTextView = accommodationOccupancyField.findElement(By.id("com.booking:id/facet_search_box_basic_field_label"));
        String roomAndGuestNumber = accommodationOccupancyFieldTextView.getText();
        String[] roomsAndGuestsNumberWords = roomAndGuestNumber.split(" ");
        int actualRoomsNumber = Integer.parseInt(roomsAndGuestsNumberWords[0]);
        int actualAdultsNumber = Integer.parseInt(roomsAndGuestsNumberWords[3]);
        int actualChildrenNumber = Integer.parseInt(roomsAndGuestsNumberWords[6]);

        /*Subtract addition for rooms number, adults number and children number*/
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

        Assertions.assertEquals(roomsNumber, actualRoomsNumber);
        Assertions.assertEquals(adultsNumber, actualAdultsNumber);
        Assertions.assertEquals(childrenNumber, actualChildrenNumber);
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

    @AfterAll
    public static void tearDown() {
        driver.quit();
    }
}
