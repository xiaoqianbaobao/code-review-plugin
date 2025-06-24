package com.codereviewer.rules;

import com.codereviewer.model.CodeIssue;
import com.codereviewer.model.FileChange;

import java.util.ArrayList;
import java.util.List;

public class StyleRule extends CodeRule {

    @Override
    public List<CodeIssue> check(FileChange fileChange) {
        List<CodeIssue> issues = new ArrayList<>();
        String[] lines = fileChange.getContent().split("\n");

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            int lineNumber = i + 1;

            checkLineLength(fileChange.getFilePath(), lineNumber, line, issues);
            checkIndentation(fileChange.getFilePath(), lineNumber, line, issues);
            checkTrailingWhitespace(fileChange.getFilePath(), lineNumber, line, issues);
            checkBraceStyle(fileChange.getFilePath(), lineNumber, line, issues);
        }

        return issues;
    }

    private void checkLineLength(String fileName, int lineNumber, String line, List<CodeIssue> issues) {
        if (line.length() > 120) {
            issues.add(createIssue(fileName, lineNumber, "INFO", "STYLE_001",
                    "代码行过长 (" + line.length() + " 字符)",
                    "建议将代码行长度控制在120字符以内"));
        }
    }

    private void checkIndentation(String fileName, int lineNumber, String line, List<CodeIssue> issues) {
        if (line.startsWith("\t")) {
            issues.add(createIssue(fileName, lineNumber, "INFO", "STYLE_002",
                    "使用了Tab字符进行缩进",
                    "建议使用4个空格代替Tab字符"));
        }
    }

    private void checkTrailingWhitespace(String fileName, int lineNumber, String line, List<CodeIssue> issues) {
        if (line.endsWith(" ") || line.endsWith("\t")) {
            issues.add(createIssue(fileName, lineNumber, "INFO", "STYLE_003",
                    "行末存在多余的空白字符",
                    "删除行末的空白字符"));
        }
    }

    private void checkBraceStyle(String fileName, int lineNumber, String line, List<CodeIssue> issues) {
        String trimmed = line.trim();
        if (trimmed.equals("{")) {
            issues.add(createIssue(fileName, lineNumber, "INFO", "STYLE_004",
                    "左大括号应该与控制语句在同一行",
                    "将左大括号移至控制语句的末尾"));
        }
    }
}