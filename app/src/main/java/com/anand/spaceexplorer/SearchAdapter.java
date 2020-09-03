package com.anand.spaceexplorer;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {

    private Context context;
    private ArrayList<SearchItemClass> searchArrayList;

    public SearchAdapter(Context context, ArrayList<SearchItemClass> list){
        this.context = context;
        this.searchArrayList = list;
    }

    @NonNull
    @Override
    public SearchAdapter.SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SearchViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_item_activity, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final SearchAdapter.SearchViewHolder holder, int position) {
        final SearchItemClass searchItem = searchArrayList.get(position);
        holder.searchTitle.setText(searchItem.getTitle());
        Picasso.with(context).load(searchItem.getHref()).fit().centerCrop().placeholder(R.drawable.nasa).into(holder.searchImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String nasaId= searchItem.getNasaId();
                Intent intent = new Intent(context,SearchImageDisplay.class);
                intent.putExtra("nasaId",nasaId);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return searchArrayList.size();
    }

    public static class SearchViewHolder extends RecyclerView.ViewHolder {

        TextView searchTitle;
        ImageView searchImage;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            searchTitle = itemView.findViewById(R.id.search_title);
            searchImage = itemView.findViewById(R.id.search_image);
        }
    }
}
