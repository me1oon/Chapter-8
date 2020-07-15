package com.example.chapter8;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class permission extends AppCompatActivity {

    private Button btnCheck,btnJump;
    private static final int REQUEST_VIDEO_CAPTURE = 888;
    private final static String[] permissions = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};

    //检查权限是否全部获取
    public static boolean isPermissionsReady(Activity activity, String[] permissions) {
        if (permissions == null || android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(activity, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }

    //申请所有使用到的权限
    public static void reuqestPermissions(Activity activity, String[] permissions, int requestCode) {
        if (permissions == null || android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }
        List<String> mPermissionList = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(activity, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(permissions[i]);//添加还未授予的权限
            }
        }
        if (mPermissionList.size() > 0) {//有权限没有通过，需要申请
            ActivityCompat.requestPermissions(activity, permissions, requestCode);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);
        btnCheck = findViewById(R.id.btn_check);
        btnJump = findViewById(R.id.btn_jump);

        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isPermissionsReady(permission.this,permissions)) {
                    reuqestPermissions(permission.this, permissions, REQUEST_VIDEO_CAPTURE);
                }
                Toast.makeText(permission.this,"已经获得全部授权", Toast.LENGTH_SHORT).show();
            }
        });

        btnJump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(permission.this,MainActivity.class));
            }
        });
    }

}