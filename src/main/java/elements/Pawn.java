package elements;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Pawn {
    //True is white, False is black
    private int type;
    private boolean king;
    private int x, y;
    private ImageView image;
    private boolean movable = false;
    private boolean isSelected = false;
    private static ImageView selection = new ImageView(new Image("selectedPawn.png"));


    //Type 0 is white
    //Type 1 is black
    //Type 2 is empty field
    public Pawn(int type, int y, int x){

        this.type = type;
        this.x = x;
        this.y = y;
    }



    public int isType() {
        return type;
    }

    public boolean isKing() {
        return king;
    }

    public boolean isMovable() {
        return movable;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getType() {
        return type;
    }

    public ImageView getImage() {
        return image;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public static ImageView getSelection() {
        return selection;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setKing(boolean king) {
        this.king = king;
    }

    public void setMovable(boolean movable) {
        this.movable = movable;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setImage(ImageView image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Pawn{" +
                "type=" + type +
                ", king=" + king +
                ", x=" + x +
                ", y=" + y +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pawn pawn = (Pawn) o;

        if (type != pawn.type) return false;
        if (king != pawn.king) return false;
        if (x != pawn.x) return false;
        if (y != pawn.y) return false;
        return image.equals(pawn.image);

    }

    @Override
    public int hashCode() {
        int result = type;
        result = 31 * result + (king ? 1 : 0);
        result = 31 * result + x;
        result = 31 * result + y;
        result = 31 * result + image.hashCode();
        return result;
    }
}
