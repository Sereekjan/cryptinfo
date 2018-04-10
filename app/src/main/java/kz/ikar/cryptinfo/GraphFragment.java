package kz.ikar.cryptinfo;


import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import kz.ikar.cryptinfo.models.CurrencyShiftsApiService;
import kz.ikar.cryptinfo.models.RetroClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static kz.ikar.cryptinfo.CurrencyFragment.currentCurrency;


/**
 * A simple {@link Fragment} subclass.
 */
public class GraphFragment extends Fragment {
    @BindView(R.id.graph)
    GraphView graphView;

    Handler mainHandler;

    enum GraphPeriod {
        Week,
        Month,
        Year
    }

    private GraphPeriod currentPeriod;

    public GraphFragment setPeriod(GraphPeriod currentPeriod) {
        this.currentPeriod = currentPeriod;
        return this;
    }

    public GraphFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_graph, container, false);

        ButterKnife.bind(this, rootView);

        mainHandler = new Handler(Looper.getMainLooper());

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadData();
    }

    public void loadData() {
        switch (currentPeriod) {
            case Week:
                loadGraphForLastWeek();
                break;
            case Month:
                loadGraphForLastMonth();
                break;
            case Year:
                loadGraphForLastYear();
                break;
        }
    }

    private void loadGraphForLastWeek() {
        CurrencyShiftsApiService api = RetroClient.getCurrencyShiftsApiService();
        Call<JsonObject> call = api.getCurrencyShifts(currentCurrency, getWeekBeforeDate(), getTodaysDate());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    loadGraphDataForLastWeek(response.body());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(graphView.getContext(), "Ошибка подключения", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadGraphForLastMonth() {
        CurrencyShiftsApiService api = RetroClient.getCurrencyShiftsApiService();
        Call<JsonObject> call = api.getCurrencyShifts(currentCurrency, getMonthBeforeDate(), getTodaysDate());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    loadGraphDataForLastMonth(response.body());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(graphView.getContext(), "Ошибка подключения", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadGraphForLastYear() {
        CurrencyShiftsApiService api = RetroClient.getCurrencyShiftsApiService();
        Call<JsonObject> call = api.getCurrencyShifts(currentCurrency, getYearBeforeDate(), getTodaysDate());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    loadGraphDataForLastYear(response.body());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(graphView.getContext(), "Ошибка подключения", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadGraphDataForLastWeek(JsonObject jsonObject) {
        JsonObject bpiJsonObject = jsonObject.getAsJsonObject("bpi");
        final DataPoint[] points = new DataPoint[bpiJsonObject.size()];
        int iter = 0;

        for (String item : bpiJsonObject.keySet()) {
            points[iter++] = new DataPoint(
                    Double.valueOf(item.substring(8, 10)),
                    bpiJsonObject.get(item).getAsFloat()
            );
        }

        final int finalIter = iter - 1;
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                LineGraphSeries<DataPoint> series = new LineGraphSeries<>(points);
                graphView.addSeries(series);

                graphView.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity()));
                graphView.getGridLabelRenderer().setNumHorizontalLabels(points.length);

                graphView.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
                    @Override
                    public String formatLabel(double value, boolean isValueX) {
                        if (isValueX) {
                            return String.valueOf((int) value);
                        } else {
                            return super.formatLabel(value, isValueX);
                        }
                    }
                });

                graphView.getViewport().setMinX(points[0].getX());
                graphView.getViewport().setMaxX(points[finalIter].getX());
            }
        });
    }

    private void loadGraphDataForLastMonth(JsonObject jsonObject) {
        JsonObject bpiJsonObject = jsonObject.getAsJsonObject("bpi");
        float[] rates = new float[7];
        final DataPoint[] points = new DataPoint[4];
        int iterWeekRates = 0,
            iterAvgRates = 0;
        double avgDate = 0;

        for (String item : bpiJsonObject.keySet()) {
            rates[iterWeekRates++] = bpiJsonObject.get(item).getAsFloat();
            if (iterWeekRates == 4) {
                try {
                    Date date = getDateByString(item);
                    long longTime = date.getTime();
                    avgDate = longTime;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            if (iterWeekRates == 7) {
                iterWeekRates = 0;
                points[iterAvgRates++] = new DataPoint(
                        avgDate,
                        getAvgCurrency(rates));
                rates = new float[7];
            }
        }

        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                LineGraphSeries<DataPoint> series = new LineGraphSeries<>(points);
                graphView.addSeries(series);

                //graphView.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity()));
                graphView.getGridLabelRenderer().setNumHorizontalLabels(points.length);

                graphView.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
                    @Override
                    public String formatLabel(double value, boolean isValueX) {
                        if (isValueX) {
                            Date date = new Date((long)value);
                            SimpleDateFormat format = new SimpleDateFormat(
                                    "MMM d",
                                    new Locale("ru", "KZ")
                            );
                            return format.format(date);
                        } else {
                            return super.formatLabel(value, isValueX);
                        }
                    }
                });

                graphView.getViewport().setMinX(points[0].getX());
                graphView.getViewport().setMaxX(points[3].getX());

                graphView.getViewport().setScalable(true);
            }
        });
    }

    private void loadGraphDataForLastYear(JsonObject jsonObject) {
        JsonObject bpiJsonObject = jsonObject.getAsJsonObject("bpi");
        float[] rates = new float[31];
        final DataPoint[] points = new DataPoint[12];
        int iterMonthRates = 0,
                iterAvgRates = 0;
        String currMonth = null;

        for (String item : bpiJsonObject.keySet()) {
            String month = item.substring(0, 7);
            if (currMonth == null) {
                currMonth = month;
            } else if (!currMonth.equals(month)) {
                iterMonthRates = 0;
                try {
                    points[iterAvgRates++] = new DataPoint(
                            getDateByString(currMonth + "-01"),
                            getAvgCurrency(rates));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                rates = new float[31];
                currMonth = month;
            } else {
                rates[iterMonthRates++] = bpiJsonObject.get(item).getAsFloat();
            }
        }

        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                LineGraphSeries<DataPoint> series = new LineGraphSeries<>(points);
                graphView.addSeries(series);

                //graphView.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity()));
                graphView.getGridLabelRenderer().setNumHorizontalLabels(points.length);
                graphView.getGridLabelRenderer().setVerticalLabelsAlign(Paint.Align.CENTER);
                graphView.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
                    @Override
                    public String formatLabel(double value, boolean isValueX) {
                        if (isValueX) {
                            Date date = new Date((long)value);
                            SimpleDateFormat format = new SimpleDateFormat(
                                    "M",
                                    new Locale("ru", "KZ")
                            );
                            return format.format(date);
                        } else {
                            return super.formatLabel(value, isValueX);
                        }
                    }
                });

                graphView.getViewport().setMinX(points[0].getX());
                graphView.getViewport().setMaxX(points[11].getX());

                graphView.getViewport().setScalable(true);
            }
        });
    }

    private float getAvgCurrency(float[] rates) {
        float sum = 0;
        int count = 0;
        for (float rate : rates) {
            if (rate != 0) {
                sum += rate;
                count++;
            }
        }
        return sum / count;
    }

    private Date getDateByString(String str) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.parse(str);
    }

    private String getTodaysDate() {
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    private String getWeekBeforeDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.WEEK_OF_MONTH, -1);
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        Date date = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    private String getMonthBeforeDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -29);
        Date date = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    private String getYearBeforeDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -1);
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        Date date = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }
}
