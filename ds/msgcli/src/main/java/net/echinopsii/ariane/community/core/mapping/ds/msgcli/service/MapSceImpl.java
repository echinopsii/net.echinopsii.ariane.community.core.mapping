/**
 * Mapping Datastore Messaging Driver Implementation :
 * provide a Mapping DS domain, repository and service messaging driver implementation
 * Copyright (C) 2016 echinopsii
 * Author: mathilde.ffrench@echinopsii.net
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
package net.echinopsii.ariane.community.core.mapping.ds.msgcli.service;

import net.echinopsii.ariane.community.core.mapping.ds.MapperEmptyResultException;
import net.echinopsii.ariane.community.core.mapping.ds.MapperParserException;
import net.echinopsii.ariane.community.core.mapping.ds.MappingDSException;
import net.echinopsii.ariane.community.core.mapping.ds.msgcli.momsp.MappingMsgcliMomSP;
import net.echinopsii.ariane.community.core.mapping.ds.service.MapSce;
import net.echinopsii.ariane.community.messaging.api.AppMsgWorker;
import net.echinopsii.ariane.community.messaging.api.MomMsgTranslator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.concurrent.TimeoutException;

public class MapSceImpl implements MapSce {

    private static final Logger log = LoggerFactory.getLogger(MapSceImpl.class);

    @Override
    public String getMapJSON(String mapperQuery) throws MappingDSException {
        String jsonMap = null;
        java.util.Map<String, Object> message = new HashMap<>();
        message.put(MomMsgTranslator.OPERATION_FDN, OP_GET_MAP);
        message.put(MapSce.PARAM_MAPPER_QUERY, mapperQuery);
        java.util.Map<String, Object> retMsg = null;
        try {
            retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, MapSce.Q_MAPPING_MAP_SERVICE, new AppMsgWorker() {
                @Override
                public java.util.Map<String, Object> apply(java.util.Map<String, Object> message) {
                    return message;
                }
            });
        } catch (TimeoutException e) {
            throw new MappingDSException(e.getMessage());
        }
        int rc = (int)retMsg.get(MomMsgTranslator.MSG_RC);
        if (rc != 0) {
            String msg_err = (String) retMsg.get(MomMsgTranslator.MSG_ERR);
            switch (rc) {
                case  MomMsgTranslator.MSG_RET_BAD_REQ:
                    throw new MapperParserException(msg_err);
                case MomMsgTranslator.MSG_RET_NOT_FOUND:
                    throw new MapperEmptyResultException(msg_err);
                default:
                    throw new MappingDSException(msg_err);
            }
        } else {
            Object retmap = retMsg.get(MomMsgTranslator.MSG_BODY);
            if (retmap instanceof String)
                jsonMap = (String) retMsg.get(MomMsgTranslator.MSG_BODY);
            else if (retmap instanceof byte[]) {
                jsonMap = new String((byte[])retmap);
            }

        }
        return jsonMap;
    }
}