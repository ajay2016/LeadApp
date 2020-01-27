package com.leadapp.testcases;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.leadapp.base.BaseTest;
import com.leadapp.utility.Xls_Reader;

public class CreateLeadTest extends BaseTest {
	
	

	@Test
	public void createLead() {

		// Extent report
		test = extent.createTest("Create Lead Test");
		test.log(Status.INFO, "Create Lead in Database");

		// xls file initialization
		Xls_Reader xls = new Xls_Reader(
				System.getProperty("user.dir") + "\\src\\main\\java\\com\\leadapp\\testdata\\testdata.xlsx");

		// get row count
		int rowCount = xls.getRowCount("CreateLeadTestData");
		
		// we need data from 2 row
				// parametarization
				for (int rowNum = 2; rowNum <= rowCount; rowNum++) {
					
					
					String browser = xls.getCellData("CreateLeadTestData", "Browser", rowNum);
					System.out.println(browser);
					openBrowser(browser);
					driver.get("http://localhost:9090/leadapp/");
					driver.findElement(By.name("email")).sendKeys("user@javachap.com");
					driver.findElement(By.name("password")).sendKeys("javachap");
					driver.findElement(By.name("password")).sendKeys(Keys.ENTER);
					
					//click on new lead
					driver.findElement(By.xpath("//a[@href='/leadapp/lead.do']")).click();
					
					//category
					Select categorySelect = new Select(driver.findElement(By.xpath("//select[@name='category']")));
					String category = xls.getCellData("CreateLeadTestData", "Category", rowNum);
					System.out.println(category);
					categorySelect.selectByVisibleText(category);
					
					//leadtitle
					String leadtitle = xls.getCellData("CreateLeadTestData", "Lead Title", rowNum);
					System.out.println(leadtitle);
					driver.findElement(By.xpath("//*[@id='Field0']")).sendKeys(leadtitle);
					
					//wait(2);
					
					//Description 
					String description = xls.getCellData("CreateLeadTestData", "Description", rowNum);
					System.out.println(description);
					driver.findElement(By.xpath("//*[@id='Field1']")).sendKeys(description);
					
					//First Name
					String firstname = xls.getCellData("CreateLeadTestData", "First Name", rowNum);
					System.out.println(firstname);
					driver.findElement(By.xpath("//*[@id='Field4']")).sendKeys(firstname);
					
					//Last Name
					String lastname = xls.getCellData("CreateLeadTestData", "First Name", rowNum);
					System.out.println(lastname);
					driver.findElement(By.xpath("//*[@id='Field5']")).sendKeys(lastname);
					
					//Email
					String email = xls.getCellData("CreateLeadTestData", "Email", rowNum);
					System.out.println(email);
					driver.findElement(By.xpath("//*[@id='Field6']")).sendKeys(email);
					
					//Phone
					String phone = xls.getCellData("CreateLeadTestData", "Phone", rowNum);
					System.out.println(phone);
					driver.findElement(By.xpath("//*[@id='Field8']")).sendKeys(phone);
					
					//wait(2);
					
					//Price 
					String price = xls.getCellData("CreateLeadTestData", "Price", rowNum);
					System.out.println(price);
					driver.findElement(By.xpath("//*[@id='Field9']")).sendKeys(price);
					
					//Submit					
					driver.findElement(By.xpath("//*[@id='saveForm']")).click();
					
					// validate if lead is generated from fe
					// connect to db and validate lead
					try {
						PreparedStatement pstmt = conn.prepareStatement("select * from lead order by LD_ID desc");
						ResultSet rs =pstmt.executeQuery();
						if(rs.next()){
							String dbFirstName=rs.getString("LD_FIRST_NAME");
							String dbLastName=rs.getString("LD_LAST_NAME");
							
							Assert.assertEquals(firstname, dbFirstName);
							Assert.assertEquals(lastname, dbLastName);
						}
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
					
					
				//	closeBrowser();

					
				}
				
				}

	}

