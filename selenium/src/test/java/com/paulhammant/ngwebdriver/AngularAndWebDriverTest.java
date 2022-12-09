package com.paulhammant.ngwebdriver;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.MovedContextHandler;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.seleniumhq.selenium.fluent.*;
import org.testng.annotations.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.openqa.selenium.By.*;
import static org.testng.Assert.assertEquals;
import static org.testng.AssertJUnit.fail;

public class AngularAndWebDriverTest {

    private ChromeDriver driver;
    private Server webServer;
    private NgWebDriver ngWebDriver;

    @BeforeSuite
    public void before_suite() throws Exception {

        // Launch Protractor's own test app on http://localhost:8080
        //((StdErrLog) Log.getRootLogger()).setLevel(StdErrLog.LEVEL_OFF);
        webServer = new Server(new QueuedThreadPool(6));
        ServerConnector connector = new ServerConnector(webServer, new HttpConnectionFactory());
        connector.setPort(8080);
        webServer.addConnector(connector);
        ResourceHandler resource_handler = new ResourceHandler();
        resource_handler.setDirectoriesListed(true);
        resource_handler.setWelcomeFiles(new String[]{"index.html"});
        resource_handler.setResourceBase("src/test/webapp");
        HandlerList handlers = new HandlerList();
        MovedContextHandler effective_symlink = new MovedContextHandler(webServer, "/lib/angular", "/lib/angular_v1.2.9");
        handlers.setHandlers(new Handler[] { effective_symlink, resource_handler, new DefaultHandler() });
        webServer.setHandler(handlers);
        webServer.start();
        System.setProperty("webdriver.chrome.driver","C:\\Users\\Administrator\\Downloads\\chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().timeouts().setScriptTimeout(30, TimeUnit.SECONDS);
        ngWebDriver = new NgWebDriver(driver);
    }

    @AfterSuite
    public void after_suite() throws Exception {
        driver.quit();
        webServer.stop();
    }

    @BeforeMethod
    public void resetBrowser() {
        driver.get("about:blank");
    }


    // Model interaction

    @Test(priority = 1)
    public void model_mutation_and_query_is_possible() throws InterruptedException {

        driver.get("http://13.228.78.72:4200/");
        ngWebDriver.waitForAngularRequestsToFinish();

        WebElement loginButton = driver.findElement(FluentBy.attribute("class", "mat-focus-indicator login-button mat-raised-button mat-button-base"));
        loginButton.click();
        ngWebDriver.waitForAngularRequestsToFinish();
        WebElement fn = driver.findElement(FluentBy.attribute("formcontrolname", "username"));
        fn.sendKeys("thach9472");
        WebElement ln = driver.findElement(FluentBy.attribute("formcontrolname", "password"));
        ln.sendKeys("12345678");
        WebElement wholeForm = driver.findElement(FluentBy.attribute("name", "loginForm"));
        wholeForm.submit();
        System.out.println("Test chua qua waitting");
//        ngWebDriver.waitForAngularRequestsToFinish();
        Thread.sleep(7000);
        System.out.println("Test da qua waitting");
//        String check=driver.getCurrentUrl()!="http://13.228.78.72:4200/homepage/active-calendar"?"0":"1";
        WebElement userInfo = driver.findElement(FluentBy.attribute("name", "user"));
        String check=userInfo!=null&&userInfo.isDisplayed()?"1":"0";

        System.out.println("Done with check:"+ check);
        assertThat(check, is("1"));
    }


}
