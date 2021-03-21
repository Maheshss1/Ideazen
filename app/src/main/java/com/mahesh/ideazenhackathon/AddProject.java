package com.mahesh.ideazenhackathon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mahesh.ideazenhackathon.model.Project;
import com.mahesh.ideazenhackathon.model.ProjectWithKeywords;
import com.mahesh.ideazenhackathon.support.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AddProject extends AppCompatActivity {

    public static final String TAG = "AddProject";

    private EditText projectNameET,  descriptionET;
    private AutoCompleteTextView projectDomainET, softwareUsedET, algorithmUsedET, referenceET;
    private RadioGroup difficultyLevelET;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser user;
    private Button createProjectButton;
    private List<String> domainKeywords, softwareKeywords, algorithmKeywords, referenceKeywords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_project);

        initiateViews();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            startActivity(new Intent(this, MainActivity.class));
            Toast.makeText(this, "Session Expired Login Again To Continue!", Toast.LENGTH_SHORT).show();
            finish();
        }

        FirebaseFirestore.getInstance().collection(Constants.KEYWORDS).document(Constants.KEYWORDS_DOMAIN).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                domainKeywords = (List<String>) documentSnapshot.get(Constants.DOMAIN);
                softwareKeywords = (List<String>) documentSnapshot.get(Constants.SOFTWARE);
                algorithmKeywords = (List<String>) documentSnapshot.get(Constants.ALGORITHM);
                referenceKeywords = (List<String>) documentSnapshot.get(Constants.REFERENCE);
                projectDomainET.setAdapter(new ArrayAdapter<String>(AddProject.this, android.R.layout.simple_list_item_1, domainKeywords));
                softwareUsedET.setAdapter(new ArrayAdapter<String>(AddProject.this, android.R.layout.simple_list_item_1, softwareKeywords));
                algorithmUsedET.setAdapter(new ArrayAdapter<String>(AddProject.this, android.R.layout.simple_list_item_1, algorithmKeywords));
                referenceET.setAdapter(new ArrayAdapter<String>(AddProject.this, android.R.layout.simple_list_item_1, referenceKeywords));
            }
        });
    }

    public void createProject(View view){
        Log.d(TAG, "createProject: ");
        String name = projectNameET.getText().toString(), domain = projectDomainET.getText().toString(), software = softwareUsedET.getText().toString(),
                algorithm = algorithmUsedET.getText().toString(), reference = referenceET.getText().toString(), description = descriptionET.getText().toString();
        int difficultLevel = getDifficultyLevel(difficultyLevelET.getCheckedRadioButtonId());

        if(name.trim().isEmpty() || domain.trim().isEmpty() || software.trim().isEmpty() || algorithm.trim().isEmpty() || reference.trim().isEmpty() || description.trim().isEmpty()){
            Toast.makeText(this, "Please Fill All The Fields To Continue!", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "createProject: 1");
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        ProjectWithKeywords project = new ProjectWithKeywords(user.getUid(), "Commerce", name, domain, software, algorithm, reference, difficultLevel, description);
        Set<String> keywords = new HashSet<>();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            project.setUserName(user.getDisplayName());
            if(user.getDisplayName()!=null) {
                keywords.addAll(Arrays.asList(user.getDisplayName().toLowerCase().split("[\\s,]+")));
            }
        }

        final List<String> domainList = Arrays.asList(domain.toLowerCase().split("[\\s,]+"));
        final List<String> softwareList = Arrays.asList(software.toLowerCase().split("[\\s,]+"));
        final List<String> algorithmList = Arrays.asList(algorithm.toLowerCase().split("[\\s,]+"));
        final List<String> referenceList = Arrays.asList(reference.toLowerCase().split("[\\s,]+"));
//        domainList.addAll(domainKeywords);
        Log.d(TAG, "createProject: "+domainList);
//        Set<String> domainSet = new HashSet<>(domainList);
        keywords.addAll(Arrays.asList(name.toLowerCase().split("[\\s,]+")));
        keywords.addAll(domainList);
        keywords.addAll(softwareList);
        keywords.addAll(algorithmList);
        keywords.addAll(referenceList);
        Log.d(TAG, "createProject: "+keywords);
        List<String> keywordList = new ArrayList<>(keywords);
        project.setKeywords(keywordList);

        DocumentReference documentReference = firebaseFirestore.collection(Constants.COLLECTION_NAME).document();
        project.setId(documentReference.getId());
        documentReference.set(project).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                takeKeywordsAndPush(domainList, softwareList, algorithmList, referenceList);
                Toast.makeText(AddProject.this, "Project Successfully Added!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddProject.this, "Failed To Add! Retry!", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onFailure: "+e.getMessage());
                e.printStackTrace();
            }
        });
    }


    private void initiateViews() {
        projectNameET = findViewById(R.id.project_name);
        projectDomainET = findViewById(R.id.project_domain);
        softwareUsedET = findViewById(R.id.software_used);
        algorithmUsedET = findViewById(R.id.algorithme_used);
        referenceET = findViewById(R.id.reference);
        difficultyLevelET = findViewById(R.id.difficult_level);
        descriptionET = findViewById(R.id.description);
        createProjectButton = findViewById(R.id.create_project_button);

    }

    private int getDifficultyLevel(int radioButtonId){
        switch (radioButtonId){
            case R.id.low_diff_radio_button:
                return 0;
            case R.id.mid_diff_radio_button:
                return 1;
            case R.id.high_diff_radio_button:
                return 2;

        }
        return 0;
    }

    private void takeKeywordsAndPush(List<String> domain, List<String> software, List<String> algorithm, List<String> reference){

        FirebaseFirestore.getInstance().collection(Constants.KEYWORDS).document(Constants.KEYWORDS_DOMAIN).update(Constants.DOMAIN, FieldValue.arrayUnion(domain.toArray())).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
        FirebaseFirestore.getInstance().collection(Constants.KEYWORDS).document(Constants.KEYWORDS_DOMAIN).update(Constants.SOFTWARE, FieldValue.arrayUnion(software.toArray())).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
        FirebaseFirestore.getInstance().collection(Constants.KEYWORDS).document(Constants.KEYWORDS_DOMAIN).update(Constants.ALGORITHM, FieldValue.arrayUnion(algorithm.toArray())).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
        FirebaseFirestore.getInstance().collection(Constants.KEYWORDS).document(Constants.KEYWORDS_DOMAIN).update(Constants.REFERENCE, FieldValue.arrayUnion(reference.toArray())).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }


}