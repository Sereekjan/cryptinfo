package kz.ikar.cryptinfo.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by User on 09.04.2018.
 */

public class Bpi {

    @SerializedName("USD")
    @Expose
    private USD uSD;
    @SerializedName("EUR")
    @Expose
    private EUR eUR;
    @SerializedName("KZT")
    @Expose
    private KZT kZT;

    public USD getUSD() {
        return uSD;
    }

    public void setUSD(USD uSD) {
        this.uSD = uSD;
    }

    public KZT getKZT() {
        return kZT;
    }

    public void setKZT(KZT kZT) {
        this.kZT = kZT;
    }

    public EUR getEUR() {
        return eUR;
    }

    public void setEUR(EUR eUR) {
        this.eUR = eUR;
    }
}
