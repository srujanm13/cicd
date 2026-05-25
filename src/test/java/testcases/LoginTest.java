package testcases;

import org.jspecify.annotations.NonNull;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.Assert;
import org.testng.annotations.*;
import utils.ConfigReader;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class LoginTest {

    WebDriver driver;

    @BeforeMethod

    public void setup(){
        String browser = getBrowserForExecution();

        if(browser.equalsIgnoreCase("chrome")) {
            ChromeOptions options = getChromeOptions();

            driver = new ChromeDriver(options);

        } else if (browser.equalsIgnoreCase("firefox")) {
            driver = new FirefoxDriver();
        }
        else {

            driver = new EdgeDriver();
        }
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));

        driver.get(ConfigReader.getUrl());

    }

    private static @NonNull ChromeOptions getChromeOptions() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--password-store=basic");
        options.addArguments("--disable-save-password-bubble");

        Map<String, Object> prefs = new HashMap<String, Object>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        prefs.put("profile.password_manager_leak_detection", false);
        options.setExperimentalOption("prefs", prefs);
        return options;
    }

    private static String getBrowserForExecution() {

        String browser = System.getProperty("browser");

        if (browser == null || browser.trim().isEmpty()) {

            browser = ConfigReader.getBrowser();

        }

        if (browser == null || browser.trim().isEmpty()) {

            throw new IllegalArgumentException("Browser is required. Pass it from command line, for example: mvn test -Pqa -Dbrowser=chrome, or set browser in config-{env}.properties");

        }

        return browser.trim();
    }

    @Test()
    public void verify_user_is_able_to_login(){

        try {
            driver.findElement(By.xpath("//input[@id='username']")).sendKeys(ConfigReader.getUsername());
            driver.findElement(By.xpath("//input[@id='password']")).sendKeys(ConfigReader.getPassword());
            driver.findElement(By.xpath("//input[@id='terms']")).click();
            driver.findElement(By.xpath("//input[@id='signInBtn']")).click();
            Assert.assertTrue(driver.findElement(By.xpath("//a[normalize-space()='ProtoCommerce']")).isDisplayed());
        } catch (Exception e) {
            Assert.fail();
            throw new RuntimeException(e);
        }
    }

    @AfterMethod
    public void teardown(){

        driver.quit();
    }

}
