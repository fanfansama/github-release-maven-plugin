package com.stepinfo.github;

import com.google.common.base.Joiner;
import com.google.common.collect.IterablesPlus;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.GHBranch;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHRelease;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHTag;
import org.kohsuke.github.PagedIterable;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.collect.IterablesPlus.getFirst;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static com.stepinfo.github.Constante.*;
import static com.stepinfo.github.DecoratorHelper.TARGET_CHANGELOG;
import static com.stepinfo.github.DecoratorHelper.decorateBold;
import static com.stepinfo.github.DecoratorHelper.decorateLink;
import static com.stepinfo.github.DecoratorHelper.decoratePuce;
import static com.stepinfo.github.DecoratorHelper.decorateReleaseName;
import static com.stepinfo.github.DecoratorHelper.getWikiExtention;
import static com.stepinfo.github.IssueHelper.extractIssues;
import static org.kohsuke.github.GHIssue.Label;

@Slf4j
public class BranchHelper {

    private static Pattern MY_PATTERN = Pattern.compile("\\#\\d+");

    public static void processBranches(GHRepository repo, Collection<GHBranch> branchList, String suffixRelease, String target) throws IOException {

        if (!(target.equals(MD) || target.equals(MEDIAWIKI))) {
            throw new IOException("'" + target + "' est incorrect ! usage : " + MD + " " + MEDIAWIKI);
        }

        cleanTargetPlugin();

        Map<String, ReleaseTag> releaseTagMap = buildReleaseTagList(repo);
        Map issueLabelMap = extractIssues(repo);

        for (GHBranch branch : branchList) {
            String branchName = branch.getName();
            if (branchName.equals(MASTER) || branchName.startsWith(suffixRelease) || branchName.startsWith("francois")) {     // FIXME :
                log.debug("*********************************************** " + branch.getName() + " *******************");
                processBranch(repo, branch, releaseTagMap, issueLabelMap, target);
            }
        }
    }

    private static void cleanTargetPlugin() {
        File plugDir = createTargetDirPluglin();
        for (File f : plugDir.listFiles()) {
            f.delete();
        }
        plugDir.delete();

    }

    private static Map<String, String> extractTags(GHRepository repo) throws IOException {
        Map<String, String> tagMap = newHashMap();
        for (GHTag tag : repo.getTags()) {
            log.info("tag :" + tag.getName() + " sha:" + tag.getCommit().getSHA1());
            tagMap.put(tag.getName(), tag.getCommit().getSHA1());
        }
        return tagMap;
    }

    private static List<ReleaseTag> listRelease(GHRepository repo) throws IOException {
        List<ReleaseTag> list = newArrayList();
        for (GHRelease release : repo.getReleases()) {
            log.debug(">[release] name:" + release.getName() + " tag:" + release.getTagName());
            ReleaseTag custom = new ReleaseTag();
            custom.setReleaseName(release.getName());
            custom.setTagName(release.getTagName());
            list.add(custom);

            for (org.kohsuke.github.GHAsset asset : release.getAssets()) {
                log.debug("-asset:{}", asset.getName());
            }

        }
        return list;
    }

    private static Map<String, ReleaseTag> buildReleaseTagList(GHRepository repo) throws IOException {
        Map<String, ReleaseTag> map = newHashMap();
        Map<String, String> tagMap = extractTags(repo);
        List<ReleaseTag> releaseTags = listRelease(repo);

        for (ReleaseTag releaseTag : releaseTags) {
            String tagName = releaseTag.getTagName();
            releaseTag.setSha(tagMap.get(tagName));
            map.put(tagMap.get(tagName), releaseTag);
        }
        return map;
    }

    private static void processBranch(GHRepository repo, GHBranch branch,
                                      Map releaseTagMap,
                                      Map issueLabelMap,
                                      String wiki) throws IOException {
        Map<String, StringBuilder> commits = buildCommit(repo, branch.getName(), branch.getSHA1(), releaseTagMap, issueLabelMap, wiki);
        writeFile(branch.getName(), commits, wiki);
    }

    private static void writeFile(String name, Map<String, StringBuilder> commits, String wikiFormat) throws IOException {

        File plugDir = createTargetDirPluglin();

        List<String> order = orderTags(commits.keySet());
        for(String key : order){
            StringBuilder data = commits.get(key);

            File releaseDir = new File(plugDir.getAbsolutePath() + "/" + name + getWikiExtention(wikiFormat));
            if (!releaseDir.exists()) {
                releaseDir.createNewFile();
            }

            FileWriter fileWritter = new FileWriter(releaseDir.getAbsolutePath(), true);
            BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
            bufferWritter.write(decorateReleaseName(wikiFormat, key));
            bufferWritter.write(data.toString());
            bufferWritter.close();
        }
    }

    private static List<String> orderTags(Set<String> strings) {

        List<String> order = newArrayList(strings);
        Collections.sort(order, new Comparator<String>() {
            public int compare(String o1, String o2) {
                if(HEAD.equals(o1)){
                    return -1;
                } else if(HEAD.equals(o2)){
                    return 1;
                }
                return o1.compareTo(o2) * -1; // inversion la liste
            }
        });
        return order;
    }

    private static File createTargetDirPluglin() {
        File plugDir = new File(TARGET_CHANGELOG);
        if (!(plugDir.exists() && plugDir.isDirectory())) {
            plugDir.mkdirs();
        }
        return plugDir;
    }

