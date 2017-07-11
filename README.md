# UMLS Visualization

## Purpose
The primary goal of infamous Unified Medical Language System (UMLS) is to unify disparate medical standards for uniform and unambiguous semantic interpretation. For the past two decades, UMLS is used in research ranging from biomedical natural language processing to electronic health record and even enhancing UMLS Metathesaurus itself. _However, only few research efforts have focused on its visualization and none of them are designed for classroom and student learning environment. Further, none of the past visualization efforts leveraged advanced open-source graphing libraries and are not open-source software_. In this research project, we have: 
* Implemented a visualization framework for UMLS using data-driven technologies and REST API. 
* Evaluating (on-going) the software framework in classroom and teaching environment across multiple schools/departments to quantitatively understand its impact in learning.

### Software Required
* Java JDK 1.8
* Java Servlet 3.0
* MySQL 5.0+ and installed UMLS database
* Apache Tomcat 8

### Instructions
* The context.xml is provided in the src/main/resources. Copy this file into tomcat_base/conf
folder and change it accordingly. 
* You also need to have mysql JDBC connection JAR library in tomcat_base/lib folder.
