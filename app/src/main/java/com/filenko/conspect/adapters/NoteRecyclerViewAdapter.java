package com.filenko.conspect.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.filenko.conspect.R;
import com.filenko.conspect.activity.EditNode;
import com.filenko.conspect.activity.EditNote;
import com.filenko.conspect.db.DataBaseConnection;
import com.filenko.conspect.essence.Note;

import java.util.ArrayList;
import java.util.List;

public class NoteRecyclerViewAdapter extends RecyclerSwipeAdapter<NoteRecyclerViewAdapter.ViewHolder> {
    private final DataBaseConnection db;
    private final Context ctx;
    private final LayoutInflater lInflater;
    private final ArrayList<Note> objects = new ArrayList<>();
    public OnClickListener onClickListener;
    private Note rootNote;
    public interface OnClickListener {
        void onOpenNoteViewClick(View view, int position);
    }

    public NoteRecyclerViewAdapter(DataBaseConnection db, Context ctx) {
        this.db = db;
        this.ctx = ctx;
        this.lInflater = LayoutInflater.from(ctx);
        this.rootNote = new Note();
        getRootNotesFromDataBase ();
    }

    public Note getRootNote() {
        return this.rootNote;
    }

    public void setRootNote(Note note) {
        this.rootNote = note;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = this.lInflater.inflate(R.layout.item_noteswipe, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Note note = this.objects.get(position);

        if(note.getType() == 1) {
            holder.itemImageViewIcon.setImageResource(R.drawable.directory2);
            if(!note.getHtml().equals("0")) {
                holder.sizeChild.setText(note.getHtml());
            } else {
                holder.sizeChild.setText("");
            }
        } else {
            holder.itemImageViewIcon.setImageResource(R.drawable.file2);
            holder.sizeChild.setText("");
        }

        holder.noteName.setText(note.getName());
        holder.noteDescription.setText(note.getDescription());

        holder.buttonEditSwipe.setOnClickListener(view -> {
            if(note.getType() == 1) {
                Intent intent = new Intent(this.ctx, EditNode.class);
                Bundle b = new Bundle();
                b.putInt("key", note.getId());
                intent.putExtras(b);
                this.ctx.startActivity(intent);
            } else {
                Intent intent = new Intent(this.ctx, EditNote.class);
                Bundle b = new Bundle();
                b.putInt("key", note.getId());
                intent.putExtras(b);
                this.ctx.startActivity(intent);
            }
        });

        holder.layoutView.setOnClickListener(v -> {
            onClickListener.onOpenNoteViewClick(v, position);
        });

        holder.buttonDeleteSwipe.setOnClickListener(v -> {
            AlertDialog diaBox = noteDelete(position);
            diaBox.show();
        });
    }

    public void setOnClickListener(OnClickListener listener) {
        this.onClickListener= listener;
    }

    public void getNodesFromDataBaseByIdPrent (int idParent) {
        if(idParent >= 0) {
            this.objects.clear();

            String sql = "SELECT * FROM NOTES WHERE parent = " + idParent + " ORDER BY type, name;";

            SQLiteDatabase database = this.db.getReadableDatabase();
            try (Cursor query = database.rawQuery(sql, null)) {
                while (query.moveToNext()) {
                    this.objects.add(new Note(query.getInt(0),
                            query.getInt(1),
                            query.getInt(2),
                            query.getString(3),
                            query.getString(4), null));
                }
                this.notifyDataSetChanged();
            } finally {
                setContFilesInItem();
            }
        }
    }

    public void getRootNotesFromDataBase () {
        SQLiteDatabase database = this.db.getReadableDatabase();
        try (Cursor query = database.rawQuery("SELECT * FROM NOTES WHERE parent = 0 ORDER BY type, name;", null)) {
            while (query.moveToNext()) {
                this.objects.add(new Note(query.getInt(0),
                        query.getInt(1),
                        query.getInt(2),
                        query.getString(3),
                        query.getString(4), "0"));
            }
        } finally {
            setContFilesInItem();
        }
    }

    private void setContFilesInItem() {
        Runnable r = () -> {
            for (Note n : this.objects) {
                if(n.getType() == 1) {
                    n.setHtml(getShortSum(loadTree(n.getId())));
                }
            }
        };
        Thread myThread = new Thread(r, "MyThread");
        myThread.start();
    }

    private int loadTree(int id) {
        SQLiteDatabase database = this.db.getReadableDatabase();
        int count;

        String sql = "SELECT _id FROM NOTES WHERE parent = "+ id +";";

        try (Cursor q = database.rawQuery(sql, null)) {
            count = q.getCount();
        }

        return count;
    }

    private int loadTreeCountQuestions(int id) {
        SQLiteDatabase database = this.db.getReadableDatabase();
        int count;

        String sql = "SELECT _id FROM QUESTIONS WHERE idnote = "+ id +";";

        try (Cursor q = database.rawQuery(sql, null)) {
            count = q.getCount();
        }

        return count;
    }

    private String getShortSum (int val) {
        double divide = val/1000;
        if(divide>=1) {
            return divide+"К";
        } else {
            return val+"";
        }
    }

    /** Get Note from database by id */
    public void getNoteFromDataBaseById (int id) {
        if(id > 0) {
            SQLiteDatabase database = this.db.getReadableDatabase();
            try (Cursor query = database.rawQuery("SELECT * FROM NOTES WHERE _id = " + id + ";", null)) {
                while (query.moveToNext()) {
                    this.rootNote.setId(id);
                    this.rootNote.setType(query.getInt(1));
                    this.rootNote.setParent(query.getInt(2));
                    this.rootNote.setName(query.getString(3));
                    this.rootNote.setDescription(query.getString(4));
                }
            }
        }
    }

    public Note getItem(int position) {
        return this.objects.get(position);
    }

    @Override
    public int getItemCount() {
        return this.objects.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipeLayoutNote;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private Note note;
        public final ImageView itemImageViewIcon;
        public final TextView noteName;
        public final TextView noteDescription;
        public final TextView sizeChild;
        public final ImageView buttonDeleteSwipe;
        public final RelativeLayout layoutView;
        public final ImageView buttonEditSwipe;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemImageViewIcon = itemView.findViewById(R.id.item_image_view_icon);
            this.noteName = itemView.findViewById(R.id.noteName);
            this.noteDescription = itemView.findViewById(R.id.noteDescription);
            this.sizeChild = itemView.findViewById(R.id.sizeChild);
            this.layoutView = itemView.findViewById(R.id.layoutView);
            this.buttonDeleteSwipe = itemView.findViewById(R.id.buttonDeleteSwipe);
            this.buttonEditSwipe = itemView.findViewById(R.id.buttonEditSwipe);
        }
    }

