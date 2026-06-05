package com.example.angi_didau.activities.auth;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.angi_didau.R;

public class RegisterActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Ánh xạ chữ "Đăng nhập"
        TextView tvGoToLogin = findViewById(R.id.tvGoToLogin);

        // Sự kiện bấm vào sẽ đóng màn hình Đăng ký, quay lại Đăng nhập
        tvGoToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Đóng Activity hiện tại
            }
        });
    }
}