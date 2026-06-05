package com.example.angi_didau.ui.favorites;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.angi_didau.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class AddNoteBottomSheet extends BottomSheetDialogFragment {

    public static AddNoteBottomSheet newInstance() {
        return new AddNoteBottomSheet();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_bottom_sheet_note, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button btnCancel = view.findViewById(R.id.btnCancel);
        Button btnSave = view.findViewById(R.id.btnSave);

        btnCancel.setOnClickListener(v -> dismiss());

        btnSave.setOnClickListener(v -> {
            // For now, just show a success message and dismiss
            // TODO: Extract data from EditTexts and save to Firebase/ViewModel
            Toast.makeText(getContext(), "Đã lưu ghi chú thành công!", Toast.LENGTH_SHORT).show();
            dismiss();
        });
    }
}
