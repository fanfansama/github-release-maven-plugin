package com.stepinfo.github;

import com.google.common.collect.Sets;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHRepository;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Maps.newHashMap;

public class IssueHelper {

    public static Map<String, Set<String>> extractIssues(GHRepository repository) throws IOException {
        Map<String, Set<String>> map = newHashMap();
        for(GHIssue issue : repository.getIssues(GHIssueState.OPEN)){
            map.put("#"+issue.getNumber(),convert(issue.getLabels()));
        }

        for(GHIssue issue : repository.getIssues(GHIssueState.CLOSED)){
            map.put("#"+issue.getNumber(),convert(issue.getLabels()));
        }

        return map;
    }

    private static Set<String> convert(Collection<GHIssue.Label> labels){
        Set<String> list = Sets.newHashSet();

        for(GHIssue.Label label : labels){
            list.add(label.getName());
        }

        return list;
    }

}
