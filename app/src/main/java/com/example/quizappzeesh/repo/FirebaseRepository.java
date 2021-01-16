package com.example.quizappzeesh.repo;


import android.util.Log;

import androidx.annotation.NonNull;

import com.example.quizappzeesh.Model.QuizListModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Collection;
import java.util.List;

public class FirebaseRepository {

    OnFireStoreTaskComplete onFireStoreTaskComplete;

    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    CollectionReference quizRef = firebaseFirestore.collection("QuizList");

    public FirebaseRepository(OnFireStoreTaskComplete onFireStoreTaskComplete){
        this.onFireStoreTaskComplete = onFireStoreTaskComplete;
    }

    public void getQuizData(){
        quizRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    onFireStoreTaskComplete.quizListDataAdded(task.getResult().toObjects(QuizListModel.class));
                }
                else{
                    Log.d("quizData_repo", "onComplete: "+ task.getException().getMessage());
                }
            }
        });
    }

    public interface OnFireStoreTaskComplete{
        void quizListDataAdded(List<QuizListModel> quizListModelsList);
    }
}
