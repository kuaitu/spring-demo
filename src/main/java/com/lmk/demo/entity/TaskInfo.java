package com.lmk.demo.entity;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


public class TaskInfo {
    private By xpath1;
    private int remainingHour;
    private String taskText;
    private WebElement webElement;

    public TaskInfo(By xpath1, int remainingHour, String taskText, WebElement webElement) {
        this.xpath1 = xpath1;
        this.remainingHour = remainingHour;
        this.taskText = taskText;
        this.webElement = webElement;
    }

    public WebElement getWebElement() {
        return webElement;
    }

    public void setWebElement(WebElement webElement) {
        this.webElement = webElement;
    }

    public String getTaskText() {
        return taskText;
    }

    public void setTaskText(String taskText) {
        this.taskText = taskText;
    }

    public By getXpath1() {
        return xpath1;
    }

    public void setXpath1(By xpath1) {
        this.xpath1 = xpath1;
    }

    public int getRemainingHour() {
        return remainingHour;
    }

    public void setRemainingHour(int remainingHour) {
        this.remainingHour = remainingHour;
    }
}
