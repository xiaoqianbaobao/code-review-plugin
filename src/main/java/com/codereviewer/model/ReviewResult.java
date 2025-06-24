package com.codereviewer.model;

import java.util.List;

public class ReviewResult {
    private List<FileChange> fileChanges;
    private List<CodeIssue> issues;

    public int getTotalIssues() {
        return issues != null ? issues.size() : 0;
    }

    public long getCriticalIssues() {
        return issues.stream().filter(i -> "CRITICAL".equals(i.getSeverity())).count();
    }

    public long getWarningIssues() {
        return issues.stream().filter(i -> "WARNING".equals(i.getSeverity())).count();
    }

    public long getInfoIssues() {
        return issues.stream().filter(i -> "INFO".equals(i.getSeverity())).count();
    }

    // Getters and Setters
    public List<FileChange> getFileChanges() { return fileChanges; }
    public void setFileChanges(List<FileChange> fileChanges) { this.fileChanges = fileChanges; }
    public List<CodeIssue> getIssues() { return issues; }
    public void setIssues(List<CodeIssue> issues) { this.issues = issues; }
}