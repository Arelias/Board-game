
import elements.Pawn;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


public class boardGameMain extends Application {

    private Image imageback = new Image("background.jpg");
    private Image blackPawn = new Image("blackPawn.png");
    private Image whitePawn = new Image("whitePawn.png");
    private Image emptyPawn = new Image("emptyPawn.png");
    private ImageView selection = new ImageView(new Image("selectedPawn.png"));
    private int x = (int) imageback.getWidth();
    private int y = (int) imageback.getHeight();
    private Pawn[][] pawnArray = setupArray();
    //private Optional<Pawn> selectedPawn = Optional.empty();
    private Pawn selectedPawn = null;
    int player = 0;


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


        GridPane menuGrid = new GridPane();


        SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.VERTICAL);
        //splitPane.setStyle("-fx-width:800px");
        splitPane.isResizable();

        GridPane checkersBoard = new GridPane();
        checkersBoard.setAlignment(Pos.CENTER);
        ColumnConstraints column = new ColumnConstraints(blackPawn.getWidth());
        RowConstraints row = new RowConstraints(blackPawn.getHeight());
        checkersBoard.setGridLinesVisible(true);
        checkersBoard.setBackground(background);
        checkersBoard.getColumnConstraints().add(column);
        checkersBoard.getRowConstraints().add(row);
        checkersBoard.setPadding(new Insets(80, 80, 80, 80));
        drawGrid(pawnArray, checkersBoard);


        Button buttonStart = new Button("Start");
        buttonStart.setMinSize(100, 100);
        Button buttonTeam = new Button("Change Team");
        buttonTeam.setMinSize(100, 100);

        menuGrid.add(buttonStart, 0, 0);
        menuGrid.add(buttonTeam, 1, 0);
        menuGrid.setHgap(20);
        menuGrid.setBackground(new Background(new BackgroundFill(Color.DARKGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        menuGrid.setAlignment(Pos.TOP_CENTER);


        splitPane.getItems().add(menuGrid);
        splitPane.getItems().add(checkersBoard);


        checkersBoard.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {


            //get cursor x and y
            //i use pawn pixel size since one pawn is one field
            //this makes getting coordinates easier
            int x = (int) ((e.getX()) / blackPawn.getHeight()) - 1;
            int y = (int) ((e.getY()) / blackPawn.getHeight()) - 1;
            //I check if player didnt click out of bounds
            if (isCursorInBounds(x, y)) {
                System.out.println("Col: " + x + " Row: " + y);


                //If player clicks on his own pawn he can only select it
                //Otherwise he has to move to an empty cell

            }
        });

        buttonStart.setOnAction((e) -> {
            System.out.println("Im pressed");
            //pawnArray = setupArray();
            drawGrid(pawnArray, checkersBoard);

            selectedPawn = null;
        });
        buttonTeam.setOnAction((e) -> {
            if (selectedPawn == null) {
                player = (player + 1)%2;
            }
        });

        //why i need to do -100?
        Scene scene = new Scene(splitPane, imageback.getWidth()-100, imageback.getHeight(), Color.BLACK);

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

    public void drawGrid(Pawn[][] array, GridPane output) {

        output.getChildren().clear();


        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (array[i][j] != null) {
                    ImageView temp = array[i][j].getImageView();


                    temp.setOnMouseClicked(e -> {

                        System.out.println("Clicked!" + e);
                        int x = GridPane.getColumnIndex((Node)e.getSource());
                        int y = GridPane.getRowIndex((Node)e.getSource());
                        System.out.println("Event Col: " + x + " Row: " + y);
                        //i can use methods here
                        //modify only with methods

                        if (pawnArray[y][x].isType() == player) {
                            selectionLogic(x, y, output);
                        } else {
                            moveLogic(pawnArray,x, y, output);
                        }
                        if (pawnArray[y][x].isType() == player){
                            System.out.println("Your team");
                        } else {
                            System.out.println("Wrong team");
                        }

                    });
                    output.add(temp, j, i);
                }
            }
        }

    }

    boolean isCursorInBounds(int x, int y) {
        return (x >= 0 && x < pawnArray.length && y >= 0 && y < pawnArray.length);
    }

    public Pawn selectPawn(int x, int y, GridPane gridPane) {

        Pawn output = pawnArray[y][x];
        gridPane.getChildren().removeAll(output.getImageView());
        gridPane.add(selection, x, y);
        return output;
    }

    public void selectionLogic(int x, int y, GridPane gridPane) {

        //if (!selectedPawn.isPresent()) {
        if(selectedPawn == null){
            System.out.println("Selecting Pawn!");
            selectedPawn = selectPawn(x, y, gridPane);

        } else {

            if (selectedPawn.equals(pawnArray[y][x])) {
                System.out.println("You already selected this Pawn! Deselecting");


                gridPane.getChildren().removeAll(selection);
                gridPane.add(selectedPawn.getImageView(), x, y);
                selectedPawn = null;
            } else {
                System.out.println("You already chose a different Pawn!");
            }
        }
    }

    public void moveLogic(Pawn[][] pawnArray, int cursor_x, int cursor_y, GridPane gridPane) {

        if (selectedPawn != null) {

            int origin_x = selectedPawn.getX();
            int origin_y = selectedPawn.getY();

            if (pawnArray[cursor_y][cursor_x].isType() != 2) {
                System.out.println("This spot is already occupied!");
            } else if (pawnArray[cursor_y][cursor_x].isType() == 2) {
                System.out.println("You want to move here?");

                if ((cursor_y + cursor_x) % 2 == 1 && pawnArray[cursor_y][cursor_x].isType() == 2) {

                    //I replace origin field with new empty pawn
                    //Then i create new Pawn of the type i moved
                    pawnArray[origin_y][origin_x] = new Pawn(2, origin_y, origin_x);
                    selectedPawn.setX(cursor_x);
                    selectedPawn.setY(cursor_y);
                    pawnArray[cursor_y][cursor_x] = selectedPawn;


                    gridPane.getChildren().removeAll(selection);
                    gridPane.getChildren().removeAll(selectedPawn.getImageView());
                    gridPane.add(pawnArray[cursor_y][cursor_x].getImageView(), cursor_x, cursor_y);
                    selectedPawn = null;


                    System.out.println("Pawn moved!");

                } else {
                    System.out.println("You cant move here");
                }
            }
            //System.out.println("Origin: " + pawnArray[origin_y][origin_x]);
            //System.out.println("Destination: " + pawnArray[cursor_y][cursor_x]);
        }

    }

}
