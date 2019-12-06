package com.example.newapp;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class WeatherActivity extends AppCompatActivity {
    EditText editText;
    TextView resultTextView ,coordResult;
    ImageView imgLogo;
    public  class DownloadTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpsURLConnection urlConnection = null;
            try{
                url =  new URL(urls[0]);
                urlConnection =(HttpsURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while (data != -1){
                    char current =(char) data;
                    result += current;
                    data = reader.read();

                }
                return result;


            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(WeatherActivity.this,"Could Not Find Weather",Toast.LENGTH_LONG).show();
                return null;
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //String resultt = s.toString();

            // Toast.makeText(MainActivity.this,s,Toast.LENGTH_LONG).show();
            try{
                JSONObject jsonObject = new JSONObject(s);
                String weatherInfo =jsonObject.getString("weather");
                //String coordInfo = jsonObject.getString("coord");
                String message = "";
               //String coord ="";

               // Toast.makeText(WeatherActivity.this,weatherInfo,Toast.LENGTH_LONG).show();
                JSONArray array = new JSONArray(weatherInfo);
                for (int i=0; i<array.length();i++){
                    JSONObject jsonPart  = array.getJSONObject(i);
                   // Toast.makeText(WeatherActivity.this,jsonPart.getString("main"),Toast.LENGTH_LONG).show();
                    //Log.i("JsonPart",jsonPart.getString("main"));
                   // Log.i("JsonPart",jsonPart.getString("description"));
                    //Log.i("JsonPart",jsonPart.getString("main"));
                    String main = jsonPart.getString("main");
                    String description = jsonPart.getString("description");

                    if (!main.equals("") && !description.equals("")){
                            message += main +":" + description +"\r\n ";
                    }
                }

                if (!message.equals("")){
                    resultTextView.setText(message);

                }







                //imgLogo.setImageResource(icon);



            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(WeatherActivity.this,"Could Not Find Weather",Toast.LENGTH_LONG).show();
            }


        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        editText = (EditText)findViewById(R.id.edtSearch);
        resultTextView = (TextView)findViewById(R.id.resultTextView);
       // imgLogo = (ImageView)findViewById(R.id.imgLogo);
        coordResult = (TextView)findViewById(R.id.coordResult);

        getSupportActionBar().setTitle("DoIt Weather");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

       // DownloadTask downloadTask = new DownloadTask();
        //String result = null;
       // downloadTask.execute("https://samples.openweathermap.org/data/2.5/weather?q=London,uk&appid=b6907d289e10d714a6e88b30761fae22");
    }

    public void getWeather(View view){

        DownloadTask downloadTask = new DownloadTask();
        //String result = null;
        downloadTask.execute("https://openweathermap.org/data/2.5/weather?q=" + editText.getText().toString() +"&appid=b6907d289e10d714a6e88b30761fae22");

        InputMethodManager inputMethodManager =(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(),0);

    }
}
