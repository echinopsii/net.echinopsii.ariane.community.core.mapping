/**
 * Mapping Messaging Server
 * Bootstrap
 * Copyright (C) 27/05/16 echinopsii
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.echinopsii.ariane.community.core.mapping.ds.msgsrv;

import net.echinopsii.ariane.community.core.mapping.ds.msgsrv.service.*;
import net.echinopsii.ariane.community.core.mapping.ds.service.MappingSce;
import net.echinopsii.ariane.community.core.mapping.ds.msgsrv.momsp.MappingMsgsrvMomSP;
import net.echinopsii.ariane.community.core.mapping.ds.service.proxy.SProxMappingSce;
import org.apache.felix.ipojo.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Dictionary;

@Component(managedservice="net.echinopsii.ariane.community.core.MappingMsgsrvManagedService")
@Instantiate
public class MappingMsgsrvBootstrap {
    private static final Logger log = LoggerFactory.getLogger(MappingMsgsrvBootstrap.class);
    private static final String MAPPING_MESSAGING_SRV = "Ariane Mapping Messaging Server";
    private boolean isStarted = false;
    private boolean isInit = false;

    @Requires
    private SProxMappingSce mappingBSce = null;
    private static SProxMappingSce mappingSce = null;

    @Bind
    public void bindMappingBSce(SProxMappingSce s) {
        log.debug("Bound to mapping service...");
        mappingBSce = s;
        mappingSce = s;
    }

    @Unbind
    public void unbindMappingBSce() {
        log.debug("Unbound from mapping service...");
        mappingBSce = null;
        mappingSce = null;
    }

    private void start() {
        if (MappingMsgsrvMomSP.start()) {
            SessionEp.start();
            ClusterEp.start();
            ContainerEp.start();
            NodeEp.start();
            GateEp.start();
            EndpointEp.start();
            LinkEp.start();
            TransportEp.start();
            MappingEp.start();
            MapEp.start();
            isStarted = true;
        }
    }

    @Validate
    public void validate() throws Exception {
        while(!isInit){
            log.debug("Waiting config initialization for {}. Sleep some times...", MAPPING_MESSAGING_SRV);
            Thread.sleep(10);
        }
        start();
        log.info("{} is started", MAPPING_MESSAGING_SRV);
    }

    private void stop() throws Exception {
        MappingMsgsrvMomSP.stop();
        isStarted = false;
    }

    @Invalidate
    public void invalidate() throws Exception {
        stop();
        log.info("{} is stopped", MAPPING_MESSAGING_SRV);
    }

    @Updated
    public void updated(final Dictionary properties) {
        log.debug("{} is being updated by {}", new Object[]{MAPPING_MESSAGING_SRV, Thread.currentThread().toString()});
        try {
            if (MappingMsgsrvMomSP.init(properties)) {
                if (isStarted) {
                    final Runnable applyConfigUpdate = new Runnable() {
                        @Override
                        public void run() {
                            try {
                                log.debug("{} will be restart to apply configuration changes...", MAPPING_MESSAGING_SRV);
                                stop();
                                start();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    new Thread(applyConfigUpdate).start();
                }
                isInit = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static SProxMappingSce getMappingSce() {
        return mappingSce;
    }
}