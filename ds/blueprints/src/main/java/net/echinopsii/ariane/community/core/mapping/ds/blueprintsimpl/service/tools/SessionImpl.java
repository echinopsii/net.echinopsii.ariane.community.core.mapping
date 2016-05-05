/**
 * Mapping Datastore Interface :
 * provide a Mapping DS domain, repository and service interfaces
 * Copyright (C) 2016 echinopsii
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
package net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.service.tools;

import net.echinopsii.ariane.community.core.mapping.ds.MappingDSException;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;

import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

import static net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.service.tools.SessionImpl.SessionWorker.*;

public class SessionImpl implements Session {

    class SessionWorkerRequest {
        String action;
        Object instance;
        Method method;
        Object[] args;

        public SessionWorkerRequest(String action, Object instance, Method method, Object[] args) {
            this.action = action;
            this.instance = instance;
            this.method = method;
            this.args = args;
        }

        public String getAction() {
            return action;
        }

        public Object getInstance() {
            return instance;
        }

        public Method getMethod() {
            return method;
        }

        public Object[] getArgs() {
            return args;
        }
    }

    class SessionWorkerReply {
        boolean error;
        Object ret;
        String error_msg;

        public SessionWorkerReply(boolean error, Object ret, String error_msg) {
            this.error = error;
            this.ret = ret;
            this.error_msg = error_msg;
        }

        public boolean isError() {
            return error;
        }

        public Object getRet() {
            return ret;
        }

        public String getError_msg() {
            return error_msg;
        }
    }

    class SessionWorker implements Runnable {

        public final static String EXECUTE = "EXECUTE";
        public final static String STOP = "STOP";

        private LinkedBlockingQueue<SessionWorkerRequest> fifoInputQ = new LinkedBlockingQueue<SessionWorkerRequest>();
        private LinkedBlockingQueue<SessionWorkerReply> fifoOutputQ = new LinkedBlockingQueue<SessionWorkerReply>();
        private boolean running = true;

        @Override
        public void run() {
            while (running) {
                SessionWorkerRequest msg = fifoInputQ.poll();
                if (msg.getAction().endsWith(STOP)) running = false;
                else if (msg.getAction().equals(EXECUTE)){
                    try {
                        Object ret = msg.getMethod().invoke(msg.getInstance(), msg.getArgs());
                        fifoOutputQ.put(new SessionWorkerReply(false, ret, null));
                    } catch (Exception e) {
                        try {
                            fifoOutputQ.put(new SessionWorkerReply(true, null, e.getMessage()));
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        }

        public LinkedBlockingQueue<SessionWorkerRequest> getFifoInputQ() {
            return fifoInputQ;
        }

        public LinkedBlockingQueue<SessionWorkerReply> getFifoOutputQ() {
            return fifoOutputQ;
        }
    }

    private String sessionId = null;
    private String sessionOwner = null;

    private SessionWorker sessionWorker = new SessionWorker();
    private Thread sessionThread = new Thread(sessionWorker);

    public SessionImpl(String clientId, String owner) {
        this.sessionId = clientId + '-' + UUID.randomUUID();
        this.sessionOwner = owner;
        this.sessionThread.setName(sessionId);
    }

    @Override
    public String getSessionID() {
        return sessionId;
    }

    @Override
    public String getSessionOwner() {
        return sessionOwner;
    }

    @Override
    public Session stop() {
        try {
            this.sessionWorker.getFifoInputQ().put(new SessionWorkerRequest(STOP, null, null, null));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public Session start() {
        this.sessionThread.start();
        return this;
    }

    @Override
    public boolean isRunning() {
        return false;
    }

    @Override
    public Object execute(String user, Object o, Method m, Object[] args) throws MappingDSException {
        try {
            this.sessionWorker.getFifoInputQ().put(new SessionWorkerRequest(EXECUTE, o, m, args));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        SessionWorkerReply reply = this.sessionWorker.getFifoOutputQ().poll();
        if (! reply.isError()) return reply.getRet();
        else throw new MappingDSException(reply.getError_msg());
    }
}
