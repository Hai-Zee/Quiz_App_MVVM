package com.example.quizappzeesh.views.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.quizappzeesh.model.QuestionsModel
import com.example.quizappzeesh.model.QuizModel
import com.example.quizappzeesh.R
import com.example.quizappzeesh.dao.QuizDao
import com.example.quizappzeesh.databinding.FragmentAddQuestionsBinding
import com.example.quizappzeesh.viewmodel.QuizListViewModel

class AddQuestionsFragment : Fragment() {

    private lateinit var fragmentAddQuestionsBinding: FragmentAddQuestionsBinding

    private var total_questions: Int = 0
    private var current_question_number = 1
    private lateinit var quizData: QuizModel
    private lateinit var navController: NavController
    private var questionsList = ArrayList<QuestionsModel>()

    private lateinit var quizListViewModel: QuizListViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        fragmentAddQuestionsBinding = FragmentAddQuestionsBinding.inflate(inflater, container, false)
        return fragmentAddQuestionsBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_list_fragment)

        quizListViewModel = ViewModelProvider(requireActivity()).get(QuizListViewModel::class.java)
        quizData = quizListViewModel.getQuizData()
        total_questions = quizData.questions

        fragmentAddQuestionsBinding.nextQuestionBtn.setOnClickListener {

            val question = fragmentAddQuestionsBinding.enterQuestion.text.toString().trim()
            val answer = fragmentAddQuestionsBinding.enterAnswer.text.toString().trim()
            val optionOne = fragmentAddQuestionsBinding.enterOptionOne.text.toString().trim()
            val optionTwo = fragmentAddQuestionsBinding.enterOptionSecond.text.toString().trim()
            val optionThree = fragmentAddQuestionsBinding.enterOptionThree.text.toString().trim()
            val optionFourth = fragmentAddQuestionsBinding.enterOptionFourth.text.toString().trim()
            val questionTiming = fragmentAddQuestionsBinding.questionTime.text.toString().trim()

            if (question.isNotEmpty() && answer.isNotEmpty() && optionOne.isNotEmpty() && optionTwo.isNotEmpty()
                    && optionThree.isNotEmpty() && optionFourth.isNotEmpty() && questionTiming.isNotEmpty()) {

                val questionsModel = QuestionsModel(
                        question = question,
                        answer = answer,
                        option_a = optionOne,
                        option_b = optionTwo,
                        option_c = optionThree,
                        option_d = optionFourth,
                        timer = Integer.parseInt(questionTiming))

                questionsList.add(questionsModel)

                if (current_question_number == total_questions) {
                    submitQuiz(quizData, questionsList)
                } else {
                    current_question_number++
                    resetUI()
                    if (current_question_number == total_questions) {
                        fragmentAddQuestionsBinding.nextQuestionBtn.text = "Submit"
                    }
                }
            } else {
                if (question.isNotEmpty())
                    fragmentAddQuestionsBinding.enterQuestion.error = null
                else
                    fragmentAddQuestionsBinding.enterQuestion.error = "Required"

                if (answer.isNotEmpty())
                    fragmentAddQuestionsBinding.enterAnswer.error = null
                else
                    fragmentAddQuestionsBinding.enterAnswer.error = "Required"

                if (optionOne.isNotEmpty())
                    fragmentAddQuestionsBinding.enterOptionOne.error = null
                else
                    fragmentAddQuestionsBinding.enterOptionSecond.error = "Required"

                if (optionTwo.isNotEmpty())
                    fragmentAddQuestionsBinding.enterOptionSecond.error = null
                else
                    fragmentAddQuestionsBinding.enterOptionSecond.error = "Required"

                if (optionThree.isNotEmpty())
                    fragmentAddQuestionsBinding.enterOptionThree.error = null
                else
                    fragmentAddQuestionsBinding.enterOptionThree.error = "Required"

                if (optionFourth.isNotEmpty())
                    fragmentAddQuestionsBinding.enterOptionFourth.error = null
                else
                    fragmentAddQuestionsBinding.enterOptionFourth.error = "Required"
            }
        }

        onBackPressed()
    }

    private fun submitQuiz(quizModelData: QuizModel, questionsList: ArrayList<QuestionsModel>) {
        val quizDao = QuizDao()
        Log.d("check", "submitQuiz: $questionsList")
        quizDao.createQuiz(quizModelData, questionsList)
        navController.navigate(R.id.action_addQuestionsFragment_to_createdQuizzesFragment)
    }

    private fun resetUI() {
        fragmentAddQuestionsBinding.questNum.text = current_question_number.toString()
        fragmentAddQuestionsBinding.enterQuestion.text.clear()
        fragmentAddQuestionsBinding.enterAnswer.text.clear()
        fragmentAddQuestionsBinding.enterOptionOne.text.clear()
        fragmentAddQuestionsBinding.enterOptionSecond.text.clear()
        fragmentAddQuestionsBinding.enterOptionThree.text.clear()
        fragmentAddQuestionsBinding.enterOptionFourth.text.clear()
        fragmentAddQuestionsBinding.questNum.text = ""
        fragmentAddQuestionsBinding.questionTime.text.clear()
    }

    private fun onBackPressed() {
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

                yesExitButton.setOnClickListener {
//                Now get transfer to detail frag from this quiz frag//
                    navController.navigateUp()
                    alertDialog.dismiss()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }
}