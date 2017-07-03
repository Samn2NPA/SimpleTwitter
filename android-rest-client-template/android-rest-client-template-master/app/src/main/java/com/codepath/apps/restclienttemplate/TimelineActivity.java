package com.codepath.apps.restclienttemplate;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.codepath.apps.restclienttemplate.adapter.TweetAdapter;
import com.codepath.apps.restclienttemplate.fragment.ComposeTweetDialog;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.TweetList;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

import static android.support.v7.widget.StaggeredGridLayoutManager.VERTICAL;

public class TimelineActivity extends AppCompatActivity implements ComposeTweetDialog.ComposeDialogListener {

    private TwitterClient client;

    private TweetAdapter adapter;
    private ComposeTweetDialog composeDialog;

    @BindView(R.id.rvTimeline)
    RecyclerView rvTimeline;

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

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fab.setVisibility(View.GONE);
                showComposeTweetDialog(true);
            }
        });

        setUpView();
    }

    private void setUpView() {
        client = TwitterApplication.getRestClient();
        adapter = new TweetAdapter(this);
        rvTimeline.setAdapter(adapter);

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1, VERTICAL);
        rvTimeline.setLayoutManager(layoutManager);

        populateTimeline(new Listener() {
            @Override
            public void onResult(TweetList tweetList) {
                adapter.setData(tweetList.getTweets());
            }
        });
    }

    private void populateTimeline(final Listener listener) {
        client.getHomeTimeline(1, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                //super.onSuccess(statusCode, headers, response);
                Log.d("Samn", response.toString());
                /*JsonParser jParser = new JsonParser();
                JsonElement jElement = jParser.parse(response.toString());
                Gson gson = new Gson();*/

                TweetList tweetList = new TweetList();
                tweetList.setTweets(Tweet.fromJsonArray(response));
                listener.onResult(tweetList);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("Samn Failure", errorResponse.toString());
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
                    }
                });
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("Samn Failure", errorResponse.toString());
            };
        });
    }

    public void showComposeTweetDialog(boolean isNewTweet){
        FragmentManager fragmentManager = getSupportFragmentManager();
        composeDialog = ComposeTweetDialog.newInstance("Tweet", isNewTweet);
        composeDialog.show(fragmentManager, "fragment_compose");
    }

}
