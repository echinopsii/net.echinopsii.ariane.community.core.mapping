/**
 * [DEFINE YOUR PROJECT NAME/MODULE HERE]
 * [DEFINE YOUR PROJECT DESCRIPTION HERE] 
 * Copyright (C) 03/06/14 echinopsii
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

package net.echinopsii.ariane.community.core.mapping.wat.controller;

import net.echinopsii.ariane.community.core.mapping.ds.dsl.registry.model.MappingDSLRegistryDirectory;
import net.echinopsii.ariane.community.core.mapping.ds.dsl.registry.model.MappingDSLRegistryRequest;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import java.util.HashSet;

public class MDSLRegistryController {

    private static final Logger log = LoggerFactory.getLogger(MDSLRegistryController.class);
    private TreeNode root;
    private TreeNode selectedDirectoryOrRequestNode;
    private TreeNode selectedRequestNode;
    private String   selectedRequestReq;
    private String   selectedRequestDesc;

    public final static String FACES_CONTEXT_APPMAP_SELECTED_REQ = "MAPPING_SELECTED_REQUEST";

    private void buildTree(MappingDSLRegistryDirectory rootDir, TreeNode rootNode) {
        for (MappingDSLRegistryDirectory subDir : rootDir.getSubDirectories()) {
            TreeNode subNode = new DefaultTreeNode("Folder", subDir, rootNode);
            buildTree(subDir, subNode);
        }
        for (MappingDSLRegistryRequest request : rootDir.getRequests())
            new DefaultTreeNode(request.isTemplate() ? "Template" : "MappingDSLRegistryRequest", request, rootNode);
    }

    @PostConstruct
    public void init() {
        root = new DefaultTreeNode(new MappingDSLRegistryDirectory().setNameR("dirRoot"), null);

        MappingDSLRegistryDirectory rootD  =  new MappingDSLRegistryDirectory().setIdR(new Long(0)).setNameR("root").setSubDirectoriesR(new HashSet<MappingDSLRegistryDirectory>()).setRequestsR(new HashSet<MappingDSLRegistryRequest>());
        MappingDSLRegistryDirectory tplDir = new MappingDSLRegistryDirectory().setIdR(new Long(1)).setNameR("Templates").setSubDirectoriesR(new HashSet<MappingDSLRegistryDirectory>()).setRequestsR(new HashSet<MappingDSLRegistryRequest>())
                                                              .setRootDirectoryR(rootD);
        MappingDSLRegistryDirectory reqDir = new MappingDSLRegistryDirectory().setIdR(new Long(2)).setNameR("Requests").setSubDirectoriesR(new HashSet<MappingDSLRegistryDirectory>()).setRequestsR(new HashSet<MappingDSLRegistryRequest>())
                                                              .setRootDirectoryR(rootD);
        rootD.getSubDirectories().add(tplDir);
        rootD.getSubDirectories().add(reqDir);

        MappingDSLRegistryRequest request1 = new MappingDSLRegistryRequest().setIdR(new Long(3)).setTemplateR(false).setNameR("request1").setRootDirectoryR(reqDir)
                                   .setRequestR("request 1").setDescriptionR("description 1");
        MappingDSLRegistryRequest request2 = new MappingDSLRegistryRequest().setIdR(new Long(4)).setTemplateR(false).setNameR("request2").setRootDirectoryR(reqDir)
                                   .setRequestR("request 2").setDescriptionR("description 2");

        reqDir.getRequests().add(request1);
        reqDir.getRequests().add(request2);

        MappingDSLRegistryRequest template1 = new MappingDSLRegistryRequest().setIdR(new Long(3)).setTemplateR(true).setNameR("template1").setRootDirectoryR(tplDir)
                                    .setRequestR("template 1").setDescriptionR("description 1");
        MappingDSLRegistryRequest template2 = new MappingDSLRegistryRequest().setIdR(new Long(4)).setTemplateR(true).setNameR("template2").setRootDirectoryR(tplDir)
                                    .setRequestR("template 2").setDescriptionR("description 2");

        MappingDSLRegistryDirectory tpl3Dir = new MappingDSLRegistryDirectory().setIdR(new Long(5)).setNameR("template3").setRootDirectoryR(tplDir).setSubDirectoriesR(new HashSet<MappingDSLRegistryDirectory>()).setRequestsR(new HashSet<MappingDSLRegistryRequest>());
        MappingDSLRegistryDirectory tpl4Dir = new MappingDSLRegistryDirectory().setIdR(new Long(6)).setNameR("template4").setRootDirectoryR(tplDir).setSubDirectoriesR(new HashSet<MappingDSLRegistryDirectory>()).setRequestsR(new HashSet<MappingDSLRegistryRequest>());

        tplDir.getRequests().add(template1);
        tplDir.getRequests().add(template2);
        tplDir.getSubDirectories().add(tpl3Dir);
        tplDir.getSubDirectories().add(tpl4Dir);

        MappingDSLRegistryRequest template31 = new MappingDSLRegistryRequest().setIdR(new Long(7)).setTemplateR(true).setNameR("template31").setRootDirectoryR(tpl3Dir)
                                     .setRequestR("template 31").setDescriptionR("description 31");
        MappingDSLRegistryRequest template32 = new MappingDSLRegistryRequest().setIdR(new Long(8)).setTemplateR(true).setNameR("template32").setRootDirectoryR(tpl3Dir)
                                     .setRequestR("template 32").setDescriptionR("description 32");
        tpl3Dir.getRequests().add(template31);
        tpl3Dir.getRequests().add(template32);

        MappingDSLRegistryRequest template41 = new MappingDSLRegistryRequest().setIdR(new Long(9)).setTemplateR(true).setNameR("template41").setRootDirectoryR(tpl3Dir)
                                     .setRequestR("template 41").setDescriptionR("description 41");
        MappingDSLRegistryRequest template42 = new MappingDSLRegistryRequest().setIdR(new Long(10)).setTemplateR(true).setNameR("template42").setRootDirectoryR(tpl3Dir)
                                     .setRequestR("template 42").setDescriptionR("description 42");
        tpl4Dir.getRequests().add(template41);
        tpl4Dir.getRequests().add(template42);

        buildTree(rootD, root);
    }

    public TreeNode getRoot() {
        return root;
    }

    public TreeNode getSelectedDirectoryOrRequestNode() {
        return selectedDirectoryOrRequestNode;
    }

    public void setSelectedDirectoryOrRequestNode(TreeNode selectedDirectoryOrRequestNode) {
        this.selectedDirectoryOrRequestNode = selectedDirectoryOrRequestNode;
        if (selectedDirectoryOrRequestNode.getData() instanceof MappingDSLRegistryRequest) {
            this.selectedRequestNode = selectedDirectoryOrRequestNode;
            this.selectedRequestReq  = ((MappingDSLRegistryRequest)this.selectedRequestNode.getData()).getRequest();
            this.selectedRequestDesc = ((MappingDSLRegistryRequest)this.selectedRequestNode.getData()).getDescription();
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(FACES_CONTEXT_APPMAP_SELECTED_REQ, this.selectedRequestReq);
        } else {
            this.selectedRequestNode = null;
            this.selectedRequestReq  = "";
            this.selectedRequestDesc = "";
        }
    }

    public TreeNode getSelectedRequestNode() {
        return selectedRequestNode;
    }

    public String getSelectedRequestReq() {
        return selectedRequestReq;
    }

    public String getSelectedRequestDesc() {
        return selectedRequestDesc;
    }
}