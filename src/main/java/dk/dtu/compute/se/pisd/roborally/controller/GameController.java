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
 * ...
 * TODO: we should really write these docstrings, else Carlos will fail us all
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class GameController {

    /**
     * Repræsenterer spillebrættet, der hører til denne spilcontroller.
     */
    final public Board board;

    /**
     * Opretter en ny GameController-instans med det angivne spillebræt.
     *
     * @param board det tilknyttede spillebræt; må ikke være null
     */
    public GameController(@NotNull Board board) {
        this.board = board;
    }

    /**
     * Flytter den aktuelle spiller til det angivne felt, hvis feltet er ledigt.
     * Herefter opdateres tælleren, og turen gives videre til næste spiller.
     *
     * @param space det felt, som spilleren skal flyttes til; må ikke være null
     */
    public void moveCurrentPlayerToSpace(@NotNull Space space) {
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


    /**
     * Starter programmeringsfasen i spillet.
     * Nulstiller alle spilleres programmeringsfelter og giver dem nye tilfældige kommandokort.
     * Sætter spillets fase til PROGRAMMING, nulstiller trintælleren og sætter den første spiller som aktuel spiller.
     */
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


    /**
     * Genererer og returnerer et tilfældigt kommandokort.
     *
     * @return et nyt CommandCard med en tilfældigt valgt kommando
     */
    private CommandCard generateRandomCommandCard() {
        Command[] commands = Command.values();
        int random = (int) (Math.random() * commands.length);
        return new CommandCard(commands[random]);
    }


    /**
     * Afslutter programmeringsfasen.
     * Skjuler alle programmeringsfelter og gør det første programmeringsfelt synligt.
     * Sætter spillets fase til ACTIVATION, nulstiller trintælleren og sætter den første spiller som aktuel spiller.
     */
    public void finishProgrammingPhase() {
        makeProgramFieldsInvisible();
        makeProgramFieldsVisible(0);
        board.setPhase(Phase.ACTIVATION);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);
    }
    /**
     * Gør programmeringsfeltet synligt for alle spillere for det angivne register.
     *
     * @param register indekset på det registerfelt, der skal gøres synligt; skal være mellem 0 og Player.NO_REGISTERS - 1
     */
    private void makeProgramFieldsVisible(int register) {
        if (register >= 0 && register < Player.NO_REGISTERS) {
            for (int i = 0; i < board.getPlayersNumber(); i++) {
                Player player = board.getPlayer(i);
                CommandCardField field = player.getProgramField(register);
                field.setVisible(true);
            }
        }
    }


    /**
     * Skjuler alle programmeringsfelter for samtlige spillere.
     */
    private void makeProgramFieldsInvisible() {
        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            for (int j = 0; j < Player.NO_REGISTERS; j++) {
                CommandCardField field = player.getProgramField(j);
                field.setVisible(false);
            }
        }
    }


    /**
     * Starter eksekveringen af spillernes programmer uden trinvis tilstand.
     * Sætter stepMode til false og fortsætter eksekveringen af programmerne.
     */
    public void executePrograms() {
        board.setStepMode(false);
        continuePrograms();
    }


    /**
     * Udfører et enkelt trin i eksekveringen af spillernes programmer.
     * Aktiverer trinvis tilstand (stepMode) og fortsætter derefter programmernes eksekvering.
     */
    public void executeStep() {
        board.setStepMode(true);
        continuePrograms();
    }


    /**
     * Fortsætter eksekveringen af spillernes programmer.
     * Kalder løbende executeNextStep(), indtil fasen ikke længere er ACTIVATION,
     * eller indtil stepMode aktiveres (trinvis tilstand).
     */
    private void continuePrograms() {
        do {
            executeNextStep();
        } while (board.getPhase() == Phase.ACTIVATION && !board.isStepMode());
    }

    /**
     * Eksekverer næste trin i spillernes programmeringssekvens.
     * Henter den aktuelle spiller og udfører dennes kommando fra programfeltet.
     * Metoden håndterer spillerskift, faseovergang og styrer rækkefølgen af spillerhandlinger.
     * Hvis spillerinteraktion er påkrævet (f.eks. LEFT_OR_RIGHT-valg), afbrydes udførslen midlertidigt.
     * Når alle spilleres kommandoer for det nuværende trin er udført, fortsættes til næste trin.
     * Når alle trin er afsluttet, eksekveres feltaktioner, og en ny programmeringsfase starter.
     *
     * Metoden bør kun kaldes under ACTIVATION- eller INTERACTION_FINISHED-faserne.
     */
    private void executeNextStep() {
        Player currentPlayer = board.getCurrentPlayer();

        if ((board.getPhase() == Phase.ACTIVATION || board.getPhase() == Phase.INTERACTION_FINISHED) && currentPlayer != null) {
            int step = board.getStep();

            if (step >= 0 && step < Player.NO_REGISTERS) {
                CommandCard card = currentPlayer.getProgramField(step).getCard();

                if (card != null) {
                    Command command = card.command;
                    executeCommand(currentPlayer, command);

                    // Hvis vi er i interaktionsfasen (LEFT_OR_RIGHT), vent på spillerens valg
                    if (board.getPhase() == Phase.PLAYER_INTERACTION) {
                        return;
                    }
                }

                // Skifter korrekt til næste spiller
                int nextPlayerNumber = (board.getPlayerNumber(currentPlayer) + 1) % board.getPlayersNumber();
                Player nextPlayer = board.getPlayer(nextPlayerNumber);
                board.setCurrentPlayer(nextPlayer);

                // Fortsætter korrekt til næste trin eller næste spiller
                if (nextPlayerNumber == 0) { // Alle spillere har haft deres tur
                    step++;
                    if (step < Player.NO_REGISTERS) {
                        board.setStep(step);
                        makeProgramFieldsVisible(step);
                    } else {
                        // Alle kort er brugt, afslut aktiveringsfasen og start ny runde
                        executeFieldActions();
                        startProgrammingPhase();
                        return;
                    }
                }

                // Sikrer, at næste spiller får sin tur
                board.setPhase(Phase.ACTIVATION);
                executeNextStep();
            } else {
                // Uventet trinindeks; bør aldrig ske
                assert false;
            }
        } else {
            // Uventet fase eller null spiller; bør aldrig ske
            assert false;
        }
    }



    /**
     * Eksekverer feltaktioner for alle spillere.
     * Gennemgår hver spillers aktuelle felt, henter alle tilknyttede feltaktioner og udfører dem.
     */
    private void executeFieldActions() {
        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player currentPlayer = board.getPlayer(i);
            Space space = currentPlayer.getSpace();
            if (space != null) {
                List<FieldAction> actions = space.getActions();
                for (FieldAction action : actions) {
                    action.doAction(this, space);
                }
            }
        }
    }


    /**
     * Udfører den angivne kommando for den givne spiller.
     *
     * Håndterer spillerens bevægelse baseret på kommandotypen.
     * Hvis kommandoen kræver spillerinteraktion (f.eks. LEFT_OR_RIGHT), sættes fasen til PLAYER_INTERACTION.
     *
     * Bemærk: Nuværende implementering er simpel og bør senere erstattes af en mere elegant løsning.
     *
     * @param player spilleren, som kommandoen skal udføres for; må ikke være null
     * @param command den kommando, der skal udføres; må ikke være null
     */
    private void executeCommand(@NotNull Player player, Command command) {
        if (player != null && player.board == board && command != null) {
            switch (command) {
                case FORWARD:
                    this.moveForward(player);
                    board.setCounter();
                    break;
                case RIGHT:
                    this.turnRight(player);
                    board.setCounter();
                    break;
                case LEFT:
                    this.turnLeft(player);
                    board.setCounter();
                    break;
                case FAST_FORWARD:
                    this.fastForward(player);
                    board.setCounter();
                    break;
                case U_TURN:
                    this.uTurn(player);
                    board.setCounter();
                    break;
                case BACKWARD:
                    this.backwards(player);
                    board.setCounter();
                    break;
                case LEFT_OR_RIGHT:
                    board.setPhase(Phase.PLAYER_INTERACTION);// Afventer spillerens valg
                    board.setCounter();
                    break;
                default:
                    // Ukendt kommando; gør ingenting
            }
        }
    }

    /**
     * Håndterer spillerens valg om at dreje til venstre under en spillerinteraktion.
     * Udfører en venstredrejning for spilleren og afslutter spillerinteraktionen.
     *
     * @param player spilleren, som har valgt at dreje til venstre; må ikke være null
     */
    public void playerChoseLeft(@NotNull Player player) {
        // Spilleren drejer til venstre
        turnLeft(player);

        endPlayerInteraction(player);
    }


    /**
     * Håndterer spillerens valg om at dreje til højre under en spillerinteraktion.
     * Udfører en højredrejning for spilleren og afslutter interaktionen.
     *
     * @param player spilleren, der har valgt at dreje til højre; må ikke være null
     */
    public void playerChoseRight(@NotNull Player player) {
        turnRight(player);
        endPlayerInteraction(player);
    }

    /**
     * Afslutter spillerinteraktion efter et spiller-valg (f.eks. drejning).
     * Opdaterer fasen, skifter til næste spiller og fortsætter eksekveringen af spillet.
     *
     * @param player spilleren, som netop har afsluttet interaktionen
     */
    private void endPlayerInteraction(Player player) {
        board.setPhase(Phase.INTERACTION_FINISHED);

        int step = board.getStep();
        int currentPlayerIndex = board.getPlayerNumber(player);
        int nextPlayerIndex = (currentPlayerIndex + 1) % board.getPlayersNumber();
        Player nextPlayer = board.getPlayer(nextPlayerIndex);

        board.setCurrentPlayer(nextPlayer);

        if (nextPlayerIndex == 0) {
            step++;
            if (step < Player.NO_REGISTERS) {
                board.setStep(step);
                makeProgramFieldsVisible(step);
            } else {
                executeFieldActions();
                startProgrammingPhase();
                return;
            }
        }

        board.setPhase(Phase.ACTIVATION);
        executeNextStep();
    }

    /**
     * Flytter spilleren i den angivne retning. Hvis det næste felt er optaget, skubbes den anden spiller videre.
     * Feltaktioner udføres efter en succesfuld flytning.
     *
     * @param player spilleren, der skal flyttes
     * @param heading retningen spilleren flyttes i
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

                for (FieldAction action : nextSpace.getActions()) {
                    action.doAction(this, nextSpace);
                }
            }
        }
    }

    /**
     * Flytter spilleren ét felt fremad i den retning, spilleren aktuelt vender.
     *
     * @param player spilleren, der skal flyttes fremad; må ikke være null
     */
    public void moveForward(@NotNull Player player) {
        move(player, player.getHeading());
    }

    /**
     * Flytter spilleren to felter fremad i den retning, spilleren aktuelt vender.
     *
     * @param player spilleren, der skal flyttes hurtigt fremad; må ikke være null
     */
    public void fastForward(@NotNull Player player) {
        moveForward(player);
        moveForward(player);
    }

    /**
     * Drejer spilleren 90 grader mod højre, hvis der ikke er vægge, der forhindrer drejningen.
     *
     * @param player spilleren, som skal drejes; må ikke være null
     */
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

    /**
     * Drejer spilleren 90 grader mod venstre, hvis der ikke er vægge, der forhindrer drejningen.
     *
     * @param player spilleren, som skal drejes; må ikke være null
     */
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

    /**
     * Drejer spilleren 180 grader (U-vending).
     *
     * @param player spilleren, som skal vende 180 grader; må ikke være null
     */
    public void uTurn(@NotNull Player player) {
        turnRight(player);
        turnRight(player);
    }

    /**
     * Flytter spilleren ét felt bagud.
     *
     * @param player spilleren, som skal flyttes bagud; må ikke være null
     */
    public void backwards(@NotNull Player player) {
        turnRight(player);
        turnRight(player);
        moveForward(player);
        turnRight(player);
        turnRight(player);
    }

    /**
     * Flytter et kommandokort fra ét felt til et andet, hvis det nye felt er ledigt.
     *
     * @param source feltet, hvor kortet flyttes fra; må ikke være null
     * @param target feltet, hvor kortet flyttes til; må ikke være null
     * @return true, hvis flytningen lykkedes; false ellers
     */
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
     * Viser et popup-vindue med navnet på den vindende spiller.
     *
     * @param player spilleren, der har vundet spillet
     */
    private void showWinnerPopup(Player player) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Spillet er slut!");
        alert.setHeaderText(null);
        alert.setContentText("Vinderen er: " + player.getName() + "!");
        alert.showAndWait();
    }


    /**
     * Stopper spillet og nulstiller fasen til INITIALISATION.
     */
    private void stopGame() {
        board.setPhase(Phase.INITIALISATION);
    }

    private static Player winner = null;

    public static Player getWinner() {
        return winner;
    }
    public static void setWinner(Player winningPlayer) { // statisk setter
        winner = winningPlayer;
    }
    /**
     * Tjekker om spilleren har nået alle checkpoints og dermed opfyldt vinderbetingelserne.
     * Hvis spilleren har vundet, vises en popup, og spillet stoppes.
     *
     * @param player spilleren, der skal tjekkes for vinderbetingelser
     */

    void checkWinCondition(Player player) {
        int totalCheckpoints = 3;

        if (player.getCheckpointCount() == totalCheckpoints) {
            showWinnerPopup(player);
            setWinner(player);
            stopGame();
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
