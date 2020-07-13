package com.bytedance.todolist.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bytedance.todolist.R;
import com.bytedance.todolist.database.TodoListDao;
import com.bytedance.todolist.database.TodoListDatabase;
import com.bytedance.todolist.database.TodoListEntity;
import com.bytedance.todolist.database.TodoOperator;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author wangrui.sh
 * @since Jul 11, 2020
 */
public class TodoListItemHolder extends RecyclerView.ViewHolder {

    private TextView mContent;
    private TextView mTimestamp;
    private CheckBox mCheckBox;
    private ImageButton mImageBtn;
    private final TodoOperator mOperator;

    public TodoListItemHolder(@NonNull View itemView,TodoOperator operator) {
        super(itemView);
        mContent = itemView.findViewById(R.id.tv_content);
        mTimestamp = itemView.findViewById(R.id.tv_timestamp);
        mCheckBox = itemView.findViewById(R.id.tv_checkbox);
        mImageBtn = itemView.findViewById(R.id.tv_delete_btn);
        mOperator = operator;
    }

    public void bind(final TodoListEntity entity) {

        if(entity == null) return;

        mContent.setText(entity.getContent());
        mTimestamp.setText(formatDate(entity.getTime()));

        mCheckBox.setOnCheckedChangeListener(null);
        if(entity.getDone()) {
            mCheckBox.setChecked(true);
            mContent.setTextColor(Color.GRAY);
            mContent.setPaintFlags(mContent.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }else {
            mCheckBox.setChecked(false);
            mContent.setTextColor(Color.BLACK);
            mContent.setPaintFlags(mContent.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
        }

        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                entity.setDone(isChecked);
                mOperator.updateTodo(entity);
            }
        });

        mImageBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                mOperator.deleteTodo(entity);
            }
        });
    }

    private String formatDate(Date date) {
        DateFormat format = SimpleDateFormat.getDateInstance();
        return format.format(date);
    }

    public static TodoListItemHolder create(Context context, ViewGroup root, TodoOperator operator){
        View v = LayoutInflater.from(context).inflate(R.layout.todo_item_layout, root, false);
        return new TodoListItemHolder(v, operator);
    }
}
