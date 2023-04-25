# Introduction
This repository is an implementation of service-based collaborative systems using the **GAG model** and the **Pub/Sub-RS protocol**. The GAG model (link) is a model for distributed collaborative system and the Pub/Sub-RS is a protocol for the asynchronous exchange of data produced collaborative. This readme is organized as follows: The first section (Project setup) explain how to setup and run the project in your computer. The second and last one present a short recall on the notion of GAG and Pub/Sub-RS.

# Project Setup

# Service oriented architecture with GAG and Pub/Sub-RS!

Guarded Attribute Grammar is a user-centric model for designing distributed collaborative systems. Fundamentally, a collaborative process within a system is modeled through a service to execute. Such a service can then be decomposed into sub-services by means of a production of an attribute grammar, for instance the treatment of an applications files that relies on two department d1 and d2 can be modeled by a **rule** **s0 -> s1 s2**. Meaning that the application process is handled by the service s0 with in turn uses the two services s1 and s2 offered by the departments d1 and d2 of the company.
Note that those services s1 and s2 may also rely on other sub services of the same company and in this case additional rules are added to represent the decomposition of service s1 and s2.

The Pub/Sub-RS is necessary for the distributed data exchange between the services involved in a process. When a service s0 decomposes into two service s1 and s2, inputs of s0 are available in the execution context of s1 and s2, and outputs of s1 and s2 are available in the execution context of s0. Moreover outputs of s1 are also available in the execution context of s2 ( they can be pass as s2 inputs)  and vice versa. Since the execution is distributed and decentralized the Pub/Sub-RS guarantees that the software component that execute each service (s1, s2, s3) is subscribed to the data that should be available in their execution context. Moreover for semi structured data where part of which is produced by several services, the Pub-Sub/RS ensures that the components are notified in real time as the part of the data they have subscribed is being produced.

