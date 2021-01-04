package com.example.quizappzeesh.views;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quizappzeesh.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class StartFragment extends Fragment implements FirebaseAuth.AuthStateListener {

    private NavController navController;
    private static final int AUTHUI_RQST_CODE = 1;
    private Button signInButton;
    private TextView loadingText;

    private FirebaseAuth firebaseAuth;

    public StartFragment() {
//        SHA1 ID
//        E7:C7:0A:5A:8E:76:50:35:9B:14:E3:C6:90:FD:E5:A7:91:77:77:40
    }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_start, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        signInButton = view.findViewById(R.id.google_sign_in_button_ID);
        loadingText = view.findViewById(R.id.loadingAppID);

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null){
            navController.navigate(R.id.action_startFragment_to_listFragment);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingText.setText("fetching..");
                googleSignIn();
            }
        });
    }

    public void googleSignIn(){
        List<AuthUI.IdpConfig> providers = Arrays.asList(new AuthUI.IdpConfig.GoogleBuilder().build());

        Intent intent = AuthUI.getInstance().
                createSignInIntentBuilder().
                setAvailableProviders(providers).build();

        startActivityForResult(intent, AUTHUI_RQST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==AUTHUI_RQST_CODE){
            if (resultCode==RESULT_OK){

                loadingText.setText(null);

//                sign in as new user or existed user..
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user.getMetadata().getCreationTimestamp() == user.getMetadata().getLastSignInTimestamp()){

                    Toast.makeText(getContext(), "Welcome "+ user.getDisplayName(), Toast.LENGTH_LONG).show();
                } else{
                    Toast.makeText(getContext(), "Welcome Back! "+ user.getDisplayName(), Toast.LENGTH_LONG).show();
                }
                navController.navigate(R.id.action_startFragment_to_listFragment);
            }
            else{
                IdpResponse response = IdpResponse.fromResultIntent(data);
                if (response==null){
                    loadingText.setText(null);
                    Toast.makeText(getContext(), "sign in cancelled", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(getContext(), response.getError().toString(), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        FirebaseAuth.getInstance().removeAuthStateListener(this);
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if (firebaseAuth.getCurrentUser() != null){
            navController.navigate(R.id.action_startFragment_to_listFragment);
        }
        else{
            return;
        }
    }
}