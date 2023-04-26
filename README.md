# Introduction
This repository is an implementation of a service-based collaborative system using the **GAG model** and the **Pub/Sub-RS protocol**. After a reminder on the notions of GAG and Pub/Sub-RS, we present the project setup.

# Service oriented architecture with GAG and Pub/Sub-RS !

**The Guarded Attribute Grammar (GAG) is a user-centric model for the design of distributed collaborative systems**. Basically, it perceives a collaborative process within a system as a service to be executed, whose execution result informs about the process completion. Such a service can then be decomposed into sub-services through a rule of the attribute grammar. For example, the processing of an application that relies on two divisions d1 and d2 can be modeled by a **rule** **s0 -> s1 s2**. This means that the application process is handled by service s0, which in turn uses the two services s1 and s2 offered by divisions d1 and d2 of the enterprise.
Like s0, s1 and s2 can also rely on other sub-services of the same company to compute their results.


**Pub/Sub-RS is a protocol for the efficient exchange of collaboratively and incrementally produced data**. When a service s0 decomposes into two services s1 and s2, the inputs of s0 are available in the execution context of s1 and s2, and the outputs of s1 and s2 are available in the execution context of s0. In addition, the outputs of s1 are also available in the execution context of s2 (they can be passed as inputs to s2) and vice versa. Since the execution is distributed and decentralized, Pub/Sub-RS ensures that the software component that executes a service (such as s1, s2, s3) is subscribed to the data that should be available in its execution context. In addition, for semi-structured data whose parts are produced by multiple services, the Pub-Sub/RS ensures that the components are notified in real time as the parts of these data (to which they have subscribed) are produced.


# Project Setup
The current repository implements an example of a doctoral defense application process conducted in a doctoral school. Four organizational divisions are considered, namely: the student's home department, the doctoral school, the financial division and the rector's office. To install the project on a Windows computer, you can follow the steps below:

- Clone the repository;
- Download Java 1.8 here ([Java 1.8](https://drive.google.com/file/d/11E0Bwy2yhdBJTMJzr0bt8gRiXIIVd4A6/view?usp=sharing)) and install it. Don't forget to include the path of the jdk bin folder in your path after the installation. **Important:** the project has been tested with Java 1.8, so please use this version to make sure you set up the project correctly.
- Download the eclipse SON ide here ([Eclipse son](https://drive.google.com/file/d/1SaJ4y0-0pVjsL60zka_MTCVopbG9twku/view?usp=sharing)) and extract it.
- Launch eclipse and select the cloned repository as eclipse workspace.
- Go to the project editor (startereditor). There you will find the file ExportClient.product.
- Open the file ExportClient.product and click on the launch button at the top right of the file.

Once you have started the project by following the steps above, you will find five interfaces corresponding to the five actors of the process (student, home department, doctoral school, financial division and rectorate). You can start the process by clicking on the "Submit a thesis" button on the student interface. The other interfaces will allow you to manage the student's submission.  The interface for a student to submit a thesis is as follows:

![Student Interface](https://raw.githubusercontent.com/Service-BP-Dev-Team/GAG-With-PubSub-RS/main/screenshots/Fig7.png)

