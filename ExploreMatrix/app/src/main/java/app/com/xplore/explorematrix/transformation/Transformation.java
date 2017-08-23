package app.com.xplore.explorematrix.transformation;

import android.graphics.PointF;

/**
 * Created by r028367 on 23/08/2017.
 */

public class Transformation {

    public static class FloatPair extends PointF {
        public FloatPair(float x, float y) {
            super(x, y);
        }
    }

    private FloatPair move, scale;

    public Transformation() {
        setMove(new FloatPair(0,0));
        setScale(new FloatPair(1,1));
    }

    /**
     * Os metodos setMove e setScale sao metodos de transformacao geometrica
     * */

    public void setMove(FloatPair move) {
        this.move = move;
    }

    public void setScale(FloatPair scale) {
        this.scale = scale;
    }

    /**
     *
     * Os metodo transformX e transformY servem para mudar o tipo de coordenada utilizado no App
     * De logico para dispositivo
     * */

    public float transformX(float x) {
       return move.x + scale.x * x;
    }

    public float transformY(float y) {
        return move.y + scale.y * y;
    }

    @Override
    public String toString() {
        return String.format("Scale(%f, %f)\nMove(%f, %f)"
                , scale.x, scale.y, move.x, move.y
        );
    }
}
