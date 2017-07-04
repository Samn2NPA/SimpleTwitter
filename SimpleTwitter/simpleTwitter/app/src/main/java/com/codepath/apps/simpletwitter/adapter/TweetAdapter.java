package com.codepath.apps.simpletwitter.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.simpletwitter.R;
import com.codepath.apps.simpletwitter.TwitterApplication;
import com.codepath.apps.simpletwitter.Utils.ResourceUtils;
import com.codepath.apps.simpletwitter.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

/**
 * Created by Samn on 02-Jul-17.
 */

public class TweetAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Tweet> tweets;
    private Context context;
    private TwitterClient client = TwitterApplication.getRestClient();
    private static long TWEET_ID;

    private Listener listener;
    public interface Listener{
        void onLoadMore();
        void onReplyToTweet(Tweet tweetReplyTo);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public TweetAdapter(@NonNull Context context) {
        this.context = context;
        this.tweets = new ArrayList<>();
    }

    public void setData(List<Tweet> newTweets){
        tweets.clear();
        tweets.addAll(newTweets);
        notifyDataSetChanged();
    }

    /* dùng cho Endless swipe (Swipe to load more) */
    public void appendData(List<Tweet> newTweets) {
        int nextPos = tweets.size();
        tweets.addAll(nextPos, newTweets);
        notifyItemRangeChanged(nextPos,newTweets.size());
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
        TWEET_ID = tweet.getTweetId();

        bindViewHolder((ViewHolder) holder, tweet);

        if(position == tweets.size() - 1){
            //if reach the last position => load more
            listener.onLoadMore();
        }
    }

    public void bindViewHolder(final ViewHolder holder, final Tweet tweet){
        holder.tvScreenName.setText(tweet.getUser().getName());
        holder.tvName.setText("@" + tweet.getUser().getScreenName());
        ResourceUtils.loadImageCircle(context, holder.ivAvatar, tweet.getUser().getProfileImageUrl());
        holder.tvBody.setText(tweet.getBody());
        holder.tvFavoriteCount.setText(String.valueOf(tweet.getFavouritesCount()));
        holder.tvRetweetCount.setText(String.valueOf( tweet.getRetweetCount()));
        holder.tvTimestamp.setText(tweet.getTimestamp());

        setUpImageView(holder.ivFavorite, holder.ivRetweet, tweet);

        //@OnClick
        retweet(holder.ivRetweet);
        favorited(holder.ivFavorite);
        holder.ivReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onReplyToTweet(tweet);
            }
        });

        if(tweet.getUrl()!= null)
            ResourceUtils.loadImage(context, holder.ivImagePost, tweet.getUrl());
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
        @BindView(R.id.tvRelativeTimestamp)
        TextView tvTimestamp;


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

    private void setUpImageView(ImageView ivFavorite, ImageView ivRetweet, Tweet tweet) {
        if(tweet.isFavorited()){
            ivFavorite.setImageResource(R.drawable.ic_favorited);
            ivFavorite.setTag(R.drawable.ic_favorited);
        }
        else{
            ivFavorite.setImageResource(R.drawable.ic_favorited_none);
            ivFavorite.setTag(R.drawable.ic_favorited_none);
        }

        if(tweet.isRetweeted()){
            ivRetweet.setImageResource(R.drawable.ic_retweet_click);
            ivRetweet.setTag(R.drawable.ic_retweet_click);
        }
        else{
            ivRetweet.setImageResource(R.drawable.ic_retweet_none);
            ivRetweet.setTag(R.drawable.ic_retweet_none);
        }

    }

    @OnClick(R.id.ivRetweet)
    public void retweet(final ImageView ivRetweet){
        if(ivRetweet.getTag().equals(R.drawable.ic_retweet_click) ){
            client.UnRetweet(String.valueOf(TWEET_ID), new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                ivRetweet.setImageResource(R.drawable.ic_retweet_none);
                ivRetweet.setTag(R.drawable.ic_retweet_none);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("Samn: failure::", errorResponse.toString());
            }
        });
        }
        else {
           client.Retweet(String.valueOf(TWEET_ID), new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                ivRetweet.setImageResource(R.drawable.ic_retweet_click);
                ivRetweet.setTag(R.drawable.ic_retweet_click);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("Samn: failure::", errorResponse.toString());
            }
        });
       }
    }

    @OnClick(R.id.ivFavorite)
    public void favorited(final ImageView ivFavorite){
        if(ivFavorite.getTag().equals(R.drawable.ic_favorited) ){
            client.favoriteDestroy(String.valueOf(TWEET_ID), new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    ivFavorite.setImageResource(R.drawable.ic_favorited_none);
                    ivFavorite.setTag(R.drawable.ic_favorited_none);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("Samn: failure::", errorResponse.toString());
                }
            });
        }
        else {
            client.favoriteCreate(String.valueOf(TWEET_ID), new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    ivFavorite.setImageResource(R.drawable.ic_favorited);
                    ivFavorite.setTag(R.drawable.ic_favorited);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("Samn: failure::", errorResponse.toString());
                }
            });
        }
    }
}
