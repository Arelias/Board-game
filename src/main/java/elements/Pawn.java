package elements;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Pawn {
    //True is white, False is black
    private int type;
    private boolean king;
    private int x, y;
    ImageView image;



    public Pawn(int type, int y, int x){
        this.type = type;
        this.x = x;
        this.y = y;

        if(this.type == 0){
            this.image = new ImageView(new Image("whitePawn.png"));
        }
        if(this.type == 1){
            this.image = new ImageView(new Image("blackPawn.png"));
        }
        if (this.type == 2){
            this.image = new ImageView(new Image("emptyPawn.png"));
            this.image.setOpacity(0);
        }
    }

    public int isType() {
        return type;
    }

    public boolean isKing() {
        return king;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setKing(boolean king) {
        this.king = king;
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

    public ImageView getImageView() {
        return image;
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
