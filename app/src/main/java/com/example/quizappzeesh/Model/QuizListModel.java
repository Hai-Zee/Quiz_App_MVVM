package com.example.quizappzeesh.Model;

import com.google.firebase.firestore.DocumentId;

public class QuizListModel {

    @DocumentId
    private String quiz_id;

    private String name, description, imageUrl, level, visibility;
    private int questions;

    public QuizListModel(){ }

    public QuizListModel(String quiz_id, String name, String description, String imageUrl, String level, String visibility, int questions) {
        this.quiz_id = quiz_id;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.level = level;
        this.visibility = visibility;
        this.questions = questions;
    }


    public String getQuiz_id() {
        return quiz_id;
    }

    public void setQuiz_id(String quiz_id) {
        this.quiz_id = quiz_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public long getQuestions() {
        return questions;
    }

    public void setQuestions(int questions) {
        this.questions = questions;
    }
}
