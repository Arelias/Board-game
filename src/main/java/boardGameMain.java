
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




        //splitPane.setStyle("-fx-width:800px");
        SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.VERTICAL);

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


        GridPane menuGrid = new GridPane();
        menuGrid.add(buttonStart, 0, 0);
        menuGrid.add(buttonTeam, 1, 0);
        menuGrid.setHgap(20);
        menuGrid.setBackground(new Background(new BackgroundFill(Color.DARKGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        menuGrid.setAlignment(Pos.TOP_CENTER);


        splitPane.getItems().add(menuGrid);
        splitPane.getItems().add(checkersBoard);




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

    public void drawGrid(Pawn[][] array, GridPane gridPane) {

        System.out.println("Grid drawing");
        gridPane.getChildren().clear();


        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (array[i][j] != null) {
                    ImageView temp = createPawn(pawnArray, j, i, gridPane, false);
                    gridPane.add(temp, j, i);
                }
            }
        }

    }

    public Pawn selectPawn(int x, int y, GridPane gridPane) {

        Pawn output = pawnArray[y][x];

        //I lose event listener when creating selected pawn, i need to fix it
        //fixed
        ImageView temp = createPawn(pawnArray,x,y,gridPane,true);

        gridPane.getChildren().removeAll(output.getImageView());
        gridPane.add(temp, x, y);
        output.setImage(temp);

        return output;
    }

    public void selectionLogic(int x, int y, GridPane gridPane) {

        //if (!selectedPawn.isPresent()) {
        if(selectedPawn == null && selectPawn(x, y, gridPane).isType() != 2){
            System.out.println("Selecting Pawn!");
            selectedPawn = selectPawn(x, y, gridPane);

        } else {

            if (selectedPawn.equals(pawnArray[y][x])) {
                System.out.println("You already selected this Pawn! Deselecting");


                //deselect pawn
                deselectPawn(x, y, gridPane);

                drawGrid(pawnArray,gridPane);
            } else {
                System.out.println("You already chose a different Pawn!");
            }
        }
    }

    public void moveLogic(Pawn[][] pawnArray, int pawn_x, int pawn_y, GridPane gridPane) {

        if (selectedPawn != null) {

            int origin_x = selectedPawn.getX();
            int origin_y = selectedPawn.getY();

            if (pawnArray[pawn_y][pawn_x].isType() != 2) {
                System.out.println("This spot is already occupied!");
            } else if (pawnArray[pawn_y][pawn_x].isType() == 2) {
                if ((pawn_y + pawn_x) % 2 == 1 && pawnArray[pawn_y][pawn_x].isType() == 2) {

                    //I replace origin field with new empty pawn
                    //Then i create new Pawn of the type i moved
                    pawnArray[origin_y][origin_x] = new Pawn(2, origin_y, origin_x);
                    selectedPawn.setX(pawn_x);
                    selectedPawn.setY(pawn_y);
                    pawnArray[pawn_y][pawn_x] = selectedPawn;

                    //jesli ruch dalej mozliwy zostaw, jesli nie zrob deselect
                    deselectPawn(pawn_x, pawn_y, gridPane);

                    System.out.println("Pawn moved!");
                    drawGrid(pawnArray, gridPane);

                } else {
                    System.out.println("You cant move here");
                }

            }
        }


    }
    public void deselectPawn(int x, int y, GridPane gridPane){


        //gridPane.getChildren().removeAll(selection);
        //gridPane.getChildren().removeAll(pawnArray[y][x].getImageView());
        gridPane.getChildren().removeAll(selectedPawn.getImageView());

        if(selectedPawn.isType() == 0){
            selectedPawn.setImage(new ImageView("whitePawn.png"));
        } else {
            selectedPawn.setImage(new ImageView("blackPawn.png"));
        }

        gridPane.add(selectedPawn.getImageView(), x, y);
        selectedPawn = null;
    }
    public ImageView createPawn(Pawn[][] pawnArray, int pawn_x, int pawn_y, GridPane gridPane, boolean selected){

        ImageView temp;
        if(!selected){
            temp = pawnArray[pawn_y][pawn_x].getImageView();
        } else {
            temp = selection;
        }



        temp.setOnMouseClicked(e -> {
            int x = GridPane.getColumnIndex((Node)e.getSource());
            int y = GridPane.getRowIndex((Node)e.getSource());

            if (pawnArray[y][x].isType() == player) {
                System.out.println("YES");
                selectionLogic(x, y, gridPane);
            } else {
                moveLogic(pawnArray,x, y, gridPane);
            }

        });

        return temp;
    }
}
