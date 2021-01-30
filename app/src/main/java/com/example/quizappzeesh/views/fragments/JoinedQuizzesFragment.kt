package com.example.quizappzeesh.views.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.quizappzeesh.R
import com.example.quizappzeesh.adapter.JoinedQuizzesAdapter
import com.example.quizappzeesh.databinding.FragmentJoinedQuizzesBinding
import com.example.quizappzeesh.model.QuizModel
import com.example.quizappzeesh.viewmodel.QuizListViewModel
import com.firebase.ui.firestore.ObservableSnapshotArray

class JoinedQuizzesFragment : Fragment(), JoinedQuizzesAdapter.OnJoinedQuizListInterface {

    private lateinit var quizListViewModel: QuizListViewModel
    private lateinit var quizListAdapter: JoinedQuizzesAdapter
    private lateinit var fade_in: Animation
    private lateinit var fade_out: Animation
    private lateinit var navController: NavController
    private lateinit var fragmentJoinedQuizzesBinding: FragmentJoinedQuizzesBinding
    private lateinit var quizObservableSnapshotArray: ObservableSnapshotArray<QuizModel>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        fragmentJoinedQuizzesBinding = FragmentJoinedQuizzesBinding.inflate(inflater, container, false)
        return fragmentJoinedQuizzesBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

//       Declaring Animations for  views
        fade_in = AnimationUtils.loadAnimation(context, R.anim.fade_in)
        fade_out = AnimationUtils.loadAnimation(context, R.anim.fade_out)

        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        fragmentJoinedQuizzesBinding.joinedQuizzesFragRecycleViewID.setLayoutManager(linearLayoutManager)
        fragmentJoinedQuizzesBinding.joinedQuizzesFragRecycleViewID.setHasFixedSize(true)
    }

    override fun onStart() {
        super.onStart()
        quizListViewModel = ViewModelProvider(requireActivity()).get(QuizListViewModel::class.java)
        quizListViewModel.getJoinedQuizOptions().observe(viewLifecycleOwner, Observer {
            quizObservableSnapshotArray = it.snapshots

//            if (it != null) {
//
//                fragmentJoinedQuizzesBinding.bottleAnimJoinedFrag.visibility = View.INVISIBLE
//            }
//            else {
//                fragmentJoinedQuizzesBinding.bottleAnimJoinedFrag.visibility = View.VISIBLE
//            }

            quizListAdapter = JoinedQuizzesAdapter(it, this)
            quizListAdapter.startListening()
            fragmentJoinedQuizzesBinding.joinedQuizzesFragRecycleViewID.adapter = quizListAdapter

            fragmentJoinedQuizzesBinding.joinedQuizzesFragRecycleViewID.startAnimation(fade_in)
            fragmentJoinedQuizzesBinding.joinedQuizzesProgressBar.startAnimation(fade_out)
            fragmentJoinedQuizzesBinding.joinedQuizzesProgressBar.visibility = View.GONE
        })
    }


    override fun onStop() {
        super.onStop()
        quizListAdapter.stopListening()
    }

    override fun onViewQuizClickListener(position: Int) {
        val quiz = quizObservableSnapshotArray[position]
        quizListViewModel.setQuizData(quiz)
        ListFragment.viewQuizClickListener()
    }
}