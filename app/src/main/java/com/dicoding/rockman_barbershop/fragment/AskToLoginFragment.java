package com.dicoding.rockman_barbershop.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.dicoding.rockman_barbershop.Activity.UserAuth.Login;
import com.dicoding.rockman_barbershop.Activity.UserAuth.Register;
import com.dicoding.rockman_barbershop.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AskToLoginFragment extends Fragment {
    Button mLogin, mRegistrasi;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_ask_to_login, container, false);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        mLogin = v.findViewById(R.id.ask_login);
        mRegistrasi = v.findViewById(R.id.ask_regist);

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), Login.class));
            }
        });

        mRegistrasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), Register.class));
            }
        });

        return v;
    }
}
