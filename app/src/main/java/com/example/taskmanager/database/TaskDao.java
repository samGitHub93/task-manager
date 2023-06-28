package com.example.taskmanager.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.taskmanager.model.Task;

import java.util.List;

@Dao
public interface TaskDao {
    @Query("SELECT * FROM task")
    List<Task> getAll();

    @Query("SELECT * FROM task WHERE id LIKE (:id)")
    Task getById(long id);

    @Query("SELECT * FROM task WHERE (INSTR(UPPER(title), UPPER((:typing))) > 0) OR (INSTR(UPPER(text), UPPER((:typing))) > 0) OR (INSTR(UPPER(date), UPPER((:typing))) > 0)")
    List<Task> getTasksByTitleOrTextOrDate(String typing);

    @Query("SELECT * FROM task WHERE date LIKE (:date)")
    List<Task> getByDate(String date);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Task> entities);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Task task);

    @Query("DELETE FROM task")
    void deleteAll();

    @Delete
    void delete(Task task);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(Task task);
}
