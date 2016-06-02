/**
 * Mapping Web Service :
 * provide a mapping DS Web Service and REST Service
 *
 * Copyright (C) 2013  Mathilde Ffrench
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

package net.echinopsii.ariane.community.core.mapping.ds.json.domain;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Link;
import net.echinopsii.ariane.community.core.mapping.ds.json.ToolBox;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;

public class LinkJSON {
    //private final static Logger  log   = LoggerFactory.getLogger(EndpointJSON.class);

    public static void link2JSON(Link link, JsonGenerator jgenerator) throws IOException {
        jgenerator.writeStartObject();
        jgenerator.writeStringField(Link.LK_ID_TOKEN, link.getLinkID());
        jgenerator.writeStringField(Link.LK_SEP_TOKEN, link.getLinkEndpointSource().getEndpointID());
        if (link.getLinkEndpointTarget()!=null)
            jgenerator.writeStringField(Link.LK_TEP_TOKEN, link.getLinkEndpointTarget().getEndpointID());
        jgenerator.writeStringField(Link.LK_TRP_TOKEN, link.getLinkTransport().getTransportID());
        jgenerator.writeEndObject();
    }

    public static void oneLink2JSON(Link link, ByteArrayOutputStream outStream) throws IOException {
        JsonGenerator jgenerator = ToolBox.jFactory.createJsonGenerator(outStream, JsonEncoding.UTF8);
        link2JSON(link, jgenerator);
        jgenerator.close();
    }

    public static void manyLinks2JSON(HashSet<Link> links, ByteArrayOutputStream outStream) throws IOException {
        JsonGenerator jgenerator = ToolBox.jFactory.createJsonGenerator(outStream, JsonEncoding.UTF8);
        jgenerator.writeStartObject();
        jgenerator.writeArrayFieldStart("links");
        for (Link current : links) LinkJSON.link2JSON(current, jgenerator);
        jgenerator.writeEndArray();
        jgenerator.writeEndObject();
        jgenerator.close();
    }

    public static class JSONDeserializedLink {
        private String linkID;
        private String linkSEPID;
        private String linkTEPID;
        private String linkTRPID;

        public String getLinkID() {
            return linkID;
        }

        public void setLinkID(String linkID) {
            this.linkID = linkID;
        }

        public String getLinkSEPID() {
            return linkSEPID;
        }

        public void setLinkSEPID(String linkSEPID) {
            this.linkSEPID = linkSEPID;
        }

        public String getLinkTEPID() {
            return linkTEPID;
        }

        public void setLinkTEPID(String linkTEPID) {
            this.linkTEPID = linkTEPID;
        }

        public String getLinkTRPID() {
            return linkTRPID;
        }

        public void setLinkTRPID(String linkTRPID) {
            this.linkTRPID = linkTRPID;
        }
    }

    public static JSONDeserializedLink JSON2Link(String payload) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(payload, JSONDeserializedLink.class);
    }
}