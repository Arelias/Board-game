
import elements.Board;
import elements.Pawn;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;


public class BoardGameMain extends Application {

    private Image imageback = new Image("background.jpg");
    //private Optional<Pawn> selectedPawn = Optional.empty();
    private Pawn selectedPawn = null;

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


        Board board = new Board();


        Button buttonStart = new Button("Start");
        buttonStart.setMinSize(100, 20);
        Button buttonDifficulty = new Button("Set to easy");
        buttonDifficulty.setMinSize(100, 20);
        Button buttonTeam = new Button("Change Team");
        buttonTeam.setMinSize(100, 20);
        Button buttonSaveGame = new Button("Save");
        buttonSaveGame.setMinSize(100, 20);
        Button buttonLoadGame = new Button("Load");
        buttonLoadGame.setMinSize(100, 20);



        GridPane menuGrid = new GridPane();
        menuGrid.getRowConstraints().add(new RowConstraints(40));
        menuGrid.add(buttonStart, 0, 2);
        menuGrid.add(buttonSaveGame, 1, 2);
        menuGrid.add(buttonLoadGame, 2, 2);
        menuGrid.add(buttonDifficulty, 3, 2);
        menuGrid.add(board.getLabelTeam(), 1,0);
        menuGrid.add(board.getLabelBlackScore(), 0,1);
        menuGrid.add(board.getLabelWhiteScore(), 2, 1);
        menuGrid.setHgap(20);
        menuGrid.setBackground(new Background(new BackgroundFill(Color.DARKGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        menuGrid.setAlignment(Pos.TOP_CENTER);



        splitPane.getItems().add(menuGrid);
        splitPane.getItems().add(board.getGridPane());
        test.add(menuGrid,0,0);
        test.add(board.getGridPane(), 0,1);



        buttonStart.setOnAction((e) -> {
            board.startGame();
        });
        buttonDifficulty.setOnAction((e) -> {
            if(board.isEasyMode()){
                buttonDifficulty.setText("Set to easy");
                board.setEasyMode(false);
                board.resetPawnImages();
            } else {
                buttonDifficulty.setText("Set to normal");
                board.setEasyMode(true);
                board.checkPawns();
            }
            board.drawGrid();
        });
        buttonTeam.setOnAction((e) -> {

            if (selectedPawn == null) {
                board.setPlayer((board.getPlayer() + 1) % 2);
            }
            board.checkPawns();
            board.drawGrid();
        });
        buttonLoadGame.setOnAction((e) -> {
            loadGame(board);
            board.checkPawns();
            board.drawGrid();
        });
        buttonSaveGame.setOnAction((e) -> {
            saveGame(board);
            System.out.printf("Game saved");
        });

        //why i need to do -100?
        Scene scene = new Scene(test, backgroundSize.getWidth()-100, backgroundSize.getHeight(), Color.BLACK);

        primaryStage.setTitle("Chekers");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public void saveGame(Board board){

        if(selectedPawn == null){
            String output = "";
            output += "Player turn: "  +  System.getProperty("line.separator");
            output += board.getPlayer() + System.getProperty("line.separator");
            output += "Black score: " + System.getProperty("line.separator");
            output += board.getBlackScore() + System.getProperty("line.separator");
            output += "White score: " + System.getProperty("line.separator");
            output += board.getWhiteScore() + System.getProperty("line.separator");
            output += "Checkerboard state: " + System.getProperty("line.separator");
            for (Pawn[] pawnRow : board.getPawnArray()){
                for(Pawn pawn : pawnRow){
                    if(pawn.isKing()){
                        output += (pawn.isType() + 3);
                    } else {
                        output += pawn.isType();
                    }
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

    public void loadGame(Board board){

        try {
            String[] lines;
            Charset charset = Charset.forName("UTF-8");
            lines = Files.readAllLines(Paths.get("gameSave.txt"), charset).toArray(new String[0]);


            board.setPlayer(Integer.parseInt(lines[1]));
            if(board.getPlayer() == 0){
                board.getLabelTeam().setText("White team turn");
            } else {
                board.getLabelTeam().setText("Black team turn");
            }
            board.setBlackScore(Integer.parseInt(lines[3]));
            board.setWhiteScore(Integer.parseInt(lines[5]));
            board.getLabelBlackScore().setText("Black: " + board.getBlackScore());
            board.getLabelWhiteScore().setText("White: " + board.getWhiteScore());
            board.setSelectedPawn(null);

            Pawn[][] temp = board.getPawnArray();
            for(int i = 0; i < board.getPawnArray()[0].length; i++){
                char[] pawnRow = lines[i+7].toCharArray();
                for (int j = 0; j < board.getPawnArray().length; j++){
                    int pawnType = Character.getNumericValue(pawnRow[j]);
                    if(pawnType > 2){
                        pawnType = pawnType - 3;
                        temp[i][j] = new Pawn(pawnType, i, j);
                        temp[i][j].setKing(true);
                    } else {
                        temp[i][j] = new Pawn(pawnType, i, j);
                    }
                    board.setUpImage(temp[i][j]);
                }
            }
            board.setPawnArray(temp);
        } catch (Exception e) {
            System.out.println("Save file corrupted!" + e);
        }

    }
}
