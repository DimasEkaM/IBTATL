package com.roof_online.ibtatl;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;

import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.ListView;
import android.widget.Toast;

import com.ajts.androidmads.library.SQLiteToExcel;
import com.kishan.askpermission.AskPermission;
import com.kishan.askpermission.ErrorCallback;
import com.kishan.askpermission.PermissionCallback;
import com.kishan.askpermission.PermissionInterface;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.roof_online.ibtatl.SalesActivity.Name;
import static com.roof_online.ibtatl.SalesActivity.mypreference;

public class MainActivity extends AppCompatActivity implements PermissionCallback, ErrorCallback {

    String[] daftar;
    ListView ListView01;
    protected Cursor cursor;
    DataHelper dbcenter;
    public static MainActivity ma;
    private static final int REQUEST_PERMISSIONS = 20;
    SharedPreferences sharedpreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //FIRST RUN ACTIVITY ONLY
        Boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("isFirstRun", true);

        if (isFirstRun) {
            //show start activity

            startActivity(new Intent(MainActivity.this, SalesActivity.class));
            /*Toast.makeText(MainActivity.this, "First Run", Toast.LENGTH_LONG)
                    .show();*/
        }

        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putBoolean("isFirstRun", false).commit();
        //FIRST RUN ACTIVITY ONLY

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, BuatBiodataActivity.class);
                startActivity(intent);
            }
        });
        ma = this;
        dbcenter = new DataHelper(this);
        RefreshList();
        reqPermission();
    }

    public void RefreshList() {
        SQLiteDatabase db = dbcenter.getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM biodata", null);
        daftar = new String[cursor.getCount()];
        cursor.moveToFirst();
        for (int cc = 0; cc < cursor.getCount(); cc++) {
            cursor.moveToPosition(cc);
            daftar[cc] = cursor.getString(1).toString();
        }
        ListView01 = (ListView) findViewById(R.id.ListView1);
        ListView01.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, daftar));
        ListView01.setSelected(true);
        ListView01.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            public void onItemClick(AdapterView arg0, View arg1, int arg2, long arg3) {
                final String selection = daftar[arg2]; //.getItemAtPosition(arg2).toString();
                final CharSequence[] dialogitem = {"Lihat Data", "Update Data", "Hapus Data"};
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Pilihan");
                builder.setItems(dialogitem, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        switch (item) {
                            case 0:
                                Intent i = new Intent(getApplicationContext(), LihatBiodataActivity.class);
                                i.putExtra("nama", selection);
                                startActivity(i);
                                break;
                            case 1:
                                Intent in = new Intent(getApplicationContext(), UpdateBiodataActivity.class);
                                in.putExtra("nama", selection);
                                startActivity(in);
                                break;
                            case 2:
                                new AlertDialog.Builder(MainActivity.this)
                                        .setMessage("Hapus data?")
                                        .setCancelable(false)
                                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                SQLiteDatabase db = dbcenter.getWritableDatabase();
                                                db.execSQL("delete from biodata where nama = '" + selection + "'");
                                                RefreshList();
                                            }
                                        })
                                        .setNegativeButton("Tidak", null)
                                        .show();


                                break;
                        }
                    }
                });
                builder.create().show();
            }
        });
        ((ArrayAdapter) ListView01.getAdapter()).notifyDataSetInvalidated();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            //exportDB();
            exportXcel();
            notif();
        }
        if (id == R.id.action_sales) {
            Intent intent = new Intent(MainActivity.this, SalesActivity.class);
            startActivity(intent);

        }
        return super.onOptionsItemSelected(item);
    }




    private void reqPermission() {
        new AskPermission.Builder(this).setPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .setCallback(this)
                .setErrorCallback(this)
                .request(REQUEST_PERMISSIONS);
    }


    public void onShowRationalDialog(final PermissionInterface permissionInterface, int requestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("We need permissions for this app.");
        builder.setPositiveButton("oke", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                permissionInterface.onDialogShown();
            }
        });
        builder.setNegativeButton("cancel", null);
        builder.show();
    }


    public void onShowSettings(final PermissionInterface permissionInterface, int requestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("We need permissions for this app. Open setting screen?");
        builder.setPositiveButton("oke", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                permissionInterface.onSettingsShown();
            }
        });
        builder.setNegativeButton("cancel", null);
        builder.show();
    }

    @Override
    public void onPermissionsGranted(int requestCode) {
        //Toast.makeText(this, "Permissions Received.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionsDenied(int requestCode) {
        Toast.makeText(this, "Permissions Denied.", Toast.LENGTH_LONG).show();
    }

    private void exportDB(){
        final String inFileName = "/data/data/com.rooline.ibtatl/databases/ibtatl.db";
        File dbFile = new File(inFileName);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(dbFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String outFileName = Environment.getExternalStorageDirectory()+"/IBT-2018.db";

        // Open the empty db as the output stream
        OutputStream output = null;
        try {
            output = new FileOutputStream(outFileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        try {
            while ((length = fis.read(buffer))>0){
                output.write(buffer, 0, length);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Close the streams
        try {
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Toast.makeText(this, "Database Berhasil di export", Toast.LENGTH_LONG).show();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void notif(){
        Toast.makeText(this, "Berhasil backup", Toast.LENGTH_LONG).show();
    }
    private void exportXcel(){

        sharedpreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);
        String nama = sharedpreferences.getString(Name, "");
        String timeStamp = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
        String directory_path = Environment.getExternalStorageDirectory().getPath() + "/Backup/";

        final String namaFile = nama+timeStamp+"guestbook.xls";
        File file = new File(directory_path);
        if (!file.exists()) {
            file.mkdirs();
        }
        // Export SQLite DB as EXCEL FILE
        SQLiteToExcel sqliteToExcel = new SQLiteToExcel(getApplicationContext(), DataHelper.DATABASE_NAME, directory_path);
        sqliteToExcel.exportAllTables(namaFile, new SQLiteToExcel.ExportListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onCompleted(String filePath) {
                File outputFile = new File(
                        (Environment.getExternalStorageDirectory().getPath() + "/Backup/"), namaFile);
                //FileProvider.getUriForFile(MainActivity.this, getString(R.string.file_provider_authority), outputFile);
                //Uri uri = Uri.fromFile(outputFile);
                Uri uri = FileProvider.getUriForFile(MainActivity.this,
                        BuildConfig.APPLICATION_ID + ".provider",
                        outputFile);
                Intent share = new Intent();
                share.setAction(Intent.ACTION_SEND);
                share.setType("application/xls");
                share.putExtra(Intent.EXTRA_STREAM, uri);
                share.setPackage("com.whatsapp");
                startActivity(share);

            }

            @Override
            public void onError(Exception e) {

            }
        });
    }
}
