package kz.ikar.cryptinfo;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.view.MenuItem;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import kz.ikar.cryptinfo.models.TransactionModel;

public class TransactionInfoActivity extends AppCompatActivity {
    @BindView(R.id.tv_btc_amount)
    TextView tvBtcAmount;
    @BindView(R.id.tv_usd_amount)
    TextView tvUsdAmount;
    @BindView(R.id.tv_type)
    TextView tvType;
    @BindView(R.id.tv_currency)
    TextView tvCurrency;
    @BindView(R.id.tv_date)
    TextView tvDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_info);

        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        TransactionModel transaction = getIntent().getParcelableExtra("TransactionObject");
        actionBar.setTitle("#" + transaction.getTid());

        showTransactionInfo(transaction);
    }

    private void showTransactionInfo(TransactionModel transaction) {
        tvBtcAmount.setText(transaction.getAmount());
        float btcAmount = Float.parseFloat(transaction.getAmount());
        float btcCurrency = Float.parseFloat(transaction.getPrice());
        float usdAmount = btcAmount * btcCurrency;
        Locale currentLocale = new Locale("ru", "KZ");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            tvCurrency.setText(
                    Html.fromHtml("1 <b>BTC</b> = $" +
                                    String.format(currentLocale, "%.2f", btcCurrency) +
                                    " <b>USD</b>",
                            Html.FROM_HTML_MODE_LEGACY)
            );
        } else {
            tvCurrency.setText(
                    Html.fromHtml("1 <b>BTC</b> = $" +
                                    String.format(currentLocale, "%.2f", btcCurrency) +
                                    " <b>USD</b>")
            );
        }
        tvType.setText(transaction.getType().equals("1") ? "Продажа" : "Покупка");
        tvUsdAmount.setText("$" + String.format(currentLocale, "%.2f", usdAmount) + "  USD");
        SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM yyyy 'в' HH:mm:ss", currentLocale);
        tvDate.setText(format.format(transaction.getDateParsed()));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return false;
    }
}
