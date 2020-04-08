package tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import pages.SolverPage;

import java.io.IOException;
import java.util.List;
import java.util.Random;

public class Solver extends BaseTest {

    private final SolverPage solver = new SolverPage(driver);
    final Random random = new Random();


    @Test
    void fistClickIsNotABomb() {
        String bomb = "mine-red";


        int randomX = random.nextInt(9);
        int randomY = random.nextInt(9);

        solver.goToBaseUrl();

        List<List<WebElement>> gameField = solver.getGameFieldMatrice();

        solver.clickOnCellByPosition(randomX, randomY, gameField);

        gameField = solver.getGameFieldMatrice();

        String cellType = solver.getCellTypeByPosition(randomX, randomY, gameField);

        Assertions.assertNotEquals(bomb, cellType);
    }

    @Test
    void solve() throws IOException {

        int randomX;
        int randomY;
        int numberOfFlags;
        int numberOfFaceDownCells;
        int gameMapSize;

        List<List<WebElement>> gameField;
        String targetCellType;
        List<int[]> numberCells;

        solver.goToBaseUrl();
        //solver.selectIntermediateDifficulty();

        // getMapSize() needs fix..
        //gameMapSize = solver.getMapSize();
        gameMapSize = 9;

        // first click at random position
        randomX = random.nextInt(gameMapSize);
        randomY = random.nextInt(gameMapSize);
        solver.firstRandomClick(randomX, randomY);


        // getting the map after the first step for the veeeery ugly main logic
        gameField = solver.getGameFieldMatrice();
        do {

            //numberCells = solver.getEveryNumberCellCoordinates(gameField);
            numberCells = solver.getEveryNumberCellCoordinates();

            for (int[] numberCell : numberCells) {

                int x = numberCell[0];
                int y = numberCell[1];

                targetCellType = solver.getCellTypeByPosition(x, y, gameField);

                if (targetCellType == null){
                    break;
                }

                //getting the number of face-down and flagged cells
                List<WebElement> nearbyElements = solver.getNearbyElementsByPosition(x, y, gameField);

                numberOfFaceDownCells = solver.getNumberOfFaceDownFields(nearbyElements);
                numberOfFlags = solver.getNumberOfFlags(nearbyElements);


                //RULE 1
                // if the number of face-down cells are equals to the target cell's number - flagged cells around it
                // every cell around the target cell is a bomb
                if ((Integer.parseInt(targetCellType) - numberOfFlags) == numberOfFaceDownCells && numberOfFaceDownCells > 0) {
                    solver.flagEveryFaceDownCell(nearbyElements);
                }
                //RULE 2
                // if the cell has same amount of flagged cells around it as the cell's number
                // none of the cells around it are bombs
                if (Integer.parseInt(targetCellType) == numberOfFlags && numberOfFaceDownCells > 0) {
                    solver.clickOnEveryNotFlaggedCell(nearbyElements);
                }

//                if (i == numberCells.size() - 1) {
//                    solver.takeScreenShot("stopped");
//                    Assertions.fail("Stopped..");
//                }
            }

            gameField = solver.getGameFieldMatrice();

        } while (solver.areThereAnyHiddenCellsLeft(gameField) && !solver.areThereAnyVisibleBombs(gameField));

        gameField = solver.getGameFieldMatrice();

        if (!solver.areThereAnyHiddenCellsLeft(gameField)) {
            solver.takeScreenShot("solved");
            System.out.println("Solved!");
        } else if (solver.areThereAnyVisibleBombs(gameField)) {
            solver.takeScreenShot("exploded");
            Assertions.fail("Exploded!");
        }
    }
}
