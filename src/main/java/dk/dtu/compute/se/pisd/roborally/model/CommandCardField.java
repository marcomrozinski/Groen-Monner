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
package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class CommandCardField extends Subject {

    /**
     * Spilleren, som dette kommandokortfelt tilhører.
     */
    final public Player player;

    private CommandCard card;

    private boolean visible;

    /**
     * Opretter et nyt kommandokortfelt for den angivne spiller.
     * Standardindstillingen for synlighed er sat til true.
     *
     * @param player spilleren som feltet tilhører
     */
    public CommandCardField(Player player) {
        this.player = player;
        this.card = null;
        this.visible = true;
    }

    /**
     * Henter det kommandokort, som er placeret i dette felt.
     *
     * @return det nuværende kommandokort, eller null hvis intet kort er til stede
     */
    public CommandCard getCard() {
        return card;
    }

    /**
     * Placerer et nyt kommandokort i dette felt eller fjerner et kort ved at sætte det til null.
     * Hvis kortet ændres, vil observere blive underrettet om ændringen.
     *
     * @param card det nye kommandokort, eller null for at fjerne kortet fra feltet
     */
    public void setCard(CommandCard card) {
        if (card != this.card) {
            this.card = card;
            notifyChange();
        }
    }

    /**
     * Returnerer om feltet er synligt.
     *
     * @return sand hvis feltet er synligt, ellers falsk
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Sætter synligheden af dette felt. Hvis synligheden ændres, vil observere blive underrettet.
     *
     * @param visible sand for at gøre feltet synligt, falsk for at gøre det usynligt
     */
    public void setVisible(boolean visible) {
        if (visible != this.visible) {
            this.visible = visible;
            notifyChange();
        }
    }
}