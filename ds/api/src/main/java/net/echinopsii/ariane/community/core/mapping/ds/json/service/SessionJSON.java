/**
 * Domain Service JSON Serialization
 * Session json serialization
 *
 * Copyright (C) 2016  Mathilde Ffrench
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
package net.echinopsii.ariane.community.core.mapping.ds.json.service;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import net.echinopsii.ariane.community.core.mapping.ds.json.ToolBox;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class SessionJSON {

    public final static String SE_ID_TOKEN = "SessionID";

    public final static void session2JSON(Session session, JsonGenerator jgenerator) throws IOException {
        jgenerator.writeStartObject();
        jgenerator.writeStringField(SE_ID_TOKEN, session.getSessionID());
        jgenerator.writeEndObject();
    }

    public final static void oneSession2JSON(Session session, ByteArrayOutputStream outStream) throws IOException {
        JsonGenerator jgenerator = ToolBox.jFactory.createJsonGenerator(outStream, JsonEncoding.UTF8);
        SessionJSON.session2JSON(session, jgenerator);
        jgenerator.close();
    }
}