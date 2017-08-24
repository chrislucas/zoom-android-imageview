package app.com.xplore.explorematrix.data;

import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by r028367 on 24/08/2017.
 */

public class StockChartSampleData {

    public static class Data {
        private double price;
        private String labelData;
        public Data(double price, String labelData) {
            this.price = price;
            this.labelData = labelData;
        }
    }


    public static ArrayList<Data> getSampleData() {
        ArrayList<Data> sample = new ArrayList<>();

        return sample;
    }

}
