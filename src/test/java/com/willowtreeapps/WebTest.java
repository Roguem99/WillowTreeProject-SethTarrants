package com.willowtreeapps;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

public class WebTest {

    private WebDriver driver;

    /**
     * Change the prop if you are on Windows or Linux to the corresponding file type
     * The chrome WebDrivers are included on the root of this project, to get the
     * latest versions go to https://sites.google.com/a/chromium.org/chromedriver/downloads
     */
    @Before
    public void setup() {
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
        Capabilities capabilities = DesiredCapabilities.chrome();
        driver = new ChromeDriver(capabilities);
        driver.navigate().to("http://www.ericrochester.com/name-game/");
    }

    @Test
    public void test_validate_title_is_present() {
        new HomePage(driver)
                .validateTitleIsPresent();
    }

    @Test
    public void test_clicking_photo_increases_tries_counter() {
        new HomePage(driver)
                .validateClickingFirstPhotoIncreasesTriesCounter();
    }

    @Test
    public void test_verify_streak_counter_properly_increments_after_five_clicks() {
        new HomePage(driver).validateClickingFirstPhotoIncreasesTriesCounterAfterNumberOfClicks(4);
    }

    @Test
    public void test_verify_streak_counter_resets_upon_incorrect_selection() {
        new HomePage(driver).validateStreakResetCondition();
    }

    @Test
    public void test_verify_after_ten_random_selections_correct_increments_of_counters() {
        new HomePage(driver).validateTriesAndCorrectCountersAfterRandomSelections(10);
    }

    @Test
    public void test_verify_new_active_match_session_begins_after_previous_completed_session() {
        new HomePage(driver).validateNewActiveMatchSessionAfterCorrectMatch();
    }

    @Test
    public void test_bonus_round_validate_incorrect_selections_appear_more_frequently_than_correct_selections() {
        new HomePage(driver).validateIncorrectFrequencyIsHigherComparedToCorrectFrequency();
    }


    @After
    public void teardown() {
        driver.quit();
        System.clearProperty("webdriver.chrome.driver");
    }

}
