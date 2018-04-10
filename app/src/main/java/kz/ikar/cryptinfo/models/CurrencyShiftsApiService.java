package kz.ikar.cryptinfo.models;

import com.google.gson.JsonObject;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by User on 09.04.2018.
 */

public interface CurrencyShiftsApiService {
    @GET("v1/bpi/historical/close.json")
    Call<JsonObject> getCurrencyShifts(
            @Query("currency") String currency,
            @Query("start") String start,
            @Query("end") String end
    );
}
