package com.deyvitineo.notes.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.deyvitineo.notes.entities.Note;
import com.deyvitineo.notes.repositories.NoteRepository;

import java.util.List;

public class ViewDeleteNoteViewModel extends AndroidViewModel {

    private NoteRepository mNoteRepository;
    private LiveData<List<Note>> mAllNotes;

    public ViewDeleteNoteViewModel(@NonNull Application application) {
        super(application);
        mNoteRepository = new NoteRepository(application);
        mAllNotes = mNoteRepository.getAllNotes();
    }

    public void delete(Note note) {
        mNoteRepository.delete(note);
    }

    public LiveData<List<Note>> getAllNotes() {
        return mAllNotes;
    }
}
