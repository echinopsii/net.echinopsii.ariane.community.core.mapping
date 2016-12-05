/**
 * Mapping Messaging Server
 * Map service messaging endpoint
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

import net.echinopsii.ariane.community.core.mapping.ds.MapperEmptyResultException;
import net.echinopsii.ariane.community.core.mapping.ds.MapperParserException;
import net.echinopsii.ariane.community.core.mapping.ds.msgsrv.MappingMsgsrvBootstrap;
import net.echinopsii.ariane.community.core.mapping.ds.msgsrv.momsp.MappingMsgsrvMomSP;
import net.echinopsii.ariane.community.core.mapping.ds.service.MapSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.MappingSce;
import net.echinopsii.ariane.community.messaging.api.AppMsgWorker;
import net.echinopsii.ariane.community.messaging.api.MomLogger;
import net.echinopsii.ariane.community.messaging.api.MomMsgTranslator;
import net.echinopsii.ariane.community.messaging.common.MomLoggerFactory;
import org.slf4j.Logger;

import java.util.Map;

public class MapEp {
    private static final Logger log = MomLoggerFactory.getLogger(MapEp.class);

    static class MapWorker implements AppMsgWorker {

        @Override
        public Map<String, Object> apply(Map<String, Object> message) {
            Object oOperation = message.get(MomMsgTranslator.OPERATION_FDN);
            String operation;
            String query;
            if (message.containsKey(MomMsgTranslator.MSG_TRACE)) ((MomLogger)log).setMsgTraceLevel(true);
            ((MomLogger)log).traceMessage("MapWorker.apply - in", message, MappingSce.GLOBAL_PARAM_PAYLOAD);

            if (oOperation==null)
                operation = MomMsgTranslator.OPERATION_NOT_DEFINED;
            else
                operation = oOperation.toString();

            switch (operation) {
                case MapSce.OP_GET_MAP:
                    query = (String) message.get(MapSce.PARAM_MAPPER_QUERY);
                    try {
                        String map = MappingMsgsrvBootstrap.getMappingSce().getMapSce().getMapJSON(query);
                        message.put(MomMsgTranslator.MSG_BODY, map);
                        message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_SUCCESS);
                    } catch (MapperParserException e) {
                        String result = e.getMessage();
                        message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                        message.put(MomMsgTranslator.MSG_ERR, result);
                    } catch (MapperEmptyResultException e) {
                        String result = e.getMessage();
                        message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_NOT_FOUND);
                        message.put(MomMsgTranslator.MSG_ERR, result);
                    } catch (Exception e) {
                        log.error("Original query is : " + query);
                        log.error(e.getMessage());
                        e.printStackTrace();
                        String result = e.getMessage();
                        message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_SERVER_ERR);
                        message.put(MomMsgTranslator.MSG_ERR, result);
                    }
                    break;
                case MomMsgTranslator.OPERATION_NOT_DEFINED:
                    message.put(MomMsgTranslator.MSG_RC, 1);
                    message.put(MomMsgTranslator.MSG_ERR, "Operation not defined ! ");
                    break;
                default:
                    message.put(MomMsgTranslator.MSG_RC, 1);
                    message.put(MomMsgTranslator.MSG_ERR, "Unknown operation (" + operation + ") ! ");
                    break;
            }
            ((MomLogger)log).traceMessage("MapWorker.apply - out", message, MappingSce.GLOBAL_PARAM_PAYLOAD);
            if (message.containsKey(MomMsgTranslator.MSG_TRACE)) ((MomLogger)log).setMsgTraceLevel(false);
            return message;
        }
    }

    public static void start() {
        if (MappingMsgsrvMomSP.getSharedMoMConnection() != null && MappingMsgsrvMomSP.getSharedMoMConnection().isConnected()) {
            MappingMsgsrvMomSP.getSharedMoMConnection().getServiceFactory().msgGroupRequestService(
                    MapSce.Q_MAPPING_MAP_SERVICE, new MapWorker()
            );
            log.info("Ariane Mapping Messaging Service is waiting message on  " + MapSce.Q_MAPPING_MAP_SERVICE + "...");
        }
    }
}
