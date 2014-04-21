package com.stepinfo.plugin;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.kohsuke.github.GHBranch;
import org.kohsuke.github.GHRepository;

import java.io.IOException;
import java.util.Map;

import static com.stepinfo.github.BranchHelper.processBranches;


@Mojo( name = "changelog" )
public class ChangeLogMojo extends AbstractConnect
{
    @Parameter(defaultValue = "")
    private String repository;

    @Parameter
    private String user;

    @Parameter
    private String password;

    @Parameter(defaultValue = "MD")
    private String wiki;


    @Parameter(defaultValue = "REL-")
    private String releaseSuffix;


    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            GHRepository repo = invoke(user, password, repository);

            getLog().info("Checking existing branch...");
            Map<String, GHBranch> branches = repo.getBranches();

                getLog().info("processing... changelog");
                processBranches(repo, branches.values(), releaseSuffix, wiki);
            getLog().info("generated !!");

        } catch (IOException ioe) {
            throw new MojoExecutionException(ioe.getMessage(), ioe);
        }
    }


}