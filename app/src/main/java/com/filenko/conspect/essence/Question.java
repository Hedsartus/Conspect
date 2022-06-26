package com.filenko.conspect.essence;

import java.util.ArrayList;
import java.util.List;

public class Question {
    private int id;
    private int idNote;
    private String title;
    private final List<Answer> answers = new ArrayList<>();

    public Question(int id, int idNote, String title) {
        this.id = id;
        this.idNote = idNote;
        this.title = title;
    }

    public Question() {
        this.id = 0;
        this.idNote = 0;
        this.title = null;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setIdNote(int idNote) {
        this.idNote = idNote;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public int getIdNote() {
        return idNote;
    }

    public String getTitle() {
        return title;
    }

    public Answer getAnswer(int id) {
        for(int i = 0; i<this.answers.size(); i++) {
            if(this.answers.get(i).getId() == id) {
                return this.answers.get(i);
            }
        }

        return null;
    }

    public List<Answer> getListAnswers () {
        return this.answers;
    }

    public void addAnswer (Answer answer) {
        this.answers.add(answer);
    }
}
