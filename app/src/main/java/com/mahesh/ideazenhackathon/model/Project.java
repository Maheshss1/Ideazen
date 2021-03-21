package com.mahesh.ideazenhackathon.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Project implements Parcelable {
    private String id;
    private String userId;
    private String stream;
    private String projectName;
    private String projectDomain;
    private String softwareUsed;
    private String algorithmUsed;
    private String reference;
    private int difficultLevel;
    private String description;
    private String userName;

    public Project() {
    }

    public Project(String userId, String stream, String projectName, String projectDomain, String softwareUsed, String algorithmUsed, String reference, int difficultLevel, String description) {
        this.userId = userId;
        this.stream = stream;
        this.projectName = projectName;
        this.projectDomain = projectDomain;
        this.softwareUsed = softwareUsed;
        this.algorithmUsed = algorithmUsed;
        this.reference = reference;
        this.difficultLevel = difficultLevel;
        this.description = description;
    }

    protected Project(Parcel in) {
        id = in.readString();
        userId = in.readString();
        stream = in.readString();
        projectName = in.readString();
        projectDomain = in.readString();
        softwareUsed = in.readString();
        algorithmUsed = in.readString();
        reference = in.readString();
        difficultLevel = in.readInt();
        description = in.readString();
        userName = in.readString();
    }

    public static final Creator<Project> CREATOR = new Creator<Project>() {
        @Override
        public Project createFromParcel(Parcel in) {
            return new Project(in);
        }

        @Override
        public Project[] newArray(int size) {
            return new Project[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getStream() {
        return stream;
    }

    public void setStream(String stream) {
        this.stream = stream;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectDomain() {
        return projectDomain;
    }

    public void setProjectDomain(String projectDomain) {
        this.projectDomain = projectDomain;
    }

    public String getSoftwareUsed() {
        return softwareUsed;
    }

    public void setSoftwareUsed(String softwareUsed) {
        this.softwareUsed = softwareUsed;
    }

    public String getAlgorithmUsed() {
        return algorithmUsed;
    }

    public void setAlgorithmUsed(String algorithmUsed) {
        this.algorithmUsed = algorithmUsed;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public int getDifficultLevel() {
        return difficultLevel;
    }

    public void setDifficultLevel(int difficultLevel) {
        this.difficultLevel = difficultLevel;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Project{" +
                "userId='" + userId + '\'' +
                "userName='" + userName + '\'' +
                "description='" + description + '\'' +
                ", stream='" + stream + '\'' +
                ", projectName='" + projectName + '\'' +
                ", projectDomain='" + projectDomain + '\'' +
                ", softwareUsed='" + softwareUsed + '\'' +
                ", algorithmUsed='" + algorithmUsed + '\'' +
                ", reference='" + reference + '\'' +
                ", difficultLevel=" + difficultLevel +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(userId);
        parcel.writeString(stream);
        parcel.writeString(projectName);
        parcel.writeString(projectDomain);
        parcel.writeString(softwareUsed);
        parcel.writeString(algorithmUsed);
        parcel.writeString(reference);
        parcel.writeInt(difficultLevel);
        parcel.writeString(description);
        parcel.writeString(userName);
    }
}
