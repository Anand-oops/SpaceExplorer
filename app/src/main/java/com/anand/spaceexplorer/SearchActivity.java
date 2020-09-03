package com.anand.spaceexplorer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.agrawalsuneet.loaderspack.loaders.WifiLoader;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    private SearchAdapter adapter;
    private WifiLoader wifiLoader;
    private TextView noQueryTv;
    private RequestQueue requestQueue;
    private RecyclerView recyclerView;
    private ArrayList<SearchItemClass> searchArrayList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        noQueryTv=findViewById(R.id.no_query_tv);
        EditText searchEditText = findViewById(R.id.search_text);
        wifiLoader =findViewById(R.id.loader);
        recyclerView=findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2,GridLayoutManager.VERTICAL,false ));
        requestQueue = Volley.newRequestQueue(this);
        searchArrayList = new ArrayList<>();

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                noQueryTv.setVisibility(View.GONE);
                wifiLoader.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                wifiLoader.setVisibility(View.VISIBLE);
                searchArrayList.clear();
                if (!editable.toString().trim().isEmpty())
                    parseJson("https://images-api.nasa.gov/search?q=" + editable.toString().trim());
            }
        });
    }

    private void parseJson(String url) {
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        wifiLoader.setVisibility(View.GONE);
                        try {
                            JSONObject collection = response.getJSONObject("collection");
                            JSONArray items = collection.getJSONArray("items");

                            for (int i =0 ;i<items.length();i++){
                                JSONObject item = items.getJSONObject(i);
                                JSONArray data = item.getJSONArray("data");
                                JSONObject object = data.getJSONObject(0);
                                String title = object.getString("title");
                                String nasaId = object.getString("nasa_id");
                                String mediaType = object.getString("media_type");


                                JSONArray links = item.getJSONArray("links");

                                JSONObject obj = links.getJSONObject(0);
                                String href = obj.getString("href");

                                searchArrayList.add(new SearchItemClass(nasaId, title, mediaType, href));
                            }
                            adapter = new SearchAdapter(SearchActivity.this, searchArrayList);
                            recyclerView.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                wifiLoader.setVisibility(View.GONE);
                AlertDialog.Builder dialog = new AlertDialog.Builder(SearchActivity.this);
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
