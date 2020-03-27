package edu.neu.madcourse.gowalk.activity;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import edu.neu.madcourse.gowalk.R;
import edu.neu.madcourse.gowalk.model.DailyStep;
import edu.neu.madcourse.gowalk.viewmodel.DailyStepViewModel;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;

public class RecordsFragment extends Fragment {
    private static final String LOG_TAG = "Record Fragment";
    static final String ARG_INTERVAL = "interval";
    static final String ARG_DATA = "data";
    private ColumnChartView columnChartView;
    private ColumnChartData columnChartData;
    private boolean hasAxes = true;
    private boolean hasLabels = true;
    private boolean hasLabelForSelected = false;
    private static final String[] DAYS = {"MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"};
    private static final String[] HOURS = {"12 AM", "6 AM", "12 PM", "6 PM"};
    //TODO: this is just for testing, need to remove after integrating with live data
    private static final List<DailyStep> weeklyData = Arrays.asList(new DailyStep(Date.valueOf("2020-03-20"), 10000),
            new DailyStep(Date.valueOf("2020-03-21"), 6000),
            new DailyStep(Date.valueOf("2020-03-22"), 5000),
            new DailyStep(Date.valueOf("2020-03-23"), 7000),
            new DailyStep(Date.valueOf("2020-03-24"), 1000),
            new DailyStep(Date.valueOf("2020-03-25"), 10000),
            new DailyStep(Date.valueOf("2020-03-26"), 8000));

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_records, container, false);
        columnChartView = rootView.findViewById(R.id.column_chart);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        generateColumnData(args.getString(ARG_INTERVAL));
//        ((ColumnChartView) view.findViewById(R.id.column_chart))
//                .setText(Integer.toString(args.getInt(ARG_OBJECT)));
    }

    //TODO: use live data from view model and implement daily and monthly helpers
    private void generateColumnData(String interval) {
        switch (interval) {
            case "DAILY":
                generateWeeklyColumnData(weeklyData);
                break;
            case "WEEKLY":
                generateWeeklyColumnData(weeklyData);
                break;
            default:
                generateWeeklyColumnData(weeklyData);
                break;
        }
    }

    private void generateWeeklyColumnData(List<DailyStep> data) {
        int numColumns = data.size();
        int numSubColumns = 1;

        List<AxisValue> xAxisValues = new ArrayList<>();
        List<Column> columns = new ArrayList<>();
        List<SubcolumnValue> values;
        Calendar calendar = Calendar.getInstance();

        for (int i = 0; i < numColumns; i++) {
            values = new ArrayList<>();
            for (int j = 0; j < numSubColumns; j++) {
                values.add(new SubcolumnValue(data.get(i).getStepCount(), ChartUtils.pickColor()));
            }
            calendar.setTime(data.get(i).getDate());
            xAxisValues.add(new AxisValue(i).setLabel(DAYS[calendar.get(Calendar.DAY_OF_WEEK) - 1]));
            Column column = new Column(values);
            column.setHasLabels(hasLabels);
            column.setHasLabelsOnlyForSelected(hasLabelForSelected);
            columns.add(column);
        }

        columnChartData = new ColumnChartData(columns);

        if (hasAxes) {
            columnChartData.setAxisXBottom(new Axis(xAxisValues)
                    .setHasLines(true).setTextSize(11).setMaxLabelChars(3).setTextColor(Color.BLACK));
            columnChartData.setAxisYLeft(new Axis().
                    setTextSize(11).setMaxLabelChars(5).setTextColor(Color.BLACK));
        } else {
            columnChartData.setAxisXBottom(null);
            columnChartData.setAxisYLeft(null);
        }

        columnChartView.setColumnChartData(columnChartData);
        columnChartView.setVisibility(View.VISIBLE);
    }


}
