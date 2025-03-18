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
import javafx.scene.control.Alert;
import java.util.List;

/**
 * Controller-klassen styrer spillets flow, herunder spillernes bevægelser,
 * programmeringsfasen, aktiveringsfasen og kontrol af vinderbetingelser.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public class GameController {

    final public Board board; // Reference til spillebrættet

    /**
     * Konstruktør til at oprette en GameController med et bræt.
     *
     * @param board Spillebrættet for dette spil
     */
    public GameController(@NotNull Board board) {
        this.board = board;
    }

    /**
     * Flytter den nuværende spiller til et nyt felt, hvis det er ledigt.
     *
     * @param space Feltet, som spilleren skal flyttes til.
     */
    public void moveCurrentPlayerToSpace(@NotNull Space space) {
        Player currentPlayer = board.getCurrentPlayer();

        if (space.getPlayer() == null) { // Tjekker om feltet er tomt
            currentPlayer.setSpace(space);
            board.setCounter(); // Øger træk-tælleren

            int PlayerNumber = board.getPlayerNumber(currentPlayer);
            int totalPlayers = board.getPlayersNumber();
            int nextPlayerIndex = (PlayerNumber + 1) % totalPlayers;
            Player nextPlayer = board.getPlayer(nextPlayerIndex);

            board.setCurrentPlayer(nextPlayer); // Sætter næste spiller
        }
    }

    /**
     * Starter programmeringsfasen, hvor spillere får tilfældige kort.
     */
    public void startProgrammingPhase() {
        board.setPhase(Phase.PROGRAMMING);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);

        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            if (player != null) {
                // Nulstil programfelter
                for (int j = 0; j < Player.NO_REGISTERS; j++) {
                    CommandCardField field = player.getProgramField(j);
                    field.setCard(null);
                    field.setVisible(true);
                }
                // Tilføj nye kort til hånden
                for (int j = 0; j < Player.NO_CARDS; j++) {
                    CommandCardField field = player.getCardField(j);
                    field.setCard(generateRandomCommandCard());
                    field.setVisible(true);
                }
            }
        }
    }

    /**
     * Genererer et tilfældigt kommando-kort.
     * @return En tilfældig CommandCard
     */
    private CommandCard generateRandomCommandCard() {
        Command[] commands = Command.values();
        int random = (int) (Math.random() * commands.length);
        return new CommandCard(commands[random]);
    }

    /**
     * Flytter en spiller i en given retning, inklusiv skub af andre spillere.
     */
    public void move(Player player, Heading heading) {
        Space currentSpace = player.getSpace();
        Space nextSpace = board.getNeighbour(currentSpace, heading);
        if (nextSpace != null) {
            if (nextSpace.getPlayer() != null) {
                move(nextSpace.getPlayer(), heading);
            }
            if (nextSpace.getPlayer() == null) {
                currentSpace.setPlayer(null);
                nextSpace.setPlayer(player);
            }
        }
    }

    /**
     * Tjekker om en spiller har vundet ved at nå alle checkpoints.
     */
    void checkWinCondition(Player player) {
        int totalCheckpoints = 3;
        if (player.getCheckpointCount() == totalCheckpoints) {
            System.out.println("Spiller " + player.getName() + " har vundet!");
            showWinnerPopup(player);
            stopGame();
        }
    }

    /**
     * Viser en popup, når en spiller vinder spillet.
     */
    private void showWinnerPopup(Player player) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Spillet er slut!");
        alert.setHeaderText(null);
        alert.setContentText("Vinderen er: " + player.getName() + "!");
        alert.showAndWait();
    }

    /**
     * Stopper spillet ved at sætte spillet tilbage til startfasen.
     */
    private void stopGame() {
        board.setPhase(Phase.INITIALISATION); // Sæt spillet tilbage til start
        System.out.println("Spillet er nu slut!");
    }
}