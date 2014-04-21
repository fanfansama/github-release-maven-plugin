github-release-maven-plugin
===========================


Permet de génerer un tag, une release et une branche sous github et le changelog

```
    <plugin>
        <groupId>com.stepinfo.plugin</groupId>
        <artifactId>github-release-maven-plugin</artifactId>
        <version>1.0-SNAPSHOT</version>
        <configuration>
            <tag>S01</tag>
            
            <wiki>MW</wiki>  <!-- format du changelog => MW : mediawiki, MD : markdown  -->
            
            <changelog>true</changelog>   <!-- generer le changelog apres la release -->
            
            <user>fanfansama</user>		<!-- par defaut, il cherche un fichier ${user.home}/.github  avec login et password ou oauth
            <password>xxx</password>
            
            <!-- <releaseSuffix>REL-</releaseSuffix> suffix du tagName par defaut REL- -> REL-{tag} -->
            
             <sha>19ebf63b8ca8b6021c6dabe845349aac14046945</sha> <!--pour taguer ailleurs que sur le HEAD, par defaut HEAD -->

            <repository>fanfansama/tpgit</repository>
            
          </configuration>
      </plugin>

```



utilisation :
* mvn github-release:release
* mvn github-release:changelog
