package com.example.quizappzeesh.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.quizappzeesh.databinding.CreatedQuizzesCustomViewBinding
import com.example.quizappzeesh.model.QuizModel
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class CreatedQuizzesAdapter(options: FirestoreRecyclerOptions<QuizModel>, val createdQuizzesClickListnerInterface: CreatedQuizzesClickListnerInterface) : FirestoreRecyclerAdapter<QuizModel, CreatedQuizzesAdapter.ViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater : LayoutInflater = LayoutInflater.from(parent.context)
        val createdQuizzesCustomViewBinding = CreatedQuizzesCustomViewBinding.inflate(layoutInflater, parent, false)
        createdQuizzesCustomViewBinding.createdQuizzesClickListnerInterface = createdQuizzesClickListnerInterface
        return ViewHolder(createdQuizzesCustomViewBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: QuizModel) {
        holder.createdQuizzesCustomViewBinding.data = model
        holder.createdQuizzesCustomViewBinding.position = position
    }

    class ViewHolder(val createdQuizzesCustomViewBinding: CreatedQuizzesCustomViewBinding) : RecyclerView.ViewHolder(createdQuizzesCustomViewBinding.root){
    }

    interface CreatedQuizzesClickListnerInterface{
        fun shareButtonListner(position: Int)
        fun deleteButtonListner(position: Int)
    }
}