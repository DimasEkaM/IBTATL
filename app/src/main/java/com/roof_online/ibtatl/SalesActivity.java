package com.roof_online.ibtatl;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class SalesActivity extends AppCompatActivity {
    SharedPreferences sharedpreferences;
    TextView name;
    public static final String mypreference = "mypref";
    public static final String Name = "nameKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales);
        name = (TextView) findViewById(R.id.etName);

        sharedpreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);
        if (sharedpreferences.contains(Name)) {
            name.setText(sharedpreferences.getString(Name, ""));
        }

    }
    public void Save(View view) {
        String n = name.getText().toString();

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(Name, n);

        editor.commit();
        finish();
        Toast.makeText(SalesActivity.this, "Berhasil", Toast.LENGTH_SHORT).show();
    }

    public void clear(View view) {
        name = (TextView) findViewById(R.id.etName);

        name.setText("");


    }

    public void Get(View view) {
        name = (TextView) findViewById(R.id.etName);

        sharedpreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);

        if (sharedpreferences.contains(Name)) {
            name.setText(sharedpreferences.getString(Name, ""));
        }

    }

    public void Back(View view){
        finish();
    }

}
