package com.moutamid.bluetoothapp.activities;

import static android.view.LayoutInflater.from;
import static com.moutamid.bluetoothapp.R.id.allusersrecyclerview;

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

import com.fxn.stash.Stash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.bluetoothapp.R;
import com.moutamid.bluetoothapp.fragments.ConversationFragment;
import com.moutamid.bluetoothapp.utils.Constants;

import java.util.ArrayList;

public class AllUsersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);

        Constants.databaseReference().child(Constants.ALL_USERS)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            tasksArrayList.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                                String name = dataSnapshot.getValue(String.class);
                                tasksArrayList.add(name);

                            }

                            tasksArrayList.remove(Stash.getString(Constants.USER_NAME));

                            initRecyclerView();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(AllUsersActivity.this, error.toException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private ArrayList<String> tasksArrayList = new ArrayList<>();

    private RecyclerView conversationRecyclerView;
    private RecyclerViewAdapterMessages adapter;

    private void initRecyclerView() {

        conversationRecyclerView = findViewById(allusersrecyclerview);
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

    private class RecyclerViewAdapterMessages extends Adapter
            <RecyclerViewAdapterMessages.ViewHolderRightMessage> {

        @NonNull
        @Override
        public ViewHolderRightMessage onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = from(parent.getContext()).inflate(R.layout.layout_item_all_users, parent, false);
            return new ViewHolderRightMessage(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolderRightMessage holder, final int position) {

            holder.title.setText(tasksArrayList.get(position));

            holder.title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(AllUsersActivity.this, Conversation2Activity.class);
                    intent.putExtra("first", true);
                    intent.putExtra("name", tasksArrayList.get(position));
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