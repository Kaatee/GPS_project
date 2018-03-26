package com.introtoandroid.gps;


import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Chronometer;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.os.SystemClock;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;
import java.io.PrintWriter;
import com.google.android.gms.common.api.GoogleApiClient;
import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import android.os.Bundle;
import android.widget.Toast;
import android.location.LocationListener;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Time;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.location.LocationListener;
import android.Manifest;

public class EkranGlowny extends AppCompatActivity {

    private boolean kliknietaPauza;
    private String srodek_transportu;
    private long base;
    private int startNumber = 1;
    private Chronometer czasomierz;
    private Spinner spin;
    private double longitude;
    private double latitude;
    int idUruchomieniaAuto;// = 1;
    int idUruchomieniaPieszy;// = 2;
    int idUruchomienia;// = 1;
    Context context = this;
    LocationManager lm;
    LocationManager locMen;
    Criteria kr;
    Location lokalizacja;
    String najlepszyDostawca;
    private static Location lastLocation = null;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    private static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 11;
    private int numerUruchomienia = 1;
    private TextView textView2 ;

    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList();
    private ArrayList<String> permissions = new ArrayList();
    private final static int ALL_PERMISSIONS_RESULT = 101;
    GPS localizer;

    double dlugosc;
    double szerokosc;



    protected double pobierz_wspolrzedne_dlug() {

        //localizer = new GPS(EkranGlowny.this);
        if (localizer.canGetLocation()) {
            longitude = localizer.getLocation().getLongitude();
            //latitude = localizer.getLatitude();
            //System.out.println("LONG: " + Double.toString(longitude));
         /*
           runOnUiThread(new Runnable() {
               @Override
               public void run() {
                   SimpleDateFormat simpleDateHere = new SimpleDateFormat("kk:mm:ss");
                   textView2.setText("Long: " + Double.toString(longitude) + " Lat: " + Double.toString(latitude) +" -> "+ simpleDateHere.format(new Date()));
               }
           }
           );
*/
            // ((TextView)findViewById(R.id.textView_Geo)).setText("Longitude:" + Double.toString(longitude) + "\nLatitude:" + Double.toString(latitude));
            //Toast.makeText(EkranGlowny.this, "Longitude:" + Double.toString(longitude) + "\nLatitude:" + Double.toString(latitude), Toast.LENGTH_SHORT).show();
            return longitude;
        }
        else {

            //localizer.showSettingsAlert();
            return 0.1;
        }
    }



    protected double pobierz_wspolrzedne_szer() {

        //localizer = new GPS(EkranGlowny.this);
        if (localizer.canGetLocation()) {
            //longitude = localizer.getLongitude();
            //latitude = localizer.getLatitude();
            latitude = localizer.getLocation().getLatitude();
            return latitude;
        }
        else {
            return 0.1;
        }
    }


