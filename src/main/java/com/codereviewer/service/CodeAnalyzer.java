package com.codereviewer.service;

import com.codereviewer.model.CodeIssue;
import com.codereviewer.model.FileChange;
import com.codereviewer.model.ReviewResult;
import com.codereviewer.rules.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CodeAnalyzer {

    private List<CodeRule> rules;

    public CodeAnalyzer() {
        initRules();
    }

    private void initRules() {
        rules = Arrays.asList(
                new NamingRule(),
                new ComplexityRule(),
                new StyleRule()
        );
    }

    public ReviewResult analyze(List<FileChange> fileChanges) {
        ReviewResult result = new ReviewResult();
        List<CodeIssue> allIssues = new ArrayList<>();

        for (FileChange fileChange : fileChanges) {
            if (fileChange.getContent().isEmpty()) {
                continue;
            }

            List<CodeIssue> fileIssues = analyzeFile(fileChange);
            allIssues.addAll(fileIssues);
        }

        result.setIssues(allIssues);
        result.setFileChanges(fileChanges);
        return result;
    }

    private List<CodeIssue> analyzeFile(FileChange fileChange) {
        List<CodeIssue> issues = new ArrayList<>();

        for (CodeRule rule : rules) {
            List<CodeIssue> ruleIssues = rule.check(fileChange);
            issues.addAll(ruleIssues);
        }

        return issues;
    }
}