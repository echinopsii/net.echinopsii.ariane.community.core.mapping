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

package net.echinopsii.ariane.community.core.mapping.msgsrv;

import net.echinopsii.ariane.community.core.mapping.ds.service.MappingSce;
import org.apache.felix.ipojo.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@Instantiate
public class MappingBootstrap {
    private static final Logger log = LoggerFactory.getLogger(MappingBootstrap.class);
    private static final String MAPPING_COMPONENT = "Ariane Mapping Component";

    @Requires
    private MappingSce mappingBSce = null;
    private static MappingSce mappingSce = null;

    @Bind
    public void bindMappingBSce(MappingSce s) {
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

    @Validate
    public void validate() throws Exception {
        log.info("{} is started", MAPPING_COMPONENT);
    }

    @Invalidate
    public void invalidate() throws Exception {
        log.info("{} is stopped", MAPPING_COMPONENT);
    }

    public static MappingSce getMappingSce() {
        return mappingSce;
    }
}