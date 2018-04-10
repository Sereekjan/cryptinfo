package kz.ikar.cryptinfo.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by User on 10.04.2018.
 */

public class TransactionModel implements Parcelable {

    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("tid")
    @Expose
    private String tid;
    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("amount")
    @Expose
    private String amount;

    public String getDate() {
        return date;
    }

    public Date getDateParsed() {
        long longTime = Long.parseLong(date);
        Date date = new Date(longTime * 1000);
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(date);
        dest.writeString(tid);
        dest.writeString(price);
        dest.writeString(type);
        dest.writeString(amount);
    }

    public static final Parcelable.Creator<TransactionModel> CREATOR = new Parcelable.Creator<TransactionModel>() {
        public TransactionModel createFromParcel(Parcel in) {
            return new TransactionModel(in);
        }

        public TransactionModel[] newArray(int size) {
            return new TransactionModel[size];
        }
    };

    private TransactionModel(Parcel in) {
        date = in.readString();
        tid = in.readString();
        price = in.readString();
        type = in.readString();
        amount = in.readString();
    }
}
