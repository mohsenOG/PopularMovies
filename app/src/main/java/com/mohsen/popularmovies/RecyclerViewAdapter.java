package com.mohsen.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mohsen.popularmovies.common.Utils;
import com.mohsen.popularmovies.model.MovieInfo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mohsen on 20.02.2018.
 *
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ItemClickListener mItemClickListener;
    private List<String> mMoviesposterRelativePath = new ArrayList<>();

    public RecyclerViewAdapter(Context context, List<String> moviesPosterRelPath) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mMoviesposterRelativePath = moviesPosterRelPath;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.recyclerview_item, parent, false);
        return new ViewHolder(mContext, view);
    }

    // Bind data to each view holder.
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindingImages(mMoviesposterRelativePath.get(position));
    }

    @Override
    public int getItemCount() {
        return mMoviesposterRelativePath.size();
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    public void swapData(List<String> newData) {
        if (newData == null || newData.size() == 0)
            return;
        mMoviesposterRelativePath.clear();
        mMoviesposterRelativePath.addAll(newData);
        notifyDataSetChanged();
    }



    class ViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener{
        ImageView mImageView;
        Context mContext;

        public ViewHolder(Context context, View itemView) {
            super(itemView);
            mContext = context;
            mImageView = itemView.findViewById(R.id.poster_image_view);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(mItemClickListener != null)
                mItemClickListener.onItemClick(v, getAdapterPosition());
        }

        public void bindingImages(String imageRelPath)
        {
            String absPath = MovieInfo.posterPathConverter(imageRelPath);
            Picasso.with(mContext).load(absPath).into(mImageView);
        }
    }
}
