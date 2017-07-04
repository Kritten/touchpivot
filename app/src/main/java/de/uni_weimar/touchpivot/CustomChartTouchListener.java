package de.uni_weimar.touchpivot;

import android.view.MotionEvent;
import android.view.View;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.listener.ChartTouchListener;

/**
 * Created by micro on 03.07.2017.
 */

public class CustomChartTouchListener implements View.OnTouchListener {
    private GraphManager.ChartItem chartItem;
    private GraphManager graphManager;
    private float initialX, initialY;

    public CustomChartTouchListener(GraphManager.ChartItem chartItem, GraphManager graphManager) {
        this.chartItem = chartItem;
        this.graphManager = graphManager;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                initialX = event.getX();
                initialY = event.getY();
                break;

            case MotionEvent.ACTION_UP:
                float finalX = event.getX();
                float finalY = event.getY();
                float velocityX = initialX - finalX;
                float velocityY = initialY - finalY;

                if(Math.abs(velocityX) > Math.abs(velocityY)) {
                    if(velocityX < 0.0) {
                        graphManager.nextLayout(this.chartItem);
                    } else {
                        graphManager.previousLayout(this.chartItem);
                    }
                } else {
                    if (velocityY < 0.0) {
                        graphManager.goBackInHistory(this.chartItem);
                    } else {
                        graphManager.goForwardInHistory(this.chartItem);
                    }
                }
                break;
        }
        return false;
    }
}
