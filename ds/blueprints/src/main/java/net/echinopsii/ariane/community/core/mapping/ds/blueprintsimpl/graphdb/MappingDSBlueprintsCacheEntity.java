package net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.graphdb;

import com.tinkerpop.blueprints.Element;
import net.echinopsii.ariane.community.core.mapping.ds.cache.MappingDSCacheEntity;

public interface MappingDSBlueprintsCacheEntity extends MappingDSCacheEntity {
    /* Add Transient Blueprints Element To Mapping Cached Entity */
    public Element getElement();
    public void    setElement(Element element);
}
