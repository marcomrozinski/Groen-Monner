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
 * Repræsenterer et transportbånd på et felt (Space) i RoboRally-spillet.
 * Når en spiller lander på et felt med et transportbånd, bliver spilleren flyttet i en bestemt retning.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */
// XXX A3
public class ConveyorBelt extends FieldAction {

    private Heading heading; // Transportbåndets retning (NORD, SYD, ØST, VEST)

    /**
     * Konstruktor til at oprette et transportbånd med en given retning.
     *
     * @param heading Den retning, transportbåndet bevæger spilleren i.
     */
    public ConveyorBelt(Heading heading) {
        this.heading = heading;
    }

    /**
     * Henter transportbåndets retning.
     *
     * @return Transportbåndets heading (NORD, SYD, ØST, VEST).
     */
    public Heading getHeading() {
        return heading;
    }

    /**
     * Udfører handlingen, når en spiller lander på transportbåndet.
     * Spilleren flyttes automatisk i transportbåndets retning.
     *
     * @param gameController Referencen til spilcontrolleren.
     * @param space Rummet (feltet) hvor spilleren står.
     * @return true, hvis spilleren blev flyttet, ellers false.
     */
    @Override
    public boolean doAction(@NotNull GameController gameController, @NotNull Space space) {
        // Hvis der ikke er en spiller på feltet, eller heading ikke er sat, gør vi ingenting
        if (space.getPlayer() == null || heading == null) {
            return false;
        }

        // Hent spillebrættet og spilleren på feltet
        Board board = space.board;
        Player player = space.getPlayer();

        // Flyt spilleren i transportbåndets retning
        gameController.move(player, heading);

        return true; // Handling udført succesfuldt
    }
}


