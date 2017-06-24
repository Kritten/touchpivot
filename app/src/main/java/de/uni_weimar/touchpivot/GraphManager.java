package de.uni_weimar.touchpivot;

import android.app.Activity;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by micro on 24.06.2017.
 */

class GraphManager {
    private Activity activity = null;

    public GraphManager(Activity activity) {
        this.activity = activity;
    }

    public void showStats(DataManager dataManager) {
        TextView textViewCountRows = (TextView) activity.findViewById(R.id.textview_count_rows);
        textViewCountRows.setText(Integer.toString(dataManager.getCountItems()));

        TextView textViewCountColumns = (TextView) activity.findViewById(R.id.textview_count_columns);
        textViewCountColumns.setText(Integer.toString(dataManager.getCountColumns()));
    }

    public void renderGraphBottom(List<BarEntry> entries, final ArrayList<String> labels) {
        BarChart chart = new BarChart(activity);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        chart.setLayoutParams(params);
        RelativeLayout layout = (RelativeLayout) activity.findViewById(R.id.layout_graph_bottom);
        layout.addView(chart);

        chart.getLegend().setEnabled(false);
        chart.setDescription(null);

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

        BarDataSet dataSet = new BarDataSet(entries, "asd");
        BarData barData = new BarData(dataSet);
        chart.setData(barData);
        chart.invalidate();
    }

    public void renderGraphBottom(List<BarEntry> entries) {
        this.renderGraphBottom(entries, null);
    }
}
