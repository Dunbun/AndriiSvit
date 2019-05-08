package com.example.taras.andriisvit;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class Converter extends AppCompatActivity {
    String fromCurrency = "EUR";
    String toCurrency = "UAH";
    MyDB db ;
    Spinner dropdownfromCurrency;
    Spinner dropdowntoCurrency;
    TextView resultOfcurrencytextView;
    EditText currencyeditView;
    ArrayList<String> items = new ArrayList<String>();
    Button oneweekBtn;
    Button onemonthBtn;
    ArrayList<Float> oneweekData = new ArrayList<Float>();
    ArrayList<Float> onemonthData = new ArrayList<Float>();
    String currencies = fromCurrency + "_" + toCurrency;
    //https://free.currencyconverterapi.com/api/v6/convert?q=EUR_UAH&compact=ultra&apiKey=667a8fab011ea833b877

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_converter);

        Button favoritesBtn = (Button)findViewById(R.id.favoritesBtn);
        Button newsBtn = (Button)findViewById(R.id.newsBtn);

        favoritesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.example.taras.andriisvit.Favorites");
                startActivity(intent);
            }
        });

        newsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.example.taras.andriisvit.News");
                startActivity(intent);
            }
        });

        dropdownfromCurrency = (Spinner) findViewById(R.id.spinner1);
        dropdowntoCurrency = (Spinner) findViewById(R.id.spinner2);
        resultOfcurrencytextView = (TextView)findViewById(R.id.resultOftextView);
        currencyeditView = (EditText)findViewById(R.id.currencyeditText);
        oneweekBtn = (Button)findViewById(R.id.W1);
        onemonthBtn = (Button)findViewById(R.id.M1);

        db = new MyDB(Converter.this);
        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.query(MyDB.TABLE_NAME, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            if(cursor != null){
                do{
                   items.add(cursor.getString(1));
                }while(cursor.moveToNext());
            }
        }
        db.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdownfromCurrency.setAdapter(adapter);
        dropdowntoCurrency.setAdapter(adapter);

        dropdownfromCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fromCurrency = items.get(position);
                Toast.makeText(Converter.this,fromCurrency,Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        dropdowntoCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                toCurrency = items.get(position);
                Toast.makeText(Converter.this,toCurrency,Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        currencyeditView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {//
                if( !currencyeditView.getText().toString().equals("")) {
                    ExchangeTask mytask = new ExchangeTask();
                    String currencies = fromCurrency + "_" + toCurrency;
                    mytask.execute("https://free.currencyconverterapi.com/api/v6/convert?q=" + currencies + "&compact=ultra&apiKey=667a8fab011ea833b877");
                }//
            }
        });


        oneweekBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getarrayweekTask myTask = new getarrayweekTask();

                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat mdformat = new SimpleDateFormat("yyyy-MM-dd");
                Date mydate = calendar.getTime();
                calendar.setTime(mydate);
                calendar.add(Calendar.DAY_OF_YEAR, 0);
                String startDate = "" + mdformat.format(calendar.getTime());
                calendar.add(Calendar.DAY_OF_YEAR, -6);
                String endDate = "" + mdformat.format(calendar.getTime());
                currencies = fromCurrency + "_" + toCurrency;

                Log.i("mytask", "startdate - " + startDate);
                Log.i("mytask", "enddate - " + endDate);
                myTask.execute("https://free.currencyconverterapi.com/api/v6/convert?q=" + currencies +"&compact=ultra&date=" + endDate + "&endDate=" + startDate +"&apiKey=667a8fab011ea833b877");


            }
        });

        onemonthBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getarraymonthTask myTask = new getarraymonthTask();

                Log.i("mytask", "fck");
                myTask.execute("https://free.currencyconverterapi.com/api/v6/convert?q=USD_UAH&compact=ultra&date=2019-03-29&endDate=2019-04-04&apiKey=667a8fab011ea833b877");

            }
        });

    }

    class ExchangeTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {

                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection)url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                int data = inputStreamReader.read();

                while(data != -1){
                    char current = (char) data;
                    result  += current;
                    data = inputStreamReader.read();

                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(result);
                result = jsonObject.getString( fromCurrency + "_" + toCurrency);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            double amount = Double.parseDouble(currencyeditView.getText().toString());
            double dresult = Double.parseDouble(result);
            dresult = dresult * amount;
            int iresult  = (int)(dresult * 100);
            dresult = iresult;
            dresult /= 100;
            result = Double.toString(dresult);

            resultOfcurrencytextView.setText(result);

            Log.i("mytask","post " + result);
            Log.i("mytask","" + fromCurrency);
            Log.i("mytask","" + toCurrency);

        }
    }

    class getarrayweekTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {

                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection)url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                int data = inputStreamReader.read();

                while(data != -1){
                    char current = (char) data;
                    result  += current;
                    data = inputStreamReader.read();

                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return result;
        }
