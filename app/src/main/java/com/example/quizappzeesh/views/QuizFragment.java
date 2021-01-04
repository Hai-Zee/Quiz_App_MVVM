package com.example.quizappzeesh.views;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quizappzeesh.Model.QuestionsModel;
import com.example.quizappzeesh.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class QuizFragment extends Fragment implements View.OnClickListener {

    private FirebaseFirestore firebaseFirestore;
    private String quizID, quizName, current_UUID;
    private TextView quizTitle, quizTimer_QuesTime, quesFeedback_verifyAns, QuestionText, question_no;
    private ImageButton crossButton;
    private ProgressBar timerProgressbar;
    private Button option_1, option_2, option_3, option_4, nextQuestionButton;
    private NavController navController;

    private int total_questions = 0;
    private CountDownTimer countDownTimer;

    private boolean canAnswer = false;
    private int currentQuestionNum = 0;
    private int correctAnswers = 0;
    private int wrongAnswers = 0;
    private int notAnswered = 0;

    private List<QuestionsModel> allQuestionsList = new ArrayList<>();
    private List<QuestionsModel> randomlyPickedQuestions = new ArrayList<>();

    //  Firebase Auth
    private FirebaseAuth firebaseAuth;

    public QuizFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_quiz, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        quizTitle = view.findViewById(R.id.quizFragTitleID);
        quizTimer_QuesTime = view.findViewById(R.id.quizTimerPrgrsbarID);
        quesFeedback_verifyAns = view.findViewById(R.id.quizQues_VerifyingLine);
        QuestionText = view.findViewById(R.id.quiz_question);
        question_no = view.findViewById(R.id.quizQuesNumbersID);
        crossButton = view.findViewById(R.id.crossButtonID);
        timerProgressbar = view.findViewById(R.id.quizPrgrsbarID);
        option_1 = view.findViewById(R.id.buttonOption_1);
        option_2 = view.findViewById(R.id.buttonOption_2);
        option_3 = view.findViewById(R.id.buttonOption_3);
        option_4 = view.findViewById(R.id.buttonOption_4);
        nextQuestionButton = view.findViewById(R.id.quizFrag_nextQuesButtonID);

        navController = Navigation.findNavController(view);

        if (getArguments() != null) {
            quizID = QuizFragmentArgs.fromBundle(getArguments()).getQuizID();
            quizName = QuizFragmentArgs.fromBundle(getArguments()).getQuizName();
            total_questions = QuizFragmentArgs.fromBundle(getArguments()).getQuestion();

        }

        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            current_UUID = firebaseAuth.getCurrentUser().getUid();
        }

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("QuizList").document(quizID).collection("Questions").get().
                addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            allQuestionsList = task.getResult().toObjects(QuestionsModel.class);

                            pickQuestions_from_allQuestionsList();

                            loadUI();
                        } else {
                            Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

//        options button and next Button clickListners..
        option_1.setOnClickListener(this);
        option_2.setOnClickListener(this);
        option_3.setOnClickListener(this);
        option_4.setOnClickListener(this);

        nextQuestionButton.setOnClickListener(this);

        crossButton.setOnClickListener(this);

        onBackPressedButton();
    }

    private void loadUI() {
        //        load first Questions
        loadQuestion(1);

//        enable options
        enableOptions();
    }

    private void loadQuestion(int questionNum) {
//        set quiz name
        quizTitle.setText(quizName);

//        set ques number
        question_no.setText(String.valueOf(questionNum));

//        load ques.
        QuestionText.setText(randomlyPickedQuestions.get(questionNum - 1).getQuestion());

//        load options
        option_1.setText(randomlyPickedQuestions.get(questionNum - 1).getOption_a());
        option_2.setText(randomlyPickedQuestions.get(questionNum - 1).getOption_b());
        option_3.setText(randomlyPickedQuestions.get(questionNum - 1).getOption_c());
        option_4.setText(randomlyPickedQuestions.get(questionNum - 1).getOption_d());

//        allowing to answering, after loading the ques..
        canAnswer = true;

//        start ques. timer
        startTimer(questionNum);
        currentQuestionNum = questionNum;
    }


    private void startTimer(int questionNum) {

        Long time = Long.valueOf(randomlyPickedQuestions.get(questionNum - 1).getTimer());

        quizTimer_QuesTime.setText(time.toString());

        timerProgressbar.setVisibility(View.VISIBLE);

        countDownTimer = new CountDownTimer(time * 1000, 1000) {
            @Override
            public void onTick(long milisUntilFinished) {
//                update left time
                quizTimer_QuesTime.setText(String.valueOf(milisUntilFinished / 1000));

//                time progress in percent
                Long percent = milisUntilFinished / (time * 10);
                timerProgressbar.setProgress(percent.intValue());
            }

            @Override
            public void onFinish() {
//                times up.... can not answer now..
                quesFeedback_verifyAns.setVisibility(View.VISIBLE);
                quesFeedback_verifyAns.setText("Oops, Time Up !");
                quesFeedback_verifyAns.setTextColor(getResources().getColor(R.color.colorRed));

                canAnswer = false;
                notAnswered++;

                showNextQuesBtn();
            }
        };

        countDownTimer.start();
    }


    private void enableOptions() {

//        enable and visible the options buttons during quiz ques..
        option_1.setVisibility(View.VISIBLE);
        option_2.setVisibility(View.VISIBLE);
        option_3.setVisibility(View.VISIBLE);
        option_4.setVisibility(View.VISIBLE);

        option_1.setEnabled(true);
        option_2.setEnabled(true);
        option_3.setEnabled(true);
        option_4.setEnabled(true);

//        hide feedback and nextButton during quiz question..
        quesFeedback_verifyAns.setVisibility(View.INVISIBLE);
        nextQuestionButton.setVisibility(View.INVISIBLE);
        nextQuestionButton.setEnabled(false);
    }

    private void pickQuestions_from_allQuestionsList() {
        for (int i = 0; i < total_questions; i++) {
            int randomNum = getRandomInteger(allQuestionsList.size());
            randomlyPickedQuestions.add(allQuestionsList.get(randomNum));
            allQuestionsList.remove(randomNum);
        }
    }

    public static int getRandomInteger(int max) {
        Random random = new Random();
        return random.nextInt(max);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonOption_1:
                verifyAnswer(option_1);
                quesFeedback_verifyAns.setVisibility(View.VISIBLE);
                break;

            case R.id.buttonOption_2:
                verifyAnswer(option_2);
                quesFeedback_verifyAns.setVisibility(View.VISIBLE);
                break;

            case R.id.buttonOption_3:
                verifyAnswer(option_3);
                quesFeedback_verifyAns.setVisibility(View.VISIBLE);
                break;

            case R.id.buttonOption_4:
                verifyAnswer(option_4);
                quesFeedback_verifyAns.setVisibility(View.VISIBLE);
                break;

            case R.id.quizFrag_nextQuesButtonID:
                if (currentQuestionNum == total_questions) {
//                    loading Results
                    showResults();
                } else {
                    currentQuestionNum++;
                    loadQuestion(currentQuestionNum);
                    resetOptions();
                }
                break;

            case R.id.crossButtonID:
//                enabling cross button to get exit from quiz fragment..
                crossButtonAlertDialog();
                break;
        }
    }


    private void showResults() {
        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("correct_answers", correctAnswers);
        resultMap.put("wrong_ans", wrongAnswers);
        resultMap.put("not_attemped_ans", notAnswered);

//        uploading result data onto database(firebase firestore)..
        firebaseFirestore.collection("QuizList").document(quizID).
                collection("Results").document(current_UUID).
                set(resultMap).
                addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            QuizFragmentDirections.ActionQuizFragmentToResultFragment actionDirection = QuizFragmentDirections.actionQuizFragmentToResultFragment();
                            actionDirection.setQuizID(quizID);
                            navController.navigate(actionDirection);
                        } else {
                            Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void resetOptions() {
        quesFeedback_verifyAns.setText(null);

        option_1.setBackground(getResources().getDrawable(R.drawable.button_options_outline, null));
        option_2.setBackground(getResources().getDrawable(R.drawable.button_options_outline, null));
        option_3.setBackground(getResources().getDrawable(R.drawable.button_options_outline, null));
        option_4.setBackground(getResources().getDrawable(R.drawable.button_options_outline, null));

        option_1.setTextColor(getResources().getColor(R.color.colorLightText, null));
        option_2.setTextColor(getResources().getColor(R.color.colorLightText, null));
        option_3.setTextColor(getResources().getColor(R.color.colorLightText, null));
        option_4.setTextColor(getResources().getColor(R.color.colorLightText, null));

        option_1.setVisibility(View.VISIBLE);
        option_2.setVisibility(View.VISIBLE);
        option_3.setVisibility(View.VISIBLE);
        option_4.setVisibility(View.VISIBLE);

    }

    private void verifyAnswer(Button selectedAnswerButton) {

// checking answers here
        if (canAnswer) {

            selectedAnswerButton.setTextColor(getResources().getColor(R.color.primary_black));
            if (randomlyPickedQuestions.get(currentQuestionNum - 1).getAnswer().equals(selectedAnswerButton.getText().toString())) {

//              ans is correct..
                correctAnswers++;
                selectedAnswerButton.setBackground(getResources().getDrawable(R.drawable.correct_answer_bg_btn, null));
                quesFeedback_verifyAns.setText("Correct Answer");
                quesFeedback_verifyAns.setTextColor(getResources().getColor(R.color.colorGreen));
            } else {
//                wrong ans
                wrongAnswers++;
                selectedAnswerButton.setBackground(getResources().getDrawable(R.drawable.wrong_answer_bg_btn, null));
                quesFeedback_verifyAns.setText("Wrong Answer \n Correct Answer is : " + randomlyPickedQuestions.get(currentQuestionNum - 1).getAnswer());
                quesFeedback_verifyAns.setTextColor(getResources().getColor(R.color.colorRed));

            }
            canAnswer = false;

//            stop timer after verifying answer by pressing any option button
            countDownTimer.cancel();

//            showing next button, just after selecting any option btn..
            showNextQuesBtn();
        }
    }

    private void showNextQuesBtn() {
        if (currentQuestionNum == total_questions) {
            nextQuestionButton.setText("Show Results");
            nextQuestionButton.setVisibility(View.VISIBLE);
            nextQuestionButton.setEnabled(true);
        } else {
            nextQuestionButton.setVisibility(View.VISIBLE);
            nextQuestionButton.setEnabled(true);
        }
    }

    private void crossButtonAlertDialog() {
        //  Enabling cross button here...
        View v = LayoutInflater.from(getContext()).inflate(R.layout.custom_view_alert_dialog, null, false);
        Button yesExitButton = v.findViewById(R.id.exit_yes_btn);
        Button noContinueButton = v.findViewById(R.id.continue_btn);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext())
                .setView(v)
                .setCancelable(false);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        noContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        yesExitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//              Stop countdown timer also before exiting from quiz..
                countDownTimer.cancel();

//                Now get transfer to detail frag from this quiz frag//
                navController.navigateUp();
                alertDialog.dismiss();
            }
        });
    }

//  When  Mobile's Back Button Pressed..
    private void onBackPressedButton() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                //  Enabling cross button here...
                View v = LayoutInflater.from(getContext()).inflate(R.layout.custom_view_alert_dialog, null, false);
                Button yesExitButton = v.findViewById(R.id.exit_yes_btn);
                Button noContinueButton = v.findViewById(R.id.continue_btn);

                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext())
                        .setView(v)
                        .setCancelable(false);

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                noContinueButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });

                yesExitButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//              Stop countdown timer also before exiting from quiz..
                        countDownTimer.cancel();

//                Now get transfer to detail frag from this quiz frag//
                        navController.navigateUp();
                        alertDialog.dismiss();
                    }
                });
            }
        };
        getActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
    }
}