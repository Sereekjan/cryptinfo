package kz.ikar.cryptinfo;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import kz.ikar.cryptinfo.models.TransactionModel;

/**
 * Created by User on 10.04.2018.
 */

public class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.TransactionsViewHolder> {
    List<TransactionModel> mItemList;
    OnItemListener listener;

    public interface OnItemListener {
        void onItemClicked(TransactionModel transaction);
    }

    public TransactionsAdapter(List<TransactionModel> mItemList, OnItemListener listener) {
        this.mItemList = mItemList;
        this.listener = listener;
    }

    @Override
    public TransactionsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction, parent, false);

        return new TransactionsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TransactionsViewHolder holder, int position) {
        holder.bind(mItemList.get(position), listener);
    }

    @Override
    public int getItemCount() {
        if (mItemList == null)
            return 0;

        return mItemList.size();
    }

    static class TransactionsViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_type)
        TextView tvType;
        @BindView(R.id.tv_date)
        TextView tvDate;
        @BindView(R.id.tv_btc_amount)
        TextView tvBtcAmount;
        @BindView(R.id.tv_btc_currency)
        TextView tvBtcCurrency;

        public TransactionsViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        public void bind(final TransactionModel transaction, final OnItemListener listener) {
            if (transaction.getType().equals("1")) {
                tvType.setText("ПРОДАЖА");
                setColorSale();
            } else {
                tvType.setText("ПОКУПКА");
            }
            //tvType.setText(transaction.getType().equals("1") ? "ПРОДАЖА" : "ПОКУПКА");
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss",
                    new Locale("ru", "KZ"));
            tvDate.setText(format.format(transaction.getDateParsed()));
            tvBtcAmount.setText(transaction.getAmount() + " BTC");
            tvBtcCurrency.setText("1 BTC = " + transaction.getPrice() + " USD");

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClicked(transaction);
                }
            });
        }

        public void setColorSale() {
            tvType.setTextColor(ContextCompat.getColor(tvType.getContext(), R.color.colorSale));
            tvDate.setTextColor(ContextCompat.getColor(tvDate.getContext(), R.color.colorSaleSubtitle));
            tvBtcAmount.setTextColor(ContextCompat.getColor(tvBtcAmount.getContext(), R.color.colorSaleTitle));
            tvBtcCurrency.setTextColor(ContextCompat.getColor(tvBtcCurrency.getContext(), R.color.colorSaleDark));
        }
    }
}
