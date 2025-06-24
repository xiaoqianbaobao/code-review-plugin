package com.codereviewer.rules;

import com.codereviewer.model.CodeIssue;
import com.codereviewer.model.FileChange;

import java.util.List;

public abstract class CodeRule {
    public abstract List<CodeIssue> check(FileChange fileChange);

    protected CodeIssue createIssue(String fileName, int lineNumber, String severity,
                                    String ruleId, String message, String suggestion) {
        return new CodeIssue(fileName, lineNumber, severity, ruleId, message, suggestion);
    }
}