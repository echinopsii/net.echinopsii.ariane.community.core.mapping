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

import net.echinopsii.ariane.community.core.mapping.ds.domain.Container;
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
            Object oOperation = message.get(MappingSce.GLOBAL_OPERATION_FDN);
            String operation;
            String sid;
            String cid;
            String name;
            Session session = null;

            if (oOperation==null)
                operation = MappingSce.GLOBAL_OPERATION_NOT_DEFINED;
            else
                operation = oOperation.toString();

            switch (operation) {
                case ContainerSce.OP_CREATE_CONTAINER:
                    break;
                case ContainerSce.OP_DELETE_CONTAINER:
                    break;
                case ContainerSce.OP_GET_CONTAINER:
                    break;
                case ContainerSce.OP_GET_CONTAINER_BY_PRIMARY_ADMIN_URL:
                    break;
                case ContainerSce.OP_GET_CONTAINERS:
                    break;
                case Container.OP_SET_CONTAINER_NAME:
                    break;
                case Container.OP_SET_CONTAINER_COMPANY:
                    break;
                case Container.OP_SET_CONTAINER_PRODUCT:
                    break;
                case Container.OP_SET_CONTAINER_TYPE:
                    break;
                case Container.OP_SET_CONTAINER_PRIMARY_ADMIN_GATE:
                    break;
                case Container.OP_SET_CONTAINER_PARENT_CONTAINER:
                    break;
                case Container.OP_SET_CONTAINER_CLUSTER:
                    break;
                case Container.OP_ADD_CONTAINER_CHILD_CONTAINER:
                case Container.OP_REMOVE_CONTAINER_CHILD_CONTAINER:
                    break;
                case Container.OP_ADD_CONTAINER_GATE:
                case Container.OP_REMOVE_CONTAINER_GATE:
                    break;
                case Container.OP_ADD_CONTAINER_NODE:
                case Container.OP_REMOVE_CONTAINER_NODE:
                    break;
                case Container.OP_ADD_CONTAINER_PROPERTY:
                case Container.OP_REMOVE_CONTAINER_PROPERTY:
                    break;
                case MappingSce.GLOBAL_OPERATION_NOT_DEFINED:
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
                    ContainerSce.Q_MAPPING_CONTAINER_SERVICE, new ContainerWorker()
            );
    }
}