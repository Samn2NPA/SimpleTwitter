package com.codepath.apps.simpletwitter.models;

/**
 * Created by Samn on 02-Jul-17.
 */

import android.text.format.DateUtils;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
{
        "created_at": "Tue Aug 28 21:16:23 +0000 2012",
        "favorited": false,
        "id_str": "240558470661799936",
        "in_reply_to_user_id_str": null,
        "entities": {
        "urls": [
        ],
        "hashtags": [
        ],
        "user_mentions": [
        ]
        },
        "text": "just another test",
        "contributors": null,
        "id": 240558470661799936,
        "retweet_count": 0,
        "in_reply_to_status_id_str": null,
        "geo": null,
        "retweeted": false,
        "in_reply_to_user_id": null,
        "place": null,
        "source": "OAuth Dancer Reborn",
        "user": {

 },
        "in_reply_to_screen_name": null,
        "in_reply_to_status_id": null
        }
*/

public class Tweet {

    @SerializedName("text")
    private String body;

    @SerializedName("id")
    private long tweetId;

    @SerializedName("created_at")
    private String createAt;

    @SerializedName("user")
    private User user;

    @SerializedName("favorited")
    private boolean isFavorited;

    @SerializedName("retweeted")
    private boolean isRetweeted;

    @SerializedName("retweet_count")
    private long retweetCount;

    @SerializedName("favourites_count")
    private long favouritesCount;

    private String name;

    private String screenName;

    private long uid;

    private String profileImageUrl;

    private static int page = 0;

    @SerializedName("url")
    private String url;

    public String getBody() {
        return body;
    }

    public long getTweetId() {
        return tweetId;
    }

    public String getCreateAt() {
        return createAt;
    }

    public boolean isFavorited() {
        return isFavorited;
    }

    public boolean isRetweeted() {
        return isRetweeted;
    }

    public long getRetweetCount() {
        return retweetCount;
    }

    public long getFavouritesCount() {
        return favouritesCount;
    }

    public String getUrl() {
        return url;
    }

    public User getUser() {
        return user;
    }

    public String getTimestamp(){
        return getRelativeTimeAgo(getCreateAt());
    }

    private String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }

    public static int nextPage(){ page += 1; return page; }
    public static int resetPage(){ page = 0; return page; }
    public static int getPage(){return page; }

    public static Tweet fromJSON (JSONObject jsonObject) {
       Tweet tweet = new Tweet();
       try {
           tweet.body = jsonObject.getString("text");
           tweet.tweetId = jsonObject.getLong("id");
           tweet.createAt = jsonObject.getString("created_at");
           tweet.isFavorited = jsonObject.getBoolean("favorited");
           tweet.isRetweeted = jsonObject.getBoolean("retweeted");
           tweet.retweetCount = jsonObject.getLong("retweet_count");
           //tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
       } catch (JSONException e) {
           e.printStackTrace();
       }
       return tweet;
   }

   public static ArrayList<Tweet> fromJSONArray(JSONArray jsonArray){
       ArrayList<Tweet> tweets = new ArrayList<>();

       for(int i = 0; i<jsonArray.length(); i++){
           try {
               JSONObject tweetJson = jsonArray.getJSONObject(i);
               Tweet tweet = Tweet.fromJSON(tweetJson);
               if(tweet!=null)
                   tweets.add(tweet);
           } catch (JSONException e) {
               e.printStackTrace();
           }
       }
       return tweets;
   }

    public static ArrayList<Tweet> fromJsonArray(JSONArray jsonArray){
        ArrayList<Tweet> tweets = new ArrayList<>();
        for(int i = 0; i<jsonArray.length(); i++){
            Tweet tweet = null;
            try {
                tweet = new Gson().fromJson(jsonArray.get(i).toString(), Tweet.class );
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(tweet != null)
                tweets.add(tweet);
        }
        return tweets;
    }
}
