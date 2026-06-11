package com.example.angi_didau.ui.favorites;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.angi_didau.R;
import com.example.angi_didau.common.constant.AppConstants;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class CustomNoteBottomSheet extends BottomSheetDialogFragment {

    private FavoritesViewModel viewModel;
    private TextInputEditText etName;
    private TextInputEditText etNote;
    private RadioGroup rgType;
    private CardView cvImagePreview;
    private ImageView ivPreview;
    private ImageView ivRemoveImage;
    private MaterialButton btnAddImage;

    private String selectedImagePath = "";

    private ActivityResultLauncher<String> galleryLauncher;
    private ActivityResultLauncher<Void> cameraLauncher;

    public static CustomNoteBottomSheet newInstance() {
        return new CustomNoteBottomSheet();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                selectedImagePath = copyUriToInternalStorage(uri);
                showPreview();
            }
        });

        cameraLauncher = registerForActivityResult(new ActivityResultContracts.TakePicturePreview(), bitmap -> {
            if (bitmap != null) {
                selectedImagePath = saveBitmapToInternalStorage(bitmap);
                showPreview();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_custom_note, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(FavoritesViewModel.class);

        etName = view.findViewById(R.id.etName);
        etNote = view.findViewById(R.id.etNote);
        rgType = view.findViewById(R.id.rgType);
        cvImagePreview = view.findViewById(R.id.cvImagePreview);
        ivPreview = view.findViewById(R.id.ivPreview);
        ivRemoveImage = view.findViewById(R.id.ivRemoveImage);
        btnAddImage = view.findViewById(R.id.btnAddImage);
        MaterialButton btnSave = view.findViewById(R.id.btnSave);

        btnAddImage.setOnClickListener(v -> showImageSourceDialog());

        ivRemoveImage.setOnClickListener(v -> {
            selectedImagePath = "";
            cvImagePreview.setVisibility(View.GONE);
            btnAddImage.setVisibility(View.VISIBLE);
        });

        btnSave.setOnClickListener(v -> saveCustomNote());
    }

    private void showImageSourceDialog() {
        String[] options = {"Chụp ảnh", "Chọn từ thư viện"};
        new AlertDialog.Builder(requireContext())
                .setTitle("Thêm ảnh")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        cameraLauncher.launch(null);
                    } else {
                        galleryLauncher.launch("image/*");
                    }
                })
                .show();
    }

    private void showPreview() {
        if (!selectedImagePath.isEmpty()) {
            cvImagePreview.setVisibility(View.VISIBLE);
            btnAddImage.setVisibility(View.GONE);
            Glide.with(this).load(selectedImagePath).into(ivPreview);
        }
    }

    private String copyUriToInternalStorage(Uri uri) {
        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
            File file = new File(requireContext().getFilesDir(), "custom_note_" + UUID.randomUUID().toString() + ".jpg");
            OutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.close();
            inputStream.close();
            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private String saveBitmapToInternalStorage(Bitmap bitmap) {
        try {
            File file = new File(requireContext().getFilesDir(), "custom_note_" + UUID.randomUUID().toString() + ".jpg");
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private void saveCustomNote() {
        String name = etName.getText() != null ? etName.getText().toString().trim() : "";
        String note = etNote.getText() != null ? etNote.getText().toString().trim() : "";

        if (name.isEmpty()) {
            etName.setError("Vui lòng nhập tên");
            return;
        }

        String type = AppConstants.ENTITY_TYPE_FOOD; // default
        if (rgType.getCheckedRadioButtonId() == R.id.rbLocation) {
            type = AppConstants.ENTITY_TYPE_LOCATION;
        }

        viewModel.addCustomNote(type, name, note, selectedImagePath);
        Toast.makeText(getContext(), "Đã thêm ghi chú cá nhân", Toast.LENGTH_SHORT).show();
        dismiss();
    }
}
