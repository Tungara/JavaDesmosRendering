import java.lang.*;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.By;

public class Desmos_Driver {
    public static WebDriver driver = new ChromeDriver();
    public static JavascriptExecutor js = (JavascriptExecutor) driver;

    public static void Setup() {
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
        driver.get("https://www.desmos.com/calculator");

        driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
    }

    public static void startState() {
        js.executeScript("Calc.setDefaultState(Calc.getState());");
    }

    public static void setViewport(int top, int bottom, int left, int right) {
        js.executeScript("Calc.setMathBounds({left: " + left + ", right: " + right + ", bottom: " + bottom + ", top: " + top + "});");
    }

    public static void graphLatex(String latex) throws InterruptedException {
        js.executeScript("Calc.setExpression({latex: '"+latex+"', color:'#000000', lineWidth: 1});");
    }

    public static void Reset() {
        driver.findElement(By.xpath("//*[@id=\"graph-container\"]/div/div/div/div[1]/div/div[2]/div[1]/div[3]/div[1]/span")).click();
        driver.findElement(By.xpath("//*[@id=\"graph-container\"]/div/div/div/div[1]/div/div[2]/div[1]/div[1]/span")).click();
    }

}
