
[![Build Status](https://travis-ci.org/metadatacenter/cadsr-reader.svg?branch=master)](https://travis-ci.org/metadatacenter/cadsr-reader)

caDSR Reader
============

This project reads XML-encoded [caDSR](https://wiki.nci.nih.gov/display/caDSR/caDSR+Wiki) [11179-based](http://metadata-standards.org/11179/)
Common Data Elements (CDEs).
At the moment is prints out a basic summary of each CDE. 
The code is designed to provide a starting point for reading CDEs for transformation into other formats.

XML files containing various CDE collections can be found [here](https://wiki.nci.nih.gov/display/caDSR/caDSR+Hosted+Data+Standards%2C+Downloads%2C+and+Transformation+Utilities).
Go to the area labeled "Download caDSR Community Released CDEs" and click on "caDSR Production CDEs in XML".
A large ZIP file can then be downloaded which will contain a dozen or so XML files containing caDSR CDEs.
Note: these enclosed XML files seemed to be encoded as ISO-LATIN-1 so convert them to UTF-8 so that the code will correctly process them.

The XML files can then be placed in a directory and processed by this software (see execution instructions below).

There is an example CDE XML file in the ```src/main/resources/xml/example``` directory.

The format of the caDSR CDEs is described by an [XML Schema document](https://github.com/metadatacenter/cadsr2cedar/blob/master/src/main/resources/xsd/DataElement_V4.0.xsd).
The [JAXB](http://www.oracle.com/technetwork/articles/javase/index-140168.html) library uses this document to generate Java classes to read the XML-encoded instances of caDSR CDEs.

The [core translation routines](https://github.com/metadatacenter/cadsr2cedar/blob/master/src/main/java/org/metadatacenter/ingestor/cadsr/CDEXMLInstances2CEDARCDEInstances.java)
extract information from the JAXB-generated Java objects and displays summary information about them.

Note that [JAXB bindings](https://github.com/metadatacenter/cadsr2cedar/blob/master/src/main/resources/xjb/bindings.xjb)
were required to rename some generated classes to avoid name collisions.

Also not that the [caDSR CDE XML Schema](https://github.com/metadatacenter/cadsr2cedar/blob/master/src/main/resources/xsd/DataElement_V4.0.xsd)
was produced semi-automatically from the caDSR-supplied [DTD-encoded schema](https://github.com/metadatacenter/cadsr2cedar/blob/master/src/main/resources/dtd/DataElement_V4.0.dtd).
We could not work with the DTD-encoded schema directly because the JAXB binding do not seem to work with DTD-based documents.

#### Building and Running

To build this library you must have the following items installed:

+ [Java 11](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
+ A tool for checking out a [Git](http://git-scm.com/) repository.
+ Apache's [Maven](http://maven.apache.org/index.html).

Get a copy of the latest code:

    git clone https://github.com/metadatacenter/cadsr-reader.git

Change into the ```cadsr-reader``` directory:

    cd cadsr-reader 

Then build it with Maven:

    mvn clean install

To process the supplied CDEs in the ```src/main/resources/xml/example``` directory:

    mvn exec:java

To process CDEs in a user-specified directory:

    mvn exec:java -Dexec.args="<directory containing CDE XML files>"
