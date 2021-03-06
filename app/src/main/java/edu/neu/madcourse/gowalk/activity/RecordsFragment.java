package edu.neu.madcourse.gowalk.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import java.sql.Date;
import java.util.ArrayList;
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
import lecho.lib.hellocharts.view.ColumnChartView;

public class RecordsFragment extends Fragment {
    public static final int COLOR_BLUE = Color.parseColor("#33B5E5");
    public static final int COLOR_VIOLET = Color.parseColor("#AA66CC");
    public static final int COLOR_GREEN = Color.parseColor("#99CC00");
    public static final int COLOR_ORANGE = Color.parseColor("#FFBB33");
    public static final int COLOR_RED = Color.parseColor("#FF4444");
    public static final int[] COLORS = new int[]{COLOR_BLUE, COLOR_VIOLET, COLOR_GREEN, COLOR_ORANGE, COLOR_RED};
    private static final String[] DAYS = {"SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};

    static final String ARG_INTERVAL = "interval";
    private ColumnChartView columnChartView;
    private ColumnChartData columnChartData;
    private boolean hasAxes = true;
    private boolean hasLabels = false;
    private boolean hasLabelForSelected = true;
    private TextView textView;
    private ImageView imageView;

    private DailyStepViewModel dailyStepViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() != null) {
            dailyStepViewModel = ViewModelProviders.of(getActivity()).get(DailyStepViewModel.class);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_records, container, false);
        columnChartView = rootView.findViewById(R.id.column_chart);
        textView = rootView.findViewById(R.id.no_data_text_view);
        imageView = rootView.findViewById(R.id.no_data_image);
        textView.setVisibility(View.INVISIBLE);
        imageView.setVisibility(View.INVISIBLE);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        assert args != null;
        String interval = args.getString(ARG_INTERVAL);
        if (dailyStepViewModel != null) {
            if ("WEEKLY".equals(interval)) {
                dailyStepViewModel.getWeeklyStepRecordsLiveData().observe(this, data -> generateColumnData(data, interval));
            } else {    // monthly
                dailyStepViewModel.getMonthlyStepRecordsLiveData().observe(this, data -> generateColumnData(data, interval));
            }
        }
    }

    private void setNoDataView() {
        columnChartView.setVisibility(View.INVISIBLE);
        textView.setText(R.string.no_chart_data);
        textView.setVisibility(View.VISIBLE);
        textView.setTextSize(20);
        imageView.setVisibility(View.VISIBLE);
    }

    private void generateColumnData(List<DailyStep> dataList, String interval) {
        if (dataList.isEmpty()) {
            setNoDataView();
            return;
        }

        int numColumns = dataList.size();
        int numSubColumns = 1;

        List<AxisValue> xAxisValues = new ArrayList<>();
        List<Column> columns = new ArrayList<>();
        List<SubcolumnValue> values;
        Calendar calendar = Calendar.getInstance();

        for (int i = 0; i < numColumns; i++) {
            values = new ArrayList<>();
            for (int j = 0; j < numSubColumns; j++) {
                values.add(new SubcolumnValue(dataList.get(i).getStepCount(), COLORS[i % COLORS.length]));
            }
            calendar.setTime(Date.valueOf(dataList.get(i).getDate()));
            if ("WEEKLY".equals(interval)) {
                xAxisValues.add(new AxisValue(i).setLabel(DAYS[calendar.get(Calendar.DAY_OF_WEEK) - 1]));
            } else {
                String label = (calendar.get(Calendar.MONTH) + 1) + "/" + (calendar.get(Calendar.DAY_OF_MONTH));
                xAxisValues.add(new AxisValue(i).setLabel(label));
            }

            Column column = new Column(values);
            column.setHasLabels(hasLabels);
            column.setHasLabelsOnlyForSelected(hasLabelForSelected);
            columns.add(column);
        }

        columnChartData = new ColumnChartData(columns);

        if (hasAxes) {
            columnChartData.setAxisXBottom(new Axis(xAxisValues)
                    .setTextSize(11).setMaxLabelChars(4).setTextColor(Color.BLACK));
            columnChartData.setAxisYLeft(new Axis()
                    .setTextSize(11).setMaxLabelChars(5).setTextColor(Color.BLACK));
        } else {
            columnChartData.setAxisXBottom(null);
            columnChartData.setAxisYLeft(null);
        }

        columnChartView.setColumnChartData(columnChartData);
        columnChartView.setVisibility(View.VISIBLE);
    }


}
