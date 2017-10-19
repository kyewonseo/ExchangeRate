package net.cj.exchangerate;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class DoitActivity extends Activity {

    private static final String TAG = DoitActivity.class.getSimpleName();
    private EditText et_dollar;
    private Button exchange_btn;
    private TextView tv_total;

    private static String url = "https://api.manana.kr/exchange/rate.json";
    private double dollar_price = 0.0;
    private double amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doit);

        et_dollar = (EditText) findViewById(R.id.et_dollar);
        exchange_btn = (Button) findViewById(R.id.exchange_btn);
        tv_total = (TextView) findViewById(R.id.tv_total);

        et_dollar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String textDollar = s.toString();

                if (s.length() <= 0) {
                    dollar_price = 0.0;
                } else {
                    dollar_price = Double.valueOf(textDollar);
                }



            }
        });


        exchange_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                exchangeRate();
                new GetApiInfo().execute();
            }
        });
    }

    private double exchangeRate(double dollar_price, double rate) {

        Double total = dollar_price * rate;
        return total;
    }

    //AsyncTask를 쓰는 이유는 안드로이드의 메인쓰레드가 한개 이기 때문
    private class GetApiInfo extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);


            if (jsonStr != null) {
                try {

                    JSONArray jsonArray = new JSONArray(jsonStr);
                    String jsonUSD = jsonArray.getString(1);
                    Gson gson = new Gson();
                    USD usd = gson.fromJson(jsonUSD, USD.class);

                    amount = exchangeRate(dollar_price, Double.valueOf(usd.getRate()));


//                    Log.e(TAG, "date => "+ usd.getDate());
//                    Log.e(TAG, "name => "+ usd.getName());
//                    Log.e(TAG, "rate => "+ usd.getRate());

//                    Log.e("test =>", jsonArray.getString(1));

//                    for (int i = 0; i < jsonArray.length(); i++) {
//                        strArr[i] = jsonArray.getString(i);
//                    }
//                    Log.e("test =>", Arrays.toString(strArr));

                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            tv_total.setText(String.valueOf(dollar_price) + "달러 는 "
                    + String.format("%.2f", amount) + "원 입니다.");

        }

    }
}


