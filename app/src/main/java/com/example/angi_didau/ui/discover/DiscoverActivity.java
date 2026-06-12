package com.example.angi_didau.ui.discover;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.angi_didau.adapter.TimelineAdapter;
import com.example.angi_didau.data.model.TimelineItem;
import android.widget.EditText;
import android.app.TimePickerDialog;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.angi_didau.R;
import com.example.angi_didau.ui.home.HomeActivity;
import com.example.angi_didau.ui.profile.ProfileActivity;
import com.example.angi_didau.ui.random.RandomActivity;
import com.example.angi_didau.ui.favorites.FavoritesActivity;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.DocumentSnapshot;
import com.example.angi_didau.common.constant.AppConstants;

public class DiscoverActivity extends AppCompatActivity {

    private LinearLayout llForm;
    private LinearLayout llResult;
    private FrameLayout flLoading;
    
    private TextView tvPeopleCount;
    private int peopleCount = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);
        
        initViews();
        setupBottomNav();
        setupFormInteractions();

        String planId = getIntent().getStringExtra(AppConstants.EXTRA_PLAN_ID);
        if (planId != null && !planId.isEmpty()) {
            loadPlanFromFirebase(planId);
        }
    }

    private void loadPlanFromFirebase(String planId) {
        String uid = com.google.firebase.auth.FirebaseAuth.getInstance().getUid();
        if (uid == null) return;
        
        llForm.setVisibility(View.GONE);
        flLoading.setVisibility(View.VISIBLE);

        FirebaseFirestore.getInstance().collection("Users").document(uid).collection("SavedPlans")
                .document(planId).get().addOnSuccessListener(doc -> {
                    flLoading.setVisibility(View.GONE);
                    if (doc.exists()) {
                        com.example.angi_didau.data.model.SavedPlan plan = doc.toObject(com.example.angi_didau.data.model.SavedPlan.class);
                        if (plan != null) {
                            showPlanDetails(plan);
                        }
                    }
                }).addOnFailureListener(e -> {
                    flLoading.setVisibility(View.GONE);
                    llForm.setVisibility(View.VISIBLE);
                    android.widget.Toast.makeText(this, "Không thể tải kế hoạch", android.widget.Toast.LENGTH_SHORT).show();
                });
    }

    private void showPlanDetails(com.example.angi_didau.data.model.SavedPlan plan) {
        llResult.setVisibility(View.VISIBLE);
        
        TextView tvAiResultTotalCost = findViewById(R.id.tvAiResultTotalCost);
        if (tvAiResultTotalCost != null) tvAiResultTotalCost.setText("Dự kiến: " + plan.getTotalCost());
        
        TextView tvAiResultTitle = findViewById(R.id.tvAiResultTitle);
        if (tvAiResultTitle != null) {
            tvAiResultTitle.setText(plan.getTitle() != null ? plan.getTitle() : "Lịch trình đã lưu");
        }
        
        RecyclerView rvTimeline = findViewById(R.id.rvTimeline);
        if (rvTimeline != null) {
            rvTimeline.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));
            rvTimeline.setAdapter(new TimelineAdapter(plan.getItems()));
        }

        Button btnSavePlan = findViewById(R.id.btnSavePlan);
        if (btnSavePlan != null) {
            btnSavePlan.setText("Đã lưu");
            btnSavePlan.setEnabled(false);
            btnSavePlan.setBackgroundResource(R.drawable.bg_button_disabled);
        }

        Button btnRegenerate = findViewById(R.id.btnRegenerate);
        if (btnRegenerate != null) {
            btnRegenerate.setVisibility(View.GONE);
        }

        Button btnEditPlan = findViewById(R.id.btnEditPlan);
        if (btnEditPlan != null) {
            btnEditPlan.setVisibility(View.GONE);
        }
    }

    private void initViews() {
        llForm = findViewById(R.id.llForm);
        llResult = findViewById(R.id.llResult);
        flLoading = findViewById(R.id.flLoading);
        tvPeopleCount = findViewById(R.id.tvPeopleCount);
    }

    private void setupFormInteractions() {
        ImageView ivMinus = findViewById(R.id.ivMinus);
        ImageView ivPlus = findViewById(R.id.ivPlus);
        
        ivMinus.setOnClickListener(v -> {
            if (peopleCount > 1) {
                peopleCount--;
                tvPeopleCount.setText(String.valueOf(peopleCount));
            }
        });

        ivPlus.setOnClickListener(v -> {
            if (peopleCount < 20) {
                peopleCount++;
                tvPeopleCount.setText(String.valueOf(peopleCount));
            }
        });

        TextView tvStartTime = findViewById(R.id.tvStartTime);
        if (tvStartTime != null) {
            tvStartTime.setOnClickListener(v -> {
                new TimePickerDialog(this, (view, hourOfDay, minute) -> {
                    String amPm = hourOfDay >= 12 ? "PM" : "AM";
                    int hour = hourOfDay % 12;
                    if (hour == 0) hour = 12;
                    String time = String.format("%02d:%02d %s", hour, minute, amPm);
                    tvStartTime.setText(time);
                }, 18, 0, false).show();
            });
        }

        TextView tvEndTime = findViewById(R.id.tvEndTime);
        if (tvEndTime != null) {
            tvEndTime.setOnClickListener(v -> {
                new TimePickerDialog(this, (view, hourOfDay, minute) -> {
                    String amPm = hourOfDay >= 12 ? "PM" : "AM";
                    int hour = hourOfDay % 12;
                    if (hour == 0) hour = 12;
                    String time = String.format("%02d:%02d %s", hour, minute, amPm);
                    tvEndTime.setText(time);
                }, 22, 0, false).show();
            });
        }

        // Budget input handled via EditText
EditText etBudget = findViewById(R.id.etBudget);
String budgetInput = etBudget != null ? etBudget.getText().toString().trim() : "";
String budgetStr = budgetInput.isEmpty() ? "Không xác định" : budgetInput + " VNĐ";

        Button btnSuggest = findViewById(R.id.btnSuggest);
        btnSuggest.setOnClickListener(v -> simulateAIGeneration());
        
        Button btnRegenerate = findViewById(R.id.btnRegenerate);
        if (btnRegenerate != null) {
            btnRegenerate.setOnClickListener(v -> {
                llResult.setVisibility(View.GONE);
                llForm.setVisibility(View.VISIBLE);
            });
        }
        
        Button btnEditPlan = findViewById(R.id.btnEditPlan);
        if (btnEditPlan != null) {
            btnEditPlan.setOnClickListener(v -> {
                llResult.setVisibility(View.GONE);
                llForm.setVisibility(View.VISIBLE);
            });
        }

        // Action Buttons in Result
        View.OnClickListener saveListener = v -> {
            RecyclerView rvTimeline = findViewById(R.id.rvTimeline);
            if (rvTimeline != null && rvTimeline.getAdapter() != null) {
                TimelineAdapter adapter = (TimelineAdapter) rvTimeline.getAdapter();
                java.util.List<TimelineItem> items = adapter.getItems();
                
                String uid = com.google.firebase.auth.FirebaseAuth.getInstance().getUid();
                if (uid == null) {
                    android.widget.Toast.makeText(this, "Vui lòng đăng nhập để lưu kế hoạch", android.widget.Toast.LENGTH_SHORT).show();
                    return;
                }
                
                TextView tvTitle = findViewById(R.id.tvAiResultTitle);
                TextView tvCost = findViewById(R.id.tvAiResultTotalCost);
                String title = tvTitle != null ? tvTitle.getText().toString() : "Kế hoạch AI";
                String cost = tvCost != null ? tvCost.getText().toString() : "Chưa rõ";
                
                com.example.angi_didau.data.model.SavedPlan plan = new com.example.angi_didau.data.model.SavedPlan(
                    null, uid, title, cost, items, System.currentTimeMillis()
                );
                
                com.google.firebase.firestore.FirebaseFirestore.getInstance()
                    .collection("Users")
                    .document(uid)
                    .collection("SavedPlans")
                    .add(plan)
                    .addOnSuccessListener(docRef -> {
                        android.widget.Toast.makeText(this, "Đã lưu kế hoạch vào bộ sưu tập", android.widget.Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        android.widget.Toast.makeText(this, "Lỗi khi lưu kế hoạch", android.widget.Toast.LENGTH_SHORT).show();
                    });
            }
        };

        View btnSavePlan = findViewById(R.id.btnSavePlan);
        if (btnSavePlan != null) btnSavePlan.setOnClickListener(saveListener);
        
        View btnSavePlanFooter = findViewById(R.id.btnSavePlanFooter);
        if (btnSavePlanFooter != null) btnSavePlanFooter.setOnClickListener(saveListener);

        View btnSharePlanFooter = findViewById(R.id.btnSharePlanFooter);
        if (btnSharePlanFooter != null) {
            btnSharePlanFooter.setOnClickListener(v -> {
                android.content.Intent shareIntent = new android.content.Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Kế hoạch AI Gợi ý");
                
                TextView tvTitle = findViewById(R.id.tvAiResultTitle);
                String title = tvTitle != null ? tvTitle.getText().toString() : "Kế hoạch thú vị";
                
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Mình vừa tạo một hành trình bằng AI trên AnGiDiDau:\n" + title);
                startActivity(android.content.Intent.createChooser(shareIntent, "Chia sẻ kế hoạch qua"));
            });
        }
    }

    private void simulateAIGeneration() {
        // Show loading
        flLoading.setVisibility(View.VISIBLE);
        
        // Get user input values
        TextView tvStartTime = findViewById(R.id.tvStartTime);
        TextView tvEndTime = findViewById(R.id.tvEndTime);

        // Wait, etPrompt is an EditText in llForm without ID but has hint "Ví dụ: Hẹn hò lãng mạn...". Let's try to find it.
        EditText etUserInput = null;
        for (int i = 0; i < llForm.getChildCount(); i++) {
            View child = llForm.getChildAt(i);
            if (child instanceof androidx.cardview.widget.CardView) {
                View innerLayout = ((androidx.cardview.widget.CardView) child).getChildAt(0);
                if (innerLayout instanceof LinearLayout) {
                    View possibleEt = ((LinearLayout) innerLayout).getChildAt(0);
                    if (possibleEt instanceof EditText) {
                        etUserInput = (EditText) possibleEt;
                    }
                }
            }
        }
        
        String startTime = tvStartTime != null ? tvStartTime.getText().toString() : "06:00 PM";
        String endTime = tvEndTime != null ? tvEndTime.getText().toString() : "10:00 PM";
        // Budget input handled via EditText
        EditText etBudget = findViewById(R.id.etBudget);
        String budgetInput = etBudget != null ? etBudget.getText().toString().trim() : "";
        String budgetStr = budgetInput.isEmpty() ? "Không xác định" : budgetInput + " VNĐ";
        String userThinking = (etUserInput != null && !etUserInput.getText().toString().isEmpty()) 
                                ? etUserInput.getText().toString() : "Không có yêu cầu đặc biệt";
        
        // Fetch data from Firestore before calling AI
        // Lấy tất cả dữ liệu theo yêu cầu của user, nhưng bỏ gửi Image URL cho AI để tối ưu hóa
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Task<QuerySnapshot> foodsTask = db.collection(AppConstants.COLLECTION_FOODS).get();
        Task<QuerySnapshot> locationsTask = db.collection(AppConstants.COLLECTION_LOCATIONS).get();

        Tasks.whenAllSuccess(foodsTask, locationsTask).addOnSuccessListener(results -> {
            StringBuilder dbContext = new StringBuilder();
            dbContext.append("\n\n=== DANH SÁCH MÓN ĂN TRONG HỆ THỐNG ===\n");
            QuerySnapshot foodsResult = (QuerySnapshot) results.get(0);
            for (DocumentSnapshot doc : foodsResult.getDocuments()) {
                String name = doc.getString("name");
                Double price = doc.getDouble("price");
                dbContext.append(String.format("- ID: %s | Tên: %s | Giá: %s\n", doc.getId(), name != null ? name : "N/A", price != null ? price + "đ" : "N/A"));
            }

            dbContext.append("\n=== DANH SÁCH ĐỊA ĐIỂM TRONG HỆ THỐNG ===\n");
            QuerySnapshot locsResult = (QuerySnapshot) results.get(1);
            for (DocumentSnapshot doc : locsResult.getDocuments()) {
                String name = doc.getString("name");
                Double price = doc.getDouble("price");
                dbContext.append(String.format("- ID: %s | Tên: %s | Giá: %s\n", doc.getId(), name != null ? name : "N/A", price != null ? price + "đ" : "N/A"));
            }

            String systemInstruction = "Bạn là một trợ lý ảo chuyên lên kế hoạch đi chơi (Ăn Gì, Đi Đâu) cho người dùng tại Việt Nam. " +
                    "Dựa vào Ngân sách, Thời gian, Số lượng người và Ý thích, hãy tạo 1 lộ trình gồm TUẨN TỰ các địa điểm phù hợp với khoảng thời gian từ " + startTime + " đến " + endTime + ". " +
                    "Số địa điểm trong lộ trình: TÍNH TOÁN DỰA TRÊN KHUNG GIờ (TB 1-2 giờ/địa điểm), đảm bảo LUÔN CÓ Ít nhất 4 mục. " +
                    "QUẢN TRọNG: CHỈ ĐƯỢC CHỌ CÁC ĐỊ ĐIỈM VÀ MÓN ĂN TỪ DANH SÁCH BÊN DƯỚI. KHÔNG TỰ BỊA RA TÊN KHÁC.\n" +
                    dbContext.toString() +
                    "\nTrả về kết quả DƯỚI DẠNG MẢNG JSON HỢP LỆ (chỉ JSON, không chứa markdown ```json hay giải thích thêm). " +
                    "Cấu trúc mỗi object trong mảng: " +
                    "{\"timeCategory\": \"Giờ bắt đầu chính xác (VD: 09:00 — CAFE)\", " +
                    "\"title\": \"Tên địa điểm\", " +
                    "\"price\": \"Giá tham khảo\", " +
                    "\"description\": \"Mô tả lý do chọn/ trải nghiệm\", " +
                    "\"location\": \"Địa chỉ ngắn gọn\", " +
                    "\"type\": \"Food hoặc Location\", " +
                    "\"entityId\": \"TRẢ VỀ CHÍNH XÁC ID của món ăn/địa điểm bạn chọn từ danh sách trên\", " +
                    "\"entityType\": \"BẮT BUỘC ghi 'Food' nếu lấy từ danh sách MÓN ĂN, hoặc 'Location' nếu lấy từ danh sách ĐỊA ĐIỂM\"}";
                    
            String prompt = String.format("Tôi cần 1 lịch trình đi chơi tối nay.\n- Số người: %d\n- Thời gian: %s đến %s\n- Ngân sách tổng: %s\n- Ý thích: %s\n\nHãy tạo 1 lịch trình hợp lý BẮT BUỘC DÙNG CÁC ĐỊA ĐIỂM VÀ MÓN ĂN TRONG HỆ THỐNG BÊN DƯỚI.", 
                    peopleCount, startTime, endTime, budgetStr, userThinking);

            com.example.angi_didau.data.remote.GeminiService.generateItinerary(systemInstruction, prompt, new com.example.angi_didau.data.remote.GeminiService.GeminiCallback() {
                @Override
                public void onSuccess(String jsonResult) {
                    flLoading.setVisibility(View.GONE);
                    llForm.setVisibility(View.GONE);
                    llResult.setVisibility(View.VISIBLE);
                    
                    TextView tvAiResultTitle = findViewById(R.id.tvAiResultTitle);
                    TextView tvAiResultTotalCost = findViewById(R.id.tvAiResultTotalCost);
                    
                    if (tvAiResultTitle != null) {
                        String titleText = String.format("Chuyến đi %d người, từ %s đến %s", peopleCount, startTime, endTime);
                        if (userThinking != null && !userThinking.trim().isEmpty()) {
                            titleText += " - " + userThinking;
                        }
                        tvAiResultTitle.setText(titleText);
                    }
                    if (tvAiResultTotalCost != null) {
                        tvAiResultTotalCost.setText(budgetStr);
                    }
                    
                    parseAndDisplayTimeline(jsonResult, foodsResult, locsResult);
                }

                @Override
                public void onError(String errorMessage) {
                    flLoading.setVisibility(View.GONE);
                    android.widget.Toast.makeText(DiscoverActivity.this, "Lỗi tạo lịch trình: " + errorMessage + ". Vui lòng thử lại.", android.widget.Toast.LENGTH_LONG).show();
                    // Just show form again, no fake data
                    llForm.setVisibility(View.VISIBLE);
                    llResult.setVisibility(View.GONE);
                }
            });
        }).addOnFailureListener(e -> {
            flLoading.setVisibility(View.GONE);
            android.widget.Toast.makeText(this, "Không thể tải dữ liệu từ server. Vui lòng kiểm tra quyền Firestore.", android.widget.Toast.LENGTH_LONG).show();
        });
    }

    private void parseAndDisplayTimeline(String json, com.google.firebase.firestore.QuerySnapshot foodsResult, com.google.firebase.firestore.QuerySnapshot locsResult) {
        RecyclerView rvTimeline = findViewById(R.id.rvTimeline);
        if (rvTimeline == null) return;
        
        List<TimelineItem> items = new ArrayList<>();
        try {
            org.json.JSONArray jsonArray = new org.json.JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                org.json.JSONObject obj = jsonArray.getJSONObject(i);
                String timeCategory = obj.optString("timeCategory", "N/A");
                String title = obj.optString("title", "Địa điểm");
                String price = obj.optString("price", "Tùy chọn");
                String description = obj.optString("description", "");
                String location = obj.optString("location", "Việt Nam");
                String type = obj.optString("type", "Food");
                String entityId = obj.optString("entityId", "");
                String entityType = obj.optString("entityType", type);
                
                String imageUrl = "";
                if (entityType.equalsIgnoreCase("Food") && foodsResult != null) {
                    for (com.google.firebase.firestore.DocumentSnapshot doc : foodsResult.getDocuments()) {
                        if (doc.getId().equals(entityId)) {
                            imageUrl = doc.getString("imageUrl");
                            break;
                        }
                    }
                } else if (locsResult != null) {
                    for (com.google.firebase.firestore.DocumentSnapshot doc : locsResult.getDocuments()) {
                        if (doc.getId().equals(entityId)) {
                            imageUrl = doc.getString("imageUrl");
                            break;
                        }
                    }
                }
                
                int iconRes = type.equalsIgnoreCase("Food") ? R.drawable.ic_restaurant_menu : R.drawable.ic_location_on;
                
                items.add(new TimelineItem(timeCategory, title, price, description, location, imageUrl != null ? imageUrl : "", iconRes, entityId, entityType));
            }
        } catch (Exception e) {
            android.util.Log.e("DiscoverActivity", "JSON Parsing error", e);
            android.widget.Toast.makeText(this, "Không thể đọc dữ liệu AI", android.widget.Toast.LENGTH_SHORT).show();
            setupFakeTimelineData();
            return;
        }

        TimelineAdapter adapter = new TimelineAdapter(items);
        rvTimeline.setAdapter(adapter);
    }


    private void setupFakeTimelineData() {
        RecyclerView rvTimeline = findViewById(R.id.rvTimeline);
        if (rvTimeline == null) return;
        
        List<TimelineItem> fakeData = new ArrayList<>();
        fakeData.add(new TimelineItem(
                "08:00 — CÀ PHÊ SÁNG",
                "The Workshop Coffee",
                "60.000đ",
                "Không gian yên tĩnh, view ban công cực chill, thích hợp để bắt đầu ngày mới.",
                "Quận 1, TP. HCM",
                "https://picsum.photos/seed/cafe1/600/300",
                R.drawable.ic_location_on,
                "", ""
        ));
        
        fakeData.add(new TimelineItem(
                "10:00 — KHÁM PHÁ",
                "Nhà thờ Đức Bà",
                "Miễn phí",
                "Điểm check-in nổi tiếng giữa lòng thành phố, kiến trúc Pháp đẹp mắt.",
                "Quận 1, TP. HCM",
                "https://picsum.photos/seed/church1/600/300",
                R.drawable.ic_location_on,
                "", ""
        ));
        
        fakeData.add(new TimelineItem(
                "12:00 — TRƯA",
                "Bánh Mì Huỳnh Hoa",
                "80.000đ",
                "Bánh mì ngon nhất Sài Gòn, combo đặc biệt cho 2 người siêu ngon.",
                "Cách Mạng Tháng 8",
                "https://picsum.photos/seed/banhmi1/600/300",
                R.drawable.ic_restaurant_menu,
                "", ""
        ));
        
        fakeData.add(new TimelineItem(
                "14:30 — CHIỀU",
                "Bến Bạch Đằng Waterbus Walk",
                "60.000đ",
                "Đi tàu thủy ngắm sông Sài Gòn, gió mát và phong cảnh tuyệt vời.",
                "Bến Bạch Đằng",
                "https://picsum.photos/seed/waterfront1/600/300",
                R.drawable.ic_location_on,
                "", ""
        ));

        TimelineAdapter adapter = new TimelineAdapter(fakeData);
        rvTimeline.setAdapter(adapter);
    }


    private void setupBottomNav() {
        // Highlight Discover Tab
        ((TextView) findViewById(R.id.tvNavDiscover)).setTextColor(getResources().getColor(R.color.primary_container));
        ((ImageView) findViewById(R.id.ivNavDiscover)).setColorFilter(getResources().getColor(R.color.primary_container));

        // Dim others
        ((TextView) findViewById(R.id.tvNavHome)).setTextColor(getResources().getColor(R.color.secondary));
        ((ImageView) findViewById(R.id.ivNavHome)).setColorFilter(getResources().getColor(R.color.secondary));
        ((TextView) findViewById(R.id.tvNavProfile)).setTextColor(getResources().getColor(R.color.secondary));
        ((ImageView) findViewById(R.id.ivNavProfile)).setColorFilter(getResources().getColor(R.color.secondary));
        ((TextView) findViewById(R.id.tvNavRandom)).setTextColor(getResources().getColor(R.color.secondary));

        // Clicks
        findViewById(R.id.navHome).setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
            overridePendingTransition(0, 0);
        });
        
        findViewById(R.id.navRandom).setOnClickListener(v -> {
            startActivity(new Intent(this, RandomActivity.class));
            finish();
            overridePendingTransition(0, 0);
        });
        
        findViewById(R.id.navFavorites).setOnClickListener(v -> {
            if (com.example.angi_didau.common.util.SessionHelper.checkGuestAndRequireLogin(this)) return;
            startActivity(new Intent(this, com.example.angi_didau.ui.favorites.FavoritesActivity.class));
            finish();
            overridePendingTransition(0, 0);
        });

        findViewById(R.id.navProfile).setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
            finish();
            overridePendingTransition(0, 0);
        });
    }
}
