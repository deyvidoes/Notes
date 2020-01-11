package com.deyvitineo.notes.database;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.deyvitineo.notes.entities.Note;
import com.deyvitineo.notes.interfaces.NoteDao;
import com.deyvitineo.notes.util.Utility;

@Database(entities = Note.class, version = 4)
public abstract class NoteDatabase extends RoomDatabase {

    //singleton
    private static NoteDatabase instance;

    public abstract NoteDao noteDao();

    public static synchronized NoteDatabase getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    NoteDatabase.class, "notes_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDBAsyncTask(instance).execute();

        }
    };


    //Populate db once its first created
    private static class PopulateDBAsyncTask extends AsyncTask<Void, Void, Void> {
        private NoteDao noteDao;

        private PopulateDBAsyncTask(NoteDatabase db) {
            this.noteDao = db.noteDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            String timeStamp = Utility.getCurrentTimestamp();
            timeStamp = timeStamp.replace("-", " ");
            noteDao.insert(new Note("Welcome!!", "I hope you enjoy this simple but yet effective notes app!", timeStamp));
            return null;
        }
    }

    //TODO: DONE REFACTORING
}
