# Introduction
This repository is an implementation of a service-based collaborative systems using the **GAG model** and the **Pub/Sub-RS protocol**. After recalling on the GAG and Pub/Sub-RS notion, we present how to setup the project.

# Service oriented architecture with GAG and Pub/Sub-RS!

**Guarded Attribute Grammar (GAG) is a user-centric model for designing distributed collaborative systems**. Fundamentally, it perceives a collaborative process within a system as a service to execute, whose execution output informs about the process termination. Such a service can then be decomposed into sub-services by means of a production of an attribute grammar, for instance the treatment of an application files that relies on two division d1 and d2 can be modeled by a **rule** **s0 -> s1 s2**. Meaning that the application process is handled by the service s0 which in turn uses the two services s1 and s2 offered by the divisions d1 and d2 of the company.
Similar to s0, s1 and s2 may also rely on other sub services of the same company in order to compute they output.

**The Pub/Sub-RS is a protocol for the effective exchange of data produced collaboratively and incrementally**. When a service s0 decomposes into two service s1 and s2, inputs of s0 are available in the execution context of s1 and s2, and outputs of s1 and s2 are available in the execution context of s0. Moreover outputs of s1 are also available in the execution context of s2 ( they can be passed as s2 inputs)  and vice versa. Since the execution is distributed and decentralized the Pub/Sub-RS guarantees that the software component that execute a service (like s1, s2, s3) is subscribed to the data that should be available in its execution context. Moreover for semi structured data where part of which is produced by several services, the Pub-Sub/RS ensures that the components are notified in real time as the part of the data (to which they have subscribed) is being produced.


# Project Setup
The current repository implements an example case of a PhD defense application process conducted in a doctoral School, 4 organization divisions are considered, namely : the student home department, the doctoral School, the financial division and the rectorate office. To setup the project in windows computer you can follow the step bellow:

- Clone the repository;
- Download Java 1.8 here (link) and install it. Don't forget to include the path of the jdk bin folder to your path after installation. **Important!** the project has been tested with Java 1.8, so please use this version to make sure you set up the project correctly.
- Download the eclipse SON ide here (link) and extract it.
- Launch eclipse and select the cloned repository as the eclipse workspace.
- Go to the project startereditor . There you will find the ExportClient.product file.
- Open the ExportClient.product file and click on the launch button present at the top right of the file.

Once you launched the project by following the steps above, you will find five interface corresponding to the five actors of the process (student, home department, doctoral school, financial division and rectorate). You can start the process by clicking on the button "Submit a thesis" present on the student interfaces. The others interfaces will allow you to handle the student submission.  The student interface to submit a thesis will look like the following figure:
![Student Interface](https://raw.githubusercontent.com/Service-BP-Dev-Team/GAG-With-PubSub-RS/main/screenshots/Fig7.png)
