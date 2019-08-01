package com.ejsfbu.app_main.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.ejsfbu.app_main.Adapters.GoalAdapter;
import com.ejsfbu.app_main.Adapters.RequestAdapter;
import com.ejsfbu.app_main.Models.Request;
import com.ejsfbu.app_main.R;
import com.ejsfbu.app_main.Models.Goal;
import com.ejsfbu.app_main.Models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChildDetailActivity extends AppCompatActivity {

    private User child;

    @BindView(R.id.tvChildDetailName)
    TextView tvChildDetailName;
    @BindView(R.id.tvChildDetailAccountCode)
    TextView tvChildDetailAccountCode;

    @BindView(R.id.ivChildDetailProfilePic)
    ImageView ivChildDetailProfilePic;

    @BindView(R.id.rvChildDetailCompletedGoals)
    RecyclerView rvChildDetailCompletedGoals;
    @BindView(R.id.rvChildDetailInProgressGoals)
    RecyclerView rvChildDetailInProgressGoals;
    @BindView(R.id.rvChildDetailPendingRequests)
    RecyclerView rvChildDetailPendingRequests;

    @BindView(R.id.ibChildDetailBack)
    ImageButton ibChildDetailBack;

    private List<Goal> completedGoals;
    private List<Goal> inProgressGoals;
    private List<Request> pendingRequests;

    private GoalAdapter completedGoalsAdapter;
    private GoalAdapter inProgressGoalsAdapter;
    private RequestAdapter pendingRequestsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_detail);

        ButterKnife.bind(this);

        completedGoals = new ArrayList<>();
        inProgressGoals = new ArrayList<>();
        pendingRequests = new ArrayList<>();

        completedGoalsAdapter = new GoalAdapter(this, completedGoals);
        inProgressGoalsAdapter = new GoalAdapter(this, inProgressGoals);
        pendingRequestsAdapter = new RequestAdapter(this, pendingRequests);

        rvChildDetailCompletedGoals.setAdapter(completedGoalsAdapter);
        rvChildDetailInProgressGoals.setAdapter(inProgressGoalsAdapter);
        rvChildDetailPendingRequests.setAdapter(pendingRequestsAdapter);

        rvChildDetailCompletedGoals.setLayoutManager(new LinearLayoutManager(this));
        rvChildDetailInProgressGoals.setLayoutManager(new LinearLayoutManager(this));
        rvChildDetailPendingRequests.setLayoutManager(new LinearLayoutManager(this));

        String childCode = getIntent().getStringExtra("childCode");
        getChildFromCode(childCode);
    }

    @OnClick(R.id.ibChildDetailBack)
    public void onClickChildDetailBack() {
        Intent intent = new Intent(this, ParentActivity.class);
        startActivity(intent);
        finish();
    }

    public void getChildFromCode(String childCode) {
        User.Query userQuery = new User.Query();
        userQuery.whereEqualTo("objectId", childCode);
        userQuery.findInBackground(new FindCallback<User>() {
            @Override
            public void done(List<User> objects, ParseException e) {
                if (e == null) {
                    child = objects.get(0);
                    fillData();
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    public void fillData() {
        tvChildDetailName.setText(child.getName());

        String accountCode = "Account Code: " + child.getObjectId();
        tvChildDetailAccountCode.setText(accountCode);

        ParseFile image = child.getProfilePic();
        if (image != null) {
            String imageUrl = image.getUrl();
            imageUrl = imageUrl.substring(4);
            imageUrl = "https" + imageUrl;
            RequestOptions options = new RequestOptions();
            options.placeholder(R.drawable.icon_user)
                    .error(R.drawable.icon_user)
                    .transform(new CenterCrop())
                    .transform(new CircleCrop());
            Glide.with(ChildDetailActivity.this)
                    .load(imageUrl)
                    .apply(options) // Extra: round image corners
                    .into(ivChildDetailProfilePic);
        }

        loadCompletedGoals();
        loadInProgressGoals();
        loadPendingRequests();
    }

    public void loadCompletedGoals() {
        List<Goal> goals = child.getCompletedGoals();
        if (goals != null) {
            completedGoals.addAll(goals);
            Collections.sort(completedGoals);
            Collections.reverse(completedGoals);
            completedGoalsAdapter.notifyDataSetChanged();
        }
    }

    public void loadInProgressGoals() {
        List<Goal> goals = child.getInProgressGoals();
        if (goals != null) {
            inProgressGoals.addAll(goals);
            Collections.sort(completedGoals);
            inProgressGoalsAdapter.notifyDataSetChanged();
        }
    }

    public void loadPendingRequests() {
        final Request.Query requestQuery = new Request.Query();
        requestQuery.fromUser(child);
        requestQuery.findInBackground(new FindCallback<Request>() {
            @Override
            public void done(List<Request> objects, ParseException e) {
                if (e == null) {
                    pendingRequests.addAll(objects);
                    pendingRequestsAdapter.notifyDataSetChanged();
                } else {
                    e.printStackTrace();
                }
            }
        });
    }
}
