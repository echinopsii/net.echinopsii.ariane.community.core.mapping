################################################################
# Neo4j
#
# neo4j-server.properties - runtime operational settings
#
################################################################

#***************************************************************
# Server configuration
#***************************************************************

# location of the database directory
org.neo4j.server.database.location=ariane/neo4j/data/graph.db

# Low-level graph engine tuning file
org.neo4j.server.db.tuning.properties=ariane/neo4j/conf/neo4j.properties

# Let the webserver only listen on the specified IP. Default is localhost (only
# accept local connections). Uncomment to allow any connection. Please see the
# security section in the neo4j manual before modifying this.
#org.neo4j.server.webserver.address=0.0.0.0

# Require (or disable the requirement of) auth to access Neo4j
dbms.security.auth_enabled=true
dbms.security.auth_store.location=ariane/neo4j/data/dbms/auth

#
# HTTP Connector
#

# http port (for all data, administrative, and UI access)
org.neo4j.server.webserver.port=7474

#
# HTTPS Connector
#

# Turn https-support on/off
org.neo4j.server.webserver.https.enabled=true

# https port (for all data, administrative, and UI access)
org.neo4j.server.webserver.https.port=7473

# Certificate location (auto generated if the file does not exist)
dbms.security.tls_certificate_file=ariane/neo4j/conf/ssl/snakeoil.cert

# Private key location (auto generated if the file does not exist)
dbms.security.tls_key_file=ariane/neo4j/conf/ssl/snakeoil.key

# Internally generated keystore (don't try to put your own
# keystore there, it will get deleted when the server starts)
org.neo4j.server.webserver.https.keystore.location=ariane/neo4j/data/keystore

# Comma separated list of JAX-RS packages containing JAX-RS resources, one
# package name for each mountpoint. The listed package names will be loaded
# under the mountpoints specified. Uncomment this line to mount the
# org.neo4j.examples.server.unmanaged.HelloWorldResource.java from
# neo4j-server-examples under /examples/unmanaged, resulting in a final URL of
# http://localhost:7474/examples/unmanaged/helloworld/{nodeId}
#org.neo4j.server.thirdparty_jaxrs_classes=org.neo4j.examples.server.unmanaged=/examples/unmanaged


#*****************************************************************
# HTTP logging configuration
#*****************************************************************

# HTTP logging is disabled. HTTP logging can be enabled by setting this
# property to 'true'.
org.neo4j.server.http.log.enabled=false

# Logging policy file that governs how HTTP log output is presented and
# archived. Note: changing the rollover and retention policy is sensible, but
# changing the output format is less so, since it is configured to use the
# ubiquitous common log format
org.neo4j.server.http.log.config=ariane/neo4j/conf/neo4j-http-logging.xml


#*****************************************************************
# Administration client configuration
#*****************************************************************

# location of the servers round-robin database directory. Possible values:
# - absolute path like /var/rrd
# - path relative to the server working directory like data/rrd
# - commented out, will default to the database data directory.
org.neo4j.server.webadmin.rrdb.location=ariane/neo4j/data/rrd

dbms.querylog.filename=ariane/neo4j/data/log/queries.log
