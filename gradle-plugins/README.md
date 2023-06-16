# Gradle Mixins

NOTE: This readme is generated from the plugin source files.
Do not edit directly.

## README.md Construction

The README.md is constructed from the plugin source and uses snippet files to piece the content together.
The snippet files are located in the `src/main/resource/gradle` folder.
The `toptick` and `bottomtick` files bracket the plugin code with Markdown code blocks.

The `header` file provides the header formatting, to which the plugin filename is appended.
Note, the `header` file **MUST NOT** have a trailing newline character.
If you edit the file with `vi` or Intellij, they will add the trailing newline.
In this situation, it is simple to reconstruct the `header` file:

```bash
% (echo && echo -n "## ") > plugins/src/main/resources/gradle/header
```

## Mixin Plugins

The mixins are meant to provide snippets of Gradle configuration based on specific functional groupings.
The groups are identified as starter scripts, and roughly grouped:

* java - basic configuration around a typical build objective
* metrics - build timing configuration
* std - top-level configs based on target type

The `std` level of configuration is organized by artifact type.
This is the only level meant to organize or include other configs.
The design is meant to be easily overridden by teams that wish to depart from the standard configs.
Teams are able to just copy the `std` level conventions to their project and override those they wish to change.
Items they don't need to change can continue to be referred in their local configs.

The intention is that the `starter-boot` package can externalize these mixins as plugins so the team can continue to refer to those mixins that do not need to change.
To that end, do not include or build upon other convention files, except at the `std` level.

I think the only exception to this rule is `starter.java.style-conventions`, which includes `starter.java.checktyle-conventions`.
This was only allowed because `style-conventions` is aggregating `checkstyle` and `spotless`.
If 'spotless' gets more complicated, then these two should be split and propagated upwards instead of being aggregated under `style-conventions`.



## starter.java.build-javatarget-conventions.gradle

```groovy
plugins {
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(16)
    }
}
```

## starter.java.build-plugintarget-conventions.gradle

```groovy
plugins {
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(11)
    }
}
```

## starter.java.build-springboot-conventions.gradle

```groovy
plugins {
    id "org.springframework.boot" apply true
}

// starter.java.build-javatarget-conventions

tasks.named('distZip'){
    enabled = false
}

tasks.named('distTar'){
    enabled = false
}

tasks.named('bootDistZip'){
    enabled = false
}

tasks.named('bootDistTar'){
    enabled = false
}
```

## starter.java.build-utils-conventions.gradle

```groovy
/**
 * Tasks for debugging build problems
 * - printSourceSetInformation  outputs source set content and classpath info for each type
 */

tasks.register('printSourceSetInformation'){
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    description = "Show source set definitions"
    doLast{
        sourceSets.each { srcSet ->
            println "["+srcSet.name+"]"
            print "-->Source directories: "+srcSet.allJava.srcDirs+"\n"
            print "-->Output directories: "+srcSet.output.classesDirs.files+"\n"
            print "-->Compile classpath:\n"
            srcSet.compileClasspath.files.each {
                print "  "+it.path+"\n"
            }
            println ""
        }
    }
}
```

## starter.java.build-utils-copyright-conventions.gradle

```groovy
/**
 * Tasks for maintaining copyright dates
 * - updateCopyrights  scans modified files for copyright string and updates to current year
 */

plugins {
}
// Requires
// id 'starter.java.build-utils-fileset-conventions'

tasks.register('updateCopyrights') {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    description = "Update the copyright dates for any files which have been modified"
    onlyIf { gitPresent && !System.getenv('GITHUB_ACTION') }
    if (gitPresent) {
        def extensions = [".java", ".kt"]
        inputs.files(filterProjectFiles(modifiedFiles, extensions))
    }
    outputs.dir('build')

    doLast {
        def now = Calendar.instance.get(Calendar.YEAR) as String
        inputs.files.each { file ->
            def line
            file.withReader { reader ->
                while (line = reader.readLine()) {
                    def matcher = line =~ /Copyright (20\d\d)-?(20\d\d)?/
                    if (matcher.count) {
                        def beginningYear = matcher[0][1]
                        if (now != beginningYear && now != matcher[0][2]) {
                            def years = "$beginningYear-$now"
                            def sourceCode = file.text
                            sourceCode = sourceCode.replaceFirst(/20\d\d(-20\d\d)?/, years)
                            file.write(sourceCode)
                            println "Copyright updated for file: $file"
                        }
                        break
                    }
                }
            }
        }
    }
}
```

## starter.java.build-utils-fileset-conventions.gradle

```groovy
/**
 * Tasks for maintaining copyright dates
 * - updateCopyrights  scans modified files for copyright string and updates to current year
 */

ext {
    /**
     * Filter files based on an array of allowable extensions.
     */
    filterFiles = { FileCollection fileSet, extensions ->
        return fileSet.filter { f -> extensions.any { e -> f.name.endsWith(e) } }
    }
    /**
     * Filter files based on an array of allowable extensions that also are in the local sub-module.
     */
    filterProjectFiles = { FileCollection fileSet, extensions ->
        return filterFiles(fileSet.filter { f -> f.path.contains(project.name) }, extensions)
    }

}

```

## starter.java.build-utils-git-conventions.gradle

