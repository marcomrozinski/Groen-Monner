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
        Player player = space.getPlayer(); // Henter spilleren, hvis der er en på dette felt

        if (player != null) {
            // Spilleren kan kun aktivere dette checkpoint, hvis det er det første
            // eller hvis spilleren allerede har det forrige checkpoint
            if (checkpointID == 1 || player.getReachedCheckpoint().contains(checkpointID - 1)) {

                // Spilleren kan kun registrere checkpointet, hvis det ikke allerede er nået
                if (!player.getReachedCheckpoint().contains(checkpointID)) {
                    player.getReachedCheckpoint().add(checkpointID); // Tilføjer checkpoint til spillerens liste
                    // **Tjek, om spilleren har vundet spillet**
                    gameController.checkWinCondition(player);

                    return true; // Handling udført succesfuldt
                }
            }
        }
        return false; // Spilleren kunne ikke aktivere checkpointet
    }

}

