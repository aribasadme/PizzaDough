package com.aribasadmetlla.pizzadough;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import java.io.File;

public class SaveFileDialog extends AppCompatDialogFragment {
    private EditText fileNameEditText;
    private SaveFileDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_save_dialog, null);

        fileNameEditText = view.findViewById(R.id.editFileName);

        builder.setView(view)
                .setTitle(getText(R.string.save_recipe))
                .setNegativeButton(getText(R.string.cancel), (dialog, which) -> {
                    // User cancelled the dialog
                })
                .setPositiveButton(getText(R.string.save), (dialog, which) -> {
                    if (fileNameEditText.getText().toString().trim().isEmpty()) {
                        listener.saveFile(null);
                        String toastMessage = getString(R.string.toast_file_name_empty);
                        Toast.makeText(requireActivity(), toastMessage, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String fileName = fileNameEditText.getText().toString().trim();
                    String filePath = requireActivity().getFilesDir() +
                            "/" +
                            fileName.trim() +
                            ".txt";
                    File file = new File(filePath);
                    listener.saveFile(file);
                });

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (SaveFileDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context +
                    "must implement SaveFileDialogListener");
        }
    }

    public interface SaveFileDialogListener {
        void saveFile(File file);
    }
}
