package components.com.project.scalingimage.views;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

/**
 * Created by r028367 on 08/08/2017.
 */

public class ZoomableImageView extends AppCompatImageView{

    private Matrix matrix = new Matrix();

    private static final int NONE   = 0;
    private static final int DRAG   = 1;
    private static final int ZOOM   = 2;
    private static final int CLICK  = 3;
    int mode = NONE;

    private PointF last = new PointF();
    private PointF start = new PointF();
    private float minScale = 1f;
    private float maxScale = 4f;
    private float[] matrixValues;

    private float redundantXSpace, redundantYSpace;
    private float width, height;
    private float saveScale = 1f;
    private float right, bottom, originalWidth, originalHeight;

    private ScaleGestureDetector mScaleDetector;
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
        return true;
    }

    private void init(Context context) {
        this.context = context;
        mScaleDetector = new ScaleGestureDetector(context, new MyScaleListener());

        matrix.setTranslate(1f, 1f);
        matrixValues = new float[9];
        setImageMatrix(matrix);
        setScaleType(ScaleType.MATRIX);

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mScaleDetector.onTouchEvent(event);
                matrix.getValues(matrixValues);
                float translateX = matrixValues[Matrix.MTRANS_X];
                float translateY = matrixValues[Matrix.MTRANS_Y];
                int action = event.getAction() & MotionEvent.ACTION_MASK;

                PointF currentPoint = new PointF(event.getX(), event.getY());

                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        last.set(event.getX(), event.getY());
                        start.set(last);
                        break;

                    case MotionEvent.ACTION_UP:

                        int diffX = (int) Math.abs(currentPoint.x - start.x);
                        int diffY = (int) Math.abs(currentPoint.y - start.y);

                        if(diffX < 3 && diffY < 3) {
                            performClick();
                        }


                        break;

                    case MotionEvent.ACTION_MOVE:

                        float deltaX = currentPoint.x - last.x;
                        float deltaY = currentPoint.y - last.y;
                        Log.i("ACTION_MOVE", String.format("DELTA (%f, %f)\n", deltaX, deltaY));

                        float scaledWidth = Math.round(originalWidth * saveScale);
                        float scaledHeight = Math.round(originalHeight * saveScale);

                        boolean limitX = false;
                        boolean limitY = false;

                        int viewWidth = getWidth();
                        int viewHeight = getHeight();

                        /**
                         * Apos aplicar a operacao de escala na largura da imagem
                         * ela ainda cabe na largura da view
                         * */
                        if(scaledWidth < viewWidth) {
                            deltaX = 0;
                            limitY = true;
                        }

                        /**
                         * Apos aplicar a operacao de escala na altura da imagem
                         * ela ainda cabe na altura da view
                         * */
                        else if(scaledHeight < viewHeight) {
                            deltaY = 0;
                            limitX = true;
                        }

                        else {
                            limitX = true;
                            limitY = true;
                        }

                        if(limitY) {
                            if(translateY + deltaY >  0) {
                                deltaY = -translateY;
                            }
                            else if(translateY + deltaY < -bottom) {
                                deltaY = -(translateY + bottom);
                            }
                        }

                        if(limitX) {
                            if(translateX + deltaX > 0) {
                                deltaX -= translateX;
                            }
                            else if(translateX + deltaX < -right) {
                                deltaX = -(translateX + right);
                            }
                        }

                        matrix.postTranslate(deltaX, deltaY);
                        last.set(currentPoint.x, currentPoint.y);

                        break;

                    case MotionEvent.ACTION_POINTER_DOWN:
                        last.set(event.getX(), event.getY());
                        start.set(last);
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        break;
                }
                setImageMatrix(matrix);
                invalidate();
                return true;
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int intrWidth = getIntrWidth();
        int intrHeight = getIntrHeight();


        Log.i("ON_MEASURE", String.format("Intrinsic Dimension (%d,%d)\n", intrWidth, intrHeight));

        float width = getMeasuredWidth();
        float height = getMeasuredHeight();

        float scale = width > height ? height / intrHeight : width / intrWidth;

        matrix.setScale(scale, scale);
        saveScale = 1f;

        originalWidth = scale * intrWidth;
        originalHeight = scale * intrHeight;

        // centro
        redundantYSpace = (height - originalHeight);
        redundantXSpace = (width -  originalWidth);

        Log.i("ON_MEASURE", String.format("Centro da imagem (%f, %f)"
                , redundantXSpace, redundantYSpace));

        matrix.postTranslate(redundantXSpace / 2, redundantYSpace / 2);
        setImageMatrix(matrix);
    }

    private  class MyScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactor = detector.getScaleFactor();
            Log.i("ON_SCALE_MY_LISTENER", String.format("ON_SCALE Scale Factor %f", scaleFactor));
            float newScale = saveScale * scaleFactor;
            Log.i("ON_SCALE_MY_LISTENER", String.format("New Scale %f", newScale));
            if(newScale < maxScale && newScale > minScale) {
                saveScale = newScale;
                float width = getWidth();
                float height = getHeight();
                right = (originalWidth * saveScale) - width;
                bottom = (originalHeight * saveScale) - height;
                float scaleBitmapWidth = originalWidth * scaleFactor;
                float scaleBitmapHeight = originalHeight* scaleFactor;
                if(scaleBitmapWidth <= width || scaleBitmapHeight <= height) {
                    matrix.postScale(scaleFactor, scaleFactor, width / 2, height / 2);
                }
                else {
                    matrix.postScale(scaleFactor, scaleFactor, detector.getFocusX(), detector.getFocusY());
                }
            }
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            Log.i("ON_SCALE_MY_LISTENER", "ON_SCALE_BEGIN");
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            Log.i("ON_SCALE_MY_LISTENER", "ON_SCALE_END");
        }
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
