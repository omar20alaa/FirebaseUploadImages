package com.learning.omar.firebaseuploadimages;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ImagesActivity extends AppCompatActivity implements ImagesAdapter.OnItemClickListener {

    @BindView(R.id.rec_view)
    RecyclerView recView;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    //Vars
    ImagesAdapter imagesAdapter;
    List<UploadModel> uploadItem;

    //Firebase
    private DatabaseReference databaseReference;
    private FirebaseStorage mStorage;
    private ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);

        ButterKnife.bind(this);
        initRecyclerView();
    }

    private void initRecyclerView() {
        recView.setHasFixedSize(true);
        recView.setLayoutManager(new LinearLayoutManager(this));
        uploadItem = new ArrayList<>();
        imagesAdapter = new ImagesAdapter(ImagesActivity.this, uploadItem);
        imagesAdapter.setOnItemClickListener(ImagesActivity.this);
        recView.setAdapter(imagesAdapter);

        mStorage = FirebaseStorage.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("uploads");
        valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                uploadItem.clear();

                for (DataSnapshot postsnapshot : dataSnapshot.getChildren()) {
                    UploadModel upload = postsnapshot.getValue(UploadModel.class);

                    upload.setmKey(postsnapshot.getKey());
                    uploadItem.add(upload);
                }

                imagesAdapter.notifyDataSetChanged();

                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ImagesActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);

            }
        });
    }

    @Override
    public void OnItemClick(int position) {
        Toast.makeText(this, "Normal click at position: " + position, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void OnWhateverClick(int position) {
        Toast.makeText(this, "Whatever click at position: " + position, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void OndeleteClick(int position) {
        UploadModel select_item = uploadItem.get(position);
        final String selectedKey = select_item.getmKey();

        StorageReference storageReference = mStorage.getReferenceFromUrl(select_item.getImg_url());
        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                databaseReference.child(selectedKey).removeValue();
                Toast.makeText(ImagesActivity.this, "item deleted", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseReference.removeEventListener(valueEventListener);
    }
}
