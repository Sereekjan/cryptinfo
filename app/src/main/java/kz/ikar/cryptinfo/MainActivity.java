package kz.ikar.cryptinfo;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import kz.ikar.cryptinfo.models.CurrencyApiService;
import kz.ikar.cryptinfo.models.CurrencyModel;
import kz.ikar.cryptinfo.models.EUR;
import kz.ikar.cryptinfo.models.KZT;
import kz.ikar.cryptinfo.models.RetroClient;
import kz.ikar.cryptinfo.models.USD;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        final ActionBar actionBar = getSupportActionBar();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentManager manager = getSupportFragmentManager();
                Fragment fragment = null;
                switch (item.getItemId()) {
                    case R.id.menu_currency:
                        fragment = new CurrencyFragment();
                        actionBar.setTitle("Курс биткоина");
                        break;
                    case R.id.menu_transactions:
                        fragment = new TransactionsFragment();
                        actionBar.setTitle("История транзакций");
                        break;
                    case R.id.menu_converter:
                        fragment = new ConverterFragment();
                        actionBar.setTitle("Ковертер валют");
                        break;
                }
                if (fragment != null)
                    manager.beginTransaction().replace(R.id.content_main, fragment).commit();

                return false;
            }
        });

        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = new CurrencyFragment();
        manager.beginTransaction().replace(R.id.content_main, fragment).commit();
    }
}
