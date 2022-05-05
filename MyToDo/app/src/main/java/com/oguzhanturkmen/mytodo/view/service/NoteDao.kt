package com.oguzhanturkmen.mytodo.view.service

import androidx.lifecycle.LiveData
import androidx.room.*
import com.oguzhanturkmen.mytodo.view.model.Note

@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note)

    @Update
    suspend fun updateNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)
//ORDER BY anahtar sözcüğü, verileri artan ya da azalan düzende sıralamak için kullanılır.
    // DESC (descending) parametresi ile büyükten küçüğe göre sıralar.
    @Query("SELECT * FROM NOTES ORDER BY id DESC")
    fun getAllNotes(): LiveData<List<Note>>

    @Query("SELECT * FROM NOTES WHERE noteTitle LIKE :query OR noteBody LIKE:query")
    fun searchNote(query: String?): LiveData<List<Note>>
}