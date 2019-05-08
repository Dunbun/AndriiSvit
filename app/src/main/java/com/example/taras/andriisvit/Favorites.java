package com.example.taras.andriisvit;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Favorites extends AppCompatActivity{

    String[]  shortName= { "UAH", "PLN", "USD", "AUD", "CAD", "RUB", "JPY", "GBP" };

    String[] fullName = { "Ukrainian Hryvnia", "Poland Zlotiy","USA Dollar", "Australian Dollar",
            "Canadian Dollar", "Russian Ruble", "Japanese Yen", "British Pound" };

    int[] images = { R.drawable.ukraina,  R.drawable.polsha,  R.drawable.usa,
            R.drawable.avstraliya,  R.drawable.canada, R.drawable.russia, R.drawable.yapan, R.drawable.velikobritaniya };

    static final String[] currencies = new String[]{"UAH", "PLN", "USD", "AUD", "CAD", "RUB", "JPY", "GBP"};

    boolean isChecked = false;

    MyDB myDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        myDB = new MyDB(Favorites.this);
        final ListView listView = (ListView)findViewById(R.id.listView);
        customadapter ca = new customadapter(false);
        listView.setAdapter(ca);

        final AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView)findViewById(R.id.autoCompleteTextView);

        autoCompleteTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });

        final ArrayAdapter<String> adapter  = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, currencies);

        autoCompleteTextView.setThreshold(1);
        autoCompleteTextView.setAdapter(adapter);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String result = "";
                result = autoCompleteTextView.getText().toString();

                for(int i = 0; i < shortName.length; i++){
                    if(shortName[i].equals(result)){
                        InputMethodManager imm = (InputMethodManager)getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(autoCompleteTextView.getWindowToken(), 0);

                        listView.smoothScrollToPosition(i);
                        listView.deferNotifyDataSetChanged();
                        listView.requestFocusFromTouch();
                        listView.setSelection(i);

                        break;
                    }
                }
            }
        });

        Button goToConverterBtn = (Button)findViewById(R.id.goToConverter);
        goToConverterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startMain = new Intent("com.example.taras.andriisvit.Converter");
                startActivity(startMain);
            }
        });


        Button btn = (Button)findViewById(R.id.allCurrencies);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isChecked = !isChecked;
                customadapter ca = new customadapter(isChecked);
                listView.setAdapter(ca);

                SQLiteDatabase database = myDB.getWritableDatabase();

                    if (isChecked) {
                        ContentValues contentValues = new ContentValues();
                        String sCurrencies = "";

                        for (int i = 0; i < shortName.length; i++) {
                            sCurrencies = shortName[i];
                            String selection = MyDB.KEY_CURENCY + " = ?";
                            String [] selectionArgs = {sCurrencies};

                            Cursor cursor = database.query(MyDB.TABLE_NAME, null, selection, selectionArgs, null, null, null);

                            if(!cursor.moveToFirst()) {
                                contentValues.put(MyDB.KEY_CURENCY, sCurrencies);
                                database.insert(MyDB.TABLE_NAME, null, contentValues);
                            }
                        }
                    } else {
                        database.delete(MyDB.TABLE_NAME, null, null);
                    }

                myDB.close();
            }
        });

    }

    class customadapter extends BaseAdapter {
        private  boolean isClicked = false;

        public customadapter(boolean isClicked){
            this.isClicked = isClicked;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return images.length;
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub

            return null;
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(final int position, View convertview, ViewGroup arg2) {
            // TODO Auto-generated method stub
            LayoutInflater inflater = getLayoutInflater();
            convertview = inflater.inflate(R.layout.list_item, null);
            TextView tv = (TextView) convertview.findViewById(R.id.short_name);
            TextView tv1 = (TextView) convertview.findViewById(R.id.full_name);
            ImageView image = (ImageView) convertview
                    .findViewById(R.id.flag);
            tv.setText(shortName[position]);
            tv1.setText(fullName[position]);
            image.setImageResource(images[position]);

            CheckBox checkBox = (CheckBox)convertview.findViewById(R.id.checkBox);

            String currency = "";
            currency = shortName[position];

            String sCurrencies = currency;

            SQLiteDatabase database = myDB.getWritableDatabase();

            String selection = MyDB.KEY_CURENCY + " = ?";
            String [] selectionArgs = {sCurrencies};

            Cursor cursor = database.query(MyDB.TABLE_NAME, null, selection, selectionArgs, null, null, null);

            if(cursor.moveToFirst()){
                checkBox.setChecked(true);
            }else{
                checkBox.setChecked(isClicked);
            }


            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    String currency = "";
                    currency = shortName[position];
                    Toast.makeText(Favorites.this,currency,Toast.LENGTH_SHORT).show();

                    String sCurrencies = currency;

                    SQLiteDatabase database = myDB.getWritableDatabase();


                    String selection = MyDB.KEY_CURENCY + " = ?";
                    String [] selectionArgs = {sCurrencies};


                    Cursor cursor = database.query(MyDB.TABLE_NAME, null, selection, selectionArgs, null, null, null);

                    if(!cursor.moveToFirst()){
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(MyDB.KEY_CURENCY, sCurrencies);

                        database.insert(MyDB.TABLE_NAME, null, contentValues);
                        //Toast.makeText(Favorites.this, "Registration completed", Toast.LENGTH_SHORT).show();
                    }
                    else{

                        database.delete(MyDB.TABLE_NAME,"currency = ? ", selectionArgs);
                        //Toast.makeText(Favorites.this, "Such account already exists :(", Toast.LENGTH_LONG).show();
                    }

                }
            });


            return convertview;
        }

    }

}


