package kz.ikar.cryptinfo;


import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemSelected;
import kz.ikar.cryptinfo.models.BpiShifts;
import kz.ikar.cryptinfo.models.CurrencyApiService;
import kz.ikar.cryptinfo.models.CurrencyModel;
import kz.ikar.cryptinfo.models.CurrencyShiftsApiService;
import kz.ikar.cryptinfo.models.EUR;
import kz.ikar.cryptinfo.models.KZT;
import kz.ikar.cryptinfo.models.RetroClient;
import kz.ikar.cryptinfo.models.USD;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class CurrencyFragment extends Fragment {
    @BindView(R.id.spinner_currency)
    MaterialSpinner spinnerCurrency;

    @BindView(R.id.tv_currency)
    TextView tvCurrency;

    @BindView(R.id.tv_graph_title)
    TextView tvGraphTitle;

    @BindView(R.id.tab)
    TabLayout tabLayout;

    @BindView(R.id.view_pager)
    ViewPager viewPager;

    Handler mainHandler;
    public static String currentCurrency = "usd";

    public CurrencyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_currency, container, false);

        ButterKnife.bind(this, rootView);

        mainHandler = new Handler(Looper.getMainLooper());

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        spinnerCurrency.setItems(Arrays.asList("BTC к USD", "BTC к EUR", "BTC к KZT"));

        loadCurrency();

        setupViewPager();

        spinnerCurrency.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                switch (position) {
                    case 0:
                        currentCurrency = "usd";
                        break;
                    case 1:
                        currentCurrency = "eur";
                        break;
                    case 2:
                        currentCurrency = "kzt";
                        break;
                }
                loadCurrency();
                setupViewPager();
            }
        });
    }

    private void setupViewPager() {
        TabViewPagerAdapter adapter = new TabViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new GraphFragment().setPeriod(GraphFragment.GraphPeriod.Week), "Неделя");
        adapter.addFragment(new GraphFragment().setPeriod(GraphFragment.GraphPeriod.Month), "Месяц");
        adapter.addFragment(new GraphFragment().setPeriod(GraphFragment.GraphPeriod.Year), "Год");
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void loadCurrency() {
        CurrencyApiService api = RetroClient.getApiService();
        Call<CurrencyModel> call = api.getCurrency(currentCurrency);
        call.enqueue(new Callback<CurrencyModel>() {
            @Override
            public void onResponse(Call<CurrencyModel> call, final Response<CurrencyModel> response) {
                if (response.isSuccessful()) {
                    CurrencyModel currency = response.body();
                    String strCurrency = "1 BTC = ";
                    switch (currentCurrency) {
                        case "usd":
                            USD usd = currency.getBpi().getUSD();
                            strCurrency += usd.getRate() + " " + usd.getCode();
                            break;
                        case "eur":
                            EUR eur = currency.getBpi().getEUR();
                            strCurrency += eur.getRate() + " " + eur.getCode();
                            break;
                        case "kzt":
                            KZT kzt = currency.getBpi().getKZT();
                            strCurrency += kzt.getRate() + " " + kzt.getCode();
                            break;
                    }
                    final String finalStrCurrency = strCurrency;
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            tvCurrency.setText(finalStrCurrency);
                            tvGraphTitle.setText(currentCurrency.toUpperCase());
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<CurrencyModel> call, Throwable t) {
                Toast.makeText(tvCurrency.getContext(), "Ошибка подключения", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
