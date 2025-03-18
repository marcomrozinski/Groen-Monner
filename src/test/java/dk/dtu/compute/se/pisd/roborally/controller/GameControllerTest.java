package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.*;
import dk.dtu.compute.se.pisd.roborally.controller.*;
import dk.dtu.compute.se.pisd.roborally.view.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GameControllerTest {

    private final int TEST_WIDTH = 8;
    private final int TEST_HEIGHT = 8;

    private GameController gameController;

    @BeforeEach
    void setUp() {
        Board board = new Board(TEST_WIDTH, TEST_HEIGHT);
        gameController = new GameController(board);
        for (int i = 0; i < 6; i++) {
            Player player = new Player(board, null,"Player " + i);
            board.addPlayer(player);
            player.setSpace(board.getSpace(i, i));
            player.setHeading(Heading.values()[i % Heading.values().length]);
        }
        board.setCurrentPlayer(board.getPlayer(0));
    }

    @AfterEach
    void tearDown() {
        gameController = null;
    }

    /**
     * Test for Assignment V1 (can be deleted later once V1 was shown to the teacher)
     */

    @Test
    void testV1() {
        Board board = gameController.board;

        Player player = board.getCurrentPlayer();
        gameController.moveCurrentPlayerToSpace(board.getSpace(0, 4));

        Assertions.assertEquals(player, board.getSpace(0, 4).getPlayer(), "Player " + player.getName() + " should be on Space (0,4)!");
    }

    @Test
    void moveCurrentPlayerToSpace() {
        Board board = gameController.board;
        Player player1 = board.getPlayer(0);
        Player player2 = board.getPlayer(1);

        gameController.moveCurrentPlayerToSpace(board.getSpace(0, 4));

        Assertions.assertEquals(player1, board.getSpace(0, 4).getPlayer(), "Player " + player1.getName() + " should beSpace (0,4)!");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should be empty!");
        Assertions.assertEquals(player2, board.getCurrentPlayer(), "Current player should be " + player2.getName() +"!");
    }

    @Test
    void moveForward() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        gameController.moveForward(current);

        Assertions.assertEquals(current, board.getSpace(0, 1).getPlayer(), "Player " + current.getName() + " should beSpace (0,1)!");
        Assertions.assertEquals(Heading.SOUTH, current.getHeading(), "Player 0 should be heading SOUTH!");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should be empty!");
    }


    // TDOD and there should be more tests added for the different assignments eventually
    @Test
    void testPlayerCannotMoveThroughWalls() {

        Board board = gameController.board;
        Player player = board.getCurrentPlayer(); //Henter nuværende spiller
        Space startSpace = board.getSpace(2, 2); //Definerer spillerens startposition
        Space blockedSpace = board.getSpace(2, 3); //Definerer den blokerede destination

        //tilføj vægge - væg mellem startSpace og blockedSpace
        startSpace.getWalls().add(Heading.SOUTH);
        blockedSpace.getWalls().add(Heading.NORTH);

        //sæt spillerens position og retning
        player.setSpace(startSpace);
        player.setHeading(Heading.SOUTH);


        gameController.moveForward(player); //forsøg på at bevæge spilleren fremad


        Assertions.assertEquals(startSpace, player.getSpace(), "Player should not move through a wall");
    }

    @Test
    void CheckpointWin() {
        Board board = gameController.board;
        Player player = board.getCurrentPlayer(); //Henter nuværende spiller
        Space startSpace = board.getSpace(2, 2); //Definerer spillerens startposition
        Space checkpointSpace = board.getSpace(2, 3);
        Checkpoint checkpoint = new Checkpoint(1);
        checkpointSpace.getActions().add(checkpoint);

        player.setSpace(startSpace);
        player.setHeading(Heading.SOUTH);

        gameController.moveForward(player); //forsøg på at bevæge spilleren fremad

        Assertions.assertEquals(checkpointSpace, player.getSpace(), "Player has successfully reached checkpoint");

    }

    @Test
    void CheckpointWrongOrder() {
        // Arrange
        Board board = gameController.board;
        Player player = board.getCurrentPlayer();
        Space checkpointSpace = board.getSpace(3, 3);

        Checkpoint checkpoint = new Checkpoint(2); // Checkpoint ID 2
        checkpointSpace.getActions().add(checkpoint); // Tilføj til feltet

        player.setSpace(checkpointSpace);

        // Act
        boolean actionTriggered = checkpoint.doAction(gameController, checkpointSpace);

        // Assert
        Assertions.assertFalse(actionTriggered, "Player should not activate checkpoint 2 without reaching checkpoint 1.");
        Assertions.assertFalse(player.getReachedCheckpoint().contains(2),
                "Player should not have checkpoint 2 in the list.");
    }

    @Test
    void testGetCheckpoint() {
        // Arrange: Opret et checkpoint med et specifikt ID
        int expectedCheckpointID = 1;
        Checkpoint checkpoint = new Checkpoint(expectedCheckpointID);

        // Act: Hent ID'et via getCheckpoint
        int actualCheckpointID = checkpoint.getCheckpoint();

        // Assert: Sammenlign det forventede ID med det faktiske, der returneres af metoden
        Assertions.assertEquals(expectedCheckpointID, actualCheckpointID,
                "getCheckpoint() should return the correct checkpoint ID.");
    }

    @Test
    void testConveyorBeltMovesPlayer() {
        // Arrange: Opret bræt, spiller og conveyor belt
        Board board = gameController.board;
        Player player = board.getCurrentPlayer(); // Spilleren der skal interagere med conveyor belt
        Space conveyorSpace = board.getSpace(3, 3); // Position for conveyor belt
        Space targetSpace = board.getSpace(4, 3); // Hvor spilleren flyttes til

        // Opret og opsæt conveyor belt
        ConveyorBelt conveyorBelt = new ConveyorBelt(Heading.EAST); // ConveyorBelt mod EAST
        conveyorSpace.getActions().add(conveyorBelt);
        player.setSpace(conveyorSpace); // Placér spilleren på conveyor feltet
        conveyorSpace.setPlayer(player);

        // Act: Aktiver conveyor belt og udfør handling
        boolean moved = conveyorBelt.doAction(gameController, conveyorSpace);

        // Assert: Tjek at spilleren er flyttet til targetSpace
        Assertions.assertTrue(moved, "The conveyor belt should have moved the player.");
        Assertions.assertEquals(targetSpace, player.getSpace(),
                "Player should have been moved to the correct target space by the conveyor belt.");
        Assertions.assertNull(conveyorSpace.getPlayer(), "Conveyor space should no longer have a player.");
        Assertions.assertEquals(player, targetSpace.getPlayer(),
                "Target space should now contain the player.");
    }
    @Test
    void testConveyorBeltMovesPlayerMultipleSteps() {
        // Arrange: Opret et bræt med en kæde af conveyor belts
        Board board = gameController.board;
        Player player = board.getCurrentPlayer();

        Space startSpace = board.getSpace(1, 1);
        Space middleSpace = board.getSpace(2, 1);
        Space endSpace = board.getSpace(3, 1);

        // Opret kæden af conveyor belts
        ConveyorBelt conveyor1 = new ConveyorBelt(Heading.EAST); // Første belt mod EAST
        ConveyorBelt conveyor2 = new ConveyorBelt(Heading.EAST); // Andet belt mod EAST
        startSpace.getActions().add(conveyor1);
        middleSpace.getActions().add(conveyor2);

        // Placér spiller på første conveyor belt
        player.setSpace(startSpace);
        startSpace.setPlayer(player);

        // Act: Udfør flere trin
        gameController.executeNextStep(); // Første bevægelse (1 til 2)
        gameController.executeNextStep(); // Anden bevægelse (2 til 3)

        // Assert: Spilleren skal være flyttet til det sidste felt
        Assertions.assertEquals(endSpace, player.getSpace(),
                "Player should have been moved to the final space in the conveyor chain.");
        Assertions.assertNull(startSpace.getPlayer(),
                "The starting space should no longer have a player.");
        Assertions.assertNull(middleSpace.getPlayer(),
                "The middle space should no longer have a player.");
        Assertions.assertEquals(player, endSpace.getPlayer(),
                "The final space should now contain the player.");
    }



}