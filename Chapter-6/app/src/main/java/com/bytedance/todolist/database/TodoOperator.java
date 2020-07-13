package com.bytedance.todolist.database;

public interface TodoOperator {
    void deleteTodo(TodoListEntity note);

    void updateTodo(TodoListEntity note);
}
