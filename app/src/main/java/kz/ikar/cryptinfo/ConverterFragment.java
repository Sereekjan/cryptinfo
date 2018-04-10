package kz.ikar.cryptinfo;


import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.jaredrummler.materialspinner.MaterialSpinner;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kz.ikar.cryptinfo.models.CurrencyApiService;
import kz.ikar.cryptinfo.models.CurrencyModel;
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
public class ConverterFragment extends Fragment {

    @BindView(R.id.spinner_from)
    MaterialSpinner spinnerFrom;
    @BindView(R.id.spinner_to)
    MaterialSpinner spinnerTo;
    @BindView(R.id.et_from)
    EditText etFrom;
    @BindView(R.id.et_to)
    EditText etTo;
    @BindView(R.id.btn_convert)
    Button btnConvert;

    List<String> allCurrencies = Arrays.asList("BTC", "USD", "EUR", "KZT");
    List<String> cryptoCurrencies = Arrays.asList("BTC");
    List<String> nonCryptoCurrencies = Arrays.asList("USD", "EUR", "KZT");

    Handler mainHandler;

    public ConverterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_converter, container, false);

        ButterKnife.bind(this, rootView);

        mainHandler = new Handler(Looper.getMainLooper());

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        spinnerFrom.setItems(allCurrencies);
        spinnerTo.setItems(nonCryptoCurrencies);

        spinnerFrom.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                if (((String)view.getItems().get(position)).equals("BTC")) {
                    spinnerTo.setItems(nonCryptoCurrencies);
                } else {
                    spinnerTo.setItems(cryptoCurrencies);
                }
            }
        });

        etTo.setKeyListener(null);
    }

    @OnClick(R.id.btn_convert)
    public void onConvertButtonClick() {
        if (etFrom.getText() == null || etFrom.getText().toString().equals("")) {
            return;
        }

        int selectedIndexFrom = spinnerFrom.getSelectedIndex();
        int selectedIndexTo = spinnerTo.getSelectedIndex();

        final String currencyFrom = (String) spinnerFrom.getItems().get(selectedIndexFrom);
        final String currencyTo = (String) spinnerTo.getItems().get(selectedIndexTo);
        final float convertingValue = Float.parseFloat(etFrom.getText().toString());

        new Thread(new Runnable() {
            @Override
            public void run() {
                float currency = 0;
                if (currencyFrom.equals("BTC")) {
                    currency = getCurrency(currencyTo);
                } else {
                    currency = getCurrency(currencyFrom);
                }

                if (currency == 0)
                    return;

                float convertedValue = 0;

                if (currencyFrom.equals("BTC")) {
                    convertedValue = convertingValue * currency;
                } else {
                    convertedValue = convertingValue / currency;
                }

                final float finalConvertedValue = convertedValue;
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (!currencyTo.equals("BTC")) {
                            etTo.setText(String.format(
                                    new Locale("ru", "KZ"),
                                    "%.2f",
                                    finalConvertedValue)
                            );
                        } else {
                            etTo.setText(String.format(
                                    new Locale("ru", "KZ"),
                                    "%.8f",
                                    finalConvertedValue)
                            );
                        }
                    }
                });
            }
        }).start();


    }

    private float getCurrency(String currentCurrency) {
        CurrencyApiService api = RetroClient.getApiService();
        Call<CurrencyModel> call = api.getCurrency(currentCurrency);
        Response<CurrencyModel> response = null;

        try {
            response = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }

        if (response.isSuccessful()) {
            CurrencyModel currency = response.body();
            switch (currentCurrency) {
                case "USD":
                    USD usd = currency.getBpi().getUSD();
                    return (float) usd.getRateFloat();

                case "EUR":
                    EUR eur = currency.getBpi().getEUR();
                    return (float) eur.getRateFloat();

                case "KZT":
                    KZT kzt = currency.getBpi().getKZT();
                    return (float) kzt.getRateFloat();

            }
        }

        return 0;
    }
}