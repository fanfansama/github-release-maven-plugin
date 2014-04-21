package com.stepinfo.github;


import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.GHBranch;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHMilestone;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.stepinfo.github.BranchHelper.processBranches;
import static com.stepinfo.github.ReleaseHelper.makeARelease;

@Slf4j
public class Essai {
                                 // String[] args
    public static void main() throws IOException {

        GitHub github = GitHub.connectUsingPassword("fanfansama", "secret+2k13");

     //   GHRepository repo = github.getRepository("StepInfo/forfait-svp-epaye");//"fanfansama/tpmaven");
        GHRepository repo = github.getRepository("fanfansama/tpgit");

        java.util.Map<String, GHBranch> branches = repo.getBranches();

        log.debug("master:{}" , repo.getMasterBranch());
        log.debug("fork:{}" , repo.isFork());
        log.debug("issues exists:{}" , repo.hasIssues());
        log.debug("wiki exists:{}", repo.hasWiki());
        log.debug("transport {}" ,repo.getGitTransportUrl());
        log.debug("url {}", repo.getUrl());

        extractRefs(repo);
        extractHooks(repo);
        listBranches(branches);


   //     makeARelease(repo, "S05", "REL-S05") ;


        //OK  repo.createMilestone("M01", "ceci est un test API") ;
        //   extractMileStone(repo, org.kohsuke.github.GHIssueState.OPEN);
        //   extractMileStone(repo, org.kohsuke.github.GHIssueState.CLOSED);

        //    processBranches(repo, branches.values());




    }

    private static void listBranches(Map<String, GHBranch> branches) {
        for (String br : branches.keySet()) {
            GHBranch branch = branches.get(br);
            log.debug(">[branch] name:{} sha:{}" , branch.getName(), branch.getSHA1());

        }
    }

    private static void extractRefs(GHRepository repo) throws IOException {
        for(org.kohsuke.github.GHRef ref : repo.getRefs()){
            log.debug("ref:{}", ref.getRef());
        }
    }

    private static void extractHooks(GHRepository repo) throws IOException {
        try{
            if(repo.getHooks() !=null) {
                for(org.kohsuke.github.GHHook ref : repo.getHooks()){
                    log.debug("hook:{}", ref.getName());
                }
            }
        } catch(java.io.FileNotFoundException fnfe){
            log.info("no hook found");
        }
    }

    private static void extractMileStone(GHRepository repo, GHIssueState state) throws IOException {
        for (GHMilestone milestone : repo.listMilestones(state)) {
            log.debug("[milestone]#" + milestone.getNumber()
                    + " state:" + milestone.getState().name()
                    + " title:" + milestone.getTitle()
                    + " open:" + milestone.getOpenIssues()
                    + " close:" + milestone.getClosedIssues()
                    //   + " desciption:'" + milestone.getDescription() + "'"
            );
            // extractIssue(repo, milestone, org.kohsuke.github.GHIssueState.OPEN);
            // extractIssue(repo, milestone, org.kohsuke.github.GHIssueState.CLOSED);
            extractIssue(repo, milestone, state);

        }
    }

    private static void extractIssue(GHRepository repo, GHMilestone milestone, GHIssueState state) throws IOException {
        java.util.List<org.kohsuke.github.GHIssue> issues = repo.getIssues(state, milestone);
        int ctr=0;
        for (org.kohsuke.github.GHIssue iss : issues) {
            StringBuilder buf = new StringBuilder();
            for (org.kohsuke.github.GHIssue.Label label : iss.getLabels()) {
                buf.append(label.getName()).append(",");
            }

            log.debug("[issue]#" + iss.getNumber()
                    + " [" + buf.toString() + "]"
                    + " " + iss.getState().name()
                    + " " + formatTitleIssue(iss.getTitle())
                    + " Mil:'" + iss.getMilestone().getTitle() + "'"
            );

            if((ctr++)>10)break;
        }
    }

    private static String formatTitleIssue(String string) {
        return string.split("- fiche ")[0];

    }


}
