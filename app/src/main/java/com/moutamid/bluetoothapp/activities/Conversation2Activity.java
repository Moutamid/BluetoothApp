package com.moutamid.bluetoothapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bluetooth.communicator.Message;
import com.bluetooth.communicator.Peer;
import com.fxn.stash.Stash;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.bluetoothapp.Global;
import com.moutamid.bluetoothapp.MainActivity;
import com.moutamid.bluetoothapp.R;
import com.moutamid.bluetoothapp.utils.Constants;

import java.util.ArrayList;

public class Conversation2Activity extends AppCompatActivity {
    private static final String TAG = "Conversation2Activity";
    private String otherUserName;
    private boolean isFirst = false;
    private Global global;
    private ArrayList<ChatMessage> currentMessagesArrayList = new ArrayList<>();

    private RecyclerView conversationRecyclerView;
    private RecyclerViewAdapterMessages adapter;

    private TextView usernameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation2);
        global = (Global) getApplication();

        usernameTextView = findViewById(R.id.user_name_conversation);

        setBackBtnClickListener();

        setAddMessageBtnCLickListener();

        if (getIntent().hasExtra("first")) {

            if (getIntent().getBooleanExtra("first", false)) {

                isFirst = true;
            }

        }

//        otherUserName = getIntent().getStringExtra("name");
//        otherUserImageUrl = getIntent().getStringExtra("url");
        otherUserName = getIntent().getStringExtra("name");

        usernameTextView.setText(otherUserName);

        initRecyclerView();

        Constants.databaseReference().child("chats").child(Stash.getString(Constants.USER_NAME))
                .child(otherUserName)
                .child("messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
//                    Toast.makeText(Conversation2Activity.this, "No chat exists", Toast.LENGTH_SHORT).show();
                    return;
                }

                currentMessagesArrayList.clear();
//                ArrayList<Message> mResults = new ArrayList<>();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ChatMessage chatMessage = dataSnapshot.getValue(ChatMessage.class);

                    currentMessagesArrayList.add(chatMessage);

//                    mResults.add(message);
                }

