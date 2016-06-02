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

import net.echinopsii.ariane.community.core.mapping.ds.msgsrv.momsp.MappingMsgsrvMomSP;
import net.echinopsii.ariane.community.core.mapping.ds.service.ContainerSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.MappingSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;
import net.echinopsii.ariane.community.messaging.api.AppMsgWorker;
import net.echinopsii.ariane.community.messaging.api.MomMsgTranslator;

import java.util.Map;

public class ContainerEp {

    static class ContainerWorker implements AppMsgWorker {
        @Override
        public Map<String, Object> apply(Map<String, Object> message) {
            Object oOperation = message.get(MappingSce.MAPPING_SCE_OPERATION_FDN);
            String operation;
            String sid;
            String cid;
            String name;
            Session session = null;

            if (oOperation==null)
                operation = MappingSce.MAPPING_SCE_OPERATION_NOT_DEFINED;
            else
                operation = oOperation.toString();

            switch (operation) {
                case ContainerSce.CONTAINER_SCE_OP_CREATE:
                    break;
                case ContainerSce.CONTAINER_SCE_OP_DELETE:
                    break;
                case ContainerSce.CONTAINER_SCE_OP_GET:
                    break;
                case ContainerSce.CONTAINER_SCE_OP_GET_BY_PRIMARY_ADMIN_URL:
                    break;
                case ContainerSce.CONTAINER_SCE_OP_GETS:
                    break;
                case MappingSce.MAPPING_SCE_OPERATION_NOT_DEFINED:
                    message.put(MomMsgTranslator.MSG_RC, 1);
                    message.put(MomMsgTranslator.MSG_ERR, "Operation not defined ! ");
                    break;
                default:
                    message.put(MomMsgTranslator.MSG_RC, 1);
                    message.put(MomMsgTranslator.MSG_ERR, "Unknown operation (" + operation + ") ! ");
                    break;
            }
            return message;
        }
    }

    public static void start() {
        if (MappingMsgsrvMomSP.getSharedMoMConnection() != null && MappingMsgsrvMomSP.getSharedMoMConnection().isConnected())
            MappingMsgsrvMomSP.getSharedMoMConnection().getServiceFactory().requestService(
                    ContainerSce.MAPPING_CONTAINER_SERVICE_Q, new ContainerWorker()
            );
    }
}
