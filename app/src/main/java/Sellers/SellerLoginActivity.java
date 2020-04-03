package Sellers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SellerLoginActivity extends AppCompatActivity {

    private Button loginSellerButton;
    private EditText emailInput,passwordInput;

    private FirebaseAuth mAuth;

    private ProgressDialog loadingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_login);

        mAuth = FirebaseAuth.getInstance();

        loadingbar = new ProgressDialog(this);

        loginSellerButton = (Button) findViewById(R.id.seller_login_btn);
        emailInput = (EditText) findViewById(R.id.seller_login_email);
        passwordInput = (EditText) findViewById(R.id.seller_login_password);

        loginSellerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginSeller();
            }
        });
    }

    private void loginSeller()
    {
        final String email = emailInput.getText().toString();
        final String password = passwordInput.getText().toString();

        if(!(email.equals("") && password.equals(""))) {

            loadingbar.setTitle("Seller Account Login");
            loadingbar.setMessage("Please wait while we are checking credentials");
            loadingbar.setCanceledOnTouchOutside(false);
            loadingbar.show();

            mAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if(task.isSuccessful())
                            {
                                Intent intent = new Intent(SellerLoginActivity.this,SellersHomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }

                        }
                    });

        }

        else
        {
            Toast.makeText(this,"Please complete the login details",Toast.LENGTH_SHORT).show();
        }

    }


}
