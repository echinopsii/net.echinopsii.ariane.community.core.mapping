/**
 * Mapping Messaging Server
 * Session service messaging endpoint
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
package net.echinopsii.ariane.community.core.mapping.ds.msgsrv.service;

import net.echinopsii.ariane.community.core.mapping.ds.service.MappingSce;
import net.echinopsii.ariane.community.core.mapping.ds.msgsrv.momsp.MappingMsgsrvMomSP;
import net.echinopsii.ariane.community.messaging.api.AppMsgWorker;

import java.util.Map;

public class SessionEp {

    static class SessionWorker implements AppMsgWorker {
        @Override
        public Map<String, Object> apply(Map<String, Object> message) {
            return null;
        }
    }

    public static void start() {
        if (MappingMsgsrvMomSP.getShared_mom_con() != null && MappingMsgsrvMomSP.getShared_mom_con().isConnected())
            MappingMsgsrvMomSP.getShared_mom_con().getServiceFactory().requestService(MappingSce.SESSION_MGR_Q, new SessionWorker());
    }
}
