package com.learning.omar.firebaseuploadimages;

import android.content.ContentResolver;
import android.content.Intent;
import android.inputmethodservice.ExtractEditText;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    //Vars
    private static final int PICK_IMAGE_REQUEST = 1;

    private Uri imgUri;

    //Firebase
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private StorageTask storageTask;

    @BindView(R.id.btn_choose_image)
    AppCompatButton btnChooseImage;

    @BindView(R.id.et_filename)
    TextInputEditText etFileName;

    @BindView(R.id.img_view)
    AppCompatImageView imgView;

    @BindView(R.id.progress_bar)
    ContentLoadingProgressBar progressBar;

    @BindView(R.id.btn_upload)
    AppCompatButton btnUpload;

    @BindView(R.id.tv_show_upload)
    TextView tvShowUpload;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initDatabase();
    }

    private void initDatabase() {
        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        databaseReference = FirebaseDatabase.getInstance().getReference("uploads");
    }

    @OnClick(R.id.btn_choose_image)
    public void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imgUri = data.getData();
            Picasso.with(this)
                    .load(imgUri)
                    .into(imgView);

        }
    }

    @OnClick(R.id.btn_upload)
    public void uploadImage() {

        if (storageTask != null && storageTask.isInProgress()) {
            Toast.makeText(this, "Upload on progress", Toast.LENGTH_SHORT).show();
        } else {
            if (imgUri != null) {
                StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                        + "." + getFileExtension(imgUri));

                storageTask = fileReference.putFile(imgUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBar.setProgress(0);
                                    }
                                }, 500);
                                Toast.makeText(MainActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();
                                UploadModel uploadModel = new UploadModel(etFileName.getText().toString().trim(), taskSnapshot.getDownloadUrl().toString());

                                String uploadID = databaseReference.push().getKey();
                                databaseReference.child(uploadID).setValue(uploadModel);

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                progressBar.setProgress((int) progress);
                            }
                        });
            } else {
                Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @OnClick(R.id.tv_show_upload)
    public void showUploadedImage() {

        Intent intent = new Intent(this,ImagesActivity.class);
        startActivity(intent);
    }


}
