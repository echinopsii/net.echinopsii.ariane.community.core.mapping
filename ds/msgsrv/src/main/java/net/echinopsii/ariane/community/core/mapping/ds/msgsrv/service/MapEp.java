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
import net.echinopsii.ariane.community.core.mapping.ds.MappingDSException;
import net.echinopsii.ariane.community.core.mapping.ds.domain.*;
import net.echinopsii.ariane.community.core.mapping.ds.json.ToolBox;
import net.echinopsii.ariane.community.core.mapping.ds.json.service.MapJSON;
import net.echinopsii.ariane.community.core.mapping.ds.msgsrv.MappingMsgsrvBootstrap;
import net.echinopsii.ariane.community.core.mapping.ds.msgsrv.momsp.MappingMsgsrvMomSP;
import net.echinopsii.ariane.community.core.mapping.ds.service.MapSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.MappingSce;
import net.echinopsii.ariane.community.messaging.api.AppMsgWorker;
import net.echinopsii.ariane.community.messaging.api.MomMsgTranslator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;

public class MapEp {
    static class MapWorker implements AppMsgWorker {

        @Override
        public Map<String, Object> apply(Map<String, Object> message) {
            Object oOperation = message.get(MappingSce.GLOBAL_OPERATION_FDN);
            String operation;
            String query;

            if (oOperation==null)
                operation = MappingSce.GLOBAL_OPERATION_NOT_DEFINED;
            else
                operation = oOperation.toString();

            switch (operation) {
                case MapSce.OP_GET_MAP:
                    query = (String) message.get(MapSce.PARAM_MAPPER_QUERY);
                    try {
                        String map = MappingMsgsrvBootstrap.getMappingSce().getMapSce().getMapJSON(query);
                        message.put(MomMsgTranslator.MSG_BODY, map);
                        message.put(MomMsgTranslator.MSG_RC, MappingSce.MAPPING_SCE_RET_SUCCESS);
                    } catch (MapperParserException e) {
                        String result = e.getMessage();
                        message.put(MomMsgTranslator.MSG_RC, MappingSce.MAPPING_SCE_RET_BAD_REQ);
                        message.put(MomMsgTranslator.MSG_ERR, result);
                    } catch (MapperEmptyResultException e) {
                        String result = e.getMessage();
                        message.put(MomMsgTranslator.MSG_RC, MappingSce.MAPPING_SCE_RET_NOT_FOUND);
                        message.put(MomMsgTranslator.MSG_ERR, result);
                    } catch (Exception e) {
                        log.error("Original query is : " + query);
                        log.error(e.getMessage());
                        e.printStackTrace();
                        String result = e.getMessage();
                        message.put(MomMsgTranslator.MSG_RC, MappingSce.MAPPING_SCE_RET_SERVER_ERR);
                        message.put(MomMsgTranslator.MSG_ERR, result);
                    }
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

    private static final Logger log = LoggerFactory.getLogger(MapEp.class);

    public static void start() {
        if (MappingMsgsrvMomSP.getSharedMoMConnection() != null && MappingMsgsrvMomSP.getSharedMoMConnection().isConnected())
            MappingMsgsrvMomSP.getSharedMoMConnection().getServiceFactory().requestService(
                    MapSce.Q_MAPPING_MAP_SERVICE, new MapWorker()
            );
    }
}
