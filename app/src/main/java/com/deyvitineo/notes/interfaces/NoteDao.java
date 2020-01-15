package com.deyvitineo.notes.interfaces;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.deyvitineo.notes.entities.Note;

import java.util.List;

@Dao
public interface NoteDao {

    @Insert
    Long insert(Note note);

    @Query("SELECT * FROM notes ORDER BY timestamp DESC")
    LiveData<List<Note>> getAllNotes();

    @Delete
    void delete(Note... note);

    @Update
    void update(Note... note);
}
