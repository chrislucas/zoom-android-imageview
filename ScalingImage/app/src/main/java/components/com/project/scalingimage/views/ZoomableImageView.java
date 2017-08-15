package components.com.project.scalingimage.views;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

/**
 * Created by r028367 on 08/08/2017.
 */

public class ZoomableImageView extends AppCompatImageView {

    private Matrix matrix = new Matrix();
    private Matrix matrixDoubleTap = new Matrix();

    private static final int NONE   = 0;
    private static final int DRAG   = 1;
    private static final int ZOOM   = 2;
    private static final int CLICK  = 3;
    private int mode = NONE;

    private PointF lastPositionTaped = new PointF();
    private PointF startPositionTaped = new PointF();
    private float minScaleTranslation = 1f;
    private float maxScaleTranslation = 4f;
    private float minScaleZoom = 1f;
    private float maxScaleZoom = 2.5f;

    private float[] matrixValues;

    private float diffRawScalingWidth, diffRawScalingHeight;

    private float saveScaleTransalation = 1f
            , saveScaleZoom = 1f;   // escala para executar o Zoom in
    private float
              distRight                          // distancia entre a imagem apos aplicar a operacao Scale e a largura da tela
            , distBottom                        // distancia entre a imagem apos aplicar a operacao Scale e a altura da tela
            , imageViewWidthScaling             // Largura da imagem apos aplicar a operacao de Scale sobre a largura intrinseca da image
            , imageViewHeightScaling            // Altura da imagem apos aplicar a operacao de Scale sobre a altura intrinseca da image
            , mWidthImageView                   // Largura (raw/bruta) original da ImageView pega atraves do metodo getMeasuredWidth()
            , mHeightImageView;                 // Altura (raw/bruta) original da ImageView pega atraves do metodo getMeasuredHeight()

    private float scaleDoubleTap = 1f;
    private boolean applyScaleDoubleTap = false;

    private ScaleGestureDetector mScaleDetector;
    private MyGestureDetector gestureDetector;
    private Context context;

    public ZoomableImageView(Context context) {
        super(context);
        init(context);
    }

