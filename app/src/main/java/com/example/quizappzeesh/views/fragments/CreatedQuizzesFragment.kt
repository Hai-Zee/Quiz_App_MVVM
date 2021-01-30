package com.example.quizappzeesh.views.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quizappzeesh.R
import com.example.quizappzeesh.adapter.CreatedQuizzesAdapter
import com.example.quizappzeesh.dao.QuizDao
import com.example.quizappzeesh.databinding.FragmentCreatedQuizzesBinding
import com.example.quizappzeesh.model.QuizModel
import com.example.quizappzeesh.viewmodel.QuizListViewModel
import com.firebase.ui.firestore.ObservableSnapshotArray
import com.google.android.material.snackbar.Snackbar

class CreatedQuizzesFragment : Fragment(), CreatedQuizzesAdapter.CreatedQuizzesClickListnerInterface {
    private lateinit var fragmentCreatedQuizzesBinding: FragmentCreatedQuizzesBinding
    private lateinit var adapter: CreatedQuizzesAdapter
    private lateinit var createdQuizzSnapshotArray: ObservableSnapshotArray<QuizModel>
    private lateinit var navController: NavController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        fragmentCreatedQuizzesBinding = FragmentCreatedQuizzesBinding.inflate(inflater, container, false)
        return fragmentCreatedQuizzesBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        val linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.orientation = RecyclerView.VERTICAL

        fragmentCreatedQuizzesBinding.createdQuizzesFragRecycleViewID.layoutManager = linearLayoutManager
        fragmentCreatedQuizzesBinding.createdQuizzesFragRecycleViewID.setHasFixedSize(true)

//        onBackPress()
    }


    override fun onStart() {
        super.onStart()
        fragmentCreatedQuizzesBinding.createdQuizzesProgressBar.visibility = View.VISIBLE

        val quizListViewModel = ViewModelProvider(requireActivity()).get(QuizListViewModel::class.java)
        quizListViewModel.getMyCreatedQuizzes().observe(viewLifecycleOwner, Observer {

//            getting snapshot array..
            createdQuizzSnapshotArray = it.snapshots
            adapter = CreatedQuizzesAdapter(it, this)
            adapter.startListening()
            fragmentCreatedQuizzesBinding.createdQuizzesFragRecycleViewID.adapter = adapter
            fragmentCreatedQuizzesBinding.createdQuizzesFragRecycleViewID.visibility = View.VISIBLE
            fragmentCreatedQuizzesBinding.createdQuizzesProgressBar.visibility = View.INVISIBLE
        })
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    override fun shareButtonListner(position: Int) {
        val intent = Intent(Intent.ACTION_SEND).setType("text/plain")
        intent.putExtra(Intent.EXTRA_TEXT, createdQuizzSnapshotArray.get(position).quiz_id)
        Log.d("share button1", "shareButtonListner: " + createdQuizzSnapshotArray.get(position).quiz_id)
        val chooser: Intent = Intent.createChooser(intent, "select below")
        startActivity(chooser)
    }

    override fun deleteButtonListner(position: Int) {
        val quizDao = QuizDao()
        val isDeleted: Boolean = quizDao.deleteQuiz(createdQuizzSnapshotArray.get(position).quiz_id)
        Log.d("delete button1", "shareButtonListner: " + createdQuizzSnapshotArray.get(position).quiz_id)

        if (isDeleted)
            Snackbar.make(requireView(), "Quiz Deleted", Snackbar.LENGTH_LONG).show()
        else
            Snackbar.make(requireView(), "Something went wrong !!", Snackbar.LENGTH_LONG).show()
    }

//    private fun onBackPress() {
//        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
//            override fun handleOnBackPressed() {
//                navController.navigate(R.id.action_createdQuizzesFragment_to_joinedQuizzesFragment)
//            }
//        }
//        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
//    }
}