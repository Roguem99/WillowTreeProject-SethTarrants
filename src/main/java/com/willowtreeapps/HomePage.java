package com.willowtreeapps;

import com.google.gson.JsonArray;
import com.google.gson.annotations.JsonAdapter;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.lang.reflect.Constructor;
import java.util.*;

/**
 * Created on 5/23/17.
 */
public class HomePage extends BasePage {


    public HomePage(WebDriver driver) {
        super(driver);
    }

    public void validateTitleIsPresent() {
        WebElement title = driver.findElement(By.cssSelector("h1"));
        Assert.assertTrue(title != null);
    }


    public void validateClickingFirstPhotoIncreasesTriesCounter() {
        //Wait for page to load
        sleep(6000);

        int count = Integer.parseInt(driver.findElement(By.className("attempts")).getText());

        driver.findElement(By.className("photo")).click();

        sleep(6000);

        int countAfter = Integer.parseInt(driver.findElement(By.className("attempts")).getText());

        Assert.assertTrue(countAfter > count);

    }

    public void validateClickingFirstPhotoIncreasesTriesCounterAfterNumberOfClicks(int numOfTries) {
        //Wait for page to load
        sleep(6000);

        int initCount = Integer.parseInt(driver.findElement(By.className("attempts")).getText());
        List<WebElement> colleaguesToSelect = driver.findElements(By.className("name"));
        String nameToMatch = driver.findElement(By.id("name")).getText();

        for (int i = 0; i <= numOfTries; i++) {
            String selectionToTestName = colleaguesToSelect.get(i).getText();
            if(nameToMatch.equals(selectionToTestName)){
                continue;
            }
            driver.findElements(By.className("shade")).get(i).click();
            sleep(1000);
            int newCount = Integer.parseInt(driver.findElement(By.className("attempts")).getText());
            Assert.assertTrue(initCount < newCount);
            sleep(1000);
        }
        int finalCountOfTries = Integer.parseInt(driver.findElement(By.className("attempts")).getText());
        Assert.assertTrue("Number of Tries did not match: "+numOfTries+" actual:"+finalCountOfTries, finalCountOfTries == numOfTries);
    }

    public void validateStreakResetCondition() {
        //Wait for page to load
        sleep(6000);
        int initStreakCount = Integer.parseInt(driver.findElement(By.className("streak")).getText());

        // start streak by choosing correct match
        int indexOfCorrectColleague = helpIndexOfCorrectColleague();
        driver.findElements(By.className("shade")).get(indexOfCorrectColleague).click();
        sleep(6000);

        // validate streak was incremented by 1
        int newStreakCount = Integer.parseInt(driver.findElement(By.className("streak")).getText());
        Assert.assertTrue("Streak did not increase. Expected"+(initStreakCount+1)+" but actual: "+newStreakCount, newStreakCount == initStreakCount+1);

        // clear streak by choosing incorrect match
        int indexOfIncorrectColleague = helpIndexOfFirstIncorrectColleague();
        driver.findElements(By.className("shade")).get(indexOfIncorrectColleague).click();
        sleep(2000);

        // validate streak was cleared and reset back to zero
        int clearedStreakCount = Integer.parseInt(driver.findElement(By.className("streak")).getText());
        Assert.assertTrue("Streak did not clear as expected. Actual: "+clearedStreakCount, clearedStreakCount == 0);
    }

