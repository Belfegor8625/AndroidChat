package com.bartoszlewandowski.androidchat.chat;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.bartoszlewandowski.androidchat.R;
import com.bartoszlewandowski.androidchat.signup.SignUpActivity;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bartoszlewandowski.androidchat.chat.users.ChatUsersActivity.SELECTED_USER;
import static com.bartoszlewandowski.androidchat.consts.DatabaseConstants.*;

public class ChatActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.refreshMessagesLayout)
    SwipeRefreshLayout refreshMessagesLayout;
    @BindView(R.id.chatListView)
    ListView chatListView;
    @BindView(R.id.edtMessage)
    EditText edtMessage;

    private ArrayList<String> messagesList;
    private ArrayAdapter<String> arrayAdapter;
    private String selectedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        selectedUser = getIntent().getStringExtra(SELECTED_USER);
        setTitle(getResources().getString(R.string.chat_title) + " " + selectedUser);
        fillListView();
        chatListView.setAdapter(arrayAdapter);
        refreshMessagesLayout.setOnRefreshListener(this);
    }

    private void fillListView() {
        messagesList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, messagesList);
        ArrayList<ParseQuery<ParseObject>> allQueries = new ArrayList<>();
        allQueries.add(createQueryInOneDirection(SENDER, RECIPIENT));
        allQueries.add(createQueryInOneDirection(RECIPIENT, SENDER));
        ParseQuery<ParseObject> myQuery = ParseQuery.or(allQueries);
        myQuery.orderByAscending(CREATED_AT);
        myQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (objects.size() > 0 && e == null) {
                    for (ParseObject chatObject : objects) {
                        String message = chatObject.get(MESSAGE) + "";
                        if (chatObject.get(SENDER).equals(ParseUser.getCurrentUser().getUsername())) {
                            message = ParseUser.getCurrentUser().getUsername() + ": " + message;
                        }
                        if (chatObject.get(SENDER).equals(selectedUser)) {
                            message = selectedUser + ": " + message;
                        }
                        messagesList.add(message);
                    }
                    arrayAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private ParseQuery<ParseObject> createQueryInOneDirection(String from, String to) {
        ParseQuery<ParseObject> userQuery = ParseQuery.getQuery(CHAT_CLASS);
        userQuery.whereEqualTo(from, ParseUser.getCurrentUser().getUsername());
        userQuery.whereEqualTo(to, selectedUser);
        return userQuery;
    }


    @Override
    public void onRefresh() {
        fillListView();
        refreshMessagesLayout.setRefreshing(false);
    }

    @OnClick(R.id.btnSendMessage)
    public void onClickBtnSendMessage() {
        String currentUserName = ParseUser.getCurrentUser().getUsername();
        final String message = edtMessage.getText().toString();
        makeChatParseObjectWithData(currentUserName, message).saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    FancyToast.makeText(ChatActivity.this, "Message sent",
                            Toast.LENGTH_SHORT, FancyToast.INFO, false).show();
                    messagesList.add(ParseUser.getCurrentUser().getUsername() + ": " + message);
                    arrayAdapter.notifyDataSetChanged();
                    edtMessage.setText("");
                }
            }
        });
    }

    private ParseObject makeChatParseObjectWithData(String currentUserName, String message) {
        ParseObject chat = new ParseObject(CHAT_CLASS);
        chat.put(SENDER, currentUserName);
        chat.put(RECIPIENT, selectedUser);
        chat.put(MESSAGE, message);
        return chat;
    }
}
