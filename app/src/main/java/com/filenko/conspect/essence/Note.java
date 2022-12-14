package com.filenko.conspect.essence;


import java.util.ArrayList;
import java.util.List;

public class Note implements Comparable<Note> {
    private int id;
    private int type; // (1) folder or (2) note
    private int parent;
    private String name;
    private String description;
    private String html;
    private final List<Note> listChild = new ArrayList<>();
    private final List<Question> questionsList = new ArrayList<>();

    public Note(int id, int type, int parent, String name, String description, String html) {
        this.id = id;
        this.type = type;
        this.parent = parent;
        this.name = name;
        this.description = description;
        this.html = html;
    }

    public Note() {
        this.id = 0;
        this.type = 0;
        this.parent = 0;
        this.name = null;
        this.description = null;
        this.html = null;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setParent(int parent) {
        this.parent = parent;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public int getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public int getParent() {
        return parent;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getHtml() {
        return html;
    }

    public void addChild(Note note) {
        note.setParent(this.getId());
        this.listChild.add(note);
    }

    public List<Question> getListQuestion() {
        return this.questionsList;
    }

    public void addChild(Question question) {
        question.setIdNote(this.getId());
        this.questionsList.add(question);
    }

    public List<Note> getListChild() {
        return this.listChild;
    }

    @Override
    public String toString() {
        return "Name = "+getName()+", id = "+getId()+", parent = "+getParent();
    }

    public void clear () {
        this.id = 0;
        this.type = 0;
        this.parent = 0;
        this.name = null;
        this.description = null;
        this.html = null;
    }

    @Override
    public int compareTo(Note o) {
        return this.getName().compareTo(o.getName());
    }
}
