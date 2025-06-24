package com.codereviewer.model;

public class CodeIssue {
    private String fileName;
    private int lineNumber;
    private String severity; // CRITICAL, WARNING, INFO
    private String ruleId;
    private String message;
    private String suggestion;
    private String codeSnippet;

    // Constructors
    public CodeIssue() {}

    public CodeIssue(String fileName, int lineNumber, String severity,
                     String ruleId, String message, String suggestion) {
        this.fileName = fileName;
        this.lineNumber = lineNumber;
        this.severity = severity;
        this.ruleId = ruleId;
        this.message = message;
        this.suggestion = suggestion;
    }

    // Getters and Setters
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public int getLineNumber() { return lineNumber; }
    public void setLineNumber(int lineNumber) { this.lineNumber = lineNumber; }
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
    public String getRuleId() { return ruleId; }
    public void setRuleId(String ruleId) { this.ruleId = ruleId; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getSuggestion() { return suggestion; }
    public void setSuggestion(String suggestion) { this.suggestion = suggestion; }
    public String getCodeSnippet() { return codeSnippet; }
    public void setCodeSnippet(String codeSnippet) { this.codeSnippet = codeSnippet; }
}