package com.edu.carpool;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class manageContact extends AppCompatActivity {
    Button btnfrag1, btnfrag2;
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_contact);

        Button btnFrag1 = findViewById(R.id.btnfrag1);
        Button btnFrag2 = findViewById(R.id.btnfrag2);
        //animation
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.move_upward);
        btnFrag1.startAnimation(animation);
        btnFrag2.startAnimation(animation);


        Animation moveDownwardAnimation = AnimationUtils.loadAnimation(this, R.anim.move_down);

        btnFrag1.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {


                Intent intent = new Intent(manageContact.this, firebaseintro.class);
                startActivity(intent);


                replaceFragment(new firebasefrag());
                btnFrag1.startAnimation(moveDownwardAnimation);

            }
        });
        btnFrag2.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {


                replaceFragment(new localfrag());
                btnFrag2.startAnimation(moveDownwardAnimation);

            }
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout,fragment);
        fragmentTransaction.commit();

    }
}