    protected void zapiszPlik(String nazwaPliku) {
        File root = android.os.Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File dir = new File(root.getAbsolutePath() + "/download");
        dir.mkdirs();

        File file = new File(dir, (nazwaPliku + ".txt"));

        SimpleDateFormat simpleDateHere = new SimpleDateFormat("yyyy-MM-dd;kk:mm:ss");

        int idUruchomienia = pobierzIdPliku(nazwaPliku);
        //if(nazwaPliku.equals("Samochod")) idUruchomienia=idUruchomieniaAuto;
       // else idUruchomienia=idUruchomieniaPieszy;

        try {
            FileOutputStream f = new FileOutputStream(file, true);
            PrintWriter pw = new PrintWriter(f);
            pw.println(idUruchomienia + ";" + pobierz_wspolrzedne_szer() + ";" + pobierz_wspolrzedne_dlug() + ";" + simpleDateHere.format(new Date()));
            pw.flush();
            pw.close();
            f.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void zapiszIdUruchomienia(String nazwaPliku, int id) {
        File root = android.os.Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File dir = new File(root.getAbsolutePath() + "/idUruchomienia");
        dir.mkdirs();

        File file = new File(dir, (nazwaPliku + ".txt"));
        try {
            FileOutputStream f = new FileOutputStream(file);
            PrintWriter pw = new PrintWriter(f);
            pw.println(id);
            pw.flush();
            pw.close();
            f.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void zwiekszIdUruchomienia(String nazwaPliku) {
        int id = pobierzIdPliku(nazwaPliku);
        id=id+2;
        zapiszIdUruchomienia(nazwaPliku,id);
    }



    protected int pobierzIdPliku(String nazwaPliku) {
        File root = android.os.Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File dir = new File(root.getAbsolutePath() + "/idUruchomienia");
        File file = new File(dir, (nazwaPliku + ".txt"));
        try {
            FileInputStream fis = new FileInputStream(file);
            BufferedReader r = new BufferedReader(new InputStreamReader(fis));

            String txt;
            txt = r.readLine();

            if (txt == null && nazwaPliku.equals("Samochod")) return 1;
            if (txt == null && nazwaPliku.equals("Pieszy")) return 2;




            int id = Integer.parseInt(txt);

            r.close();
            zapiszIdUruchomienia(nazwaPliku, id);
            return id;

        } catch (IOException e) {
            if (nazwaPliku.equals("Samochod")) return 1;
            else return 2;
            //e.printStackTrace();

        }
    }


    private class PetlaZapisuKlasa extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... voids) {

            Calendar c = Calendar.getInstance();
            SimpleDateFormat dateformat = new SimpleDateFormat("ss");//"mm:ss aa"

            int coIleSekund=3000; //sekundy * 1000 bo milisekundy
            if(srodek_transportu.equals("Samochod")) coIleSekund=10000;
            if(srodek_transportu.equals("Pieszy")) coIleSekund=15000;
            do
            {
                String datetime = dateformat.format(c.getTime());
                if (System.currentTimeMillis() % coIleSekund == 0) {
                    zapiszPlik(srodek_transportu);
                }
            }
            while(kliknietaPauza==false);
            return null;
        }

        private void zapisPlik(String nazwaPliku) {
            File root = android.os.Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            File dir = new File(root.getAbsolutePath() + "/download");
            dir.mkdirs();
            File file = new File(dir, (nazwaPliku + ".txt"));
            SimpleDateFormat simpleDateHere = new SimpleDateFormat("yyyy-MM-dd;kk:mm:ss");

            int idUruchomienia=pobierzIdPliku(nazwaPliku);
            //if(nazwaPliku.equals("Samochod")) idUruchomienia=idUruchomieniaAuto;
            //else idUruchomienia=idUruchomieniaPieszy;

            try {
                FileOutputStream f = new FileOutputStream(file, true);
                PrintWriter pw = new PrintWriter(f);
                pw.println(idUruchomienia + ";" + pobierz_wspolrzedne_szer() + ";" + pobierz_wspolrzedne_dlug() + ";" + simpleDateHere.format(new Date()));
                pw.flush();
                pw.close();
                f.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        private double pobierz_wspolrzedne_dlug() {

            //localizer = new GPS(EkranGlowny.this);
            if (localizer.canGetLocation()) {
                longitude = localizer.getLongitude();
                latitude = localizer.getLatitude();
                //System.out.println("LONG: " + Double.toString(longitude));
         /*
           runOnUiThread(new Runnable() {
               @Override
               public void run() {
                   SimpleDateFormat simpleDateHere = new SimpleDateFormat("kk:mm:ss");
                   textView2.setText("Long: " + Double.toString(longitude) + " Lat: " + Double.toString(latitude) +" -> "+ simpleDateHere.format(new Date()));
               }
           }
           );
*/
                // ((TextView)findViewById(R.id.textView_Geo)).setText("Longitude:" + Double.toString(longitude) + "\nLatitude:" + Double.toString(latitude));
                //Toast.makeText(EkranGlowny.this, "Longitude:" + Double.toString(longitude) + "\nLatitude:" + Double.toString(latitude), Toast.LENGTH_SHORT).show();
                return longitude;
            }
            else {

                //localizer.showSettingsAlert();
                return 0.1;
            }
        }

        private double pobierz_wspolrzedne_szer() {

            if (localizer.canGetLocation()) {
                longitude = localizer.getLongitude();
                latitude = localizer.getLatitude();
                return latitude;
            }
            else {
                return 0.1;
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ekran_glowny);
        textView2 = (TextView)findViewById(R.id.textView2);

        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);
        localizer = new GPS(EkranGlowny.this);
        permissionsToRequest = findUnAskedPermissions(permissions);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


            if (permissionsToRequest.size() > 0)
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }

        final Button start = (Button) findViewById(R.id.button);

        start.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                kliknietaPauza = false;
                spin = (Spinner) findViewById(R.id.spinner);
                TextView tekst = (TextView) spin.getSelectedView();
                srodek_transportu = tekst.getText().toString();


                if (startNumber == 1) {
                    czasomierz = (Chronometer) findViewById(R.id.chronometer2);
                    czasomierz.setBase(SystemClock.elapsedRealtime());
                    //idUruchomienia = pobierzIdPliku(srodek_transportu);
                } else
                    czasomierz.setBase(SystemClock.elapsedRealtime() + base);


                czasomierz.start();
                new PetlaZapisuKlasa().execute();

            }

        });

        final Button pauza = (Button) findViewById(R.id.button2);
        pauza.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                kliknietaPauza = true;
                base = czasomierz.getBase() - SystemClock.elapsedRealtime();
                czasomierz.stop();
                startNumber++;
            }
        });

        final Button zakonczAktywnosc = (Button) findViewById(R.id.button3);
        zakonczAktywnosc.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                czasomierz.setBase(SystemClock.elapsedRealtime());
                base = 0;
                czasomierz.stop();
                numerUruchomienia++;
                zwiekszIdUruchomienia(srodek_transportu);
                /*if(srodek_transportu.equals("Samochod")) {
                    idUruchomieniaAuto+=2;
                    zapiszIdUruchomienia(srodek_transportu,idUruchomieniaAuto);
                }
                else{
                    idUruchomieniaPieszy+=2;
                    zapiszIdUruchomienia(srodek_transportu,idUruchomieniaPieszy);
                } */
                //idUruchomienia++;

            }
        });
    }

    private ArrayList findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    // @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {

            case ALL_PERMISSIONS_RESULT:
                for (String perms : permissionsToRequest) {
                    if (!hasPermission(perms)) {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }

                }

                break;
        }

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(EkranGlowny.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        localizer.stopListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        localizer = new GPS(EkranGlowny.this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        localizer.stopListener();
    }


}

