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

import dk.dtu.compute.se.pisd.designpatterns.observer.Observer;
import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.RoboRally;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Controller for applikationen, der håndterer spillets flow, herunder start, stop og interaktion med UI.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public class AppController implements Observer {

    final private List<Integer> PLAYER_NUMBER_OPTIONS = Arrays.asList(2, 3, 4, 5, 6);

    final private List<String> PLAYER_COLORS = Arrays.asList("red", "green", "blue", "orange", "grey", "magenta");

    final private List<String> boardChoices = Arrays.asList("simple", "advanced");

    final private RoboRally roboRally;

    private GameController gameController;

    /**
     * Konstruktør for AppController.
     *
     * @param roboRally Reference til RoboRally applikationen.
     */
    public AppController(@NotNull RoboRally roboRally) {
        this.roboRally = roboRally;
    }

    /**
     * Starter et nyt spil.
     */
    public void newGame() {
        ChoiceDialog<Integer> dialog = new ChoiceDialog<>(PLAYER_NUMBER_OPTIONS.get(0), PLAYER_NUMBER_OPTIONS);
        dialog.setTitle("Player number");
        dialog.setHeaderText("Select number of players");
        Optional<Integer> result = dialog.showAndWait();

        if (result.isPresent()) {
            if (gameController != null) {
                if (!stopGame()) {
                    return;
                }
            }

            ChoiceDialog<String> boardDialog = new ChoiceDialog<>("simple", boardChoices);
            boardDialog.setTitle("Board Selection");
            boardDialog.setHeaderText("Select board type");
            Optional<String> boardResult = boardDialog.showAndWait();

            if (!boardResult.isPresent()) return;

            String boardType = boardResult.get();

            BoardFactory factory = BoardFactory.getInstance();
            Board board = factory.createBoard(boardType);

            gameController = new GameController(board);
            int no = result.get();

            for (int i = 0; i < no; i++) {
                Player player = new Player(board, PLAYER_COLORS.get(i), "Player " + (i + 1));
                board.addPlayer(player);
                player.setSpace(board.getSpace(i % board.width, i));
            }

            gameController.startProgrammingPhase();

            roboRally.createBoardView(gameController);
        }
    }

    /**
     * Gemmer det nuværende spil (skal implementeres senere).
     */
    public void saveGame() {
        // TODO V4a: needs to be implemented
    }

    /**
     * Indlæser et tidligere spil (skal implementeres senere).
     * Hvis intet spil er i gang, startes et nyt spil.
     */
    public void loadGame() {
        // TODO V4a: needs to be implemented
        if (gameController == null) {
            newGame();
        }
    }

    /**
     * Stopper det nuværende spil og giver mulighed for at gemme det.
     *
     * @return true, hvis spillet blev stoppet succesfuldt, ellers false.
     */
    public boolean stopGame() {
        if (gameController != null) {
            saveGame();

            gameController = null;

            roboRally.createBoardView(null);
            return true;
        }
        return false;
    }

    /**
     * Afslutter applikationen, men giver mulighed for at stoppe det nuværende spil først.
     */
    public void exit() {
        if (gameController != null) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Exit RoboRally?");
            alert.setContentText("Are you sure you want to exit RoboRally?");
            Optional<ButtonType> result = alert.showAndWait();

            if (!result.isPresent() || result.get() != ButtonType.OK) {
                return;
            }
        }

        if (gameController == null || stopGame()) {
            Platform.exit();
        }
    }

    /**
     * Tjekker om der kører et spil.
     *
     * @return true, hvis der er et aktivt spil, ellers false.
     */
    public boolean isGameRunning() {
        return gameController != null;
    }

    /**
     * Opdaterer controlleren, når observerede objekter ændrer tilstand.
     * (Lige nu gør denne metode ingenting, men den kan bruges i fremtiden)
     *
     * @param subject Det observerede objekt.
     */
    @Override
    public void update(Subject subject) {
        // XXX do nothing for now
    }
}

