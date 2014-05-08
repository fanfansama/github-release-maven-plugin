package com.stepinfo.github;

import org.kohsuke.github.GHRefBuilder;
import org.kohsuke.github.GHRelease;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHTag;

import java.io.IOException;

public class ReleaseHelper {

    public static void makeARelease(GHRepository repo,
                                    String tagName,
                                    String branchSuffix,
                                    String releaseName,
                                    String sha,
                                    boolean makeNewBranch) throws IOException {

        GHRelease release = repo.createRelease(tagName)
                .name(releaseName)
                .prerelease(false)
                .commitish( "HEAD".equals(sha) ? null : sha )
                .create();

        if(makeNewBranch){
            for(GHTag tag : repo.getTags()){
                if(tagName.equals(tag.getName())){
                    String ash = tag.getCommit().getSHA1();
                    new GHRefBuilder(repo, branchSuffix+releaseName, ash ).create();
                    return;
                }
            }
            throw new IOException("tag :" + tagName + " not found");
        }
    }

}
