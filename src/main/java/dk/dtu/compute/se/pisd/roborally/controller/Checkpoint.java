package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import org.jetbrains.annotations.NotNull;

/**
 * Repræsenterer et checkpoint på spillebrættet i RoboRally.
 * Når en spiller rammer et checkpoint, gemmes det, og det tjekkes, om spilleren har vundet.
 */
public class Checkpoint extends FieldAction {

    private final int checkpointID; // Unikt ID for hvert checkpoint

    /**
     * Konstruktor til at oprette et checkpoint med et specifikt ID.
     *
     * @param checkpointID Det ID, som dette checkpoint får.
     */
    public Checkpoint(int checkpointID) {
        this.checkpointID = checkpointID;
    }

    /**
     * Returnerer checkpointets ID.
     *
     * @return ID for dette checkpoint.
     */
    public int getCheckpoint() {
        return checkpointID;
    }

    /**
     * Udfører handlingen, når en spiller lander på et checkpoint.
     *
     * @param gameController Referencen til spilcontrolleren.
     * @param space Rummet (feltet) hvor spilleren står.
     * @return true, hvis spilleren registrerede et nyt checkpoint, ellers false.
     */
    @Override
    public boolean doAction(@NotNull GameController gameController, @NotNull Space space) {
        Player player = space.getPlayer();
        if (player != null) {
            if (checkpointID == 1 || player.getReachedCheckpoint().contains(checkpointID - 1)) {

                if (!player.getReachedCheckpoint().contains(checkpointID)) {
                    player.getReachedCheckpoint().add(checkpointID);
                    gameController.checkWinCondition(player);

                    return true;
                }
            }
        }
        return false;
    }

}

