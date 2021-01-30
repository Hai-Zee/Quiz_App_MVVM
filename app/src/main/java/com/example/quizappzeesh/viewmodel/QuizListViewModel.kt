package com.example.quizappzeesh.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.quizappzeesh.model.QuizModel
import com.example.quizappzeesh.repo.FirebaseRepository
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class QuizListViewModel : ViewModel() {

    private val firebaseRepo = FirebaseRepository()
    private var quizModel = QuizModel()

    fun getJoinedQuizOptions(): MutableLiveData<FirestoreRecyclerOptions<QuizModel>> {
        return firebaseRepo.getJoinedQuizOptions()
    }

    fun getMyCreatedQuizzes(): MutableLiveData<FirestoreRecyclerOptions<QuizModel>> {
        return firebaseRepo.getMyCreatedQuizzes()
    }

    // These setter and getter are for Quiz model data transfer from one fragment to another, when needed....
    fun setQuizData(quizModel: QuizModel) {
        this.quizModel = quizModel
    }
    fun getQuizData(): QuizModel {
        return quizModel
    }
}