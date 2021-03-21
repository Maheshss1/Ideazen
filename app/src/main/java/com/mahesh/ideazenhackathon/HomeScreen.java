package com.mahesh.ideazenhackathon;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.mahesh.ideazenhackathon.adapter.ProjectAdapter;
import com.mahesh.ideazenhackathon.model.Project;
import com.mahesh.ideazenhackathon.support.Constants;

import java.util.ArrayList;
import java.util.List;

public class HomeScreen extends AppCompatActivity implements OnClickListener{

    private static final String TAG = "HomeScreenLog";
    FirebaseFirestore db;
    private RecyclerView recyclerView;
    private Button addProjectButton;
    private SearchView searchView;
    private ProjectAdapter projectAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

//        Toolbar toolbar = findViewById(R.id.toolbar);
//
//        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.project_recyclerview);
        searchView = findViewById(R.id.search_view);

        db = FirebaseFirestore.getInstance();

        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fetchProjects();
        setUpSearchView();
    }

    private void setUpSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                db.collection(Constants.COLLECTION_NAME).whereArrayContains("keywords", s.toLowerCase()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error != null){
                            Toast.makeText(HomeScreen.this, "Error Occured While Searching!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(value != null) {
                            List<DocumentSnapshot> documentSnapshots = value.getDocuments();
                            List<Project> projectList = new ArrayList<>();
                            for(DocumentSnapshot documentSnapshot: documentSnapshots){
                                Project project = documentSnapshot.toObject(Project.class);
                                projectList.add(project);
                            }

                            projectAdapter.setProjectList(projectList);
                        }
                    }
                });
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if(s.trim().length() == 0){
                    fetchProjects();
                    return true;
                }
                return false;

            }
        });
    }

    private void fetchProjects() {

        db.collection(Constants.COLLECTION_NAME).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null){
                    Toast.makeText(HomeScreen.this, "Failed To Load! Retry!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(value != null) {
                    List<DocumentSnapshot> documentSnapshots = value.getDocuments();
                    Log.d(TAG, "onEvent: "+documentSnapshots);

                    List<Project> projectList = new ArrayList<>();
                    for(DocumentSnapshot documentSnapshot: documentSnapshots){
                        Project project = documentSnapshot.toObject(Project.class);
                        projectList.add(project);
                    }
                    Log.d(TAG, "onEvent: "+projectList);
                    projectAdapter = new ProjectAdapter(HomeScreen.this, projectList);
                    projectAdapter.setOnClickListener(HomeScreen.this);
                    recyclerView.setAdapter(projectAdapter);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_homescreen, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.add_project:
                startActivity(new Intent(this, AddProject.class));
                return true;
            case R.id.logout:
                 logout();
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
            .setTitle("Are You Sure You Want To Logout?")
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(HomeScreen.this, SplashScreen.class));
                    finish();
                }
            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            })
            .create();
        alertDialog.show();

    }

    public void createProject(View view){
        startActivity(new Intent(this, AddProject.class));
    }

    @Override
    public void onClick(Project project, int position) {
        Log.d(TAG, "onClick: ");
        String uuid = project.getUserId();
        Intent intent = new Intent(this, ProjectActivity.class);
        intent.putExtra(Constants.USER_ID, uuid);
        intent.putExtra(Constants.PROJECT_PARCEL, project);
        startActivity(intent);
    }
}