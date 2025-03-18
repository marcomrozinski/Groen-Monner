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
            Player player = new Player(board, null, "Player " + i);
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

        Assertions.assertEquals(player, board.getSpace(0, 4).getPlayer(), "Player " + player.getName() + " should be on Space (0,4)");
    }

    @Test
    void moveCurrentPlayerToSpace() {
        Board board = gameController.board;
        Player player1 = board.getPlayer(0);
        Player player2 = board.getPlayer(1);

        gameController.moveCurrentPlayerToSpace(board.getSpace(0, 4));

        Assertions.assertEquals(player1, board.getSpace(0, 4).getPlayer(), "Player " + player1.getName() + " should beSpace (0,4)");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should be empty");
        Assertions.assertEquals(player2, board.getCurrentPlayer(), "Current player should be " + player2.getName());
    }

    @Test
    void moveForward() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        gameController.moveForward(current);

        Assertions.assertEquals(current, board.getSpace(0, 1).getPlayer(), "Player " + current.getName() + " should beSpace (0,1)");
        Assertions.assertEquals(Heading.SOUTH, current.getHeading(), "Player 0 should be heading SOUTH");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should be empty");
    }


    @Test
    void testPlayerCannotMoveThroughWalls() {

        Board board = gameController.board;
        Player player = board.getCurrentPlayer();
        Space startSpace = board.getSpace(2, 2);
        Space blockedSpace = board.getSpace(2, 3);

        startSpace.getWalls().add(Heading.SOUTH);
        blockedSpace.getWalls().add(Heading.NORTH);

        player.setSpace(startSpace);
        player.setHeading(Heading.SOUTH);


        gameController.moveForward(player);


        Assertions.assertEquals(startSpace, player.getSpace(), "Player should not move through a wall");
    }

    @Test
    void CheckpointWin() {
        Board board = gameController.board;
        Player player = board.getCurrentPlayer();
        Space startSpace = board.getSpace(2, 2);
        Space checkpointSpace = board.getSpace(2, 3);
        Checkpoint checkpoint = new Checkpoint(1);
        checkpointSpace.getActions().add(checkpoint);

        player.setSpace(startSpace);
        player.setHeading(Heading.SOUTH);

        gameController.moveForward(player);

        Assertions.assertEquals(checkpointSpace, player.getSpace(), "Player has successfully reached checkpoint");

    }

    @Test
    void CheckpointWrongOrder() {

        Board board = gameController.board;
        Player player = board.getCurrentPlayer();
        Space checkpointSpace = board.getSpace(3, 3);

        Checkpoint checkpoint = new Checkpoint(2);
        checkpointSpace.getActions().add(checkpoint);

        player.setSpace(checkpointSpace);


        boolean actionTriggered = checkpoint.doAction(gameController, checkpointSpace);


        Assertions.assertFalse(actionTriggered, "Player should not activate checkpoint 2 without reaching checkpoint 1.");
        Assertions.assertFalse(player.getReachedCheckpoint().contains(2),
                "Player should not have checkpoint 2 in the list.");
    }

    @Test
    void testGetCheckpoint() {

        int expectedCheckpointID = 1;
        Checkpoint checkpoint = new Checkpoint(expectedCheckpointID);

        int actualCheckpointID = checkpoint.getCheckpoint();

        Assertions.assertEquals(expectedCheckpointID, actualCheckpointID,
                "getCheckpoint() should return the correct checkpoint ID.");
    }

    @Test
    void testConveyorBeltMovesPlayer() {

        Board board = gameController.board;
        Player player = board.getCurrentPlayer();
        Space conveyorSpace = board.getSpace(3, 3);
        Space targetSpace = board.getSpace(4, 3);


        ConveyorBelt conveyorBelt = new ConveyorBelt(Heading.EAST);
        conveyorSpace.getActions().add(conveyorBelt);
        player.setSpace(conveyorSpace);
        conveyorSpace.setPlayer(player);


        boolean moved = conveyorBelt.doAction(gameController, conveyorSpace);


        Assertions.assertTrue(moved, "The conveyor belt should have moved the player.");
        Assertions.assertEquals(targetSpace, player.getSpace(),
                "Player should have been moved to the correct target space by the conveyor belt.");
        Assertions.assertNull(conveyorSpace.getPlayer(), "Conveyor space should no longer have a player.");
        Assertions.assertEquals(player, targetSpace.getPlayer(),
                "Target space should now contain the player.");
    }

    @Test
    void testGetHeading() {

        Heading expectedHeading = Heading.NORTH;
        ConveyorBelt conveyorBelt = new ConveyorBelt(expectedHeading);

        Heading actualHeading = conveyorBelt.getHeading();

        Assertions.assertEquals(expectedHeading, actualHeading,
                "Metoden getHeading() skal returnere den korrekte retning.");
    }

    @Test
    void testFastForward() {
        Board board = gameController.board;
        Player currentPlayer = board.getCurrentPlayer();
        currentPlayer.setHeading(Heading.NORTH);
        Space initialSpace = currentPlayer.getSpace();


        gameController.fastForward(currentPlayer);

        Space firstMove = board.getNeighbour(initialSpace, currentPlayer.getHeading());
        Space secondMove = firstMove != null ? board.getNeighbour(firstMove, currentPlayer.getHeading()) : null;

        if (secondMove != null) {
            Assertions.assertEquals(secondMove, currentPlayer.getSpace(),
                    "Player should move two spaces forward with fast forward.");
        } else if (firstMove != null) {
            Assertions.assertEquals(firstMove, currentPlayer.getSpace(),
                    "Player should move only one space forward as the second move is invalid.");
        } else {
            Assertions.assertEquals(initialSpace, currentPlayer.getSpace(),
                    "Player should not move as both moves are invalid.");
        }

        Assertions.assertEquals(Heading.NORTH, currentPlayer.getHeading(),
                "Player's heading should remain unchanged after fast forward.");
    }


    @Test
    void testTurnRight() {
        Board board = gameController.board;
        Player currentPlayer = board.getCurrentPlayer();
        currentPlayer.setHeading(Heading.NORTH);

        gameController.turnRight(currentPlayer);

        Assertions.assertEquals(Heading.EAST, currentPlayer.getHeading(),
                "Player's heading should change to EAST after turning right.");
    }

    @Test
    void testTurnLeft() {
        Board board = gameController.board;
        Player currentPlayer = board.getCurrentPlayer();
        currentPlayer.setHeading(Heading.NORTH);

        gameController.turnLeft(currentPlayer);

        Assertions.assertEquals(Heading.WEST, currentPlayer.getHeading(),
                "Player's heading should change to WEST after turning left.");
    }

    @Test
    void testUTurn() {
        Board board = gameController.board;
        Player currentPlayer = board.getCurrentPlayer();
        currentPlayer.setHeading(Heading.NORTH);

        gameController.uTurn(currentPlayer);

        Assertions.assertEquals(Heading.SOUTH, currentPlayer.getHeading(),
                "Player's heading should be SOUTH after making a U-turn.");
    }

    @Test
    void testMoveBackwards() {

        Board board = gameController.board;
        Player currentPlayer = board.getCurrentPlayer();
        Space initialSpace = currentPlayer.getSpace();
        currentPlayer.setHeading(Heading.NORTH);

        gameController.backwards(currentPlayer);

        Space expectedSpaceBackwards = board.getNeighbour(initialSpace, Heading.SOUTH);
        Assertions.assertEquals(expectedSpaceBackwards, currentPlayer.getSpace(),
                "Player should move backwards to the SOUTH.");
        Assertions.assertEquals(Heading.NORTH, currentPlayer.getHeading(),
                "Player's heading should remain NORTH after moving backwards.");
    }

    @Test
    void testPlayerCannotMoveOutsideBoard() {
        Board board = gameController.board;
        Player currentPlayer = board.getPlayer(0);
        currentPlayer.setSpace(board.getSpace(0, 0));
        currentPlayer.setHeading(Heading.WEST);

        gameController.moveForward(currentPlayer);

        Assertions.assertEquals(board.getSpace(0, 0), currentPlayer.getSpace(),
                "Player should not move outside the boundaries of the board.");
    }
}





