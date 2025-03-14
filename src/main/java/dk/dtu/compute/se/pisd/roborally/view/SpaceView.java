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
package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.controller.Checkpoint;
import dk.dtu.compute.se.pisd.roborally.controller.ConveyorBelt;
import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.jetbrains.annotations.NotNull;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */

public class SpaceView extends StackPane implements ViewObserver {

    final public static int SPACE_HEIGHT = 30; // 60; // 75;
    final public static int SPACE_WIDTH = 30;  // 60; // 75;

    public final Space space;


    public SpaceView(@NotNull Space space) {
        this.space = space;


        this.setPrefSize(SPACE_WIDTH, SPACE_HEIGHT);
        this.setMinSize(SPACE_WIDTH, SPACE_HEIGHT);
        this.setMaxSize(SPACE_WIDTH, SPACE_HEIGHT);

        if ((space.x + space.y) % 2 == 0) {
            this.setStyle("-fx-background-color: white;");
        } else {
            this.setStyle("-fx-background-color: black;");
        }

        // updatePlayer();

        // This space view should listen to changes of the space
        space.attach(this);
        update(space);
    }

    private void drawWalls() {
        for (Heading direction : space.getWalls()) {
            Rectangle wall = new Rectangle();

            switch (direction) {
                case SOUTH:
                    wall = new Rectangle(SPACE_WIDTH, 3, Color.RED);
                    wall.setTranslateY(SPACE_HEIGHT / 2.0 - 1.5);
                    break;

                case EAST:
                    wall = new Rectangle(3, SPACE_HEIGHT, Color.RED);
                    wall.setTranslateX(SPACE_WIDTH / 2.0 - 1.5);
                    break;

                case WEST:
                    wall = new Rectangle(3, SPACE_HEIGHT, Color.RED);
                    wall.setTranslateX(-SPACE_WIDTH / 2.0 + 1.5); // Flytter en smule højre
                    break;
            }

            this.getChildren().add(wall);
        }
    }




    private void updatePlayer() {
        Player player = space.getPlayer();
        if (player != null) {
            Polygon arrow = new Polygon(0.0, 0.0,
                    10.0, 20.0,
                    20.0, 0.0 );
            try {
                arrow.setFill(Color.valueOf(player.getColor()));
            } catch (Exception e) {
                arrow.setFill(Color.MEDIUMPURPLE);
            }

            arrow.setRotate((90*player.getHeading().ordinal())%360);
            this.getChildren().add(arrow);
        }
    }

    @Override
    public void updateView(Subject subject) {
        // Kontrollerer om opdateringen er relevant for dette 'space'
        if (subject == this.space) {
            // fjerner tidligere grafik
            this.getChildren().clear();

            // Skifter baggrundsfarve på 'space' position
            if ((space.x + space.y) % 2 == 0) {
                this.setStyle("-fx-background-color: white;"); // Hvid baggrund
            } else {
                this.setStyle("-fx-background-color: black;"); // Sort baggrund
            }
            drawWalls(); // Tegner vægge i dette 'space'

            // XXX A3: drawing walls and action on the space (could be done
            //         here); it would be even better if fixed things on
            //         spaces  are only drawn once (and not on every update)
            drawConveyorBelt();
            drawCheckPoint();
            updatePlayer(); // Opdaterer spillerens visning i 'space'
        }
    }
    private void drawConveyorBelt() {
        for (FieldAction action : space.getActions()) {
            if (action instanceof ConveyorBelt) {
                ConveyorBelt belt = (ConveyorBelt) action;

                // Opret en pil (trekant)
                Polygon arrow = new Polygon(
                        0.0, 0.0,
                        10.0, 20.0,
                        20.0, 0.0
                );
                arrow.setFill(Color.GRAY); // Sætter farven til grå
                arrow.setRotate((90 * belt.getHeading().ordinal()) % 360); // Roterer pilen efter heading

                this.getChildren().add(arrow);

            }
        }
    }
    private void drawCheckPoint() {
        for(FieldAction action : space.getActions()) {
            if (action instanceof Checkpoint) {
                Checkpoint checkpoint = (Checkpoint) action;
                int checkpointNumber = checkpoint.getCheckpoint();

                // Opret en sekskant (hexagon) som repræsentation af checkpointet
                Polygon hexagon = new Polygon(
                        0.0, -10.0,
                        8.66, -5.0,
                        8.66, 5.0,
                        0.0, 10.0,
                        -8.66, 5.0,
                        -8.66, -5.0
                );

                hexagon.setFill(Color.YELLOW); // Sætter farven til gul
                hexagon.setStroke(Color.BLACK); // Tilføjer sort kant

                Text numberText = new Text(String.valueOf(checkpointNumber)); // Konverter tallet til tekst
                numberText.setFont(new Font(14)); // Sæt skriftstørrelse


                // Tilføj hexagon og tal til scenen
                this.getChildren().addAll(hexagon, numberText);


            }
        }
    }

}