```groovy
/**
 * Tasks for maintaining copyright dates
 * - updateCopyrights  scans modified files for copyright string and updates to current year
 */
plugins {
    id 'org.ajoberstar.grgit'
}

ext {
    gitPresent = new File('.git').exists()
    if (gitPresent) {
        modifiedFiles = files(grgit.status().unstaged.modified)
    }
}

```

## starter.java.build-utils-property-conventions.gradle

```groovy

ext {
    /**
     * Utility function for choosing between a team-defined configuration and a default core-define value.
     *
     * @param value variable (or null)
     * @param defaultValue return value if null
     * @return one or the other value
     */
    getValueOrDefault = { String value, String defaultValue ->
        return !value ? defaultValue : value;
    }

    /**
     * Utility function for choosing between a team-defined configuration and a default core-define value.
     *
     * @param value variable name (as String)
     * @param defaultValue return value if null
     * @return one or the other value
     */
    getPropertyOrDefault = { String propertyName,  defaultValue ->
        return project.hasProperty(propertyName) ? project.properties[propertyName] : defaultValue;
    }

    /**
     * Utility function for choosing between a team-defined configuration and a default core-define value.
     *
     * @param tagName environment variable name (or null)
     * @param defaultValue return value if environment value is null or doesn't exist
     * @return environment value or default
     */
    getEnvOrDefault = { String tagName, String defaultValue ->
        String ref = System.getenv(tagName)
        return !ref ? defaultValue : ref;
    }
}

```

## starter.java.config-conventions.gradle

```groovy
/**
 * Provides configurations for platform (BOM) dependencies, to ensure non-api/runtime configurations are set properly.
 */

configurations {
    springBom
    compileOnly.extendsFrom(springBom)
    annotationProcessor.extendsFrom(springBom)
    implementation.extendsFrom(springBom)
    testAnnotationProcessor.extendsFrom(springBom)

    annotationBom
    implementation.extendsFrom(annotationBom)
    annotationProcessor.extendsFrom(annotationBom)
    testAnnotationProcessor.extendsFrom(annotationBom)

    checkstyleRules
}

```

## starter.java.container-conventions.gradle

```groovy
/**
 * Provides docker container settings
 */

plugins {
    id 'base'
    id 'com.palantir.docker'
    id 'com.palantir.docker-run'
    id 'com.palantir.docker-compose'
}
// Requires
// id 'starter.java.build-utils-property-conventions'

ext {
    dockerRegistry = project.hasProperty("dockerRegistry") ? "${project.dockerRegistry}" : "${group}"
    dockerImageVersion = project.hasProperty("buildNumber") ? "${project.version}-${project.buildNumber}" : project.version
}

docker {
    dependsOn(assemble)
    name "${dockerRegistry}/${rootProject.name}"
    tag "Build", "${dockerRegistry}/${rootProject.name}:${dockerImageVersion}"
    tag "Latest", "${dockerRegistry}/${rootProject.name}:latest"
    noCache true
    dockerfile file('src/docker/Dockerfile')
}

dockerRun {
    name project.name
    image "${dockerRegistry}/${rootProject.name}"
    ports '8080:8080'
    env 'SECRETHUB_HELLO': getEnvOrDefault('SECRETHUB_HELLO', 'override-me')
}

dockerCompose {
    dockerComposeFile 'src/docker/docker-compose.yml'
}

def dockerStart = tasks.register('dockerStart', DefaultTask) {
    dependsOn "dockerPrune", "docker", "dockerRun"
}

def dockerPrune = tasks.register('dockerPrune', DefaultTask) {
    mustRunAfter 'dockerStop', 'dockerRemoveContainer'
    dependsOn 'dockerPruneContainer', 'dockerPruneImage'
}

def dockerPruneContainer = tasks.register('dockerPruneContainer', Exec) {
    executable "docker"
    args "container", "prune", "-f"
}

def dockerPruneImage = tasks.register('dockerPruneImage', Exec) {
    executable "docker"
    args "image", "prune", "-f"
}

def dockerPruneVolume = tasks.register('dockerPruneVolume', Exec) {
    executable "docker"
    args "volume", "prune", "-f"
}

def dcPrune = tasks.register('dcPrune', DefaultTask) {
    mustRunAfter('dockerComposeDown')
    dependsOn 'dockerPruneContainer', 'dockerPruneImage'
}

def dcPruneVolume = tasks.register('dcPruneVolume', DefaultTask) {
    mustRunAfter('dockerComposeDown')
    dependsOn 'dockerPruneVolume'
}

def lintDockerfile = tasks.register('lintDockerfile', DefaultTask) {
    def binary = "hadolint"
    ext.targets = [ "src/docker/Dockerfile" ]
    def taskTimeout = 10000L
    def result = 0
    def sout = new StringBuilder(), serr = new StringBuilder()
    doLast {
        ext.targets.each { f ->
            def cmdLine = "${binary} ${f}"
            def proc = cmdLine.execute(null, project.projectDir)
            proc.consumeProcessOutput(sout, serr)
            proc.waitForOrKill(taskTimeout)
            result |= proc.exitValue()
        }
        if (result != 0 && serr) {
            ant.fail(serr) }
        else if (result != 0) {
            ant.fail(sout)
        }
    }
}

tasks.named("dockerRemoveContainer").configure {
    mustRunAfter('dockerStop')
}

tasks.named("dockerComposeUp").configure {
    dependsOn tasks.named("docker")
}

tasks.named("dockerRun").configure {
    dependsOn tasks.named("docker")
}

tasks.named("check").configure {
    dependsOn('lintDockerfile')
}```

