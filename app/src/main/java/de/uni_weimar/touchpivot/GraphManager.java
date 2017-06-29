package de.uni_weimar.touchpivot;

import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
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
import java.util.List;

/**
 * Created by micro on 24.06.2017.
 */

class GraphManager implements OnChartGestureListener {
    private Activity activity = null;
    private List<ChartItem> listHistory = new ArrayList<>();
    private ChartItem currentChart = null;
    private List<Class> listChartTypes = new ArrayList<>();

    public GraphManager(Activity activity) {
        this.activity = activity;
//        listChartTypes.add(PieChart.class);
        listChartTypes.add(BarChart.class);
        listChartTypes.add(LineChart.class);
    }

    public void showStats(DataManager dataManager) {
        TextView textViewCountRows = (TextView) activity.findViewById(R.id.textview_count_rows);
        textViewCountRows.setText(Integer.toString(dataManager.getCountItems()));

        TextView textViewCountColumns = (TextView) activity.findViewById(R.id.textview_count_columns);
        textViewCountColumns.setText(Integer.toString(dataManager.getCountColumns()));
    }

    public Chart getCurrentChart() {
        return currentChart.chart;
    }

    public void renderGraphBottom(List<Entry> entries, final List<String> labels, Class chartType) {
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

        RelativeLayout layout = (RelativeLayout) activity.findViewById(R.id.layout_graph_bottom);
        layout.addView(chart);
        chart.setOnChartGestureListener(this);
        chart.getLegend().setEnabled(false);
        chart.setDescription(null);

        chart.invalidate();

        if(currentChart != null) {
            currentChart.chart.setVisibility(View.GONE);
        }

        ChartItem chartItem = new ChartItem(chart, chartType, entries, labels);
        currentChart = chartItem;

        listHistory.add(chartItem);
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

    public void renderGraphBottom(List<Entry> entries) {
        this.renderGraphBottom(entries, null, BarChart.class);
    }
    public void renderGraphBottom(List<Entry> entries, ArrayList<String> labels) {
        this.renderGraphBottom(entries, labels, BarChart.class);
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
    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
//        System.out.println(Float.toString(velocityX)+" "+ Float.toString(velocityY));
//        Toast.makeText(activity, Float.toString(velocityX)+" "+ Float.toString(velocityY), Toast.LENGTH_SHORT).show();
//        horizontal swipe
        if(Math.abs(velocityX) > Math.abs(velocityY)) {
            if(velocityX < 0.0) {
                this.nextLayout();
            } else {
                this.previousLayout();
            }
        } else {
        }
    }

    private void nextLayout() {
        Class chartType = currentChart.chartType;
//         Toast.makeText(activity, "next", Toast.LENGTH_SHORT).show();

        int index = listChartTypes.indexOf(chartType);
        if(++index == listChartTypes.size()) {
            index = 0;
        }
        this.renderGraphBottom(currentChart.entries, currentChart.columns, listChartTypes.get(index));
    }
    private void previousLayout() {
        Class chartType = currentChart.chartType;
//        Toast.makeText(activity, "previous", Toast.LENGTH_SHORT).show();
        int index = listChartTypes.indexOf(chartType);
        if(--index == - 1) {
            index = listChartTypes.size() - 1;
        }
        this.renderGraphBottom(currentChart.entries, currentChart.columns, listChartTypes.get(index));
    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {
    }

    private class ChartItem {
        public Chart chart;
        public Class chartType;
        public List<Entry> entries;
        public List<String> columns;

        public ChartItem(Chart chart, Class<BarChart> chartType, List<Entry> entries, List<String> columns) {
            this.chart = chart;
            this.chartType = chartType;
            this.entries = entries;
            this.columns = columns;
        }
    }
}
