package com.lmk.demo.task;

import com.lmk.demo.entity.TaskInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.util.CollectionUtils;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class GoldTask {

    public static void doJob() {
        Map<String, String> map = new HashMap<>();
        map.put("快兔","a657781930");
        map.put("yuyuji","P@ssw0rd");
        map.put("3521328348","88888888");
        for (Map.Entry<String, String> entry : map.entrySet()) {
            log.info("用户：{}，获取金币!", entry.getKey().trim());
            acquiringGoldCoinsByUser(entry.getKey().trim(), entry.getValue().trim());
        }
    }

    private static void acquiringGoldCoinsByUser(String name,String pwd) {
        WebDriver driver = null;
        try {
            ChromeOptions options = new ChromeOptions();
            /**
             * ERROR com.lmk.demo.task.GoldTask - unknown error: Chrome failed to start: exited abnormally
             * (unknown error: DevToolsActivePort file doesn't exist)
             * (The process started from chrome location /usr/bin/google-chrome is no longer running, so ChromeDriver is assuming that Chrome has crashed.)
             * Build info: version: 'unknown', revision: 'unknown', time: 'unknown'
             */
            // linux, 加上下面两行，解决报错
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");

            options.addArguments("blink-settings=imagesEnabled=false"); //不加载图片, 提升速度
            options.addArguments("--headless"); //浏览器不提供可视化页面. linux下如果系统不支持可视化不加这条会启动失败
            options.addArguments("--disable-gpu"); // 谷歌文档提到需要加上这个属性来规避bug
            // options.addArguments("disable-infobars");// 设置chrome浏览器的参数，使其不弹框提示（chrome正在受自动测试软件的控制）
            driver = new ChromeDriver(options);
        } catch (Exception e) {
            log.error("启动谷歌浏览器驱动报错：" + e.getMessage());
        }

        // 登录
        login(name, pwd, driver);

        if (!isLogin(driver)) {
            log.error("当前时间：{}，用户：{}，登录失败!",new Date(), name);
            return;
        }

        List<TaskInfo> taskInfoList = new ArrayList<>();
        TaskInfo task1 = new TaskInfo(
                By.xpath("//*[@id=\"main\"]/table/tbody/tr/td[2]/div[1]/table/tbody/tr[2]/td[4]"),
                0,
                null,
                null);
        TaskInfo task2 = new TaskInfo(
                By.xpath("//*[@id=\"main\"]/table/tbody/tr/td[2]/div[1]/table/tbody/tr[5]/td[4]"),
                0,
                null,
                null);
        taskInfoList.add(task1);
        taskInfoList.add(task2);

        // 领取任务
        getTask(taskInfoList, driver);

        // 执行任务
        Boolean isGet = doTask(taskInfoList, driver);

        if (isGet) {
            log.info("当前时间：{}，领取成功!", new Date());
        } else {
            log.error("当前时间：{}，领取失败!", new Date());
        }

        // 查看用户金币
        watchGoldInfo(driver, name);

        try {
            driver.close();
        } catch (Exception e) {
            log.error("关闭谷歌浏览器驱动报错：" + e.getMessage());
        }

        log.info("用户：{}，结束退出!", name);
    }

    private static void watchGoldInfo(WebDriver driver, String name) {
        String url = null;

        if ("快兔".equals(name)) {
            url = "https://bbs.imoutolove.me/u.php?action-show-uid-1193138.html";
        } else if ("yuyuji".equals(name)) {
            url = "https://bbs.imoutolove.me/u.php?action-show-uid-1200472.html";
        } else if ("3521328348".equals(name)) {
            url = "https://bbs.imoutolove.me/u.php?action-show-uid-1248069.html";
        }

        if (StringUtils.isEmpty(url)) {
            return;
        }
        driver.get(url);
        WebElement webElement = driver.findElement(By.xpath("//*[@id=\"u-profile-s\"]/table/tbody/tr[9]/th"));
        log.info("用户：{}，当前的金币是：{}", name, webElement.getText());
    }

    private static void getTask(List<TaskInfo> taskInfoList, WebDriver driver) {
        if (CollectionUtils.isEmpty(taskInfoList)) {
            return;
        }
        driver.get("https://bbs.imoutolove.me/plugin.php?H_name-tasks.html");
        for (TaskInfo taskInfo : taskInfoList) {
            WebElement webElement = driver.findElement(taskInfo.getXpath1());
            taskInfo.setTaskText(webElement.getText());
            taskInfo.setWebElement(webElement);
            taskInfo.setRemainingHour(getHour(webElement.getText()));
        }
    }

    private static int getHour(String text) {
        if (StringUtils.isEmpty(text)) {
            return 0;
        }
        Pattern p = Pattern.compile("[^0-9]");
        Matcher m = p.matcher(text);
        String result = m.replaceAll("");
        return StringUtils.isEmpty(result) ? 0 : Integer.valueOf(result);
    }

    private static Boolean doTask(List<TaskInfo> taskInfoList, WebDriver driver) {
        if (CollectionUtils.isEmpty(taskInfoList)) {
            return false;
        }

        for (Iterator<TaskInfo> it = taskInfoList.iterator(); it.hasNext(); ) {
            TaskInfo taskInfo = it.next();
            if (taskInfo.getRemainingHour() > 0) {
                log.error("当前时间：{}，领取失败，失败原因：{}", new Date(), taskInfo.getTaskText());
                it.remove();
                continue;
            }
            WebElement webElement = taskInfo.getWebElement();
            if (webElement == null) {
                continue;
            }

            WebElement buttn = webElement.findElement(By.tagName("a"));
            if (buttn == null) {
                continue;
            }
            buttn.click();
        }

        if (CollectionUtils.isEmpty(taskInfoList)) {
            return false;
        }

        for (TaskInfo taskInfo : taskInfoList) {
            driver.get("https://bbs.imoutolove.me/plugin.php?H_name-tasks-actions-newtasks.html.html");
            try {
                WebElement job = driver.findElement(By.xpath("//*[@id=\"newid_15\"]/tr[1]/td[4]"));

                WebElement buttn = job.findElement(By.tagName("a"));
                if (buttn == null) {
                    continue;
                }
                buttn.click();
            } catch (Exception e) {
                log.error(e.getMessage());
            }

        }
        return true;
    }

    private static boolean isLogin(WebDriver driver) {
        driver.get("https://bbs.imoutolove.me/index.php");
        WebElement userName = driver.findElement(By.xpath("//*[@id=\"user-login\"]/a[1]"));
        if (userName != null) {
            return (StringUtils.isNotEmpty(userName.getText()) && !userName.getText().equals("登录"));

        }
        return false;
    }

    private static void login(String name, String pwd, WebDriver driver){
        driver.get("https://bbs.imoutolove.me/login.php");
        WebElement nameElement = driver.findElement(By.xpath("//*[@id=\"main\"]/form/div/table/tbody/tr[2]/td/input"));
        WebElement pwdElement = driver.findElement(By.xpath("//*[@id=\"main\"]/form/div/table/tbody/tr[3]/td/input"));
        nameElement.sendKeys(name);
        pwdElement.sendKeys(pwd);
        pwdElement.submit();
    }
}
