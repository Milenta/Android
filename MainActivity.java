package studio.emendi.mareapp;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.concurrent.ExecutionException;



public class MainActivity extends AppCompatActivity {

    // link za korisceni api : http://openweathermap.org/
    // Potrebno je registrovati se na sajtu da bi dobili appi(key) za koriscenje .


    // http://api.openweathermap.org/data/2.5/weather?q=Belgrade,rs&appid=6994f0e043512e1fb45e9ef224aab894
    //link za podatke


    TextView mTxtTemp, pritisak, vlaznost, min_temp, max_temp, vetar, nemaNeta;
    ImageView SlikaNet;
    DownloadApi downloadApi;
    SqlLiteBaza baza;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String city = "Belgrade";

        downloadApi = new DownloadApi();
       downloadApi.execute("http://api.openweathermap.org/data/2.5/weather?q="+city+",rs&appid=6994f0e043512e1fb45e9ef224aab894");

        baza = new SqlLiteBaza(getApplicationContext());

        mTxtTemp = (TextView)findViewById(R.id.textViewTemp);
        pritisak = (TextView)findViewById(R.id.textViewPritisak);
        vlaznost = (TextView)findViewById(R.id.textViewVlaznost);
        min_temp = (TextView)findViewById(R.id.textViewMinTemp);
        max_temp = (TextView)findViewById(R.id.textViewMaxTemp);
        vetar = (TextView)findViewById(R.id.textViewBrzina);
        nemaNeta = (TextView)findViewById(R.id.noConnection);
        SlikaNet = (ImageView)findViewById(R.id.SlikaSaNeta);


// da pise podatke u bazu i da ocitava podatke iz baze
        // preuzimanje stream-a u JSON




