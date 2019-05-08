package com.example.taras.andriisvit;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class News extends AppCompatActivity {
    private TableLayout tableLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        Button favoritesBtn = (Button)findViewById(R.id.buttonFavorites);
        Button converterBtn = (Button)findViewById(R.id.buttonConverter);

        favoritesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.example.taras.andriisvit.Favorites");
                startActivity(intent);
            }
        });

        converterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.example.taras.andriisvit.Converter");
                startActivity(intent);
            }
        });

        tableLayout = (TableLayout) findViewById(R.id.tableLayout1);
        MyTask mt = new MyTask();
        mt.execute();
    }
    protected void CreateTable(Elements elementsTable)
    {
        for (Element element : elementsTable)
        {
            for (Element row : element.select("tr"))
            {
                Elements tds = row.select("td");
                TableRow tableRow = new TableRow(this);
                int index = 0, counter = 0;
                int[] width = {60, 50, 150, 70, 70, 80};
                if(tds.size() >= 6)
                {
                    for (Element td : tds)
                    {
                        if(counter == 2)
                        {
                            counter++;
                        }
                        else
                        {
                            TextView textView = new TextView(this);
                            textView.setText(td.text());
                            textView.setTextSize(11);
                            textView.setGravity(Gravity.CENTER);
                            textView.setTextColor(Color.BLACK);
                            textView.setWidth(width[index]);
                            tableRow.addView(textView);
                            if (index < 5)
                            {
                                index++;
                            }
                            counter++;
                        }
                    }
                }
                tableLayout.addView(tableRow);
            }
        }
    }

    class MyTask extends AsyncTask<Void, Void, Void> {
        Elements elementsTable;
        @Override
        protected Void doInBackground(Void... params) {
            //Тут пишем основной код
            Document doc = null;//Здесь хранится будет разобранный html документ
            try {
                //Считываем заглавную страницу http://harrix.org
                doc = Jsoup.connect("https://www.investing.com/economic-calendar").get();
            } catch (IOException e) {
                //Если не получилось считать
                e.printStackTrace();
            }

            //Если всё считалось, что вытаскиваем из считанного html документа заголовок
            if (doc != null) {
                elementsTable = doc.select("#economicCalendarData");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            CreateTable(elementsTable);
        }

    }
}
