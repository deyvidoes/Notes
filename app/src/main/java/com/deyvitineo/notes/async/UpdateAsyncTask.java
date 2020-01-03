package com.deyvitineo.notes.async;

import android.os.AsyncTask;

import com.deyvitineo.notes.models.Note;
import com.deyvitineo.notes.persistance.NoteDao;

public class UpdateAsyncTask extends AsyncTask<Note, Void, Void> {

    private static final String TAG = "UpdateAsyncTask";

    private NoteDao mNoteDao;
    public UpdateAsyncTask(NoteDao dao) {
        mNoteDao = dao;
    }


    @Override
    protected Void doInBackground(Note... notes) {
        mNoteDao.update(notes);
        return null;
    }
}
