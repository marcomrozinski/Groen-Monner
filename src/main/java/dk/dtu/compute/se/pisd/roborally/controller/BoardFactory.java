package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Space;

/**
 * A factory for creating boards. The factory itself is implemented as a singleton.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */
// XXX A3: might be used for creating a first slightly more interesting board.
public class BoardFactory {

    /**
     * The single instance of this class, which is lazily instantiated on demand.
     */
    static private BoardFactory instance = null;

    /**
     * Constructor for BoardFactory. It is private in order to make the factory a singleton.
     */
    private BoardFactory() {
    }

    /**
     * Returns the single instance of this factory. The instance is lazily
     * instantiated when requested for the first time.
     *
     * @return the single instance of the BoardFactory
     */
    public static BoardFactory getInstance() {
        if (instance == null) {
            instance = new BoardFactory();
        }
        return instance;


    }

    public Board createBoard(String name) {
        Board board = null;
        board = new Board(8, 8, name);

        if (("advanced".equalsIgnoreCase(name))) {
            board = createAdvancedBoard();
        }
        if (("simple".equalsIgnoreCase(name))) {
            board = createSimpleBoard();
        }
        return board;
    }

    public Board createSimpleBoard() {
        Board simple = new Board(8,8, "Simple board");

        // add some walls, actions and checkpoints to some spaces
        Space space = simple.getSpace(0,0);
        space.getWalls().add(Heading.SOUTH);
        ConveyorBelt action  = new ConveyorBelt(Heading.WEST, 2);
        space.getActions().add(action);


        space = simple.getSpace(1,0);
        space.getWalls().add(Heading.NORTH);
        action  = new ConveyorBelt(Heading.WEST, 1);
        space.getActions().add(action);

        //emil er spasser

        space = simple.getSpace(1,1);
        space.getWalls().add(Heading.WEST);
        action  = new ConveyorBelt(Heading.NORTH, 2);
        space.getActions().add(action);

        space = simple.getSpace(5,5);
        space.getWalls().add(Heading.SOUTH);
        action  = new ConveyorBelt(Heading.WEST, 1);
        space.getActions().add(action);

        space = simple.getSpace(6,5);
        action  = new ConveyorBelt(Heading.NORTH, 2);
        space.getActions().add(action);

        return simple;
    }

public Board createAdvancedBoard() {
        Board advanced = new Board(16, 8, "Advanced board");

    Space space = advanced.getSpace(4,0);
    space.getWalls().add(Heading.NORTH);
    ConveyorBelt action  = new ConveyorBelt(Heading.EAST, 2);
    space.getActions().add(action);

    space = advanced.getSpace(3,0);
    space.getWalls().add(Heading.NORTH);
    action  = new ConveyorBelt(Heading.WEST, 1);
    space.getActions().add(action);

    space = advanced.getSpace(1,1);
    space.getWalls().add(Heading.WEST);
    action  = new ConveyorBelt(Heading.NORTH, 2);
    space.getActions().add(action);

    space = advanced.getSpace(5,5);
    space.getWalls().add(Heading.SOUTH);
    action  = new ConveyorBelt(Heading.WEST, 1);
    space.getActions().add(action);

    space = advanced.getSpace(6,5);
    action  = new ConveyorBelt(Heading.SOUTH, 2);
    space.getActions().add(action);

    space = advanced.getSpace(8,2);
    space.getWalls().add(Heading.SOUTH);
    action  = new ConveyorBelt(Heading.NORTH, 2);
    space.getActions().add(action);

    space = advanced.getSpace(9,6);
    space.getWalls().add(Heading.WEST);
    action  = new ConveyorBelt(Heading.WEST, 1);
    space.getActions().add(action);

    space = advanced.getSpace(11,3);
    space.getWalls().add(Heading.EAST);
    action  = new ConveyorBelt(Heading.SOUTH, 1);
    space.getActions().add(action);

    space = advanced.getSpace(11,7);
    space.getWalls().add(Heading.SOUTH);
    action  = new ConveyorBelt(Heading.WEST, 2);
    space.getActions().add(action);

    space = advanced.getSpace(13,6);
    space.getWalls().add(Heading.WEST);
    action  = new ConveyorBelt(Heading.SOUTH, 1);
    space.getActions().add(action);

    space = advanced.getSpace(14,2);
    space.getWalls().add(Heading.EAST);
    action  = new ConveyorBelt(Heading.NORTH, 1);
    space.getActions().add(action);

    space = advanced.getSpace(15,1);
    space.getWalls().add(Heading.NORTH);
    action  = new ConveyorBelt(Heading.WEST, 2);
    space.getActions().add(action);

    space = advanced.getSpace(15,7);
    space.getWalls().add(Heading.WEST);
    action  = new ConveyorBelt(Heading.NORTH, 2);
    space.getActions().add(action);


    return advanced;
}


}

