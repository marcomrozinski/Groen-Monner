package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Space;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import org.jetbrains.annotations.NotNull;

public class Checkpoint extends FieldAction {
    private final int checkpointID;

    public Checkpoint(int checkpointID) { // Constructor til at definere checkpoint
        this.checkpointID = checkpointID;
    }

    public int getCheckpoint() { // Gemmer checkpoint ID for det respektive checkpoint
        return checkpointID;
    }

    @Override
    public boolean doAction(@NotNull GameController gameController, @NotNull Space space) {
        Player player = space.getPlayer();

        if (player != null && player == gameController.board.getCurrentPlayer()) {
            // Tjekker om spilleren har nået de tidligere checkpoints i rækkefølge
            if (checkpointID == 1 || player.getReachedCheckpoint().contains(checkpointID - 1)) {
                if (!player.getReachedCheckpoint().contains(checkpointID)) {
                    player.getReachedCheckpoint().add(checkpointID);

                    // Hvis dette er det sidste checkpoint, spilleren mangler
                    if (isLastCheckpoint(gameController.board, checkpointID)) {
                        gameController.setWinner(player); // Spilleren vinder
                    }
                    return true;
                }
            }
        }
        return false; // Hvis spilleren ikke kan tage dette checkpoint
    }

    private boolean isLastCheckpoint(Board board, int checkpointID) {
        int maxCheckpoint = 0;

        for (Space space : board.getSpaces()) {
            if (space.getActions() != null && space.getActions() instanceof Checkpoint) {
                Checkpoint checkpoint = (Checkpoint) space.getActions(); // Typecasting
                int id = checkpoint.getCheckpoint();
                if (id > maxCheckpoint) {
                    maxCheckpoint = id;
                }
            }
        }
        return checkpointID == maxCheckpoint; // Tjekker om dette er det højeste checkpoint
    }



}

