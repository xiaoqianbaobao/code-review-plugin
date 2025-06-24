package com.codereviewer.model;

public class FileChange {
    private String filePath;
    private String changeType; // ADD, MODIFY, DELETE
    private String content;

    // Getters and Setters
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public String getChangeType() { return changeType; }
    public void setChangeType(String changeType) { this.changeType = changeType; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}