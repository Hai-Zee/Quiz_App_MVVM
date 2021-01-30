package com.example.quizappzeesh.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.quizappzeesh.databinding.JoinedQuizListCustomViewBinding
import com.example.quizappzeesh.model.QuizModel
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class JoinedQuizzesAdapter(options : FirestoreRecyclerOptions<QuizModel>, val onJoinedQuizListInterface: OnJoinedQuizListInterface) : FirestoreRecyclerAdapter<QuizModel, JoinedQuizzesAdapter.ViewHolder>(options){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val joinedQuizListCustomViewBinding = JoinedQuizListCustomViewBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(joinedQuizListCustomViewBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: QuizModel) {
        holder.joinedQuizListCustomViewBinding.joinedQuizData = model
        holder.joinedQuizListCustomViewBinding.position = position
        holder.joinedQuizListCustomViewBinding.onJoinedQuizListInterface = onJoinedQuizListInterface
    }

    class ViewHolder(val joinedQuizListCustomViewBinding: JoinedQuizListCustomViewBinding) : RecyclerView.ViewHolder(joinedQuizListCustomViewBinding.root)

    interface OnJoinedQuizListInterface {
        fun onViewQuizClickListener(position: Int)

    }
}