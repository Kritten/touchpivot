package de.uni_weimar.touchpivot;

import android.view.MotionEvent;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;

/**
 * Created by micro on 02.07.2017.
 */

public class CustomChartListener implements OnChartGestureListener {
    private GraphManager.ChartItem chartItem;
    private GraphManager graphManager;
    public CustomChartListener(GraphManager.ChartItem chartItem, GraphManager graphManager) {
        this.chartItem = chartItem;
        this.graphManager = graphManager;
    }

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartLongPressed(MotionEvent me) {

    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {

    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {
        System.out.println(this);
        System.out.println(this.chartItem);
        System.out.println(this.graphManager);
    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
//        System.out.println(Float.toString(velocityX)+" "+ Float.toString(velocityY));
//        horizontal swipe
        if(Math.abs(velocityX) > Math.abs(velocityY)) {
            if(velocityX < 0.0) {
                this.graphManager.nextLayout(this.chartItem);
            } else {
                this.graphManager.previousLayout(this.chartItem);
            }
        } else {
            if(velocityY < 0.0) {
                this.graphManager.goBackInHistory(this.chartItem);
            } else {
                this.graphManager.goForwardInHistory(this.chartItem);
            }
//        graphManger.getCurrentChart().setOnClickListener(new CustomClickListener(this));
        }
        System.out.println("REACHED HERE");
    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {

    }
}
