package com.deyvitineo.notes.async;

import android.os.AsyncTask;
import android.util.Log;

import com.deyvitineo.notes.models.Note;
import com.deyvitineo.notes.persistance.NoteDao;

public class DeleteAsyncTask extends AsyncTask<Note, Void, Void> {

    private static final String TAG = "DeleteAsyncTask";
    private NoteDao mNoteDao;
    public DeleteAsyncTask(NoteDao dao) {
        mNoteDao = dao;
    }

    @Override
    protected Void doInBackground(Note... notes) {
        Log.d(TAG, "doInBackground: Deleted note: " + notes[0].toString());
        mNoteDao.delete(notes);
        return null;
    }
}
