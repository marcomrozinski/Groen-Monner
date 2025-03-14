package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import org.jetbrains.annotations.NotNull;

public class Checkpoint extends FieldAction {
    private final int checkpointID;

    public Checkpoint(int checkpointID) { // Constructor til at definere checkpoint
        this.checkpointID = checkpointID;
    }

    public int getCheckpoint() { //Gemmer checkpoint ID for det respektive checkpoint
        return checkpointID;

    }

    @Override
    public boolean doAction(@NotNull GameController gameController, @NotNull Space space) {
        Player player = space.getPlayer();
        if (player != null) {
            if (checkpointID == 1 || player.getReachedCheckpoint().contains(checkpointID - 1)) {
                if (!player.getReachedCheckpoint().contains(checkpointID)) {
                    player.getReachedCheckpoint().add(checkpointID);

                    // Tjek om spilleren har vundet
                    gameController.checkWinCondition(player);

                    return true;
                }
            }
        }
        return false;
    }


}
