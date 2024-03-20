package es.uc3m.android.travel_rex;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class SignUpListener implements OnCompleteListener<AuthResult> {

    Context context;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    Map<String, Object> userDetails;

    public SignUpListener(Context context, FirebaseAuth mAuth, FirebaseFirestore db, Map<String, Object> userDetails) {
        this.context = context;
        this.mAuth = mAuth;
        this.db = db;
        this.userDetails = userDetails;
    }

    @Override
    public void onComplete(@NonNull Task<AuthResult> task) {
        if (task.isSuccessful()) {
            FirebaseUser user = mAuth.getCurrentUser();
            db.collection("users").document(user.getUid()).set(userDetails);
            Log.d(this.getClass().getName(),
                    "createUserWithEmail:success " + user);
            context.startActivity(new Intent(context, MainActivity.class));
        } else {
            Log.w(this.getClass().getName(), "createUserWithEmail:failure",
                    task.getException());
            String message = context.getResources().getString(R.string.auth_failed) + " " +
                    task.getException().getMessage();
            Snackbar snackbar = Snackbar.make(((Activity) context).getCurrentFocus(),
                    message, Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }
}
