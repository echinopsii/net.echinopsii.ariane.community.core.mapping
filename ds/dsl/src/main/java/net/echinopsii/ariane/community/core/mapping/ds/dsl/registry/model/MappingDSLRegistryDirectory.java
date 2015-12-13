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

package net.echinopsii.ariane.community.core.mapping.ds.dsl.registry.model;

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
import java.util.*;

@Entity
@XmlRootElement
@Table(name="uxResourceDirectory",uniqueConstraints = @UniqueConstraint(columnNames = {"directoryName"}))
public class MappingDSLRegistryDirectory implements IUXResource<UXPermission>, Serializable, Comparable<IUXResource> {

    private static final Logger log = LoggerFactory.getLogger(MappingDSLRegistryRequest.class);

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id = null;
    @Version
    @Column(name = "version")
    private int version = 0;

    @Column(name="directoryName")
    @NotNull
    private String name;

    @Column
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    private MappingDSLRegistryDirectory rootDirectory;

    @OneToMany(mappedBy = "rootDirectory", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    private Set<MappingDSLRegistryDirectory> subDirectories;

    @OneToMany(mappedBy = "rootDirectory", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    private Set<MappingDSLRegistryRequest> requests;

    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    private Group group;

    @ManyToMany(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    private Set<UXPermission> uxPermissions;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MappingDSLRegistryDirectory setIdR(Long id) {
        this.id = id;
        return this;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public MappingDSLRegistryDirectory setVersionR(int version) {
        this.version = version;
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MappingDSLRegistryDirectory setNameR(String path) {
        this.name = path;
        return this;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public MappingDSLRegistryDirectory setUserR(User user) {
        this.user = user;
        return this;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public MappingDSLRegistryDirectory setGroupR(Group group) {
        this.group = group;
        return this;
    }

    public Set<UXPermission> getUxPermissions() {
        return uxPermissions;
    }

    public void setUxPermissions(Set<UXPermission> uxPermissions) {
        this.uxPermissions = uxPermissions;
    }

    public MappingDSLRegistryDirectory setUxPermissionsR(Set<UXPermission> uxPermissions) {
        this.uxPermissions = uxPermissions;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MappingDSLRegistryDirectory setDescriptionR(String description) {
        this.description = description;
        return this;
    }

    public MappingDSLRegistryDirectory getRootDirectory() {
        return rootDirectory;
    }

    public void setRootDirectory(MappingDSLRegistryDirectory rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    public MappingDSLRegistryDirectory setRootDirectoryR(MappingDSLRegistryDirectory rootDirectory) {
        this.rootDirectory = rootDirectory;
        return this;
    }

    public Set<MappingDSLRegistryDirectory> getSubDirectories() {
        return subDirectories;
    }

    public void setSubDirectories(Set<MappingDSLRegistryDirectory> subDirectories) {
        this.subDirectories = subDirectories;
    }

    public MappingDSLRegistryDirectory setSubDirectoriesR(Set<MappingDSLRegistryDirectory> subDirectories) {
        this.subDirectories = subDirectories;
        return this;
    }

    public Set<MappingDSLRegistryRequest> getRequests() {
        return requests;
    }

    public void setRequests(Set<MappingDSLRegistryRequest> requests) {
        this.requests = requests;
    }

    public MappingDSLRegistryDirectory setRequestsR(Set<MappingDSLRegistryRequest> requests) {
        this.requests = requests;
        return this;
    }

    public String getFullPath() {
        String ret = "";
        if (rootDirectory != null)
            ret = rootDirectory.getFullPath();
        ret += "/" + name;
        return ret;
    }

    public Set<IUXResource> getOrderedChildsList() {
        TreeSet<IUXResource> ret = new TreeSet<>();
        ret.addAll(this.subDirectories);
        ret.addAll(this.requests);
        return ret;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MappingDSLRegistryDirectory directory = (MappingDSLRegistryDirectory) o;

        if (version != directory.version) {
            return false;
        }
        if (!id.equals(directory.id)) {
            return false;
        }
        if (!name.equals(directory.name)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "MappingDSLRegistryDirectory{" +
                       "id=" + id + ", path='" + name + '\'' +
                       '}';
    }

    public MappingDSLRegistryDirectory clone() {
        return new MappingDSLRegistryDirectory().setIdR(this.id).setVersionR(this.version).setNameR(this.name).setDescriptionR(this.description).setRootDirectoryR(rootDirectory).
                               setSubDirectoriesR(new HashSet<MappingDSLRegistryDirectory>(this.subDirectories)).setUserR(this.user).setUxPermissionsR(new HashSet(this.uxPermissions)).
                               setGroupR(this.group).setRequestsR(new HashSet(this.requests));
    }

    @Override
    public int compareTo(IUXResource o) {
        return this.name.compareTo(o.getName());
    }
}