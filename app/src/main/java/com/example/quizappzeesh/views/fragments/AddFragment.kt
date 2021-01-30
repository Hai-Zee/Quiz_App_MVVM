package com.example.quizappzeesh.views.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.quizappzeesh.R
import com.example.quizappzeesh.dao.QuizDao
import com.example.quizappzeesh.databinding.FragmentAddBinding
import com.example.quizappzeesh.databinding.JoinQuizAlertDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddFragment : BottomSheetDialogFragment() {
    private lateinit var fragmentAddBinding: FragmentAddBinding
    private lateinit var navController: NavController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_add, container, false)

        fragmentAddBinding = FragmentAddBinding.inflate(inflater, container, false)
        return fragmentAddBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_list_fragment)

//        create quiz
        fragmentAddBinding.createQuizSelectBtn.setOnClickListener {
            navController.navigate(R.id.action_addFragment2_to_createQuizFragment)
        }
//        join quiz
        fragmentAddBinding.joinQuizSelectBtn.setOnClickListener {
            val joinQuizAlertDialogBinding = JoinQuizAlertDialogBinding.inflate(LayoutInflater.from(context))
            val alertDialog = AlertDialog.Builder(requireContext())
                    .setView(joinQuizAlertDialogBinding.root)
                    .setCancelable(false)
                    .create()
             alertDialog.show()

//            submit button
            joinQuizAlertDialogBinding.submitQuizIDBtn.setOnClickListener {
                val docID_from_alertdialog = joinQuizAlertDialogBinding.quizIdEnteryfield.text.toString().trim()
                if (docID_from_alertdialog.isNotEmpty()){
                    // set progressbar or process dialog below...
                    val quizDao = QuizDao()
                    quizDao.joinQuiz(docID_from_alertdialog)
                    navController.navigateUp()
                } else {
                    joinQuizAlertDialogBinding.quizIdEnteryfield.error = "Required"
                }
                alertDialog.dismiss()
            }
//            cancel button
            joinQuizAlertDialogBinding.cancelQuizIDBtn.setOnClickListener {
                alertDialog.dismiss()
            }
        }
    }
}