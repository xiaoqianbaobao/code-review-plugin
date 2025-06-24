package com.codereviewer.rules;

import com.codereviewer.model.CodeIssue;
import com.codereviewer.model.FileChange;

import java.util.ArrayList;
import java.util.List;

public class ComplexityRule extends CodeRule {

    private static final int MAX_METHOD_LINES = 50;
    private static final int MAX_CYCLOMATIC_COMPLEXITY = 10;

    @Override
    public List<CodeIssue> check(FileChange fileChange) {
        List<CodeIssue> issues = new ArrayList<>();
        String[] lines = fileChange.getContent().split("\n");

        checkMethodLength(fileChange.getFilePath(), lines, issues);
        checkCyclomaticComplexity(fileChange.getFilePath(), lines, issues);

        return issues;
    }

    private void checkMethodLength(String fileName, String[] lines, List<CodeIssue> issues) {
        boolean inMethod = false;
        int methodStartLine = 0;
        int methodLines = 0;
        int braceCount = 0;

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();

            if (line.matches(".*\\b(public|private|protected)\\s+.*\\(.*\\).*\\{.*")) {
                inMethod = true;
                methodStartLine = i + 1;
                methodLines = 1;
                braceCount = countChar(line, '{') - countChar(line, '}');
            } else if (inMethod) {
                methodLines++;
                braceCount += countChar(line, '{') - countChar(line, '}');

                if (braceCount == 0) {
                    if (methodLines > MAX_METHOD_LINES) {
                        issues.add(createIssue(fileName, methodStartLine, "WARNING", "COMPLEXITY_001",
                                "方法过长 (" + methodLines + " 行)，建议拆分",
                                "将方法拆分为多个小方法，每个方法不超过 " + MAX_METHOD_LINES + " 行"));
                    }
                    inMethod = false;
                }
            }
        }
    }

    private void checkCyclomaticComplexity(String fileName, String[] lines, List<CodeIssue> issues) {
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            int complexity = calculateLineComplexity(line);

            if (complexity > MAX_CYCLOMATIC_COMPLEXITY) {
                issues.add(createIssue(fileName, i + 1, "CRITICAL", "COMPLEXITY_002",
                        "代码复杂度过高，请简化逻辑",
                        "减少条件判断和循环嵌套，考虑使用策略模式或状态模式"));
            }
        }
    }

    private int calculateLineComplexity(String line) {
        int complexity = 0;
        String[] complexityKeywords = {"if", "else", "for", "while", "switch", "case", "catch", "&&", "||"};

        for (String keyword : complexityKeywords) {
            complexity += countOccurrences(line, keyword);
        }

        return complexity;
    }

    private int countChar(String str, char ch) {
        return (int) str.chars().filter(c -> c == ch).count();
    }

    private int countOccurrences(String str, String substring) {
        return (str.length() - str.replace(substring, "").length()) / substring.length();
    }
}