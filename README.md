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

            <!-- format du changelog => MW : mediawiki, MD : markdown  -->
            <wiki>MW</wiki>

            <!-- generer le changelog apres la release -->
            <changelog>true</changelog>

            <!-- par defaut, il cherche un fichier ${user.home}/.github  avec login et password ou oauth
            <user>fanfansama</user>
            <password>xxx</password>
            
            <releaseSuffix>REL-</releaseSuffix> <!-- suffix du tagName par defaut REL- -> REL-{tag} -->

            <!--pour taguer ailleurs que sur le HEAD, par defaut HEAD
            <sha>19ebf63b8ca8b6021c6dabe845349aac14046945</sha>
            -->
            <repository>fanfansama/tpgit</repository>
            
          </configuration>
      </plugin>

```

# Authentification

```
créer un fichier : ${user.home}/.github
ajouter les lignes :
login=<login>
password=<password>
```

```
ou utiliser les parametres du plugin
```

# Utilisation

* mvn github-release:release
* mvn github-release:changelog
