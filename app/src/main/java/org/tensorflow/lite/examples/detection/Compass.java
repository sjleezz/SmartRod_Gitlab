package org.tensorflow.lite.examples.detection;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;

import android.os.Handler;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static java.lang.Thread.sleep;

public class Compass extends __TTS__ {

    private final Bundle params = new Bundle();

    private String TAG = "seek near building";
    private static final String IP_ADDRESS = "203.252.166.206";
    private static final String TAG_JSON = "smartrod";
    private static final String TAG_NAME = "bname";
    private static final String TAG_DISTANCE = "distance";
    private static final String TAG_DIRECTION = "diretion";

    private EditText mEditTextName;
    private EditText mEditTextLatitude;
    private EditText mEditTextLongitude;
    private TextView mTextViewResult;

    private String name;
    private String lat;
    private String lon;

    LocationManager locationManager;
    LocationListener locationListener;
    boolean isGPSEnabled;
    boolean isNetworkEnabled;

    //ArrayList<HashMap<String, String>> mArrayList;
    //ListView mListViewList;
    String mJsonString;

    Button buttonGetName;
    Button buttonGetLoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        // GPS ?????? ??????
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Compass.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }

        // GPS provider???
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        // ???????????? ?????? ??????
        mEditTextName = (EditText) findViewById(R.id.editText_main_name);
        mEditTextLatitude = (EditText) findViewById(R.id.editText_latitude);
        //mListViewList = (ListView) findViewById(R.id.listView_main_list);
        mEditTextLongitude = (EditText) findViewById(R.id.editText_longitude);
        mTextViewResult = (TextView) findViewById(R.id.textView_main_result);

        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        // insert.php??? ???????????? ??????
        /*Button buttonInsert = (Button)findViewById(R.id.button_insert);
        buttonInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = mEditTextName.getText().toString();
                lat = mEditTextLatitude.getText().toString();
                lon = mEditTextLongitude.getText().toString();

                InsertData task = new InsertData();
                task.execute("http://" + IP_ADDRESS + "/insert.php", name, lat, lon);

                mEditTextName.setText("");
                mEditTextLatitude.setText("");
                mEditTextLongitude.setText("");
            }
        });*/
        buttonGetLoc = (Button) findViewById(R.id.button_gps);
        buttonGetLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isGPSEnabled || isNetworkEnabled) {
                    Log.e("GPS Enable", "true");

                    final List<String> m_lstProviders = locationManager.getProviders(false);
                    locationListener = new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            Log.d("onLocationChanged", "onLocationChanged");
                            Log.d("location", "[" + location.getProvider() + "] (" + location.getLatitude() + "," + location.getLongitude() + ")");

                            mEditTextLatitude.setText(String.valueOf(location.getLatitude()));
                            mEditTextLongitude.setText(String.valueOf(location.getLongitude()));
                            locationManager.removeUpdates(this);
                            buttonGetName.performClick();

                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {
                            Log.d("onStatusChanged", "onStatusChanged");
                        }

                        @Override
                        public void onProviderEnabled(String provider) {
                            Log.d("onProviderEnabled", "onProviderEnabled");
                        }

                        @Override
                        public void onProviderDisabled(String provider) {
                            Log.d("onProviderDisabled", "onProviderDisabled");

                        }
                    };

                    // QQQ: ??????, ????????? 0 ?????? ???????????? ????????? ?????? ?????? ????????? ??????????????? ????????? ????????? ?????? ??? ??????.

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for (String name : m_lstProviders) {
                                locationManager.requestLocationUpdates(name, 1000, 0, locationListener);
                            }

                        }
                    });

                } else {
                    Log.e("GPS Enable", "false");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Snackbar.make(getCurrentFocus(), "????????? ?????????????????? GPS??? ?????? ????????????????????????.", Snackbar.LENGTH_INDEFINITE).setAction("GPS??????", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivity(intent);
                                }
                            }).show();
                        }
                    });
                }
            }
        });


        // query.php??? ???????????? ??????
        buttonGetName = (Button) findViewById(R.id.button_get_name);
        buttonGetName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mArrayList.clear();
                GetData task = new GetData();
                imm.hideSoftInputFromWindow(mEditTextName.getWindowToken(), 0);
                task.execute(mEditTextLatitude.getText().toString(), mEditTextLongitude.getText().toString());

            }
        });

        //mArrayList = new ArrayList<>();

    }

    public void stoplocation() {
        Log.d("stop GPS", "compass");
        locationManager.removeUpdates(locationListener);
    }

    @Override
    public void onResume() {
        super.onResume();

        // n?????? ????????? ???????????????
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                //Toast.makeText(getApplicationContext(),"[onResume] run",Toast.LENGTH_SHORT).show();
                funcVoiceOut("????????????????????? ???????????????. ?????? ??????????????? 100???????????? ???????????? ???????????????.");
                while(tts.isSpeaking()) {}
                buttonGetLoc.performClick();
            }
        }, 1000);

    }

    @Override
    public void onRestart() {
        Log.d("onRestart()"," compass");
        super.onRestart();
    }

    @Override
    public void onPause() {    // startActivity() ????????? ?????? ?????????
        Log.d("onPause()"," compass");

        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d("onStop()"," compass");

        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.d("onDestroy()"," compass");
        if(tts != null) {
            tts.shutdown();
        }

        super.onDestroy();
    }


    // interact with query.php
    private class GetData extends AsyncTask<String, Void, String>{

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(Compass.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            //mTextViewResult.setText(result);
            Log.d("", "response - " + result);

            if (result == null){

                mTextViewResult.setText(errorString);
            }
            else {

                mJsonString = result;
                showResult();
            }
        }


        @Override
        protected String doInBackground(String... params) {

            String search_lat = params[0];
            String search_lon = params[1];

            String serverURL = "http://" + IP_ADDRESS + "/php/query.php";
            String postParameters = "latitude=" + search_lat + "&longitude=" + search_lon;

            // ???????????? ?????? ??????
            //float temp_lat = 37.54168974881991f;
            //float temp_lon = 127.07868833913464f;
            //String postParameters = "latitude=" + temp_lat + "&longitude=" + temp_lon;


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response code (200 is success) : " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }
                bufferedReader.close();

                System.out.println("sb : " + sb.toString().trim());

                return sb.toString().trim();

            } catch (Exception e) {
                Log.d(TAG, "InsertData: Error ", e);
                errorString = e.toString();
                return null;
            }
        }
    }

    private void showResult() {
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            String[] buf = new String[50];

            int cnt = 0;
            for (int i = 0; i < jsonArray.length(); i++) {
                Toast.makeText(getApplicationContext(),""+i+"",Toast.LENGTH_SHORT).show();
                JSONObject item = jsonArray.getJSONObject(i);
                String name = item.getString(TAG_NAME);
                //String distance = item.getString(TAG_DISTANCE);

                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put(TAG_NAME, name);
                //hashMap.put(TAG_DISTANCE, distance);
                //mArrayList.add(hashMap);            // JSON ?????? ??????
                System.out.println("name : " + lookup_name(name));

                buf[cnt] = lookup_name(name);
                //distance_meter[cnt] = Double.parseDouble(distance) * 1000;
                cnt++;
            }
            funcVoiceOut("?????? 100?????? ?????????, ????????? ?????? ???????????? ????????????...");
            sleep(2000);
            for (int i = 0; i < cnt; i++) {
                funcVoiceOut(buf[i]);
                while (tts.isSpeaking()) { }
                sleep(500);
            }
            funcVoiceOut("?????????... ?????? ????????? ???????????????...");

            stoplocation();
            finish();
            startActivity(new Intent(this, Main.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));




            /*ListAdapter adapter = new SimpleAdapter(
                    Compass.this, mArrayList, R.layout.item_list,
                    new String[]{TAG_NAME, TAG_DISTANCE}
                    , new int[]{R.id.textView_list_name, R.id.textView_list_distance}
            );


            mListViewList.setAdapter(adapter);*/

        } catch (JSONException | InterruptedException e) {

            Log.d(TAG_NAME, "showResult : ", e);
        }

        // interact with insert.php
        /*class InsertData extends AsyncTask<String, Void, String> {
            ProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                progressDialog = ProgressDialog.show(Compass.this,
                        "Please Wait", null, true, true);
            }


            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                progressDialog.dismiss();
                mTextViewResult.setText(result);
                Log.d(TAG, "POST response = " + result);
            }


            @Override
            protected String doInBackground(String... params) {

                String name = (String) params[1];
                String lat = (String) params[2];
                String lon = (String) params[2];

                System.out.println("############ name ############" + name);
                System.out.println("############ lat ############" + lat);
                System.out.println("############ lon ############" + lon);

                String serverURL = (String) params[0];
                String postParameters = "name=" + name + "&latitude=" + lat + "&longitude=" + lon;

                try {
                    URL url = new URL(serverURL);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                    httpURLConnection.setReadTimeout(5000);
                    httpURLConnection.setConnectTimeout(5000);
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.connect();

                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    outputStream.write(postParameters.getBytes("UTF-8"));
                    outputStream.flush();
                    outputStream.close();


                    int responseStatusCode = httpURLConnection.getResponseCode();
                    Log.d(TAG, "POST response code - " + responseStatusCode);

                    InputStream inputStream;
                    if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                        inputStream = httpURLConnection.getInputStream();
                    } else {
                        inputStream = httpURLConnection.getErrorStream();
                    }


                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    while ((line = bufferedReader.readLine()) != null) {
                        sb.append(line);
                    }
                    bufferedReader.close();
                    return sb.toString();
                } catch (Exception e) {
                    Log.d(TAG, "InsertData: Error ", e);
                    return new String("Error: " + e.getMessage());
                }
            }
        }*/

    }

    public String lookup_name(String name) {
        String result = null;
        switch(name) {
            case "nature science" :
                result = "?????? ?????????";
                break;
            case "architect" :
                result = "?????????";
                break;
            case "haebong gwan" :
                result = "????????? ?????? ????????? ?????? ??????";
                break;
            case "sangheo research" :
                result = "?????? ?????????";
                break;
            case "social science" :
                result = "?????? ?????? ??????";
                break;
            case "executive department" :
                result = "?????????";
                break;
            case "engineering" :
                result = "?????? ??????";
                break;
            case "student hall" :
                result = "?????? ??????";
                break;
            case "sangheo library" :
                result = "?????? ?????????";
                break;
            case "liberal arts" :
                result = "?????? ??????";
                break;
            case "language education" :
                result = "?????? ?????????";
                break;
            case "museum" :
                result = "?????????";
                break;
            case "jonghap" :
                result = "?????? ?????????";
                break;
            case "life science" :
                result = "?????? ?????????";
                break;
            case "animal life" :
                result = "?????? ?????? ?????? ??????";
                break;
            case "iphak jungbo" :
                result = "?????? ?????????";
                break;
            case "sanhak" :
                result = "?????? ?????????";
                break;
            case "art" :
                result = "?????? ????????? ??????";
                break;
            case "new millennium" :
                result = "????????? ?????????";
                break;
            case "near home" :
                result = "???????????? ??????";
                break;
            default :
                break;
        }

        return result;
    }

    @Override
    public void funcVoiceOut(final String OutMsg) {                    // ????????? ???????????? ??????
        tts.speak(OutMsg, TextToSpeech.QUEUE_FLUSH, null);
        while(tts.isSpeaking()) {}
    }

}

