package com.shaik.testteam;

import java.time.LocalDate;
import java.time.Period;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


public class Email
{
	protected final static Logger log= Logger.getLogger(Email.class);
	
	@BeforeClass()	
	public void setLogger()
	{		
		PropertyConfigurator.configure(System.getProperty("user.dir")+"//log4j.properties");
	}
	
	@Test(priority = 1)
	public void JoinedDate()
	{
		try
		{	
			log.info("**** -- Started JoinedDate Test -- *****");
			String strFilePath = System.getProperty("user.dir") +"//Data.xlsx";
			XLSXFileController reader = new XLSXFileController(strFilePath);
			int excelRowCount = reader.getRowCount("JoinedData");
			System.out.println("****  "+excelRowCount+"   *****");
			String Date, Month, Year, Name, EmailID;
			LocalDate joinedDate, todayDate;
			Period agewithCompany;
			int years, months, days;
			for (int i = 2; i <= excelRowCount; i++) {
				Date = reader.getCellData("JoinedData", "Date", i).split("\\.")[0];
				Month = reader.getCellData("JoinedData", "Month", i).split("\\.")[0];
				Year = reader.getCellData("JoinedData", "Year", i).split("\\.")[0];
				Name = reader.getCellData("JoinedData", "Name", i);
				EmailID = reader.getCellData("JoinedData", "EmailID", i);
				
				joinedDate = LocalDate.of(Integer.parseInt(Year), MonthName_Number(Month), Integer.parseInt(Date));
				todayDate = LocalDate.now();
				agewithCompany = Period.between(joinedDate, todayDate);
				years = agewithCompany.getYears();
				months = agewithCompany.getMonths();
				days = agewithCompany.getDays();
				
				if (months == 0 && days == 0)
				{
					log.info("number of years completed by "+ Name +" : " + years); 
					log.info("Email ID of "+ Name +" is : " + EmailID);
					EmailController.sendAnniversaryEmailWithAttachments(EmailID, Name, years);
				}
			}
		}
		catch(Exception ex)
		{
			log.error("Class: "+Thread.currentThread().getStackTrace()[1].getClassName() +"| Method: "+ex.getStackTrace()[0].getMethodName() +"| Exception desc : " + ex.getMessage());		
		}
	}
	
	@Test(priority = 2)
	public void BirthDay()
	{
		try
		{	
			log.info("**** -- Started Birthday Test-- *****");
			String strFilePath = System.getProperty("user.dir") +"//Data.xlsx";
			XLSXFileController reader = new XLSXFileController(strFilePath);
			int excelRowCount = reader.getRowCount("BDayData");
			System.out.println("****  "+excelRowCount+"   *****");
			String Date, Month, Name, EmailID, presentMonth;
			int presentdate;
			for (int i = 2; i <= excelRowCount; i++) {
				Date = reader.getCellData("BDayData", "Date", i).split("\\.")[0];
				Month = reader.getCellData("BDayData", "Month", i).split("\\.")[0];
				Name = reader.getCellData("BDayData", "Name", i);
				EmailID = reader.getCellData("BDayData", "EmailID", i);
			
				presentMonth = LocalDate.now().getMonth().toString();
				presentdate = LocalDate.now().getDayOfMonth();
						
				if (Month.equalsIgnoreCase(presentMonth) && Integer.parseInt(Date) == presentdate)
				{
					log.info("Today is "+ Name +" BirthDay "); 
					log.info("Email ID of "+ Name +" is : " + EmailID);
					EmailController.sendBirthDayEmailWithAttachments(EmailID, Name);
				}
			}
		}
		catch(Exception ex)
		{
			log.error("Class: "+Thread.currentThread().getStackTrace()[1].getClassName() +"| Method: "+ex.getStackTrace()[0].getMethodName() +"| Exception desc : " + ex.getMessage());		
		}
	}
	
	private static int MonthName_Number(String MonthName)
	{
		int monthNumber = 0;
		try
		{
			switch (MonthName) 
			{
			case "January":   monthNumber =1;  break;
			case "February":  monthNumber =2;  break;
			case "March":     monthNumber =3;  break;
			case "April": 	  monthNumber =4;  break;
			case "May":       monthNumber =5;  break;
			case "June":      monthNumber =6;  break;
			case "July":      monthNumber =7;  break;
			case "August":    monthNumber =8;  break;	
			case "September": monthNumber =9;  break;
			case "October":   monthNumber =10; break;
			case "November":  monthNumber =11; break;
			case "December":  monthNumber =12; break;
			}
		}
		catch(Exception ex)
		{
			log.info("Class: " + Thread.currentThread().getStackTrace()[1].getClassName() + "| Method: " + ex.getStackTrace()[0].getMethodName() + "| Exception desc : " + ex.getMessage());
		}
		return monthNumber;
	}

}
