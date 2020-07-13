package com.bytedance.todolist.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * @author wangrui.sh
 * @since Jul 11, 2020
 */
@Dao
public interface TodoListDao {
    @Query("SELECT * FROM todo")
    List<TodoListEntity> loadAll();

    @Insert
    long addTodo(TodoListEntity entity);

    @Delete
    void deleteTodo(TodoListEntity entity);

    @Update
    void updateTodo(TodoListEntity Entity);

    @Query("DELETE FROM todo")
    void deleteAll();
}
