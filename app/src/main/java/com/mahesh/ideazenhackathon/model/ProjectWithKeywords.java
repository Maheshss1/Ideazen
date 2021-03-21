package com.mahesh.ideazenhackathon.model;

import java.util.List;
import java.util.Set;

public class ProjectWithKeywords extends Project {

    private List<String> keywords;

    public ProjectWithKeywords(String userId, String stream, String projectName, String projectDomain, String softwareUsed, String algorithmUsed, String reference, int difficultLevel, String description) {
        super(userId, stream, projectName, projectDomain, softwareUsed, algorithmUsed, reference, difficultLevel, description);
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    @Override
    public String toString() {
        return "ProjectWithKeywords{" +
                "keywords=" + keywords +
                '}';
    }
}
