package com.deyvitineo.notes.async;

import android.os.AsyncTask;
import android.util.Log;

import com.deyvitineo.notes.models.Note;
import com.deyvitineo.notes.persistance.NoteDao;

public class InsertAsyncTask extends AsyncTask<Note, Void, Void> {

    private static final String TAG = "InsertAsyncTask";
    private NoteDao mNoteDao;
    public InsertAsyncTask(NoteDao dao) {
        mNoteDao = dao;
    }

    @Override
    protected Void doInBackground(Note... notes) {
        Log.d(TAG, "doInBackground: THREAD: " + Thread.currentThread().getName());
        mNoteDao.insertNotes(notes);
        return null;
    }
}
