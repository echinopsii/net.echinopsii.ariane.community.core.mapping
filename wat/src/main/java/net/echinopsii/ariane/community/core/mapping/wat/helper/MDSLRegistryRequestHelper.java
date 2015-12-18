package net.echinopsii.ariane.community.core.mapping.wat.helper;

import net.echinopsii.ariane.community.core.mapping.ds.dsl.registry.MappingDSLRegistryBootstrap;
import net.echinopsii.ariane.community.core.mapping.ds.dsl.registry.model.MappingDSLRegistryRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;

/**
 * Created by sagar on 17/12/15.
 */
public class MDSLRegistryRequestHelper {

    private static final Logger log = LoggerFactory.getLogger(MDSLRegistryRequestHelper.class);

    public Boolean deleteRequest(long requestID) {
        EntityManager em = MappingDSLRegistryBootstrap.getIDMJPAProvider().createEM();
        MappingDSLRegistryRequest entity = null;
        try {
            em.getTransaction().begin();
            entity = em.find(MappingDSLRegistryRequest.class, requestID);
            entity.getRootDirectory().getRequests().remove(entity);
            em.remove(entity);
            em.getTransaction().commit();
/*
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Mapping DSL request deleted successfully !",
                    "Mapping DSL request name : " + entity.getName());
            FacesContext.getCurrentInstance().addMessage(null, msg);*/
            return Boolean.TRUE;
        }  catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Throwable raised while deleting mapping dsl request " + ((entity!=null) ? entity.getName() : "null") + " !",
                    "Throwable message : " + t.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
        } finally {
            em.close();
        }
        return Boolean.FALSE;
    }
}
