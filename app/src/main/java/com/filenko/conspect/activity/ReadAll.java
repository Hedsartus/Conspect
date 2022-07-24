package com.filenko.conspect.activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.filenko.conspect.R;
import com.filenko.conspect.db.DataBaseConnection;
import com.filenko.conspect.essence.Note;
import com.filenko.conspect.tree.TreeNode;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerFontProvider;
import com.itextpdf.tool.xml.XMLWorkerHelper;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ReadAll extends AppCompatActivity  {
    private DataBaseConnection db;
    private final List<Note> objects = new ArrayList<>();
    private TextView textViewHtml;
    private TreeNode<Note> treeRoot;
    private StringBuilder stringBuilder = new StringBuilder();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_readall);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        this.db = new DataBaseConnection(this);
        this.textViewHtml = findViewById(R.id.tvReadAll);


        Bundle bundle = getIntent().getExtras();
        if(bundle!= null && bundle.getInt("idnote") > 0) {
            if(loadHtmlContent(bundle.getInt("idnote"))) {
                for (TreeNode<Note> n : treeRoot) {
                    if (n.data.getType() == 1) {
                        this.textViewHtml.append(Html.fromHtml(
                                "<h1 align=\"center\"><font color=\"#008000\">" + n.data.getName() + "</font></h1>"));
                        this.textViewHtml.append(Html.fromHtml(n.data.getDescription() + "<br><br>"));
                        this.textViewHtml.append(Html.fromHtml(n.data.getHtml() + "<br>"));

                        stringBuilder.append("<h1 align=\"center\"><font color=\"#008000\">" + n.data.getName() + "</font></h1>");
                        stringBuilder.append(n.data.getDescription() + "<br>");
                        stringBuilder.append(n.data.getHtml() + "<br>");

                    } else {
                        this.textViewHtml.append(Html.fromHtml("<h3><font color=\"#808000\">" + n.data.getName() + "</font></h3>"));
                        this.textViewHtml.append(Html.fromHtml(n.data.getHtml() + "<br><br>"));
                        stringBuilder.append("<h3><font color=\"#808000\">" + n.data.getName() + "</font></h3>");
                        stringBuilder.append(n.data.getHtml() + "<br>");
                    }
                }
            }
        }

    }

    private boolean loadHtmlContent(int id) {
        this.objects.clear();
        SQLiteDatabase database = this.db.getReadableDatabase();
        treeRoot = new TreeNode<>(new Note(id, 1, 0, "Root", "", ""));

                String sql = "WITH recursive " +
                "  Parrent_Id(n) AS ( " +
                "    VALUES("+id+") " +
                "    UNION " +
                "    SELECT _id FROM NOTES, Parrent_Id WHERE NOTES.parent = Parrent_Id.n) " +
                "SELECT _id, type, parent, name, description, html FROM NOTES " +
                "WHERE NOTES._id IN Parrent_Id AND NOTES.parent != 0 ORDER BY parent, name";

        try (Cursor q = database.rawQuery(sql, null)) {
            while (q.moveToNext()) {
                Comparable<Note> searchCriteria = treeData -> {
                    if (treeData == null)
                        return 1;
                    boolean nodeOk = treeData.getId() == q.getInt(2);
                    return nodeOk ? 0 : 1;
                };

                TreeNode<Note> found = treeRoot.findTreeNode(searchCriteria);

                if(found != null) {
                    found.addChild(new Note(q.getInt(0),
                            q.getInt(1),
                            q.getInt(2),
                            q.getString(3),
                            q.getString(4),
                            q.getString(5)));
                } else {
                    treeRoot.addChild(new Note(q.getInt(0),
                            q.getInt(1),
                            q.getInt(2),
                            q.getString(3),
                            q.getString(4),
                            q.getString(5)));
                }
            }

            return this.treeRoot.children.size()>0;
        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        menu.add(0, 1, 0, "PDF").setShowAsAction(1);
//
//
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if(item.getItemId() == 1) {
//            try {
//                toPdf();
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (DocumentException e) {
//                e.printStackTrace();
//            }
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    private void toPdf() throws IOException, DocumentException {
//        String outFileName =
//                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +
//                        "/000001.pdf";
//
//        // Параметры страницы
//        Document document = new Document(PageSize.A4, 35, 35, 25, 25);
//        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(outFileName));
//        document.open();
//        document.newPage();
//        document.add(new Chunk(""));
//
//        String str = this.textViewHtml.getText().toString();
//
//        XMLWorkerHelper worker = XMLWorkerHelper.getInstance();
//        InputStream is = new ByteArrayInputStream(this.stringBuilder.toString().getBytes("UTF-8"));
//        worker.parseXHtml(writer, document, is, Charset.forName("UTF-8"));
//        document.close();
//    }
}
