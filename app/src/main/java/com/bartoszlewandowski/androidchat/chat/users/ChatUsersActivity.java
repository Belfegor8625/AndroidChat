package com.bartoszlewandowski.androidchat.chat.users;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.bartoszlewandowski.androidchat.R;
import com.bartoszlewandowski.androidchat.signup.SignUpActivity;
import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.bartoszlewandowski.androidchat.consts.DatabaseConstants.USERNAME;

public class ChatUsersActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.listView)
    ListView listView;
    @BindView(R.id.pullToRefreshLayout)
    SwipeRefreshLayout pullToRefreshLayout;

    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> usersArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_users);
        ButterKnife.bind(this);
        listView.setOnItemClickListener(this);
        fillListView();
        pullToRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onRefresh() {
        refreshList();
        arrayAdapter.notifyDataSetChanged();
        pullToRefreshLayout.setRefreshing(false);
    }

    private void refreshList() {
        ParseQuery<ParseUser> parseQuery = ParseUser.getQuery();
        parseQuery.whereNotEqualTo(USERNAME, ParseUser.getCurrentUser().getUsername());
        parseQuery.whereNotContainedIn(USERNAME, usersArray);
        parseQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                if (e == null) {
                    setListViewData(users);
                }
            }
        });
    }

    private void fillListView() {
        usersArray = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, usersArray);
        ParseQuery<ParseUser> parseQuery = ParseUser.getQuery();
        parseQuery.whereNotEqualTo(USERNAME, ParseUser.getCurrentUser().getUsername());
        parseQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                if (e == null) {
                    setListViewData(users);
                }
            }
        });
    }

    private void setListViewData(List<ParseUser> users) {
        if (users.size() > 0) {
            for (ParseUser user : users) {
                usersArray.add(user.getUsername());
            }
            listView.setAdapter(arrayAdapter);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logoutUserItem) {
            logOutCurrentUser();
        }
        return super.onOptionsItemSelected(item);
    }

    private void logOutCurrentUser() {
        ParseUser.logOutInBackground(new LogOutCallback() {
            @Override
            public void done(ParseException e) {
                startSignUpActivity();
                finish();
            }
        });
    }

    private void startSignUpActivity() {
        Intent intent = new Intent(ChatUsersActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }


}
