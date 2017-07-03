package com.codepath.apps.simpletwitter.models;

import java.util.List;

/**
 * Created by Samn on 02-Jul-17.
 */

public class TweetList {
    private List<Tweet> tweets;

    public List<Tweet> getTweets() {
        return tweets;
    }

    public void setTweets(List<Tweet> tweets) {
        this.tweets = tweets;
    }

}
