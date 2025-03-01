package com.example.todolist;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.todolist.Model.ToDoModel;
import com.example.todolist.Utils.DataBaseHelper;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class AddNewTask extends BottomSheetDialogFragment {

    public static final String TAG = "AddNewTask";
    private EditText mEditText;
    private Button mSavebutton;
    private DataBaseHelper myDB;
    public static AddNewTask newInstance(){
        return new AddNewTask();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.add_new_task, container, false);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mEditText = view.findViewById(R.id.editText);
        mSavebutton = view.findViewById(R.id.addButton);
        myDB = new DataBaseHelper(getActivity());

        // Use a final wrapper for isUpdate
        final boolean[] isUpdate = {false};

        final Bundle bundle = getArguments();
        if (bundle != null) {
            isUpdate[0] = true;
            String task = bundle.getString("task");
            mEditText.setText(task);
            if (task != null && task.length() > 0) {
                mSavebutton.setEnabled(true);
            } else {
                mSavebutton.setEnabled(false);
                mSavebutton.setBackgroundColor(Color.GRAY);
            }
        }

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().isEmpty()) {
                    mSavebutton.setEnabled(false);
                    mSavebutton.setBackgroundColor(Color.GRAY);
                } else {
                    mSavebutton.setEnabled(true);
                    mSavebutton.setBackgroundColor(Color.BLUE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        mSavebutton.setOnClickListener(v -> {
            String text = mEditText.getText().toString();
            if (isUpdate[0] && bundle != null && bundle.containsKey("ID")) {
                Object taskId = bundle.get("ID");
                if (taskId instanceof Integer) {
                    myDB.updateTask((Integer) taskId, text);
                } else if (taskId instanceof String) {
                    try {
                        int id = Integer.parseInt((String) taskId);
                        myDB.updateTask(id, text);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                ToDoModel item = new ToDoModel();
                item.setTask(text);
                item.setStatus(0);
                myDB.insertTask(item);
            }
            dismiss();
        });
    }
    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        Activity activity = getActivity();
        if (activity instanceof OnDialogCloseListener){
            ((OnDialogCloseListener)activity).onDialogClose(dialog);;
        }
    }
}