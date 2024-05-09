package es.uc3m.android.travel_rex;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

import es.uc3m.android.travel_rex.R;

// This file using this Source code: https://github.com/Everyday-Programmer/Upload-Image-to-Firebase-Android/tree/main/app/src/main/java/com/example/uploadimagefirebase

public class uploadPhoto extends AppCompatActivity {
    private FirebaseUser user;
    StorageReference storageReference;
    LinearProgressIndicator progressIndicator;
    Uri image;
    MaterialButton uploadImage, selectImage;
    ImageView imageView;

    String imageUuid;
    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
public void onActivityResult(ActivityResult result) {
    if (result.getResultCode() == RESULT_OK) {
        if (result.getData() != null) {
            uploadImage.setEnabled(true);
            image = result.getData().getData();
            Glide.with(getApplicationContext()).load(image).into(imageView);
        }
        } else {
            Toast.makeText(uploadPhoto.this, "Please select an image", Toast.LENGTH_SHORT).show();
        }
    }
});

@Override
protected void onCreate(Bundle savedInstanceState) {
    user = FirebaseAuth.getInstance().getCurrentUser();

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_upload_photo);

    FirebaseApp.initializeApp(uploadPhoto.this);
    storageReference = FirebaseStorage.getInstance().getReference();

    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    progressIndicator = findViewById(R.id.progress);

    imageView = findViewById(R.id.imageView);
    selectImage = findViewById(R.id.selectImage);
    uploadImage = findViewById(R.id.uploadImage);

    selectImage.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
    Intent intent = new Intent(Intent.ACTION_PICK);
    intent.setType("image/*");
    activityResultLauncher.launch(intent);
    }

});

uploadImage.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View view) {
    uploadImage(image);
    }
    });
}

private void uploadImage(Uri file) {
    String type = getIntent().getStringExtra("type");
    String path = "";
    if ("profile".equals(type)){
        String uid = user.getUid();
        path = "profile_images/" + uid;
    }else {
        String imageUuid = getIntent().getStringExtra("uuid");
        path = "visited_images/" + imageUuid;
    }

    StorageReference ref = storageReference.child(path);
    ref.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
@Override
public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
Toast.makeText(uploadPhoto.this, "Image Uploaded!!", Toast.LENGTH_SHORT).show();
finish();
}
}).addOnFailureListener(new OnFailureListener() {
@Override
public void onFailure(@NonNull Exception e) {
Toast.makeText(uploadPhoto.this, "Failed!" + e.getMessage(), Toast.LENGTH_SHORT).show();
}
}).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
@Override
public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
progressIndicator.setMax(Math.toIntExact(taskSnapshot.getTotalByteCount()));
progressIndicator.setProgress(Math.toIntExact(taskSnapshot.getBytesTransferred()));
}
});
}
}