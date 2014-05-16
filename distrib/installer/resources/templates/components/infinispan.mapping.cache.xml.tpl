<?xml version="1.0" encoding="UTF-8"?>
<infinispan xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xmlns="urn:infinispan:config:6.0"
            xsi:schemaLocation="urn:infinispan:config:6.0 http://www.infinispan.org/schemas/infinispan-config-6.0.xsd">
   <global>
      <globalJmxStatistics enabled="true" cacheManagerName="CC_MAPPING"/>
      <transport
            transportClass="org.infinispan.remoting.transport.jgroups.JGroupsTransport"
            clusterName="CC_Mapping_Cluster"
            distributedSyncTimeout="50000">
         <!-- Note that the JGroups transport uses sensible defaults if no configuration property is defined. -->
         <properties>
            <property name="configurationFile" value="##JGroupConfPath"/>
         </properties>
         <!-- See the JGroupsTransport javadocs for more flags -->
      </transport>
   </global>

   <namedCache name="infinispan.mapping.cache">
      <clustering mode="invalidation">
         <stateTransfer fetchInMemoryState="false" timeout="20000"/>
         <sync replTimeout="20000"/>
      </clustering>
      <locking isolationLevel="READ_COMMITTED" concurrencyLevel="1000"
               lockAcquisitionTimeout="15000" useLockStriping="false"/>
      <eviction maxEntries="10000" strategy="LRU"/>
      <expiration maxIdle="3600000" wakeUpInterval="5000"/>
      <transaction transactionMode="NON_TRANSACTIONAL"/>
   </namedCache>

</infinispan>
