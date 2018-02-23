package com.mohsen.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mohsen.popularmovies.model.MovieInfo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mohsen on 20.02.2018.
 *
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private final Context mContext;
    private final LayoutInflater mLayoutInflater;
    private ItemClickListener mItemClickListener;
    private List<String> mMoviesPosterRelativePath = new ArrayList<>();

    public RecyclerViewAdapter(Context context, List<String> moviesPosterRelPath) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mMoviesPosterRelativePath = moviesPosterRelPath;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.recyclerview_item, parent, false);
        return new ViewHolder(mContext, view);
    }

    // Bind data to each view holder.
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindingImages(mMoviesPosterRelativePath.get(position));
    }

    @Override
    public int getItemCount() {
        return mMoviesPosterRelativePath.size();
    }

    public interface ItemClickListener {
        void onItemClick(String posterRelPath);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    public void swapData(List<String> newData) {
        if (newData == null || newData.size() == 0)
            return;
        mMoviesPosterRelativePath.clear();
        mMoviesPosterRelativePath.addAll(newData);
        notifyDataSetChanged();
    }



    class ViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener{
        @BindView(R.id.poster_image_view) ImageView mImageView;
        private String mImageRelPath;
        private Context mContext;

        public ViewHolder(Context context, View itemView) {
            super(itemView);
            mContext = context;
            mImageRelPath = null;
            itemView.setOnClickListener(this);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void onClick(View v) {
            if(mItemClickListener != null && mImageRelPath != null && !mImageRelPath.isEmpty())
                mItemClickListener.onItemClick(mImageRelPath);
        }

        public void bindingImages(String imageRelPath)
        {
            mImageRelPath = imageRelPath;
            String absPath = MovieInfo.posterPathConverter(imageRelPath);
            Picasso.with(mContext).load(absPath).into(mImageView);
        }
    }
}
