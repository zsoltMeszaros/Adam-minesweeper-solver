package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.io.FileHandler;

import java.io.File;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SolverPage extends BasePage {

    Actions action = new Actions(driver);

    By gameFieldBy = By.xpath("//div[@id='game-field']");
    By gameFieldRowBy = By.xpath("//div[@class='game-row']");
    By gameFieldCellBy = By.xpath("//div[@class='cell']");

    public SolverPage(WebDriver driver) {
        super(driver);
    }

    public void goToBaseUrl() {
        driver.get(BASE_URL);
    }

    public void clickOnCellByPosition(int x, int y, List<List<WebElement>> gameField) {

        gameField.get(x).get(y).click();
    }

    public void firstRandomClick(int x, int y) {
        driver.findElements(gameFieldRowBy).get(x).findElements(gameFieldCellBy).get(y).click();
    }

    public int getMapSize() {
        return driver.findElements(gameFieldRowBy).get(0).findElements(gameFieldCellBy).size();
    }

    public List<List<WebElement>> getGameFieldMatrice() {

        WebElement gameFieldElement = driver.findElement(gameFieldBy);

        List<List<WebElement>> gameFieldList = new ArrayList<>();
        int rowsNumber = gameFieldElement.findElements(gameFieldRowBy).size();
        List<WebElement> cells = driver.findElements(gameFieldCellBy);

        int counter = 0;
        for (int i = 0; i < rowsNumber; i++) {
            List<WebElement> row = new ArrayList<>();
            for (int j = 0; j < rowsNumber; j++) {
                row.add(cells.get(counter));
                counter++;
            }
            gameFieldList.add(row);
        }

        return gameFieldList;
    }

    public void flagCellByPosition(int x, int y, List<List<WebElement>> gameField) {

        action.contextClick(gameField.get(x).get(y)).perform();
    }

    public void flagCell(WebElement cell) {

        action.contextClick(cell).perform();
    }

    public List<WebElement> getNearbyElementsByPosition(int x, int y, List<List<WebElement>> gameField) {

        List<WebElement> results = new ArrayList<>();

        for (int i = x - 1; i < x + 2; i++) {
            for (int j = y - 1; j < y + 2; j++) {
                if (j > 8 || j < 0 || i > 8 || i < 0) {
                    continue;
                }


                results.add(gameField.get(i).get(j));
            }
        }

        return results;
    }

    public boolean areThereAnyFaceDownNearby(int x, int y, List<List<WebElement>> gameField) {


        for (int i = x - 1; i < x + 2; i++) {
            for (int j = y - 1; j < y + 2; j++) {
                if (j > 8 || j < 0 || i > 8 || i < 0) {
                    continue;
                }
                WebElement cell = gameField.get(i).get(j);

                if (getCellType(cell).equals("face-down")) {
                    return true;
                }
            }
        }

        return false;
    }

    public String getCellType(WebElement cell) {

        try {
            String type = cell.findElement(By.tagName("img")).getAttribute("src");
            type = type.replace("http://sweeperofmines.herokuapp.com", "");
            type = type.replace("/images/tiles/", "");
            type = type.replace(".png", "");

            return type;

        } catch (StaleElementReferenceException e){
            System.out.println(Arrays.toString(e.getStackTrace()));
        }

        return null;
    }

    public String getCellTypeByPosition(int x, int y, List<List<WebElement>> gameField) {


        try {
            WebElement cell = gameField.get(x).get(y).findElement(By.tagName("img"));
            String type = cell.getAttribute("src");
            type = type.replace("http://sweeperofmines.herokuapp.com", "");
            type = type.replace("/images/tiles/", "");
            type = type.replace(".png", "");

            return type;

        } catch (StaleElementReferenceException e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
        }


        return null;
    }

    public boolean areThereAnyHiddenCellsLeft(List<List<WebElement>> gameField) {

        for (List<WebElement> row : gameField) {
            for (WebElement cell : row) {
                if (getCellType(cell).equals("face-down")) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean areThereAnyVisibleBombs(List<List<WebElement>> gameField) {

        for (List<WebElement> row : gameField) {
            for (WebElement cell : row) {
                if (getCellType(cell).equals("mine-red")) {
                    return true;
                }
            }
        }
        return false;
    }


    public int getNumberOfFlags(List<WebElement> cells) {

        int counter = 0;

        for (WebElement cell : cells) {
            if (getCellType(cell).equals("flagged")) {
                counter++;
            }
        }

        return counter;
    }

    public int getNumberOfFaceDownFields(List<WebElement> cells) {

        int counter = 0;

        for (WebElement cell : cells) {
            if (getCellType(cell).equals("face-down")) {
                counter++;
            }
        }

        return counter;
    }

    public void clickOnEveryNotFlaggedCell(List<WebElement> cells) {

        try {

            for (WebElement cell : cells) {
                if (getCellType(cell).equals("face-down")) {
                    cell.click();
                }
            }
        } catch (NullPointerException e){
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
    }

    public void flagEveryFaceDownCell(List<WebElement> cells) {

        for (WebElement cell : cells) {
            if (getCellType(cell).equals("face-down")) {
                flagCell(cell);
            }
        }
    }


    public List<int[]> getEveryNumberCellCoordinates(List<List<WebElement>> gameField) {

        List<int[]> numberCells = new ArrayList<>();

        String empty = "0";
        String faceDown = "face-down";
        String flagged = "flagged";

        for (int i = 0; i < gameField.size(); i++) {
            List<WebElement> row = gameField.get(i);
            for (int j = 0; j < row.size(); j++) {

                String targetCellType = getCellType(row.get(j));

                if (areThereAnyFaceDownNearby(i, j, gameField)) {

                    if (!(targetCellType.equals(faceDown) || targetCellType.equals(empty) || targetCellType.equals(flagged))) {
                        int[] cellCoordinates = new int[2];
                        cellCoordinates[0] = i;
                        cellCoordinates[1] = j;
                        numberCells.add(cellCoordinates);
                    }
                }
            }
        }

        return numberCells;

    }

    public List<int[]> getEveryNumberCellCoordinates() {

        String empty = "0";
        String faceDown = "face-down";
        String flagged = "flagged";

        List<int[]> resultCells = new ArrayList<>();

        List<WebElement> rows = driver.findElements(gameFieldRowBy);

        for (int i = 0; i < rows.size(); i++) {
            List<WebElement> cells = rows.get(i).findElements(By.className("cell"));

            for (int j = 0; j < cells.size(); j++) {


                String targetCellType = getCellType(cells.get(j));

                if (!(targetCellType.equals(faceDown) || targetCellType.equals(empty) || targetCellType.equals(flagged))) {
                    int[] cellCoordinates = new int[2];
                    cellCoordinates[0] = i;
                    cellCoordinates[1] = j;
                    resultCells.add(cellCoordinates);
                }
            }
        }

        return resultCells;

    }

    public void selectIntermediateDifficulty() {
        driver.findElement(By.id("intermediate")).click();
    }

    public void selectExpertDifficulty() {
        driver.findElement(By.id("expert")).click();
    }

    public void takeScreenShot(String message) throws IOException {

        File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

        long ts = System.currentTimeMillis() / 1000;

        File DestFile = new File("/home/eugene315/Desktop/Adam-minesweeper-solver/screenshots/" + message + "_" + ts + ".png");

        FileHandler.copy(scrFile, DestFile);
    }
}
