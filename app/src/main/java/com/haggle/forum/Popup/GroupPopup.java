package com.haggle.forum.Popup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.FragmentManager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.haggle.forum.R;

public class GroupPopup extends AppCompatDialogFragment {

    private Button join,create;
    private FragmentManager fragmentManager;


    public GroupPopup(FragmentManager fragmentManager ) {
        this.fragmentManager = fragmentManager;

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.popup_group_holder,null);

        builder.setView(view);
        join = view.findViewById(R.id.JoinButton);
        create = view.findViewById(R.id.CreateButton);
        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                join();
                dismiss();
            }
        });
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                create();
                dismiss();
            }
        });
        AlertDialog dialog = builder.show();
        dialog.getWindow().setLayout(600, 450);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        return dialog;
    }

    private void join(){
        PrivateJoinPopup privateJoinPopup = new PrivateJoinPopup();
        privateJoinPopup.show(fragmentManager,"Join");
    }

    private void create(){
        CreateGroupPopup createGroupPopup =new CreateGroupPopup();
        createGroupPopup.show(fragmentManager, "Create");
    }


}
