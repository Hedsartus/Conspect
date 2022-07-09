package com.filenko.conspect.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.filenko.conspect.R;
import com.filenko.conspect.activity.EditNode;
import com.filenko.conspect.db.DataBaseConnection;
import com.filenko.conspect.essence.Note;

import java.util.ArrayList;

public class NoteRecyclerViewAdapter extends RecyclerSwipeAdapter<NoteRecyclerViewAdapter.ViewHolder> {
    private final DataBaseConnection db;
    private final Context ctx;
    private final LayoutInflater lInflater;
    private final ArrayList<Note> objects = new ArrayList<>();
    public OnClickListener onClickListener;
    public interface OnClickListener {
        void onOpenNoteViewClick(View view, int position);
    }

    public NoteRecyclerViewAdapter(DataBaseConnection db, Context ctx) {
        this.db = db;
        this.ctx = ctx;
        this.lInflater = LayoutInflater.from(ctx);
        getRootNotesFromDataBase ();
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
            Intent intent = new Intent(this.ctx, EditNode.class);
            Bundle b = new Bundle();
            b.putInt("key", note.getId());
            intent.putExtras(b);
            this.ctx.startActivity(intent);
        });

        holder.layoutView.setOnClickListener(v -> {
            onClickListener.onOpenNoteViewClick(v, position);
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
                    n.setHtml(loadTree(n.getId()));
                }
            }
        };
        Thread myThread = new Thread(r, "MyThread");
        myThread.start();
    }

    private String loadTree(int id) {
        SQLiteDatabase database = this.db.getReadableDatabase();
        int count;

        String sql = "SELECT _id FROM NOTES WHERE parent = "+ id +";";

        try (Cursor q = database.rawQuery(sql, null)) {
            count = q.getCount();
        }

        return getShortSum (count);
    }

    private String getShortSum (int val) {
        double divide = val/1000;
        if(divide>=1) {
            return divide+"К";
        } else {
            return val+"";
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
        private ImageView buttonEditSwipe;

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
}