//                Stash.put(Constants.ALL_MESSAGES, mResults);
                initRecyclerView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: " + error.getMessage());
                Toast.makeText(Conversation2Activity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    boolean block = false;

    private void setAddMessageBtnCLickListener() {
        final EditText editText = findViewById(R.id.reply_edit_text_activity_conversation);

        findViewById(R.id.send_reply_btn_activity_conversation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String replyText = editText.getText().toString();

                if (TextUtils.isEmpty(replyText)) {
                    editText.setError("Please enter a message!");
                    editText.requestFocus();
                    return;
                }

                if (isFirst) {
                    uploadOtherUserDetails();
                }


                ChatMessage message = new ChatMessage();
                message.setMsgText(replyText);
                message.setMsgUser(Stash.getString(Constants.USER_NAME));

                editText.setText("");

                adapter.addMessage(message);

                uploadMessage(message);


            }
        });

    }

    private void uploadMessage(ChatMessage message) {

        Constants.databaseReference().child("chats").child(Stash.getString(Constants.USER_NAME))
                .child(otherUserName)
                .child("messages")
                .push()
                .setValue(message);

        Constants.databaseReference().child("chats").child(otherUserName)
                .child(Stash.getString(Constants.USER_NAME))
                .child("messages")
                .push()
                .setValue(message).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                adapter.removeMessage();
                Toast.makeText(Conversation2Activity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        /*// SETTING LAST MESSAGE ON BOTH ACCOUNTS

        Constants.databaseReference().child("chats").child(Stash.getString(Constants.USER_NAME))
                .child(otherUserName)
                .child("lastMcg").setValue(message.getMsgText());

        Constants.databaseReference().child("chats").child(otherUserName)
                .child(Stash.getString(Constants.USER_NAME))
                .child("lastMcg").setValue(message.getMsgText());
*/
    }

    private void uploadOtherUserDetails() {
        String myName = Stash.getString(Constants.USER_NAME);
//        String myUrl = utils.getStoredString(Conversation2Activity.this, "profileUrl");

        Constants.databaseReference().child("chats").child(otherUserName)
                .child(Stash.getString(Constants.USER_NAME))
                .child("name").setValue(myName);

//        Constants.databaseReference().child("chats").child(otherUserName)
//                .child(Stash.getString(Constants.USER_NAME))
//                .child("imageUrl").setValue(myUrl);

        Constants.databaseReference().child("chats").child(Stash.getString(Constants.USER_NAME))
                .child(otherUserName)
                .child("name").setValue(otherUserName);

        /*Constants.databaseReference().child("chats").child(Stash.getString(Constants.USER_NAME))
                .child(otherUserName)
                .child("imageUrl").setValue(otherUserImageUrl);*/

    }

    private void setBackBtnClickListener() {
        findViewById(R.id.backbtn_conversation_activity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        findViewById(R.id.bluetoothBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Conversation2Activity.this, MainActivity.class));
            }
        });

    }

    private void initRecyclerView() {
        Log.d(TAG, "initRecyclerView: ");

        conversationRecyclerView = findViewById(R.id.conversation_recyclerview);
        adapter = new RecyclerViewAdapterMessages();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
//        linearLayoutManager.setStackFromEnd(true);

        conversationRecyclerView.setLayoutManager(linearLayoutManager);
//        conversationRecyclerView.setHasFixedSize(true);
        conversationRecyclerView.setNestedScrollingEnabled(false);

        conversationRecyclerView.setAdapter(adapter);

        scrollRecyclerViewToEnd();

    }

    private void scrollRecyclerViewToEnd() {
        Log.d(TAG, "scrollRecyclerViewToEnd: ");
        conversationRecyclerView.scrollToPosition(conversationRecyclerView.getAdapter().getItemCount() - 1);

    }

    private class RecyclerViewAdapterMessages extends RecyclerView.Adapter
            <RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            if (viewType == 1) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.component_message_received, parent, false);
                return new ViewHolderLeftMessage(view);

            } else {
                View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.component_message_send, parent, false);
                return new ViewHolderRightMessage(view1);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            if (holder.getItemViewType() == 1) {
                ViewHolderLeftMessage holderLeftMessage = (ViewHolderLeftMessage) holder;
                holderLeftMessage.leftText.setText(currentMessagesArrayList.get(holder.getAdapterPosition()).getMsgText());
            } else {
                ViewHolderRightMessage holderRightMessage = (ViewHolderRightMessage) holder;
                holderRightMessage.rightText.setText(currentMessagesArrayList.get(holder.getAdapterPosition()).getMsgText());
            }

        }

        @Override
        public int getItemCount() {
            if (currentMessagesArrayList == null)
                return 0;
            return currentMessagesArrayList.size();
        }

        String myname = Stash.getString(Constants.USER_NAME);

        @Override
        public int getItemViewType(int position) {

            /*

             * 1 Left Message
             * 2 Right Message
             */

            if (currentMessagesArrayList.get(position).getMsgUser().equals(myname)) {
                return 2;
            } else {
                return 1;
            }
        }

        public class ViewHolderLeftMessage extends RecyclerView.ViewHolder {

            TextView leftText;

            public ViewHolderLeftMessage(@NonNull View v) {
                super(v);
                leftText = v.findViewById(R.id.text_content);
            }
        }

        public class ViewHolderRightMessage extends RecyclerView.ViewHolder {

            TextView rightText;

            public ViewHolderRightMessage(@NonNull View v) {
                super(v);
                rightText = v.findViewById(R.id.text);
            }
        }

        public void addMessage(ChatMessage c) {
            Log.d(TAG, "addMessage: ");

            currentMessagesArrayList.add(c);

            notifyItemInserted(currentMessagesArrayList.size() - 1);

            scrollRecyclerViewToEnd();

        }

        public void removeMessage() {
            currentMessagesArrayList.remove(currentMessagesArrayList.size() - 1);
            notifyItemRemoved(currentMessagesArrayList.size() - 1);
            notifyItemRangeChanged(currentMessagesArrayList.size() - 1, getItemCount());
        }

    }

    private static class ChatMessage {
        private String msgText;
        private String msgUser;

        public void setMsgText(String msgText) {
            this.msgText = msgText;
        }

        public void setMsgUser(String msgUser) {
            this.msgUser = msgUser;
        }

        public ChatMessage(String msgText, String msgUser) {
            this.msgText = msgText;
            this.msgUser = msgUser;

        }

        public ChatMessage() {
        }

        public String getMsgText() {
            return msgText;
        }

        public String getMsgUser() {
            return msgUser;
        }

    }
}