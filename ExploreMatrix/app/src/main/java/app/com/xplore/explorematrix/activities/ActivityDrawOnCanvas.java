package app.com.xplore.explorematrix.activities;

import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

import app.com.xplore.explorematrix.R;
import app.com.xplore.explorematrix.views.objects.Triangle;

public class ActivityDrawOnCanvas extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_draw_on_canvas);
        Triangle triangle = new Triangle(this, Color.BLUE, Paint.Style.STROKE, 25);
        setContentView(triangle);
    }
}