## starter.java.container-spring-conventions.gradle

```groovy
/**
 * Provides docker container settings
 */

plugins {
}
// Requires
// id 'starter.java.container-conventions'


docker {
    files "build/libs/${bootJar.archiveFileName.get()}", "bin/entrypoint.sh"
    buildArgs([JAR_FILE: bootJar.archiveFileName.get(), ENTRYPOINT_FILE: "entrypoint.sh"])
}

dockerRun {
    env 'SECRETHUB_HELLO': getEnvOrDefault('SECRETHUB_HELLO', 'override-me'),
            'JAVA_PROFILE': '-Dspring.profiles.include=docker'
}

```

## starter.java.deps-build-conventions.gradle

```groovy
/**
 * Provides a set of common dependencies for typical build.
 * Includes proper dependencies for lombok and mapstruct annotation processing.
 */

// starter.java.build-javatarget-conventions

dependencies {
    implementation 'com.fasterxml.jackson.datatype:jackson-datatype-jsr310'

    implementation "org.zalando:problem-spring-web"
    implementation "org.springdoc:springdoc-openapi-ui"
    implementation "org.springdoc:springdoc-openapi-webmvc-core"
    //implementation "org.springdoc:springdoc-openapi-security"
    implementation "org.springdoc:springdoc-openapi-data-rest"
    implementation 'org.mapstruct:mapstruct'

    compileOnly 'org.projectlombok:lombok'

    annotationProcessor 'org.projectlombok:lombok'
    annotationProcessor 'org.mapstruct:mapstruct-processor'

}

```

## starter.java.deps-integration-conventions.gradle

```groovy
/**
 * Provides a set of common dependencies for typical integration test.
 */

// starter.java.build-javatarget-conventions

dependencies {

    integrationTestImplementation 'org.assertj:assertj-core'
    integrationTestImplementation 'org.junit.jupiter:junit-jupiter-api'
    integrationTestImplementation('org.springframework.boot:spring-boot-starter-test') {
        exclude group:'org.junit.vintage', module: 'junit-vintage-engine'
    }

    integrationTestRuntimeOnly 'org.junit.jupiter:junit-jupiter-engine'
}

```

## starter.java.deps-open-tracing-common-conventions.gradle

```groovy
/**
 * Typical dependencies to implement open tracing.
 */

dependencies {
    // Tracing support ==========================================================
    api 'io.opentracing.brave:brave-opentracing'
    api 'io.opentracing:opentracing-api'
    api 'io.zipkin.reporter2:zipkin-reporter'
    api 'io.zipkin.reporter2:zipkin-sender-okhttp3'
}
```

## starter.java.deps-plugin-conventions.gradle

```groovy
/**
 * Provides a set of common dependencies for typical gradle plugin unit test .
 */

// starter.java.build-javatarget-conventions

dependencies {
    testImplementation('org.spockframework:spock-core') {
        exclude module: 'groovy-all'
    }
}

```

## starter.java.deps-plugin-integration-conventions.gradle

```groovy
/**
 * Provides a set of common dependencies for typical gradle plugin integration test .
 */

// starter.java.build-javatarget-conventions

dependencies {
    integrationTestImplementation('org.spockframework:spock-core') {
        exclude module: 'groovy-all'
    }
    integrationTestImplementation gradleTestKit()
}

```

## starter.java.deps-test-conventions.gradle

```groovy
/**
 * Provides a set of common dependencies for typical unit testing.
 */
plugins {
    id 'java'
}

// starter.java.build-javatarget-conventions

dependencies {
    testImplementation 'org.mockito:mockito-core'
    testCompileOnly 'org.projectlombok:lombok'
    testAnnotationProcessor 'org.projectlombok:lombok'

    testImplementation 'com.tngtech.archunit:archunit'
    testImplementation 'org.assertj:assertj-core'
    testImplementation 'org.junit.jupiter:junit-jupiter-api'
    //since this is a spring boot starter, assume spring at all levels
    testImplementation('org.springframework.boot:spring-boot-starter-test') {
        exclude group:'org.junit.vintage', module: 'junit-vintage-engine'
    }
    // Use JUnit Jupiter Engine for testing.
    testRuntimeOnly 'org.junit.jupiter:junit-jupiter-engine'

}

```

## starter.java.doc-asciidoc-conventions.gradle

