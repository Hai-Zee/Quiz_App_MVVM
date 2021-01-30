package com.example.quizappzeesh.dao

import com.example.quizappzeesh.model.QuestionsModel
import com.example.quizappzeesh.model.QuizModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class QuizDao {

    val firebaseFirestore = FirebaseFirestore.getInstance()
    val quizListCollection = firebaseFirestore.collection("QuizList")
    val userCollection = firebaseFirestore.collection("User")
    val user = Firebase.auth.currentUser

    fun createQuiz(quizModel: QuizModel, questionsList: ArrayList<QuestionsModel>) {

        GlobalScope.launch(Dispatchers.IO) {
            val docID: String = quizListCollection.document().id
            quizModel.quiz_id = docID
            quizListCollection.document(docID).set(quizModel)
            // now setting up questions list
            // we cant set ArrayList directly into firestore db.
            // we can set POJO class object or key value pair class object...
            val hashMap = HashMap<String, ArrayList<QuestionsModel>>()
            hashMap["questionsList"] = questionsList
            quizListCollection.document(docID)
                    .collection("Questions")
                    .document()
                    .set(hashMap)
            // now uploading things in user side
            userCollection.document(user!!.uid)
                    .collection("MyCreatedQuizzes")
                    .document(docID)
                    .set(quizModel)
            // also setting questions
            userCollection.document(user.uid)
                    .collection("MyCreatedQuizzes")
                    .document(docID)
                    .collection("Questions")
                    .document()
                    .set(hashMap)
        }
    }

    fun deleteQuiz(docID: String): Boolean {
        val a: Boolean = userCollection
                .document(user!!.uid)
                .collection("MyCreatedQuizzes")
                .document(docID)
                .delete().isSuccessful

        val b: Boolean = quizListCollection
                .document(docID)
                .delete().isSuccessful
        return a && b
    }


    fun joinQuiz(doc_ID: String) {

        GlobalScope.launch(Dispatchers.IO) {
//            var questionsList : ArrayList<QuestionsModel> = ArrayList()
            val quiz: QuizModel? = quizListCollection
                    .document(doc_ID)
                    .get()
                    .await()
                    .toObject(QuizModel::class.java)
//            val questionList
            quizListCollection.document(doc_ID)
                    .collection("Questions")
                    .get()
                    .addOnCompleteListener {
                        val querySnapshot: QuerySnapshot? = it.result
                        val documentSnapshotList: MutableList<DocumentSnapshot>? = querySnapshot?.documents

                        val documentSnapshot: DocumentSnapshot? = documentSnapshotList?.get(0)

                        val questionsList = documentSnapshot?.get("questionsList") as ArrayList<*>?

                        // Now add quiz and questions to MyParticipatedQuiz collection
                        userCollection.document(user?.uid!!)
                                .collection("MyParticipatedQuiz")
                                .document(doc_ID)
                                .set(quiz!!)
                                .addOnCompleteListener { task: Task<Void> ->

                                    if (task.isSuccessful) {
                                        val map = HashMap<String, ArrayList<QuestionsModel>>()

                                        map["questionsList"]  = questionsList as ArrayList<QuestionsModel>

                                        userCollection.document(user.uid)
                                                .collection("MyParticipatedQuiz")
                                                .document(doc_ID)
                                                .collection("Questions")
                                                .document()
                                                .set(map).addOnCompleteListener { task: Task<Void> ->
                                                    if (task.isSuccessful) {
                                                        // response
                                                    }
                                                }
                                    } else {
                                        // set error
                                    }
                                }
                    }
        }

    }

    fun setResult(correctAnswers: String, wrongAnswers: String, notAnswered: String, docID: String) {
        val resultMap = HashMap<String, Any>()
        resultMap["correct_answers"] = correctAnswers
        resultMap["wrong_ans"] = wrongAnswers
        resultMap["not_attemped_ans"] = notAnswered

        userCollection
                .document(user!!.uid)
                .collection("MyParticipatedQuiz")
                .document(docID)
                .collection("Result")
                .document(user.uid)
                .set(resultMap)
    }

    //    getting questions list from My MyParticipatedQuiz collections...
//    fun getAllQuestions(docID: String): ArrayList<QuestionsModel>? {
//
//        var questionsList: ArrayList<QuestionsModel>? = null
//        userCollection
//                .document(user!!.uid)
//                .collection("MyParticipatedQuiz")
//                .document(docID)
//                .collection("Questions")
//                .get().addOnCompleteListener {
//
//                    val questionListModel: QuestionList? = it.result?.documents?.get(0)?.toObject(QuestionList::class.java)
//                    questionsList = questionListModel?.questionsList
//
//                    Log.d("super10", "getAllQuestions: ${questionsList?.get(0)}")  // ye wala chl rha hai i mean data deriya hai
//                }
//
//
//        // ye wala nhi chl rha
//        Log.d("super11", "getAllQuestions: ${questionsList?.get(0)}   ////----////   ${ksdk(docID)}")
//        return questionsList
//    }
//
//
//    fun ksdk(docID: String): ArrayList<QuestionsModel>? {
//
//        var questionsList: ArrayList<QuestionsModel>? = null
//
//        GlobalScope.launch() {
//
//            val a = userCollection
//                    .document(user!!.uid)
//                    .collection("MyParticipatedQuiz")
//                    .document(docID)
//                    .collection("Questions")
//                    .get()
//                    .await()
//                    .documents
//                    .get(0)
//                    .toObject(QuestionList::class.java)
//
//            withContext(Dispatchers.Main) {
//                questionsList = a?.questionsList
//            }
//        }
//
//        return questionsList
//
//    }

}