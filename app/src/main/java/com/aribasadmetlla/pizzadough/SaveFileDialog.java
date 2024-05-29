package com.aribasadmetlla.pizzadough;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import java.io.File;
import java.util.Objects;

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
                .setNegativeButton(getText(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton(getText(R.string.save), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String fileName = fileNameEditText.getText().toString();
                        String filePath = requireActivity().getFilesDir() +
                                "/" +
                                fileName +
                                ".txt";
                        File file = new File(filePath);
                        listener.saveFile(file);
                    }
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
