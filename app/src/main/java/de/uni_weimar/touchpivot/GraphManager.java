package de.uni_weimar.touchpivot;

import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by micro on 24.06.2017.
 */

class GraphManager {
    private Activity activity = null;
    private DataManager dataManager = null;
    private List<ChartItem> listHistory = new ArrayList<>();
    private int historyCurrentState = -1;
    private ChartItem currentChart = null;
    private List<Class> listChartTypes = new ArrayList<>();

    public GraphManager(Activity activity, DataManager dataManager) {
        this.activity = activity;
        this.dataManager = dataManager;

//        listChartTypes.add(PieChart.class);
        listChartTypes.add(BarChart.class);
        listChartTypes.add(LineChart.class);

        renderInitialState();
    }

    private void renderInitialState() {
        this.showStats();

        List<Entry> entries = new ArrayList<>();
        int counter = 0;
        for(String column: this.dataManager.getColumns()) {
            Set<String> set = new HashSet<>();
            for(String value: this.dataManager.getColumn(column)) {
                set.add(value);
            }
            entries.add(new Entry(counter, set.size()));
            counter += 1;
        }

        ChartItem chartItem = this.addGraph(entries, BarChart.class, Location.Bottom , this.dataManager.getColumns(), false);
        chartItem.chart.setOnTouchListener(null);
    }

    private void showStats() {
        TextView textViewCountRows = (TextView) activity.findViewById(R.id.textview_count_rows);
        textViewCountRows.setText(Integer.toString(this.dataManager.getCountItems()));

        TextView textViewCountColumns = (TextView) activity.findViewById(R.id.textview_count_columns);
        textViewCountColumns.setText(Integer.toString(this.dataManager.getCountColumns()));
    }

    public Chart getCurrentChart() {
        return currentChart.chart;
    }

    public ChartItem addGraph(List<Entry> entries, Class chartType, Location location, final List<String> labels, boolean addToHistory) {
        Chart chart = null;
        switch (chartType.getSimpleName()) {
            case "BarChart":
                chart = this.createBarChart(entries, labels);
                break;
            case "LineChart":
                chart = this.createLineChart(entries, labels);
                break;
            case "PieChart":
                chart = this.createPieChart(entries);
                break;
        }

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        chart.setLayoutParams(params);


        RelativeLayout layout;
        if(location == null) {
            location = Location.Bottom;
        }
        switch(location) {
            case Top:
                layout = (RelativeLayout) activity.findViewById(R.id.layout_graph_top);
                break;
            case Bottom:
                layout = (RelativeLayout) activity.findViewById(R.id.layout_graph_bottom);
                break;
            default:
                layout = (RelativeLayout) activity.findViewById(R.id.layout_graph_bottom);
                break;
        }
        layout.addView(chart);

        chart.getLegend().setEnabled(false);
        chart.setDescription(null);

        chart.invalidate();

        if(currentChart != null) {
            currentChart.chart.setVisibility(View.GONE);
        }

        ChartItem chartItem = new ChartItem(chart, chartType, entries, labels, location);
        currentChart = chartItem;

        if(addToHistory) {
            listHistory.add(chartItem);
        }


//        chartItem.chart.setOnChartGestureListener(new CustomChartListener(chartItem, this));
        chartItem.chart.setOnTouchListener(new CustomChartTouchListener(chartItem, this));
        return chartItem;
    }

    private Chart createPieChart(List<Entry> entries) {
        List<PieEntry> pieEntries = new ArrayList<>();
        for(Entry entry: entries) {
//            System.out.println(entry.getY());
//            System.out.println(Float.toString(entry.getX()));
            pieEntries.add(new PieEntry(entry.getY(), Float.toString(entry.getX())));
        }
        PieChart chart = new PieChart(activity);

        PieDataSet dataSet = new PieDataSet(pieEntries, "asd");
        PieData data = new PieData(dataSet);
        chart.setData(data);

//        chart.setRotationEnabled(false);
//        chart.setDrawHoleEnabled(false);
//        chart.setHighlightPerTapEnabled(false);
        chart.setTouchEnabled(false);

        return chart;
    }

    private Chart createLineChart(List<Entry> entries, final List<String> labels) {
        LineChart chart = new LineChart(activity);

        LineDataSet dataSet = new LineDataSet(entries, "asd");
        LineData data = new LineData(dataSet);
        chart.setData(data);

        chart.setHighlightPerDragEnabled(false);

        if(labels != null) {
            IAxisValueFormatter formatter = new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    return labels.get((int) value);
                }
            };

