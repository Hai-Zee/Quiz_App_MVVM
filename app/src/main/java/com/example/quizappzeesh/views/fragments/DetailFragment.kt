package com.example.quizappzeesh.views.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.quizappzeesh.R
import com.example.quizappzeesh.databinding.FragmentDetailBinding
import com.example.quizappzeesh.model.QuizModel
import com.example.quizappzeesh.viewmodel.QuizListViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import kotlin.random.Random

class DetailFragment : Fragment() {

    private lateinit var navController: NavController
    private lateinit var quizListViewModel: QuizListViewModel
    private lateinit var quiz: QuizModel
    private var quizID: String = ""
    private lateinit var correctAnswers: String
    private lateinit var wrongAnswers: String
    private lateinit var notAnswered: String
    private lateinit var fragmentDetailBinding: FragmentDetailBinding
    private var totalQuestions = 0
    private var per = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        fragmentDetailBinding = FragmentDetailBinding.inflate(inflater, container, false)
        return fragmentDetailBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)

        quizListViewModel = ViewModelProvider(requireActivity()).get(QuizListViewModel::class.java)
        quiz = quizListViewModel.getQuizData()

        Log.d("TAG1", "onViewCreated: ${quiz.name}")
        fragmentDetailBinding.quiz = quiz
        quizID = quiz.quiz_id
        totalQuestions = quiz.questions
//        detailButton.setOnClickListener(View.OnClickListener {
//            val actionDirection: DetailFragmentDirections.ActionDetailFragmentToQuizFragment = DetailFragmentDirections.actionDetailFragmentToQuizFragment()
//            actionDirection.setQuestion(totalQuestions)
//            actionDirection.setQuizID(quizID)
//            actionDirection.setQuizName(quizName)
//            navController.navigate(actionDirection)
//        })
//    }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // Loading Result's score to show the field of YOUR OLD SCORE..
        loadResultScore()

        fragmentDetailBinding.detailFragStartQuizButtonID.setOnClickListener {
            navController.navigate(R.id.action_detailFragment_to_quizFragment)
        }
    }

    private fun loadResultScore() {

        val firebaseFirestore = FirebaseFirestore.getInstance()
        val firebaseAuth = FirebaseAuth.getInstance()
        val userID = firebaseAuth.currentUser!!.uid

        val fks: Task<DocumentSnapshot> = firebaseFirestore
                .collection("User")
                .document(userID)
                .collection("MyParticipatedQuiz")
                .document(quizID)
                .collection("Result")
                .document(userID)
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {

                        val documentSnapshot: DocumentSnapshot? = it.result

                        if (documentSnapshot != null && documentSnapshot.exists()) {

                            correctAnswers = documentSnapshot["correct_answers"].toString()
                            wrongAnswers = documentSnapshot["wrong_ans"].toString()
                            notAnswered = documentSnapshot["not_attemped_ans"].toString()

                            val total = correctAnswers.toFloat() + wrongAnswers.toFloat() + notAnswered.toFloat()
                            per = Math.round(correctAnswers.toFloat() * 100 / total)

                            fragmentDetailBinding.detailYourScoreInfoID.text = "$per%"
                        }
                    }
                }
    }
}
