package ua.edu.znu.booking;

import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;
import java.net.URL;

import static io.appium.java_client.touch.offset.PointOption.point;

/**
 * Test of input data entering.
 */
public class InputTest {

  private static AndroidDriver driver;

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

    driver = new AndroidDriver(remoteUrl, desiredCapabilities);
  }

  @Test
  public void sampleTest() {
    MobileElement el1 = (MobileElement) driver.findElementByAccessibilityId("Перейти вгору");
    el1.click();
    MobileElement el2 = (MobileElement) driver.findElementById("com.booking:id/facet_search_box_accommodation_destination");
    el2.click();
    MobileElement el3 = (MobileElement) driver.findElementById("com.booking:id/facet_with_bui_free_search_booking_header_toolbar_content");
    el3.sendKeys("Лондон");
    MobileElement el4 = (MobileElement) driver.findElementByXPath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/androidx.recyclerview.widget.RecyclerView/android.view.ViewGroup[1]");
    el4.click();
    MobileElement el5 = (MobileElement) driver.findElementByAccessibilityId("01 квітня 2022");
    el5.click();
    MobileElement el6 = (MobileElement) driver.findElementByAccessibilityId("06 квітня 2022");
    el6.click();
    MobileElement el7 = (MobileElement) driver.findElementById("com.booking:id/facet_date_picker_confirm");
    el7.click();
    MobileElement el8 = (MobileElement) driver.findElementById("com.booking:id/facet_search_box_accommodation_occupancy");
    el8.click();
    MobileElement el9 = (MobileElement) driver.findElementByXPath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.view.ViewGroup/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.LinearLayout/android.widget.LinearLayout[2]/android.widget.LinearLayout[2]/android.widget.TextView[3]");
    el9.click();
    MobileElement el10 = (MobileElement) driver.findElementByXPath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.view.ViewGroup/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.LinearLayout/android.widget.LinearLayout[3]/android.widget.LinearLayout[2]/android.widget.TextView[3]");
    el10.click();
    (new TouchAction(driver))
            .press(point(442, 770))
            .moveTo(point(442, 407))
            .release()
            .perform();
    (new TouchAction(driver))
            .press(point(355, 741))
            .moveTo(point(337, 603))
            .release()
            .perform();
    MobileElement el11 = (MobileElement) driver.findElementById("android:id/button1");
    el11.click();
    MobileElement el12 = (MobileElement) driver.findElementByXPath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.view.ViewGroup/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.LinearLayout[1]/android.widget.LinearLayout[1]/android.widget.LinearLayout[2]/android.widget.TextView[3]");
    el12.click();
    MobileElement el13 = (MobileElement) driver.findElementById("com.booking:id/group_config_apply_button");
    el13.click();
  }

  @AfterAll
  public static void tearDown() {
    driver.quit();
  }
}
