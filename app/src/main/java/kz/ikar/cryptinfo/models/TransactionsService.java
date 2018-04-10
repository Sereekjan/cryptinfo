package kz.ikar.cryptinfo.models;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by User on 10.04.2018.
 */

public interface TransactionsService {
    @GET("api/v2/transactions/btcusd/")
    Call<List<TransactionModel>> getTransactions();
}