```groovy
/**
 * Swaggerhub configurations
 */

plugins {
    id 'org.asciidoctor.jvm.pdf'
    id 'org.asciidoctor.jvm.gems'
    id 'org.asciidoctor.jvm.convert'
}

configurations {
    docs
}

dependencies {
    docs "io.spring.docresources:spring-doc-resources:${doc_resources_version}@zip"
}


tasks.register('prepareAsciidocBuild', type: Sync) {
    dependsOn configurations.docs
    from {
        configurations.docs.collect { zipTree(it) }
    }
    from 'docs/src/main/asciidoc/','docs/src/main/java','docs/src/main/kotlin'
    into "$buildDir/asciidoc"
}

asciidoctorPdf {
    dependsOn prepareAsciidocBuild
    baseDirFollowsSourceFile()
    configurations 'asciidoctorExt'

    asciidoctorj {
        sourceDir "$buildDir/asciidoc"
        inputs.dir(sourceDir)
        sources {
            include 'index.adoc'
        }
        options doctype: 'book'
        attributes 'icons': 'font',
                'sectanchors': '',
                'sectnums': '',
                'toc': '',
                'source-highlighter' : 'coderay',
                revnumber: project.version,
                'project-version': project.version
    }
}

asciidoctorj {
    version = '2.4.1'
    // fatalWarnings ".*"
    options doctype: 'book', eruby: 'erubis'
    attributes([
            icons: 'font',
            idprefix: '',
            idseparator: '-',
            docinfo: 'shared',
            revnumber: project.version,
            sectanchors: '',
            sectnums: '',
            'source-highlighter': 'highlight.js',
            highlightjsdir: 'js/highlight',
            'highlightjs-theme': 'googlecode',
            stylesdir: 'css/',
            stylesheet: 'stylesheet.css',
            'spring-version': project.version,
            'project-version': project.version,
            'java-examples': 'gov/va/starter/boot/jdocs',
            'kotlin-examples': 'gov/va/starter/boot/kdocs'
    ])
}

asciidoctor {
    dependsOn asciidoctorPdf
    baseDirFollowsSourceFile()
    configurations 'asciidoctorExt'
    sourceDir = file("$buildDir/asciidoc")
    sources {
        include '*.adoc'
    }
    resources {
        from(sourceDir) {
            include 'images/*', 'css/**', 'js/**'
        }
    }

}

tasks.register('reference', dependsOn: asciidoctor) {
    group = 'Documentation'
    description = 'Generate the reference documentation'
}
```

## starter.java.doc-markdown-conventions.gradle

```groovy
/**
 * Swaggerhub configurations
 */

plugins {
    id 'base'
}
// Requires
// id 'starter.java.build-utils-git-conventions'
// id 'starter.java.build-utils-fileset-conventions'


tasks.register('updateMarkdownToc') {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    description = "(re)builds table of contents for Markdown documentation"
    onlyIf { gitPresent && !System.getenv('GITHUB_ACTION') }
    if (gitPresent) {
        def extensions = [ '.md' ]
        inputs.files(filterProjectFiles(modifiedFiles, extensions))
    }
    //outputs.dir('build')
    outputs.upToDateWhen { false }

    doLast {
        StringBuilder files = new StringBuilder()

        inputs.files.each { f -> files.append(" ").append(f) }
        def cmdLine = "${project.rootDir}/scripts/generate-toc.sh --update ${files.toString()}  "
        logger.debug("[{}]: {}", project.projectDir, cmdLine)
        def proc = cmdLine.execute(null, project.projectDir)

        proc.in.eachLine { line -> logger.quiet(line) }
        proc.out.close()
        proc.waitFor()
        logger.quiet("Exit code: [{}]", proc.exitValue())
    }
}

tasks.named('build').configure {
    dependsOn tasks.named('updateMarkdownToc')
}
```

## starter.java.doc-mkdocs-conventions.gradle

```groovy
/**
 * Swaggerhub configurations
 */

tasks.register('deployDocToGithubPages', Exec) {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    description = "use mkdocs to generate static site and commit to gh-pages branch"
    executable('mkdocs')
    args('gh-deploy', "--clean")
}

tasks.register('serveDocs', Exec) {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    description = "use mkdocs to serve documents locally"
    executable('mkdocs')
    args('serve', "-f", "../mkdocs.yml")
}
```

## starter.java.doc-springdoc-conventions.gradle

```groovy
/**
 * plugins supporting generation of OpenAPI docs from code
 */

plugins {
    id "com.github.johnrengelman.processes"
    id "org.springdoc.openapi-gradle-plugin"
}

```

## starter.java.doc-swagger-conventions.gradle

```groovy
/**
 * Swaggerhub configurations
 */

plugins {
    id "io.swagger.swaggerhub"
}

```

## starter.java.lint-checkstyle-conventions.gradle

```groovy
/**
 * Setting for running checkstyle, pulls configuration from the checkstyle jar.
 */

plugins {
    // Apply the java Plugin to add support for Java.
    id 'checkstyle'
}

configurations {
    checkstyleRules
}

dependencies {
    checkstyleRules platform('gov.va.starter:checkstyle-bom')
    checkstyleRules 'gov.va.starter:checkstyle'
}

checkstyle {
    toolVersion "${checkstyle_version}"
//    configFile = rootProject.file('settings/checkstyle/checkstyle.xml')
    config project.resources.text.fromArchiveEntry(configurations.checkstyleRules, 'settings/checkstyle/checkstyle.xml')
    configProperties = [
            'checkstyle.cache.file': "${buildDir}/checkstyle.cache",
    ]
    ignoreFailures = true
    showViolations = true

}

checkstyleMain {
    source = "src/main/java"
}
checkstyleTest {
    source = "src/test/java"
}

```

## starter.java.lint-shellcheck-conventions.gradle

```groovy
/**
 * Top-level configuration of all the typical standard configurations for a gradle plugin
 */

plugins {
    id 'base'
    id 'com.felipefzdz.gradle.shellcheck'
}


shellcheck {
    sources = files(".")
    ignoreFailures = true
    showViolations = true
    shellcheckVersion = "${shellcheck_version}"
    useDocker = false
    shellcheckBinary = "shellcheck"
    severity = "style" // "error"
}

tasks.named('shellcheck').configure {
    reports {
        xml.enabled = false
        txt.enabled = false
        html.enabled = true
    }
}

check.configure {
    dependsOn tasks.named('shellcheck')
}
```

