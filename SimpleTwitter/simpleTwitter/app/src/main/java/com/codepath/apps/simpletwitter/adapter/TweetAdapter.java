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
import cz.msebera.android.httpclient.Header;

/**
 * Created by Samn on 02-Jul-17.
 */

public class TweetAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Tweet> tweets;
    private Context context;

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

        final TwitterClient client = TwitterApplication.getRestClient();
        if(tweet.isFavorited()){
            holder.ivFavorite.setImageResource(R.drawable.ic_favorited);
            holder.ivFavorite.setTag(R.drawable.ic_favorited);
        }
        else{
            holder.ivFavorite.setImageResource(R.drawable.ic_favorited_none);
            holder.ivFavorite.setTag(R.drawable.ic_favorited_none);
        }

        holder.ivFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.ivFavorite.getTag().equals(R.drawable.ic_favorited) ){
                    client.favoriteDestroy(String.valueOf(tweet.getTweetId()), new JsonHttpResponseHandler(){
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            holder.ivFavorite.setImageResource(R.drawable.ic_favorited_none);
                            holder.ivFavorite.setTag(R.drawable.ic_favorited_none);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            Log.d("Samn: failure::", errorResponse.toString());
                        }
                    });
                }
                else {
                    client.favoriteCreate(String.valueOf(tweet.getTweetId()), new JsonHttpResponseHandler(){
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            holder.ivFavorite.setImageResource(R.drawable.ic_favorited);
                            holder.ivFavorite.setTag(R.drawable.ic_favorited);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            Log.d("Samn: failure::", errorResponse.toString());
                        }
                    });
                }
            }
        });

        if(tweet.isRetweeted()){
            holder.ivRetweet.setImageResource(R.drawable.ic_retweet_click);
            holder.ivRetweet.setTag(R.drawable.ic_retweet_click);
        }
        else{
            holder.ivRetweet.setImageResource(R.drawable.ic_retweet_none);
            holder.ivRetweet.setTag(R.drawable.ic_retweet_none);
        }

        holder.ivRetweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.ivRetweet.getTag().equals(R.drawable.ic_retweet_click) ){
                    client.UnRetweet(String.valueOf(tweet.getTweetId()), new JsonHttpResponseHandler(){
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            holder.ivRetweet.setImageResource(R.drawable.ic_retweet_none);
                            holder.ivRetweet.setTag(R.drawable.ic_retweet_none);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            Log.d("Samn: failure::", errorResponse.toString());
                        }
                    });
                }
                else {
                    client.Retweet(String.valueOf(tweet.getTweetId()), new JsonHttpResponseHandler(){
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            holder.ivRetweet.setImageResource(R.drawable.ic_retweet_click);
                            holder.ivRetweet.setTag(R.drawable.ic_retweet_click);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            Log.d("Samn: failure::", errorResponse.toString());
                        }
                    });
                }
            }
        });

        holder.ivReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onReplyToTweet(tweet);
            }
        });

        if(tweet.getUrl()!= null)
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
}
