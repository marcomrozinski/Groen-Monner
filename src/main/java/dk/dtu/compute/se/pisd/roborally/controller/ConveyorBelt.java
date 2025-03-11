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

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import org.jetbrains.annotations.NotNull;

/**
 * This class represents a conveyor belt on a space.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
// XXX A3
public class ConveyorBelt extends FieldAction {

    private Heading heading;
    private int speed; // 1 = enkelt bånd, 2 = dobbelt bånd

    // KONSTRUKTOR: Sørger for at vi kan angive heading og speed
    public ConveyorBelt(Heading heading, int speed) {
        this.heading = heading;
        this.speed = speed;
    }

    public Heading getHeading() {
        return heading;
    }

    public int getSpeed() {
        return speed;
    }

    /**
     * Implementation of the action of a conveyor belt. Needs to be implemented for A3.
     */
    @Override
    public boolean doAction(@NotNull GameController gameController, @NotNull Space space) {
        if (space.getPlayer() == null || heading == null) {
            return false;
        }

        Board board = space.board;
        Player player = space.getPlayer();

        // Ryk spilleren op til 'speed' felter frem
        Space currentSpace = space;
        for (int i = 0; i < speed; i++) {
            Space nextSpace = board.getNeighbour(currentSpace, heading);
            if (nextSpace == null || nextSpace.getPlayer() != null) {
                break; // Stop hvis næste plads er optaget eller udenfor boardet
            }
            nextSpace.setPlayer(player);
            currentSpace.setPlayer(null);
            currentSpace = nextSpace;
        }
        return true;
    }
}

