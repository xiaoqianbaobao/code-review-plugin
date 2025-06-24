package com.codereviewer;

import com.codereviewer.model.ReviewResult;
import com.codereviewer.service.CodeAnalyzer;
import com.codereviewer.service.GitService;
import com.codereviewer.service.ReportGenerator;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;

@Mojo(name = "review")
public class CodeReviewMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Parameter(property = "baseBranch", defaultValue = "master")
    private String baseBranch;

    @Parameter(property = "outputDir", defaultValue = "${project.build.directory}/code-review")
    private String outputDir;

    @Parameter(property = "includePatterns", defaultValue = "**/*.java")
    private String includePatterns;

    @Parameter(property = "excludePatterns", defaultValue = "**/target/**")
    private String excludePatterns;

    private GitService gitService;
    private CodeAnalyzer codeAnalyzer;
    private ReportGenerator reportGenerator;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("开始执行代码评审...");

        try {
            initServices();

            // 获取项目根目录
            File projectDir = project.getBasedir();
            getLog().info("项目目录: " + projectDir.getAbsolutePath());

            // 获取Git变更
            getLog().info("分析Git变更，基线分支: " + baseBranch);
            var fileChanges = gitService.getChangedFiles(projectDir, baseBranch);

            if (fileChanges.isEmpty()) {
                getLog().info("没有发现代码变更");
                return;
            }

            getLog().info("发现 " + fileChanges.size() + " 个文件变更");

            // 执行代码分析
            getLog().info("开始代码分析...");
            ReviewResult result = codeAnalyzer.analyze(fileChanges);

            // 生成报告
            getLog().info("生成评审报告...");
            File outputDirectory = new File(outputDir);
            reportGenerator.generateReport(result, outputDirectory);

            getLog().info("代码评审完成！报告已生成到: " + outputDirectory.getAbsolutePath());

            // 输出总结信息
            getLog().info("评审总结:");
            getLog().info("- 总问题数: " + result.getTotalIssues());
            getLog().info("- 严重问题: " + result.getCriticalIssues());
            getLog().info("- 一般问题: " + result.getWarningIssues());
            getLog().info("- 建议优化: " + result.getInfoIssues());

        } catch (Exception e) {
            getLog().error("代码评审执行失败", e);
            throw new MojoExecutionException("代码评审失败: " + e.getMessage(), e);
        }
    }

    private void initServices() {
        this.gitService = new GitService();
        this.codeAnalyzer = new CodeAnalyzer();
        this.reportGenerator = new ReportGenerator();
    }
}