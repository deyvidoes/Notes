package com.deyvitineo.notes.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.deyvitineo.notes.entities.Note;
import com.deyvitineo.notes.repositories.NoteRepository;

public class AddEditNoteViewModel extends AndroidViewModel {

    private NoteRepository mNoteRepository;

    public AddEditNoteViewModel(@NonNull Application application) {
        super(application);
        mNoteRepository = new NoteRepository(application);
    }

    public void insert(Note note) {
        mNoteRepository.insert(note);
    }


    public void update(Note note) {
        mNoteRepository.update(note);
    }
}