## starter.java.lint-spotless-conventions.gradle

```groovy
/**
 * Configuration for spotless code formatting
 */

plugins {
    id "com.diffplug.spotless"
}

spotless {
    java {
        googleJavaFormat()
    }
}
```

## starter.java.publish-bootjar-conventions.gradle

```groovy
/**
 * Configuration for publishing fat jar for spring-boot application
 */

plugins {
    id 'maven-publish'
}

publishing {
    publications {
        bootJarArtifact(MavenPublication) {
            artifact bootJar
        }
    }
}
```

## starter.java.publish-jar-conventions.gradle

```groovy
/**
 * Configurations for publishing jar files
 */

plugins {
    id 'maven-publish'
}

publishing {
    publications {
        libJarArtifact(MavenPublication) {
            from components.java
        }
    }
}
```

## starter.java.publish-pom-conventions.gradle

```groovy
/**
 * Configurations for publishing BOM packages (java-platform).
 */

plugins {
    id 'maven-publish'
}

publishing {
    publications {
        platformJarArtifact(MavenPublication) {
            from components.javaPlatform
        }
    }
}

```

## starter.java.publish-repo-conventions.gradle

```groovy
/**
 * Definition of publishing repository for any packages
 */

plugins {
    id 'maven-publish'
}

publishing {
    repositories {
        maven {
            name = "githubPackages"
            // change to point to your repo, e.g. http://my.org/repo
            def releasesRepoUrl = "${mavenRepository}"
            def snapshotsRepoUrl = "${mavenSnapshotRepository}"
            url = version.endsWith('SNAPSHOT') ? snapshotsRepoUrl : releasesRepoUrl
            credentials(PasswordCredentials)
        }
    }
}
```

## starter.java.release-conventions.gradle

```groovy
/**
 * Configurations for axion-release-plugin.
 */

plugins {
    id 'pl.allegro.tech.build.axion-release'
}
// Requires
// id 'starter.java.build-utils-property-conventions'

scmVersion {

    repository {
        // doc: Repository
        type = 'git' // type of repository
        directory = project.rootProject.file('./') // repository location
        remote = 'origin' // remote name

        // doc: Authorization
        customKey = getEnvOrDefault('GIT_AUTH_KEY', "customKeyDefault")
        customKeyPassword = getEnvOrDefault('GIT_SECRET_KEY', "secretKeyDefault") // key password
    }

    // doc: Dry run
//    localOnly = true // never connect to remote

    // doc: Uncommitted changes
    ignoreUncommittedChanges = false // should uncommitted changes force version bump

    // doc: Version / Tag with highest version
    useHighestVersion = true // Defaults as false, setting to true will find the highest visible version in the commit tree

    // doc: Version / Sanitization
    sanitizeVersion = true // should created version be sanitized, true by default

    // doc: Basic usage / Basic configuration
//    foldersToExclude = ['gradle'] // ignore changes in these subdirs when calculating changes to parent

    tag { // doc: Version / Parsing
        prefix = 'release' // prefix to be used, 'release' by default
//        branchPrefix = [ // set different prefix per branch
//                         'legacy/.*' : 'legacy'
//        ]

        versionSeparator = '-' // separator between prefix and version number, '-' by default
//        serialize = { tag, version -> ... } // creates tag name from raw version
//        deserialize = { tag, position, tagName -> ... } // reads raw version from tag
//        initialVersion = { tag, position -> ... } // returns initial version if none found, 0.1.0 by default
    }

    nextVersion { // doc: Next version markers
        suffix = 'SNAPSHOT' // tag suffix
        separator = '-' // separator between version and suffix
//        serializer = { nextVersionConfig, version -> ... } // append suffix to version tag
//        deserializer = { nextVersionConfig, position -> ... } // strip suffix off version tag
    }

    // doc: Version / Decorating
//    versionCreator { version, position -> ... } // creates version visible for Gradle from raw version and current position in scm
//    versionCreator 'versionWithBranch' // use one of predefined version creators
//    branchVersionCreator = [ // use different creator per branch
//                             'main/.*': 'default',
//                             'feature/.*': 'versionWithBranch'
//    ]

    // doc: Version / Incrementing
//    versionIncrementer {context, config -> ...} // closure that increments a version from the raw version, current position in scm and config
    versionIncrementer 'incrementPatch' // use one of predefined version incrementing rules
//    branchVersionIncrementer = [ // use different incrementer per branch
//                                 'main/.*': 'incrementMinor'
//                                 'feature/.*': 'incrementMinor'
//                                 'release.*/.*': 'incrementPatch'
//    ]

    // doc: Pre/post release hooks
//    createReleaseCommit true // should create empty commit to annotate release in commit history, false by default
//    releaseCommitMessage { version, position -> ... } // custom commit message if commits are created

    // doc: Pre-release checks
    checks {
        uncommittedChanges = true // permanently disable uncommitted changes check
        aheadOfRemote = false // permanently disable ahead of remote check
        snapshotDependencies = true // ensure no components depend on snapshot releases
    }
}

allprojects {
    project.version = scmVersion.version
}
```

## starter.java.repo-altsource-conventions.gradle

