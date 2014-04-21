package com.stepinfo.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.kohsuke.github.GHBranch;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import java.io.IOException;
import java.util.Map;

import static com.stepinfo.github.BranchHelper.processBranches;
import static com.stepinfo.github.ReleaseHelper.makeARelease;


/**
 * @requiresProject false
 */
@Mojo(name = "release")
public class ReleaseMojo extends AbstractConnect {

    public static final String TRUE = "true";
    /**
     * Tag, release and make a branch
     */

    @Parameter
    private String tag;

    @Parameter(defaultValue = "REL-")
    private String releaseSuffix;

    @Parameter(defaultValue = "")
    private String repository;

    @Parameter
    private String user;

    @Parameter
    private String password;

    @Parameter(defaultValue = "false")
    private String changelog;

    @Parameter(defaultValue = "MD")
    private String wiki;

    @Parameter(defaultValue = "HEAD")
    private String sha;

    public void execute() throws MojoExecutionException, MojoFailureException {
        try {

            GHRepository repo = invoke(user, password, repository);
            String releaseName = releaseSuffix + tag;

            getLog().info("Checking existing branch...");
            Map<String, GHBranch> branches = repo.getBranches();

            getLog().info("processing... Tag:" + tag + " Release:" + releaseName);
            makeARelease(repo, tag.toString(), releaseName, sha);

            getLog().info("changelog:"+changelog);
            if (TRUE.equals(changelog)) {
                getLog().info("processing... changelog");
                processBranches(repo, branches.values(), releaseSuffix, wiki);
            }
            getLog().info("released !!");

        } catch (IOException ioe) {
            throw new MojoExecutionException(ioe.getMessage(), ioe);
        }
    }
}

