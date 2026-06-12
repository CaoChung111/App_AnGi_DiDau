package com.example.angi_didau.common.util;

import android.app.Activity;
import android.content.Intent;
import androidx.appcompat.app.AlertDialog;
import com.example.angi_didau.ui.auth.LoginActivity;

public class SessionHelper {

    /**
     * Kiểm tra xem người dùng có phải là khách không.
     * Nếu là khách, hiển thị thông báo yêu cầu đăng nhập.
     * @return true nếu là khách (đã show dialog), false nếu đã đăng nhập.
     */
    public static boolean checkGuestAndRequireLogin(Activity activity) {
        SessionManager sessionManager = new SessionManager(activity);
        if (sessionManager.isGuestMode()) {
            new AlertDialog.Builder(activity)
                    .setTitle("Yêu cầu Đăng nhập")
                    .setMessage("Bạn cần đăng nhập để sử dụng tính năng này. Đăng nhập ngay?")
                    .setPositiveButton("Đăng nhập", (dialog, which) -> {
                        sessionManager.clearSession(); // Thoát khỏi guest mode
                        Intent intent = new Intent(activity, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        activity.startActivity(intent);
                        activity.finish();
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
            return true;
        }
        return false;
    }
}