```groovy
/**
 * Configurations for specifying a configurable repository (`mavenRepository`, `MAVEN_REPO_USERNAME`, `MAVEN_REPO_PASSWORD`)
 */

repositories {
    maven {
        url findProperty('mavenRepository')
        credentials {
            username = System.getenv('MAVEN_REPO_USERNAME')
            password = System.getenv('MAVEN_REPO_PASSWORD')
        }
    }
}
```

## starter.java.repo-default-conventions.gradle

```groovy
/**
 * Configurations for specifying standard defaults (local, mavenCentral, JCenter)
 */

repositories {
    mavenLocal()
    mavenCentral()
    jcenter()
}
```

## starter.java.repo-local-conventions.gradle

```groovy
/**
 * Configurations for specifying only local maven `~/.m2` repository
 */

repositories {
    mavenLocal()
}
```

## starter.java.repo-starter-conventions.gradle

```groovy
/**
 * Configurations for specifying starter-bom Github Packages repository
 */

repositories {
    maven {
        name = "starterBootPkgs"
        url = uri("https://maven.pkg.github.com/department-of-veterans-affairs/lighthouse-di-starter-boot")
        credentials {
            String result = "unknown"
            result = System.getenv("STARTERBOOTPKGS_USERNAME")
            username = !result ? "STARTERBOOTPKGS_USERNAME_FIXME" : result;
            result = System.getenv("STARTERBOOTPKGS_TOKEN")
            password = !result ? "STARTERBOOTPKGS_TOKEN_FIXME" : result;
        }
    }
}
```

## starter.java.test-conventions.gradle

```groovy
/**
 * Configuration for test task
 */
plugins {
    id 'java'
}

test {
    useJUnitPlatform {
//        excludeEngines 'junit-vintage'
    }
    testLogging {
        events = ["failed"]
        exceptionFormat = "short"
        showStandardStreams = project.hasProperty("showStandardStreams") ?: false
        showExceptions = true
        showCauses = false
        showStackTraces = false
        debug {
            events = ["started", "skipped", "failed"]
            showStandardStreams = true
            exceptionFormat = "full"
            showExceptions = true
            showCauses = true
            showStackTraces = true
        }
        info {
            events = ["skipped", "failed"]
            exceptionFormat = "short"
            showExceptions = true
            showCauses = true
            showStackTraces = true
        }
        minGranularity = 2
        maxGranularity = 4
        displayGranularity = 0
    }
}


```

## starter.java.test-gatling-conventions.gradle

```groovy
/**
 * Gatling configuration for running stress tests
 */

plugins {
    id 'io.gatling.gradle'
}

sourceSets {
    gatling {
        scala.srcDirs = ["src/gatling/scala"]
        resources.srcDirs = ["src/gatling/resources"]
    }
}
```

## starter.java.test-integration-conventions.gradle

```groovy
/**
 * Configurations for testing
 */

plugins {
    id 'java'
}
// Requires
// id 'starter.java.build-utils-property-conventions'

def integrationTestSets = sourceSets.create('integrationTest') {
    compileClasspath += sourceSets.main.output
    runtimeClasspath += sourceSets.main.output
}

configurations[integrationTestSets.implementationConfigurationName].extendsFrom(configurations.implementation)
configurations[integrationTestSets.runtimeOnlyConfigurationName].extendsFrom(configurations.runtimeOnly)

def integrationTest = tasks.register('integrationTest', Test) {
    description = 'Runs integration tests.'
    group = 'verification'

    testClassesDirs = integrationTestSets.output.classesDirs
    classpath = integrationTestSets.runtimeClasspath

    // should find integration test output summary and use that as the timestamp comparison
    // if there should be any updates that would affect the integration test
    outputs.upToDateWhen { false }
    shouldRunAfter tasks.named('test')
    useJUnitPlatform {
        // excludeEngines 'junit-vintage'
    }
    testLogging {
        showStandardStreams = false // true
        // events "passed", "skipped", "failed"
        showExceptions true
        showCauses true
        minGranularity 2
        minGranularity 4
        displayGranularity 0
    }
}

tasks.named('check') {
    dependsOn integrationTest
}


```

## starter.java.test-jacoco-aggregation-conventions.gradle

```groovy
plugins {
    id 'java'
    id 'jacoco'
    id 'org.barfuin.gradle.jacocolog'
}

jacocoLogTestCoverage {
    logAlways = true
    counters {
        showComplexityCoverage = true
        showClassCoverage = true
        showLineCoverage = true
    }
}

def cleanReports = tasks.register('cleanReports', Exec) {
    group = 'Verification'
    description = "Clean up aggregate jacoco reports"
    executable('rm')
    args('-rf', "${project.buildDir}/{reports,jacoco}")
}

clean.configure {
    dependsOn cleanReports
}
```

## starter.java.test-jacoco-conventions.gradle

```groovy
/**
 * Configuration for jacoco
 */

plugins {
    id 'java'
    id 'jacoco'
}
// Requires
// id 'starter.java.build-utils-property-conventions'

jacoco {
    toolVersion = jacoco_version
    reportsDir = file("$buildDir/jacoco")
}

test {
    finalizedBy jacocoTestReport
    finalizedBy jacocoTestCoverageVerification
}


jacocoTestReport {
    reports {
        html.enabled true
        xml.enabled true
        csv.enabled false
    }

    afterEvaluate {
        classDirectories.setFrom(files(classDirectories.files.collect {
            fileTree(dir: it, exclude: ['**/*MapperImpl.*', '**/*Application.*'] )
        }))
    }

    dependsOn test
}

jacocoTestCoverageVerification {
    violationRules {
        rule {
            enabled = jacoco_enforce_violations
            limit {
                minimum = jacoco_minimum_coverage
            }
            afterEvaluate {
                classDirectories.setFrom(files(classDirectories.files.collect {
                    fileTree(dir: it, exclude: ['**/*MapperImpl.*', '**/*Application.*'] )
                }))
            }
        }
    }
}



```

