package de.uni_weimar.touchpivot;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private DataManager dataManager = null;
    private GraphManager graphManger = null;
    private ListView dataTable = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataTable = (ListView)findViewById(R.id.dataTable);
//        create the data manager (loads the data)
        dataManager = new DataManager(this);
//        create the graph manager displays the data)
        graphManger = new GraphManager(this, dataManager);

//        graphManger.renderGraphBottom(entries, dataManager.getColumns());
//        graphManger.getCurrentChart().setOnClickListener(new CustomClickListener(this));
    }
}
