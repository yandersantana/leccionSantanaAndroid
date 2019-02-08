package com.gorunteamsandroid.developer.leccionandroidhilos;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "AsyncTaskActivity";

    public final static String path = "https://leccionserver.herokuapp.com/guardarUsuario";
    public final static String path2 = "https://leccionserver.herokuapp.com/mostrarUsuario/";
    ServicioWeb servicio;
    String respuesta;

    private Button btnPlay;
    private Button btnHilo;
    public TextView txtnom;
    public TextView txtrespuesta;
    public String respuesta2;
    private MediaPlayer mediaplayer;
    public int contador=0;
    public int rcorrecto=0;
    public int rincorrecto=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Inicializamos la clase MediaPlayer asociandole el fichero de Audio
        mediaplayer = MediaPlayer.create(this, R.raw.audio);

        //Obtenemos los tres botones de la interfaz
        btnHilo= (Button)findViewById(R.id.btnCalcular);

       // btnPlay= (Button)findViewById(R.id.btnAudio);

       // txtnom= (TextView) findViewById(R.id.txtNombre);




        //Log.d(TAG, "por aqui entra" + txtrespuesta.getText());
        //respuesta2= String.valueOf(txtrespuesta.getText());


        btnHilo.setOnClickListener(this);
    }

    public void onClick(View v) {

        switch(v.getId()){
            case R.id.btnCalcular:
                txtrespuesta= (TextView) findViewById(R.id.txtrespuesta);
                respuesta2=String.valueOf(txtrespuesta.getText());
                Log.d(TAG, "recibi"+txtrespuesta.getText());
                //Iniciamos el audio
                if(respuesta2.equals("11")){
                    /*NotificationCompat.Builder mBuilder;
                    NotificationManager mNotifyMgr =(NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);

                    int icono = R.mipmap.ic_launcher;
                   // Intent i=new Intent(MainActivity.this, MensajeActivity.class);
                   // PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, 0, i, 0);

                    mBuilder =new NotificationCompat.Builder(getApplicationContext())
                            .setSmallIcon(icono)
                            .setContentTitle("Titulo")
                            .setContentText("Correcto")
                            .setVibrate(new long[] {100, 250, 100, 500})
                            .setAutoCancel(true);
                  mBuilder.build();*/

                    Toast toast1 =
                            Toast.makeText(getApplicationContext(),
                                    "Correcto", Toast.LENGTH_SHORT);

                    toast1.show();
                    mediaplayer.start();
                    contador++;
                    rcorrecto++;

                }else{
                    Toast toast1 =
                            Toast.makeText(getApplicationContext(),
                                    "Incorrecto", Toast.LENGTH_SHORT);

                    toast1.show();
                    contador++;
                    rincorrecto++;
                }
                Log.d(TAG, "ESTOY CONTANDO"+contador);

                if(contador==3){
                    /*Intent itemintent = new Intent(this, login2.class);
                    this.startActivity(itemintent);*/
                    final View popupView = LayoutInflater.from(this).inflate(R.layout.respuestas, null);
                    final PopupWindow popupWindow = new PopupWindow(popupView ,1000,600);
                   // popupWindow.setFocusable(true);
                    TextView texto = (TextView) popupView.findViewById(R.id.txtcorrecto);
                    texto.setText(String.valueOf(rcorrecto));
                    TextView texto2 = (TextView) popupView.findViewById(R.id.txtincorrectas);
                    texto2.setText(String.valueOf(rincorrecto));
                    popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

                }

                break;
            /*case R.id.btnEnviar:
                servicio = (ServicioWeb) new ServicioWeb().execute();

                break;*/
        }
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
            stringMap.put("nombre", String.valueOf(txtnom.getText()));
            stringMap.put("apellido", String.valueOf(txtnom.getText()));
            stringMap.put("correo", String.valueOf(txtnom.getText()));

            Log.d(TAG, "por aqui entra");

            String requestBody = FormRegister.Utils.buildPostParameters(stringMap);
            try {
                urlConnection = (HttpURLConnection) FormRegister.Utils.makeRequest("POST", path, null, "application/x-www-form-urlencoded", requestBody);
                InputStream inputStream;
                Log.d(TAG, requestBody);
                Log.d(TAG, "por aqui entra 2");
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



        @Override
        protected void onPostExecute(String respuesta) {
            super.onPostExecute(respuesta);

            Log.d(TAG, "onPostExecute");
            if (respuesta=="correcto"){

                Toast toast1 =
                        Toast.makeText(getApplicationContext(),
                                "registro exitoso", Toast.LENGTH_SHORT);

                toast1.show();




            }else{
                Toast toast1 =
                        Toast.makeText(getApplicationContext(),
                                "no se registro", Toast.LENGTH_SHORT);

                toast1.show();


            }

        }
    }






    private class ServicioWeb2 extends AsyncTask<Integer, Integer, String> {

        @Override
        protected void onPreExecute() {
            //GuardarEditar.setEnabled(false);
        }
        @Override
        protected String doInBackground(Integer... params) {
            return getWebServiceResponseData();
        }

        protected String getWebServiceResponseData() {
            respuesta="";
            //String url = "http://10.0.2.2/api/token";
            HttpURLConnection urlConnection = null;
            Map<String, String> stringMap = new HashMap<>();


            Log.i("MainActivity", "onCreate -> else -> Todos los EditText estan llenos.");
            stringMap.put("nombre", "opopop");
            stringMap.put("apellido", "opopop");
            stringMap.put("descripcion", "opopop");

            String requestBody = FormRegister.Utils.buildPostParameters(stringMap);
            try {
                urlConnection = (HttpURLConnection) FormRegister.Utils.makeRequest("PUT", path2, null, "application/x-www-form-urlencoded", requestBody);
                InputStream inputStream;
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
                respuesta="ok";
            } catch (Exception e) {
                e.printStackTrace();
                return e.toString();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

            return respuesta;
        }

        @Override
        protected void onPostExecute(String nombre) {
            //GuardarEditar.setEnabled(true);
            super.onPostExecute(nombre);
            Log.d(TAG, "onPostExecute");
            if (nombre=="ok"){

                Log.d(TAG, "ok");
            }else{

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


