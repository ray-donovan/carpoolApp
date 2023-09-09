package com.edu.carpool;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class firebaseStorage extends AppCompatActivity {
    private FirebaseAuth mAuth;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkcontactlist);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
            finish();
            return;
        }


        FirebaseApp.initializeApp(this);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(currentUser.getUid())
                .child("emergencyContacts");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                StringBuilder dataText = new StringBuilder();

                for (DataSnapshot contactSnapshot : dataSnapshot.getChildren()) {
                    String name = contactSnapshot.child("name").getValue(String.class);
                    String phone = contactSnapshot.child("phone").getValue(String.class);
                    String relationship = contactSnapshot.child("relationship").getValue(String.class);


                    dataText.append("Name: ").append(name).append("\n");
                    dataText.append("Phone: ").append(phone).append("\n");
                    dataText.append("Relationship: ").append(relationship).append("\n");

                }

                TextView textViewData = findViewById(R.id.textViewData);
                textViewData.setText(dataText.toString());

                Button btnClear = findViewById(R.id.btnClear);

                btnClear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clearHistory();
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void clearHistory() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(mAuth.getCurrentUser().getUid())
                .child("emergencyContacts");

        dbRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                if (task.isSuccessful()) {
                    showToast("History Cleared");
                    TextView textViewData = findViewById(R.id.textViewData);
                    textViewData.setText("");
                } else {
                    showToast("Failed to clear history");
                }
            }
        });

    }

    private void showToast(String message) {
        Toast.makeText(firebaseStorage.this, message, Toast.LENGTH_SHORT).show();
    }

}