//https://free.currencyconverterapi.com/api/v6/convert?q=USD_GBP&compact=ultra&date=2019-2-28&endDate=2019-3-4&apiKey=667a8fab011ea833b877
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            JSONObject jsonObject = null;
            JSONArray jsonArray = null;
            try {
                oneweekData.clear();

                jsonObject = new JSONObject(result);
                result = jsonObject.getString(currencies);
                jsonObject = new JSONObject(result);

                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat mdformat = new SimpleDateFormat("yyyy-MM-dd");
                Date mydate = calendar.getTime();
                calendar.setTime(mydate);

                calendar.add(Calendar.DAY_OF_YEAR, 0);
                for(int i = 0; i < 7; i++) {
                    String strDate = "" + mdformat.format(calendar.getTime());

                    result = jsonObject.getString(strDate);

                    float dresult = Float.parseFloat(result);
                    int iresult = (int) (dresult * 1000);
                    dresult = iresult;
                    dresult /= 1000;
                    oneweekData.add(dresult);

                    calendar.add(Calendar.DAY_OF_YEAR, -1);
                }

                draw(oneweekData);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    class getarraymonthTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            Log.i("mytask", "works");

            try {

                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat mdformat = new SimpleDateFormat("yyyy-MM-dd");
                Date mydate = calendar.getTime();
                calendar.setTime(mydate);

                for(int i = 0; i < 4; i++) {
                    calendar.add(Calendar.DAY_OF_YEAR, -1);
                    String startDate = "" + mdformat.format(calendar.getTime());
                    calendar.add(Calendar.DAY_OF_YEAR, -6);
                    String endDate = "" + mdformat.format(calendar.getTime());
                    currencies = fromCurrency + "_" + toCurrency;

                    Log.i("mytask", "startdate - " + startDate);
                    Log.i("mytask", "enddate - " + endDate);

                    url = new URL("https://free.currencyconverterapi.com/api/v6/convert?q=" + currencies + "&compact=ultra&date=" + endDate + "&endDate=" + startDate + "&apiKey=667a8fab011ea833b877");
                    urlConnection = (HttpURLConnection)url.openConnection();
                    InputStream inputStream = urlConnection.getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                    int data = inputStreamReader.read();

                    while(data != -1){
                        char current = (char) data;
                        result  += current;
                        data = inputStreamReader.read();
                    }
                    if(i != 3){
                        result += ",";
                    }
                }
                result = "{\"currency\":[" + result + "]}";

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return result;
        }
        //https://free.currencyconverterapi.com/api/v6/convert?q=USD_GBP&compact=ultra&date=2019-2-28&endDate=2019-3-4&apiKey=667a8fab011ea833b877
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            JSONObject jsonObject = null;
            JSONArray jsonArray = null;


            try {
                jsonObject = new JSONObject(result);
                jsonArray = new JSONArray(jsonObject.getString("currency"));


                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat mdformat = new SimpleDateFormat("yyyy-MM-dd");
                Date mydate = calendar.getTime();
                calendar.setTime(mydate);

                calendar.add(Calendar.DAY_OF_YEAR, -1);

            for(int j = 0; j < 4; j++) {
                for (int i = 0; i < 7; i++) {
                    String strDate = "" + mdformat.format(calendar.getTime());

                    jsonObject = jsonArray.getJSONObject(j);
                    result = jsonObject.getString(currencies);
                    jsonObject = new JSONObject(result);
                    result = jsonObject.getString(strDate);

                    float dresult = Float.parseFloat(result);
                    int iresult = (int) (dresult * 1000);
                    dresult = iresult;
                    dresult /= 1000;
                    onemonthData.add(dresult);

                    calendar.add(Calendar.DAY_OF_YEAR, -1);
                }

                Log.i("mytask", onemonthData.toString());
                draw(onemonthData);

            }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void draw(ArrayList<Float> oneweekData){
        float width = 0;
        float height = 0;
        float step = 0;
        float cof = 0;
        float windowheight = 0;
        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.graphic);
        //LinearLayout windowLayout = (LinearLayout)findViewById(R.id.window);
        Collections.reverse(oneweekData);
        float max = oneweekData.get(0);
        float min = oneweekData.get(0);

        for(int i = 1; i < oneweekData.size(); i++){
            if(max < oneweekData.get(i)){
                max = oneweekData.get(i);
            }

            if(min > oneweekData.get(i)){
                min = oneweekData.get(i);
            }
        }

        width = linearLayout.getWidth();
        height = linearLayout.getHeight();
        step = (width - 35 ) / (oneweekData.size() - 1);
        cof =(height - 10)/(max - min);
        // when we draw we mul cof with every element of array
        // also x  - x + step


        Paint paint = new Paint();
        Bitmap bg = Bitmap.createBitmap((int)width, (int)height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bg);

        paint.setColor(getApplicationContext().getResources().getColor(R.color.colorAccent));

        float swd = paint.getStrokeWidth();
        paint.setStrokeWidth(4);
        for(int i = 0; i < oneweekData.size() - 1 ; i++) {
            canvas.drawLine(35 + i  * step, height - (oneweekData.get(i) - min) * cof , 35 + (i + 1) * step, height - (oneweekData.get(i + 1) - min) * cof, paint);
        }

        paint.setStrokeWidth(swd);
        width = linearLayout.getWidth();
        height = linearLayout.getHeight();
        cof = (height - 10)/(max - min);

        paint.setTextSize(10);
        paint.setColor(getApplicationContext().getResources().getColor(R.color.colorGrey));
        for(int i = 0; i < oneweekData.size()  ; i++) {
            canvas.drawText("" + oneweekData.get(i),0, height - (oneweekData.get(i) - min) * cof - 1, paint);
            canvas.drawLine(0, height - (oneweekData.get(i) - min) * cof - 1 , width, height - (oneweekData.get(i) - min) * cof,paint);
        }

        linearLayout.setBackgroundDrawable(new BitmapDrawable(bg));
    }


}
