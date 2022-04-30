package com.moutamid.bluetoothapp.activities;

import static android.view.LayoutInflater.from;
import static com.moutamid.bluetoothapp.R.id.chatsrecyclerview;
import static com.moutamid.bluetoothapp.R.layout.layout_item_all_users;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bluetooth.communicator.Message;
import com.fxn.stash.Stash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.bluetoothapp.R;
import com.moutamid.bluetoothapp.databinding.ActivityChatsBinding;
import com.moutamid.bluetoothapp.utils.Constants;

import java.util.ArrayList;

public class ChatsActivity extends AppCompatActivity {
    private ActivityChatsBinding b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityChatsBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        b.newChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ChatsActivity.this, AllUsersActivity.class));
            }
        });

        Constants.databaseReference().child(Constants.CHATS).child(Stash.getString(Constants.USER_NAME))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            return;
                        }

                        tasksArrayList.clear();

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            ChatModel model = dataSnapshot.getValue(ChatModel.class);
                            model.setUid(dataSnapshot.getKey());
                            tasksArrayList.add(model);
                        }


                        initRecyclerView();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ChatsActivity.this, error.toException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


    }

    private static class ChatModel {

        private String name, imageUrl, lastMcg, uid;

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public ChatModel(String name, String imageUrl, String lastMcg, String uid) {
            this.name = name;
            this.imageUrl = imageUrl;
            this.lastMcg = lastMcg;
            this.uid = uid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getLastMcg() {
            return lastMcg;
        }

        public void setLastMcg(String lastMcg) {
            this.lastMcg = lastMcg;
        }

        public ChatModel(String name, String imageUrl, String lastMcg) {
            this.name = name;
            this.imageUrl = imageUrl;
            this.lastMcg = lastMcg;
        }

        ChatModel() {
        }
    }

    private ArrayList<ChatModel> tasksArrayList = new ArrayList<>();

    private RecyclerView conversationRecyclerView;
    private RecyclerViewAdapterMessages adapter;

    private void initRecyclerView() {

        conversationRecyclerView = findViewById(chatsrecyclerview);
        conversationRecyclerView.addItemDecoration(new DividerItemDecoration(conversationRecyclerView.getContext(), DividerItemDecoration.VERTICAL));
        adapter = new RecyclerViewAdapterMessages();
        //        LinearLayoutManager layoutManagerUserFriends = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        //    int numberOfColumns = 3;
        //int mNoOfColumns = calculateNoOfColumns(getApplicationContext(), 50);
        //  recyclerView.setLayoutManager(new GridLayoutManager(this, mNoOfColumns));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        //linearLayoutManager.setReverseLayout(true);
        conversationRecyclerView.setLayoutManager(linearLayoutManager);
        conversationRecyclerView.setHasFixedSize(true);
        conversationRecyclerView.setNestedScrollingEnabled(false);

        conversationRecyclerView.setAdapter(adapter);

        //    if (adapter.getItemCount() != 0) {

        //        noChatsLayout.setVisibility(View.GONE);
        //        chatsRecyclerView.setVisibility(View.VISIBLE);

        //    }

    }

    /*public static int calculateNoOfColumns(Context context, float columnWidthDp) { // For example columnWidthdp=180
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float screenWidthDp = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (screenWidthDp / columnWidthDp + 0.5); // +0.5 for correct rounding to int.
        return noOfColumns;
    }*/

    private class RecyclerViewAdapterMessages extends Adapter
            <RecyclerViewAdapterMessages.ViewHolderRightMessage> {

        @NonNull
        @Override
        public ViewHolderRightMessage onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = from(parent.getContext()).inflate(layout_item_all_users, parent, false);
            return new ViewHolderRightMessage(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolderRightMessage holder, final int position) {

            holder.title.setText(tasksArrayList.get(position).name);

            holder.title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ChatsActivity.this, Conversation2Activity.class);
                    intent.putExtra("name", tasksArrayList.get(position).name);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            if (tasksArrayList == null)
                return 0;
            return tasksArrayList.size();
        }

        public class ViewHolderRightMessage extends ViewHolder {

            TextView title;

            public ViewHolderRightMessage(@NonNull View v) {
                super(v);
                title = v.findViewById(R.id.textviewname);

            }
        }

    }

}