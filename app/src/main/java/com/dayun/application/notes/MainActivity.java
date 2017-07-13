package com.dayun.application.notes;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.dayun.application.db.NotesDB;

public class MainActivity extends ListActivity {

    public static final int REQUEST_CODE_ADD_NOTE = 1;
    public static final int REQUEST_CODE_EDIT_NOTE = 2;


    private SimpleCursorAdapter adapter = null;
    private NotesDB db;
    private SQLiteDatabase dbRead;
    private View.OnClickListener btnAddNote_clickHander = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            startActivityForResult(new Intent(MainActivity.this, EditNoteActivity.class),
                    REQUEST_CODE_ADD_NOTE);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        db = new NotesDB(this);
        dbRead = db.getReadableDatabase();
        adapter = new SimpleCursorAdapter(this, R.layout.notes_list_cell, null,
                new String[]{NotesDB.COLUMN_NAME_NOTE_NAME, NotesDB.COLUMN_NAME_NOTE_DATE},
                new int[]{R.id.tvName, R.id.tvDate});

        setListAdapter(adapter);

        refreshNotesListView();

        findViewById(R.id.btnAddNote).setOnClickListener(btnAddNote_clickHander);
    }

    public void refreshNotesListView() {
        adapter.changeCursor(dbRead.query(NotesDB.TABLE_NAME_NOTES, null, null, null, null, null, null));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Cursor c = adapter.getCursor();
        c.moveToPosition(position);

        Intent i = new Intent(MainActivity.this, EditNoteActivity.class);
        i.putExtra(EditNoteActivity.EXTRA_NOTE_ID, c.getInt(c.getColumnIndex(NotesDB.COLUMN_NAME_ID)));
        i.putExtra(EditNoteActivity.EXTRA_NOTE_NAME, c.getString(c.getColumnIndex(NotesDB.COLUMN_NAME_NOTE_NAME)));
        i.putExtra(EditNoteActivity.EXTRA_NOTE_CONTENT, c.getString(c.getColumnIndex(NotesDB.COLUMN_NAME_NOTE_CONTENT)));
        startActivityForResult(i, REQUEST_CODE_EDIT_NOTE);

        super.onListItemClick(l, v, position, id);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_EDIT_NOTE:
            case REQUEST_CODE_ADD_NOTE:

                if (resultCode == Activity.RESULT_OK) {
                    refreshNotesListView();
                }
        }
    }
}
