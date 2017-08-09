package components.com.project.scalingimage.list;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.List;

import components.com.project.scalingimage.R;
import components.com.project.scalingimage.entities.Post;
import components.com.project.scalingimage.list.adapter.AdapterRecycleViewZoomableImageView;

public class ListZoomableImageView extends AppCompatActivity {

    public static class RecycleItemClickListener implements RecyclerView.OnItemTouchListener {

        private OnItemClickListener onItemClickListener;
        private Context context;
        private GestureDetector gestureDetector;
        public interface OnItemClickListener {
            public void onItemClick(View view, int position);
        }

        public RecycleItemClickListener(Context context, OnItemClickListener onItemClickListener) {
            this.context = context;
            this.onItemClickListener = onItemClickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onSingleTapUp(MotionEvent e) {
                        return true;
                    }
                }
            );
        }

        /**
         * Silently observe and/or take over touch events sent to the RecyclerView
         * before they are handled by either the RecyclerView itself or its child views.
         * <p>
         * <p>The onInterceptTouchEvent methods of each attached OnItemTouchListener will be run
         * in the order in which each listener was added, before any other touch processing
         * by the RecyclerView itself or child views occurs.</p>
         *
         * @param rv
         * @param e  MotionEvent describing the touch event. All coordinates are in
         *           the RecyclerView's coordinate system.
         * @return true if this OnItemTouchListener wishes to begin intercepting touch events, false
         * to continue with the current behavior and continue observing future events in
         * the gesture.
         */
        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View viewChild = rv.findChildViewUnder(e.getX(), e.getY());
            if(viewChild != null && onItemClickListener != null && gestureDetector.onTouchEvent(e)) {
                onItemClickListener.onItemClick(viewChild, rv.getChildAdapterPosition(viewChild));
                return true;
            }
            return false;
        }

        /**
         * Process a touch event as part of a gesture that was claimed by returning true from
         * a previous call to {@link #onInterceptTouchEvent}.
         *
         * @param rv
         * @param e  MotionEvent describing the touch event. All coordinates are in
         */
        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        /**
         * Called when a child of RecyclerView does not want RecyclerView and its ancestors to
         * intercept touch events with
         * {@link android.view.ViewGroup#onInterceptTouchEvent(MotionEvent)}.
         *
         * @param disallowIntercept True if the child does not want the parent to
         *                          intercept touch events.
         * @see android.view.ViewParent#requestDisallowInterceptTouchEvent(boolean)
         */
        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }


    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_zoomable_image_view);

        int idA = getResources().getIdentifier("dust", "drawable", getPackageName());
        int idB = getResources().getIdentifier("penguins", "drawable", getPackageName());

        Drawable drawableA, drawableB;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawableA = getResources().getDrawable(idA, getTheme());
            drawableB = getResources().getDrawable(idB, getTheme());
        }
        else {
            drawableA = ContextCompat.getDrawable(this, R.drawable.dust);
            drawableB = ContextCompat.getDrawable(this, R.drawable.penguins);
        }

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        final List<Post> posts = Post.getList(this, drawableA, drawableB);

        AdapterRecycleViewZoomableImageView adapter = new AdapterRecycleViewZoomableImageView(posts, this);
        recyclerView = (RecyclerView) findViewById(R.id.list_image_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        RecycleItemClickListener.OnItemClickListener onItemClickListener = new RecycleItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Post post = posts.get(position);
            }
        };

        RecycleItemClickListener recycleItemClickListener = new RecycleItemClickListener(this, onItemClickListener);
        recyclerView.addOnItemTouchListener(recycleItemClickListener);
    }
}
