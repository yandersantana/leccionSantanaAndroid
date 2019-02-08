package com.gorunteamsandroid.developer.leccionandroidhilos;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class FormRegister extends AppCompatActivity {
    private static final String TAG = "AsyncTaskActivity";

    public final static String path = "https://restgorun.herokuapp.com/guardarUsuario";
    public final static String path2 = "https://restgorun.herokuapp.com/listarusuarios";
    java.net.URL url;
    ArrayList listaUsuarios=new ArrayList();
    String responseText;
    StringBuffer response;

    public EditText txtMail;
    EditText txtPass;
    EditText txtName;
    String respuesta;
    ServicioWeb servicio;
    boolean validarEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_register);

        txtMail=(EditText) findViewById(R.id.txtUser);
        txtPass=(EditText) findViewById(R.id.txtPass);
        txtName=(EditText) findViewById(R.id.txtName);

        Button goresume = (Button) findViewById(R.id.login);
        goresume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent itemintent = new Intent(FormRegister.this, login2.class);
                // FormRegister.this.startActivity(itemintent);
                if(isConnectedToInternet())
                {
                    // Run AsyncTask
                    servicio = (ServicioWeb) new ServicioWeb().execute();
                }
                else
                {


                }
            }
        });
    }



    public boolean isConnectedToInternet(){
        ConnectivityManager connectivity = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }

        }
        return false;
    }

    private class ServicioWeb extends AsyncTask<Integer, Integer, String> {



        @Override
        protected String doInBackground(Integer... params) {
            return getWebServiceResponseData();
        }

        protected String getWebServiceResponseData() {
            respuesta="";

            HttpURLConnection urlConnection = null;
            Map<String, String> stringMap = new HashMap<>();
            // Obtienes el layout que contiene los EditText






            String varComparar=this.getWebServiceResponseData2(String.valueOf(txtMail.getText()));
            if(varComparar=="existe"){

                return "logear";
            }else{
                if(validarEmail==true){
                    Log.i("MainActivity", "onCreate -> else -> Todos los EditText estan llenos.");
                    stringMap.put("mail", String.valueOf(txtMail.getText()));
                    stringMap.put("pass", String.valueOf(txtPass.getText()));
                    stringMap.put("nombre", String.valueOf(txtName.getText()));
                    String requestBody = Utils.buildPostParameters(stringMap);
                    try {
                        urlConnection = (HttpURLConnection) Utils.makeRequest("POST", path, null, "application/x-www-form-urlencoded", requestBody);
                        InputStream inputStream;
                        Log.d(TAG, requestBody);
                        // get stream
                        if (urlConnection.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST) {
                            inputStream = urlConnection.getInputStream();
                        } else {
                            inputStream = urlConnection.getErrorStream();
                        }
                        // parse stream
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                        String temp, response = "";
                        while ((temp = bufferedReader.readLine()) != null) {
                            response += temp;
                        }
                        respuesta="correcto";
                        return respuesta;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return e.toString();
                    } finally {
                        if (urlConnection != null) {
                            urlConnection.disconnect();
                        }
                    }

                }
                else {

                    return "Dd";
                }


            }
        }

        protected String getWebServiceResponseData2(String dato) {
            try {
                url=new URL(path2);
                Log.d(TAG, "ServerData: " + path2);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");

                int responseCode = conn.getResponseCode();

                Log.d(TAG, "Response code: " + responseCode);
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    // Reading response from input Stream
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(conn.getInputStream()));
                    String output;
                    response = new StringBuffer();

                    while ((output = in.readLine()) != null) {
                        response.append(output);
                    }
                    in.close();
                }}
            catch(Exception e){
                e.printStackTrace();
            }
            String mailcompare= dato;
            responseText = response.toString();
            Log.d(TAG, "data:" + responseText);
            try {
                JSONArray jsonarray = new JSONArray(responseText);
                for (int i=0;i<jsonarray.length();i++){
                    JSONObject jsonobject = jsonarray.getJSONObject(i);
                    String mail = jsonobject.getString("mail");
                    //String pass=jsonobject.getString("pass");
                    if (String.valueOf(mailcompare).equals(String.valueOf(mail))){
                        respuesta="existe";

                    }else{

                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return respuesta;
        }

        public  boolean isValidEmail(CharSequence target) {
            if (target == null)
                return false;

            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }


        @Override
        protected void onPostExecute(String respuesta) {
            super.onPostExecute(respuesta);
            Log.d(TAG, "onPostExecute");
            if (respuesta=="correcto"){

            }else{



                // Log.d(TAG, "Registro fail:" + nombre);

            }

        }
    }


    public static class Utils{
        public static String buildPostParameters(Object content) {
            String output = null;
            if ((content instanceof String) ||
                    (content instanceof JSONObject) ||
                    (content instanceof JSONArray)) {
                output = content.toString();
            } else if (content instanceof Map) {
                Uri.Builder builder = new Uri.Builder();
                HashMap hashMap = (HashMap) content;
                if (hashMap != null) {
                    Iterator entries = hashMap.entrySet().iterator();
                    while (entries.hasNext()) {
                        Map.Entry entry = (Map.Entry) entries.next();
                        builder.appendQueryParameter(entry.getKey().toString(), entry.getValue().toString());
                        entries.remove(); // avoids a ConcurrentModificationException
                    }
                    output = builder.build().getEncodedQuery();
                }
            }

            return output;
        }

        public static URLConnection makeRequest(String method, String apiAddress, String accessToken, String mimeType, String requestBody) throws IOException {
            URL url = null;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(apiAddress);

                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(!method.equals("GET"));
                urlConnection.setRequestMethod(method);

                urlConnection.setRequestProperty("Authorization", "Bearer " + accessToken);

                urlConnection.setRequestProperty("Content-Type", mimeType);
                OutputStream outputStream = new BufferedOutputStream(urlConnection.getOutputStream());
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "utf-8"));
                writer.write(requestBody);
                writer.flush();
                writer.close();
                outputStream.close();

                urlConnection.connect();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            return urlConnection;
        }
    }



}

