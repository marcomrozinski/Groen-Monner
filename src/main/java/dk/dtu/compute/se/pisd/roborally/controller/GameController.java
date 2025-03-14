/*
 *  This file is part of the initial project provided for the
 *  course "Project in Software Development (02362)" held at
 *  DTU Compute at the Technical University of Denmark.
 *
 *  Copyright (C) 2019, 2020: Ekkart Kindler, ekki@dtu.dk
 *
 *  This software is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; version 2 of the License.
 *
 *  This project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this project; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * ...
 * TODO: we should really write these docstrings, else Carlos will fail us all
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class GameController {

    final public Board board;

    public GameController(@NotNull Board board) {
        this.board = board;
    }

    /**
     * This is just some dummy controller operation to make a simple move to see something
     * happening on the board. This method should eventually be deleted!
     *
     * @param space the space to which the current player should move
     */
    public void moveCurrentPlayerToSpace(@NotNull Space space) {
        // TODO V1: method should be implemented by the students:
        //   - the current player should be moved to the given space
        //     (if it is free())
        //   - and the current player should be set to the player
        //     following the current player
        //   - the counter of moves in the game should be increased by one
        //     if and when the player is moved (the counter and the status line
        //     message needs to be implemented at another place)
        Player currentPlayer = board.getCurrentPlayer();

        if (space.getPlayer() == null) {
            currentPlayer.setSpace(space);
            board.setCounter();

            int PlayerNumber = board.getPlayerNumber(currentPlayer);

            int totalPlayers = board.getPlayersNumber();


            int nextPlayerIndex = (PlayerNumber + 1) % totalPlayers;
            Player nextPlayer = board.getPlayer(nextPlayerIndex);

            board.setCurrentPlayer(nextPlayer);
        }
    }

    // XXX V2
    public void startProgrammingPhase() {
        board.setPhase(Phase.PROGRAMMING);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);

        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            if (player != null) {
                for (int j = 0; j < Player.NO_REGISTERS; j++) {
                    CommandCardField field = player.getProgramField(j);
                    field.setCard(null);
                    field.setVisible(true);
                }
                for (int j = 0; j < Player.NO_CARDS; j++) {
                    CommandCardField field = player.getCardField(j);
                    field.setCard(generateRandomCommandCard());
                    field.setVisible(true);
                }
            }
        }
    }

    // XXX V2
    private CommandCard generateRandomCommandCard() {
        Command[] commands = Command.values();
        int random = (int) (Math.random() * commands.length);
        return new CommandCard(commands[random]);
    }

    // XXX V2
    public void finishProgrammingPhase() {
        makeProgramFieldsInvisible();
        makeProgramFieldsVisible(0);
        board.setPhase(Phase.ACTIVATION);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);
    }

    // XXX V2
    private void makeProgramFieldsVisible(int register) {
        if (register >= 0 && register < Player.NO_REGISTERS) {
            for (int i = 0; i < board.getPlayersNumber(); i++) {
                Player player = board.getPlayer(i);
                CommandCardField field = player.getProgramField(register);
                field.setVisible(true);
            }
        }
    }

    // XXX V2
    private void makeProgramFieldsInvisible() {
        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            for (int j = 0; j < Player.NO_REGISTERS; j++) {
                CommandCardField field = player.getProgramField(j);
                field.setVisible(false);
            }
        }
    }

    // XXX V2
    public void executePrograms() {
        board.setStepMode(false);
        continuePrograms();
    }

    // XXX V2
    public void executeStep() {
        board.setStepMode(true);
        continuePrograms();
    }

    // XXX V2
    private void continuePrograms() {
        do {
            executeNextStep();
        } while (board.getPhase() == Phase.ACTIVATION && !board.isStepMode());
    }

    // XXX V2
    private void executeNextStep() {
        Player currentPlayer = board.getCurrentPlayer();
        if (board.getPhase() == Phase.ACTIVATION && currentPlayer != null) {
            int step = board.getStep();
            if (step >= 0 && step < Player.NO_REGISTERS) {
                CommandCard card = currentPlayer.getProgramField(step).getCard();
                if (card != null) {
                    Command command = card.command;
                    executeCommand(currentPlayer, command);
                }
                int nextPlayerNumber = board.getPlayerNumber(currentPlayer) + 1;
                if (nextPlayerNumber < board.getPlayersNumber()) {
                    board.setCurrentPlayer(board.getPlayer(nextPlayerNumber));
                } else {
                    executeFieldActions();
                    step++;
                    if (step < Player.NO_REGISTERS) {
                        makeProgramFieldsVisible(step);
                        board.setStep(step);
                        board.setCurrentPlayer(board.getPlayer(0));
                    } else {
                        startProgrammingPhase();
                    }
                }
            } else {
                // this should not happen
                assert false;
            }
        } else {
            // this should not happen
            assert false;
        }
    }

    private void executeFieldActions() {
        for(int i = 0; i < board.getPlayersNumber(); i++) {
            Player currentPlayer = board.getPlayer(i);
            Space space = currentPlayer.getSpace();
            if (space != null) {
                List<FieldAction> actions = space.getActions();
                for(FieldAction action : actions ) {
                    action.doAction(this , space);
                }
            }
        }
    }

    // XXX V2
    private void executeCommand(@NotNull Player player, Command command) {
        if (player != null && player.board == board && command != null) {
            // XXX This is a very simplistic way of dealing with some basic cards and
            //     their execution. This should eventually be done in a more elegant way
            //     (this concerns the way cards are modelled as well as the way they are executed).

            switch (command) {
                case FORWARD:
                    this.moveForward(player);
                    break;
                case RIGHT:
                    this.turnRight(player);
                    break;
                case LEFT:
                    this.turnLeft(player);
                    break;
                case FAST_FORWARD:
                    this.fastForward(player);
                    break;
                case U_TURN:
                    this.uTurn(player);
                    break;
                case BACKWARD:
                    this.backwards(player);
                    break;
                default:
                    // DO NOTHING (for now)
            }
        }
    }

    // TODO V2
    public void move (Player player, Heading heading) {
        Space currentSpace = player.getSpace();
        Space nextSpace = board.getNeighbour(currentSpace, heading);
        if (nextSpace != null) {
            if (nextSpace.getPlayer() != null) { // Hvis en spiller allerede står der
                move(nextSpace.getPlayer(), heading); // Skub den anden spiller videre i samme retning
            }
            if (nextSpace.getPlayer() == null) { // Hvis feltet er ledigt efter skubning
                currentSpace.setPlayer(null);
                nextSpace.setPlayer(player);
            }

        }
    }

    public void moveForward(@NotNull Player player) {
        Space currentSpace = player.getSpace();
        Heading playerHeading = player.getHeading();

        if (currentSpace.getWalls().contains(playerHeading)) {
            return;
        }

        Space nextSpace = board.getNeighbour(currentSpace, playerHeading);

        if (nextSpace == null || nextSpace.getWalls().contains(playerHeading)) {
            return;
        }
        if (nextSpace.getPlayer() == null) {
            currentSpace.setPlayer(null); // Fjern spiller fra nuværende felt
            nextSpace.setPlayer(player); // Flyt spiller til næste felt
        }
    }



    // TODO V2
    public void fastForward(@NotNull Player player) {
       moveForward(player);
       moveForward(player);
        }



    // TODO V2
    public void turnRight(@NotNull Player player) {
        Space currentSpace = player.getSpace();
        Heading newHeading = player.getHeading().next();
        Space nextSpace = board.getNeighbour(currentSpace, newHeading);

        if (currentSpace.getWalls().contains(newHeading)) {
            return;
        }

        if (nextSpace != null && nextSpace.getWalls().contains(newHeading.prev().prev())) {
            return;
        }
        player.setHeading(newHeading);
    }

    // TODO V2
    public void turnLeft(@NotNull Player player) {
        Space currentSpace = player.getSpace();
        Heading newHeading = player.getHeading().prev();
        Space nextSpace = board.getNeighbour(currentSpace, newHeading);

        if (currentSpace.getWalls().contains(newHeading)) {
            return;
        }

        if (nextSpace != null && nextSpace.getWalls().contains(newHeading.prev().prev())) {
            return;
        }
        player.setHeading(newHeading);
    }

    public void uTurn(@NotNull Player player) {
        turnRight(player);
        turnRight(player);
    }

    public void backwards(@NotNull Player player) {
        turnRight(player);
        turnRight(player);
        moveForward(player);
        turnRight(player);
        turnRight(player);
    }

    public boolean moveCards(@NotNull CommandCardField source, @NotNull CommandCardField target) {
        CommandCard sourceCard = source.getCard();
        CommandCard targetCard = target.getCard();
        if (sourceCard != null && targetCard == null) {
            target.setCard(sourceCard);
            source.setCard(null);
            return true;
        } else {
            return false;
        }
    }

    /**
     * A method called when no corresponding controller operation is implemented yet.
     * This should eventually be removed.
     */
    public void notImplemented() {
        // XXX just for now to indicate that the actual method is not yet implemented
        assert false;
    }
}
