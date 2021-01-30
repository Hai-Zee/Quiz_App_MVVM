package com.example.quizappzeesh.views.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.quizappzeesh.model.QuestionsModel
import com.example.quizappzeesh.R
import com.example.quizappzeesh.dao.QuizDao
import com.example.quizappzeesh.databinding.FragmentQuizBinding
import com.example.quizappzeesh.model.QuizModel
import com.example.quizappzeesh.viewmodel.QuizListViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.collections.ArrayList
import kotlin.random.Random

class QuizFragment : Fragment(), View.OnClickListener {

    private lateinit var myQuiz: QuizModel
    private lateinit var quizID: String
    private val quizDao = QuizDao()
    private lateinit var navController: NavController
    private lateinit var countDownTimer: CountDownTimer
    private var total_questions = 0
    private var canAnswer = false
    private var currentQuestionNum = 0
    private var correctAnswers = 0
    private var wrongAnswers = 0
    private var notAnswered = 0
    private lateinit var fragmentQuizBinding: FragmentQuizBinding
    private lateinit var quizListViewModel: QuizListViewModel
    private var randomlyPickedQuestions: ArrayList<QuestionsModel> = ArrayList()

    //  Firebase Auth & firestore
    private var firebaseAuth: FirebaseAuth? = null
    private lateinit var firebaseFirestore: FirebaseFirestore


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        fragmentQuizBinding = FragmentQuizBinding.inflate(inflater, container, false)
        return fragmentQuizBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        quizListViewModel = ViewModelProvider(requireActivity()).get(QuizListViewModel::class.java)
        myQuiz = quizListViewModel.getQuizData()
        quizID = myQuiz.quiz_id
        total_questions = myQuiz.questions

        navController = Navigation.findNavController(view)

//      starting getting questions from here..
        quizDao.userCollection
                .document(quizDao.user!!.uid)
                .collection("MyParticipatedQuiz")
                .document(quizID)
                .collection("Questions")
                .get().addOnCompleteListener {

                    val questionListModel: QuestionListModel? = it.result?.documents?.get(0)?.toObject(QuestionListModel()::class.java)
                    val myList: ArrayList<QuestionsModel>? = questionListModel?.questionsList
                    // picking random questions from all questions list...
                    pickRandomQuestions(myList)
                    loadUI()
                    // loadUi()  method ko jb is scope ke baahr call krre hai to wo pickQuestions_ method ke phle hi chl rha hai
                    // because wo UI thread pr chlra hai and
                    // pickQuestions Background thread pr

//                    val questionsList = documentSnapshot?.get("questionsList")  // i think its a list of hashmap
//                    val a: ArrayList<*> = questionsList as ArrayList<*>
//                    Log.d("super1", "onViewCreated: ${a.get(0)}")
//                    // yha tk shi hai

                    // method one                                      ||          method two
//                    val arralist: ArrayList<Any> = ArrayList(a)       ||          val check = ArrayList<QuestionsModel>()
//                    val b = arralist as ArrayList<QuestionsModel>     ||          check.addAll(a as Collection<QuestionsModel>)
                }

