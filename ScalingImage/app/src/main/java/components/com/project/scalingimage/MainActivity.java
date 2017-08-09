package components.com.project.scalingimage;


import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;

    private Matrix matrix = new Matrix()
            , saveMatrix = new Matrix();
    private PointF start = new PointF()
            , midPoint = new PointF();
    private float oldDistance = 1f, originalScale = -1f, originMidW, originMidH;
    private float [] lastPosEvent = new float[4];
    private int w, h;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.image_to_scaling);
        imageView.setOnTouchListener(getOnTouchListener());
        ViewTreeObserver viewTreeObserver = imageView.getViewTreeObserver();
        viewTreeObserver.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                w = imageView.getMeasuredWidth();
                h = imageView.getMeasuredHeight();
                originalScale   = (float) ((w*1.0)/(h*1.0));
                originMidW      = ((float) (w*1.0)/ 2.0f);
                originMidH      = ((float) (h*1.0)/ 2.0f);
                //Log.i("PRE_DRAW_IMAGEVIEW", String.format("(%d, %d)", w, h));
                return true;
            }
        });

        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

            }
        });
    }

    public class MyGestureDetector extends GestureDetector {
        private ImageView imageView;
        public ImageView getImageView() {
            return imageView;
        }

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
        public MyGestureDetector(Context context, OnGestureListener listener, ImageView imageView) {
            super(context, listener);
            this.imageView = imageView;
        }
    }

    private MyGestureDetector getGestureDetector(Context context, ImageView imageView) {
        return new MyGestureDetector(context, new MyGestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                Log.i("SINGLE_TAP_CONF", "SINGLE_TAP_CONF");
                return true;
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                Log.i("SINGLE_TAP_UP", "SINGLE_TAP_UP");
                return true;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                Log.i("ON_DOUBLE_TAP", "ON_DOUBLE_TAP");
                return true;
            }

            @Override
            public boolean onDoubleTapEvent(MotionEvent e) {
                Log.i("ON_DOUBLE_TAP_EVENT", "ON_DOUBLE_TAP_EVENT");
                return true;
            }
        }
        , imageView);
    }

    private View.OnTouchListener getOnTouchListener() {
        final Context context = this;
        return new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ImageView imageView = (ImageView) v;
                /**
                 * https://developer.android.com/reference/android/widget/ImageView.ScaleType.html
                 * */
                imageView.setScaleType(ImageView.ScaleType.MATRIX);
                //imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                float scale;
                int c = -1;
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        saveMatrix.set(matrix);
                        start.set(event.getX(), event.getY());
                        Log.i("ACTION_DOWN", "ACTION_DOWN"/*String.valueOf(imageView.getId())*/);
                        break;

                    case MotionEvent.ACTION_BUTTON_PRESS:
                        break;

                    case MotionEvent.ACTION_UP:
                        //matrix.setScale(originalScale, originalScale/*, originMidW, originMidH*/);
                        //Log.i("ACTION_UP", "ACTION_UP"/*String.valueOf(imageView.getId())*/);
                        break;
                    /**
                     *
                     * */
                    case MotionEvent.ACTION_POINTER_UP:
                        //Log.i("ACTION_POINTER_UP", "ACTION_POINTER_UP"/*String.valueOf(imageView.getId())*/);
                        break;

                    case MotionEvent.ACTION_POINTER_DOWN:
                        Log.i("ACTION_POINTER_DOWN", "ACTION_POINTER_DOWN"/*String.valueOf(imageView.getId())*/);
                        c = event.getPointerCount();
                        if(c == 2) {
                            oldDistance = euclidianDistance(event);
                            if(oldDistance > 5.0d) {
                                saveMatrix.set(matrix);
                                midPoint(midPoint, event);
                            }
                            lastPosEvent[0] = event.getX(0);
                            lastPosEvent[1] = event.getX(1);
                            lastPosEvent[2] = event.getY(0);
                            lastPosEvent[3] = event.getY(1);

                            Log.i("LAST_ACT_POINTER_DOWN", String.format("%f %f %f %f"
                                , lastPosEvent[0]
                                , lastPosEvent[1]
                                , lastPosEvent[2]
                                , lastPosEvent[3])
                            );
                        }
                        break;

                    /**
                     * Uma acao capturada entre ACTION_DOWN e ACTION_UP
                     * Nesse momento, podemos capturar o ponto P(x,y) mais recente
                     * onde o usuario pressionou a tela.
                     * */
                    case MotionEvent.ACTION_MOVE:
                        Log.i("ACTION_MOVE", "ACTION_MOVE"/*String.valueOf(imageView.getId())*/);
                        c = event.getPointerCount();
                        if(c == 2) {
                            float newDistance = euclidianDistance(event);
                            if(newDistance > 400.0d && newDistance < 1200.0d) {
                                matrix.set(saveMatrix);
                                scale = newDistance / oldDistance;
                                w = imageView.getWidth();
                                h = imageView.getHeight();
                                Log.i("FINGER_DISTANCE_SCALE", String.format("Scale %f\n", scale));
                                Log.i("FINGER_DISTANCE_N_SCALE", String.format("Distance %f Dim(%f %f)\n"
                                    , newDistance
                                    , (w * scale)
                                    , (h * scale))
                                );
                                /**
                                 * https://developer.android.com/reference/android/graphics/Matrix.html#setScale(float, float)
                                 *
                                 * Aplicando a operacao de Escala na imagem. O ponto
                                 * P(x, y) e o ponto pivo que nao vai ser alterado durante a operacao de escala
                                 *
                                 * */
                                //matrix.setScale((float) scale, (float) scale, midPoint.x, midPoint.y);
                                /**
                                 *
                                 * Aplicar a operacao de Scaling apos a concatenacao da matriz de escala
                                 * */
                                //matrix.postScale((float) scale, (float) scale, midPoint.x, midPoint.y);
                                matrix.postScale(scale, scale);
                            }
                        }
                        /**
                         * TODO implementar rotacao
                         * */
                        else if( c == 3) {

                        }
                        break;
                }

                Log.i("MATRIX_SCALING", matrix.toString());

                imageView.setImageMatrix(matrix);
                imageView.invalidate();
                float data [] = new float[9];
                matrix.getValues(data);

                final float scaleX = data[Matrix.MSCALE_X];
                final float scaleY = data[Matrix.MSCALE_Y];

                Drawable drawable = imageView.getDrawable();
                float fw = drawable.getIntrinsicWidth() * scaleX;
                float fh = drawable.getIntrinsicHeight() * scaleY;

                //Log.i("NEW_DIMENTION", String.format("(%f, %f)", fw, fh));
                MyGestureDetector gestureDetector = getGestureDetector(context, imageView);
                gestureDetector.onTouchEvent(event);
                return true;
            }
        };
    }

    private float euclidianDistance(MotionEvent event) {
        float diffx = event.getX(0) - event.getX(1);
        float diffy = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(diffx*diffx + diffy*diffy);
    }

    private void midPoint(PointF pointF, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        pointF.set( x/2.0f, y/2.0f);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
