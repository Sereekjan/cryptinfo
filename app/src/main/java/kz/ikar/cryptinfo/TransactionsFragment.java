package kz.ikar.cryptinfo;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import kz.ikar.cryptinfo.models.RetroClient;
import kz.ikar.cryptinfo.models.TransactionModel;
import kz.ikar.cryptinfo.models.TransactionsService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class TransactionsFragment extends Fragment {

    @BindView(R.id.rv_transactions)
    RecyclerView rvTransactions;

    TransactionsAdapter adapter;
    Handler mainHander;

    public TransactionsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_transactions, container, false);

        ButterKnife.bind(this, rootView);

        mainHander = new Handler(Looper.getMainLooper());

        rvTransactions.setLayoutManager(new LinearLayoutManager(getActivity()));

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadTransactions();
    }

    private void loadTransactions() {
        TransactionsService api = RetroClient.getTransactionsService();
        Call<List<TransactionModel>> call = api.getTransactions();
        call.enqueue(new Callback<List<TransactionModel>>() {
            @Override
            public void onResponse(Call<List<TransactionModel>> call, Response<List<TransactionModel>> response) {
                if (response.isSuccessful()) {
                    List<TransactionModel> transactionsData = response.body();
                    if (transactionsData.size() > 500) {
                        transactionsData = transactionsData.subList(0, 500);
                    }
                    adapter = new TransactionsAdapter(transactionsData, new TransactionsAdapter.OnItemListener() {
                        @Override
                        public void onItemClicked(TransactionModel transaction) {
                            Intent intent = new Intent(getActivity(), TransactionInfoActivity.class);
                            intent.putExtra("TransactionObject", transaction);
                            startActivity(intent);
                        }
                    });
                    mainHander.post(new Runnable() {
                        @Override
                        public void run() {
                            rvTransactions.setAdapter(adapter);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<TransactionModel>> call, Throwable t) {
                Toast.makeText(rvTransactions.getContext(), "Ошибка подключения", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
