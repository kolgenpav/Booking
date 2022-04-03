package ua.edu.znu.booking;

import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static io.appium.java_client.touch.offset.PointOption.point;

/**
 * Test of input data entering.
 */
public class InputTest {

    private static AndroidDriver<MobileElement> driver;

    @BeforeAll
    public static void setUp() throws MalformedURLException {
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        desiredCapabilities.setCapability("platformName", "Android");
        desiredCapabilities.setCapability("appium:platformVersion", "10.0");
        desiredCapabilities.setCapability("appium:automationName", "UiAutomator2");
        desiredCapabilities.setCapability("appium:deviceName", "Android10Phone");
        desiredCapabilities.setCapability("appium:app", "e:\\Booking_31.2.apk");
        desiredCapabilities.setCapability("appium:ensureWebviewsHavePages", true);
        desiredCapabilities.setCapability("appium:nativeWebScreenshot", true);
        desiredCapabilities.setCapability("appium:newCommandTimeout", 3600);
        desiredCapabilities.setCapability("appium:connectHardwareKeyboard", true);

        URL remoteUrl = new URL("http://127.0.0.1:4723/wd/hub");

        driver = new AndroidDriver<>(remoteUrl, desiredCapabilities);
    }

    @Test
    public void sampleTest() {
        MobileElement el1 = driver.findElementByAccessibilityId("Перейти вгору");
        el1.click();
        MobileElement el2 = driver.findElementById("com.booking:id/facet_search_box_accommodation_destination");
        el2.click();
        MobileElement el3 = driver.findElementById("com.booking:id/facet_with_bui_free_search_booking_header_toolbar_content");
        el3.sendKeys("Лондон");
        MobileElement el4 = driver.findElementByXPath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/androidx.recyclerview.widget.RecyclerView/android.view.ViewGroup[1]");
        el4.click();
        LocalDate currentDate = LocalDate.now();
        String currentDateString = currentDate.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"));
        MobileElement el5 = driver.findElementByAccessibilityId(currentDateString);
        el5.click();
        LocalDate currentDatePlus5 = currentDate.plusDays(5);
        String currentDatePlus5String = currentDatePlus5.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"));
        MobileElement el6 = driver.findElementByAccessibilityId(currentDatePlus5String);
        el6.click();
        MobileElement el7 = driver.findElementById("com.booking:id/facet_date_picker_confirm");
        el7.click();
        MobileElement el8 = driver.findElementById("com.booking:id/facet_search_box_accommodation_occupancy");
        el8.click();
        MobileElement el9 = driver.findElementByXPath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.view.ViewGroup/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.LinearLayout/android.widget.LinearLayout[2]/android.widget.LinearLayout[2]/android.widget.TextView[3]");
        el9.click();
        MobileElement el10 = driver.findElementByXPath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.view.ViewGroup/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.LinearLayout/android.widget.LinearLayout[3]/android.widget.LinearLayout[2]/android.widget.TextView[3]");
        el10.click();
        (new TouchAction<>(driver))
                .press(point(442, 770))
                .moveTo(point(442, 407))
                .release()
                .perform();
        (new TouchAction<>(driver))
                .press(point(355, 741))
                .moveTo(point(337, 603))
                .release()
                .perform();
        MobileElement el11 = driver.findElementById("android:id/button1");
        el11.click();
        MobileElement el12 = driver.findElementByXPath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.view.ViewGroup/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.LinearLayout[1]/android.widget.LinearLayout[1]/android.widget.LinearLayout[2]/android.widget.TextView[3]");
        el12.click();
        MobileElement el13 = driver.findElementById("com.booking:id/group_config_apply_button");
        el13.click();

        /*Assert destination is "London"*/
        MobileElement el14 = el2.findElementById("com.booking:id/facet_search_box_basic_field_label");
        String destination = el14.getText();
        Assertions.assertEquals("Лондон", destination);

        /*Assert accommodation start date is now and end date is now + 5 days*/
        MobileElement el15 = driver.findElementById("com.booking:id/facet_search_box_accommodation_dates");
        MobileElement el16 = el15.findElementById("com.booking:id/facet_search_box_basic_field_label");
        String accommodationDates = el16.getText();
        String expectedCurrentDateString = currentDate.format(DateTimeFormatter.ofPattern("E, dd MMMM"));
        String expectedCurrentDatePlus5String = currentDatePlus5.format(DateTimeFormatter.ofPattern("E, dd MMMM"));
        String expectedAccommodationDates = expectedCurrentDateString + " - " + expectedCurrentDatePlus5String;
        Assertions.assertEquals(expectedAccommodationDates, accommodationDates);

        /*Assert accommodation occupancy is "2 номери " + '\u00b7' + " 3 дорослих " + '\u00b7' + " 1 дитина"*/
        MobileElement el17 = el8.findElementById("com.booking:id/facet_search_box_basic_field_label");
        String roomAndGuestNumber = el17.getText();
        String expectedRoomAndGuestNumber = "2 номери " + '\u00b7' + " 3 дорослих " + '\u00b7' + " 1 дитина";
        Assertions.assertEquals(expectedRoomAndGuestNumber, roomAndGuestNumber);
    }

    @AfterAll
    public static void tearDown() {
        driver.quit();
    }
}
