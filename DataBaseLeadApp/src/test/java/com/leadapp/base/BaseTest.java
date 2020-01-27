package com.leadapp.base;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeDriverService;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.io.FileHandler;
import org.testng.ITestResult;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;

public class BaseTest {

	public ExtentTest test;
	public ExtentReports extent;
	public static WebDriver driver;
	String url = "jdbc:mysql://localhost:3306/";
	String dbDriver = "com.mysql.jdbc.Driver";
	String dbName = "lead";
	protected Connection conn = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;

	String userName = "root";
	String password = "root";
	// public String browser = "Mozilla";

	/*
	 * @BeforeSuite: The annotated method will be run before all tests in this suite
	 * have run.
	 * 
	 * @AfterSuite: The annotated method will be run after all tests in this suite
	 * have run.
	 * 
	 * @BeforeTest: The annotated method will be run before any test method
	 * belonging to the classes inside the <test> tag is run.
	 * 
	 * @AfterTest: The annotated method will be run after all the test methods
	 * belonging to the classes inside the <test> tag have run.
	 * 
	 * @BeforeGroups: The list of groups that this configuration method will run
	 * before. This method is guaranteed to run shortly before the first test method
	 * that belongs to any of these groups is invoked.
	 * 
	 * @AfterGroups: The list of groups that this configuration method will run
	 * after. This method is guaranteed to run shortly after the last test method
	 * that belongs to any of these groups is invoked.
	 * 
	 * @BeforeClass: The annotated method will be run before the first test method
	 * in the current class is invoked.
	 * 
	 * @AfterClass: The annotated method will be run after all the test methods in
	 * the current class have been run.
	 * 
	 * @BeforeMethod: The annotated method will be run before each test method.
	 * 
	 * @AfterMethod: The annotated method will be run after each test method.
	 */

	@BeforeTest
	public void connect() {
		try {
			Class.forName(dbDriver).newInstance();
			conn = DriverManager.getConnection(url + dbName, userName, password);
		} catch (Exception e) {
			System.out.println("Could not establish connection");
			e.printStackTrace();
			// Assert.fail("Could not establish connection");
			throw new SkipException("Could not establish connection");
		} // create object of Driver

	}

	@BeforeMethod

	public void setupExtentandDatabase() {

		// Extent Report
		ExtentHtmlReporter reporter = new ExtentHtmlReporter(
				System.getProperty("user.dir") + "\\Reports\\" + getCurrentDateTime() + ".html");
		extent = new ExtentReports();
		extent.attachReporter(reporter);

		/*
		 * // Database connection try { Class.forName(dbDriver).newInstance(); conn =
		 * DriverManager.getConnection(url + dbName, userName, password); } catch
		 * (Exception e) { System.out.println("Could not establish connection");
		 * e.printStackTrace(); // Assert.fail("Could not establish connection"); throw
		 * new SkipException("Could not establish connection"); } // create object of
		 * Driver
		 */
	}

	@AfterMethod
	// runs after every testcases
	// after test is finished ItestResults has all the information
	public void tearDownMethod(ITestResult result) throws IOException {

		// caputure screenshot on failure
		if (result.getStatus() == ITestResult.FAILURE) {
			// Helper.captureScreenshot(driver);
			test.fail("Test Failed", MediaEntityBuilder.createScreenCaptureFromPath(captureScreenshot(driver)).build());

		} /*
			 * else if (result.getStatus() == ITestResult.SUCCESS) { //
			 * Helper.captureScreenshot(driver); test.pass("Test Passed",
			 * MediaEntityBuilder.createScreenCaptureFromPath(Helper.captureScreenshot(
			 * driver)).build());
			 * 
			 * }
			 */

		extent.flush();
		if (driver != null) {

			driver.quit();
		}

	}

	@AfterTest
	public void disConnect() {

		try {
			if (rs != null)
				rs.close();

			if (pstmt != null)
				pstmt.close();

			if ((conn != null) && (!conn.isClosed()))
				conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	// screenshot

	public static String captureScreenshot(WebDriver driver) {

		String screenshotpath = System.getProperty("user.dir") + "\\Screenshots\\" + getCurrentDateTime() + ".png";
		File srecFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		File DestFile = new File(screenshotpath);
		try {
			FileHandler.copy(srecFile, DestFile);
		} catch (IOException e) {
			System.out.println("Unable to capture screenshot" + e.getMessage());
			e.printStackTrace();
		}
		return screenshotpath;

	}

	public static String getCurrentDateTime() {

		DateFormat customFormat = new SimpleDateFormat("yyyy_MM_dd_HHmmss");
		Date currentDate = new Date();
		return customFormat.format(currentDate);

	}

	public void wait(int time) {
		try {
			Thread.sleep(time * 1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void openBrowser(String browserType) {

		test.log(Status.INFO, "Opening browser  " + browserType);

		/*
		 * if (prop == null) { prop = new Properties(); FileInputStream file = new
		 * FileInputStream( System.getProperty("user.dir") +
		 * "//src//test//resources//projectconfig.properties"); prop.load(file); }
		 */
		// System.out.println(prop.get("appurl"));
		if (browserType.equals("chrome")) {
			System.setProperty("webdriver.chrome.driver",
					System.getProperty("user.dir") + "\\drivers\\chromedriver.exe");
			System.setProperty(ChromeDriverService.CHROME_DRIVER_LOG_PROPERTY, "null");
			driver = new ChromeDriver();

		} else if (browserType.equals("firefox")) {
			System.setProperty("webdriver.gecko.driver", System.getProperty("user.dir") + "\\drivers\\geckodriver.exe");
			System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE, "null");
			driver = new FirefoxDriver();

		} else if (browserType.equals("ie")) {
			System.setProperty("webdriver.ie.driver", System.getProperty("user.dir") + "\\drivers\\IEDriverServer.exe");
			driver = new InternetExplorerDriver();

		} else if (browserType.equals("Edge")) {
			System.setProperty(EdgeDriverService.EDGE_DRIVER_EXE_PROPERTY, "null");
			System.setProperty(EdgeDriverService.EDGE_DRIVER_LOG_PROPERTY, "null");
			driver = new EdgeDriver();
		}

		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
		driver.manage().timeouts().setScriptTimeout(30, TimeUnit.SECONDS);

		// System.out.println("Open Browser");

	}

	public void closeBrowser() {
		test.log(Status.INFO, "Closing Browser");
		driver.close();
	}
}