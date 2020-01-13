package com.deyvitineo.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.deyvitineo.notes.adapters.NotesRecyclerAdapter;
import com.deyvitineo.notes.repositories.NoteRepository;
import com.deyvitineo.notes.util.VerticalSpacingItemDecorator;
import com.deyvitineo.notes.entities.Note;
import com.deyvitineo.notes.viewmodels.ViewDeleteNoteViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

//main activity
public class NotesListActivity extends AppCompatActivity {

    private static final String TAG = "NotesListActivity";

    // UI components
    private RecyclerView mRecyclerView;
    private FloatingActionButton mFloatingActionButton;

    //vars
    private NotesRecyclerAdapter mNotesRecyclerAdapter;
    private ViewDeleteNoteViewModel mViewDeleteNoteViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        Log.d(TAG, "onCreate: Activity started");
        initWidgets();
        initRecyclerView();
        setUpListeners();


        mViewDeleteNoteViewModel.getAllNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                mNotesRecyclerAdapter.submitList(notes);
            }
        });
        setSupportActionBar((Toolbar) findViewById(R.id.notes_toolbar));
    }

    private void initWidgets() {
        mFloatingActionButton = findViewById(R.id.fab_add_note);
        mViewDeleteNoteViewModel = ViewModelProviders.of(this).get(ViewDeleteNoteViewModel.class);

    }

    private void initRecyclerView() {
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);

        mNotesRecyclerAdapter = new NotesRecyclerAdapter();
        mRecyclerView.setAdapter(mNotesRecyclerAdapter);

        mNotesRecyclerAdapter.setOnItemClickListener(new NotesRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Note note) {
                Intent intent = new Intent(NotesListActivity.this, NoteActivity.class);
                intent.putExtra(NoteActivity.EXTRA_TITLE, note.getTitle());
                intent.putExtra(NoteActivity.EXTRA_CONTENT, note.getContent());
                intent.putExtra(NoteActivity.EXTRA_ID, note.getId());
                startActivity(intent);
            }
        });
        Log.d(TAG, "initRecyclerView: recycler view and adapter initialized");
    }

    public void setUpListeners() {

        //Add new note listener
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NotesListActivity.this, NoteActivity.class);
                startActivity(intent);
            }
        });

        //Implements Touch gesture to delete a note on a swipe left
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                mViewDeleteNoteViewModel.delete(mNotesRecyclerAdapter.getNoteAt(viewHolder.getAdapterPosition()));
                Log.d(TAG, "onSwiped: Note deleted");
                Toast.makeText(NotesListActivity.this, "Note Deleted", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(mRecyclerView);
        Log.d(TAG, "setUpListeners: Listeners initialized");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.settings) {
            Intent intent = new Intent(this, PreferencesContainerActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
