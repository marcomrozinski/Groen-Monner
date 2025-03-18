package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Space;

/**
 * En factory-klasse til at oprette bræt (Board) i RoboRally-spillet.
 * Klassen er implementeret som et singleton-mønster, så der kun er én instans af den.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */
// XXX A3: might be used for creating a first slightly more interesting board.
public class BoardFactory {

    /**
     * Den eneste instans af BoardFactory (singleton).
     * Oprettes først når den efterspørges første gang (lazy instantiation).
     */
    static private BoardFactory instance = null;

    /**
     * Privat konstruktør for at forhindre direkte instansiering.
     * Dette sikrer, at factory-mønsteret fungerer som singleton.
     */
    private BoardFactory() {
    }

    /**
     * Returnerer den eneste instans af BoardFactory.
     * Opretter instansen, hvis den ikke allerede findes.
     *
     * @return Den eneste instans af BoardFactory.
     */
    public static BoardFactory getInstance() {
        if (instance == null) {
            instance = new BoardFactory();
        }
        return instance;
    }

    /**
     * Opretter et bræt baseret på navnet (simple eller advanced).
     *
     * @param name Navnet på brættet (f.eks. "simple" eller "advanced").
     * @return Et Board-objekt med den valgte type.
     */
    public Board createBoard(String name) {
        Board board = new Board(8, 8, name);

        if ("advanced".equalsIgnoreCase(name)) {
            board = createAdvancedBoard();
        }
        if ("simple".equalsIgnoreCase(name)) {
            board = createSimpleBoard();
        }
        return board;
    }

    /**
     * Opretter et simpelt 8x8 bræt med nogle vægge, transportbånd og checkpoints.
     *
     * @return Et simpelt bræt.
     */
    public Board createSimpleBoard() {
        Board simple = new Board(8,8, "Simple board");

        Space space = simple.getSpace(4,3);
        space.getWalls().add(Heading.SOUTH);
        ConveyorBelt action  = new ConveyorBelt(Heading.EAST);
        space.getActions().add(action);

        space = simple.getSpace(5,2);
        space.getWalls().add(Heading.NORTH);
        action  = new ConveyorBelt(Heading.SOUTH);
        space.getActions().add(action);

        space = simple.getSpace(2,4);
        space.getWalls().add(Heading.SOUTH);
        action  = new ConveyorBelt(Heading.WEST);
        space.getActions().add(action);

        space = simple.getSpace(6,5);
        action  = new ConveyorBelt(Heading.NORTH);
        space.getActions().add(action);

        space = simple.getSpace(1,2);
        Checkpoint checkpoint = new Checkpoint(1);
        space.getActions().add(checkpoint);

        space = simple.getSpace(3,7);
        checkpoint = new Checkpoint(2);
        space.getActions().add(checkpoint);

        space = simple.getSpace(7,7);
        checkpoint = new Checkpoint(3);
        space.getActions().add(checkpoint);

        return simple;
    }

    /**
     * Opretter et avanceret 16x8 bræt med flere vægge, transportbånd og checkpoints.
     *
     * @return Et avanceret bræt.
     */
    public Board createAdvancedBoard() {
        Board advanced = new Board(16, 8, "Advanced board");

        Space space = advanced.getSpace(4,0);
        space.getWalls().add(Heading.NORTH);
        ConveyorBelt action  = new ConveyorBelt(Heading.EAST);
        space.getActions().add(action);

        space = advanced.getSpace(1,3);
        space.getWalls().add(Heading.WEST);
        action  = new ConveyorBelt(Heading.NORTH);
        space.getActions().add(action);

        space = advanced.getSpace(6,5);
        space.getWalls().add(Heading.SOUTH);
        action  = new ConveyorBelt(Heading.WEST);
        space.getActions().add(action);

        space = advanced.getSpace(8,2);
        space.getWalls().add(Heading.SOUTH);
        action  = new ConveyorBelt(Heading.NORTH);
        space.getActions().add(action);

        space = advanced.getSpace(9,6);
        space.getWalls().add(Heading.WEST);
        space.getActions().add(action);

        space = advanced.getSpace(9,3);
        space.getWalls().add(Heading.WEST);
        action  = new ConveyorBelt(Heading.EAST);
        space.getActions().add(action);

        space = advanced.getSpace(11,3);
        space.getWalls().add(Heading.EAST);
        action  = new ConveyorBelt(Heading.SOUTH);
        space.getActions().add(action);

        space = advanced.getSpace(13,6);
        space.getWalls().add(Heading.EAST);
        action  = new ConveyorBelt(Heading.SOUTH);
        space.getActions().add(action);

        space = advanced.getSpace(14,2);
        space.getWalls().add(Heading.EAST);
        action  = new ConveyorBelt(Heading.NORTH);
        space.getActions().add(action);

        space = advanced.getSpace(15,1);
        space.getWalls().add(Heading.NORTH);
        action  = new ConveyorBelt(Heading.WEST);
        space.getActions().add(action);

        // Tilføjer checkpoints på brættet
        space = advanced.getSpace(1,2);
        Checkpoint checkpoint = new Checkpoint(1);
        space.getActions().add(checkpoint);

        space = advanced.getSpace(5,7);
        checkpoint = new Checkpoint(2);
        space.getActions().add(checkpoint);

        space = advanced.getSpace(14,1);
        checkpoint = new Checkpoint(3);
        space.getActions().add(checkpoint);

        return advanced;
    }
}