        if (isNetworkAvailable()) {

            try {
                JSONObject object = new JSONObject(downloadApi.get());
                String main = object.getString("main");
                JSONObject mainJson = new JSONObject(main);

                String temp = mainJson.getString("temp");

                Double tempC = Double.parseDouble(mainJson.getString("temp")) - 273.15;
                DecimalFormat df = new DecimalFormat("#.#");
                String tempD = df.format(tempC);
                System.out.println(df.format(tempC));
               //  int tempCF = (int) (tempC - 273.15);

                String mainPritisak = mainJson.getString("pressure");
                String mainVlaznost = mainJson.getString("humidity");
                String mainMinTemp = mainJson.getString("temp_min");
                String mainMaxTemp = mainJson.getString("temp_max");


                Double tempMinA = Double.parseDouble(mainJson.getString("temp_min")) - 273.15;
                String tempMinB = df.format(tempMinA);

                Double tempMaxA = Double.parseDouble(mainJson.getString("temp_max")) - 273.15;
                String tempMaxB = df.format(tempMaxA);


                String wind = object.getString("wind");
                JSONObject windJson = new JSONObject(wind);

                String mainVetar = windJson.getString("speed");

                String weather = object.getString("weather");
                JSONArray jsonArray = new JSONArray(weather);
              JSONObject weatherObj =  jsonArray.getJSONObject(0);


                String slicica = weatherObj.getString("description");




                baza.insertData(tempD, mainPritisak, mainVlaznost, tempMinB, tempMaxB, mainVetar, slicica);

                if(slicica.equals("clear sky") ){
                    SlikaNet.setImageResource(R.drawable.sunce);
                }
                if(slicica.equals("few clouds") ){
                    SlikaNet.setImageResource(R.drawable.oblacno);
                }
                if(slicica.equals("scattered clouds") ){
                    SlikaNet.setImageResource(R.drawable.oblacno);
                }
                if(slicica.equals("broken clouds") ){
                    SlikaNet.setImageResource(R.drawable.oblacno);
                }
                if(slicica.equals("shower rain") ){
                    SlikaNet.setImageResource(R.drawable.kisovito);
                }
                if(slicica.equals("light rain") ){
                    SlikaNet.setImageResource(R.drawable.kisovito);
                }
                if(slicica.equals("moderate rain") ){
                    SlikaNet.setImageResource(R.drawable.kisovito);
                }
                if(slicica.equals("rain") ){
                    SlikaNet.setImageResource(R.drawable.kisovito);
                }
                if(slicica.equals("thunderstorm") ){
                    SlikaNet.setImageResource(R.drawable.oluja);
                }
                if(slicica.equals("snow") ){
                    SlikaNet.setImageResource(R.drawable.sneg);
                }
                if(slicica.equals("mist") ){
                    SlikaNet.setImageResource(R.drawable.mist2);
                }

                mTxtTemp.setText("Temperatura je: " + tempD + "°C");
                pritisak.setText("Pritisak je : " + mainPritisak + "mb");
                vlaznost.setText("Vlaznost vazduha je: " + mainVlaznost + "%");
                min_temp.setText("Minimalna temperatura je: " + tempMinB + "°C");
                max_temp.setText("Maksimalna temperatura je: " + tempMaxB + "°C");
                vetar.setText("Brzina vetra je: " + mainVetar + "m/s");


            } catch (JSONException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

        }
        else {
            nemaNeta.setText("Nemate internet, poslednji podaci su:");

            Cursor res = baza.getAllData();
            if(res.getCount() == 0) {

                Toast.makeText(getApplicationContext(),"Baza je prazna",Toast.LENGTH_LONG).show();
                // show message

                return;
            }

            StringBuffer buffer = new StringBuffer();
            while (res.moveToNext()) {

                System.out.println(res.getString(1));
                mTxtTemp.setText("Temperatura je: "+res.getString(1)+"°C");
                pritisak.setText("Pritisak je: "+res.getString(2)+"mb");
                vlaznost.setText("Vlaznost vazduha je: "+ res.getString(3)+"%");
                min_temp.setText("Minimalna temperatura je: " +res.getString(4) +"°C");
                max_temp.setText("Maksimalna temperatura je: "+ res.getString(5)+"°C");
                vetar.setText("Brzina vetra je: "+ res.getString(6)+"m/s");
                String slika = (res.getString(7));

                if(slika.equals("clear sky") ){
                    SlikaNet.setImageResource(R.drawable.sunce);
                }
                if(slika.equals("few clouds") ){
                    SlikaNet.setImageResource(R.drawable.oblacno);
                }
                if(slika.equals("scattered clouds") ){
                    SlikaNet.setImageResource(R.drawable.oblacno);
                }
                if(slika.equals("broken clouds") ){
                    SlikaNet.setImageResource(R.drawable.oblacno);
                }
                if(slika.equals("shower rain") ){
                    SlikaNet.setImageResource(R.drawable.kisovito);
                }
                if(slika.equals("light rain") ){
                    SlikaNet.setImageResource(R.drawable.kisovito);
                }
                if(slika.equals("moderate rain") ){
                    SlikaNet.setImageResource(R.drawable.kisovito);
                }
                if(slika.equals("rain") ){
                    SlikaNet.setImageResource(R.drawable.kisovito);
                }
                if(slika.equals("thunderstorm") ){
                    SlikaNet.setImageResource(R.drawable.oluja);
                }
                if(slika.equals("snow") ){
                    SlikaNet.setImageResource(R.drawable.sneg);
                }
                if(slika.equals("mist") ){
                    SlikaNet.setImageResource(R.drawable.mist2);
                }
//                buffer.append("Id :"+ res.getString(0)+"\n");
//                buffer.append("Name :"+ res.getString(1)+"\n");
//                buffer.append("Surname :"+ res.getString(2)+"\n");
//                buffer.append("Marks :"+ res.getString(3)+"\n\n");
            }

            // Show all data

        }
    }






    // @OnClick(R.id.textViewMaxTemp2)
    public  void onClik(){

        System.out.println("Kliknuto");

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    //private boolean isNetworkConnected() {
    //    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

    //    return cm.getActiveNetworkInfo() != null;
   // }



    public class DownloadApi extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... params) {

            String res = "";
            URL url;
            HttpURLConnection connection = null;


            try {
                url = new URL(params[0]);
                // kreiramo objekat za http konekciju i izvrsavamo konekciju
                connection = (HttpURLConnection) url.openConnection();
                // kreiramo objekat za ulazni stream
                InputStream inputStream = connection.getInputStream();
                // kreiramo reader za ucitani stream
                InputStreamReader reader = new InputStreamReader(inputStream);

                int data = reader.read();

                while (data != -1) {


                    char curent = (char) data;
                    res += curent;

                    data = reader.read();

                }

                return res;
            } catch (IOException e) {
                e.printStackTrace();
            }

            return res;
        }


    }
}
