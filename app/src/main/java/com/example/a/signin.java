package com.example.a;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.a.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class signin extends BottomSheetDialogFragment {

    EditText emailinsignin;
    EditText passwordinsignin;
    EditText nameinsignin;
    Button submitinsignin;
    private AlertDialog dialog;
    FirebaseAuth mAuth;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.AppBottomSheetDialogTheme); // Apply the custom style
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate your layout for the bottom sheet
        View view = inflater.inflate(R.layout.signinfragment, container, false);

        emailinsignin = view.findViewById(R.id.emailinsignin);
        passwordinsignin = view.findViewById(R.id.passwordinsignin);
        submitinsignin = view.findViewById(R.id.submitinsignin);
        nameinsignin = view.findViewById(R.id.nameinsignin);
        mAuth = FirebaseAuth.getInstance();

        submitinsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                showProgressDialog();
                String email, password, name;
                email = String.valueOf(emailinsignin.getText());
                password = String.valueOf(passwordinsignin.getText());
                name = String.valueOf(nameinsignin.getText());

                if (TextUtils.isEmpty(email)){
                    Toast.makeText(requireContext(), "Enter Email", Toast.LENGTH_SHORT).show();
                    dismissProgressDialog();
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    Toast.makeText(requireContext(), "Enter Password", Toast.LENGTH_SHORT).show();
                    dismissProgressDialog();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                showProgressDialog();
                                if (task.isSuccessful()) {

                                    uploadData(name, email);
//                                    Toast.makeText(requireContext(), "Authentication Success.",
//                                            Toast.LENGTH_SHORT).show();
//                                    //saveData();
//                                    Intent intent =new Intent(requireContext(),Homepage.class);
//                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                    startActivity(intent);
//                                    //overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
//                                    //finish();
                                } else {
                                    Toast.makeText(requireContext(), "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });



            }
        });

        return view;

    }

    private void uploadData(String name, String email) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            DataClass dataClass = new DataClass(name, email, userId);

            FirebaseDatabase.getInstance().getReference("Users").child(userId)
                    .setValue(dataClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(requireContext(), "Data Saved Successfully.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(requireContext(), Homepage.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            } else {
                                Toast.makeText(requireContext(), "Failed to save data.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(e -> {
                        Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }


    private void showProgressDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss(); // Dismiss any existing dialog to avoid duplicate dialogs
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        dialog = builder.create(); // Assign dialog to the member variable
        dialog.show();
    }


    private void dismissProgressDialog() {
        if (dialog != null && dialog.isShowing() && isAdded() && !requireActivity().isFinishing()) {
            dialog.dismiss();
        }
    }


}