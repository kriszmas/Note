package com.example.note.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.note.model.Note

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note)

    @Update
    suspend fun updateNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)

    @Query("SELECT * FROM NOTES ORDER BY lastModified DESC")
    fun getAllNotes(): LiveData<List<Note>>

    @Query("SELECT * FROM NOTES WHERE noteTitle LIKE :query OR noteDesc LIKE :query ORDER BY CASE WHEN noteTitle LIKE :query THEN 0 ELSE 1 END, INSTR(noteTitle, :query)")
    fun searchNote(query: String?): LiveData<List<Note>>

    @Query("Delete FROM NOTES")
    suspend fun deleteAllNotes()


}