    public void validateTriesAndCorrectCountersAfterRandomSelections(int numOfRandSelections) {
        //Wait for page to load
        sleep(6000);
        int correctCountTracker = 0;
        int triesCountTracker = 0;
        int i = 0;
        int randomNumIndexTracker = 0;

        // generate random index value
        int[] intArrOfIndexes = new int[60];
        Random randIndex = new Random();
        for(int a =0; a < intArrOfIndexes.length; a++){
            intArrOfIndexes[a] = randIndex.nextInt(5);
        }
        List<Integer> usedIncSelIndexes = new ArrayList<>();

        while( i < numOfRandSelections){
            int indexOfColleagueMatch = helpIndexOfCorrectColleague();

            System.out.println("Attempt # "+i);
            i++;
            int parseRandIndex = intArrOfIndexes[randomNumIndexTracker];
            System.out.println("Randomized Index (0-4): "+parseRandIndex);

            int incorrectSelectionsCount = 0;

            System.out.println("Index is Correct Match? ==> "+(indexOfColleagueMatch == parseRandIndex)+" Index: "+parseRandIndex);

            if (indexOfColleagueMatch == parseRandIndex){
                correctCountTracker++;
                System.out.println("Clicking Correct Match at index "+parseRandIndex);
                driver.findElements(By.className("shade")).get(parseRandIndex).click();
                sleep(6000);
                usedIncSelIndexes.clear(); // clear usedIncSelIndexes arrayList if correct is selected
                incorrectSelectionsCount = 0; // clear count of incorrectSelectionsCount if correct is selected
                int correctCheck = Integer.parseInt(driver.findElement(By.className("correct")).getText());
                Assert.assertTrue("Correct count did not increment. Expected: "+correctCountTracker+" Actual: "+correctCheck,correctCountTracker == correctCheck);
            }else{
                while(usedIncSelIndexes.contains(parseRandIndex)){
                    randomNumIndexTracker++;
                    parseRandIndex = intArrOfIndexes[randomNumIndexTracker];
                    System.out.println("Due to prev selection, using next unused index in array of randoms: "+parseRandIndex);
                }
                if (indexOfColleagueMatch == parseRandIndex) {
                    correctCountTracker++;
                    System.out.println("Clicking Correct Match, after duplicate index click found, at index "+parseRandIndex);
                    driver.findElements(By.className("shade")).get(parseRandIndex).click();
                    sleep(6000);
                    usedIncSelIndexes.clear(); // clear usedIncSelIndexes arrayList if correct is selected
                    incorrectSelectionsCount = 0; // clear count of incorrectSelectionsCount if correct is selected
                    int correctCheck = Integer.parseInt(driver.findElement(By.className("correct")).getText());
                    Assert.assertTrue("Correct count did not increment. Expected: " + correctCountTracker + " Actual: " + correctCheck, correctCountTracker == correctCheck);
                }else {
                    System.out.println("Clicking Incorrect Match at index "+parseRandIndex);
                    driver.findElements(By.className("shade")).get(parseRandIndex).click();
                    usedIncSelIndexes.add(parseRandIndex);
                    incorrectSelectionsCount++;
                }
                sleep(6000);
            }
            triesCountTracker++;
            int triesCheck = Integer.parseInt(driver.findElement(By.className("attempts")).getText());
            Assert.assertTrue("Tries did not increment. Expected: "+triesCountTracker+" Actual: "+triesCheck,triesCheck == triesCountTracker);
        }
        Assert.assertTrue("Number of selections ("+numOfRandSelections+") did not match Tries count of "+triesCountTracker,triesCountTracker == numOfRandSelections);
    }

    public void validateNewActiveMatchSessionAfterCorrectMatch() {
        //Wait for page to load
        sleep(6000);
        List<String> collNamesList = new ArrayList<>();
        List<String> collImagesList = new ArrayList<>();
        List<String> newCollNamesList = new ArrayList<>();
        List<String> newCollImagesList = new ArrayList<>();

        // get arrayList of original match option names
        List<WebElement> colleaguesNames = driver.findElements(By.className("name"));
        for(WebElement elName : colleaguesNames){
            collNamesList.add(elName.getText());
        }
        // get arrayList of original match option images
        List<WebElement> colleaguesImages = driver.findElements(By.cssSelector("img"));
        for(WebElement elImage : colleaguesImages){
            collImagesList.add(elImage.getAttribute("src"));
        }

        // get index of colleague that is Correct Match and click that element
        int corrMatchColleagueIndex = helpIndexOfCorrectColleague();
        driver.findElements(By.className("shade")).get(corrMatchColleagueIndex).click();
        sleep(6000);
        int correctCount = Integer.parseInt(driver.findElement(By.className("correct")).getText());
        Assert.assertTrue("Correct count did not increment by 1 as expected. Correct Count: "+ correctCount, (correctCount > 0));

        // get arrayList of new match option names
        List<WebElement> newColleaguesNames = driver.findElements(By.className("name"));
        for(WebElement elName : newColleaguesNames){
            newCollNamesList.add(elName.getText());
        }
        // get arrayList of new match option images
        List<WebElement> newColleaguesImages = driver.findElements(By.cssSelector("img"));
        for(WebElement elImage : newColleaguesImages){
            newCollImagesList.add(elImage.getAttribute("src"));
        }

        Assert.assertFalse("Colleague Name Lists are matching but should not.", newCollNamesList.equals(collNamesList));
        Assert.assertFalse("Colleague Image Lists are matching but should not.", newCollImagesList.equals(collImagesList));
    }

