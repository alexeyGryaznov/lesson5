import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Tests{


    public WebDriver setUp() {
        WebDriver webdriver= WebDriverManager.chromedriver().create();
        webdriver.get("https://www.dns-shop.ru/");
        webdriver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
        webdriver.manage().window().setSize(new Dimension(1500,800));
        return webdriver;
    }

    @Test
    @DisplayName("Авторизация пользователя")
        public void LoginINOutTest () {
            WebDriver webdriver=setUp();
            String Nik = "Пришелец-CY75848";
            WebElement AutorezationButton = webdriver.findElement(By.xpath("//button[@data-role='login-button']"));
            WebDriverWait waitForElement = new WebDriverWait(webdriver, 3);
            AutorezationButton.sendKeys(Keys.ENTER);
            waitForElement.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[contains(text(),'Войти с паролем')]")));
            webdriver.findElement(By.xpath("//div[@class='block-other-login-methods__password-caption']")).click();
            webdriver.findElement(By.xpath("//input[@autocomplete='username']")).sendKeys("lightsteach@yandex.ru");
            webdriver.findElement(By.xpath("//input[@autocomplete='current-password']")).sendKeys("test123456");
            webdriver.findElement(By.xpath("//div[@class='form-entry-with-password__main-button']//*[contains(text(),'Войти')]")).click();
            webdriver.findElement(By.xpath("//div[@class='header-profile__level']")).click();
            WebElement NickName = webdriver.findElement(By.xpath("//span[@class='header-profile__username'][contains(text(),'Пришелец-CY75848')]"));
            Assertions.assertEquals(Nik, NickName.getText());
            waitForElement.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[@href='/logout/']")));
            webdriver.findElement(By.xpath("//a[@href='/logout/']")).sendKeys(Keys.ENTER);
        }
    @Test
    @DisplayName("Смена локации")
        public void ChangeCityTest() {
            WebDriver webdriver=setUp();
            String location="Москва";
            WebElement LocalCity= webdriver.findElement(By.xpath("//ul[@class='header-top-menu__common-list']//div[@class='city-select w-choose-city-widget']//p"));
            System.out.println("Город по умолчанию: "+LocalCity.getText());
            webdriver.findElement(By.xpath("//a[@class='w-choose-city-widget pseudo-link pull-right']")).click();

            WebDriverWait waitForElement=new WebDriverWait(webdriver,3);
            By searchForm= By.xpath("//div[@class='base-modal select-city-modal base-modal_full-on-mobile']//div[contains(text(),'Ваш город')]");
            waitForElement.until(ExpectedConditions.presenceOfElementLocated(searchForm));
            webdriver.findElement(By.xpath("//input[@data-role='search-city']")).sendKeys("Москва");
            webdriver.findElement(By.xpath("//input[@data-role='search-city']")).sendKeys(Keys.ENTER);
            new WebDriverWait(webdriver,3).until(ExpectedConditions.presenceOfElementLocated(By.xpath("//ul[@class='header-top-menu__common-list']//div[@class='city-select w-choose-city-widget']//p")));
            By newLocation= By.xpath("//ul[@class='header-top-menu__common-list']//div[@class='city-select w-choose-city-widget']//p");
            Assertions.assertEquals(location,webdriver.findElement(newLocation).getText());
        }
    @Test
    @DisplayName("добавление товара в корзину")
        public void PutProductToCart() {
        WebDriver webdriver=setUp();
        String Search="//div[@class='header-menu-wrapper']//input[@placeholder='Поиск по сайту']";
        String UpToPrice="//input[@type='number'][contains(@placeholder, 'до')]";
        webdriver.findElement(By.xpath(Search)).sendKeys("утюг");
        webdriver.findElement(By.xpath(Search)).sendKeys(Keys.ENTER);
        new WebDriverWait(webdriver, 3).until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[@data-role='login-button']")));
        WebElement scroll= webdriver.findElement(By.xpath("//button[@data-role='login-button']"));
        scroll.sendKeys(Keys.PAGE_DOWN);
        webdriver.findElement(By.xpath(UpToPrice)).sendKeys("1000");
        webdriver.findElement(By.xpath(UpToPrice)).sendKeys(Keys.ENTER);
        new WebDriverWait(webdriver,2).until(ExpectedConditions.elementToBeSelected(By.xpath("//input[@data-max='1000']")));
        new WebDriverWait(webdriver, 3).until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[contains(text(),'Купить')]")));
        new WebDriverWait(webdriver, 2).until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class='product-buy__price product-buy__price_active'][contains(text(),'650')]")));
        String itemcardnumber=webdriver.findElement(By.xpath("//div[./div/div/div[contains(text(),'650')]]")).getAttribute("data-code");
        webdriver.findElement(By.xpath("//button[contains(text(),'Купить')]")).sendKeys(Keys.ENTER);
        webdriver.findElement(By.xpath("//a[@data-commerce-target='CART']")).sendKeys(Keys.ENTER);
        String addproductitem=webdriver.findElement(By.xpath("//div[@class='cart-items__product-code']/div")).getText();
        Assertions.assertEquals(addproductitem,itemcardnumber);
        webdriver.findElement(By.xpath("//div[@class='cart-items__wrapper']//preceding::button[@class='menu-control-button remove-button']")).sendKeys(Keys.ENTER);
    }
    @Test
    @DisplayName("специальные акции открываются в отдельном окне при клике из виджета тех поддержки")
        public void SpeshialPriseTest(){
        WebDriver webdriver=setUp();
        webdriver.findElement(By.xpath("//div[@data-role='chat-button']")).click();
        new WebDriverWait(webdriver,3).until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@data-role='chat-container']")));
        webdriver.findElement(By.xpath("//div[@data-role='chat-container']//a[contains(text(),'Акции')]")).click();
        List<String> windowsHand=new ArrayList(webdriver.getWindowHandles());
        Assumptions.assumeFalse(windowsHand.isEmpty());
        webdriver.switchTo().window(windowsHand.get(1));
        Assertions.assertEquals("https://www.dns-shop.ru/actions/",webdriver.getCurrentUrl());
    }
    @Test
    @DisplayName("проверка сохранения товара в избраном для залогиненого пользователя")
        public void AddProductToWishList(){
        WebDriver webdriver=setUp();
        webdriver.get("https://www.dns-shop.ru/product/dbe0f59a93743330/hlebopec-midea-bm-220-q3-w-belyj/");
        WebElement AutorezationButton = webdriver.findElement(By.xpath("//button[@data-role='login-button']"));
        AutorezationButton.sendKeys(Keys.ENTER);
        WebDriverWait waitForElement = new WebDriverWait(webdriver, 3);
        waitForElement.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[contains(text(),'Войти с паролем')]")));
        webdriver.findElement(By.xpath("//div[@class='block-other-login-methods__password-caption']")).click();
        webdriver.findElement(By.xpath("//input[@autocomplete='username']")).sendKeys("lightsteach@yandex.ru");
        webdriver.findElement(By.xpath("//input[@autocomplete='current-password']")).sendKeys("test123456");
        webdriver.findElement(By.xpath("//div[@class='form-entry-with-password__main-button']//*[contains(text(),'Войти')]")).click();
        webdriver.findElement(By.xpath("//div[@class='header-profile__level']")).click();
        waitForElement.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//span[@class='header-profile__username'][contains(text(),'Пришелец-CY75848')]")));
        String itemNumber=webdriver.findElement(By.xpath("//div[@class='product-card-top__code']")).getText();
        waitForElement.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class='product-buy product-buy_one-line']//button[@class='button-ui button-ui_white button-ui_icon wishlist-btn']")));
        webdriver.findElement(By.xpath("//div[@class='product-buy product-buy_one-line']//button[@class='button-ui button-ui_white button-ui_icon wishlist-btn']")).sendKeys(Keys.ENTER);
        webdriver.findElement(By.xpath("//a[@class='ui-link wishlist-link']")).sendKeys(Keys.ENTER);
        System.out.println(itemNumber);
        String additemnumber = webdriver.findElement(By.xpath("//div[@data-id='product']")).getAttribute("data-code");
        System.out.println(additemnumber);
        Assertions.assertTrue(itemNumber.contains(additemnumber));
    }
    }

