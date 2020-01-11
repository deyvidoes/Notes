package com.deyvitineo.notes.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.deyvitineo.notes.entities.Note;
import com.deyvitineo.notes.repositories.NoteRepository;

public class AddEditNoteViewModel extends AndroidViewModel {

    private NoteRepository mNoteRepository;
    private static final String TAG = "AddEditNoteViewModel";

    public AddEditNoteViewModel(@NonNull Application application) {
        super(application);
        mNoteRepository = new NoteRepository(application);
    }

    public Long insert(Note note) {
        Log.d(TAG, "insert: CALLED - VIEW MODEL");
        return mNoteRepository.insert(note);
    }

    public void update(Note note) {
        mNoteRepository.update(note);
    }

}