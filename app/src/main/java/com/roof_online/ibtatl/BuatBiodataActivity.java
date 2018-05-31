package com.roof_online.ibtatl;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.PublicKey;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;


import static com.roof_online.ibtatl.SalesActivity.Name;
import static com.roof_online.ibtatl.SalesActivity.mypreference;

public class BuatBiodataActivity extends AppCompatActivity {

    SharedPreferences sharedpreferences;

    protected Cursor cursor;
    DataHelper dbHelper;
    Button ton1, ton2, btn;
    EditText text1, text2, text3, text4, text5,text6,text7,text8;
    ImageView image;
    public String fname,filePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buat_biodata);
        createFolder();
        String timeStamp = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());

        dbHelper = new DataHelper(this);
        text1 = (EditText) findViewById(R.id.editText1);
        text2 = (EditText) findViewById(R.id.editText2);
        text3 = (EditText) findViewById(R.id.editText3);
        text4 = (EditText) findViewById(R.id.editText4);
        text5 = (EditText) findViewById(R.id.editText5);
        text6 = (EditText) findViewById(R.id.editText6);
        text7 = (EditText) findViewById(R.id.editText7);
        text8 = (EditText) findViewById(R.id.editText8);
        ton1 = (Button) findViewById(R.id.button1);
        ton2 = (Button) findViewById(R.id.button2);
        image  =(ImageView) findViewById(R.id.imagePhoto);
        btn    =(Button) findViewById(R.id.btnPhoto);

        text6.setEnabled(false);
        text7.setEnabled(false);

        sharedpreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);

        if (sharedpreferences.contains(Name)) {
            text6.setText(sharedpreferences.getString(Name, ""));
        }
        ton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub



                if(text2.getText().toString().equals(""))
                {
                    Toast.makeText(BuatBiodataActivity.this, "Nama Kosong", Toast.LENGTH_SHORT).show();
                }
                else if(text5.getText().toString().equals(""))
                {
                    Toast.makeText(BuatBiodataActivity.this, "Email kosong", Toast.LENGTH_SHORT).show();
                }
                else if(text3.getText().toString().equals(""))
                {
                    Toast.makeText(BuatBiodataActivity.this, "Handphone kosong", Toast.LENGTH_SHORT).show();
                }
                else {
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    db.execSQL("insert into biodata(id, nama, hp, perusahaan, email,sales,foto,notes,produk,status) " +
                            "values(null,'" +
                            text2.getText().toString() + "','" +
                            text3.getText().toString() + "','" +
                            text4.getText().toString() + "','" +
                            text5.getText().toString() + "','" +
                            text6.getText().toString() + "','" +
                            text7.getText().toString() + "','" +
                            text8.getText().toString() + "','iko',0)");
                    Toast.makeText(getApplicationContext(), "Berhasil", Toast.LENGTH_LONG).show();
                    //MainActivity.ma.RefreshList();
                    //finish();
                    text2.setText("");
                    text3.setText("");
                    text4.setText("");
                    text5.setText("");
                    text7.setText("");
                    text8.setText("");
                    image.setImageResource(android.R.color.transparent);
                }
            }
        });
        ton2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                onBackPressed();
                MainActivity.ma.RefreshList();
                //finish();
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, 1888);
            }
        });
    }


    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Kembali ke menu awal?")
                .setCancelable(false)
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        BuatBiodataActivity.this.finish();
                    }
                })
                .setNegativeButton("Tidak", null)
                .show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1888 && resultCode == RESULT_OK) {


            Bitmap photo = (Bitmap) data.getExtras().get("data");
            image.setImageBitmap(photo );

            // to create a random image file name
            Random generator = new Random();
            int n = 10000;
            n = generator.nextInt(n);
            String nama = sharedpreferences.getString(Name, "");
            fname = nama+n+".jpg";

            filePath = "/sdcard/PictureFolder/"+fname;
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(filePath);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            text7.setText(fname);
            BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);

            //choose another format if PNG doesn't suit you
            photo.compress(Bitmap.CompressFormat.PNG, 100, bos);

            try {
                bos.flush();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                bos.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void createFolder()
    {
        String RootDir = Environment.getExternalStorageDirectory()
                + File.separator + "PictureFolder";
        File RootFile = new File(RootDir);
        RootFile.mkdir();
    }


}
