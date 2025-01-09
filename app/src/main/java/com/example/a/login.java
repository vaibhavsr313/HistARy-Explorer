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


public class login extends BottomSheetDialogFragment {

    EditText emailinlogin;
    EditText passwordinlogin;
    Button submitinlogin;
    private AlertDialog dialog;
    FirebaseAuth mAuth;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.AppBottomSheetDialogTheme); // Apply the custom style
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate your layout for the bottom sheet
        View view = inflater.inflate(R.layout.loginfragment, container, false);

        emailinlogin = view.findViewById(R.id.emailinlogin);
        passwordinlogin = view.findViewById(R.id.passwordinlogin);
        submitinlogin = view.findViewById(R.id.submitinlogin);
        mAuth = FirebaseAuth.getInstance();

        submitinlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                showProgressDialog();
                String email, password;
                email = String.valueOf(emailinlogin.getText());
                password = String.valueOf(passwordinlogin.getText());

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

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                showProgressDialog();
                                if (task.isSuccessful()) {
                                    Toast.makeText(requireContext(), "Successfully Login", Toast.LENGTH_SHORT).show();
                                    Intent intent =new Intent(requireContext(),Homepage.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    //overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                                    //finish();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(requireContext(), "Wrong Email or password",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        return view;
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
