package kz.ikar.cryptinfo.models;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by User on 09.04.2018.
 */

public class RetroClient {
    private static final String URL_CURRENCY = "https://api.coindesk.com/";

    private static final String URL_TRANSACTIONS = "https://www.bitstamp.net/";

    private static Retrofit getRetrofitCurrencyInstance() {
        return new Retrofit.Builder()
                .baseUrl(URL_CURRENCY)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private static Retrofit getRetrofitTransactionsInstance() {
        return new Retrofit.Builder()
                .baseUrl(URL_TRANSACTIONS)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static CurrencyApiService getApiService() {
        return getRetrofitCurrencyInstance().create(CurrencyApiService.class);
    }

    public static CurrencyShiftsApiService getCurrencyShiftsApiService() {
        return getRetrofitCurrencyInstance().create(CurrencyShiftsApiService.class);
    }

    public static TransactionsService getTransactionsService() {
        return getRetrofitTransactionsInstance().create(TransactionsService.class);
    }
}