## starter.java.test-unit-conventions.gradle

```groovy
plugins {
}
// Requires
// id 'starter.java.test-conventions'

```

## starter.java.versions-conventions.gradle

```groovy
/**
 * Configuration for ben-manes/gradle-versions-plugin and patrikerdes/gradle-use-latest-versions-plugin.
 *
 * These plugins will display a list of all the (direct) dependencies in your project, along with the version.
 * It will also determine if a newer version of the package is available, depending on the rules you set up.
 * For example, the default configuration specifies that if the current package is stable, then it will not suggest non-stable version updates.
 *
 * The gradle-use-latest-versions-plugin will use the information provided by the versions plugin to make changes to`build.gradle` and `gradle.properties` files to update dependency versions.
 */

plugins {
    id("com.github.ben-manes.versions")
    id("se.patrikerdes.use-latest-versions")
}

def isNonStable = { String version ->
    def stableKeyword = ['RELEASE', 'FINAL', 'GA'].any { it -> version.toUpperCase().contains(it) }
    def regex = /^[0-9,.v-]+(-r)?$/
    return !stableKeyword && !(version ==~ regex)
}

def isSnapshot = { String version ->
    return ['SNAPSHOT'].any { it -> version.toUpperCase().contains(it) }
}

def isReleaseCandidate = { String version ->
    return ['RC', 'rc'].any { it -> version.toUpperCase().contains(it) }
}

dependencyUpdates {
    checkForGradleUpdate = true
    checkConstraints = true

    resolutionStrategy {
        componentSelection {
            all {
                if (isNonStable(candidate.version) && !isNonStable(project.version)) {
                    reject('Release candidate')
                }
            }
        }
    }
}

useLatestVersions {
    // A whitelist of dependencies to update, in the format of group:name
    // Equal to command line: --update-dependency=[values]
    updateWhitelist = []
    // A blacklist of dependencies to update, in the format of group:name
    // Equal to command line: --ignore-dependency=[values]
    //
    // NOTE: This is the list of spring-defined dependencies currently used in
    // starter.java.build-conventions
    updateBlacklist = [
            'com.fasterxml.jackson.datatype:jackson-datatype-jsr310',
            'org.assertj:assertj-core',
            'org.junit.jupiter:junit-jupiter-api',
            'org.junit.jupiter:junit-jupiter-engine',
            'org.mapstruct:mapstruct',
            'org.mapstruct:mapstruct-processor',
            'org.mockito:mockito-core',
            'org.projectlombok:lombok',
            'org.springframework.boot:spring-boot-starter-test'
    ]
    // When enabled, root project gradle.properties will also be populated with
    // versions from subprojects in multi-project build
    // Equal to command line: --update-root-properties
    updateRootProperties = false
    // List of root project files to update when updateRootProperties is enabled.
    // `build.gradle` is not an acceptable entry here as it breaks other expected
    // functionality. Version variables in `build.gradle` need to be moved into
    // a separate file which can be listed here.
    // Equal to command line: --root-version-files=[values]
    rootVersionFiles = ['gradle.properties']
}
```

## starter.metrics.build-time-tracker-conventions.gradle

```groovy
/**
 * Configuration for tracking how long a build takes, using `net.rdrei.android.buildtimetracker`
 */

plugins {
    id "net.rdrei.android.buildtimetracker"
}

buildtimetracker {
    reporters {
        csv {
            output "build/times.csv"
            append true
            header false
        }

        summary {
            ordered false
            threshold 50
            barstyle "unicode"
        }

        csvSummary {
            csv "build/times.csv"
        }
    }
}

```

## starter.metrics.talaiot-conventions.gradle

```groovy
/**
 * Configuration for tracking how long a build takes, using talaiot.
 * NOTE: No configuration set for shipping metrics to an aggregator, but the capability exists.
 */

plugins {
    id "com.cdsap.talaiot"
    id "com.cdsap.talaiot.plugin.base"
}

talaiot {
    metrics {
        // You can add your own custom Metric objects:
        customMetrics(
//                MyCustomMetric(),
        // Including some of the provided metrics, individually.
                HostnameMetric()
        )

        // Or define build or task metrics directly:
        customBuildMetrics(
                kotlinVersion: $kotlinVersion,
                javaVersion: $javaVersion
        )
//        customTaskMetrics(
//                customProperty: $value
//        )
    }

    filter {
        tasks {
//        excludes = arrayOf("preDebugBuild", "processDebugResources")
        }
        modules {
//        excludes = arrayOf(":app")
        }
        threshold {
//        minExecutionTime = 10
        }
        build {
            success = true
//          requestedTasks {
//            includes = arrayOf(":app:assemble.*")
//            excludes = arrayOf(":app:generate.*")
//          }
        }
    }

    ignoreWhen {
//        envName = "CI"
//        envValue = "true"
    }

    publishers {
//        influxDbPublisher {
//            dbName = "tracking"
//            url = "http://localhost:8086"
//            taskMetricName = "task"
//            buildMetricName = "build"
//        },
        jsonPublisher = true
        timelinePublisher = true
        taskDependencyPublisher {
            html = true
        }

    }

}
```

