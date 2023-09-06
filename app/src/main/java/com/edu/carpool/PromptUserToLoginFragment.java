package com.edu.carpool;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class PromptUserToLoginFragment extends Fragment {

    private Button loginRedirect, signupRedirect;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_prompt_user_to_login, container, false);

        loginRedirect = rootView.findViewById(R.id.login_button);
        signupRedirect = rootView.findViewById(R.id.signup_button);

        loginRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Login.class);
                startActivity(intent);
            }
        });

        signupRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Signup.class);
                startActivity(intent);
            }
        });

        return rootView;
    }
}