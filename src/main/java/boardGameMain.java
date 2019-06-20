
import elements.Pawn;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
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
    private int x = (int)imageback.getWidth();
    private int y = (int)imageback.getHeight();
    Pawn[][] pawnArray = setupArray();
    Pawn selectedPawn = null;
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
        splitPane.isResizable();


        GridPane gridPane = setupGrid(pawnArray,background);



        Button buttonStart = new Button("Start");
        buttonStart.setMinSize(100,100);
        Button buttonTeam = new Button("Change Team");
        buttonTeam.setMinSize(100,100);

        menuGrid.add(buttonStart,0,0);
        menuGrid.add(buttonTeam,1,0);
        menuGrid.setHgap(20);
        menuGrid.setBackground(new Background(new BackgroundFill(Color.DARKGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        menuGrid.setAlignment(Pos.TOP_CENTER);


        splitPane.getItems().add(menuGrid);
        splitPane.getItems().add(gridPane);





        gridPane.addEventHandler(MouseEvent.MOUSE_CLICKED, e ->{

            //get cursor x and y
            //i use pawn pixel size since one pawn is one field
            //this makes getting coordinates easier
            int x = (int)((e.getX())/blackPawn.getHeight()) -1;
            int y = (int)((e.getY())/blackPawn.getHeight()) -1;
            //I check if player didnt click out of bounds
            if(isCursorInBounds(x,y)) {
                System.out.println("Col: " + x + " Row: " +  y);
                //print the data of field im pointing at
                //System.out.println(pawnArray[y][x].toString());

                //If pawn exists and is on players team go on
                //if no pawns are selected then check
                //if pawn is not selected, select it
                //if it is selected

                //Pionek jest tej samej druzyny co gracz
                //Sprawdzmy czy juz jest wybrany
                if (pawnArray[y][x].isType() == player) {

                    //Select Pawn
                    //Remove old icon of pawn
                    //And put seletion icon
                    if (selectedPawn == null) {
                        System.out.println("Selecting Pawn!");
                        selectedPawn = pawnArray[y][x];
                        gridPane.getChildren().removeAll(selectedPawn.getImageView());
                        gridPane.add(selection, x, y);
                        //if we got a selected pawn
                    } else {
                        //If we already have selected a pawn
                        //And the pawn we select now is the same
                        //We deselect our pawn
                        if (selectedPawn.equals(pawnArray[y][x])) {
                            System.out.println("You already selected this Pawn! Deselecting");
                            gridPane.getChildren().removeAll(selection);
                            gridPane.add(selectedPawn.getImageView(), x, y);
                            selectedPawn = null;
                        } else {
                            System.out.println("You already chose a different Pawn!");
                        }
                    }


                    //if we have a selected pawn
                    //we let him move to an empty field
                    //when i move pawn i have to delete emtpy field
                    //i have to also delete my pawn
                    //then replace emtpy field with new pawn
                    //then replace pawn field with emtpy field
                } else if (selectedPawn != null && pawnArray[y][x].isType() == 2) {
                    System.out.println("You want to move here?");
                    //System.out.println("Selected: " + selectedPawn);
                    //System.out.println("Destination: " + pawnArray[y][x]);
                    int x_temp = selectedPawn.getX();
                    int y_temp = selectedPawn.getY();
                    if ((y + x) % 2 == 1 && pawnArray[y][x].isType() == 2) {
                        //I replace origin field with new empty pawn
                        //Then i create new Pawn of the type i moved
                        pawnArray[y_temp][x_temp] = new Pawn(2, y_temp, x_temp);
                        pawnArray[y][x] = new Pawn(selectedPawn.isType(), y, x);


                        //this works
                        //When i move pawn
                        //I remove selection highlight
                        //I remove pawn image from origin field
                        //I put that pawn image in destination field
                        gridPane.getChildren().removeAll(selection);
                        gridPane.getChildren().removeAll(selectedPawn.getImageView());
                        gridPane.add(pawnArray[y][x].getImageView(), x, y);


                        //gridPane updates now i need to update array
                        pawnArray[y_temp][x_temp] = new Pawn(2, y_temp, x_temp);
                        //System.out.println(pawnArray[y_temp][x_temp]);
                        System.out.println("Pawn moved!");
                        selectedPawn = null;
                    } else {
                        System.out.println("You cant move here");
                    }


                }
            }
        });

        buttonStart.setOnAction((e) -> {
            System.out.println("Im pressed");
            pawnArray = setupArray();
            gridPane.getChildren().clear();
            for(int i = 0; i < 8; i++){
                for(int j = 0; j < 8; j++){
                    if(pawnArray[i][j] != null){
                        gridPane.add(pawnArray[i][j].getImageView(),j,i);
                    }
                }
            }
            selectedPawn = null;
        });
        buttonTeam.setOnAction((e) -> {
            if(selectedPawn == null){
                if(player == 0)
                    player = 1;
                else
                    player = 0;
            }
        });

        //row and column constraint
        //style pola
        //why i need to do -100?
        Scene scene = new Scene(splitPane, imageback.getWidth()-100, imageback.getHeight(), Color.BLACK);

        primaryStage.setTitle("Chekers");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public Pawn[][] setupArray(){
        Pawn[][] output = new Pawn[8][8];
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                if(i < 3 && (i+j) % 2 == 1) {
                    output[i][j] = new Pawn(0, i, j);
                } else if(i > 4 && (i+j) % 2 == 1){
                    output[i][j] = new Pawn(1,i,j);
                } else {
                    output[i][j] = new Pawn(2,i,j);
                }

            }
        }
        return output;
    }
    public GridPane setupGrid(Pawn[][] array, Background background){
        GridPane output = new GridPane();
        output.setAlignment(Pos.CENTER);
        ColumnConstraints column = new ColumnConstraints(blackPawn.getWidth());
        RowConstraints row = new RowConstraints(blackPawn.getHeight());
        output.setGridLinesVisible(true);
        output.setBackground(background);
        output.getColumnConstraints().add(column);
        output.getRowConstraints().add(row);
        output.addRow(4);
        output.setPadding(new Insets(80, 80, 80, 80));



        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                if(array[i][j] != null){
                    output.add(array[i][j].getImageView(),j,i);
                }
            }
        }
        return output;
    }
    boolean isCursorInBounds(int x, int y){
       return (x >= 0 && x < pawnArray.length && y >= 0 && y < pawnArray.length);
    }
    Pawn selectPawn(Pawn[][] pawnArray, int x, int y, GridPane gridPane){
        Pawn output = pawnArray[y][x];
        gridPane.getChildren().removeAll(selectedPawn.getImageView());
        gridPane.add(selection, x, y);
        return output;
    }

}
