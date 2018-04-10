package kz.ikar.cryptinfo.models;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by User on 09.04.2018.
 */

public interface CurrencyApiService {
    @GET("v1/bpi/currentprice/{currency}.json")
    Call<CurrencyModel> getCurrency(@Path("currency") String currency);
}
