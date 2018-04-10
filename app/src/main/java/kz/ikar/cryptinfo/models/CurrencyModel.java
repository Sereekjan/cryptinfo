package kz.ikar.cryptinfo.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by User on 09.04.2018.
 */

public class CurrencyModel {

    private String disclaimer;
    @SerializedName("bpi")
    @Expose
    private Bpi bpi;

    public Bpi getBpi() {
        return bpi;
    }

    public void setBpi(Bpi bpi) {
        this.bpi = bpi;
    }

}