    private AlertDialog noteDelete(int position) {
        Note note = (Note) getItem(position);
        String catNote = note.getType() == 1 ? "каталога" : "карточки";
        String dangerString;
        if(note.getType() == 1) {
            int countNoteChild = loadTree(note.getId());
            dangerString = "Каталог содержит "+countNoteChild+" файла, удалить все?";
        } else {
            int countAuestionsChild = loadTreeCountQuestions(note.getId());
            dangerString = "Карточка содержит "+countAuestionsChild+" вопроса, удалить все?";
        }




        final AlertDialog alertDialog = new AlertDialog.Builder(this.ctx)
                //set message, title, and icon
                .setTitle("Удаление "+catNote)
                .setMessage(dangerString)
                .setIcon(R.drawable.delete)
                .setPositiveButton("Да", (dialog, whichButton) -> {
                    int index = note.getId();
                    if(index>0) {
                        if (deleteNote(index)) {
                            this.objects.remove(position);
                            this.notifyDataSetChanged();
                        }
                    }
                    dialog.dismiss();
                }).setNegativeButton("Отмена", (dialog, which) -> dialog.dismiss())
                .create();
        return alertDialog;
    }

    private boolean deleteNote(int id) {
        SQLiteDatabase database = this.db.getWritableDatabase();
        int delCount = database.delete("NOTES", "_id =" + id, null);

        return delCount > 0;
    }

    private boolean deleteQuestion(int id) {
        SQLiteDatabase database = this.db.getWritableDatabase();
        List<Integer> listAnswersId = new ArrayList<>();

        try (Cursor query = database.rawQuery(
                "SELECT * FROM ANSWER WHERE idquestion = " + id + ";", null)) {
            while (query.moveToNext()) {
                listAnswersId.add(query.getInt(0));
            }

        }

        for(Integer val : listAnswersId) {
            if(!deleteAnswer(val)) {
                return false;
            }
        }

        int delCount = database.delete("QUESTIONS", "_id =" + id, null);

        return delCount > 0;
    }

    private boolean deleteAnswer(int id) {
        SQLiteDatabase database = this.db.getWritableDatabase();
        int delCount = database.delete("ANSWER", "_id =" + id, null);

        return delCount > 0;
    }


}
