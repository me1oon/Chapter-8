package com.bytedance.todolist.activity;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bytedance.todolist.R;
import com.bytedance.todolist.database.TodoListEntity;
import com.bytedance.todolist.database.TodoOperator;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wangrui.sh
 * @since Jul 11, 2020
 */
public class TodoListAdapter extends RecyclerView.Adapter<TodoListItemHolder> {
    private List<TodoListEntity> mDatas;
    private final TodoOperator mOperator;

    public TodoListAdapter(TodoOperator operator) {
        mDatas = new ArrayList<>();
        this.mOperator = operator;
    }
    @NonNull
    @Override
    public TodoListItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return TodoListItemHolder.create(parent.getContext(), parent, mOperator);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoListItemHolder holder, int position) {
        holder.bind(mDatas.get(position));
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @MainThread
    public void setData(List<TodoListEntity> list) {
        if(list == null) return;
        mDatas = list;
        notifyDataSetChanged();
    }
}