            XAxis xAxis = chart.getXAxis();
            xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
            xAxis.setValueFormatter(formatter);
        }

        chart.setHighlightPerTapEnabled(false);

        return chart;
    }

    private Chart createBarChart(List<Entry> entries,  final List<String> labels) {
        List<BarEntry> barEntries = new ArrayList<>();
        for(Entry entry: entries) {
            barEntries.add(new BarEntry(entry.getX(), entry.getY()));
        }
        BarChart chart = new BarChart(activity);

        BarDataSet dataSet = new BarDataSet(barEntries, "asd");
        BarData data = new BarData(dataSet);
        chart.setData(data);

        chart.setHighlightPerDragEnabled(false);

        if(labels != null) {
            IAxisValueFormatter formatter = new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    return labels.get((int) value);
                }
            };

            XAxis xAxis = chart.getXAxis();
            xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
            xAxis.setValueFormatter(formatter);
        }

        chart.setHighlightPerTapEnabled(false);

        return chart;
    }

    public void goBackInHistory(ChartItem chartItem) {
        System.out.println("go back");
        if(chartItem.location == Location.Top) {
            if(historyCurrentState == 0) {
                return;
            }

            historyCurrentState -= 1;
            ChartItem chartItemOld = listHistory.get(historyCurrentState);

            chartItem.location = Location.Bottom;

            final View srcView  = chartItem.chart;
//        final TextView srcView  = (TextView) activity.findViewById(R.id.textView);
            final RelativeLayout destContainer  = (RelativeLayout) activity.findViewById(R.id.layout_graph_bottom);
            final LinearLayout rootView  = (LinearLayout) activity.findViewById(R.id.rootView);

            final float position_y = getAbsY( srcView );

            RelativeLayout parent = (RelativeLayout) srcView.getParent();
            parent.removeView(srcView);

            int dpi = (int) (50 * activity.getResources().getDisplayMetrics().density);
//        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(dpi, dpi);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
//        lparams.setMargins(0,(int) (10 * activity.getResources().getDisplayMetrics().density),0,0);
            srcView.setLayoutParams(params);
//        destView.setBackgroundColor(activity.getResources().getColor(android.R.color.holo_blue_bright));
            destContainer.addView(srcView);

            final ViewTreeObserver observer = srcView.getViewTreeObserver();

            observer.addOnPreDrawListener( new ViewTreeObserver.OnPreDrawListener() {
                                               @Override
                                               public boolean onPreDraw() {
                                                   observer.removeOnPreDrawListener( this );
                                                   srcView.setTranslationY(  position_y - getAbsY( srcView ));
                                                   rootView.getOverlay().add( srcView );
                                                   srcView.animate().translationX( 0 ).translationY( 0 )
                                                           .setInterpolator( new DecelerateInterpolator( 2 ) )
                                                           .setDuration( 300 )
                                                           .withEndAction( new Runnable() {
                                                               @Override
                                                               public void run() {
                                                                   rootView.getOverlay().remove( srcView );
                                                                   destContainer.addView( srcView );
                                                               }
                                                           } );
                                                   return true;
                                               }
                                           }
            );
        }
    }

    public void goForwardInHistory(ChartItem chartItem) {
        System.out.println("go forward");

        if(chartItem.location == Location.Bottom) {
            if(historyCurrentState == listHistory.size() - 1) {
                return;
            }

            historyCurrentState += 1;
            ChartItem chartItemOld = listHistory.get(historyCurrentState);

            chartItem.location = Location.Top;

            final Chart srcView  = chartItem.chart;
//        final TextView srcView  = (TextView) activity.findViewById(R.id.textView);
            final RelativeLayout destContainer  = (RelativeLayout) activity.findViewById(R.id.layout_graph_top);
            final LinearLayout rootView  = (LinearLayout) activity.findViewById(R.id.rootView);

            final float position_y = getAbsY( srcView );

            RelativeLayout parent = (RelativeLayout) srcView.getParent();
            srcView.setOnChartGestureListener(null);
//            System.out.println(parent);
                parent.removeView(srcView);
//
            int dpi = (int) (50 * activity.getResources().getDisplayMetrics().density);
        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(dpi, dpi);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
//        lparams.setMargins(0,(int) (10 * activity.getResources().getDisplayMetrics().density),0,0);
            srcView.setLayoutParams(params);
//        destView.setBackgroundColor(activity.getResources().getColor(android.R.color.holo_blue_bright));
            destContainer.addView(srcView);

            final ViewTreeObserver observer = srcView.getViewTreeObserver();

            observer.addOnPreDrawListener( new ViewTreeObserver.OnPreDrawListener() {
                                               @Override
                                               public boolean onPreDraw() {
                                                   observer.removeOnPreDrawListener( this );
                                                   srcView.setTranslationY(  position_y - getAbsY( srcView ));
                                                   rootView.getOverlay().add( srcView );
                                                   srcView.animate().translationX( 0 ).translationY( 0 )
                                                           .setInterpolator( new DecelerateInterpolator( 2 ) )
                                                           .setDuration( 300 )
                                                           .withEndAction( new Runnable() {
                                                               @Override
                                                               public void run() {
                                                                   rootView.getOverlay().remove( srcView );
                                                                   destContainer.addView( srcView );
                                                               }
                                                           } );
                                                   return true;
                                               }
                                           }
            );
        }
    }

    public void nextLayout(ChartItem chartItem) {
        Class chartType = chartItem.chartType;
//         Toast.makeText(activity, "next", Toast.LENGTH_SHORT).show();

        int index = listChartTypes.indexOf(chartType);
        if(++index == listChartTypes.size()) {
            index = 0;
        }
        this.addGraph(chartItem.entries, listChartTypes.get(index), chartItem.location, chartItem.columns, false);
    }
    public void previousLayout(ChartItem chartItem) {
        Class chartType = chartItem.chartType;
//        Toast.makeText(activity, "previous", Toast.LENGTH_SHORT).show();
        int index = listChartTypes.indexOf(chartType);
        if(--index == - 1) {
            index = listChartTypes.size() - 1;
        }
        this.addGraph(chartItem.entries, listChartTypes.get(index), chartItem.location, chartItem.columns, false);
    }

    private float getAbsY( View view ) {
        if( view.getParent() == view.getRootView() ) {
            return view.getY();
        } else {
            return view.getY() + getAbsY( ( View )view.getParent() );
        }
    }

    public enum Location {
        Top, Bottom
    }

    public class ChartItem {
        public Chart chart;
        public Class chartType;
        public List<Entry> entries;
        public List<String> columns;
        public Location location;

        public ChartItem(Chart chart, Class<BarChart> chartType, List<Entry> entries, List<String> columns, Location location) {
            this.chart = chart;
            this.chartType = chartType;
            this.entries = entries;
            this.columns = columns;
            this.location = location;
        }
    }
}
