package com.codepath.apps.simpletwitter.adapter;

import android.content.Context;

import com.codepath.apps.simpletwitter.R;
import com.codepath.oauth.OAuthBaseClient;
import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.api.BaseApi;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/scribejava/scribejava/tree/master/scribejava-apis/src/main/java/com/github/scribejava/apis
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final BaseApi REST_API_INSTANCE = TwitterApi.instance(); // Change this
	public static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL
	public static final String REST_CONSUMER_KEY = "kaIcv0GPO1tfRcWtXE6h0tSMr";       // Change this
	public static final String REST_CONSUMER_SECRET = "yFMeC3fLZAkAHwJH7Jl7y7qcbv3UMNpMaaFi4VMYygXcucswtw"; // Change this

	// Landing page to indicate the OAuth flow worked in case Chrome for Android 25+ blocks navigation back to the app.
	public static final String FALLBACK_URL = "https://codepath.github.io/android-rest-client-template/success.html";

	// See https://developer.chrome.com/multidevice/android/intents
	public static final String REST_CALLBACK_URL_TEMPLATE = "intent://%s#Intent;action=android.intent.action.VIEW;scheme=%s;package=%s;S.browser_fallback_url=%s;end";

	public TwitterClient(Context context) {
		super(context, REST_API_INSTANCE,
				REST_URL,
				REST_CONSUMER_KEY,
				REST_CONSUMER_SECRET,
				String.format(REST_CALLBACK_URL_TEMPLATE, context.getString(R.string.intent_host),
						context.getString(R.string.intent_scheme), context.getPackageName(), FALLBACK_URL));
	}

	/* 1. Define the endpoint URL with getApiUrl and pass a relative path to the endpoint
	 * 	  i.e getApiUrl("statuses/home_timeline.json");*/

	public void getHomeTimeline(int page, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/home_timeline.json");
		RequestParams params = new RequestParams();
		params.put("page", String.valueOf(page));
		getClient().get(apiUrl, params, handler);
	}

	public void postTweet(String body, String in_reply_to_status_id, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/update.json");
		RequestParams params = new RequestParams();
		params.put("status", body);
		if(in_reply_to_status_id != null)
			params.put("in_reply_to_status_id", in_reply_to_status_id);
		getClient().post(apiUrl, params, handler);
	}

	public void getCurrentUser(AsyncHttpResponseHandler handler){
		String apiUrl = getApiUrl("account/verify_credentials.json");
		getClient().get(apiUrl, null, handler);
	}

	public void favoriteCreate(String status_id, AsyncHttpResponseHandler handler){
		String apiUrl = getApiUrl("favorites/create.json");
		RequestParams params = new RequestParams();
		params.put("id", status_id);
		getClient().post(apiUrl, params, handler);
	}

	public void favoriteDestroy(String status_id, AsyncHttpResponseHandler handler){
		String apiUrl = getApiUrl("favorites/destroy.json");
		RequestParams params = new RequestParams();
		params.put("id", status_id);
		getClient().post(apiUrl, params, handler);
	}

	public void Retweet(String status_id, AsyncHttpResponseHandler handler){
		String strUrl = "statuses/retweet/" + status_id + ".json";
		String apiUrl = getApiUrl(strUrl);
		getClient().post(apiUrl, null, handler);
	}

	public void UnRetweet(String status_id, AsyncHttpResponseHandler handler){
		String strUrl = "statuses/unretweet/" + status_id + ".json";
		String apiUrl = getApiUrl(strUrl);
		getClient().post(apiUrl, null, handler);
	}
}
