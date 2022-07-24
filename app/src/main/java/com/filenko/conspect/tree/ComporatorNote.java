package com.filenko.conspect.tree;

import com.filenko.conspect.essence.Note;

import java.util.Comparator;

public class ComporatorNote implements Comparator<Note> {
    @Override
    public int compare(Note o1, Note o2) {
        if(o1.getParent()> o2.getParent()) {
            return 1;
        } else if(o1.getParent() < o2.getParent()) {
            return -1;
        } else if(o1.getType()> o2.getType()) {
            return 1;
        } else if(o1.getType() < o2.getType()) {
            return -1;
        } else if(o1.getId() > o2.getId()) {
            return 1;
        } else if (o1.getId() < o2.getId()) {
            return -1;
        }

        return o1.compareTo(o2);
    }
}
