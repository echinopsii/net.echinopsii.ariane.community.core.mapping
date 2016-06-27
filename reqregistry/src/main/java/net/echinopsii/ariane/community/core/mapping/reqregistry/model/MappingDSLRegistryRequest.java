/**
 * [DEFINE YOUR PROJECT NAME/MODULE HERE]
 * [DEFINE YOUR PROJECT DESCRIPTION HERE] 
 * Copyright (C) 02/06/14 echinopsii
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

package net.echinopsii.ariane.community.core.mapping.reqregistry.model;

import javax.validation.constraints.NotNull;
import net.echinopsii.ariane.community.core.idm.base.model.IUXResource;
import net.echinopsii.ariane.community.core.idm.base.model.jpa.Group;
import net.echinopsii.ariane.community.core.idm.base.model.jpa.UXPermission;
import net.echinopsii.ariane.community.core.idm.base.model.jpa.User;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@XmlRootElement
@Table(name="uxResourceRequest", uniqueConstraints = @UniqueConstraint(columnNames = {"requestName"}))
public class MappingDSLRegistryRequest implements IUXResource<UXPermission>, Serializable, Comparable<IUXResource>  {

    private static final Logger log = LoggerFactory.getLogger(MappingDSLRegistryRequest.class);

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id = null;
    @Version
    @Column(name = "version")
    private int version = 0;

    @Column(name="requestName")
    @NotNull
    private String name;

    @Column(columnDefinition="LONGTEXT")
    @NotNull
    private String request;

    @Column(columnDefinition="LONGTEXT")
    private String description;

    @Column
    private boolean isTemplate;

    @ManyToOne(fetch = FetchType.EAGER)
    private MappingDSLRegistryDirectory rootDirectory;

    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    private Group group;

    @ManyToMany(fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    private Set<UXPermission> uxPermissions;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MappingDSLRegistryRequest setIdR(Long id) {
        this.id = id;
        return this;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public MappingDSLRegistryRequest setVersionR(int version) {
        this.version = version;
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MappingDSLRegistryRequest setNameR(String name) {
        this.name = name;
        return this;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public MappingDSLRegistryRequest setRequestR(String request) {
        this.request = request;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MappingDSLRegistryRequest setDescriptionR(String description) {
        this.description = description;
        return this;
    }

    public boolean isTemplate() {
        return isTemplate;
    }

    public void setTemplate(boolean isTemplate) {
        this.isTemplate = isTemplate;
    }

    public MappingDSLRegistryRequest setTemplateR(boolean isTemplate) {
        this.isTemplate = isTemplate;
        return this;
    }

    public MappingDSLRegistryDirectory getRootDirectory() {
        return rootDirectory;
    }

    public void setRootDirectory(MappingDSLRegistryDirectory rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    public MappingDSLRegistryRequest setRootDirectoryR(MappingDSLRegistryDirectory rootDirectory) {
        this.rootDirectory = rootDirectory;
        return this;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public MappingDSLRegistryRequest setUserR(User user) {
        this.user = user;
        return this;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public MappingDSLRegistryRequest setGroupR(Group group) {
        this.group = group;
        return this;
    }

    public Set<UXPermission> getUxPermissions() {
        return uxPermissions;
    }

    public void setUxPermissions(Set<UXPermission> userPermissions) {
        this.uxPermissions = userPermissions;
    }

    public MappingDSLRegistryRequest setUxPermissionsR(Set<UXPermission> uxPermissions) {
        this.uxPermissions = uxPermissions;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MappingDSLRegistryRequest request = (MappingDSLRegistryRequest) o;

        if (version != request.version) {
            return false;
        }
        if (!id.equals(request.id)) {
            return false;
        }
        if (!name.equals(request.name)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return "MappingDSLRegistryRequest{" +
                       "id=" + id + ", name='" + name + '\'' +
                       '}';
    }

    public MappingDSLRegistryRequest clone() {
        return new MappingDSLRegistryRequest().setIdR(this.id).setVersionR(this.version).setNameR(this.name).setRequestR(this.request).setDescriptionR(this.description).
                                               setUserR(this.user).setUxPermissionsR(new HashSet(this.uxPermissions)).setGroupR(this.group).setTemplateR(this.isTemplate).
                                               setRootDirectoryR(this.rootDirectory);
    }

    @Override
    public int compareTo(IUXResource o) {
        return this.name.compareTo(o.getName());
    }
}