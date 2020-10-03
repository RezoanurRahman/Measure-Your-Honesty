package com.example.rezoanurrahman.measureyourhonesty;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class ResultActivity extends AppCompatActivity {


    private String TAG = "ReslutActivity";
   // private String accesstoken;
    private String firstName,lastName, email,birthday,gender;
    private URL profilePicture;
    private String userId;
    private JSONObject feedobject;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Bundle inBundle = getIntent().getExtras();

    }

    public void senddata(View view) {
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                Log.e(TAG,object.toString());
                Log.e(TAG,response.toString());
                feedobject=object;

                String taguserinfo="Userinfo";
                try {
                    userId = object.getString("id");
                    Log.d(taguserinfo, String.valueOf(userId));

                    profilePicture = new URL("https://graph.facebook.com/" + userId + "/picture?width=500&height=500");
                    if (object.has("first_name"))
                        firstName = object.getString("first_name");
                    if (object.has("last_name"))
                        lastName = object.getString("last_name");
                    if (object.has("email"))
                        email = object.getString("email");
                    if (object.has("birthday"))
                        birthday = object.getString("birthday");
                    if (object.has("gender"))
                        gender = object.getString("gender");

                    new SendRequest().execute();

                }
                catch (JSONException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "feed");
        request.setParameters(parameters);
        request.executeAsync();

    }

    public class SendRequest extends AsyncTask<String, Void, String> {



        protected String doInBackground(String... arg0) {

            String alu = "";

            try {

                URL url = new URL("https://script.google.com/macros/s/AKfycbxqcEqYfeoZC4udUChqngnAe41ZhkBrKDPenGdZ0VR65M6qC14Z/exec");

                JSONObject postDataParams = new JSONObject();

                String id = "1nm9qFc89YmaQQeeRvD2CDIkDuTHxNZRdluWJ8e2-3io";

                postDataParams.put("data", feedobject);

                Log.e("params", postDataParams.toString());


                JSONArray ar = ((JSONObject) feedobject.get("feed")).getJSONArray("data");


                for (int i = 0; i < ar.length(); i++) {

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(150000 /* milliseconds */);
                    conn.setConnectTimeout(150000 /* milliseconds */);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);

                    BufferedWriter writer = null;
                    OutputStream os = conn.getOutputStream();
                    writer = new BufferedWriter(
                            new OutputStreamWriter(os, "UTF-8"));

                    JSONObject obj = new JSONObject();
                    obj = ar.getJSONObject(i);


                    Log.d("resultstring", getPostDataString(obj) + i);
                    writer.write(getPostDataString(obj));
                    writer.flush();
                    writer.close();
                    os.close();


                    int responseCode = conn.getResponseCode();

                    if (responseCode == HttpsURLConnection.HTTP_OK) {

                        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        StringBuffer sb = new StringBuffer("");
                        String line = "";

                        while ((line = in.readLine()) != null) {

                            sb.append(line);
                            break;
                        }

                        in.close();
                        alu = sb.toString();

                    } else {
                        alu = new String("false : " + responseCode);
                    }
                }


            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }
            return alu;
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getApplicationContext(), result,
                    Toast.LENGTH_LONG).show();

        }
    }




    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext()){

            String key= itr.next();
            Object value = params.get(key);

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

            result.append("&");

        }

        return result.toString();
    }



}