    public ZoomableImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ZoomableImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        implTouch(event);
        return true;
    }

    private OnTouchListener getOnTouchListener() {
        return new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                implTouch(event);
                return true;
            }
        };
    }

    private void init(Context context) {
        super.setClickable(true);
        this.context = context;
        this.mScaleDetector = new ScaleGestureDetector(context, new MyScaleListener());
        this.matrix.setTranslate(1f, 1f);
        this.matrixValues = new float[9];
        setImageMatrix(matrix);
        /**
         * Log.i("MATRIX_INIT", matrix.toString());
         * Definindo o modo de operacao de scale sera feito na ImageView
         * */
        setScaleType(ScaleType.MATRIX);
        // nao preciso implementar OnTouchListener
        //setOnTouchListener(getOnTouchListener());
        GestureDetector.SimpleOnGestureListener simpleOnGestureListener = new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                setScaleType(ScaleType.MATRIX);
                float width = getWidth(), height = getHeight();
                applyScaleDoubleTap = ! applyScaleDoubleTap;
                scaleDoubleTap = applyScaleDoubleTap ? scaleDoubleTap * 1.7f : scaleDoubleTap / 1.7f;
                matrixDoubleTap.postScale(scaleDoubleTap, scaleDoubleTap, width / 2, height / 2);
                setImageMatrix(matrix);
                invalidate();
                Log.i("ON_DOUBLE_TAP", String.format("%f Apply Double Tap %s.\n Matrix %s"
                        , scaleDoubleTap,  applyScaleDoubleTap, matrix.toString()));
                return true;
            }

            @Override
            public boolean onDoubleTapEvent(MotionEvent e) {
                Log.i("ON_DOUBLE_TAP_EVENT", "DOUBLE_TAP");
                return true;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                Log.i("ON_SINGLE_TAP_CONF", "SINGLE_TAP");
                return super.onSingleTapConfirmed(e);
            }
        };
        this.gestureDetector = new MyGestureDetector(context, simpleOnGestureListener);
    }

    private void implTouch(MotionEvent event) {
        mScaleDetector.onTouchEvent(event);
        matrix.getValues(matrixValues);
        /**
         * Constantes
         * {@link Matrix.MPERSP_0}, {@link Matrix.MPERSP_1}, {@link Matrix.MPERSP_2}
         * {@link Matrix.MSCALE_X}, {@link Matrix.MSCALE_Y}
         * {@link Matrix.MSKEW_X}, {@link Matrix.MSKEW_Y}
         * {@link Matrix.ScaleToFit}
         * */
        float translateX = matrixValues[Matrix.MTRANS_X];
        float translateY = matrixValues[Matrix.MTRANS_Y];
        // acao realizada ao tocar na tela
        int action = event.getAction() & MotionEvent.ACTION_MASK;
        // posicao atual tocada
        float currentX = event.getX();
        float currentY = event.getY();
        Log.i("ON_TOUCH", String.format("Ponto(%f, %f).", currentX, currentY));
        /**
         * Ponto da tela
         * A coordenada de dispositivo
         * da esq -> dir X
         * de cima -> baixo y
         *
         * */
        PointF currentPositionTaped = new PointF(currentX, currentY);
        switch (action) {
            /**
             * Gesto de pressionar a tela
             * */
            case MotionEvent.ACTION_DOWN:
                currentX = event.getX();
                currentY = event.getY();
                lastPositionTaped.set(currentX, currentY);
                startPositionTaped.set(lastPositionTaped);
                mode = DRAG;
                break;
            /**
             * Gesto de remover o dedo da tela
             * */
            case MotionEvent.ACTION_UP:
                int diffX = (int) Math.abs(currentPositionTaped.x - startPositionTaped.x);
                int diffY = (int) Math.abs(currentPositionTaped.y - startPositionTaped.y);
                //Log.i("ACTION_UP", String.format("DIFFXY (%d %d)\n", diffX, diffY));
                if(diffX < CLICK && diffY < CLICK) {
                    /**
                     * Executa um click se OnclickListener tiver sido implementado
                     * */
                    boolean hasOnclickListener = performClick();
                    Log.i("PERFORM_CLICK", String.valueOf(hasOnclickListener));
                }
                mode = NONE;
                break;
            /**
             * Evento capturado entre o ACTION_DOWN e ACTION_UP. Esse evento contem
             * o as coordenadas do ponto na tela mais recentes pressionado pelo usuario
             * e todos os pontos anteriores desde o ultimo ACTION_DOWN o ACTION_MOVE
             * */
            case MotionEvent.ACTION_MOVE:
                /**
                 *  (mode == DRAG) == V
                 *  O usuario pressionou o dedo (MotionEvent.ACTION_DOWN) sobre a imagem e a arrastou
                 *
                 *  (mode == ZOOM) == V
                 *  O usuario pressionou 2 ou mais dedos na tela (case MotionEvent.ACTION_POINTER_DOWN)
                 *  e fez um movimento de abrir ou fechar os dedos (pinch)
                 *
                 * */
                if(mode == ZOOM || (mode == DRAG && saveScaleTransalation > minScaleTranslation && saveScaleTransalation < maxScaleTranslation)) {
                    Log.i("ACTION_MOVE", String.format("%s", mode == ZOOM ? "ZOOM" : "DRAG"));
                    float deltaX = currentPositionTaped.x - lastPositionTaped.x;
                    float deltaY = currentPositionTaped.y - lastPositionTaped.y;
                    float proportionalWidth   = Math.round(imageViewWidthScaling * saveScaleTransalation);
                    float proportionalHeight  = Math.round(imageViewHeightScaling * saveScaleTransalation);
                    int imageViewWidth   = getWidth();
                    int imageViewHeight  = getHeight();
                    Log.i("ACTION_MOVE", String.format("ANTES DELTAXY(%f, %f)", deltaX, deltaY));
                    Log.i("ACTION_MOVE", String.format("SCALE(%f)", saveScaleTransalation));
                    Log.i("ACTION_MOVE", String.format("ORIGIN-DIM(%d, %d)", imageViewWidth, imageViewHeight));
                    Log.i("ACTION_MOVE", String.format("SCALE-DIM(%f, %f)", proportionalWidth, proportionalHeight));
                    Log.i("ACTION_MOVE", String.format("Transalate (%f,%f)", translateX, translateY));
                    Log.i("ACTION_MOVE", String.format("RB (%f,%f)", distRight, distBottom));
                    Log.i("ACTION_MOVE", "\n");
                    boolean restrictMovimentX  = false, restrictMovimentY  = false;
                    /**
                     * Se a imagem ainda cabe na tela restringir o movimento para esquerda/direita
                     * e para cima e para baixo
                     * */
                    if(proportionalWidth <= imageViewWidth && proportionalHeight <= imageViewHeight) {
                        break;
                    }
                    /**
                     * Apos aplicar a operacao de escala na largura da imagem original
                     * a imagem resultante tem a largura menor que a original ?
                     *
                     * Coordenada Y, da esquerda da tela para direita
                     *
                     * */
                    else if (proportionalWidth < imageViewWidth) {
                        //
                        deltaX = 0;
                        restrictMovimentY = true;
                    }
                    /**
                     * E a altura ?
                     * Coordenada X do topo da tela ate a base
                     * */
                    else if(proportionalHeight < imageViewHeight) {
                        deltaY = 0;
                        restrictMovimentX = true;
                    }
                    else {
                        restrictMovimentY = true;
                        restrictMovimentX = true;
                    }
                    // movimentos da esquerda para direita
                    if( restrictMovimentY ) {
                        if(translateY + deltaY >  0) {
                            deltaY = -translateY;
                        }
                        else if(translateY + deltaY < -distBottom) {
                            deltaY = -(translateY + distBottom);
                        }
                    }
                    // movimento de cima para baixo
                    if( restrictMovimentX ) {
                        if(translateX + deltaX > 0) {
                            deltaX = -translateX;
                        }
                        else if(translateX + deltaX < -distRight) {
                            deltaX = -(translateX + distRight);
                        }
                    }
                    Log.i("ACTION_MOVE", String.format("DEPOIS DELTAXY(%f, %f)", deltaX, deltaY));
                    matrix.postTranslate(deltaX, deltaY);
                    lastPositionTaped.set(currentPositionTaped.x, currentPositionTaped.y);
                }
                break;

            /**
             * A non-primary pointer has gone down.
             * Quando o usuario pressionada a tela com 2 ou mais dedos
             * o evento (MotionEvent.ACTION_POINTER_DOWN) e executado
             * */
            case MotionEvent.ACTION_POINTER_DOWN:
                currentX = event.getX();
                currentY = event.getY();
                lastPositionTaped.set(currentX, currentY);
                startPositionTaped.set(lastPositionTaped);
                mode = ZOOM;
                break;
            /**
             * A non-primary pointer has gone up.
             * Quando o usuario toca na tela com 2 ou mais dedos, ao
             * remove-los o evento (MotionEvent.ACTION_POINTER_UP) eh
             * executado ateh que sobre somente 1 dedo, quando o ultimo
             * dedo for removido da tela, o evento ACTION_UP sera executado
             * */
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;
        }
        gestureDetector.onTouchEvent(event);
        setImageMatrix(matrix);
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        /**
         * Encapsula o metodo {@link Drawable#getIntrinsicWidth()}
         * responsavel por retornar o valor de largura intrinseco que o Drawable
         * possui, incluindo tambem valores de padding, caso a imagem possua
         * */
        int intrWidth   = getIntrWidth();
        int intrHeight  = getIntrHeight();
        // @return The raw measured mWidthImageView of this view. // valor bruto ?
        mWidthImageView = getMeasuredWidth();
        // @return The raw measured mHeightImageView of this view. // valor bruto ?
        mHeightImageView = getMeasuredHeight();
        // Escala proporcional. Razao entre widthRaw/instrinsicWidth ou heightRaw/intrinsicHeight
        //
        float scale = mWidthImageView > mHeightImageView ? mHeightImageView / intrHeight : mWidthImageView / intrWidth;

        Log.i("ON_MEASURE", String.format("Dimensao bruta (%f, %f)\n Escala %f\nIntrinsic W/H(%d %d)"
                , mWidthImageView
                , mHeightImageView
                , scale
                , intrWidth
                , intrHeight)
        );
        matrix.setScale(scale, scale);
        saveScaleTransalation = 1f;
        // tamanho original
        imageViewWidthScaling   = scale * intrWidth;
        imageViewHeightScaling  = scale * intrHeight;
        Log.i("ON_MEASURE", String.format("Dimensao pos Scaling(%f, %f)\n Escala %f", imageViewWidthScaling, imageViewHeightScaling, scale));
        diffRawScalingHeight    = (mHeightImageView - imageViewHeightScaling);
        diffRawScalingWidth     = (mWidthImageView - imageViewWidthScaling);
        float midRawWidth       = diffRawScalingWidth / 2.0f;
        float midRawHeight      = diffRawScalingHeight / 2.0f;
        Log.i("ON_MEASURE", String.format("Centro da imagem (%f, %f).\nTransalacao (%f, %f)."
                , diffRawScalingWidth
                , diffRawScalingHeight
                , midRawWidth
                , midRawHeight
            )
        );
        matrix.postTranslate(midRawWidth, midRawHeight);
        setImageMatrix(matrix);
    }

    private class MyGestureDetector extends GestureDetector{
        /**
         * Creates a GestureDetector with the supplied listener.
         * You may only use this constructor from a {@link Looper} thread.
         *
         * @param context  the application's context
         * @param listener the listener invoked for all the callbacks, this must
         *                 not be null.
         * @throws NullPointerException if {@code listener} is null.
         * @see Handler#Handler()
         */
        public MyGestureDetector(Context context, OnGestureListener listener) {
            super(context, listener);
        }
    }

    private class MyScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        private void debugScaleGestureDetector(ScaleGestureDetector detector, float scaleFactor) {
            Log.i("ON_SCALE_MY_LISTENER", String.format("ON_SCALE Scale Factor %f.\n (Current,Previous)(%f,%f)"
                    , scaleFactor
                    , detector.getCurrentSpan()     // a media entre cada ponto que forma o gesto executado na tela do celular
                    , detector.getPreviousSpan()    // idem para
                    )
            );
        }

        /**
         *
         * */
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            /**
             * Return the scaling factor from the previous scale event to the current
             * event. This value is defined as
             * ({@link #getCurrentSpan()} / {@link #getPreviousSpan()}).
             *
             * @return The current scaling factor.
             */
            float scaleFactor = detector.getScaleFactor();
            float newScale = saveScaleTransalation * scaleFactor;
            Log.i("ON_SCALE_MY_LISTENER", String.format("ON_SCALE - New Scale %f", newScale));
            /**
             * Verifica se a escala esta dentro do intervalor [min,max] para redimensionar
             * a imagem
             * */
            if(newScale < maxScaleTranslation && newScale > minScaleTranslation) {
                saveScaleTransalation = newScale;
            }
            if(newScale < maxScaleZoom && newScale > minScaleZoom) {
                saveScaleZoom = newScale;
                // Largura da ImageView
                float width = getWidth();
                // altura da ImageView
                float height = getHeight();
                /**
                 * imageViewWidthScaling e imageViewHeightScaling sao definidos no metodo
                 * onMeasure
                 * */
                //
                distRight = (imageViewWidthScaling * saveScaleZoom) - width;
                distBottom = (imageViewHeightScaling * saveScaleZoom) - height;
                float scaleBitmapWidth = imageViewWidthScaling * scaleFactor;
                float scaleBitmapHeight = imageViewHeightScaling * scaleFactor;
                Log.i("LISTENER_ACTION_MOVE", String.format("wh (%f %f)\n whs(%f %f)\nFactor (%f)"
                        , width
                        , height
                        , scaleBitmapWidth
                        , scaleBitmapHeight
                        , scaleFactor
                ));

                if(scaleBitmapWidth < width || scaleBitmapHeight < height) {
                    matrix.postScale(scaleFactor, scaleFactor, width / 2, height / 2);
                }
                else {
                    float detectFocusPx = detector.getFocusX(), detectFocusPy = detector.getFocusY();
                    Log.i("LISTENER_ACTION_MOVE", String.format("Detect Focus (%f %f)", detectFocusPx, detectFocusPy));
                    matrix.postScale(scaleFactor, scaleFactor, detectFocusPx, detectFocusPy);
                }
            }
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            mode = ZOOM;
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {}
    }

    private int getIntrWidth() {
        Drawable drawable = getDrawable();
        return  drawable != null ? drawable.getIntrinsicWidth() : 0;
    }

    private int getIntrHeight() {
        Drawable drawable = getDrawable();
        return  drawable != null ? drawable.getIntrinsicHeight() : 0;
    }
}
