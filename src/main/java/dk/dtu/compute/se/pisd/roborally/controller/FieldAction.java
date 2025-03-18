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

import dk.dtu.compute.se.pisd.roborally.model.Space;

/**
 * Abstrakt klasse for handlinger (actions) der kan udføres på felter (spaces) på spillebrættet.
 *
 * Da denne klasse implementerer spil-logik, er den placeret i controller-pakken.
 * Dog fungerer den også som en modelklasse, da den repræsenterer elementer på brættet.
 *
 * FieldAction bruges som en superklasse for specifikke felthandlinger,
 * såsom transportbånd (ConveyorBelt) og checkpoints (Checkpoint).
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */
// XXX A3
public abstract class FieldAction {

    /**
     * Metode, der definerer en handling, som skal udføres på et felt.
     *
     * Denne metode skal implementeres af alle underklasser (fx ConveyorBelt og Checkpoint).
     * Den modtager GameController for at kunne interagere med spillets regler,
     * og Space, som repræsenterer det felt, hvor handlingen sker.
     *
     * @param gameController Den GameController, der styrer spillets flow.
     * @param space Det felt (Space), som handlingen udføres på.
     * @return true, hvis handlingen blev udført succesfuldt, ellers false.
     */
    public abstract boolean doAction(GameController gameController, Space space);
}
