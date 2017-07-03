package com.codepath.apps.simpletwitter;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.codepath.apps.simpletwitter.adapter.TweetAdapter;
import com.codepath.apps.simpletwitter.adapter.TwitterClient;
import com.codepath.apps.simpletwitter.fragment.ComposeTweetDialog;
import com.codepath.apps.simpletwitter.models.Tweet;
import com.codepath.apps.simpletwitter.models.TweetList;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

import static android.support.v7.widget.StaggeredGridLayoutManager.VERTICAL;

public class TimelineActivity extends AppCompatActivity implements ComposeTweetDialog.ComposeDialogListener {

    private TwitterClient client;

    private TweetAdapter adapter;
    private ComposeTweetDialog composeDialog;
    private FloatingActionButton fab;

    @BindView(R.id.rvTimeline)
    RecyclerView rvTimeline;
    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout swipeRefresh;
    @BindView(R.id.pbLoadMore)
    ProgressBar pbLoadMore;

    @Override
    public void onFinishComposeDialog(String content, String in_reply_to_status_id) {
        Log.d("SAMN: body status: ", content);
        postTweet(content, in_reply_to_status_id);
    }

    private interface Listener{
        void onResult(TweetList tweetList);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        setUpView();
    }

    private void setUpView() {
        client = TwitterApplication.getRestClient();
        adapter = new TweetAdapter(this);
        rvTimeline.setAdapter(adapter);

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1, VERTICAL);
        rvTimeline.setLayoutManager(layoutManager);

        adapter.setListener(new TweetAdapter.Listener() {
            @Override
            public void onLoadMore() {
                Tweet.nextPage();
                pbLoadMore.setVisibility(View.VISIBLE);
                populateTimeline(new Listener() {
                    @Override
                    public void onResult(TweetList tweetList) {
                        adapter.appendData(tweetList.getTweets());
                    }
                });
            }

            @Override
            public void onReplyToTweet(Tweet tweetReplyTo) {
                showComposeTweetDialog(false, tweetReplyTo);
            }
        });

        populateTimeline(new Listener() {
            @Override
            public void onResult(TweetList tweetList) {
                adapter.setData(tweetList.getTweets());
            }
        });

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fab.setVisibility(View.GONE);
                showComposeTweetDialog(true, null);
            }
        });

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Tweet.resetPage();
                populateTimeline(new Listener() {
                    @Override
                    public void onResult(TweetList tweetList) {
                        adapter.setData(tweetList.getTweets());
                    }
                });
            }
        });
        swipeRefresh.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

    }

    private void populateTimeline(final Listener listener) {
        client.getHomeTimeline(Tweet.getPage(), new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

                Log.d("Samn", Tweet.getPage() +"");

                TweetList tweetList = new TweetList();
                tweetList.setTweets(Tweet.fromJsonArray(response));
                listener.onResult(tweetList);
                handleComplete();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("Samn Failure", errorResponse.toString());
                handleComplete();
                try {
                    if(errorResponse.getInt("code") == 88)
                        Toast.makeText(TimelineActivity.this, "Out of data!", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            };
        });
    }

    private void postTweet(String content, String in_reply_to_status_id){
        client.postTweet(content, in_reply_to_status_id, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                populateTimeline(new Listener() {
                    @Override
                    public void onResult(TweetList tweetList) {
                        adapter.setData(tweetList.getTweets());
                        composeDialog.dismiss();
                        fab.setVisibility(View.VISIBLE);
                    }
                });
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("Samn Failure", errorResponse.toString());
            };
        });
    }

    public void showComposeTweetDialog(boolean isNewTweet, Tweet tweet){
        FragmentManager fragmentManager = getSupportFragmentManager();
        composeDialog = ComposeTweetDialog.newInstance("Tweet", isNewTweet, tweet);
        composeDialog.show(fragmentManager, "fragment_compose");
    }

    private void handleComplete(){
        pbLoadMore.setVisibility(View.GONE);
        swipeRefresh.setRefreshing(false);
    }
}
