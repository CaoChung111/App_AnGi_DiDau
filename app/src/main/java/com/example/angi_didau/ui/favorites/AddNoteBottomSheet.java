package com.example.angi_didau.ui.favorites;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.angi_didau.R;
import com.example.angi_didau.common.constant.AppConstants;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Bottom sheet for adding a custom note/favorite entry.
 * <p>
 * Uses {@link FavoritesViewModel} (Activity-scoped) to save data to Firestore.
 * Validates that the user is logged in and that the title field is not empty
 * before attempting to save.
 */
public class AddNoteBottomSheet extends BottomSheetDialogFragment {

    public static AddNoteBottomSheet newInstance() {
        return new AddNoteBottomSheet();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_bottom_sheet_note, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get the Activity-scoped ViewModel
        FavoritesViewModel viewModel =
                new ViewModelProvider(requireActivity()).get(FavoritesViewModel.class);

        EditText etTitle   = view.findViewById(R.id.etName);
        EditText etContent = view.findViewById(R.id.etNote);
        Button   btnCancel = view.findViewById(R.id.btnCancel);
        Button   btnSave   = view.findViewById(R.id.btnSave);

        btnCancel.setOnClickListener(v -> dismiss());

        btnSave.setOnClickListener(v -> {
            String title   = etTitle   != null && etTitle.getText()   != null ? etTitle.getText().toString().trim()   : "";
            String content = etContent != null && etContent.getText() != null ? etContent.getText().toString().trim() : "";

            if (title.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập tên địa điểm/món ăn", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) {
                Toast.makeText(getContext(), "Bạn cần đăng nhập để lưu ghi chú", Toast.LENGTH_SHORT).show();
                return;
            }

            // Use a generated ID based on title since this is a user-custom entry (not tied to Firestore doc)
            String entityId = "custom_" + System.currentTimeMillis();

            viewModel.addFavoriteWithNote(
                    entityId,
                    AppConstants.ENTITY_TYPE_FOOD,   // default type for custom notes
                    title,
                    "",                              // custom notes don't have an image
                    content
            );

            // Observe result and dismiss
            viewModel.getSaveNoteResult().observe(getViewLifecycleOwner(), success -> {
                if (Boolean.TRUE.equals(success)) {
                    dismiss();
                } else if (Boolean.FALSE.equals(success)) {
                    Toast.makeText(getContext(), "Lưu thất bại, thử lại sau", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
