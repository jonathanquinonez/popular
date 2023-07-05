package com.popular.android.mibanco.object;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BankLocationDetail {

    private int id = -1;
    private String sundayOpeningTime;
    private String sundayClosingTime;
    private String mondayOpeningTime;
    private String mondayClosingTime;
    private String tuesdayOpeningTime;
    private String tuesdayClosingTime;
    private String wednesdayOpeningTime;
    private String wednesdayClosingTime;
    private String thursdayOpeningTime;
    private String thursdayClosingTime;
    private String fridayOpeningTime;
    private String fridayClosingTime;
    private String saturdayOpeningTime;
    private String saturdayClosingTime;
    private String phone;
    private String holidayNameEnglish;
    private String holidayNameSpanish;
    private String holidayDescriptionEnglish;
    private String holidayDescriptionSpanish;
    private Date holidayStartDate;
    private Date holidayEndDate;
    private String holidayOpeningTime;
    private String holidayClosingTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSundayOpeningTime() {
        return sundayOpeningTime;
    }

    public void setSundayOpeningTime(String sundayOpeningTime) {
        this.sundayOpeningTime = sundayOpeningTime;
    }

    public String getSundayClosingTime() {
        return sundayClosingTime;
    }

    public void setSundayClosingTime(String sundayClosingTime) {
        this.sundayClosingTime = sundayClosingTime;
    }

    public String getMondayOpeningTime() {
        return mondayOpeningTime;
    }

    public void setMondayOpeningTime(String mondayOpeningTime) {
        this.mondayOpeningTime = mondayOpeningTime;
    }

    public String getMondayClosingTime() {
        return mondayClosingTime;
    }

    public void setMondayClosingTime(String mondayClosingTime) {
        this.mondayClosingTime = mondayClosingTime;
    }

    public String getTuesdayOpeningTime() {
        return tuesdayOpeningTime;
    }

    public void setTuesdayOpeningTime(String tuesdayOpeningTime) {
        this.tuesdayOpeningTime = tuesdayOpeningTime;
    }

    public String getTuesdayClosingTime() {
        return tuesdayClosingTime;
    }

    public void setTuesdayClosingTime(String tuesdayClosingTime) {
        this.tuesdayClosingTime = tuesdayClosingTime;
    }

    public String getWednesdayOpeningTime() {
        return wednesdayOpeningTime;
    }

    public void setWednesdayOpeningTime(String wednesdayOpeningTime) {
        this.wednesdayOpeningTime = wednesdayOpeningTime;
    }

    public String getWednesdayClosingTime() {
        return wednesdayClosingTime;
    }

    public void setWednesdayClosingTime(String wednesdayClosingTime) {
        this.wednesdayClosingTime = wednesdayClosingTime;
    }

    public String getThursdayOpeningTime() {
        return thursdayOpeningTime;
    }

    public void setThursdayOpeningTime(String thursdayOpeningTime) {
        this.thursdayOpeningTime = thursdayOpeningTime;
    }

    public String getThursdayClosingTime() {
        return thursdayClosingTime;
    }

    public void setThursdayClosingTime(String thursdayClosingTime) {
        this.thursdayClosingTime = thursdayClosingTime;
    }

    public String getFridayOpeningTime() {
        return fridayOpeningTime;
    }

    public void setFridayOpeningTime(String fridayOpeningTime) {
        this.fridayOpeningTime = fridayOpeningTime;
    }

    public String getFridayClosingTime() {
        return fridayClosingTime;
    }

    public void setFridayClosingTime(String fridayClosingTime) {
        this.fridayClosingTime = fridayClosingTime;
    }

    public String getSaturdayOpeningTime() {
        return saturdayOpeningTime;
    }

    public void setSaturdayOpeningTime(String saturdayOpeningTime) {
        this.saturdayOpeningTime = saturdayOpeningTime;
    }

    public String getSaturdayClosingTime() {
        return saturdayClosingTime;
    }

    public void setSaturdayClosingTime(String saturdayClosingTime) {
        this.saturdayClosingTime = saturdayClosingTime;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getHolidayNameEnglish() {
        return holidayNameEnglish;
    }

    public void setHolidayNameEnglish(String holidayNameEnglish) {
        this.holidayNameEnglish = holidayNameEnglish;
    }

    public String getHolidayNameSpanish() {
        return holidayNameSpanish;
    }

    public void setHolidayNameSpanish(String holidayNameSpanish) {
        this.holidayNameSpanish = holidayNameSpanish;
    }

    public String getHolidayDescriptionEnglish() {
        return holidayDescriptionEnglish;
    }

    public void setHolidayDescriptionEnglish(String holidayDescriptionEnglish) {
        this.holidayDescriptionEnglish = holidayDescriptionEnglish;
    }

    public String getHolidayDescriptionSpanish() {
        return holidayDescriptionSpanish;
    }

    public void setHolidayDescriptionSpanish(String holidayDescriptionSpanish) {
        this.holidayDescriptionSpanish = holidayDescriptionSpanish;
    }

    public Date getHolidayStartDate() {
        return holidayStartDate;
    }

    public void setHolidayStartDate(String holidayStartDate) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        Date date = null;
        try {
            date = format.parse(holidayStartDate);
        }catch (Exception e){
        }
        this.holidayStartDate = date;
    }

    public Date getHolidayEndDate() {
        return holidayEndDate;
    }

    public void setHolidayEndDate(String holidayEndDate) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        Date date = null;
        try {
            date = format.parse(holidayEndDate);
        }catch (Exception e){
        }
        this.holidayEndDate = date;
    }

    public String getHolidayOpeningTime() {
        return holidayOpeningTime;
    }

    public void setHolidayOpeningTime(String holidayOpeningTime) {
        this.holidayOpeningTime = holidayOpeningTime;
    }

    public String getHolidayClosingTime() {
        return holidayClosingTime;
    }

    public void setHolidayClosingTime(String holidayClosingTime) {
        this.holidayClosingTime = holidayClosingTime;
    }

}
