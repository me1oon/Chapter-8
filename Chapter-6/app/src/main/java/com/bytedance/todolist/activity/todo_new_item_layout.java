package com.bytedance.todolist.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;

import com.bytedance.todolist.R;

import java.util.Calendar;

public class todo_new_item_layout extends AppCompatActivity {

    private Button confirmBtn;
    private EditText contentEditor;
    private String content;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_new_item_layout);

        confirmBtn = findViewById(R.id.confirm_button);
        contentEditor = findViewById(R.id.content_editor);

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                content = contentEditor.getText().toString();
                if(content.isEmpty()){
                    Toast.makeText(todo_new_item_layout.this, "输入不能为空.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent();
                intent.putExtra("Content", content);
                setResult(1, intent);
                finish();
            }
        });
    }
}