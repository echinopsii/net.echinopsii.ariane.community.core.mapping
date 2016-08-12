# COMMON MESSAGING PROVIDER CONFIGURATION
mom_cli.impl=##MCLI_IMPL
mom_host.fqdn=##MHOST_FQDN
mom_host.port=##MHOST_PORT
mom_host.user=##MHOST_USER
mom_host.password=##MHOST_PASSWD

# SPECIFIC NATS MESSAGING PROVIDER CONFIGURATION
# mom_cli.nats.connection_name=##MCLI_NATS_NAME

# SPECIFIC RABBITMQ MESSAGING PROVIDER CONFIGURATION
mom_host.rbq_vhost=##MHOST_RBQ_VHOST
# mom_cli.rabbitmq.product=Ariane
# mom_cli.rabbitmq.information=Ariane Mapping Remote Messaging Service
# mom_cli.rabbitmq.copyright=AGPLv3
mom_cli.rabbitmq.version=##MCLI_RBQ_VERSION

# ARIANE CLIENT PROPERTIES
ariane.pgurl=http://##ARIANE_FQDN:6969/ariane
ariane.osi=##ARIANE_HOST
ariane.otm=##ARIANE_OPS_TEAM
ariane.app=Ariane Messaging Server
ariane.cmp=echinopsii
