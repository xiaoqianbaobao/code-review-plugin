package com.codereviewer.rules;

import com.codereviewer.model.CodeIssue;
import com.codereviewer.model.FileChange;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NamingRule extends CodeRule {

    private static final Pattern CLASS_PATTERN = Pattern.compile("class\\s+([a-zA-Z_][a-zA-Z0-9_]*)");
    private static final Pattern METHOD_PATTERN = Pattern.compile("(public|private|protected)\\s+[a-zA-Z0-9_<>\\[\\]\\s]+\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*\\(");
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("(private|public|protected)\\s+[a-zA-Z0-9_<>\\[\\]\\s]+\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*[;=]");

    @Override
    public List<CodeIssue> check(FileChange fileChange) {
        List<CodeIssue> issues = new ArrayList<>();
        String[] lines = fileChange.getContent().split("\n");

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            int lineNumber = i + 1;

            // 检查类名命名
            checkClassName(fileChange.getFilePath(), lineNumber, line, issues);

            // 检查方法名命名
            checkMethodName(fileChange.getFilePath(), lineNumber, line, issues);

            // 检查变量名命名
            checkVariableName(fileChange.getFilePath(), lineNumber, line, issues);
        }

        return issues;
    }

    private void checkClassName(String fileName, int lineNumber, String line, List<CodeIssue> issues) {
        Matcher matcher = CLASS_PATTERN.matcher(line);
        if (matcher.find()) {
            String className = matcher.group(1);
            if (!Character.isUpperCase(className.charAt(0))) {
                issues.add(createIssue(fileName, lineNumber, "WARNING", "NAMING_001",
                        "类名应该以大写字母开头: " + className,
                        "将类名改为: " + Character.toUpperCase(className.charAt(0)) + className.substring(1)));
            }
        }
    }

    private void checkMethodName(String fileName, int lineNumber, String line, List<CodeIssue> issues) {
        Matcher matcher = METHOD_PATTERN.matcher(line);
        if (matcher.find()) {
            String methodName = matcher.group(2);
            if (Character.isUpperCase(methodName.charAt(0))) {
                issues.add(createIssue(fileName, lineNumber, "WARNING", "NAMING_002",
                        "方法名应该以小写字母开头: " + methodName,
                        "将方法名改为: " + Character.toLowerCase(methodName.charAt(0)) + methodName.substring(1)));
            }
        }
    }

    private void checkVariableName(String fileName, int lineNumber, String line, List<CodeIssue> issues) {
        Matcher matcher = VARIABLE_PATTERN.matcher(line);
        if (matcher.find()) {
            String variableName = matcher.group(2);
            if (Character.isUpperCase(variableName.charAt(0))) {
                issues.add(createIssue(fileName, lineNumber, "INFO", "NAMING_003",
                        "变量名应该以小写字母开头: " + variableName,
                        "将变量名改为: " + Character.toLowerCase(variableName.charAt(0)) + variableName.substring(1)));
            }
        }
    }
}