package ema.mosis.elfak.rs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class RegisterActivity extends AppCompatActivity {

    ProgressDialog pDialog;
    FirebaseAuth mAuth;
    EditText txtEmail,txtPassword, txtPassword2;
    Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        txtEmail = findViewById(R.id.emailRegister);
        txtPassword = findViewById(R.id.passwordRegister);
        txtPassword2 = findViewById(R.id.passwordRegisterConfirm);
        btnRegister = findViewById(R.id.register_btn);
        mAuth = FirebaseAuth.getInstance();

        btnRegister.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                register();
            }
        });
    }

    public void register()
    {
        pDialog = new ProgressDialog(RegisterActivity.this);
        pDialog.setMessage("Please wait");
        pDialog.show();

        final String email = txtEmail.getText().toString().trim();
        final String password = txtPassword.getText().toString().trim();
        final String password2 = txtPassword2.getText().toString().trim();

        if(checkInputError(email,password,password2))
        {
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
            {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {
                    pDialog.dismiss();

                    if(task.isSuccessful())
                    {
                        Toast.makeText(getApplicationContext(),"Registration successful!",Toast.LENGTH_SHORT).show();

                        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task)
                            {
                                if(task.isSuccessful())
                                {
                                    sendToMain();
                                }
                                else
                                    Toast.makeText(getApplicationContext(), "Login failed.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    else
                    {
                        if (task.getException() instanceof FirebaseAuthUserCollisionException)
                            Toast.makeText(getApplicationContext(), "User with this email already exists.", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(getApplicationContext(), "An error occurred", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else
            pDialog.dismiss();

    }

    public boolean checkInputError(String email,String password,String password2)
    {
        if(email.isEmpty())
        {
            txtEmail.setError("Please enter your email.");
            txtEmail.requestFocus();
            return false;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            txtEmail.setError("Please enter a valid email address.");
            txtEmail.requestFocus();
            return false;
        }

        if (password.isEmpty())
        {
            txtPassword.setError("Please enter your password.");
            txtPassword.requestFocus();
            return false;
        }

        if(password.length() < 6)
        {
            txtPassword.setError("Password must contain at least 6 characters.");
            txtPassword.requestFocus();
            return false;
        }

        if (password2.isEmpty())
        {
            txtPassword2.setError("Please enter your password.");
            txtPassword2.requestFocus();
            return false;
        }

        if(!password.equals(password2))
        {
            txtPassword2.setError("Passwords must match");
            txtPassword2.requestFocus();
            return false;
        }

        return true;
    }

    public void sendToMain()
    {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }

}
