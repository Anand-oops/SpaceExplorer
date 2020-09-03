package com.anand.spaceexplorer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.agrawalsuneet.loaderspack.loaders.WifiLoader;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SearchImageDisplay extends AppCompatActivity {

    private static final String TAG = "SearchImage";
    private ImageView searchImage;
    private WifiLoader wifiLoader;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_image);

        searchImage = findViewById(R.id.search_image_view);
        wifiLoader = findViewById(R.id.loader);
        requestQueue = Volley.newRequestQueue(this);

        String nasaId = getIntent().getStringExtra("nasaId");

        Log.i(TAG, "onCreate: nasaID"+nasaId);

        parseJson("https://images-api.nasa.gov/asset/" + nasaId);
    }

    private void parseJson(String url) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject collection = response.getJSONObject("collection");
                            JSONArray items = collection.getJSONArray("items");

                            for (int i = 0; i < items.length(); i++) {
                                JSONObject item = items.getJSONObject(i);
                                String href = item.getString("href").replace("http","https");
                                String mediaType = href.substring(href.length() - 3);
                                if (mediaType.equals("jpg")) {
                                    wifiLoader.setVisibility(View.GONE);
                                    Picasso.with(SearchImageDisplay.this).load(href).placeholder(R.drawable.nasa).into(searchImage);
                                    break;
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                wifiLoader.setVisibility(View.GONE);
                AlertDialog.Builder dialog = new AlertDialog.Builder(SearchImageDisplay.this);
                if (isConnected())
                    dialog.setMessage("No data found...");
                else dialog.setMessage("Check your Internet Connection");

                dialog.setTitle("Alert !!!");
                dialog.setIcon(android.R.drawable.ic_dialog_alert);
                dialog.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                finish();
                            }
                        });
                AlertDialog alertDialog = dialog.create();
                alertDialog.setCancelable(false);
                alertDialog.show();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    public boolean isConnected() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
