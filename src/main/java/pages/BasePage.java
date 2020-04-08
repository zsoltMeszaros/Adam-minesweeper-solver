package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import util.MyProperties;

public abstract class BasePage {
    static final String BASE_URL = MyProperties.getBaseUrl();
    WebDriver driver;
    Wait<WebDriver> wait;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(this.driver, MyProperties.getTimeout());
    }


}