## starter.std.java.application-conventions.gradle

```groovy
/**
 * Top-level configuration of all the typical standard configurations for a Spring Boot application package.
 */

plugins {
    // Apply the java Plugin to add support for Java.
    id 'java'
    id "org.ajoberstar.grgit"
    // Apply the application plugin to add support for building a CLI application in Java.
    id 'application'
    id 'starter.java.build-utils-property-conventions'
    id 'starter.java.build-javatarget-conventions'
    id 'starter.java.build-springboot-conventions'
    id 'starter.java.deps-build-conventions'
    id 'starter.java.container-conventions'
    id 'starter.java.container-spring-conventions'
    id 'starter.java.lint-checkstyle-conventions'
    id 'starter.java.doc-springdoc-conventions'
    id 'starter.java.test-conventions'
    id 'starter.java.test-jacoco-conventions'
    id 'starter.java.test-integration-conventions'
    id 'starter.java.test-gatling-conventions'
    id 'starter.java.deps-test-conventions'
    id 'starter.java.deps-integration-conventions'
    id 'starter.java.publish-repo-conventions'
    id 'starter.java.publish-bootjar-conventions'
    id 'starter.java.versions-conventions'
}

dependencies {
    testImplementation 'org.mock-server:mockserver-netty'
    // open tracing testing/mock support
    testImplementation 'io.opentracing:opentracing-mock'
    testImplementation 'com.tngtech.archunit:archunit'
}
```

## starter.std.java.bom-conventions.gradle

```groovy
/**
 * Top-level configuration of all the typical standard configurations for a Bill of Materials package
 */

plugins {
    id 'java-platform'
    id 'starter.java.publish-repo-conventions'
    id 'starter.java.publish-pom-conventions'
    id 'starter.java.versions-conventions'
}


```

## starter.std.java.cli-conventions.gradle

```groovy
/**
 * Top-level configuration of all the typical standard configurations for a java cli (untested)
 */

plugins {
    // Apply the java Plugin to add support for Java.
    id 'java'
    id "org.ajoberstar.grgit"
    // Apply the application plugin to add support for building a CLI application in Java.
    id 'application'
    id 'starter.java.build-utils-property-conventions'
    id 'starter.java.build-javatarget-conventions'
    id 'starter.java.deps-build-conventions'
    id 'starter.java.deps-test-conventions'
    id 'starter.java.container-conventions'
    id 'starter.java.lint-checkstyle-conventions'
    id 'starter.java.test-conventions'
    id 'starter.java.test-jacoco-conventions'
    id 'starter.java.test-unit-conventions'
    id 'starter.java.publish-repo-conventions'
    id 'starter.java.publish-jar-conventions'
    id 'starter.java.versions-conventions'
}

```

## starter.std.java.library-conventions.gradle

```groovy
/**
 * Top-level configuration of all the typical standard configurations for a normal Java jar.
 */

plugins {
    // Apply the java Plugin to add support for Java.
    id 'java'
    id 'java-library'
    id "org.ajoberstar.grgit"
    id 'starter.java.build-utils-property-conventions'
    id 'starter.java.build-javatarget-conventions'
    id 'starter.java.deps-open-tracing-common-conventions'
    id 'starter.java.deps-build-conventions'
    id 'starter.java.lint-checkstyle-conventions'
//    id 'starter.java.doc-swagger-conventions'
    id 'starter.java.test-conventions'
    id 'starter.java.test-unit-conventions'
    id 'starter.java.test-jacoco-conventions'
    id 'starter.java.deps-test-conventions'
    id 'starter.java.publish-repo-conventions'
    id 'starter.java.publish-jar-conventions'
    id 'starter.java.versions-conventions'
}

```

## starter.std.java.library-spring-conventions.gradle

```groovy
/**
 * Top-level configuration of all the typical standard configurations for a normal Java jar.
 */

plugins {
    id 'starter.std.java.library-conventions'
    id 'starter.java.config-conventions'
    id 'starter.java.build-utils-conventions'
}

```

## starter.std.java.plugin-conventions.gradle

```groovy
/**
 * Top-level configuration of all the typical standard configurations for a gradle plugin
 */

plugins {
    // Apply the java Plugin to add support for Java.
    id 'groovy'
    id 'java'
    id 'java-library'
    id 'java-gradle-plugin'
    id "org.ajoberstar.grgit"
    id 'starter.java.build-utils-property-conventions'
    id 'starter.java.build-plugintarget-conventions'
    id 'starter.java.config-conventions'
    id 'starter.java.lint-checkstyle-conventions'
    id 'starter.java.test-jacoco-conventions'
    id 'starter.java.test-unit-conventions'
    id 'starter.java.deps-plugin-conventions'
    id 'starter.java.deps-test-conventions'
    id 'starter.java.publish-repo-conventions'
    id 'starter.java.versions-conventions'
}

```

## starter.std.java.shell-conventions.gradle

```groovy
/**
 * Top-level configuration of all the typical standard configurations for a gradle plugin
 */

plugins {
    id 'starter.java.lint-shellcheck-conventions'
}

```
