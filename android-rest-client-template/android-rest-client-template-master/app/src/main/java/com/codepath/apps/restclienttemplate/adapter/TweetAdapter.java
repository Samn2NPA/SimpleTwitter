package com.codepath.apps.restclienttemplate.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApplication;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.Utils.ResourceUtils;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

/**
 * Created by Samn on 02-Jul-17.
 */

public class TweetAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Tweet> tweets;
    private Context context;

    public TweetAdapter(@NonNull Context context) {
        this.context = context;
        this.tweets = new ArrayList<>();
    }

    public void setData(List<Tweet> newTweets){
        tweets.clear();
        tweets.addAll(newTweets);
        Log.d("SAMN: List:: ", newTweets.size() + "");
        notifyDataSetChanged();
    }

    private Tweet getTweetAtPosition(int itemPos) {
        return tweets.get(itemPos);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_timeline, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Tweet tweet = tweets.get(position);
        bindViewHolder((ViewHolder) holder, tweet);
    }

    public void bindViewHolder(final ViewHolder holder, final Tweet tweet){
        holder.tvScreenName.setText(tweet.getUser().getName());
        holder.tvName.setText("@" + tweet.getUser().getScreenName());
        ResourceUtils.loadImageCircle(context, holder.ivAvatar, tweet.getUser().getProfileImageUrl());
        holder.tvBody.setText(tweet.getBody());
        holder.tvFavoriteCount.setText(String.valueOf(tweet.getFavouritesCount()));
        holder.tvRetweetCount.setText(String.valueOf( tweet.getRetweetCount()));

        if(tweet.isFavorited()){
            holder.ivFavorite.setImageResource(R.drawable.ic_favorited);
        }
        else
            holder.ivFavorite.setImageResource(R.drawable.ic_favorited_none);

        final boolean isFavorited = tweet.isFavorited();
        final TwitterClient client = TwitterApplication.getRestClient();

        holder.ivFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFavorited){
                    client.favoriteDestroy(String.valueOf(tweet.getTweetId()), new JsonHttpResponseHandler(){
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            holder.ivFavorite.setImageResource(R.drawable.ic_favorited_none);
                        }
                    });
                }
                else{
                    client.favoriteCreate(String.valueOf(tweet.getTweetId()), new JsonHttpResponseHandler(){
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            holder.ivFavorite.setImageResource(R.drawable.ic_favorited);
                        }
                    });
                }
            }
        });

        if(tweet.getUrl()!=null)
            ResourceUtils.loadImageCircle(context, holder.ivImagePost, tweet.getUrl());
        else
            holder.ivImagePost.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.ivAvatar)
        ImageView ivAvatar;
        @BindView(R.id.tvScreenName)
        TextView tvScreenName;
        @BindView(R.id.tvName)
        TextView tvName;
        @BindView(R.id.tvBody)
        TextView tvBody;
        @BindView(R.id.ivImagePost)
        ImageView ivImagePost;
        @BindView(R.id.ivReply)
        ImageView ivReply;
        @BindView(R.id.ivRetweet)
        ImageView ivRetweet;
        @BindView(R.id.ivFavorite)
        ImageView ivFavorite;
        @BindView(R.id.tvRetweetCount)
        TextView tvRetweetCount;
        @BindView(R.id.tvFavoriteCount)
        TextView tvFavoriteCount;


        public ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int itemPos = getAdapterPosition();
                    Tweet tweet = getTweetAtPosition(itemPos);
                }
            });
        }
    }

}
