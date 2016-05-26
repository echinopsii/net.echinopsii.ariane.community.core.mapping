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

package net.echinopsii.ariane.community.core.mapping.ds.messagingimpl.domain;

import net.echinopsii.ariane.community.core.mapping.ds.MappingDSException;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Cluster;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Container;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Gate;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Node;
import net.echinopsii.ariane.community.core.mapping.ds.domain.proxy.SProxContainer;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;

import java.util.HashMap;
import java.util.Set;

public class ContainerImpl implements SProxContainer {
    @Override
    public void setContainerName(Session session, String name) throws MappingDSException {

    }

    @Override
    public void setContainerCompany(Session session, String company) throws MappingDSException {

    }

    @Override
    public void setContainerProduct(Session session, String product) throws MappingDSException {

    }

    @Override
    public void setContainerType(Session session, String type) throws MappingDSException {

    }

    @Override
    public void setContainerPrimaryAdminGate(Session session, Gate gate) throws MappingDSException {

    }

    @Override
    public void setContainerCluster(Session session, Cluster cluster) throws MappingDSException {

    }

    @Override
    public void addContainerProperty(Session session, String propertyKey, Object value) throws MappingDSException {

    }

    @Override
    public void removeContainerProperty(Session session, String propertyKey) throws MappingDSException {

    }

    @Override
    public void setContainerParentContainer(Session session, Container container) throws MappingDSException {

    }

    @Override
    public boolean addContainerChildContainer(Session session, Container container) throws MappingDSException {
        return false;
    }

    @Override
    public boolean removeContainerChildContainer(Session session, Container container) throws MappingDSException {
        return false;
    }

    @Override
    public boolean addContainerNode(Session session, Node node) throws MappingDSException {
        return false;
    }

    @Override
    public boolean removeContainerNode(Session session, Node node) throws MappingDSException {
        return false;
    }

    @Override
    public boolean addContainerGate(Session session, Gate service) throws MappingDSException {
        return false;
    }

    @Override
    public boolean removeContainerGate(Session session, Gate service) throws MappingDSException {
        return false;
    }

    @Override
    public String getContainerID() {
        return null;
    }

    @Override
    public void setContainerID(String ID) {

    }

    @Override
    public String getContainerName() {
        return null;
    }

    @Override
    public void setContainerName(String name) {

    }

    @Override
    public String getContainerCompany() {
        return null;
    }

    @Override
    public void setContainerCompany(String company) throws MappingDSException {

    }

    @Override
    public String getContainerProduct() {
        return null;
    }

    @Override
    public void setContainerProduct(String product) throws MappingDSException {

    }

    @Override
    public String getContainerType() {
        return null;
    }

    @Override
    public void setContainerType(String type) throws MappingDSException {

    }

    @Override
    public String getContainerPrimaryAdminGateURL() {
        return null;
    }

    @Override
    public Gate getContainerPrimaryAdminGate() {
        return null;
    }

    @Override
    public void setContainerPrimaryAdminGate(Gate gate) throws MappingDSException {

    }

    @Override
    public Cluster getContainerCluster() {
        return null;
    }

    @Override
    public void setContainerCluster(Cluster cluster) throws MappingDSException {

    }

    @Override
    public HashMap<String, Object> getContainerProperties() {
        return null;
    }

    @Override
    public void addContainerProperty(String propertyKey, Object value) throws MappingDSException {

    }

    @Override
    public void removeContainerProperty(String propertyKey) throws MappingDSException {

    }

    @Override
    public Container getContainerParentContainer() {
        return null;
    }

    @Override
    public void setContainerParentContainer(Container container) throws MappingDSException {

    }

    @Override
    public Set<? extends Container> getContainerChildContainers() {
        return null;
    }

    @Override
    public boolean addContainerChildContainer(Container container) throws MappingDSException {
        return false;
    }

    @Override
    public boolean removeContainerChildContainer(Container container) throws MappingDSException {
        return false;
    }

    @Override
    public Set<? extends Node> getContainerNodes(long depth) {
        return null;
    }

    @Override
    public boolean addContainerNode(Node node) throws MappingDSException {
        return false;
    }

    @Override
    public boolean removeContainerNode(Node node) throws MappingDSException {
        return false;
    }

    @Override
    public Set<? extends Gate> getContainerGates() {
        return null;
    }

    @Override
    public boolean addContainerGate(Gate service) throws MappingDSException {
        return false;
    }

    @Override
    public boolean removeContainerGate(Gate service) throws MappingDSException {
        return false;
    }
}
