package org.kohsuke.github;

import lombok.Getter;
import lombok.Setter;


@Getter @Setter
public class GHTag {
    private GHRepository owner;
    private GitHub root;

    private String name;
    private GHCommit commit;

    GHTag wrap(GHRepository owner) {
        this.owner = owner;
        this.root = owner.root;
        return this;
    }

    static GHTag[] wrap(GHTag[] tags, GHRepository owner) {
        for (GHTag tag : tags) {
            tag.wrap(owner);
        }
        return tags;
    }
}
