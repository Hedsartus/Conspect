package com.filenko.conspect.adapters;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.filenko.conspect.R;
import com.filenko.conspect.db.DataBaseConnection;
import com.filenko.conspect.essence.Note;

import java.util.ArrayList;

public class NodeAdapter extends BaseAdapter {
    private DataBaseConnection db;
    private Context ctx;
    private LayoutInflater lInflater;
    private ListView listView;
    private ArrayList<Note> objects = new ArrayList<>();

    public NodeAdapter(DataBaseConnection db, Context ctx, ListView listView) {
        this.db = db;
        this.ctx = ctx;
        lInflater = (LayoutInflater) this.ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.listView = listView;
        getNodesFromDataBase ();
    }

    @Override
    public int getCount() {
        return this.objects.size();
    }

    @Override
    public Object getItem(int position) {
        return this.objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        Note n = (Note) getItem(position);
        return n.getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.item_note, parent, false);
        }

        Note n = (Note) getItem(position);

        if(n.getType() == 1) {
            ((ImageView) view.findViewById(R.id.item_image_view_icon)).setImageResource(R.drawable.directory2);
        } else {
            ((ImageView) view.findViewById(R.id.item_image_view_icon)).setImageResource(R.drawable.file2);
        }

        ((TextView) view.findViewById(R.id.noteName)).setText(n.getName());
        ((TextView) view.findViewById(R.id.noteDescription)).setText(n.getDescription());

        return view;
    }

    public int getObjectId (int position) {
        Note note = (Note) getItem(position);
        return note.getId();
    }

    public void getNodesFromDataBaseById (int id) {
        if(id >= 0) {
            this.objects.clear();

            SQLiteDatabase database = this.db.getReadableDatabase();
            try (Cursor query = database.rawQuery("SELECT * FROM NOTES WHERE parent = " + id + ";", null)) {
                while (query.moveToNext()) {
                    this.objects.add(new Note(query.getInt(0),
                            query.getInt(1),
                            query.getInt(2),
                            query.getString(3),
                            query.getString(4), null));
                }

                this.notifyDataSetChanged();
            } finally {
                //this.notifyDataSetChanged();
            }
        }
    }

    public void getNodesFromDataBase () {
        SQLiteDatabase database = this.db.getReadableDatabase();
        try (Cursor query = database.rawQuery("SELECT * FROM NOTES WHERE parent = 0;", null)) {
            while (query.moveToNext()) {
                this.objects.add(new Note(query.getInt(0),
                        query.getInt(1),
                        query.getInt(2),
                        query.getString(3),
                        query.getString(4), null));
            }
        }
    }
}
