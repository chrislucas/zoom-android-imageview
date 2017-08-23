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
             * setScale(h, -h)
             *
             * Girando a coordenada de dispositivo
             *
             * */
            transformation.setScale(new Transformation.FloatPair(h, -h));
            int ratio = (w-h)/2;
            //int ratio = w/2;
            /**
             *
             *
             * */
            transformation.setMove(new Transformation.FloatPair(ratio, h));
        }
        else
        {
            transformation.setScale(new Transformation.FloatPair(w, -w));
            int ratio = (h-w)/2;
            //int ratio = h/2;
            /**
             * Em landscape os desenhos sao
             *
             * */
            transformation.setMove(new Transformation.FloatPair(0, h - ratio));
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
        float pointCX = transformation.transformX(.8f);
        float pointCY = transformation.transformY(.2f);

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
