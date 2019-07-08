package elements;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;





public class Board {

    private static Image imageback = new Image("background.jpg");
    private static BackgroundSize backgroundSize = new BackgroundSize(imageback.getWidth(), imageback.getHeight(), true, true, true, false);
    private static BackgroundImage backgroundImage = new BackgroundImage(imageback, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
    private static Background background = new Background(backgroundImage);

    private static ImageView selection = new ImageView(new Image("selectedPawn.png"));
    private static Image blockedPawn = new Image("blockedPawn.png");
    private static Image blackPawn = new Image("blackPawn.png");

    Label labelTeam = new Label();
    Label labelBlackScore = new Label();
    Label labelWhiteScore = new Label();
    int whiteScore = 0;
    int blackScore = 0;

    private static Pawn[][] pawnArray;
    private static GridPane gridPane;

    int player = 0;
    boolean moved = true;
    private static Pawn selectedPawn = null;


    public void startGame() {
        player = 0;
        this.labelTeam.setText("White team turn");
        this.whiteScore = 0;
        this.blackScore = 0;
        this.labelBlackScore.setText("Black: " + this.blackScore);
        this.labelWhiteScore.setText("Black: " + this.whiteScore);
        selectedPawn = null;
        pawnArray = setupArray();
        drawGrid();
        checkPawns();

    }

    public Board() {
        setUpSelectionImage();
        pawnArray = setupArray();
        gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setGridLinesVisible(true);
        gridPane.setBackground(background);
        gridPane.getColumnConstraints().add(new ColumnConstraints(blackPawn.getWidth()));
        gridPane.getRowConstraints().add(new RowConstraints(blackPawn.getHeight()));
        gridPane.setPadding(new Insets(80, 80, 80, 80));
        checkPawns();
        drawGrid();


        labelTeam.setText("White team turn");
        labelTeam.setMinSize(100, 20);
        labelTeam.setStyle("-fx-font: 24 arial;");
        labelBlackScore.setText("Black: " + blackScore);
        labelBlackScore.setStyle("-fx-font: 16 arial;");
        labelWhiteScore.setText("White: " + whiteScore);
        labelWhiteScore.setStyle("-fx-font: 16 arial;");


    }


    public void setUpSelectionImage() {

        selection.setOnMouseClicked(e -> {
            int x = GridPane.getColumnIndex((Node) e.getSource());
            int y = GridPane.getRowIndex((Node) e.getSource());

            selectionLogic(x, y);
        });
    }

    public void setUpImage(Pawn pawn) {

        if (pawn.getType() == 0) {
            pawn.setImage(new ImageView(new Image("whitePawn.png")));
        }
        if (pawn.getType() == 1) {
            pawn.setImage(new ImageView(new Image("blackPawn.png")));
        }
        if (pawn.getType() == 2) {
            pawn.setImage(new ImageView(new Image("emptyPawn.png")));
            pawn.getImage().setOpacity(0);
        }


        pawn.getImage().setOnMouseClicked(e -> {

            int x = GridPane.getColumnIndex((Node) e.getSource());
            int y = GridPane.getRowIndex((Node) e.getSource());

            //selection logic
            if (selectedPawn == null) {
                selectionLogic(x, y);
            } else {
                moveLogic(x, y);
            }
        });


    }

    public void setSelectedPawn(Pawn selectedPawn) {
        this.selectedPawn = selectedPawn;
    }

    public int getWhiteScore() {
        return whiteScore;
    }

    public void setWhiteScore(int whiteScore) {
        this.whiteScore = whiteScore;
    }

    public int getBlackScore() {
        return blackScore;
    }

    public void setBlackScore(int blackScore) {
        this.blackScore = blackScore;
    }

    public Label getLabelTeam() {
        return labelTeam;
    }

    public Label getLabelBlackScore() {
        return labelBlackScore;
    }

    public Label getLabelWhiteScore() {
        return labelWhiteScore;
    }

    public GridPane getGridPane() {
        return gridPane;
    }

    public void setPlayer(int player) {
        this.player = player;
    }

    public Pawn[][] getPawnArray() {
        return pawnArray;
    }

    public void setPawnArray(Pawn[][] pawnArray) {
        this.pawnArray = pawnArray;
    }

    public int getPlayer() {
        return player;
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
                setUpImage(output[i][j]);
            }
        }
        return output;
    }

    public void drawGrid() {

        //System.out.println("Grid drawing");
        gridPane.getChildren().clear();


        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (this.pawnArray[i][j] != null) {
                    gridPane.add(pawnArray[i][j].getImage(), j, i);
                }
            }
        }

    }

    public void selectionLogic(int x, int y) {

        Pawn clickedPawn = pawnArray[y][x];


        if (clickedPawn.isType() != 2 && clickedPawn.isMovable() && selectedPawn == null) {

            System.out.println("Selecting Pawn!");
            clickedPawn = selectPawn(x, y);
            selectedPawn = clickedPawn;

        } else {
            if (selectedPawn != null) {
                if (selectedPawn.equals(clickedPawn)) {
                    System.out.println("You already selected this Pawn! Deselecting");

                    //deselect pawn
                    deselectPawn(x, y);
                    //checkPawns();
                    drawGrid();

                } else {
                    System.out.println("You already chose a different Pawn!");
                }
            }
        }
    }

    public Pawn selectPawn(int x, int y) {

        Pawn output = this.pawnArray[y][x];

        if (output.isMovable() == true) {
            output.setImage(selection);
            drawGrid();
        }

        return output;
    }

    public void deselectPawn(int x, int y) {

        Pawn output = pawnArray[y][x];
        setUpImage(output);
        if (selectedPawn.getType() == player) {
            selectedPawn.getImage().setImage(new Image("possiblePawn.png"));
        }
        selectedPawn = null;
        drawGrid();
    }

    public boolean victoryCheck() {
        int countMovable = 0;
        int countPawns = 0;

        for (Pawn[] pawnRow : pawnArray) {
            for (Pawn pawn : pawnRow) {
                if (pawn.isType() == player) {
                    if (canPawnMove(pawn)) {
                        countMovable++;
                    }
                    countPawns++;
                }
            }
        }

        if (countPawns == 0 || countMovable == 0) {
            if ((player + 1) % 2 == 0) {
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
            checkPawns();
            drawGrid();
            return true;
        }
        return false;
    }

    public boolean canPawnMove(Pawn pawn) {

        int enemyTeam = (player + 1) % 2;
        int x = pawn.getX();
        int y = pawn.getY();

        boolean topLeft = false;
        boolean topRight = false;
        boolean botLeft = false;
        boolean botRight = false;


        if (x <= pawnArray.length - 2 && y <= pawnArray.length - 2) {
            botRight = pawnArray[y + 1][x + 1].isType() == 2;

            if (x < pawnArray.length - 2 && y < pawnArray.length - 2) {
                botRight = botRight || (pawnArray[y + 1][x + 1].isType() == enemyTeam && pawnArray[y + 2][x + 2].isType() == 2);
            }
        }


        if (x >= 1 && y <= pawnArray.length - 2) {
            botLeft = pawnArray[y + 1][x - 1].isType() == 2;

            if (x > 1 && y < pawnArray.length - 2) {
                botLeft = botLeft || (pawnArray[y + 1][x - 1].isType() == enemyTeam && pawnArray[y + 2][x - 2].isType() == 2);
            }

        }

        if (y >= 1 && x <= pawnArray.length - 2) {
            topRight = pawnArray[y - 1][x + 1].isType() == 2;

            if (y > 1 && x < pawnArray.length - 2) {
                topRight = topRight || (pawnArray[y - 1][x + 1].isType() == enemyTeam && pawnArray[y - 2][x + 2].isType() == 2);
            }

        }

        if (x >= 1 && y >= 1) {
            topLeft = pawnArray[y - 1][x - 1].isType() == 2;

            if (x > 1 && y > 1) {
                topLeft = topLeft || (pawnArray[y - 1][x - 1].isType() == enemyTeam && pawnArray[y - 2][x - 2].isType() == 2);
            }
        }

        if (pawn.isKing()) {

            if (botLeft || botRight || topLeft || topRight) {
                return true;
            }

        } else {

            if (enemyTeam == 1 && (botLeft || botRight)) {
                return true;
            }
            if (enemyTeam == 0 && (topLeft || topRight)) {
                return true;
            }
        }

        //System.out.println("Pawn blocked, X: " + pawn.getX() + ", Y: " + pawn.getY() + ", length: " + pawnArray.length);
        pawn.getImage().setImage(blockedPawn);
        return false;
    }

    public void checkKing(Pawn pawn) {
        if (player == 0 && pawn.getY() == 7 && pawn.isKing() == false) {
            pawn.setKing(true);
        }
        if (player == 1 && pawn.getY() == 0 && pawn.isKing() == false) {
            pawn.setKing(true);
        }
    }

    public void checkPawns() {

        int countHitting = 0;

        if (victoryCheck() == false) {
            //Check pawns that can hit
            for (Pawn[] pawnRow : this.pawnArray) {
                for (Pawn pawn : pawnRow) {
                    if (pawn.isType() == this.player) {
                        checkKing(pawn);
                        if (canPawnHit(pawn)) {
                            countHitting++;
                            pawn.setMovable(true);
                            setUpImage(pawn);
                            pawn.getImage().setImage(new Image("possiblePawn.png"));
                        }
                    } else {
                        pawn.setMovable(false);
                        setUpImage(pawn);
                    }
                }
            }
        }

        if (countHitting == 0) {
            for (Pawn[] pawnRow : pawnArray) {
                for (Pawn pawn : pawnRow) {
                    if (pawn.isType() == player) {
                        if (canPawnMove(pawn)) {
                            pawn.setMovable(true);
                            pawn.getImage().setImage(new Image("possiblePawn.png"));
                        }
                    }
                }
            }
        }
    }

    public boolean canPawnHit(Pawn pawn) {


        int enemyTeam = (player + 1) % 2;
        int x = pawn.getX();
        int y = pawn.getY();
        int checkingX = 0;
        int checkingY = 0;
        int counter;

        if (pawn.isKing()) {
            checkingX = pawn.getX() - 1;
            checkingY = pawn.getY() - 1;
            counter = 0;
            while (checkingX > 1 && checkingY > 1 && counter < 1) {
                //if i meet a
                if (pawnArray[checkingY][checkingX].isType() == enemyTeam || pawnArray[checkingY][checkingX].isType() == pawn.isType()) {
                    counter++;
                }
                if (pawnArray[checkingY][checkingX].isType() == enemyTeam && pawnArray[checkingY - 1][checkingX - 1].isType() == 2) {
                    return true;
                }
                //left and up
                checkingX--;
                checkingY--;
            }


            checkingX = pawn.getX() - 1;
            checkingY = pawn.getY() + 1;
            counter = 0;
            while (checkingX > 1 && checkingY < pawnArray.length - 2 && counter < 1) {
                if (pawnArray[checkingY][checkingX].isType() == enemyTeam || pawnArray[checkingY][checkingX].isType() == pawn.isType()) {
                    counter++;
                }
                if (pawnArray[checkingY][checkingX].isType() == enemyTeam && pawnArray[checkingY + 1][checkingX - 1].isType() == 2) {
                    return true;
                }
                //left and down
                checkingX--;
                checkingY++;
            }


            checkingX = pawn.getX() + 1;
            checkingY = pawn.getY() - 1;
            counter = 0;
            while (checkingX < pawnArray.length - 2 && checkingY > 1 && counter < 1) {
                if (pawnArray[checkingY][checkingX].isType() == enemyTeam || pawnArray[checkingY][checkingX].isType() == pawn.isType()) {
                    counter++;
                }
                if (pawnArray[checkingY][checkingX].isType() == enemyTeam && pawnArray[checkingY - 1][checkingX + 1].isType() == 2) {
                    return true;
                }
                //right and up
                checkingX++;
                checkingY--;
            }


            checkingX = pawn.getX() + 1;
            checkingY = pawn.getY() + 1;
            counter = 0;
            while (checkingX < pawnArray.length - 2 && checkingY < pawnArray.length - 2 && counter < 1) {
                if (pawnArray[checkingY][checkingX].isType() == enemyTeam || pawnArray[checkingY][checkingX].isType() == pawn.isType()) {
                    counter++;
                }
                if (pawnArray[checkingY][checkingX].isType() == enemyTeam && pawnArray[checkingY + 1][checkingX + 1].isType() == 2) {
                    return true;
                }
                //if right and down
                checkingX++;
                checkingY++;
            }

        } else {
            //Checks bot right hit
            if (x < pawnArray.length - 2 && y < pawnArray.length - 2) {
                if (pawnArray[y + 1][x + 1].isType() == enemyTeam && pawnArray[y + 2][x + 2].isType() == 2) {
                    return true;
                }
            }
            //Checks bot left
            if (x > 1 && y < pawnArray.length - 2) {
                if (pawnArray[y + 1][x - 1].isType() == enemyTeam && pawnArray[y + 2][x - 2].isType() == 2) {
                    //check for empty field

                    return true;
                }

            }
            //System.out.println("Cant hit bot left");
            if (x < pawnArray.length - 2 && y > 1) {
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
        }

        return false;
    }

    public void moveLogic(int destX, int destY) {

        //ranking graczy, procentowy win loss w osobnym pliku

        //rekurencyjne sprawdzenie bicia
        //poziomm trudnosci z sugestiami
        //wybor modelu pionka
        //mozliwosc wgrania wlasnego

        int originX = selectedPawn.getX();
        int originY = selectedPawn.getY();
        Pawn destinationPawn = pawnArray[destY][destX];

        this.moved = !canPawnHit(selectedPawn);

        boolean diagonalMovement = Math.abs(destX - originX) == Math.abs(destY - originY);
        boolean destinationCheck = destinationPawn.isType() == 2;
        boolean forwardMove = ((player == 0 && originY < destY) || (player == 1 && originY > destY) || selectedPawn.isKing()) && diagonalMovement && canPawnMove(selectedPawn);

        int pawnDistance = (Math.abs(originX - destX) + Math.abs(originY - destY));
        boolean hitMove = (pawnDistance == 4 || selectedPawn.isKing()) && canPawnHit(selectedPawn);
        boolean noHitMove = (pawnDistance == 2 || selectedPawn.isKing()) && forwardMove && !canPawnHit(selectedPawn);

        boolean goodMove = (selectedPawn.isKing() || forwardMove || hitMove) && destinationCheck;


        if (this.pawnArray[destY][destX].isType() != 2) {
            System.out.println("This spot is already occupied!");

        } else if (goodMove && hitMove != noHitMove) {

            //This is for kings mainly
            if(hitMove){
                if(canPawnHitDestination(selectedPawn, destinationPawn)){
                    hit(selectedPawn, destinationPawn);
                    moved = !canPawnHit(selectedPawn);
                } else {
                    System.out.println("You have to hit!");
                }
            } else {
                hit(this.selectedPawn, destinationPawn);
                moved = true;
            }
            if(!moved){
                System.out.println("You can still move!");
            } else {
                player = (player + 1) % 2;
                checkPawns();
                deselectPawn(destX, destY);
                if (player == 0) {
                    labelTeam.setText("White team turn");
                } else {
                    labelTeam.setText("Black team turn");
                }
                System.out.println("Pawn moved!");
            }
            drawGrid();


        } else {
            System.out.println("You cant move here");
        }
    }


    public boolean canPawnHitDestination(Pawn origin, Pawn destination) {


        boolean canHit = false;

        int enemyTeam = (origin.isType() + 1) % 2;
        boolean directionX = origin.getX() > destination.getX();

        //True if move is from down to up
        //So pawn is moving up
        boolean directionY = origin.getY() > destination.getY();
        int enemyX = 0;
        int enemyY = 0;


        //left and up
        if (directionX && directionY) {
            enemyX = destination.getX() + 1;
            enemyY = destination.getY() + 1;
        }
        //if left and down
        if (directionX && !directionY) {
            enemyX = destination.getX() + 1;
            enemyY = destination.getY() - 1;
        }
        //if right and up
        if (!directionX && directionY) {
            enemyX = destination.getX() - 1;
            enemyY = destination.getY() + 1;
        }
        //if right and down
        if (!directionX && !directionY) {
            enemyX = destination.getX() - 1;
            enemyY = destination.getY() - 1;
        }

        if (pawnArray[enemyY][enemyX].isType() == enemyTeam) {
            canHit = true;
        }

        int checkingX = origin.getX();
        int checkingY = origin.getY();

        while (canHit && checkingX >= 0 && checkingY >= 0 && checkingX != enemyX && checkingY != enemyY) {
            //left and up
            if (directionX && directionY) {
                checkingX--;
                checkingY--;
            }
            //if left and down
            if (directionX && !directionY) {
                checkingX--;
                checkingY++;
            }
            //if right and up
            if (!directionX && directionY) {
                checkingX++;
                checkingY--;

            }
            //if right and down
            if (!directionX && !directionY) {
                checkingX++;
                checkingY++;
            }
            //If along the way to our target there is an obstacle, we cant hit
            if ((pawnArray[checkingY][checkingX].isType() == 1 || pawnArray[checkingY][checkingX].isType() == 0) && checkingX != enemyX && checkingY != enemyY) {
                canHit = false;
            }
        }
        return canHit;
    }

    public void hit(Pawn origin, Pawn destination) {

        int enemyTeam = (origin.isType() + 1) % 2;
        //True if move is from right to left
        //So pawn is moving left
        boolean directionX = origin.getX() > destination.getX();
        //True if move is from down to up
        //So pawn is moving up
        boolean directionY = origin.getY() > destination.getY();

        int x = 0;
        int y = 0;

        //left and up
        if (directionX && directionY) {
            x = destination.getX() + 1;
            y = destination.getY() + 1;
        }
        //if left and down
        if (directionX && !directionY) {
            x = destination.getX() + 1;
            y = destination.getY() - 1;
        }
        //if right and up
        if (!directionX && directionY) {
            x = destination.getX() - 1;
            y = destination.getY() + 1;
        }
        //if right and down
        if (!directionX && !directionY) {
            x = destination.getX() - 1;
            y = destination.getY() - 1;
        }

        //remove old
        pawnArray[y][x] = new Pawn(2, y, x);
        setUpImage(pawnArray[y][x]);

        pawnArray[origin.getY()][origin.getX()] = new Pawn(2, origin.getY(), origin.getX());
        setUpImage(pawnArray[origin.getY()][origin.getX()]);

        selectedPawn.setX(destination.getX());
        selectedPawn.setY(destination.getY());
        pawnArray[destination.getY()][destination.getX()] = selectedPawn;
    }
}
