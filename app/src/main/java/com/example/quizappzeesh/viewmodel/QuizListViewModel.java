package com.example.quizappzeesh.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.quizappzeesh.Model.QuizListModel;
import com.example.quizappzeesh.repo.FirebaseRepository;

import java.util.List;

public class QuizListViewModel extends ViewModel implements FirebaseRepository.OnFireStoreTaskComplete {

    MutableLiveData<List<QuizListModel>> quizListModelData = new MutableLiveData<>();

    FirebaseRepository firebaseRepository = new FirebaseRepository(this);


    public QuizListViewModel(){
        firebaseRepository.getQuizData();
    }


    @Override
    public void quizListDataAdded(List<QuizListModel> quizListModelsList) {
        quizListModelData.setValue(quizListModelsList);
    }

    @Override
    public void onError(Exception e) {

    }

    public LiveData<List<QuizListModel>> getQuizListModelData(){
        return quizListModelData;
    }
}
