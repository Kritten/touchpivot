package de.uni_weimar.touchpivot;

import android.app.Activity;
import android.opengl.Visibility;
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

import java.util.ArrayList;
import java.util.HashMap;
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
//    private ChartItem currentChart = null;
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
//        this.showStats();

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

        Chart chart = createChart(entries, BarChart.class, this.dataManager.getColumns());
        RelativeLayout layout = (RelativeLayout) activity.findViewById(R.id.layout_graph_bottom);
        chart.invalidate();
        layout.addView(chart);
        chart.invalidate();
        ChartItem chartItem = new ChartItem(chart, BarChart.class, entries, this.dataManager.getColumns());
        chartItem.changeLayoutOnly = true;
        chart.setOnTouchListener(new CustomChartTouchListener(chartItem, this, true));
//        animate(chartItem, true);
    }

    private void showStats() {
        LinearLayout wrapperStats = (LinearLayout) activity.findViewById(R.id.wrapper_stats);
        wrapperStats.setVisibility(View.VISIBLE);

        TextView textViewCountRows = (TextView) activity.findViewById(R.id.textview_count_rows);
        textViewCountRows.setText(Integer.toString(this.dataManager.getCountItems()));

        TextView textViewCountColumns = (TextView) activity.findViewById(R.id.textview_count_columns);
        textViewCountColumns.setText(Integer.toString(this.dataManager.getCountColumns()));
    }

    private void hideStats() {
        LinearLayout wrapperStats = (LinearLayout) activity.findViewById(R.id.wrapper_stats);
        wrapperStats.setVisibility(View.INVISIBLE);
    }

    private Chart createChart(List<Entry> entries, Class<BarChart> chartType, List<String> labels) {
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

        chart.getLegend().setEnabled(false);
        chart.setDescription(null);

        return chart;
    }

    public void addPreview(List<Entry> entries, Class<BarChart> chartType, List<String> labels) {
        System.out.println("preview");
        Chart chart = createChart(entries, chartType, labels);
        chart.invalidate();

        ChartItem chartItem = new ChartItem(chart, chartType, entries, labels);

        RelativeLayout layout = (RelativeLayout) activity.findViewById(R.id.layout_graph_bottom);
        layout.removeAllViews();

        layout.addView(chart);

        chartItem.getCurrentChart().setOnTouchListener(new CustomChartTouchListener(chartItem, this, true));
    }

    private void animate(ChartItem chartItem, boolean up) {
        final Chart srcView  = chartItem.getCurrentChart();
        final RelativeLayout destContainer;
        if(up) {
            destContainer  = (RelativeLayout) activity.findViewById(R.id.layout_graph_top);
        } else {

            destContainer  = (RelativeLayout) activity.findViewById(R.id.layout_graph_bottom);
        }
        final LinearLayout rootView  = (LinearLayout) activity.findViewById(R.id.rootView);

        final float position_y = getAbsY( srcView );
//        System.out.println(srcView.getParent().get);

        RelativeLayout parent = (RelativeLayout) srcView.getParent();
        srcView.setOnChartGestureListener(null);
        parent.removeView(srcView);
//        int dpi = (int) (50 * activity.getResources().getDisplayMetrics().density);
//        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(dpi, dpi);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        srcView.setLayoutParams(params);
        destContainer.addView(srcView);


        srcView.getViewTreeObserver().addOnPreDrawListener( new ViewTreeObserver.OnPreDrawListener() {
               @Override
               public boolean onPreDraw() {
                   srcView.getViewTreeObserver().removeOnPreDrawListener(this);
                   srcView.setTranslationY(  position_y - getAbsY( srcView ));
                   rootView.getOverlay().add( srcView );
                   srcView.animate().translationX( 0 ).translationY( 0 )
                           .setInterpolator( new DecelerateInterpolator( 2 ) )
                           .setDuration(300)
                           .withEndAction( new Runnable() {
                               @Override
                               public void run() {
                                   rootView.getOverlay().remove( srcView );
                                   destContainer.removeAllViews();
                                   destContainer.addView( srcView );
                               }
                           } );
                   return true;
               }
           }
        );
    }

    public ChartItem addGraph(List<Entry> entries, Class chartType, final List<String> labels, boolean addToHistory) {
        Chart chart = createChart(entries, chartType, labels);
        ChartItem chartItem = new ChartItem(chart, chartType, entries, labels);

//        if(historyCurrentState != -1) {
//            System.out.println("new");
//            listHistory.get(historyCurrentState).getCurrentChart().setVisibility(View.GONE);
//        }
//
//        if(addToHistory) {
//            historyCurrentState++;
//            listHistory.add(chartItem);
//        } else {
//            listHistory.set(historyCurrentState, chartItem);
//        }

        RelativeLayout layout = (RelativeLayout) activity.findViewById(R.id.layout_graph_bottom);
        layout.removeAllViews();
        layout.addView(chart);
        chartItem.getCurrentChart().setOnTouchListener(new CustomChartTouchListener(chartItem, this, false));

        animate(chartItem, true);

        historyCurrentState = listHistory.size();
        listHistory.add(chartItem);
//         goForwardInHistory(chartItem);

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
        if(historyCurrentState == 0) {
            return;
        }
        if(((View) chartItem.getCurrentChart().getParent()).getId() == R.id.layout_graph_bottom) {
            return;
        }
        System.out.println("go back");
//            historyCurrentState -= 1;
//            ChartItem chartItemOld = listHistory.get(historyCurrentState);

        historyCurrentState--;
        ChartItem chartItemNew = listHistory.get(historyCurrentState);
        Chart chartNew = chartItemNew.getCurrentChart();

        RelativeLayout layout = (RelativeLayout) activity.findViewById(R.id.layout_graph_top);
//        layout.removeAllViews();
        layout.addView(chartNew);
//        chartNew.setVisibility(View.VISIBLE);


        animate(chartItem, false);


//            final View srcView  = chartItem.getCurrentChart();
////        final TextView srcView  = (TextView) activity.findViewById(R.id.textView);
//            final RelativeLayout destContainer  = (RelativeLayout) activity.findViewById(R.id.layout_graph_bottom);
//            final LinearLayout rootView  = (LinearLayout) activity.findViewById(R.id.rootView);
//
//        System.out.println(getAbsY( srcView ));
//            final float position_y = getAbsY( srcView );
//
//            RelativeLayout parent = (RelativeLayout) srcView.getParent();
//            parent.removeView(srcView);
//
//            int dpi = (int) (50 * activity.getResources().getDisplayMetrics().density);
////        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(dpi, dpi);
//
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
////        lparams.setMargins(0,(int) (10 * activity.getResources().getDisplayMetrics().density),0,0);
//            srcView.setLayoutParams(params);
////        destView.setBackgroundColor(activity.getResources().getColor(android.R.color.holo_blue_bright));
//            destContainer.addView(srcView);
//
//            final ViewTreeObserver observer = srcView.getViewTreeObserver();
//
//            observer.addOnPreDrawListener( new ViewTreeObserver.OnPreDrawListener() {
//                   @Override
//                   public boolean onPreDraw() {
//                       observer.removeOnPreDrawListener( this );
//                       srcView.setTranslationY(  position_y - getAbsY( srcView ));
//                       rootView.getOverlay().add( srcView );
//                       srcView.animate().translationX( 0 ).translationY( 0 )
//                               .setInterpolator( new DecelerateInterpolator( 2 ) )
//                               .setDuration( 300 )
//                               .withEndAction( new Runnable() {
//                                   @Override
//                                   public void run() {
//                                       rootView.getOverlay().remove( srcView );
//                                       destContainer.addView( srcView );
//                                       showStats();
//                                   }
//                               } );
//                       return true;
//                   }
//               }
//            );
    }

    public void goForwardInHistory(ChartItem chartItem) {
        if(historyCurrentState == listHistory.size() - 1) {
            return;
        }
        if(((View) chartItem.getCurrentChart().getParent()).getId() == R.id.layout_graph_top) {
            return;
        }

        System.out.println("go forward");
//        hideStats();


        historyCurrentState++;
        try {
            ChartItem chartItemNew = listHistory.get(historyCurrentState + 1);
            Chart chartNew = chartItemNew.getCurrentChart();
            RelativeLayout layout = (RelativeLayout) activity.findViewById(R.id.layout_graph_bottom);
            layout.addView(chartNew);

        } catch (IndexOutOfBoundsException e) {
            System.out.println("ECEPTION!!!");
        }


        animate(chartItem, true);

//            final Chart srcView  = chartItem.getCurrentChart();
////        final TextView srcView  = (TextView) activity.findViewById(R.id.textView);
//            final RelativeLayout destContainer  = (RelativeLayout) activity.findViewById(R.id.layout_graph_top);
//            final LinearLayout rootView  = (LinearLayout) activity.findViewById(R.id.rootView);
//
//        System.out.println(getAbsY( srcView ));
//            final float position_y = getAbsY( srcView );
//
//            RelativeLayout parent = (RelativeLayout) srcView.getParent();
//            srcView.setOnChartGestureListener(null);
////            System.out.println(parent);
//                parent.removeView(srcView);
////
//            int dpi = (int) (50 * activity.getResources().getDisplayMetrics().density);
//        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(dpi, dpi);
//
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
////        lparams.setMargins(0,(int) (10 * activity.getResources().getDisplayMetrics().density),0,0);
//            srcView.setLayoutParams(params);
////        destView.setBackgroundColor(activity.getResources().getColor(android.R.color.holo_blue_bright));
//            destContainer.addView(srcView);
//
//            final ViewTreeObserver observer = srcView.getViewTreeObserver();
//
//            observer.addOnPreDrawListener( new ViewTreeObserver.OnPreDrawListener() {
//                   @Override
//                   public boolean onPreDraw() {
//                       observer.removeOnPreDrawListener( this );
//                       srcView.setTranslationY(  position_y - getAbsY( srcView ));
//                       rootView.getOverlay().add( srcView );
//                       srcView.animate().translationX( 0 ).translationY( 0 )
//                               .setInterpolator( new DecelerateInterpolator( 2 ) )
//                               .setDuration( 300 )
//                               .withEndAction( new Runnable() {
//                                   @Override
//                                   public void run() {
//                                       rootView.getOverlay().remove( srcView );
//                                       destContainer.addView( srcView );
//                                   }
//                               } );
//                       return true;
//                   }
//               }
//            );
    }

    private float getAbsY( View view ) {
        if( view.getParent() == view.getRootView() ) {
            return view.getY();
        } else {
            return view.getY() + getAbsY( ( View )view.getParent() );
        }
    }

    public class ChartItem {
        public HashMap<Class, Chart> charts = new HashMap<>();
        public Class chartType;
        public List<Entry> entries;
        public List<String> labels;
        public boolean changeLayoutOnly = false;

        public ChartItem(Chart chart, Class<BarChart> chartType, List<Entry> entries, List<String> labels) {
            this.charts.put(chartType, chart);
            this.chartType = chartType;
            this.entries = entries;
            this.labels = labels;
        }

        Chart getCurrentChart() {
            return charts.get(chartType);
        }

        public void switchLayout(boolean nextChartType) {
            Chart currentChart = getCurrentChart();

            int index = listChartTypes.indexOf(chartType);

            if(nextChartType) {
                if(++index == listChartTypes.size()) {
                    index = 0;
                }
            } else {
                if(--index == - 1) {
                    index = listChartTypes.size() - 1;
                }
            }
            chartType = listChartTypes.get(index);

            if(!charts.containsKey(chartType)) {
                System.out.println("CREATING GRAPH");
                createChart(currentChart);
            } else {
                updateChart(currentChart);
                System.out.println("ONLY UPDATING GRAPH");
            }
        }

        private void updateChart(Chart currentChart) {
            Chart chart = charts.get(chartType);

//            RelativeLayout parent = (RelativeLayout) currentChart.getParent();
//            RelativeLayout layout;
//            if current chart is drawn at bottom
//            if(parent.getId() == R.id.layout_graph_bottom) {
//                layout = (RelativeLayout) activity.findViewById(R.id.layout_graph_bottom);
//            } else {
//                layout = (RelativeLayout) activity.findViewById(R.id.layout_graph_top);
//            }
//            layout.addView(chart);
            chart.setVisibility(View.VISIBLE);
            currentChart.setVisibility(View.GONE);
        }

        private void createChart(Chart currentChart) {
            Chart chart = null;
            switch (chartType.getSimpleName()) {
                case "BarChart":
                    chart = createBarChart(entries, labels);
                    break;
                case "LineChart":
                    chart = createLineChart(entries, labels);
                    break;
                case "PieChart":
                    chart = createPieChart(entries);
                    break;
            }

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            chart.setLayoutParams(params);

            chart.getLegend().setEnabled(false);
            chart.setDescription(null);

            chart.invalidate();

            RelativeLayout parent = (RelativeLayout) currentChart.getParent();
            RelativeLayout layout;
//            if current chart is drawn at bottom
            if(parent.getId() == R.id.layout_graph_bottom) {
                layout = (RelativeLayout) activity.findViewById(R.id.layout_graph_bottom);
            } else {
                layout = (RelativeLayout) activity.findViewById(R.id.layout_graph_top);
            }
            layout.addView(chart);

            currentChart.setVisibility(View.GONE);
            chart.setOnTouchListener(new CustomChartTouchListener(this, GraphManager.this, changeLayoutOnly));

            charts.put(chartType, chart);
        }
    }
}
