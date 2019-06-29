
import elements.Pawn;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;


public class boardGameMain extends Application {

    private Image imageback = new Image("background.jpg");
    private Image blackPawn = new Image("blackPawn.png");
    private ImageView selection = new ImageView(new Image("selectedPawn.png"));
    private Pawn[][] pawnArray = setupArray();
    boolean moved = true;
    Label labelTeam = new Label();
    Label labelBlackScore = new Label();
    Label labelWhiteScore = new Label();


    //private Optional<Pawn> selectedPawn = Optional.empty();
    private Pawn selectedPawn = null;
    int player = 0;
    int whiteScore = 0;
    int blackScore = 0;


    public static void main(String[] args) {
        launch(args);
    }


    //Create splitpane
    //top part for data shit
    //bottom part for game shit
    @Override
    public void start(Stage primaryStage) throws Exception {
        BackgroundSize backgroundSize = new BackgroundSize(imageback.getWidth(), imageback.getHeight(), true, true, true, false);
        BackgroundImage backgroundImage = new BackgroundImage(imageback, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
        Background background = new Background(backgroundImage);


        //splitPane.setStyle("-fx-width:800px");
        SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.VERTICAL);
        GridPane test = new GridPane();


        GridPane checkersBoard = new GridPane();
        checkersBoard.setAlignment(Pos.CENTER);
        ColumnConstraints column = new ColumnConstraints(blackPawn.getWidth());
        RowConstraints row = new RowConstraints(blackPawn.getHeight());
        checkersBoard.setGridLinesVisible(true);
        checkersBoard.setBackground(background);
        checkersBoard.getColumnConstraints().add(new ColumnConstraints(blackPawn.getWidth()));
        checkersBoard.getRowConstraints().add(new RowConstraints(blackPawn.getHeight()));
        checkersBoard.setPadding(new Insets(80, 80, 80, 80));
        drawGrid(pawnArray, checkersBoard);


        Button buttonStart = new Button("Start");
        buttonStart.setMinSize(100, 20);
        Button buttonTeam = new Button("Change Team");
        buttonTeam.setMinSize(100, 20);
        Button buttonSaveGame = new Button("Save");
        buttonSaveGame.setMinSize(100, 20);
        Button buttonLoadGame = new Button("Load");
        buttonLoadGame.setMinSize(100, 20);

        labelTeam.setText("White team turn");
        labelTeam.setMinSize(100,20);
        labelTeam.setStyle("-fx-font: 24 arial;");
        labelBlackScore.setText("Black: " + blackScore);
        labelBlackScore.setStyle("-fx-font: 16 arial;");
        labelWhiteScore.setText("White: " + whiteScore);
        labelWhiteScore.setStyle("-fx-font: 16 arial;");

        GridPane menuGrid = new GridPane();
        menuGrid.getRowConstraints().add(new RowConstraints(40));
        menuGrid.add(buttonStart, 0, 2);
        menuGrid.add(buttonSaveGame, 1, 2);
        menuGrid.add(buttonLoadGame, 2, 2);
        menuGrid.add(labelTeam, 1,0);
        menuGrid.add(labelBlackScore, 0,1);
        menuGrid.add(labelWhiteScore, 2, 1);
        menuGrid.setHgap(20);
        menuGrid.setBackground(new Background(new BackgroundFill(Color.DARKGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        menuGrid.setAlignment(Pos.TOP_CENTER);



        splitPane.getItems().add(menuGrid);
        splitPane.getItems().add(checkersBoard);
        test.add(menuGrid,0,0);
        test.add(checkersBoard, 0,1);

        checkPawns(checkersBoard);


        buttonStart.setOnAction((e) -> {
            System.out.println("Im pressed");
            labelTeam.setText("White team turn");
            player = 0;
            blackScore = 0;
            whiteScore = 0;
            selectedPawn = null;
            pawnArray = setupArray();
            drawGrid(pawnArray, checkersBoard);
            checkPawns(checkersBoard);
            labelBlackScore.setText("Black: " + blackScore);
            labelWhiteScore.setText("White: " + whiteScore);

        });
        buttonTeam.setOnAction((e) -> {

            if (selectedPawn == null) {
                player = (player + 1) % 2;
            }
            checkPawns(checkersBoard);
            drawGrid(pawnArray, checkersBoard);
        });
        buttonLoadGame.setOnAction((e) -> {
            loadGame();
            checkPawns(checkersBoard);
            drawGrid(pawnArray, checkersBoard);
        });
        buttonSaveGame.setOnAction((e) -> {
            saveGame();
            System.out.printf("Game saved");
        });

        //why i need to do -100?
        Scene scene = new Scene(test, backgroundSize.getWidth()-100, backgroundSize.getHeight(), Color.BLACK);

        primaryStage.setTitle("Chekers");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public Pawn[][] setupArray() {
        Pawn[][] output = new Pawn[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (i < 3 && (i + j) % 2 == 1) {
                    output[i][j] = new Pawn(0, i, j);
                } else if (i > 4 && (i + j) % 2 == 1) {
                    output[i][j] = new Pawn(1, i, j);
                } else {
                    output[i][j] = new Pawn(2, i, j);
                }

            }
        }
        return output;
    }

    public void drawGrid(Pawn[][] array, GridPane gridPane) {

        System.out.println("Grid drawing");
        gridPane.getChildren().clear();


        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (array[i][j] != null) {
                    ImageView temp = createPawn(j, i, gridPane, false);
                    gridPane.add(temp, j, i);
                }
            }
        }

    }

    public void selectionLogic(int x, int y, GridPane gridPane) {

        //if (!selectedPawn.isPresent()) {
        Pawn clickedPawn = selectPawn(x, y, gridPane);

        if (selectedPawn == null && clickedPawn.isType() != 2) {
            if(clickedPawn.isMovable()){
                System.out.println("Selecting Pawn!");
                selectedPawn = clickedPawn;
                if(!pawnArray[selectedPawn.getY()][selectedPawn.getX()].equals(selectedPawn)){
                    System.out.println("ERROR WRONG COORDS");
                } else {
                    System.out.println(pawnArray[selectedPawn.getY()][selectedPawn.getX()]);
                }
            }
        } else {

            if (selectedPawn.equals(clickedPawn)) {
                System.out.println("You already selected this Pawn! Deselecting");


                //deselect pawn
                deselectPawn(x, y, gridPane);

                checkPawns(gridPane);
                drawGrid(pawnArray, gridPane);

            } else {
                System.out.println("You already chose a different Pawn!");
            }
        }


    }

    public void moveLogic(int destX, int destY, GridPane gridPane) {

        //To do
        //Only diagonal movement
        //Only pawns that can hit can move
        //Otherwise if none can hit, any can move
        //

        //move selected pawn only diagonally
        //pawn that i select must be able to move
        //able to move = hit enemy pawn if possible


        //bez ruchow do tylu
        //jezeli niemozliwy ruch, gracz przegrywa (czy remis?)
        //damke
        //ranking graczy, procentowy win loss w osobnym pliku

        //rekurencyjne sprawdzenie bicia
        //poziomm trudnosci z sugestiami
        //wybor modelu pionka
        //mozliwosc wgrania wlasnego


        if (selectedPawn != null) {

            int originX = selectedPawn.getX();
            int originY = selectedPawn.getY();
            Pawn destinationPawn = pawnArray[destY][destX];

            int pawnDistance = (Math.abs(originX - destX) + Math.abs(originY - destY));
            boolean hitDone = canHit(selectedPawn, destinationPawn);
            moved = !hitDone;


            boolean diagonalMovement = !(destX == originX) && !(destY == originY);
            boolean movementCheck = diagonalMovement && destinationPawn.isType() == 2;
            boolean hitMove = pawnDistance == 4 && hitDone;
            boolean noHitMove = pawnDistance == 2 && !hitDone;
            //System.out.println("TEST = " + pawnDistance);



            if (pawnArray[destY][destX].isType() != 2) {
                System.out.println("This spot is already occupied!");

            } else if (movementCheck && (hitMove || noHitMove) &&
                    (checkPawnNeighbors(selectedPawn) && hitDone || !checkPawnNeighbors(selectedPawn))) {


                pawnArray[originY][originX] = new Pawn(2, originY, originX);
                selectedPawn.setX(destX);
                selectedPawn.setY(destY);
                pawnArray[destY][destX] = selectedPawn;

                if (checkPawnNeighbors(selectedPawn) && !moved) {
                    System.out.println("You can still move!");
                } else {
                    player = (player + 1) % 2;
                    checkPawns(gridPane);
                    deselectPawn(destX, destY, gridPane);
                    if(player == 0){
                        labelTeam.setText("White team turn");
                    } else {
                        labelTeam.setText("Black team turn");
                    }
                }
                //check only if he had hit a pawn


                System.out.println("Pawn moved!");

                drawGrid(pawnArray, gridPane);
            } else {
                System.out.println("You cant move here");
            }
        }


    }

    public Pawn selectPawn(int x, int y, GridPane gridPane) {

        Pawn output = pawnArray[y][x];
        if (!output.isMovable()){
            return output;
        }
        //I lose event listener when creating selected pawn, i need to fix it
        //fixed
        ImageView temp = createPawn(x, y, gridPane, true);

        gridPane.getChildren().removeAll(output.getImageView());
        gridPane.add(temp, x, y);
        output.setImage(temp);

        System.out.println(output.getX() + " " + output.getY());
        return output;
    }

    public void deselectPawn(int x, int y, GridPane gridPane) {


        //gridPane.getChildren().removeAll(selection);
        //gridPane.getChildren().removeAll(pawnArray[y][x].getImageView());
        gridPane.getChildren().removeAll(selectedPawn.getImageView());

        if (selectedPawn.isType() == 0) {
            selectedPawn.setImage(new ImageView("whitePawn.png"));
        } else {
            selectedPawn.setImage(new ImageView("blackPawn.png"));
        }

        gridPane.add(selectedPawn.getImageView(), x, y);
        selectedPawn = null;
    }

    public ImageView createPawn(int pawnX, int pawnY, GridPane gridPane, boolean selected) {

        ImageView temp;
        if (!selected) {
            temp = pawnArray[pawnY][pawnX].getImageView();
        } else {
            temp = selection;
        }

        temp.setOnMouseClicked(e -> {
            int x = GridPane.getColumnIndex((Node) e.getSource());
            int y = GridPane.getRowIndex((Node) e.getSource());

            if (pawnArray[y][x].isType() == player) {
                System.out.println("YES");
                selectionLogic(x, y, gridPane);
            } else {
                moveLogic(x, y, gridPane);
            }

        });

        return temp;
    }

    public void checkPawns(GridPane gridPane) {


        int countPawns = 0;
        int countMovable = 0;
        for (Pawn[] pawnRow : pawnArray) {
            for (Pawn pawn : pawnRow) {
                if (pawn.isType() == player) {
                    countPawns++;
                if (checkPawnNeighbors(pawn)) {
                        countMovable++;
                        pawn.setMovable(true);
                        pawn.setImage(new ImageView("possiblePawn.png"));
                    }
                } else {
                    pawn.setMovable(false);
                    if(pawn.isType() == 0){
                        pawn.setImage(new ImageView("whitePawn.png"));
                    }
                    if(pawn.isType() == 1){
                        pawn.setImage(new ImageView("blackPawn.png"));
                    }

                }
            }
        }
        if(countPawns == 0){
            if((player+1)%2 == 0){
                System.out.printf("White won");
                whiteScore++;
            } else {
                System.out.println("Black won");
                blackScore++;
            }
            labelBlackScore.setText("Black: " + blackScore);
            labelWhiteScore.setText("White: " + whiteScore);
            labelTeam.setText("White team turn");
            player = 0;

            pawnArray = setupArray();
            checkPawns(gridPane);
            drawGrid(pawnArray, gridPane);

        }
        if(countMovable == 0){
            for (Pawn[] pawnRow : pawnArray) {
                for (Pawn pawn : pawnRow) {
                    if (pawn.isType() == player){
                        pawn.setMovable(true);
                    }
                }
            }
        }

    }

    public boolean checkPawnNeighbors(Pawn pawn) {

        //returns true if it can hit in any direction
        int enemyTeam = (player + 1) % 2;
        int x = pawn.getX();
        int y = pawn.getY();

        if (x < pawnArray.length - 2 && y < pawnArray.length - 2) {
            if (pawnArray[y + 1][x + 1].isType() == enemyTeam && pawnArray[y + 2][x + 2].isType() == 2) {
                //check for empty field

                return true;
            }
        }
        //System.out.println("Cant hit bot right");
        if (x > 1 && y < pawnArray.length - 2) {
            if (pawnArray[y + 1][x - 1].isType() == enemyTeam && pawnArray[y + 2][x - 2].isType() == 2) {
                //check for empty field

                return true;
            }

        }
        //System.out.println("Cant hit bot left");
        if (y > 1 && x < pawnArray.length - 2) {
            if (pawnArray[y - 1][x + 1].isType() == enemyTeam && pawnArray[y - 2][x + 2].isType() == 2) {
                //check for empty field
                return true;
            }
        }
        //System.out.println("Cant hit top right");
        if (x > 1 && y > 1) {
            if (pawnArray[y - 1][x - 1].isType() == enemyTeam && pawnArray[y - 2][x - 2].isType() == 2) {
                //check for empty field
                return true;
            }
        }
        //System.out.println("Cant hit top left");
        return false;
    }

    public boolean canHit(Pawn origin, Pawn destination){

        int enemyTeam = (origin.isType() + 1) % 2;
        int testX = (origin.getX() + destination.getX())/2;
        int testY = (origin.getY() + destination.getY())/2;

        //System.out.println(pawnArray[testY][testX]);
        if(pawnArray[testY][testX].isType() == enemyTeam){
            pawnArray[testY][testX] = new Pawn(2,testY,testX);
            return true;
        }
        return false;
    }

    public void saveGame(){

        if(selectedPawn == null){
            String output = "";
            output += "Player turn: "  +  System.getProperty("line.separator");
            output += player + System.getProperty("line.separator");
            output += "Black score: " + System.getProperty("line.separator");
            output += blackScore + System.getProperty("line.separator");
            output += "White score: " + System.getProperty("line.separator");
            output += whiteScore + System.getProperty("line.separator");
            output += "Checkerboard state: " + System.getProperty("line.separator");
            for (Pawn[] pawnRow : pawnArray){
                for(Pawn pawn : pawnRow){
                    output += pawn.isType();
                }
                output += System.getProperty("line.separator");
            }
            try {
                PrintWriter out = new PrintWriter("gameSave.txt");
                out.write(output);
                out.close();
            } catch (Exception e) {
                System.out.println("File not saved " + e);
            }
        } else {
            System.out.println("Finish move or deselect pawn");
        }
    }

    public void loadGame(){
        try {
            String[] lines;
            Charset charset = Charset.forName("UTF-8");
            lines = Files.readAllLines(Paths.get("gameSave.txt"), charset).toArray(new String[0]);

            player = Integer.parseInt(lines[1]);
            if(player == 0){
                labelTeam.setText("White team turn");
            } else {
                labelTeam.setText("Black team turn");
            }
            blackScore = Integer.parseInt(lines[3]);
            whiteScore = Integer.parseInt(lines[5]);
            labelBlackScore.setText("Black: " + blackScore);
            labelWhiteScore.setText("White: " + whiteScore);
            selectedPawn = null;

            for(int i = 0; i < pawnArray[0].length; i++){
                char[] pawnRow = lines[i+7].toCharArray();
                for (int j = 0; j < pawnArray.length; j++){
                    int pawnType = Character.getNumericValue(pawnRow[j]);
                    pawnArray[i][j] = new Pawn(pawnType, i, j);
                }
            }
        } catch (Exception e) {
            System.out.println("Save file corrupted!" + e);
        }

    }
}
