#********************************************************************
# Property file references
#********************************************************************

wrapper.java.additional=-Dorg.neo4j.server.properties=conf/neo4j-server.properties
wrapper.java.additional=-Dlog4j.configuration=file:conf/log4j.properties

#********************************************************************
# JVM Parameters
#********************************************************************

wrapper.java.additional=-XX:+UseG1GC
wrapper.java.additional=-XX:-OmitStackTraceInFastThrow
wrapper.java.additional=-XX:hashCode=5

# Uncomment the following lines to enable garbage collection logging
#wrapper.java.additional=-Xloggc:data/log/neo4j-gc.log
#wrapper.java.additional=-XX:+PrintGCDetails
#wrapper.java.additional=-XX:+PrintGCDateStamps
#wrapper.java.additional=-XX:+PrintGCApplicationStoppedTime
#wrapper.java.additional=-XX:+PrintPromotionFailure
#wrapper.java.additional=-XX:+PrintTenuringDistribution

# Java Heap Size: by default the Java heap size is dynamically
# calculated based on available system resources.
# Uncomment these lines to set specific initial and maximum
# heap size in MB.
#wrapper.java.initmemory=512
#wrapper.java.maxmemory=512

#********************************************************************
# Wrapper settings
#********************************************************************
# path is relative to the bin dir
wrapper.pidfile=../data/neo4j-server.pid

#********************************************************************
# Wrapper Windows NT/2000/XP Service Properties
#********************************************************************
# WARNING - Do not modify any of these properties when an application
#  using this configuration file has been installed as a service.
#  Please uninstall the service before modifying this section.  The
#  service can then be reinstalled.

# Name of the service
wrapper.name=neo4j

# User account to be used for linux installs. Will default to current
# user if not set.
wrapper.user=

#********************************************************************
# Other Neo4j system properties
#********************************************************************
wrapper.java.additional=-Dneo4j.ext.udc.source=tarball
