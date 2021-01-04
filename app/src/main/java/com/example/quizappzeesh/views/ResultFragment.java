package com.example.quizappzeesh.views;

import android.animation.Animator;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import com.airbnb.lottie.LottieAnimationView;
import com.example.quizappzeesh.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ResultFragment extends Fragment {
    private TextView resultPercent, correctAns, wrongAns, not_answered;
    private LottieAnimationView passedAnimation, failedAnimation, celebrationAnimation;
    private ProgressBar totalPercentProgress;
    private Button gotoHomeButton;
    private NavController navController;

    private String quizID, current_UUID, correctAnswers, wrongAnswers, notAnswered;

    private int per = 0;

    private FirebaseFirestore firebaseFirestore;

    public ResultFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_result, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        resultPercent = view.findViewById(R.id.result_percent);
        correctAns = view.findViewById(R.id.resultFrag_CorrectAns_NUM_ID);
        wrongAns = view.findViewById(R.id.resultFrag_WrongAns_NUM_ID);
        not_answered = view.findViewById(R.id.resultFrag_MissedAns_NUM_ID);
        passedAnimation = view.findViewById(R.id.passedAnimID);
        failedAnimation = view.findViewById(R.id.failedAnimID);
        gotoHomeButton = view.findViewById(R.id.resultFrag_gotoHomeButtonID);
        celebrationAnimation = view.findViewById(R.id.celebrationAnimID);
        totalPercentProgress = view.findViewById(R.id.result_progress);

        quizID = ResultFragmentArgs.fromBundle(getArguments()).getQuizID();

        navController = Navigation.findNavController(view);
        firebaseFirestore = FirebaseFirestore.getInstance();

        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            current_UUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        firebaseFirestore.collection("QuizList").document(quizID).
                collection("Results").document(current_UUID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        correctAnswers = documentSnapshot.get("correct_answers").toString();
                        wrongAnswers = documentSnapshot.get("wrong_ans").toString();
                        notAnswered = documentSnapshot.get("not_attemped_ans").toString();

                        float total = Float.parseFloat(correctAnswers) + Float.parseFloat(wrongAnswers) + Float.parseFloat(notAnswered);
                        per = Math.round((Float.parseFloat(correctAnswers)*100)/total);

                        resultPercent.setText(String.valueOf(per+"%"));
                        totalPercentProgress.setProgress(per);
                        correctAns.setText(correctAnswers);
                        wrongAns.setText(wrongAnswers);
                        not_answered.setText(notAnswered);

                        show_pass_fail_anim();

                    } else {
                        task.getException().getMessage();
                    }
                }
            }
        });

//        Go to HomePage (List Fragment)
        gotoHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_resultFragment_to_listFragment);
            }
        });

        backButtonPressed();
    }

    public void show_pass_fail_anim(){

        if (per>33) {
            failedAnimation.setVisibility(View.INVISIBLE);
            passedAnimation.setVisibility(View.VISIBLE);

            celebrationAnimation.setVisibility(View.VISIBLE);

            celebrationAnimation.addAnimatorListener(new Animator.AnimatorListener() {
               @Override
               public void onAnimationStart(Animator animator) {
                   
               }

               @Override
               public void onAnimationEnd(Animator animator) {
                   celebrationAnimation.setVisibility(View.INVISIBLE);
               }

               @Override
               public void onAnimationCancel(Animator animator) {

               }

               @Override
               public void onAnimationRepeat(Animator animator) {

               }
           });
        } else {
            passedAnimation.setVisibility(View.INVISIBLE);
            failedAnimation.setVisibility(View.VISIBLE);
            celebrationAnimation.setVisibility(View.INVISIBLE);
        }
    }

    private void backButtonPressed() {

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                navController.navigate(R.id.action_resultFragment_to_listFragment);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),callback);
    }

}