[![CircleCI](https://circleci.com/gh/department-of-veterans-affairs/abd-vro/tree/main.svg?style=shield&circle-token=adbf14b0367e25d8a01014669c255af742498dd3)](https://circleci.com/gh/department-of-veterans-affairs/abd-vro/tree/main)

# ABD VRO

ABD VRO (Automated Benefits Delivery - Virtual Regional Office)

Guided by the vision to get Veterans benefits *in minutes, not months*, the VA Automated Benefits Delivery team (within the Office of the CTO) seeks to use available data and automated processes to reduce the time that Veterans must wait for a claim decision. The Virtual Regional Office software uses existing health evidence data to help ratings adjudicators make decisions about disability claims.

The overall goal for this project is to build automation to enrich Veteran claims with relevant data and remove unnecessary roadblocks to get Veteran's their benefits faster. Specifically, we aim to:
* Reduce the time a Veteran waits for a decision on an application (aka “claim processing time”)
* Reduce the number of unnecessary medical exams ordered for disability compensation applications (aka “unnecessary exams”)
* Reduce the amount of manual effort required to make a decision on each claim (aka“touch time”)

## Building

[LHDI's Java Starter Kit](#lhdis-java-starter-kit) was used to populate this codebase (see [PR #8](https://github.com/department-of-veterans-affairs/abd-vro/pull/8)) using Java (AdoptOpenJDK) 17 and Gradle 7.4.

LHDI's [tools](https://animated-carnival-57b3e7f5.pages.github.io/ROADMAP/), [services](https://animated-carnival-57b3e7f5.pages.github.io/RELEASES/), and [support team](https://animated-carnival-57b3e7f5.pages.github.io/SUPPORT/).

## Packages (in Github Container Registry)

Initially, there are 2 packages (container images):
- `abd-vro/abd_vro-db-init`
- `abd-vro/abd_vro-app`

If more are created, go to the [VA Organization's Packages page](https://github.com/orgs/department-of-veterans-affairs/packages?tab=packages&q=abd-vro), search for the package, and  manually connect them to the repo.
[LHDI info](https://animated-carnival-57b3e7f5.pages.github.io/starterkits/java/development-guide/#changing-published-package-visibility)

## CircleCI

Followed [LHDI instructions](https://animated-carnival-57b3e7f5.pages.github.io/starterkits/java/development-guide/#setting-up-a-ci-pipeline) and used [lighthouse-di-circleci-java17-image](https://github.com/department-of-veterans-affairs/lighthouse-di-circleci-java17-image).

It runs similar operations as the `main.yml` Github Action.

## Lightkeeper

Followed [Getting Started instructions](https://animated-carnival-57b3e7f5.pages.github.io/#getting-started) and [LHDI LightKeeper docs](https://animated-carnival-57b3e7f5.pages.github.io/installing-lightkeeper/).

Next, `lightkeeper create clusterconfig nonprod > kube_config` (Step 4). Since `kubectl` is not installed on the GFE, transferred `kube_config` from GFE to development laptop (where `kubectl` is installed) and saved it as `~/.kube/config`. Note this config is for `nonprod`.

Remember to specify the namespace (e.g., `--namespace va-abd-rrd-dev`) for all `kubectl` commands, e.g.:
`kubectl get pods --namespace va-abd-rrd-dev` and `helm list --namespace va-abd-rrd-dev`

## EKS/Kubernetes clusters
From [LHDI kubernetes docs](https://animated-carnival-57b3e7f5.pages.github.io/kubernetes/), clusters are:
- ldx-nonprod-1 (DEV, SANDBOX, QA)
- ldx-prod-1 (PROD)
- ldx-preview-1 (PREVIEW)

[Associated gateways](https://animated-carnival-57b3e7f5.pages.github.io/service-mesh/routing-traffic/#environment-gateways)
- ldx-nonprod (NONPROD)
- ldx-nonprod-1 (NONPROD1)
- ldx-dev (DEV)
- ldx-prod-1 (PROD1)
- ldx-preview-1 (PREVIEW1) (no gateway)
- ldx-mapi-1 (MAPI-1)

DNS:
- dev.lighthouse.va.gov
- sandbox.lighthouse.va.gov
- qa.lighthouse.va.gov
- (PROD) api.lighthouse.va.gov

Output from `lightkeeper list team va-abd-rrd`:

```json
{
  "name": "va-abd-rrd",
  "id": "...",
  "description": "va-abd-rrd description",
  "namespaces": [
    {
      "name": "va-abd-rrd-prod",
      "cluster": "ldx-prod-1",
      "cpu": 128,
      "ram": "256G",
      "provided": true,
      "imageSigningEnabled": true
    },
    {
      "name": "va-abd-rrd-qa",
      "cluster": "ldx-nonprod-1",
      "cpu": 128,
      "ram": "256G",
      "provided": true,
      "imageSigningEnabled": false
    },
    {
      "name": "va-abd-rrd-sandbox",
      "cluster": "ldx-nonprod-1",
      "cpu": 128,
      "ram": "256G",
      "provided": true,
      "imageSigningEnabled": true
    },
    {
      "name": "va-abd-rrd-dev",
      "cluster": "ldx-nonprod-1",
      "cpu": 128,
      "ram": "256G",
      "provided": true,
      "imageSigningEnabled": false
    }
  ],
  "scm": null,
  "artifact_store": null,
  "pipeline": null,
  "registry": null,
  "reporting": null,
  "observer": null
}
```

## DataDog

* [LHDI dashboards](https://app.datadoghq.com/dashboard/lists?q=LHDI)
* [lasershark's dashboard](https://app.datadoghq.com/dashboard/u8q-bbj-xs8/lasershark) - one of the [Alpha customers](https://animated-carnival-57b3e7f5.pages.github.io/#alpha-customers)

# LHDI's Java Starter Kit

**In this README:**

- [About](#about)
- [Getting Started](#getting-started)
  - [Required Dependencies](#required-dependencies)
  - [Running the Application](#running-the-application)
  - [Verifying the Application is Running](#verifying-the-application-is-running)
- [CI/CD Pipeline](#cicd-pipeline)
  - [Secrets](#secrets)
  - [Default Pipeline Behavior](#default-pipeline-behavior)
  - [Dependencies](#dependencies)
  - [Deploying the Application](#deploying-the-application)
- [What's Next](#whats-next)

---

## About

This is a Java Spring Boot application that has been created using the [Lighthouse DI Java 17 Starterkit][1].
It is intended to be used as a starting point for building Java APIs and should be customized to deliver whatever functionality is required.
If no other changes have been made, this application will have [these features][2] included by default.

## Getting Started

### Required Dependencies

Before you run this application locally, you will need to make sure you have all the following required dependencies available in your local environment:

- Java 17 ([Mac Guide][6] | [Other OS Guide][5])
- Gradle 7.2 (used by Github Actions)
- [docker][7]
- [hadolint][8]
- [spectral][12]
- [shellcheck][9]

> Use the [Mac OS Guide][4] to make sure you have all the above dependencies available in your local environment.
> Otherwise, refer to the [Other Operating Systems Guide][5].

You will also need to have a GitHub personal access token with the `read:packages` permission exported in your local shell.
This is required to in order to download application artifacts that are published to the [VA GitHub Package Registry][10].

You can generate a new access token by following [this guide][11].
When you have your token, make sure it is available in your local shell by running:

```bash
export GITHUB_ACCESS_TOKEN=<replace-with-token-from-github>
```

### Running the Application

Once you have all the required dependencies, you can start the application in your local environment by navigating to the root of your application directory and running the following command:

```bash
./gradlew clean
./gradlew build check docker
```

This will build all the application artifacts and docker images.

> Note: Due to the way Gradle computes dependencies, the `clean` command must always be separate from `build` commands

You can then start the application by running:

```bash
./gradlew :app:dockerComposeDown :app:dcPrune :app:dockerComposeUp
```

This should bring up a docker container with the app running at http://localhost:8081

There are shortcut tasks defined in the root `build.gradle` file to make life a bit easier:

```bash
./gradlew devloop     # rebuild what is out of date, recreate and restart individual docker images
./gradlew restartloop # stop containers, then proceed with `devloop`
./gradlew resetloop   # stop containers, clean volumes, then proceed with `devloop`
```

The `devloop` is most convenient and quickest feedback cycle if one needs to do manual testing with the system services.

> Note that at this time, `./gradlew run` and `./gradlew bootRun` require additional setup with database dependencies prior to use with a local development environment.

### Verifying the Application is Running

You can verify that the application is up and running by issuing the following commands in your terminal:

```bash
curl localhost:8081/health
curl localhost:8081/info
```

You should get back responses similar to the following:

```bash
curl localhost:8081/health

{
    "status":"UP",
    "components":{
        "db":{
            "status":"UP",
            "details":{
                "database":"PostgreSQL",
                "validationQuery":"isValid()"
            }
        },
        "diskSpace":{
            "status":"UP",
            "details":{
                "total":62725623808,
                "free":53279326208,
                "threshold":10485760,
                "exists":true
            }
        },
        "livenessState":{
            "status":"UP"
        },
        "ping":{
            "status":"UP"
        },
        "readinessState":{
            "status":"UP"
        }
    },
    "groups":[
        "liveness",
        "readiness"
    ]
}
```

```bash
curl localhost:8081/actuator/info

{
    "app": {
        "description": "Java API Starter from Template",
        "name": "abd_vro"
    }
}
```

## CI/CD Pipeline

This project comes with a skeleton Github Actions CI/CD pipeline out of the box. You can always choose to rewrite the pipeline using a different CI/CD tool; this pipeline serves as an example that you can use and run with minimal setup.

### Secrets

In order to run the pipeline, you will need to [create a personal access token][11] and add it to your repository's secrets in Github. The access token should have `write:packages` scope.

The secrets you need to configure are

- `ACCESS_TOKEN`: the personal access token
- `USERNAME`: the Github username of the user who owns the access token

### Default Pipeline Behavior

The default pipeline has 3 jobs, which do the following things:

- Runs CIS benchmark tests against the application Docker image using `docker-bench-security`
- Builds and tests application
- Publishes Docker image to VA GHCR repository

### Dependencies

The pipeline runs on Github's `ubuntu-latest` runner, which is currently Ubuntu 20.04. The Github Actions [Ubuntu 20.04 documentation](https://github.com/actions/virtual-environments/blob/main/images/linux/Ubuntu2004-Readme.md) lists the software installed by default. To learn more about choosing a Github runner and Github-hosted runner types, see the [`job.<job-id>.runs-on`](https://docs.github.com/en/actions/using-workflows/workflow-syntax-for-github-actions#jobsjob_idruns-on) documentation.

Software required for the pipeline but not installed by default, such as Java 17, hadolint, and spectral, is installed in the pipeline. The installation for app build dependencies is implemented as an action in <`./.github/actions/setup-pipeline/action.yml`>.

### Deploying the Application

The pipeline does not currently deploy the application to the DI Kubernetes clusters out of the box, although this setup will be coming in the future. To learn how to deploy your applications, see the [DI ArgoCD docs](https://animated-carnival-57b3e7f5.pages.github.io/cluster-services/argo-cd/).

## Common Errors

1. Error: Cannot find plugin

    Error Message:

    ```
    * What went wrong:
    Plugin [id: 'gov.va.starter.plugin.cookiecutter', version: '0.1.20', apply: false] was not found in any of the following sources:

    - Gradle Core Plugins (plugin is not in 'org.gradle' namespace)
    - Plugin Repositories (could not resolve plugin artifact 'gov.va.starter.plugin.cookiecutter:gov.va.starter.plugin.cookiecutter.gradle.plugin:0.1.20')
    Searched in the following repositories:
        MavenLocal(file:/Users/aasare/.m2/repository/)
        Gradle Central Plugin Repository
        MavenRepo
        BintrayJCenter
        maven(https://palantir.bintray.com/releases)
        maven2(https://dl.bintray.com/adesso/junit-insights)
        starterBootPkgs(https://maven.pkg.github.com/department-of-veterans-affairs/lighthouse-di-starter-boot)
        nexus(https://tools.health.dev-developer.va.gov/nexus)
    ```

    Fix:  Set your Github token as per the instructions in the [Required Dependencies](#required-dependencies) section above.

## What's Next

Once you have verified that you are able to run the application successfully, you can now start customizing the application to deliver the functionality you would like.

By default, this application assumes the use of a build, test, release cycle as defined in [this development guide][14].
Take a look at that guide to see how you can make changes, test them and get them deployed to a target environment.

The application itself is organized into the following three tiers of functionality:

- API
- Service (business logic)
- Persistence

To see how each of these tiers is used by default, take a look at the [Project Structure][13] documentation.

[1]: https://github.com/department-of-veterans-affairs/lighthouse-di-starterkit-java
[2]: https://animated-carnival-57b3e7f5.pages.github.io/starterkits/java/explore-features/
[4]: https://github.com/department-of-veterans-affairs/lighthouse-di-starterkit-java/blob/main/docs/developing-on-mac.md
[5]: https://animated-carnival-57b3e7f5.pages.github.io/starterkits/java/developing-on-other-os/
[6]: https://animated-carnival-57b3e7f5.pages.github.io/starterkits/java/developing-on-mac/
[7]: https://docs.docker.com/get-docker/
[8]: https://github.com/hadolint/hadolint#install
[9]: https://github.com/koalaman/shellcheck#readme
[10]: https://github.com/orgs/department-of-veterans-affairs/packages
[11]: https://docs.github.com/en/github/authenticating-to-github/keeping-your-account-and-data-secure/creating-a-personal-access-token
[12]: https://meta.stoplight.io/docs/spectral/ZG9jOjYyMDc0Mw-installation
[13]: https://github.com/department-of-veterans-affairs/lighthouse-di-starterkit-java/blob/main/docs/project-structure.md
[14]: https://github.com/department-of-veterans-affairs/lighthouse-di-starterkit-java/blob/main/docs/development-guide.md
