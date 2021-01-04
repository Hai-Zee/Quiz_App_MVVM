package com.example.quizappzeesh.views;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.quizappzeesh.Model.QuizListModel;
import com.example.quizappzeesh.R;
import com.example.quizappzeesh.viewmodel.QuizListViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.badge.BadgeUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class DetailFragment extends Fragment {

    private NavController navController;
    private QuizListViewModel quizListViewModel;
    private int position;
    private String quizID, quizName, correctAnswers, wrongAnswers, notAnswered;
    private ImageView detailImage;
    private TextView detailTitle, detailDesc, detailLevel, detailTotalQuestions, detailOldScore;
    private Button detailButton;

    private int totalQuestions = 0;
    private int per = 0;

    private String current_UUID;


    public DetailFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        detailImage = view.findViewById(R.id.detailImageID);
        detailTitle = view.findViewById(R.id.detailTitleID);
        detailDesc = view.findViewById(R.id.detailDescID);
        detailLevel = view.findViewById(R.id.detailDifficultyLevelInfoID);
        detailButton = view.findViewById(R.id.detailFragStartQuizButtonID);
        detailTotalQuestions = view.findViewById(R.id.detailTotalQuestInfoID);
        detailOldScore = view.findViewById(R.id.detailYourScoreInfoID);

        if (getArguments() != null) {
            position = DetailFragmentArgs.fromBundle(getArguments()).getPosition();
        }

        detailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DetailFragmentDirections.ActionDetailFragmentToQuizFragment actionDirection = DetailFragmentDirections.actionDetailFragmentToQuizFragment();
                actionDirection.setQuestion(totalQuestions);
                actionDirection.setQuizID(quizID);
                actionDirection.setQuizName(quizName);

                navController.navigate(actionDirection);
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        quizListViewModel = new ViewModelProvider(getActivity()).get(QuizListViewModel.class);

        quizListViewModel.getQuizListModelData().observe(getViewLifecycleOwner(), new Observer<List<QuizListModel>>() {
            @Override
            public void onChanged(List<QuizListModel> quizListModelList) {

                QuizListModel data = quizListModelList.get(position);
                detailTitle.setText(data.getName());
                detailDesc.setText(data.getDescription());
                detailLevel.setText(data.getLevel());
                detailTotalQuestions.setText(String.valueOf(data.getQuestions()));

                quizID = data.getQuiz_id();
                totalQuestions = (int) data.getQuestions();
                quizName = data.getName();

                Glide.with(getContext()).load(data.getImageUrl()).centerCrop().placeholder(R.drawable.placeholder_image).into(detailImage);

                // Loading Result's score to show the field of YOUR OLD SCORE..
                loadResultScore();
            }
        });
    }

    private void loadResultScore() {

        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            current_UUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            Log.d("TAG1", "loadResultScore: user is "+ current_UUID);
        }

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseFirestore.collection("QuizList").document(quizID).
                collection("Results").document(current_UUID).
                get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Log.d("TAG3", "loadResultScore: Task is On");
                DocumentSnapshot documentSnapshot = task.getResult();

                if (documentSnapshot!=null && documentSnapshot.exists()){

                    correctAnswers = documentSnapshot.get("correct_answers").toString();
                    wrongAnswers = documentSnapshot.get("wrong_ans").toString();
                    notAnswered = documentSnapshot.get("not_attemped_ans").toString();

                    float total = Float.parseFloat(correctAnswers) + Float.parseFloat(wrongAnswers) + Float.parseFloat(notAnswered);
                    per = Math.round((Float.parseFloat(correctAnswers)*100)/total);


                    detailOldScore.setText(String.valueOf(per+"%"));
                }
            }
        });
    }
}