        //  options button and next Button clickListeners..
        fragmentQuizBinding.buttonOption1.setOnClickListener(this)
        fragmentQuizBinding.buttonOption2.setOnClickListener(this)
        fragmentQuizBinding.buttonOption3.setOnClickListener(this)
        fragmentQuizBinding.buttonOption4.setOnClickListener(this)
        fragmentQuizBinding.quizFragNextQuesButtonID.setOnClickListener(this)
        fragmentQuizBinding.crossButtonID.setOnClickListener(this)
        onBackPressedButton()
    }

    private fun pickRandomQuestions(allQuestionsList: ArrayList<QuestionsModel>?) {

        for (i in 0 until allQuestionsList?.size!!) {
            val randomNum = Random.nextInt(0, allQuestionsList.size)
            randomlyPickedQuestions.add(allQuestionsList.get(randomNum))
            allQuestionsList.removeAt(randomNum)
        }
    }

    private fun loadUI() {
//        load first Question
        loadQuestion(1)
        //        set quiz name
        fragmentQuizBinding.quizFragQuizNameID.text = myQuiz.name
//        enable options
        enableOptions()
    }

    private fun loadQuestion(questionNum: Int) {
//        set ques number
        fragmentQuizBinding.quizQuesNumbersID.text = currentQuestionNum.toString()
//        load ques.
        fragmentQuizBinding.quizQuestion.text = randomlyPickedQuestions[questionNum - 1].question
//        load options
        fragmentQuizBinding.buttonOption1.text = randomlyPickedQuestions[questionNum - 1].option_a
        fragmentQuizBinding.buttonOption2.text = randomlyPickedQuestions[questionNum - 1].option_b
        fragmentQuizBinding.buttonOption3.text = randomlyPickedQuestions[questionNum - 1].option_c
        fragmentQuizBinding.buttonOption4.text = randomlyPickedQuestions[questionNum - 1].option_d

//        allowing to answering, after loading the ques..
        canAnswer = true

//        start ques. timer
        startTimer(questionNum)
        currentQuestionNum = questionNum
    }

    private fun startTimer(questionNum: Int) {

        val time = java.lang.Long.valueOf(randomlyPickedQuestions[questionNum - 1].timer.toLong())
        fragmentQuizBinding.quizTimerPrgrsbarID.text = time.toString()
        fragmentQuizBinding.quizPrgrsbarID.visibility = View.VISIBLE
        countDownTimer = object : CountDownTimer(time * 1000, 1000) {
            override fun onTick(milisUntilFinished: Long) {
//                update left time
                fragmentQuizBinding.quizTimerPrgrsbarID.text = (milisUntilFinished / 1000).toString()

//                time progress in percent
                val percent = milisUntilFinished / (time * 10)
                fragmentQuizBinding.quizPrgrsbarID.progress = percent.toInt()
            }

            override fun onFinish() {
//                times up.... can not answer now..
                fragmentQuizBinding.quizQuesVerifyingLine.visibility = View.VISIBLE
                fragmentQuizBinding.quizQuesVerifyingLine.text = "Oops, Time Up !"
                fragmentQuizBinding.quizQuesVerifyingLine.setTextColor(resources.getColor(R.color.colorRed))
                canAnswer = false
                notAnswered++
                showNextQuesBtn()
            }
        }
        countDownTimer.start()
    }

    private fun enableOptions() {

//        enable and visible the options buttons during quiz ques..
        fragmentQuizBinding.buttonOption1.visibility = View.VISIBLE
        fragmentQuizBinding.buttonOption2.visibility = View.VISIBLE
        fragmentQuizBinding.buttonOption3.visibility = View.VISIBLE
        fragmentQuizBinding.buttonOption4.visibility = View.VISIBLE
        fragmentQuizBinding.buttonOption1.isEnabled = true
        fragmentQuizBinding.buttonOption2.isEnabled = true
        fragmentQuizBinding.buttonOption3.isEnabled = true
        fragmentQuizBinding.buttonOption4.isEnabled = true

//        hide feedback and nextButton during quiz question..
        fragmentQuizBinding.quizQuesVerifyingLine.visibility = View.INVISIBLE
        fragmentQuizBinding.quizFragNextQuesButtonID.visibility = View.INVISIBLE
        fragmentQuizBinding.quizFragNextQuesButtonID.isEnabled = false
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.buttonOption_1 -> {
                verifyAnswer(fragmentQuizBinding.buttonOption1)
                fragmentQuizBinding.quizQuesVerifyingLine.visibility = View.VISIBLE
            }
            R.id.buttonOption_2 -> {
                verifyAnswer(fragmentQuizBinding.buttonOption2)
                fragmentQuizBinding.quizQuesVerifyingLine.visibility = View.VISIBLE
            }
            R.id.buttonOption_3 -> {
                verifyAnswer(fragmentQuizBinding.buttonOption3)
                fragmentQuizBinding.quizQuesVerifyingLine.visibility = View.VISIBLE
            }
            R.id.buttonOption_4 -> {
                verifyAnswer(fragmentQuizBinding.buttonOption4)
                fragmentQuizBinding.quizQuesVerifyingLine.visibility = View.VISIBLE
            }
            R.id.quizFrag_nextQuesButtonID -> if (currentQuestionNum == total_questions) {
//                    loading Results
                showResults()
            } else {
                currentQuestionNum++
                loadQuestion(currentQuestionNum)
                resetOptions()
            }
            R.id.crossButtonID -> //                enabling cross button to get exit from quiz fragment..
                crossButtonAlertDialog()
        }
    }

    private fun showResults() {
        quizDao.setResult(correctAnswers.toString(), wrongAnswers.toString(), notAnswered.toString(), quizID)

        val actionDirection: QuizFragmentDirections.ActionQuizFragmentToResultFragment = QuizFragmentDirections.actionQuizFragmentToResultFragment()
        actionDirection.setQuizID(quizID)
        navController.navigate(actionDirection)
    }

    private fun resetOptions() {

//        disappear next button till any option pressed..
        fragmentQuizBinding.quizFragNextQuesButtonID.visibility = View.INVISIBLE
        fragmentQuizBinding.quizFragNextQuesButtonID.isEnabled = false
//        disappear next button till any option pressed..
        fragmentQuizBinding.quizQuesVerifyingLine.setText(null)
//        reset options..
        fragmentQuizBinding.buttonOption1.background = resources.getDrawable(R.drawable.button_options_outline, null)
        fragmentQuizBinding.buttonOption2.background = resources.getDrawable(R.drawable.button_options_outline, null)
        fragmentQuizBinding.buttonOption3.background = resources.getDrawable(R.drawable.button_options_outline, null)
        fragmentQuizBinding.buttonOption4.background = resources.getDrawable(R.drawable.button_options_outline, null)
        fragmentQuizBinding.buttonOption1.setTextColor(resources.getColor(R.color.colorLightText, null))
        fragmentQuizBinding.buttonOption2.setTextColor(resources.getColor(R.color.colorLightText, null))
        fragmentQuizBinding.buttonOption3.setTextColor(resources.getColor(R.color.colorLightText, null))
        fragmentQuizBinding.buttonOption4.setTextColor(resources.getColor(R.color.colorLightText, null))
        fragmentQuizBinding.buttonOption1.visibility = View.VISIBLE
        fragmentQuizBinding.buttonOption2.visibility = View.VISIBLE
        fragmentQuizBinding.buttonOption3.visibility = View.VISIBLE
        fragmentQuizBinding.buttonOption4.visibility = View.VISIBLE
    }

    private fun verifyAnswer(selectedAnswerButton: Button?) {

// checking answers here
        if (canAnswer) {
            selectedAnswerButton!!.setTextColor(resources.getColor(R.color.primary_black))
            if (randomlyPickedQuestions[currentQuestionNum - 1].answer == selectedAnswerButton.text.toString()) {

//              ans is correct..
                correctAnswers++
                selectedAnswerButton.background = resources.getDrawable(R.drawable.correct_answer_bg_btn, null)
                fragmentQuizBinding.quizQuesVerifyingLine.text = "Correct Answer"
                fragmentQuizBinding.quizQuesVerifyingLine.setTextColor(resources.getColor(R.color.colorGreen))
            } else {
//                wrong ans
                wrongAnswers++
                selectedAnswerButton.background = resources.getDrawable(R.drawable.wrong_answer_bg_btn, null)
                fragmentQuizBinding.quizQuesVerifyingLine.text = """Wrong Answer
 Correct Answer is : ${randomlyPickedQuestions[currentQuestionNum - 1].answer}"""
                fragmentQuizBinding.quizQuesVerifyingLine.setTextColor(resources.getColor(R.color.colorRed))
            }
            canAnswer = false

//            stop timer after verifying answer by pressing any option button
            countDownTimer.cancel()

//            showing next button, just after selecting any option btn..
            showNextQuesBtn()
        }
    }

    private fun showNextQuesBtn() {
        if (currentQuestionNum == total_questions) {
            fragmentQuizBinding.quizFragNextQuesButtonID.text = "Show Results"
            fragmentQuizBinding.quizFragNextQuesButtonID.visibility = View.VISIBLE
            fragmentQuizBinding.quizFragNextQuesButtonID.isEnabled = true
        } else {
            fragmentQuizBinding.quizFragNextQuesButtonID.visibility = View.VISIBLE
            fragmentQuizBinding.quizFragNextQuesButtonID.isEnabled = true
        }
    }

    private fun crossButtonAlertDialog() {
        //  Enabling cross button here...
        val v = LayoutInflater.from(context).inflate(R.layout.exit_alert_dialog_custom_view, null, false)
        val yesExitButton = v.findViewById<Button>(R.id.exit_yes_btn)
        val noContinueButton = v.findViewById<Button>(R.id.continue_btn)
        val builder = AlertDialog.Builder(requireContext())
                .setView(v)
                .setCancelable(false)
        val alertDialog = builder.create()
        alertDialog.show()
        noContinueButton.setOnClickListener { alertDialog.dismiss() }
        yesExitButton.setOnClickListener { //              Stop countdown timer also before exiting from quiz..
            countDownTimer.cancel()

//                Now get transfer to detail frag from this quiz frag//
            navController.navigateUp()
            alertDialog.dismiss()
        }
    }

    //  When  Mobile's Back Button Pressed..
    private fun onBackPressedButton() {
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                //  Enabling cross button here...
                val v = LayoutInflater.from(context).inflate(R.layout.exit_alert_dialog_custom_view, null, false)
                val yesExitButton = v.findViewById<Button>(R.id.exit_yes_btn)
                val noContinueButton = v.findViewById<Button>(R.id.continue_btn)
                val builder = AlertDialog.Builder(requireContext())
                        .setView(v)
                        .setCancelable(false)
                val alertDialog = builder.create()
                alertDialog.show()
                noContinueButton.setOnClickListener { alertDialog.dismiss() }

                yesExitButton.setOnClickListener { //              Stop countdown timer also before exiting from quiz..
                    countDownTimer.cancel()
//                Now get transfer to detail frag from this quiz frag//
                    navController.navigateUp()
                    alertDialog.dismiss()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }


    data class QuestionListModel(val questionsList: ArrayList<QuestionsModel>? = null)
}