    private static Map<String, StringBuilder> buildCommit(GHRepository repo, String branchName, String branchSha,
                                                          Map<String, ReleaseTag> releaseTagMap,
                                                          Map issueLabelMap,
                                                          String wiki) throws IOException {
        Map<String, StringBuilder> releaseMapBuffer = newHashMap();
        PagedIterable<GHCommit> commits;
        String lastTags;
        StringBuilder buf = new StringBuilder(SAUT_DE_LIGNE);
        commits = branchSha == null ? repo.listCommits() : repo.listCommitsSinceSha(branchSha);

        GHCommit head = getFirst(commits);

        if (getReleaseName(releaseTagMap, head) != null) {
            lastTags = getReleaseName(releaseTagMap, head);
        } else {
            lastTags = HEAD;
        }

        for (GHCommit commit : commits) {
            String current = getReleaseName(releaseTagMap, commit) ;
            if(!MASTER.equals(branchName) && current == lastTags){  // si la branch est vide
                break;
            }

            buf.append(formatCommitResumeLine(repo, commit, issueLabelMap, wiki));

            if (current != null && current != lastTags) {
                loggerCommitRelease(buf, current);
                lastTags = current;
                releaseMapBuffer.put(lastTags, buf);
                buf = new StringBuilder(SAUT_DE_LIGNE);
                if (!MASTER.equals(branchName)) {       // FIXME : detecter le master
                    break;
                }
            }
        }
        if(buf.toString().length() > 1){
            releaseMapBuffer.put(lastTags, buf);
            loggerCommitRelease(buf, lastTags);
        }
        return releaseMapBuffer;
    }

    private static String getReleaseName(Map<String, ReleaseTag> releaseTagMap, GHCommit commit) {
        ReleaseTag releaseTag = releaseTagMap.get(commit.getSHA1());
        return (releaseTag == null ? null : releaseTag.getReleaseName());
    }

    private static void loggerCommitRelease(StringBuilder buf, String titre) {
        log.debug(">>>>>>>>>>>>>>>>>>>>" + titre + "<<<<<<<<<<<<<<<");
        log.debug(buf.toString());
    }

    private static String formatCommitResumeLine(GHRepository repo,
                                                 GHCommit commit,
                                                 Map issueLabelMap,
                                                 String wiki) throws IOException {
        StringBuilder buf = new StringBuilder();

        extractAndFormatCommitDescription(repo, commit, issueLabelMap, wiki, buf);
        extractAndFormatCommitAuthor(commit, buf);
        extractAndFormatSha(repo, commit, wiki, buf);
        return decoratePuce(wiki, buf.toString());
    }

    private static void extractAndFormatCommitDescription(GHRepository repo,
                                                          GHCommit commit,
                                                          Map issueLabelMap,
                                                          String wiki,
                                                          StringBuilder buf) {
        if (commit.getCommitShortInfo() != null) {
            String libelle = commit.getCommitShortInfo().getMessage();
            arrangeCommitDescription(repo, libelle, issueLabelMap, wiki, buf);
        }
    }

    private static void extractAndFormatSha(GHRepository repo, GHCommit commit, String wiki, StringBuilder buf) {
        String cha = commit.getSHA1();
        buf.append(" (").append(decorateLink(wiki, cha.substring(0, 5), repo.getUrl() + "/commit/" + cha)).append(")");
    }

    private static void extractAndFormatCommitAuthor(GHCommit commit, StringBuilder buf) throws IOException {
        buf.append(" [");

        if (commit.getCommitShortInfo() != null && commit.getCommitShortInfo().getAuthor() != null) {
            buf.append(commit.getCommitShortInfo().getAuthor().getName());
        } else if (commit.getCommitter() != null) {
            buf.append(commit.getCommitter().getName());
        } else if (commit.getAuthor() != null) {
            buf.append(commit.getAuthor().getName());
        } else {
            throw new NoSuchFieldError("commiter non trouvé");
        }
        buf.append("]");

    }

    private static void arrangeCommitDescription(GHRepository repo, String title,
                                                 Map<String, Set<String>> issueLabelMap,
                                                 String wiki,
                                                 StringBuilder buf) {

        String string = title.split(SAUT_DE_LIGNE)[0];   // FIXME : Je ne prends que la premiere ligne
        Set<String> labels = newHashSet();
        Matcher m = MY_PATTERN.matcher(string);
        while (m.find()) {
            String s = m.group(0);
            labels.addAll(issueLabelMap.get(s));
            String urlIssue = performUrl(repo, wiki, s);
          //  log.info(s + " -> " + urlIssue);
            string = string.replace(s, urlIssue);
        }
        buf.append(string);
        buf.append(extractAndFormatLabels(labels, wiki));
    }

    private static StringBuilder extractAndFormatLabels(Set<String> labels, String wiki) {
        StringBuilder buf = new StringBuilder(" ");
        List<String> labs = newArrayList();
        Joiner joiner = Joiner.on(", ");

        for (String label : labels) {
            labs.add(decorateBold(wiki, label));
        }

        return joiner.appendTo(buf, labs);
    }

    private static String performUrl(GHRepository repo, String wiki, String libelle) {
        return decorateLink(wiki, libelle, repo.getUrl() + "/issues/" + libelle.replace("#", ""));
    }


}