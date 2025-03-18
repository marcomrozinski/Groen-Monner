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
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;

import static dk.dtu.compute.se.pisd.roborally.model.Heading.SOUTH;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
/**
 * Klassen repræsenterer en spiller i RoboRally-spillet. Hver spiller har
 * et navn, en farve, en position (space), en retning (heading) og et program (register)
 * samt en liste over opnåede checkpoints.
 *
 * Klassen anvender observer-mønsteret, så ændringer i en spiller kan notificere
 * andre objekter, der observerer spilleren.
 */

public class Player extends Subject {

    private List<Integer> reachedCheckpoint = new ArrayList<>();

    public List<Integer> getReachedCheckpoint() { // Returnerer checkpoint listen
        return reachedCheckpoint;
    }

    final public static int NO_REGISTERS = 5;
    final public static int NO_CARDS = 8;

    final public Board board;

    private String name;
    private String color;

    private Space space;
    private Heading heading = SOUTH;

    private CommandCardField[] program;
    private CommandCardField[] cards;

    public Player(@NotNull Board board, String color, @NotNull String name) {
        this.board = board;
        this.name = name;
        this.color = color;

        this.space = null;

        program = new CommandCardField[NO_REGISTERS];
        for (int i = 0; i < program.length; i++) {
            program[i] = new CommandCardField(this);
        }

        cards = new CommandCardField[NO_CARDS];
        for (int i = 0; i < cards.length; i++) {
            cards[i] = new CommandCardField(this);
        }
    }

    /**
     * Henter spillerens navn.
     *
     * @return Spillerens navn.
     */
    public String getName() {
        return name;
    }

    /**
     * Sætter spillerens navn og notifikér om ændringen.
     *
     * @param name Det nye navn for spilleren.
     */
    public void setName(String name) {
        if (name != null && !name.equals(this.name)) {
            this.name = name;
            notifyChange();
            if (space != null) {
                space.playerChanged();
            }
        }
    }

    /**
     * Henter farven på spilleren.
     *
     * @return Spillerens farve.
     */
    public String getColor() {
        return color;
    }

    /**
     * Ændrer farven på spilleren og notifikér om ændringen.
     *
     * @param color Den nye farve for spilleren.
     */
    public void setColor(String color) {
        this.color = color;
        notifyChange();
        if (space != null) {
            space.playerChanged();
        }
    }

    /**
     * Henter spillerens nuværende position (space) på brættet.
     *
     * @return Spilleren nuværende placering (Space).
     */
    public Space getSpace() {
        return space;
    }

    /**
     * Sætter spillerens position på brættet, og opdaterer både den gamle
     * og den nye position.
     *
     * @param space Den nye position for spilleren.
     */
    public void setSpace(Space space) {
        Space oldSpace = this.space;
        if (space != oldSpace &&
                (space == null || space.board == this.board)) {
            this.space = space;
            if (oldSpace != null) {
                oldSpace.setPlayer(null);
            }
            if (space != null) {
                space.setPlayer(this);
            }
            notifyChange();
        }
    }

    /**
     * Henter spillerens nuværende retning.
     *
     * @return Spillerens retning (Heading).
     */
    public Heading getHeading() {
        return heading;
    }

    /**
     * Sætter spillerens retning og notifikér om ændringen.
     *
     * @param heading Den nye retning for spilleren.
     */
    public void setHeading(@NotNull Heading heading) {
        if (heading != this.heading) {
            this.heading = heading;
            notifyChange();
            if (space != null) {
                space.playerChanged();
            }
        }
    }

    /**
     * Henter et programfelt for spilleren.
     *
     * @param i Indekset for det ønskede programfelt.
     * @return Programfeltet ved det angivne indeks.
     */
    public CommandCardField getProgramField(int i) {
        return program[i];
    }

    /**
     * Henter et kortfelt for spilleren.
     *
     * @param i Indekset for det ønskede kortfelt.
     * @return Kortfeltet ved det angivne indeks.
     */
    public CommandCardField getCardField(int i) {
        return cards[i];
    }

    /**
     * Henter antallet af checkpoints, som spilleren har opnået.
     *
     * @return Antallet af nåede checkpoints.
     */
    public int getCheckpointCount() {
        return reachedCheckpoint.size();
    }

}
