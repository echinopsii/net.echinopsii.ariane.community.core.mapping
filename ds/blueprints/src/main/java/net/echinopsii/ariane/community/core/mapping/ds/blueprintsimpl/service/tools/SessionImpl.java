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
import net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.graphdb.MappingDSGraphDB;
import net.echinopsii.ariane.community.core.mapping.ds.cache.MappingDSCache;
import net.echinopsii.ariane.community.core.mapping.ds.cache.MappingDSCacheEntity;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;
import net.echinopsii.ariane.community.messaging.api.MomLogger;
import net.echinopsii.ariane.community.messaging.common.MomLoggerFactory;
import org.slf4j.Logger;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.service.tools.SessionImpl.SessionWorker.*;

public class SessionImpl implements Session {

    private final static Logger log = MomLoggerFactory.getLogger(SessionImpl.class);

    class SessionWorkerRequest {
        String action;
        Object instance;
        Method method;
        Object[] args;
        LinkedBlockingQueue<SessionWorkerReply> replyQ;

        public SessionWorkerRequest(String action, Object instance, Method method,
                                    Object[] args, LinkedBlockingQueue<SessionWorkerReply> repQ) {
            this.action = action;
            this.instance = instance;
            this.method = method;
            this.args = args;
            this.replyQ = repQ;
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

        public LinkedBlockingQueue<SessionWorkerReply> getReplyQ() {
            return replyQ;
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
        public final static String COMMIT = "COMMIT";
        public final static String ROLLBACK = "ROLLBACK";
        public final static String TRACE = "TRACE";

        private LinkedBlockingQueue<SessionWorkerRequest> fifoInputQ = new LinkedBlockingQueue<>();
        private boolean running = true;
        private Session attachedSession = null;

        public SessionWorker(Session session) {
            this.attachedSession = session;
        }


        private void returnToQueue(SessionWorkerRequest req, SessionWorkerReply ret) {
            if (req.getReplyQ() != null) {
                try {
                    req.getReplyQ().put(ret);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void run() {
            MappingDSGraphDB.putThreadedSession(this.attachedSession);
            while (running) {
                SessionWorkerRequest msg = null;
                try {
                    msg = fifoInputQ.poll(2, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (msg != null) {
                    if (msg.getAction().equals(STOP)) {
                        log.debug("[" + Thread.currentThread().getId() + ".worker.stop]");
                        running = false;
                    } else if (msg.getAction().equals(COMMIT)) {
                        log.debug("[" + Thread.currentThread().getId() + ".worker.commit]");
                        MappingDSGraphDB.commit();
                        for (MappingDSCacheEntity entity: ((SessionImpl)this.attachedSession).sessionExistingObjectCache.values())
                            MappingDSCache.putEntityToCache(entity);
                        for (MappingDSCacheEntity entity: ((SessionImpl)this.attachedSession).sessionRemovedObjectCache.values())
                            MappingDSCache.removeEntityFromCache(entity);
                        ((SessionImpl)this.attachedSession).sessionExistingObjectCache.clear();
                        ((SessionImpl)this.attachedSession).sessionRemovedObjectCache.clear();
                        this.returnToQueue(msg, new SessionWorkerReply(false, Void.TYPE, null));
                    } else if (msg.getAction().equals(ROLLBACK)) {
                        log.debug("[" + Thread.currentThread().getId() + ".worker.rollback]");
                        MappingDSGraphDB.rollback();
                        ((SessionImpl)this.attachedSession).sessionExistingObjectCache.clear();
                        ((SessionImpl)this.attachedSession).sessionRemovedObjectCache.clear();
                        this.returnToQueue(msg, new SessionWorkerReply(false, Void.TYPE, null));
                    } else if (msg.getAction().equals(EXECUTE)) {
                        try {
                            log.debug("[" + Thread.currentThread().getId() + ".worker.execute] " +
                                    msg.getInstance().toString() + "." + msg.getMethod().toString() + "(" + Arrays.toString(msg.getArgs()) + ")");
                            Object ret = msg.getMethod().invoke(msg.getInstance(), msg.getArgs());
                            if (msg.getMethod().getReturnType().equals(Void.TYPE))
                                ret = Void.TYPE;
                            this.returnToQueue(msg, new SessionWorkerReply(false, ret, null));
                        } catch (Exception e) {
                            e.printStackTrace();
                            this.returnToQueue(msg, new SessionWorkerReply(true, null, e.getMessage()));
                        }
                    } else if (msg.getAction().equals(TRACE)) {
                        boolean isTraceEnabled = (boolean) msg.getArgs()[0];
                        log.debug("[" + Thread.currentThread().getId() + ".worker.trace] " + this.attachedSession.getSessionID() + " : " + isTraceEnabled);
                        ((MomLogger)log).setTraceLevel(isTraceEnabled);
                    }
                }
            }
            MappingDSGraphDB.removeThreadedSession();
        }

        public LinkedBlockingQueue<SessionWorkerRequest> getFifoInputQ() {
            return fifoInputQ;
        }

        public boolean isRunning() {
            return running;
        }
    }

    private String sessionId = null;

    private SessionWorker sessionWorker = new SessionWorker(this);
    private Thread sessionThread = new Thread(sessionWorker);

    private HashMap<String, MappingDSCacheEntity> sessionExistingObjectCache = new HashMap<>();
    private HashMap<String, MappingDSCacheEntity> sessionRemovedObjectCache = new HashMap<>();

    public SessionImpl(String clientId) {
        clientId = clientId.replace(" ", "_");
        this.sessionId = clientId + '-' + UUID.randomUUID();
        this.sessionThread.setName(sessionId);
    }

    @Override
    public String getSessionID() {
        return sessionId;
    }

    @Override
    public Session stop() {
        try {
            //Rollback any operation not commited yet
            this.rollback();
        } catch (MappingDSException e) {
            e.printStackTrace();
        }
        try {
            this.sessionWorker.getFifoInputQ().put(new SessionWorkerRequest(STOP, null, null, null, null));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while(sessionWorker.isRunning())
            try {
                Thread.sleep(10);
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
        return sessionWorker.isRunning();
    }

    private SessionWorkerReply getReply(LinkedBlockingQueue<SessionWorkerReply> repQ) throws MappingDSException {
        SessionWorkerReply reply = null;
        while (reply==null)
            try {
                reply = repQ.poll(2, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                throw new MappingDSException(e.getMessage());
            }
        return reply;
    }

    @Override
    public Object execute(Object o, String methodName, Object[] args) throws MappingDSException {
        log.debug("["+ sessionId +".execute] {"+o.getClass().getName()+","+methodName+"}");
        LinkedBlockingQueue<SessionWorkerReply> repQ = new LinkedBlockingQueue<>();
        try {
            Class[] parametersType = null;
            String parameters = "";
            if (args !=null) {
                parametersType = new Class[args.length];
                for (int i = 0; i < args.length; i++) {
                    if (args[i] != null) {
                        parametersType[i] = args[i].getClass();
                        parameters += args[i].getClass().getName();
                    } else {
                        parametersType[i] = Object.class;
                        parameters += Object.class.getName();
                    }
                    if (i < args.length-1)  parameters += ", ";
                }
            }
            Method m = null;
            try {
                m = o.getClass().getMethod(methodName, parametersType);
            } catch (NoSuchMethodException e) {
                Method[] methods = o.getClass().getMethods();
                //log.error("Method name to found : " + methodName);
                methodLoop: for (Method method : methods) {
                    //log.error("Method loop : " + method.getName());
                    if (!methodName.equals(method.getName())) {
                        continue;
                    }
                    Class<?>[] paramTypes = method.getParameterTypes();
                    if (args == null && paramTypes == null) {
                        m = method;
                        break;
                    } else if (args == null || paramTypes == null
                            || paramTypes.length != args.length) {
                        continue;
                    }

                    for (int i = 0; i < args.length; ++i) {
                        //log.error("paramTypes["+i+"] = " + paramTypes[i].getName());
                        //log.error("args["+i+"] = " + args[i].getClass().getName());
                        boolean isAssignable;
                        if (args[i]!=null) isAssignable = paramTypes[i].isAssignableFrom(args[i].getClass());
                        else isAssignable = true;
                        //log.error("isAssignable: " + isAssignable);
                        if (!isAssignable) {
                            continue methodLoop;
                        }
                    }
                    m = method;
                }
                if (m == null) throw new MappingDSException("Method " + methodName + "(" + parameters + ")" +
                        "@" + o.getClass().getName() + " does not exists !");
            }
            this.sessionWorker.getFifoInputQ().put(new SessionWorkerRequest(EXECUTE, o, m, args, repQ));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.debug("["+ sessionId +".execute] wait reply ...");

        SessionWorkerReply reply = getReply(repQ);
        log.debug("[" + sessionId + ".execute] reply error : " + reply.isError());
        if (! reply.isError()) return reply.getRet();
        else throw new MappingDSException(reply.getError_msg());
    }

    @Override
    public Session commit() throws MappingDSException {
        LinkedBlockingQueue<SessionWorkerReply> repQ = new LinkedBlockingQueue<>();
        try {
            this.sessionWorker.getFifoInputQ().put(new SessionWorkerRequest(COMMIT, null, null, null, repQ));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        getReply(repQ);
        return this;
    }

    @Override
    public Session rollback() throws MappingDSException {
        LinkedBlockingQueue<SessionWorkerReply> repQ = new LinkedBlockingQueue<>();
        try {
            this.sessionWorker.getFifoInputQ().put(new SessionWorkerRequest(ROLLBACK, null, null, null, repQ));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        getReply(repQ);
        return this;
    }

    @Override
    public Session traceSession(boolean isTraceEnabled) {
        try {
            Object[] args = new Object[]{isTraceEnabled};
            this.sessionWorker.getFifoInputQ().put(new SessionWorkerRequest(TRACE, null, null, args, null));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return this;
    }

    public MappingDSCacheEntity getCachedEntity(String id) {
        return sessionExistingObjectCache.get(id);
    }

    public void putEntityToCache(MappingDSCacheEntity entity) {
        sessionExistingObjectCache.put(entity.getEntityCacheID(), entity);
    }

    public void removeEntityFromCache(MappingDSCacheEntity entity) {
        sessionExistingObjectCache.remove(entity.getEntityCacheID());
        sessionRemovedObjectCache.put(entity.getEntityCacheID(), entity);
    }
}
