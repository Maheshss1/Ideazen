package com.mahesh.ideazenhackathon;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.mahesh.ideazenhackathon.model.Project;
import com.mahesh.ideazenhackathon.model.User;
import com.mahesh.ideazenhackathon.support.Constants;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProjectActivity extends AppCompatActivity {

    private CircleImageView profilePic;
    private TextView userNameTV, userNumberTV, userEmailTV;
    private boolean isHavingNumber = true;
    private User user;
    private FirebaseUser firebaseUser;
    private TextView projectName, projectDomain, softwareUsed, algorithmUsed, reference, description;
    private View difficultLevel;
    private ImageButton deleteButton;

    public static final int CALL_REQUEST_CODE = 3135;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);
        try {
            getWindow().setStatusBarColor(ContextCompat.getColor(ProjectActivity.this, R.color.colorPrimary));// set status background white
        }catch (Exception e){}

        initiatingViews();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser == null){
            startActivity(new Intent(this, MainActivity.class));
            Toast.makeText(this, "Session Expired! Login To Continue!", Toast.LENGTH_SHORT).show();
            finish();
        }
        String userId = getIntent().getStringExtra(Constants.USER_ID);
        Toast.makeText(this, userId, Toast.LENGTH_SHORT).show();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection(Constants.USER_COLLECTION).whereEqualTo("uuid", userId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null){
                    Toast.makeText(ProjectActivity.this, "Error Fetching Data!", Toast.LENGTH_SHORT).show();
                    Log.d("TAG", "onEvent: "+error.getMessage());
                    return;
                }

                if(value!= null){
                     user = value.getDocuments().get(0).toObject(User.class);
                     Picasso.get().load(user.getDisplayPic()).into(profilePic);

                     userNameTV.setText(user.getName());
                     if(user.getPhone()!= null && !user.getPhone().isEmpty()) {
                         userNumberTV.setText(user.getPhone());
                         isHavingNumber = false;
                     }
                     userEmailTV.setText(user.getEmail());
                }
            }
        });

        fetchProjectDetails();
    }

    private void fetchProjectDetails() {
        projectName = findViewById(R.id.project_name);
        projectDomain = findViewById(R.id.project_domain);
        softwareUsed = findViewById(R.id.software_used);
        algorithmUsed = findViewById(R.id.algorithm_used);
        reference = findViewById(R.id.reference);
        difficultLevel = findViewById(R.id.difficult_level);
        description = findViewById(R.id.description);
        deleteButton = findViewById(R.id.delete_button);

        Project project = getIntent().getParcelableExtra(Constants.PROJECT_PARCEL);
        if(project != null) {
            projectName.setText(project.getProjectName());
            projectDomain.setText(project.getProjectDomain());
            softwareUsed.setText(project.getSoftwareUsed());
            algorithmUsed.setText(project.getAlgorithmUsed());
            reference.setText(project.getReference());
            description.setText(project.getDescription());
            int color = 0;
            if(project.getDifficultLevel() == 0) color = R.color.low_difficult_green;
            if(project.getDifficultLevel() == 1) color = R.color.medium_difficult_yellow;
            if(project.getDifficultLevel() == 2) color = R.color.high_difficult_red;

            if(color!=0) {
                difficultLevel.setBackgroundColor(ResourcesCompat.getColor(getResources(), color, null));
            }

            if(!project.getUserId().equals(firebaseUser.getUid())){
                deleteButton.setVisibility(View.GONE);
            }
        }
    }

    private void initiatingViews() {
        profilePic = findViewById(R.id.circleImageView);
        userNameTV = findViewById(R.id.user_name);
        userNumberTV = findViewById(R.id.user_number);
        userEmailTV = findViewById(R.id.email);
    }

    public void sendEmail(View view){
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
//        intent.putExtra(Intent.EXTRA_EMAIL, userEmailTV.getText().toString());
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{userEmailTV.getText().toString()});
        intent.putExtra(Intent.EXTRA_SUBJECT, "I really liked your project and would really like to talk over it!");
        intent.putExtra(Intent.EXTRA_TEXT, "Hii "+userNameTV.getText().toString()+",");
        startActivity(Intent.createChooser(intent, "Send email..."));

//        if(intent.resolveActivity(getPackageManager()) != null){
//        }
    }

    public void call(View view){
        if(isHavingNumber) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "7276679431"));
                startActivity(intent);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, CALL_REQUEST_CODE);
            }
        }else{
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:"));

            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{userEmailTV.getText().toString()});
            intent.putExtra(Intent.EXTRA_SUBJECT, "I really liked your project and would really like to talk over it!");
            intent.putExtra(Intent.EXTRA_TEXT, "Hii "+userNameTV.getText().toString()+",\nI will be happy if you share your number so we can brainstorm more on this!");
            startActivity(Intent.createChooser(intent, "Send email..."));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CALL_REQUEST_CODE && resultCode == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, "Permission Granted Proceed To Call!", Toast.LENGTH_SHORT).show();
        }
    }
}