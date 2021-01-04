package com.example.quizappzeesh.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.quizappzeesh.Model.QuizListModel;
import com.example.quizappzeesh.R;

import java.util.List;

public class QuizListAdapter extends RecyclerView.Adapter<QuizListAdapter.ViewHolder> {

    List<QuizListModel> quizListModelList;
    OnQuizListInterface onQuizListInterface;

    public QuizListAdapter(OnQuizListInterface onQuizListInterface){
        this.onQuizListInterface = onQuizListInterface;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quiz_list_custom_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        QuizListModel listData = quizListModelList.get(position);
        holder.listTitle.setText(listData.getName());
        holder.listDesc.setText(listData.getDescription());
        holder.listLevel.setText(listData.getLevel());

        Glide.with(holder.listImage.getContext())
                .load(listData.getImageUrl())
                .centerCrop()
                .placeholder(R.drawable.placeholder_image)
                .into(holder.listImage);

        holder.listButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onQuizListInterface.onClickListner(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return quizListModelList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView listImage;
        private TextView listTitle, listDesc, listLevel;
        private Button listButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            listImage = itemView.findViewById(R.id.listImageID);
            listTitle = itemView.findViewById(R.id.listTitleID);
            listDesc = itemView.findViewById(R.id.listDescID);
            listLevel = itemView.findViewById(R.id.listDifficultyLevelID);
            listButton = itemView.findViewById(R.id.listFragViewQuizButtonID);
        }
    }

    public void setQuizListModelList(List<QuizListModel> quizListModelList) {
        this.quizListModelList = quizListModelList;
        notifyDataSetChanged();
    }

    public interface OnQuizListInterface{
        void onClickListner(int position);
    }
}
