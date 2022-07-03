package com.filenko.conspect.essence;

import java.util.ArrayList;
import java.util.List;

public class Question {
    private int id;
    private int type; // 1 - множественный, 2 - да/нет
    private int idNote;
    private String title;
    private final List<Answer> answers = new ArrayList<>();

    public Question(int id, int type, int idNote, String title) {
        this.id = id;
        this.type = type;
        this.idNote = idNote;
        this.title = title;
    }

    public Question() {
        this.id = 0;
        this.type = 1;
        this.idNote = 0;
        this.title = null;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setType(int type) {
        this.type = type;
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

    public int getType() {
        return type;
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
