package com.mahesh.ideazenhackathon.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.protobuf.DescriptorProtos;
import com.mahesh.ideazenhackathon.OnClickListener;
import com.mahesh.ideazenhackathon.R;
import com.mahesh.ideazenhackathon.model.Project;
import com.mahesh.ideazenhackathon.support.Constants;

import java.util.List;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder> {

    private List<Project> projectList;
    private Context context;
    private OnClickListener onClickListener;

    public ProjectAdapter(Context context, List<Project> projectList) {
        this.projectList = projectList;
        this.context = context;

    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setProjectList(List<Project> projectList) {
        this.projectList = projectList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_project_layout, parent, false);
        return new ProjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectViewHolder holder, int position) {
        Project project = projectList.get(position);
        holder.projectName.setText(project.getProjectName());
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        holder.projectUser.setText("(" + project.getUserName() + ")");
        holder.projectDomain.setText(project.getProjectDomain());
        holder.softwareUsed.setText(project.getSoftwareUsed());
        holder.algorithmUsed.setText(project.getAlgorithmUsed());
        holder.description.setText(project.getDescription());
        holder.reference.setText(project.getReference());
        int color = 0;
        if (project.getDifficultLevel() == 0) color = R.color.low_difficult_green;
        if (project.getDifficultLevel() == 1) color = R.color.medium_difficult_yellow;
        if (project.getDifficultLevel() == 2) color = R.color.high_difficult_red;

        if (color != 0) {
            holder.difficultLevel.setBackgroundColor(ResourcesCompat.getColor(context.getResources(), color, null));
        }

        if (!project.getUserId().equals(user.getUid())) {
            holder.deleteButton.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return projectList.size();
    }

    class ProjectViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView projectName, projectUser, projectDomain, softwareUsed, algorithmUsed, reference, description;
        View difficultLevel;
        ImageButton deleteButton;

        public ProjectViewHolder(@NonNull View itemView) {
            super(itemView);

            projectName = itemView.findViewById(R.id.project_name);
            projectUser = itemView.findViewById(R.id.project_user);
            projectDomain = itemView.findViewById(R.id.project_domain);
            softwareUsed = itemView.findViewById(R.id.software_used);
            algorithmUsed = itemView.findViewById(R.id.algorithm_used);
            reference = itemView.findViewById(R.id.reference);
            difficultLevel = itemView.findViewById(R.id.difficult_level);
            description = itemView.findViewById(R.id.description);
            deleteButton = itemView.findViewById(R.id.delete_button);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("TAG", "onClick: " + getAdapterPosition());
                    onClickListener.onClick(projectList.get(getAdapterPosition()), getAdapterPosition());

                }
            });

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getAdapterPosition() >= 0) {
                        AlertDialog dialog = new AlertDialog.Builder(context).setTitle("Are You Sure You Want To Delete " + projectList.get(getAdapterPosition()).getProjectName() + "?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        FirebaseFirestore.getInstance().collection(Constants.COLLECTION_NAME).document(projectList.get(getAdapterPosition()).getId())
                                                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(context, "Project Deleted!", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(context, "Failed To Delete", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                }).create();
                        dialog.show();
                    }
                }
            });
        }

        @Override
        public void onClick(View view) {
            Log.d("TAG", "onClick: ");
            onClickListener.onClick(projectList.get(getAdapterPosition()), getAdapterPosition());
        }
    }

}
