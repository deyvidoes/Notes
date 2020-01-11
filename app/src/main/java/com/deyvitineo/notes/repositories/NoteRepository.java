package com.deyvitineo.notes.repositories;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.deyvitineo.notes.database.NoteDatabase;
import com.deyvitineo.notes.interfaces.NoteDao;
import com.deyvitineo.notes.entities.Note;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class NoteRepository {

    private NoteDao mNoteDao;
    private LiveData<List<Note>> mAllNotes;
    private NoteDatabase mNoteDatabase;

    public NoteRepository(Application application) {
        mNoteDatabase = NoteDatabase.getInstance(application);
        mNoteDao = mNoteDatabase.noteDao();
        mAllNotes = mNoteDao.getAllNotes();
    }

    /*These public methods will be accessible by the view model which will not have any idea of how
     *the data is being handled or whatnot (abstraction)
     */
    public Long insert(Note note) {
        try {
            Long id = new InsertNoteAsyncTask(mNoteDao).execute(note).get();
            return id;
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void update(Note note) {
        new UpdateNoteAsyncTask(mNoteDao).execute(note);
    }

    public void delete(Note note) {
        new DeleteNoteAsyncTask(mNoteDao).execute(note);
    }

    public LiveData<List<Note>> getAllNotes() {
        return mAllNotes;
    }

    //Task in charge of inserting notes
    private static class InsertNoteAsyncTask extends AsyncTask<Note, Void, Long>{

        private NoteDao noteDao;

        private InsertNoteAsyncTask(NoteDao noteDao){
            this.noteDao = noteDao;
        }

        @Override
        protected Long doInBackground(Note... notes) {
            Long id = noteDao.insert(notes[0]);
            return id;
        }
    }
    //Task in charge of updating notes
    private static class UpdateNoteAsyncTask extends AsyncTask<Note, Void, Void> {
        private NoteDao noteDao;

        private UpdateNoteAsyncTask(NoteDao noteDao) {
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.update(notes[0]);
            return null;
        }
    }
    //Task in charge of  deleting a single note
    private static class DeleteNoteAsyncTask extends AsyncTask<Note, Void, Void> {
        private NoteDao noteDao;

        private DeleteNoteAsyncTask(NoteDao noteDao) {
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.delete(notes[0]);
            return null;
        }
    }
}


