package com.filenko.conspect.essence;

public class Answer {
    private int id;
    private int idQuestion;
    private String answer;
    private boolean correct;

    public Answer(int id, int idQuestion, String answer, int correct) {
        this.id = id;
        this.idQuestion = idQuestion;
        this.answer = answer;
        this.correct = correct > 0;
    }

    public Answer() {
        this.id = 0;
        this.idQuestion = 0;
        this.answer = null;
        this.correct = false;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setIdQuestion(int idQuestion) {
        this.idQuestion = idQuestion;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public int getId() {
        return id;
    }

    public int getIdQuestion() {
        return idQuestion;
    }

    public String getAnswer() {
        return answer;
    }

    public boolean isCorrect() {
        return correct;
    }
}
