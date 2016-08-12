## mapping impl bundle name
mapping.ds.bundle.name                 = ##mappingBundleName

## BLUEPRINTS BUNDLE CONF
mapping.ds.blueprints.implementation   = Neo4j
mapping.ds.blueprints.neo4j.configfile = ##mappingNeo4JConfigFile
# mapping.ds.blueprints.directory       = ##mappingDirectory
# - other configuration properties available :
# mapping.ds.blueprints.url
# mapping.ds.blueprints.user
# mapping.ds.blueprints.password

## MESSAGING BUNDLE CONF
mom_cli.impl      = ##MCLI_IMPL
mom_host.fqdn     = ##MHOST_FQDN
mom_host.port     = ##MHOST_PORT
mom_host.user     = ##MHOST_USER
mom_host.password = ##MHOST_PASSWD

# SPECIFIC NATS MESSAGING PROVIDER CONFIGURATION
# mom_cli.nats.connection_name = Ariane Mapping Messaging Proxy

# SPECIFIC RABBITMQ MESSAGING PROVIDER CONFIGURATION
mom_host.rbq_vhost = ##MHOST_RBQ_VHOST
# mom_cli.rabbitmq.product = Ariane
# mom_cli.rabbitmq.information = Ariane Mapping Messaging Proxy
# mom_cli.rabbitmq.copyright = AGPLv3
# mom_cli.rabbitmq.version = ##MCLI_RBQ_VERSION

# ARIANE CLIENT PROPERTIES
ariane.pgurl = http://##ARIANE_FQDN:6969/ariane
ariane.osi   = ##ARIANE_HOST
ariane.otm   = ##ARIANE_OPS_TEAM
ariane.app   = Ariane Core Front MS
ariane.cmp   = echinopsii