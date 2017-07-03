package com.codepath.apps.simpletwitter.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.simpletwitter.R;
import com.codepath.apps.simpletwitter.TwitterApplication;
import com.codepath.apps.simpletwitter.Utils.ResourceUtils;
import com.codepath.apps.simpletwitter.models.Tweet;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Samn on 03-Jul-17.
 */

public class ComposeTweetDialog extends DialogFragment {

    private static final String TAG = ComposeTweetDialog.class.getSimpleName();
    private static final String DIALOG_TITLE = "title";

    private static boolean IS_NEW_TWEET;
    private static Tweet tweet;

    private String content;
    private String in_reply_to_status_id;

    @BindView(R.id.tvNameComp)
    TextView tvNameComp;
    @BindView(R.id.tvScreenNameComp)
    TextView tvScreenNameComp;
    @BindView(R.id.ivAvatarComp)
    ImageView ivAvatarComp;
    @BindView(R.id.tvReplyTo)
    TextView tvReplyTo;
    @BindView(R.id.etBodyComp)
    EditText etBodyComp;
    @BindView(R.id.btnTweet)
    Button btnTweet;
    @BindView(R.id.ivCancel)
    ImageView ivCancel;
    @BindView(R.id.tvCountChar)
    TextView tvCountChar;

    public ComposeTweetDialog(){
    }

    public static ComposeTweetDialog newInstance(String title, boolean isNewTweet,
                                                 @Nullable Tweet tweetReplyTo){
        ComposeTweetDialog frag = new ComposeTweetDialog();
        IS_NEW_TWEET = isNewTweet; //xác nhận có phải NEW TWEET hay REPLY
        if(tweetReplyTo!=null)
            tweet = tweetReplyTo;

        Bundle args = new Bundle();
        args.putString(DIALOG_TITLE, title);
        frag.setArguments(args);
        return frag;
    }

    public interface ComposeDialogListener{
        void onFinishComposeDialog(String content, String in_reply_to_status_id);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_compose, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        setUpViewNormal();

        if(!IS_NEW_TWEET)
            setUpViewReply();
        else
            setUpViewTweet();

        setUpButton();

        //set title to dialog
        String title = getArguments().getString(DIALOG_TITLE, "Compose Tweet");
        getDialog().setTitle(title);
    }

    private void setUpViewNormal() {
        tvNameComp.setText(TwitterApplication.CurrentUser.getScreenName());
        tvScreenNameComp.setText(TwitterApplication.CurrentUser.getName());
        ResourceUtils.loadImageRoundedCorner(getContext(), ivAvatarComp, TwitterApplication.CurrentUser.getProfileImageUrl());
    }

    private void setUpButton() {
        ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                content = etBodyComp.getText().toString();
                ComposeDialogListener listener = (ComposeDialogListener) getActivity();
                if(IS_NEW_TWEET){
                    if(content!= null || content!="")
                        listener.onFinishComposeDialog(content, null);
                    else{
                        Toast.makeText(getContext(), "Write something about you today!", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    if(content!= null || content!="")
                        listener.onFinishComposeDialog(content, String.valueOf(tweet.getTweetId()));
                }
            }
        });
    }

    private void setUpViewTweet() {
        etBodyComp.setHint("What's happening?");
        tvReplyTo.setVisibility(View.GONE);
    }

    private void setUpViewReply() {
        //// TODO: 03-Jul-17 phải truyền TWEET ID của bài mình click vào
        tvReplyTo.setText("In Reply to " + tweet.getUser().getName());
        etBodyComp.setText("@"+ tweet.getUser().getScreenName());
    }


}
