package com.codereviewer.service;

import com.codereviewer.model.FileChange;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class GitService {

    public List<FileChange> getChangedFiles(File projectDir, String baseBranch) throws Exception {
        List<FileChange> changes = new ArrayList<>();

        try (Repository repository = new FileRepositoryBuilder()
                .setGitDir(new File(projectDir, ".git"))
                .readEnvironment()
                .findGitDir()
                .build()) {

            Git git = new Git(repository);

            // 获取当前HEAD和基线分支的commit
            ObjectId headId = repository.resolve("HEAD");
            ObjectId baseId = repository.resolve(baseBranch);

            if (baseId == null) {
                throw new IllegalArgumentException("找不到基线分支: " + baseBranch);
            }

            try (RevWalk walk = new RevWalk(repository)) {
                RevCommit headCommit = walk.parseCommit(headId);
                RevCommit baseCommit = walk.parseCommit(baseId);

                // 创建TreeParser
                CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
                oldTreeIter.reset(repository.newObjectReader(), baseCommit.getTree());

                CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
                newTreeIter.reset(repository.newObjectReader(), headCommit.getTree());

                // 获取diff
                List<DiffEntry> diffs = git.diff()
                        .setOldTree(oldTreeIter)
                        .setNewTree(newTreeIter)
                        .call();

                for (DiffEntry diff : diffs) {
                    String filePath = diff.getNewPath();
                    if (filePath.equals("/dev/null")) {
                        filePath = diff.getOldPath();
                    }

                    // 只处理Java文件
                    if (filePath.endsWith(".java")) {
                        FileChange change = createFileChange(projectDir, diff);
                        if (change != null) {
                            changes.add(change);
                        }
                    }
                }
            }
        }

        return changes;
    }

    private FileChange createFileChange(File projectDir, DiffEntry diff) {
        try {
            String filePath = diff.getNewPath();
            if (filePath.equals("/dev/null")) {
                filePath = diff.getOldPath();
            }

            File file = new File(projectDir, filePath);
            String content = "";

            if (file.exists() && diff.getChangeType() != DiffEntry.ChangeType.DELETE) {
                content = new String(Files.readAllBytes(file.toPath()), "UTF-8");
            }

            FileChange change = new FileChange();
            change.setFilePath(filePath);
            change.setChangeType(diff.getChangeType().name());
            change.setContent(content);

            return change;

        } catch (Exception e) {
            System.err.println("处理文件变更失败: " + e.getMessage());
            return null;
        }
    }
}