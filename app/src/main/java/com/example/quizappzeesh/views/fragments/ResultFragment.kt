package com.example.quizappzeesh.views.fragments

import android.animation.Animator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.airbnb.lottie.LottieAnimationView
import com.example.quizappzeesh.R
import com.example.quizappzeesh.dao.QuizDao
import com.example.quizappzeesh.model.QuizModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class ResultFragment : Fragment() {
    private lateinit var quizID: String
    private var quizDao: QuizDao = QuizDao()

    private lateinit var resultPercent: TextView
    private lateinit var correctAns: TextView
    private lateinit var wrongAns: TextView
    private lateinit var not_answered: TextView
    private lateinit var passedAnimation: LottieAnimationView
    private lateinit var failedAnimation: LottieAnimationView
    private lateinit var celebrationAnimation: LottieAnimationView
    private lateinit var totalPercentProgress: ProgressBar
    private lateinit var gotoHomeButton: Button
    private lateinit var navController: NavController
    private lateinit var correctAnswers: String
    private lateinit var wrongAnswers: String
    private lateinit var notAnswered: String
    private var per = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_result, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        resultPercent = view.findViewById(R.id.result_percent)
        correctAns = view.findViewById(R.id.resultFrag_CorrectAns_NUM_ID)
        wrongAns = view.findViewById(R.id.resultFrag_WrongAns_NUM_ID)
        not_answered = view.findViewById(R.id.resultFrag_MissedAns_NUM_ID)
        passedAnimation = view.findViewById(R.id.passedAnimID)
        failedAnimation = view.findViewById(R.id.failedAnimID)
        gotoHomeButton = view.findViewById(R.id.resultFrag_gotoHomeButtonID)
        celebrationAnimation = view.findViewById(R.id.celebrationAnimID)
        totalPercentProgress = view.findViewById(R.id.result_progress)
        quizID = ResultFragmentArgs.fromBundle(requireArguments()).quizID
        navController = Navigation.findNavController(view)


        quizID = ResultFragmentArgs.fromBundle(requireArguments()).quizID
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        quizDao.userCollection.document(quizDao.user!!.uid).collection("MyParticipatedQuiz").document(quizID)
                .collection("Result").document(quizDao.user!!.uid).get().addOnCompleteListener {
                    if (it.isSuccessful){

                        val documentSnapshot : DocumentSnapshot? = it.result

                        if (documentSnapshot != null && documentSnapshot.exists()){

                            correctAnswers = documentSnapshot["correct_answers"].toString()
                            wrongAnswers = documentSnapshot["wrong_ans"].toString()
                            notAnswered = documentSnapshot["not_attemped_ans"].toString()

                    val total = correctAnswers.toFloat() + wrongAnswers.toFloat() + notAnswered.toFloat()
                    per = Math.round(correctAnswers.toFloat() * 100 / total)
                    resultPercent.text = per.toString()

                    totalPercentProgress.progress = per
                    correctAns.text = correctAnswers
                    wrongAns.text = wrongAnswers
                    not_answered.text = notAnswered
                    show_pass_fail_anim()
                } else {
                    it.exception!!.message
                }
            }
        }

//        Go to HomePage (List Fragment)
        gotoHomeButton.setOnClickListener { navController.navigate(R.id.action_resultFragment_to_listFragment) }
        backButtonPressed()
    }

    fun show_pass_fail_anim() {
        if (per > 33) {
            failedAnimation.visibility = View.INVISIBLE
            passedAnimation.visibility = View.VISIBLE
            celebrationAnimation.visibility = View.VISIBLE
            celebrationAnimation.addAnimatorListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animator: Animator) {}
                override fun onAnimationEnd(animator: Animator) {
                    celebrationAnimation.visibility = View.INVISIBLE
                }

                override fun onAnimationCancel(animator: Animator) {}
                override fun onAnimationRepeat(animator: Animator) {}
            })
        } else {
            passedAnimation.visibility = View.INVISIBLE
            failedAnimation.visibility = View.VISIBLE
            celebrationAnimation.visibility = View.INVISIBLE
        }
    }

    private fun backButtonPressed() {
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navController.navigate(R.id.action_resultFragment_to_listFragment)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }
}