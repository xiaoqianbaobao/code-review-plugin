package com.codereviewer.service;

import com.codereviewer.model.CodeIssue;
import com.codereviewer.model.FileChange;
import com.codereviewer.model.ReviewResult;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportGenerator {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void generateReport(ReviewResult result, File outputDir) throws IOException {
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        // 生成HTML报告
        generateHtmlReport(result, outputDir);

        // 生成JSON报告
        generateJsonReport(result, outputDir);

        // 生成Markdown报告
        generateMarkdownReport(result, outputDir);
    }

    private void generateHtmlReport(ReviewResult result, File outputDir) throws IOException {
        File htmlFile = new File(outputDir, "code-review-report.html");

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n")
                .append("<html lang='zh-CN'>\n")
                .append("<head>\n")
                .append("    <meta charset='UTF-8'>\n")
                .append("    <meta name='viewport' content='width=device-width, initial-scale=1.0'>\n")
                .append("    <title>代码评审报告</title>\n")
                .append("    <style>\n")
                .append(getHtmlStyles())
                .append("    </style>\n")
                .append("</head>\n")
                .append("<body>\n");

        // 报告头部
        html.append("    <div class='header'>\n")
                .append("        <h1>代码评审报告</h1>\n")
                .append("        <p>生成时间: ").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).append("</p>\n")
                .append("    </div>\n");

        // 统计信息
        html.append("    <div class='summary'>\n")
                .append("        <h2>评审统计</h2>\n")
                .append("        <div class='stats'>\n")
                .append("            <div class='stat-item critical'>严重问题: ").append(result.getCriticalIssues()).append("</div>\n")
                .append("            <div class='stat-item warning'>警告问题: ").append(result.getWarningIssues()).append("</div>\n")
                .append("            <div class='stat-item info'>建议优化: ").append(result.getInfoIssues()).append("</div>\n")
                .append("            <div class='stat-item total'>总计: ").append(result.getTotalIssues()).append("</div>\n")
                .append("        </div>\n")
                .append("    </div>\n");

        // 文件变更列表
        html.append("    <div class='file-changes'>\n")
                .append("        <h2>文件变更 (").append(result.getFileChanges().size()).append(" 个文件)</h2>\n")
                .append("        <ul>\n");
        for (FileChange change : result.getFileChanges()) {
            html.append("            <li><span class='change-type ").append(change.getChangeType().toLowerCase()).append("'>")
                    .append(change.getChangeType()).append("</span> ").append(change.getFilePath()).append("</li>\n");
        }
        html.append("        </ul>\n")
                .append("    </div>\n");

        // 问题详情
        if (!result.getIssues().isEmpty()) {
            html.append("    <div class='issues'>\n")
                    .append("        <h2>问题详情</h2>\n");

            Map<String, java.util.List<CodeIssue>> issuesByFile = result.getIssues().stream()
                    .collect(Collectors.groupingBy(CodeIssue::getFileName));

            for (Map.Entry<String, java.util.List<CodeIssue>> entry : issuesByFile.entrySet()) {
                html.append("        <div class='file-section'>\n")
                        .append("            <h3>").append(entry.getKey()).append("</h3>\n");

                for (CodeIssue issue : entry.getValue()) {
                    html.append("            <div class='issue ").append(issue.getSeverity().toLowerCase()).append("'>\n")
                            .append("                <div class='issue-header'>\n")
                            .append("                    <span class='severity'>").append(issue.getSeverity()).append("</span>\n")
                            .append("                    <span class='rule-id'>").append(issue.getRuleId()).append("</span>\n")
                            .append("                    <span class='line-number'>第 ").append(issue.getLineNumber()).append(" 行</span>\n")
                            .append("                </div>\n")
                            .append("                <div class='issue-message'>").append(issue.getMessage()).append("</div>\n");
                    if (issue.getSuggestion() != null && !issue.getSuggestion().isEmpty()) {
                        html.append("                <div class='issue-suggestion'><strong>建议:</strong> ").append(issue.getSuggestion()).append("</div>\n");
                    }
                    html.append("            </div>\n");
                }
                html.append("        </div>\n");
            }
            html.append("    </div>\n");
        }

        html.append("</body>\n</html>");

        try (FileWriter writer = new FileWriter(htmlFile, false)) {
            writer.write(html.toString());
        }
    }

    private String getHtmlStyles() {
        return """
            body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; margin: 0; padding: 20px; background-color: #f5f5f5; }
            .header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px; border-radius: 10px; margin-bottom: 20px; }
            .header h1 { margin: 0; font-size: 2.5em; }
            .header p { margin: 10px 0 0 0; opacity: 0.9; }
            .summary { background: white; padding: 20px; border-radius: 10px; margin-bottom: 20px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
            .stats { display: flex; gap: 15px; flex-wrap: wrap; }
            .stat-item { padding: 15px 20px; border-radius: 8px; font-weight: bold; color: white; min-width: 120px; text-align: center; }
            .stat-item.critical { background: linear-gradient(135deg, #ff6b6b, #ee5a24); }
            .stat-item.warning { background: linear-gradient(135deg, #feca57, #ff9ff3); }
            .stat-item.info { background: linear-gradient(135deg, #48dbfb, #0abde3); }
            .stat-item.total { background: linear-gradient(135deg, #1dd1a1, #10ac84); }
            .file-changes, .issues { background: white; padding: 20px; border-radius: 10px; margin-bottom: 20px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
            .file-changes ul { list-style: none; padding: 0; }
            .file-changes li { padding: 8px 0; border-bottom: 1px solid #eee; }
            .change-type { padding: 4px 8px; border-radius: 4px; font-size: 0.8em; font-weight: bold; margin-right: 10px; }
            .change-type.add { background: #d4edda; color: #155724; }
            .change-type.modify { background: #fff3cd; color: #856404; }
            .change-type.delete { background: #f8d7da; color: #721c24; }
            .file-section { margin-bottom: 25px; }
            .file-section h3 { color: #333; border-bottom: 2px solid #667eea; padding-bottom: 5px; }
            .issue { border-left: 4px solid #ddd; padding: 15px; margin: 10px 0; border-radius: 0 8px 8px 0; }
            .issue.critical { border-left-color: #ff6b6b; background: #fff5f5; }
            .issue.warning { border-left-color: #feca57; background: #fffbf0; }
            .issue.info { border-left-color: #48dbfb; background: #f0fcff; }
            .issue-header { display: flex; gap: 15px; margin-bottom: 8px; align-items: center; }
            .severity { padding: 2px 8px; border-radius: 12px; font-size: 0.8em; font-weight: bold; }
            .issue.critical .severity { background: #ff6b6b; color: white; }
            .issue.warning .severity { background: #feca57; color: #333; }
            .issue.info .severity { background: #48dbfb; color: white; }
            .rule-id { font-family: monospace; background: #f8f9fa; padding: 2px 6px; border-radius: 4px; font-size: 0.9em; }
            .line-number { color: #666; font-size: 0.9em; }
            .issue-message { font-weight: 500; margin-bottom: 8px; color: #333; }
            .issue-suggestion { color: #666; font-style: italic; }
            """;
    }

    private void generateJsonReport(ReviewResult result, File outputDir) throws IOException {
        File jsonFile = new File(outputDir, "code-review-report.json");
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(jsonFile, result);
    }

    private void generateMarkdownReport(ReviewResult result, File outputDir) throws IOException {
        File mdFile = new File(outputDir, "code-review-report.md");

        StringBuilder md = new StringBuilder();
        md.append("# 代码评审报告\n\n");
        md.append("**生成时间:** ").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).append("\n\n");

        // 统计信息
        md.append("## 评审统计\n\n");
        md.append("| 类型 | 数量 |\n");
        md.append("|------|------|\n");
        md.append("| 严重问题 | ").append(result.getCriticalIssues()).append(" |\n");
        md.append("| 警告问题 | ").append(result.getWarningIssues()).append(" |\n");
        md.append("| 建议优化 | ").append(result.getInfoIssues()).append(" |\n");
        md.append("| **总计** | **").append(result.getTotalIssues()).append("** |\n\n");

        // 文件变更
        md.append("## 文件变更 (").append(result.getFileChanges().size()).append(" 个文件)\n\n");
        for (FileChange change : result.getFileChanges()) {
            md.append("- **").append(change.getChangeType()).append("** `").append(change.getFilePath()).append("`\n");
        }
        md.append("\n");

        // 问题详情
        if (!result.getIssues().isEmpty()) {
            md.append("## 问题详情\n\n");

            Map<String, java.util.List<CodeIssue>> issuesByFile = result.getIssues().stream()
                    .collect(Collectors.groupingBy(CodeIssue::getFileName));

            for (Map.Entry<String, java.util.List<CodeIssue>> entry : issuesByFile.entrySet()) {
                md.append("### ").append(entry.getKey()).append("\n\n");

                for (CodeIssue issue : entry.getValue()) {
                    String severityIcon = getSeverityIcon(issue.getSeverity());
                    md.append("#### ").append(severityIcon).append(" ").append(issue.getSeverity())
                            .append(" - ").append(issue.getRuleId()).append(" (第 ").append(issue.getLineNumber()).append(" 行)\n\n");
                    md.append("**问题:** ").append(issue.getMessage()).append("\n\n");
                    if (issue.getSuggestion() != null && !issue.getSuggestion().isEmpty()) {
                        md.append("**建议:** ").append(issue.getSuggestion()).append("\n\n");
                    }
                    md.append("---\n\n");
                }
            }
        }

        try (FileWriter writer = new FileWriter(mdFile, false)) {
            writer.write(md.toString());
        }
    }

    private String getSeverityIcon(String severity) {
        switch (severity) {
            case "CRITICAL": return "🚨";
            case "WARNING": return "⚠️";
            case "INFO": return "ℹ️";
            default: return "📝";
        }
    }
}