    public void validateIncorrectFrequencyIsHigherComparedToCorrectFrequency() {
        //Wait for page to load
        sleep(6000);

        List<String> displayedColleagues = new ArrayList<>();
        List<String> incorrectSelectedColleagues = new ArrayList<>();
        List<String> matchedSelectedColleagues = new ArrayList<>();

        int numOfRuns = 30;
        for(int i = 0; i < numOfRuns; i++){
            System.out.print("Match # "+i+" out of "+numOfRuns);
            for ( String name : helpArrayOfColleaguesToSelect()){
                displayedColleagues.add(name);
            }
//            System.out.println("displayed colleagues => "+displayedColleagues);
            List<WebElement> listOfNames = driver.findElements(By.className("name"));

            int indexOfIncColleague = helpIndexOfFirstIncorrectColleague();
            String nameOfIncorrectCollClicked = listOfNames.get(indexOfIncColleague).getText();
            driver.findElements(By.className("shade")).get(indexOfIncColleague).click();
            sleep(3000);
            incorrectSelectedColleagues.add(nameOfIncorrectCollClicked);

            int indexOfCorrectMatch = helpIndexOfCorrectColleague();
            String nameOfMatchClicked = listOfNames.get(indexOfCorrectMatch).getText();
            int loops = 0;
            while(incorrectSelectedColleagues.contains(nameOfMatchClicked) && loops < incorrectSelectedColleagues.size()){
                loops++;
                driver.navigate().refresh();
                sleep(12000);
            }
            indexOfCorrectMatch = helpIndexOfCorrectColleague();
            nameOfMatchClicked = listOfNames.get(indexOfCorrectMatch).getText();
            driver.findElements(By.className("shade")).get(indexOfCorrectMatch).click();
            sleep(6000);
            matchedSelectedColleagues.add(nameOfMatchClicked);
        }

        List<Integer> frequencyOfIncorrect = new ArrayList<>();
        List<Integer> frequencyOfMatched = new ArrayList<>();
        for(String incName : incorrectSelectedColleagues){
            frequencyOfIncorrect.add(Collections.frequency(displayedColleagues, incName));
        }
        for(String matchName : matchedSelectedColleagues){
            frequencyOfMatched.add(Collections.frequency(displayedColleagues, matchName));
        }

        OptionalDouble averageOfIncorrect = frequencyOfIncorrect
                .stream()
                .mapToDouble(a -> a)
                .average();
        OptionalDouble averageOfCorrect = frequencyOfMatched
                .stream()
                .mapToDouble(a -> a)
                .average();

        System.out.println("The Incorrect Selection Average: "+averageOfIncorrect.getAsDouble());
        System.out.println("The Correct Selection Average: "+averageOfCorrect.getAsDouble());
        Assert.assertTrue("Incorrect Average: "+averageOfIncorrect.getAsDouble()+"Correct Average : "+averageOfCorrect.getAsDouble(), averageOfIncorrect.getAsDouble() > averageOfCorrect.getAsDouble());
    }








    public String[] helpArrayOfColleaguesToSelect() {
        String[] selectableColleagues = new String[5];
        List<WebElement> colleaguesToSelect = driver.findElements(By.className("name"));
        for (int i = 0; i <= 4; i++) {
            String selectionToTestName = colleaguesToSelect.get(i).getText();
            selectableColleagues[i] = selectionToTestName;
        }
        System.out.println(selectableColleagues);
        return selectableColleagues;
    }

    public int helpIndexOfCorrectColleague() {
        String[] arrayOfColleagues = helpArrayOfColleaguesToSelect();
        String nameToMatch = driver.findElement(By.id("name")).getText();

        int index = -1;
        for(int i=0; i < arrayOfColleagues.length; i++){
            if(arrayOfColleagues[i].equals(nameToMatch)){
                index = i;
                break;
            }
        }
        return index;
    }

    public int helpIndexOfFirstIncorrectColleague() {
        String[] arrayOfColleagues =  helpArrayOfColleaguesToSelect();
        String nameToMatch = driver.findElement(By.id("name")).getText();

        int index = -1;
        for (int i = 0; i < arrayOfColleagues.length; i++) {
            if (!arrayOfColleagues[i].equals(nameToMatch)) {
                index = i;
                break;
            }else {
                index = i+1;
            }
        }
        return index;
    }

    public void helpClickCorrectColleague() {
        String[] arrayOfColleagues = helpArrayOfColleaguesToSelect();
        String nameToMatch = driver.findElement(By.id("name")).getText();

        int index = -1;
        for(int i=0; i < arrayOfColleagues.length; i++){
            if(arrayOfColleagues[i].equals(nameToMatch)){
                index = i;
                break;
            }
        }
        System.out.println("Clicking on Correct Colleague at index "+index);
        driver.findElements(By.className("shade")).get(index).click();
    }

    public void helpClickIncorrectColleague() {
        String[] arrayOfColleagues =  helpArrayOfColleaguesToSelect();
        String nameToMatch = driver.findElement(By.id("name")).getText();

        int index = -1;
        for (int i = 0; i < arrayOfColleagues.length; i++) {
            if (!arrayOfColleagues[i].equals(nameToMatch)) {
                index = i;
                break;
            }else {
                index = i+1;
            }
        }
        driver.findElements(By.className("shade")).get(index).click();
    }

    public int helpIndexOfColleagueByName(String collName) {
        String[] arrayOfColleagues =  helpArrayOfColleaguesToSelect();
        List<WebElement> nameToMatch = driver.findElements(By.id("name"));

        int index = -1;
        for (int i = 0; i < arrayOfColleagues.length; i++) {
            if (!arrayOfColleagues[i].equals(collName)) {
                index = i;
                break;
            }else {
                index = i+1;
            }
        }
        return index;
    }

}
