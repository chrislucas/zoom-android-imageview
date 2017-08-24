package app.com.xplore.explorematrix.views.objects;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;

import app.com.xplore.explorematrix.transformation.Transformation;

/**
 * Created by r028367 on 23/08/2017.
 */

public class Triangle extends View {

    @ColorInt
    private int colorEdge;
    private Transformation transformation;
    private Paint paint;
    private Path path;


    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     */
    public Triangle(Context context, int colorEdge, Paint.Style paintStyle, int strokeWidth) {
        super(context);
        init(paintStyle, strokeWidth, colorEdge);
    }

    /**
     * Constructor that is called when inflating a view from XML. This is called
     * when a view is being constructed from an XML file, supplying attributes
     * that were specified in the XML file. This version uses a default paintStyle of
     * 0, so the only attribute values applied are those in the Context's Theme
     * and the given AttributeSet.
     * <p>
     * <p>
     * The method onFinishInflate() will be called after all children have been
     * added.
     *
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     * @param attrs   The attributes of the XML tag that is inflating the view.
     */
    public Triangle(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Perform inflation from XML and apply a class-specific base paintStyle from a
     * theme attribute. This constructor of View allows subclasses to use their
     * own base paintStyle when they are inflating. For example, a Button class's
     * constructor would call this version of the super class constructor and
     * supply <code>R.attr.buttonStyle</code> for <var>defStyleAttr</var>; this
     * allows the theme's button paintStyle to modify all of the base view attributes
     * (in particular its background) as well as the Button class's attributes.
     *
     * @param context      The Context the view is running in, through which it can
     *                     access the current theme, resources, etc.
     * @param attrs        The attributes of the XML tag that is inflating the view.
     * @param defStyleAttr An attribute in the current theme that contains a
     *                     reference to a paintStyle resource that supplies default values for
     *                     the view. Can be 0 to not look for defaults.
     */
    public Triangle(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    private void init(Paint.Style paintStyle, int strokeWidth, int colorEdge) {
        transformation = new Transformation();
        setBackgroundColor(Color.BLACK);
        paint = new Paint();
        paint.setStyle(paintStyle);
        paint.setColor(colorEdge);
        paint.setStrokeWidth(strokeWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(path, paint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        path = new Path();
        boolean landscapeMode = w > h;
        Log.i("ONSIZE_CHANGED", String.format("Dim(%d,%d)", w, h));
        /**
         * Preparando Scale e Move para trocar de escala logica para de dispositivo
         * */
        if(landscapeMode)
        {
            /**
             * setScale(int h, int -h)
             * A tela do dispositivo eh vista como um plano cartesiano, porem o eixo
             * da coordenada e invertido, de cima para baixo com o eixo Y e da esquerda para
             * direita com o eixo X.
             *
             * Para podermos trabalhar com um tipo de coordenada mais comum, onde o o eixo
             * Y cresce de baixo para cima, nos transformamos o valor maximo da altura da tela em que
             * estamos trabalhando em negativo. A forma que o Sistema desenha na tela nao muda, se pedirmos
             * para desenhar um pixel no ponto (0,0) o sistema desenhara no canto superior esquerdo da tela
             * se quisermos que o desenho seja feito no canto inferior esquerdo, como estamos acostumados,
             * devemos fazer a seguinte conta, (alturaMaxima - pontoYQueDesejamosDesenha)
             * Por exemplo: Numa tela 100x100, se quisermos que o nosso desenho tenha origem no canto
             * inferior esquerdo da tela, sempre que informarmos um valor de Y para um ponto onde
             * devemos fazer a conta 100 - Y. Um exemplo pratico, desenhar uma reta que parta do ponto
             * A(0, 0) e va ate o ponto B(10, 0), e que essa reta fique no canto inferior da tela.
             * O pontos A e B terao que ser convertidos para Aa(0, 100 - 0), Bb(0, 100 - 0) para
             * que sua reta seja desenhada no lugar que se deseja
             *
             * Por isso a funcao setScale recebe os pontos (h, -h) quando esta em landscape
             * e (w, -w) quando esta em portrait
             * */

            /**
             * {@link Transformation#setScale(Transformation.FloatPair)}
             *
             * Definindo a escala que sera usada para transformar as coordenadas
             * logicas em coordenadas de dispositivos. Como explicado acima, como
             * na coordenada de dispositivo, os valores de Y variam de 0 ate N
             * porem a ela inicia no canto superior esquerdo e termina no canto inferior
             * esquerdo. Assim para limitar o desenho dentro da telam o valor de Y na escaka
             * eb definido como -N
             *
             * */
            /**
             * {@link Transformation#setMove(Transformation.FloatPair)}
             *
             * setMove(move.x, move.y)
             *
             * Apos definir a escala usamos o metodo acima para definir o ponto de origem
             * na coordenada de dispostivo
             *
             * Quando quando quisermos desenha um ponto especifico na tela, esse ponto sera
             * pintado entre os pontos P1(move.x - x * scale.x, -min(w,h))  e P2(move.y - y * scale, -min(w, h))
             *
             * As formula
             *  move.x + x * scale.x
             *  move.y + y * scale.y
             *  indicam que o desenho sera feito a partir da origem (move.x, move.y) que seria
             *  o canto superior da nova coordenada.
             *  (move.x + x * scale.x; move.y + y * scale.y) faz a conversao
             *  da coordenada logica para dispositivo e como Y eh negativo, limitamos a area de desenho
             *  para uma area visivel na tela
             *
             *
             * */
            //transformation.setScale(new Transformation.FloatPair(w, -h));
            //transformation.setMove(new Transformation.FloatPair(0, h));
            //transformation.setScale(new Transformation.FloatPair(h, -h));
            //transformation.setMove(new Transformation.FloatPair(ratio, h));
            int ratio = (w-h)/2;
            Transformation.FloatPair scale = new Transformation.FloatPair(w, -h);
            Transformation.FloatPair translate = new Transformation.FloatPair(0, h);
            defineCoordenates(scale, translate);
        }
        else
        {
            /**
             *
             * */
            //transformation.setScale(new Transformation.FloatPair(w, -h));
            //transformation.setMove(new Transformation.FloatPair(0, h));

            int ratio = h - (h-w)/2;
            /**
             *
             *
             * */
            //transformation.setScale(new Transformation.FloatPair(w, -w));
            //transformation.setMove(new Transformation.FloatPair(0, ratio));
            Transformation.FloatPair scale = new Transformation.FloatPair(w, -h);
            Transformation.FloatPair translate = new Transformation.FloatPair(0, h);
            defineCoordenates(scale, translate);
        }
        Log.i("ONSIZE_CHANGED", String.format("%s\n%s"
                , landscapeMode ? "LAND" : "PORTRAIT", transformation.toString()));

        /**
         * Posicionando os pontos do triangulo usando a coordenada de dispositivo
         * */
        float pointAX = transformation.transformX(.1f);
        float pointAY = transformation.transformY(.1f);
        float pointBX = transformation.transformX(.8f);
        float pointBY = transformation.transformY(.8f);
        float pointCX = transformation.transformX(.85f);
        float pointCY = transformation.transformY(.1f);

        String strTransformation = String.format("A(%f, %f); B(%f, %f); C(%f, %f)"
                , pointAX, pointAY, pointBX, pointBY, pointCX, pointCY);

        Log.i("ONSIZE_CHANGED", strTransformation);

        // origem
        path.moveTo(pointAX, pointAY);
        // aresta AB
        path.lineTo(pointBX, pointBY);
        // aresta AC
        path.lineTo(pointCX, pointCY);
        //
        path.close();
    }

    private void defineCoordenates(Transformation.FloatPair scale, Transformation.FloatPair translate) {
        transformation.setScale(scale);
        transformation.setMove(translate);
    }

    @Override
    public boolean onDragEvent(DragEvent event) {

        int dragEventAction = event.getAction();

        switch (dragEventAction) {
            case DragEvent.ACTION_DRAG_STARTED:
                Log.i("DRAG_EVENT", "ACTION_DRAG_STARTED");
                break;

            case DragEvent.ACTION_DRAG_ENDED:
                Log.i("DRAG_EVENT", "ACTION_DRAG_ENDED");
                break;
        }

        return true;
    }
}
