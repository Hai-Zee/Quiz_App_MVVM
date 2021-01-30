package com.example.quizappzeesh.repo

import androidx.lifecycle.MutableLiveData
import com.example.quizappzeesh.dao.QuizDao
import com.example.quizappzeesh.model.QuizModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FirebaseRepository {

    private val quizDao = QuizDao()
    private var liveQuizOptions: MutableLiveData<FirestoreRecyclerOptions<QuizModel>> = MutableLiveData()
    private var liveCreatedQuizOptions: MutableLiveData<FirestoreRecyclerOptions<QuizModel>> = MutableLiveData()


    fun getJoinedQuizOptions(): MutableLiveData<FirestoreRecyclerOptions<QuizModel>> {

        GlobalScope.launch(Dispatchers.IO) {
            val queryOfJoinedQuiz = quizDao.userCollection
                    .document(quizDao.user?.uid!!)
                    .collection("MyParticipatedQuiz")
                    .orderBy("publishedAt", Query.Direction.DESCENDING)

            val options: FirestoreRecyclerOptions<QuizModel> = FirestoreRecyclerOptions.Builder<QuizModel>()
                    .setQuery(queryOfJoinedQuiz, QuizModel::class.java)
                    .build()

            withContext(Dispatchers.Main) {
                liveQuizOptions.value = options
            }
        }
        return liveQuizOptions
    }

    fun getMyCreatedQuizzes(): MutableLiveData<FirestoreRecyclerOptions<QuizModel>> {

        GlobalScope.launch(Dispatchers.IO) {

            val queryOfCreatedQuiz = quizDao.userCollection
                    .document(quizDao.user?.uid!!)
                    .collection("MyCreatedQuizzes")
                    .orderBy("publishedAt", Query.Direction.DESCENDING)

            val options: FirestoreRecyclerOptions<QuizModel> = FirestoreRecyclerOptions.Builder<QuizModel>()
                    .setQuery(queryOfCreatedQuiz, QuizModel::class.java)
                    .build()

            withContext(Dispatchers.Main) {
                liveCreatedQuizOptions.value = options
            }
        }
        return liveCreatedQuizOptions
    }
}
