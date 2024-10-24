---
name: VRO Engineer Onboarding
about: This issue will walk a VRO engineer through their onboarding
title: 'VRO Engineer Onboarding - [ NAME ] '
labels: VRO-team
assignees: ''

---

## Engineer Onboarding Checklist

Below are actions you should take and resources to review. As you go through onboarding, feel free to add other pages to this list if you think they'd be helpful.

### Getting Started

### Meetings and Communication

- [ ]  **Get added to recurring team meetings** such as:
    - [ ]  Weekly VBA Stakeholder Sync.
    - [ ]  Daily VRO Team Sprint Standups.
    - [ ]  Biweekly VRO Sprint Planning / Review / Retrospective.
- [ ]  Get the VRO Delivery Manager to the tool LHDI uses for the management of our cATO (TODO: Confirm what this is)
- [ ]  **Team Roster Addition:** Ensure you’ve been added to the VRO team roster [here](https://github.com/department-of-veterans-affairs/abd-vro/wiki/Virtual-Regional-Office-Overview)
- [ ]  **Set up team 1:1s**: See the [VRO team roster](https://github.com/department-of-veterans-affairs/abd-vro/wiki/Virtual-Regional-Office-Overview#-who-is-the-vro-team) for who to set up meetings with (reference team members' Slack profiles for calendar URLs to find available times).
- [ ]  **Ask in the daily scrum meeting** where you can pair with other engineers.

### Slack Channels

- [ ]  **Join relevant Slack channels**:
    - **Most relevant channels**:
        - [ ]  `#benefits-vro`: Team coordination.
        - [ ]  `#benefits-vro-engineering`: Engineering collaboration.
        - [ ]  `#benefits-vro-support`: Collaboration with partner teams.
        - [ ]  `#benefits-cft`: Cross-team collaboration.
    - **On-Call channels**
        - [ ]  `#benefits-vro-on-call`:
        - [ ]  TODO: add others
        - [ ]  [`#bep-integration-support2077`](https://dsva.slack.com/archives/C05DUAWDVRQ): private channel, you’ll need to be added.
    - **Additional channels**:
        - [ ]  Review the [Slack Channels Spreadsheet](https://docs.google.com/spreadsheets/d/1c3e6SVuhVz3q4bh1qf5jj25EmusCd3NVXhDjSIpr9yk/edit#gid=1020923043).
        - *Note*: You don't have to join all channels; start with `#benefits-vro-*` and `#benefits-cft-*` channels.

### Microsoft Teams Channels

 Get added to the relevant Microsoft Teams channels:

- [ ]  [BIP Microsoft Teams](https://teams.microsoft.com/l/entity/2a527703-1f6f-4559-a332-d8a7d288cd88/_djb2_msteams_prefix_3931627218?context=%7B%22subEntityId%22%3Anull%2C%22channelId%22%3A%2219%3A38316fe7e5bc49e8a984da519a70e1df%40thread.tacv2%22%7D&groupId=007f7821-5d9f-4f4f-b0fc-0e0a24ee625f&tenantId=e95f1b23-abaf-45ee-821d-b7ab251ab3bf&allowXTenantAccess=false)
- [ ]  [BGS Microsoft Teams](https://teams.microsoft.com/l/channel/19%3A38316fe7e5bc49e8a984da519a70e1df%40thread.tacv2/Product%20-%20BEP%20-%20General?groupId=007f7821-5d9f-4f4f-b0fc-0e0a24ee625f)

### GitHub Access

- [ ]  **Get added to the [VA GitHub organization](https://github.com/department-of-veterans-affairs/github-user-requests/issues/new/choose)**.
    - [ ]  Provide your GitHub username to your Delivery Manager or team lead.
- [ ]  **Get added to relevant GitHub teams**:
    - [ ]  [OCTO-VRO GitHub team](https://github.com/orgs/department-of-veterans-affairs/teams/octo-vro/members) for repo access.
    - [ ]  [Benefits VRO Engineers](https://github.com/orgs/department-of-veterans-affairs/teams/benefits-vro-engineers/members) for additional privileges.
    - [ ]  [VA-ABD-RRD GitHub team](https://github.com/orgs/department-of-veterans-affairs/teams/va-abd-rrd/members) for deployment environments.
        - [ ]  For "dangerous" actions, get added to [VRO-RESTRICTED](https://github.com/orgs/department-of-veterans-affairs/teams/vro-restricted/members).
- [ ]  **Set up GitHub permissions**:
    - [ ]  Ensure you have the GH permissions to bypass PR requirements and allow force pushes to the `main` branch of `abd-vro`.

### Local Environment Setup

- [ ]  **Set up your [local environment](https://github.com/department-of-veterans-affairs/abd-vro/wiki/Local-Setup)** and test services.
    - [ ]  Note any issues and update the Local Setup page.
    - [ ]  Seek assistance on the `#benefits-vro-engineering` channel if needed.

---

### Resources to Review

- [ ]  **Explore the [VRO Wiki](https://github.com/department-of-veterans-affairs/abd-vro/wiki)**, especially:
    - [ ]  **Introduction**:
        - [ ]  [VRO Engineer Onboarding](https://github.com/department-of-veterans-affairs/abd-vro/wiki/VRO-Engineer-Onboarding).
    - [ ]  **Architecture**:
        - [ ]  [VRO Architecture Diagram](https://github.com/department-of-veterans-affairs/abd-vro/wiki/VRO-Architecture-Diagram).
    - [ ]  **Priorities and Roadmap**:
        - [ ]  [VRO Priorities](https://github.com/department-of-veterans-affairs/abd-vro/wiki/VRO-priorities).
        - [ ]  [VRO Roadmap](https://github.com/department-of-veterans-affairs/abd-vro/wiki/VRO-v2-Roadmap).
    - [ ]  **Technical Documentation**:
        - [ ]  [Routing API Requests](https://github.com/department-of-veterans-affairs/abd-vro/wiki/Routing-API-requests).
        - [ ]  [Code Structure](https://github.com/department-of-veterans-affairs/abd-vro/wiki/Code-structure).
        - [ ]  [Gradle Projects](https://github.com/department-of-veterans-affairs/abd-vro/wiki/Code-structure#gradle-projects).
        - [ ]  [Development Process](https://github.com/department-of-veterans-affairs/abd-vro/wiki/Development-process).
        - [ ]  [Team Processes](https://github.com/department-of-veterans-affairs/abd-vro/wiki/Team%20Processes).
        - [ ]  [External APIs](https://github.com/department-of-veterans-affairs/abd-vro/wiki/External-APIs-to-interact-with-other-systems).
        - [ ]  [LHDI's Boilerplate Instructions](https://github.com/department-of-veterans-affairs/abd-vro/wiki/LHDI%27s-Boilerplate-Instructions).
        - [ ]  [On-Call Responsibilities](https://github.com/department-of-veterans-affairs/abd-vro/wiki/On-Call-Responsibilities).
        - [ ]  [DataDog Monitoring](https://github.com/department-of-veterans-affairs/abd-vro/wiki/DataDog-monitoring).

### Murals

- [ ]  **VA's Benefit Portfolio Organigram**: [Mural Link](https://app.mural.co/t/departmentofveteransaffairs9999/m/departmentofveteransaffairs9999/1678236223248/ef60b22feff4aa22c594256683b81988abc1f181?sender=u5e5c57ecc5136069f64f2819).
- [ ]  **VRO's Working Agreements**: [Mural Link](https://app.mural.co/t/departmentofveteransaffairs9999/m/departmentofveteransaffairs9999/1697222300764/071b9570f3bf4a8a678a547a8d3ccd9410e977df?sender=u5e5c57ecc5136069f64f2819).

### Videos

- [ ]  **Watch**:
    - [ ]  ["The Way We Work" presentation](https://us06web.zoom.us/rec/share/sHRSblAbF229tQpj7-Nqmmdi7Zb5bWclCd0lqMMdGAttuaJZDByZy8C7vi1BO9Mo.7XuVLn108mwIpnQj?startTime=1680030182000).
    - [ ]  [LHDI Team Demo](https://lighthouseva.slack.com/archives/C03UA9MV1EH/p1681327320119849?thread_ts=1681304404.488959&cid=C03UA9MV1EH) on SecRel pipeline and SD Elements.
- [ ]  **Optional**:
    - [ ]  '2022-06 VRO code walk-through.mp4' in [Shared Meeting Notes](https://dvagov.sharepoint.com/:f:/r/sites/vaabdvro/Shared%20Documents/VRO%20-%20Virtual%20Regional%20Office/Shared%20Meeting%20Notes?csf=1&web=1&e=0LcwED).

---

### Team Integration

### Onboarding Buddy and Collaboration

- [ ]  **Get added to the `#benefits-onboarding` Slack channel**.
    - [ ]  Complete onboarding procedures in the Slack Canvas.
    - [ ]  Provide your role, email, and team for onboarding buddies.
- [ ]  **Go through Benefits Portfolio onboarding materials and buddy meetings**.

### Pair Programming and Code Walkthroughs

- [ ]  **Set up code walkthrough meetings with engineering team members**.
    - [ ]  Ask during scrum ceremonies or on Slack.
- [ ]  **Ask to pair with other engineers** during daily scrum meetings.

---

### Additional Steps

### VA Email and Tools

- [ ]  **When you receive your [VA.gov](http://va.gov/) email address**:
    - [ ]  **Add it to your GitHub account** via [settings](https://github.com/settings/emails).
    - [ ]  **Inform your Delivery Manager** for SharePoint and Okta access.
    - [ ]  **Set up an Okta account** [following these instructions](https://dsva.slack.com/archives/C01U8EYHSEM/p1697220895678199).
    - [ ]  **Request Datadog access** for [VA.gov](http://va.gov/) and LHDI.
        - [ ]  **Datadog Monitoring**: Review [DataDog Monitoring](https://github.com/department-of-veterans-affairs/abd-vro/wiki/DataDog-monitoring).
- [ ]  **Access VA's Citrix Access Gateway (CAG)**:
    - [ ]  Request copy-and-paste permissions via [this article](https://yourit.va.gov/va?id=kb_article_view_yourit&sys_id=f96381201ba6a99048a36242604bcbcb&table=kb_knowledge).
- [ ]  Get added to [SDE for LHDI](https://sde.lighthouse.va.gov/bunits/va/abd-vro/abd-vro/tasks/phase/activities/).
- [ ]  **Add yourself as a VRO ZH Board Workspace Member:** If not already done, do so in the [VRO board’s settings](https://app.zenhub.com/workspaces/vro-team-6557e67173391c000e1409f3/board).

### PagerDuty and On-Call Rotation

- [ ]  **When ready to join on-call rotation**:
    - [ ]  Request a PagerDuty account in [`#vfs-platform-support`](https://dsva.slack.com/archives/CBU0KDSB1).
    - [ ]  Request to be added to the `VRO Benefits` team.
    - [ ]  **On-Call Responsibilities**: Review [On-Call Responsibilities](https://github.com/department-of-veterans-affairs/abd-vro/wiki/On-Call-Responsibilities).

### PTO Communication

- [ ]  **Understand PTO communication** within the team:
    - [ ]  **Slack**: Notify appropriate channels.
    - [ ]  **Outlook Calendar**: Update after VA access is granted.
    - [ ]  **Google Shared Team Calendar**: Added by Delivery Manager.

---

### Optional Items

- [ ]  **For background and context**, read:
    - [ ]  **Teams and Vision**: [ABD Vision Snapshot](https://dvagov.sharepoint.com/:p:/r/sites/HypertensionRapidDecisionPilot/_layouts/15/Doc.aspx?sourcedoc=%7B4A6FD69B-D04E-4CD8-879A-AE2C547B7A66%7D&file=ABD%20Vision%20Snapshot%20_%20May%202022.pptx&action=edit&mobileredirect=true).
- [ ]  **If working in [VA.gov](http://va.gov/)'s codebase (RRD)**, review: TODO: is this needed?
    - [ ]  [Technical Onboarding](https://dvagov.sharepoint.com/:b:/s/vaabdvro/ERtVkVHH1ShAimZQXPUOLrUB7MQvGRyFxXEuSBKO-mtZjw?e=OTccFK).
    - [ ]  [RRD Technical Overview](https://dvagov.sharepoint.com/:b:/s/vaabdvro/ERtVkVHH1ShAimZQXPUOLrUB7MQvGRyFxXEuSBKO-mtZjw?e=OTccFK).
    - [ ]  [VA DevOps Release Process](https://dvagov.sharepoint.com/:w:/s/vaabdvro/ES5f3QVSvsVJgPKRgz9_kfQBEYyQLif_ugcKs1FCDgF7nQ?e=cKFNOq) *(may need to be updated)*.

---

## Conclusion

As you go through the onboarding process, don't hesitate to contact your team members for assistance. If any items are blocking you, please notify your Delivery Manager or mention it in the `#benefits-vro-engineering` Slack channel.

We're excited to have you on the team and look forward to collaborating with you!
