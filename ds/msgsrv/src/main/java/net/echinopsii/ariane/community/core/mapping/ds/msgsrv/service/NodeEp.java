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

import net.echinopsii.ariane.community.core.mapping.ds.domain.Node;
import net.echinopsii.ariane.community.core.mapping.ds.msgsrv.momsp.MappingMsgsrvMomSP;
import net.echinopsii.ariane.community.core.mapping.ds.service.MappingSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.NodeSce;
import net.echinopsii.ariane.community.messaging.api.AppMsgWorker;
import net.echinopsii.ariane.community.messaging.api.MomMsgTranslator;

import java.util.Map;

public class NodeEp {

    static class NodeWorker implements AppMsgWorker {
        @Override
        public Map<String, Object> apply(Map<String, Object> message) {
            Object oOperation = message.get(MappingSce.GLOBAL_OPERATION_FDN);
            String operation;

            if (oOperation==null)
                operation = MappingSce.GLOBAL_OPERATION_NOT_DEFINED;
            else
                operation = oOperation.toString();

            switch (operation) {
                case NodeSce.OP_CREATE_NODE:
                    break;
                case NodeSce.OP_DELETE_NODE:
                    break;
                case NodeSce.OP_GET_NODE:
                    break;
                case NodeSce.OP_GET_NODE_BY_EPURL:
                    break;
                case NodeSce.OP_GET_NODE_BY_NAME:
                    break;
                case NodeSce.OP_GET_NODES:
                    break;
                case Node.OP_SET_NODE_NAME:
                    break;
                case Node.OP_SET_NODE_CONTAINER:
                    break;
                case Node.OP_SET_NODE_PARENT_NODE:
                    break;
                case Node.OP_ADD_ENDPOINT:
                case Node.OP_REMOVE_ENDPOINT:
                    break;
                case Node.OP_ADD_NODE_PROPERTY:
                case Node.OP_REMOVE_NODE_PROPERTY:
                    break;
                case Node.OP_ADD_NODE_CHILD_NODE:
                case Node.OP_REMOVE_NODE_CHILD_NODE:
                    break;
                case Node.OP_ADD_TWIN_NODE:
                case Node.OP_REMOVE_TWIN_NODE:
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
                    NodeSce.Q_MAPPING_CONTAINER_SERVICE, new NodeWorker()
            );
    }
}
