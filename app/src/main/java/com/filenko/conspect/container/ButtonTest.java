package com.filenko.conspect.container;

import android.content.Context;
import android.graphics.Color;

import com.filenko.conspect.essence.Answer;

public class ButtonTest extends androidx.appcompat.widget.AppCompatButton {
    private Answer answer;
    private boolean select = false;

    public ButtonTest(Context context, Answer answer) {
        super(context);
        this.answer = answer;
        setText(this.answer.getAnswer());
        setBackgroundColor(Color.rgb(213, 213, 213)); // gray

    }

    public boolean isCorrect () {
        return answer.isCorrect();
    }

    public boolean isSelectAnswer() {
        return this.select;
    }

    public void setSelectedAnswer(boolean b) {
        this.select = b;
    }
}
