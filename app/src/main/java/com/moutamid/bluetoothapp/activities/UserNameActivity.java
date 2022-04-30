package com.moutamid.bluetoothapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.fxn.stash.Stash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.bluetoothapp.Global;
import com.moutamid.bluetoothapp.MainActivity;
import com.moutamid.bluetoothapp.R;
import com.moutamid.bluetoothapp.databinding.ActivityUserNameBinding;
import com.moutamid.bluetoothapp.utils.Constants;

public class UserNameActivity extends AppCompatActivity {
    private ActivityUserNameBinding b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityUserNameBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        if (Stash.getBoolean(Constants.IS_LOGGED_IN)){
            Global global = (Global) getApplication();
            global.getBluetoothCommunicator().setName(Stash.getString(Constants.USER_NAME));
            startActivity(new Intent(UserNameActivity.this, ChatsActivity.class));
            finish();
            return;
        }

        b.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog progressDialog;
                progressDialog = new ProgressDialog(UserNameActivity.this);
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Loading...");
                if (b.nameTextviewCreateProfile.getText().toString().isEmpty())
                    return;

                Constants.databaseReference().child(Constants.ALL_USERS)
                        .child(b.nameTextviewCreateProfile.getText().toString())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                progressDialog.dismiss();
                                if (snapshot.exists()) {
                                    Toast.makeText(UserNameActivity.this, "Username already exist!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Constants.databaseReference().child(Constants.ALL_USERS)
                                            .child(b.nameTextviewCreateProfile.getText().toString())
                                            .setValue(b.nameTextviewCreateProfile.getText().toString());
                                    Stash.put(Constants.IS_LOGGED_IN, true);
                                    Stash.put(Constants.USER_NAME, b.nameTextviewCreateProfile.getText().toString());
                                    Global global = (Global) getApplication();
                                    global.getBluetoothCommunicator().setName(b.nameTextviewCreateProfile.getText().toString());
                                    Toast.makeText(UserNameActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(UserNameActivity.this, ChatsActivity.class));
                                    finish();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                progressDialog.dismiss();
                                Toast.makeText(UserNameActivity.this, error.toException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });

    }
}