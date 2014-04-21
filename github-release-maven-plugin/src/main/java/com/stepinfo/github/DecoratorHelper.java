package com.stepinfo.github;

import lombok.extern.slf4j.Slf4j;

import static com.stepinfo.github.Constante.FILE_MD;
import static com.stepinfo.github.Constante.FILE_MEDIAWIKI;
import static com.stepinfo.github.Constante.MD;
import static com.stepinfo.github.Constante.MEDIAWIKI;
import static com.stepinfo.github.Constante.SAUT_DE_LIGNE;


@Slf4j
public  class DecoratorHelper {

    public static final String TARGET_CHANGELOG = "target/changelog";

    public static String decorateLink(String wiki , String text, String url){
        String deco = "";

        switch (wiki){
            case MD :
                deco = "[" + text + "](" + url + ")";
                break;
            case MEDIAWIKI :                // [http://fr.wikipedia.org/wiki/Git Git]
                deco = "[" + url + " " + text + "]";
                break;
            default:
                deco = "(" + text + ")";
        }

        return deco ;
    }

    public static String decorateReleaseName(String wiki , String text){
        String deco = "";

        switch (wiki){
            case MD :
                deco = "# " + text ;
                break;
            case MEDIAWIKI :
                deco = "=== " + text + " ===" ;
                break;
            default:
                deco = text;
        }

        return new StringBuilder()
                .append(SAUT_DE_LIGNE)
                .append(SAUT_DE_LIGNE)
                .append(deco)
                .append(SAUT_DE_LIGNE)
                .append(SAUT_DE_LIGNE).toString();
    }

    public static String decoratePuce(String wiki , String text){
        String deco = "";

        switch (wiki){
            case MD :
                deco = "* " + text ;
                break;
            case MEDIAWIKI :
                deco = "* " + text ;
                break;
            default:
                deco=text;
        }

        return deco + SAUT_DE_LIGNE;
    }

    public static String decorateBold(String wiki , String text){
        String deco = "";

        switch (wiki){
            case MD :
                deco = "**" + text + "**" ;
                break;
            case MEDIAWIKI :
                deco = "'''" + text + "'''" ;
                break;
            default:
                deco=text;
        }

        return deco ;
    }

    public static String getWikiExtention(String wiki){
        switch (wiki){
            case MD :
                return FILE_MD;
            case MEDIAWIKI :
                return FILE_MEDIAWIKI;
            default:
                log.error("Le format n'est pas prit en compte {}", wiki);
                return FILE_MD;
        }
    }

}
