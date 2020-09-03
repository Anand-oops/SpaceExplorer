package com.anand.spaceexplorer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.agrawalsuneet.loaderspack.loaders.WifiLoader;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.squareup.picasso.Picasso;
import com.android.volley.toolbox.Volley;
import com.google.android.youtube.player.YouTubeInitializationResult;

import org.json.JSONException;
import org.json.JSONObject;

public class ApodActivity extends YouTubeBaseActivity {

    private YouTubePlayerView youTubePlayerView;
    YouTubePlayer.OnInitializedListener listener;
    private RelativeLayout apodLayout;
    private WifiLoader wifiLoader;
    private ImageView apodImage;
    private TextView apodTitle, apodDesc;
    private RequestQueue requestQueue;
    private String date, title, desc, mediaType, mediaUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apod);

        apodLayout = findViewById(R.id.apod_layout);
        wifiLoader = findViewById(R.id.loader);
        youTubePlayerView = findViewById(R.id.youtube_player);
        apodImage = findViewById(R.id.apod_image_view);
        apodTitle = findViewById(R.id.apod_title);
        apodDesc = findViewById(R.id.apod_desc);
        requestQueue = Volley.newRequestQueue(this);
        date = getIntent().getStringExtra("date");

        listener= new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                String ytTemp=mediaUrl.replace("https://www.youtube.com/embed/","");
                String ytFinal=ytTemp.replace("?rel=0","");
                youTubePlayer.loadVideo(ytFinal);
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        };
        parseJason();
    }

    private void parseJason() {
        final String APOD_URL = "https://api.nasa.gov/planetary/apod?api_key=fsS37fRt6xqdhNxf5KzWO23bAGZJkBWVoVw0I0zS&date=" + date;
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, APOD_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            title = response.getString("title");
                            desc = response.getString("explanation");
                            mediaType = response.getString("media_type");
                            mediaUrl = response.getString("url");

                            wifiLoader.setVisibility(View.GONE);
                            apodLayout.setVisibility(View.VISIBLE);

                            if (mediaType.contentEquals("image")) {
                                youTubePlayerView.setVisibility(View.GONE);
                                Picasso.with(ApodActivity.this).load(mediaUrl).fit().placeholder(R.drawable.nasa).centerCrop().into(apodImage);
                            } else if (mediaType.contentEquals("video")) {
                                apodImage.setVisibility(View.GONE);
                                youTubePlayerView.initialize(YoutubeApi.getYtKey(),listener);
                            } else {
                                Toast.makeText(ApodActivity.this, "No media available", Toast.LENGTH_SHORT).show();
                            }
                            apodTitle.setText(title);
                            apodDesc.setText(desc);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                wifiLoader.setVisibility(View.GONE);
                AlertDialog.Builder dialog = new AlertDialog.Builder(ApodActivity.this);
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
