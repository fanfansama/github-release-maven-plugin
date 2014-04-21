package com.stepinfo.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import java.io.IOException;

import static com.google.common.base.Strings.isNullOrEmpty;

abstract class  AbstractConnect extends AbstractMojo {

    public GHRepository invoke(String user, String password, String repository) throws IOException, MojoExecutionException {
            getLog().info("Connecting... to github");

            GitHub github;
            try {
                github = GitHub.connect();
            } catch (IOException ioe) {
                getLog().warn(ioe.getMessage());
                getLog().info("try with account " + user);
                github = GitHub.connectUsingPassword(user.toString(), password.toString());
            }

            if(isNullOrEmpty(repository.toString())){
                throw new MojoExecutionException("'repository' can not be empty");
            }

            getLog().info("Connecting... to github:" + repository.toString());
            //   GHRepository repo = github.getRepository("StepInfo/forfait-svp-epaye");//"fanfansama/tpmaven");
            return github.getRepository(repository.toString());
        }


}