package es.uc3m.android.travel_rex;

import static es.uc3m.android.travel_rex.LoginActivity.displayDialog;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Button listeners
        findViewById(R.id.sign_up_button).setOnClickListener(this::signUp);
        findViewById(R.id.sign_in).setOnClickListener(this::signIn);
    }

    private void signUp(View view) {
        EditText userName = findViewById(R.id.user_name);
        EditText userLocation = findViewById(R.id.user_location);
        EditText userEmail = findViewById(R.id.user_email);
        EditText userPassword = findViewById(R.id.user_password);
        EditText userPasswordConfirm = findViewById(R.id.user_password_confirm);

        String name = userName.getText().toString();
        String location = userLocation.getText().toString();
        String email = userEmail.getText().toString();
        String password = userPassword.getText().toString();
        String passwordConfirm = userPasswordConfirm.getText().toString();

        if (!isValidEmailAddress(email)) {
            displayDialog(this, R.string.sign_up_error_title, R.string.sign_up_error_invalid_email);
        } else if (!password.equals(passwordConfirm)) {
            displayDialog(this, R.string.sign_up_error_title, R.string.sign_up_error_passwd_diff);
        } else if (password.length() < 6) {
            displayDialog(this, R.string.sign_up_error_title, R.string.sign_up_error_passwd_len);
        } else {
            // Initialize Firebase Auth & Firestore
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // User object to save to db
            Map<String, Object> userDetails = new HashMap<>();
            userDetails.put("name", name);
            userDetails.put("location", location);
            userDetails.put("email", email);

            // Create user
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new SignUpListener(this, mAuth, db, userDetails));
        }

    }

    private void signIn(View view) {
        finish();
    }

    // Source: https://stackoverflow.com/questions/624581/what-is-the-best-java-email-address-validation-method
    public boolean isValidEmailAddress(String email) {
        String ePattern =
                "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

}