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

import java.lang.reflect.InvocationTargetException;
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
                    log.warn("[" + Thread.currentThread().getName() + ".worker.returnToQueue] Interrupted while putting worker reply...");
                    if (log.isDebugEnabled()) e.printStackTrace();
                }
            }
        }

        private int sleepTime = 500; // 500ms sleep between each retry
        private int maxRetryCount = 15; // 15 retry max => 7.5 seconds before raising an error

        private void execute(SessionWorkerRequest msg, int retry) {
            try {
                log.debug("[" + Thread.currentThread().getName() + ".worker.execute] " +
                        msg.getInstance().toString() + "." + msg.getMethod().toString() + " (" + Arrays.toString(msg.getArgs()) + ")");
                Object ret = msg.getMethod().invoke(msg.getInstance(), msg.getArgs());
                if (msg.getMethod().getReturnType().equals(Void.TYPE))
                    ret = Void.TYPE;
                this.returnToQueue(msg, new SessionWorkerReply(false, ret, null));
            } catch (InvocationTargetException ie) {
                Throwable th = ie.getCause();
                // it th not null check class name instead importing optional packages
                if (th!=null && th.getClass().getName().equals("org.neo4j.kernel.DeadlockDetectedException")) {
                    if (retry < maxRetryCount) {
                        try {
                            Thread.sleep(sleepTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        log.debug("[" + Thread.currentThread().getName() + ".worker.execute] Retry request after DeadlockDetectedException : " + msg.getInstance().toString() + "." + msg.getMethod().toString() + " (" + Arrays.toString(msg.getArgs()) + ")");
                        this.execute(msg, ++retry);
                    } else {
                        log.warn("[" + Thread.currentThread().getName() + ".worker.execute] InvocationTargetException (" + th.getClass().getName() + ") raised while executing request : " + th.getMessage() + " ...");
                        log.warn("[" + Thread.currentThread().getName() + ".worker.execute] Request : " + msg.getInstance().toString() + "." + msg.getMethod().toString() + " (" + Arrays.toString(msg.getArgs()) + ")");
                        if (log.isTraceEnabled()) th.printStackTrace();
                        this.returnToQueue(msg, new SessionWorkerReply(true, null, th.getMessage()));
                    }
                } else {
                    if (th!=null) log.warn("[" + Thread.currentThread().getName() + ".worker.execute] InvocationTargetException (" + th.getClass().getName() + ") raised while executing request : " + th.getMessage() + " ...");
                    else log.warn("[" + Thread.currentThread().getName() + ".worker.execute] InvocationTargetException raised while executing request...");
                    log.warn("[" + Thread.currentThread().getName() + ".worker.execute] Request : " + msg.getInstance().toString() + "." + msg.getMethod().toString() + " (" + Arrays.toString(msg.getArgs()) + ")");
                    if (th!=null && log.isTraceEnabled()) th.printStackTrace();
                    this.returnToQueue(msg, new SessionWorkerReply(true, null, (th!=null) ? th.getMessage() : ie.getMessage()));
                }
            } catch (Exception e) {
                if (e.getMessage()!=null) log.warn("[" + Thread.currentThread().getName() + ".worker.execute] Exception ( " + e.getClass().getName() +  " ) raised while executing request : " + e.getMessage() + " ...");
                else log.warn("[" + Thread.currentThread().getName() + ".worker.execute] Exception ( " + e.getClass().getName() +  " ) raised while executing request ...");
                log.warn("[" + Thread.currentThread().getName() + ".worker.execute] Request : " + msg.getInstance().toString() + "." + msg.getMethod().toString() + " (" + Arrays.toString(msg.getArgs()) + ")");
                if (log.isTraceEnabled()) e.printStackTrace();
                this.returnToQueue(msg, new SessionWorkerReply(true, null, e.getMessage()));
            }
        }

        @Override
        public void run() {
            MappingDSGraphDB.putThreadedSession(this.attachedSession);
            while (running) {
                SessionWorkerRequest msg = null;
                try {
                    msg = fifoInputQ.poll(50, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    log.warn("Interrupted while polling worker request queue...");
                    if (log.isDebugEnabled()) e.printStackTrace();
                }
                if (msg != null) {
                    switch (msg.getAction()) {
                        case STOP:
                            log.warn("[" + Thread.currentThread().getName() + ".worker.run] stop");
                            running = false;
                        case ROLLBACK:
                            log.warn("[" + Thread.currentThread().getName() + ".worker.run] rollback");
                            MappingDSGraphDB.rollback();
                            ((SessionImpl) this.attachedSession).sessionExistingObjectCache.clear();
                            ((SessionImpl) this.attachedSession).sessionRemovedObjectCache.clear();
                            this.returnToQueue(msg, new SessionWorkerReply(false, Void.TYPE, null));
                            break;
                        case COMMIT:
                            log.warn("[" + Thread.currentThread().getName() + ".worker.run] commit");
                            MappingDSGraphDB.commit();
                            for (MappingDSCacheEntity entity : ((SessionImpl) this.attachedSession).sessionExistingObjectCache.values()) {
                                MappingDSCache.removeEntityFromCache(entity);
                                MappingDSCache.putEntityToCacheIfNotExists(entity);
                            }
                            for (MappingDSCacheEntity entity : ((SessionImpl) this.attachedSession).sessionRemovedObjectCache.values())
                                MappingDSCache.removeEntityFromCache(entity);
                            ((SessionImpl) this.attachedSession).sessionExistingObjectCache.clear();
                            ((SessionImpl) this.attachedSession).sessionRemovedObjectCache.clear();
                            this.returnToQueue(msg, new SessionWorkerReply(false, Void.TYPE, null));
                            break;
                        case EXECUTE:
                            this.execute(msg, 0);
                            break;
                        case TRACE:
                            boolean isTraceEnabled = (boolean) msg.getArgs()[0];
                            log.warn("[" + Thread.currentThread().getName() + ".worker.run] trace " + this.attachedSession.getSessionID() + " : " + isTraceEnabled);
                            ((MomLogger) log).setMsgTraceLevel(isTraceEnabled);
                            break;
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

    private boolean waitingAnswer = false;
    private boolean interruptAnswerWait = false;
    private boolean toBeGarbaged = false;

    public SessionImpl(String clientId) {
        clientId = clientId.replace(" ", "_");
        synchronized (UUID.class) {
            this.sessionId = clientId + '-' + UUID.randomUUID();
        }
        this.sessionThread.setName(sessionId);
    }

    @Override
    public String getSessionID() {
        return sessionId;
    }

    @Override
    public Session stop() {
        this.unlockIfWaitingAnswer();
        if (this.toBeGarbaged) log.warn("Closing blocked transaction " + this.sessionId);
        try {
            this.sessionWorker.getFifoInputQ().put(new SessionWorkerRequest(STOP, null, null, null, null));
        } catch (InterruptedException e) {
            log.warn("Interrupted while stopping session... " + e.getMessage());
            if (log.isDebugEnabled()) e.printStackTrace();
        }
        int counter = 0;
        while(sessionWorker.isRunning() && !this.toBeGarbaged) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                log.warn("Interrupted while waiting session to be stopped... " + e.getMessage());
                if (log.isDebugEnabled()) e.printStackTrace();
            }
            counter++;
            if (counter==100) break;
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

    private boolean isNeo4JWaitingWriteLock() {
        boolean neo4jWaitingWriteLock = false;
        int threadStackTraceLength = this.sessionThread.getStackTrace().length;
        if (threadStackTraceLength>1) {
            if (this.sessionThread.getStackTrace()[0].toString().contains("org.neo4j.kernel.impl.locking.community.RWLock.acquireWriteLock"))
                neo4jWaitingWriteLock = true;
            log.debug("[" + this.sessionThread.getName() + ".getReply] current session thread has been interrupted. StackTrace[0] : " + this.sessionThread.getStackTrace()[0]);
        }
        if (threadStackTraceLength>2) {
            if (this.sessionThread.getStackTrace()[1].toString().contains("org.neo4j.kernel.impl.locking.community.RWLock.acquireWriteLock"))
                neo4jWaitingWriteLock = true;
            log.debug("[" + this.sessionThread.getName() + ".getReply] current session thread has been interrupted. StackTrace[1] : " + this.sessionThread.getStackTrace()[1]);
        }
        if (threadStackTraceLength>3) {
            if (this.sessionThread.getStackTrace()[2].toString().contains("org.neo4j.kernel.impl.locking.community.RWLock.acquireWriteLock"))
                neo4jWaitingWriteLock = true;
            log.debug("[" + this.sessionThread.getName() + ".getReply] current session thread has been interrupted. StackTrace[2] : " + this.sessionThread.getStackTrace()[2]);
        }
        if (threadStackTraceLength>4) {
            if (this.sessionThread.getStackTrace()[3].toString().contains("org.neo4j.kernel.impl.locking.community.RWLock.acquireWriteLock"))
                neo4jWaitingWriteLock = true;
            log.debug("[" + this.sessionThread.getName() + ".getReply] current session thread has been interrupted. StackTrace[3] : " + this.sessionThread.getStackTrace()[3]);
        }
        if (threadStackTraceLength>5) {
            if (this.sessionThread.getStackTrace()[4].toString().contains("org.neo4j.kernel.impl.locking.community.RWLock.acquireWriteLock"))
                neo4jWaitingWriteLock = true;
            log.debug("[" + this.sessionThread.getName() + ".getReply] current session thread has been interrupted. StackTrace[4] : " + this.sessionThread.getStackTrace()[4]);
        }
        if (threadStackTraceLength>6) {
            if (this.sessionThread.getStackTrace()[5].toString().contains("org.neo4j.kernel.impl.locking.community.RWLock.acquireWriteLock"))
                neo4jWaitingWriteLock = true;
            log.debug("[" + this.sessionThread.getName() + ".getReply] current session thread has been interrupted. StackTrace[5] : " + this.sessionThread.getStackTrace()[5]);
        }
        return neo4jWaitingWriteLock;
    }

    private SessionWorkerReply getReply(LinkedBlockingQueue<SessionWorkerReply> repQ) throws MappingDSException {
        SessionWorkerReply reply = null;
        this.waitingAnswer = true;
        while (reply==null && !interruptAnswerWait)
            try {
                reply = repQ.poll(50, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                log.warn("[" + this.sessionThread.getName() + ".getReply] Interrupted while waiting session thread to be interrupt... " + e.getMessage());
                if (log.isTraceEnabled()) e.printStackTrace();
            }
        this.waitingAnswer = false;

        if (reply == null && interruptAnswerWait) {
            log.debug("[" + sessionId + ".getReply] current session thread will been interrupted. Current state : " + this.sessionThread.getState().toString());
            boolean neo4jWaitingWriteLock = isNeo4JWaitingWriteLock();
            this.sessionThread.interrupt();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                log.warn("[" + this.sessionThread.getName() + ".getReply] Interrupted while waiting session thread to be interrupt... " + e.getMessage());
                if (log.isTraceEnabled()) e.printStackTrace();
            }
            if (neo4jWaitingWriteLock && isNeo4JWaitingWriteLock())
                this.toBeGarbaged = true;
            this.interruptAnswerWait = false;
            if (this.toBeGarbaged)
                reply = new SessionWorkerReply(true, null, MappingDSException.MAPPING_OVERLOAD);
            else
                reply = new SessionWorkerReply(true, null, MappingDSException.MAPPING_TIMEOUT);
        }

        return reply;
    }

    private void unlockIfWaitingAnswer() {
        if (this.waitingAnswer) {
            log.debug("[" + sessionId + ".execute] current session is waiting answer from last call. Interrupt.");
            this.interruptAnswerWait = true;
            while (this.interruptAnswerWait)
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    log.warn("Interrupted while waiting session thread unlocking... " + e.getMessage());
                    if (log.isDebugEnabled()) e.printStackTrace();
                }
            this.waitingAnswer = false;
        }
    }

    @Override
    public Object execute(Object o, String methodName, Object[] args) throws MappingDSException {
        if (this.toBeGarbaged)
            throw new MappingDSException("Mapping transaction to be garbaged !");

        this.unlockIfWaitingAnswer();
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
            log.warn("Interrupted while executing request... " + e.getMessage());
            if (log.isDebugEnabled()) e.printStackTrace();
        }

        log.debug("["+ sessionId +".execute] wait reply ...");
        SessionWorkerReply reply = getReply(repQ);
        log.debug("[" + sessionId + ".execute] reply error : " + reply.isError());
        if (!reply.isError()) return reply.getRet();
        else throw new MappingDSException(reply.getError_msg());
    }

    @Override
    public Session commit() throws MappingDSException {
        if (this.toBeGarbaged)
            throw new MappingDSException("Mapping transaction to be garbaged !");

        this.unlockIfWaitingAnswer();
        LinkedBlockingQueue<SessionWorkerReply> repQ = new LinkedBlockingQueue<>();
        try {
            this.sessionWorker.getFifoInputQ().put(new SessionWorkerRequest(COMMIT, null, null, null, repQ));
        } catch (InterruptedException e) {
            log.warn("Interrupted while commiting session ... " + e.getMessage());
            if (log.isDebugEnabled()) e.printStackTrace();
        }
        getReply(repQ);
        return this;
    }

    @Override
    public Session rollback() throws MappingDSException {
        if (this.toBeGarbaged)
            throw new MappingDSException("Mapping transaction to be garbaged !");

        this.unlockIfWaitingAnswer();
        LinkedBlockingQueue<SessionWorkerReply> repQ = new LinkedBlockingQueue<>();
        try {
            this.sessionWorker.getFifoInputQ().put(new SessionWorkerRequest(ROLLBACK, null, null, null, repQ));
        } catch (InterruptedException e) {
            log.warn("Interrupted while rollbacking session ... " + e.getMessage());
            if (log.isDebugEnabled()) e.printStackTrace();
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
            log.warn("Interrupted while tracing session ... " + e.getMessage());
            if (log.isDebugEnabled()) e.printStackTrace();
        }
        return this;
    }

    public MappingDSCacheEntity getCachedEntity(String id) {
        return sessionExistingObjectCache.get(id);
    }

    public MappingDSCacheEntity putEntityToCacheIfNotExists(MappingDSCacheEntity entity) {
        if (sessionExistingObjectCache!=null && !sessionExistingObjectCache.containsKey(entity.getEntityCacheID())) {
            sessionExistingObjectCache.put(entity.getEntityCacheID(), entity);
            return entity;
        } else if (sessionExistingObjectCache!=null) return (MappingDSCacheEntity) sessionExistingObjectCache.get(entity.getEntityCacheID());
        else return entity;
    }

    public void removeEntityFromCache(MappingDSCacheEntity entity) {
        sessionExistingObjectCache.remove(entity.getEntityCacheID());
        sessionRemovedObjectCache.put(entity.getEntityCacheID(), entity);
    }
}
