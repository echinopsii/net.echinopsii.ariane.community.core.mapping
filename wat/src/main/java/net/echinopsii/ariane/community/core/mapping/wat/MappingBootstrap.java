/**
 * [DEFINE YOUR PROJECT NAME/MODULE HERE]
 * [DEFINE YOUR PROJECT DESCRIPTION HERE] 
 * Copyright (C) 27/02/14 echinopsii
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

package net.echinopsii.ariane.community.core.mapping.wat;

import net.echinopsii.ariane.community.core.mapping.ds.service.MappingSce;
import net.echinopsii.ariane.community.core.portal.base.model.*;
import net.echinopsii.ariane.community.core.portal.base.plugin.FaceletsResourceResolverService;
import net.echinopsii.ariane.community.core.portal.base.plugin.FacesMBeanRegistry;
import net.echinopsii.ariane.community.core.portal.base.plugin.MainMenuEntityRegistry;
import net.echinopsii.ariane.community.core.portal.base.plugin.RestResourceRegistry;
import net.echinopsii.ariane.community.core.portal.base.plugin.UserPreferencesRegistry;
import org.apache.felix.ipojo.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ArrayList;

@Component
@Provides(properties= {@StaticServiceProperty(name="targetArianeComponent", type="java.lang.String", value="Portal")})
@Instantiate
public class MappingBootstrap implements FaceletsResourceResolverService {
    private static final Logger log = LoggerFactory.getLogger(MappingBootstrap.class);
    private static final String MAPPING_COMPONENT = "Ariane Mapping Component";

    protected static ArrayList<MainMenuEntity> mappingMainMenuEntityList = new ArrayList<MainMenuEntity>() ;
    protected static ArrayList<UserPreferenceSection> userPreferenceSectionList = new ArrayList<UserPreferenceSection>();

    private static String MAIN_MENU_MAPPING_CONTEXT;

    private static final int MAIN_MENU_MAP_RANK = 4;
    private static final String basePath = "/META-INF";
    private static final String FACES_CONFIG_FILE_PATH = basePath + "/faces-config.xml";
    private static final String REST_EP_FILE_PATH = basePath + "/rest.endpoints";

    @Requires(from="ArianePortalFacesMBeanRegistry")
    private FacesMBeanRegistry portalPluginFacesMBeanRegistry = null;

    @Requires
    private MainMenuEntityRegistry mainMenuEntityRegistry = null;

    @Requires
    private RestResourceRegistry restResourceRegistry = null;

    @Requires
    private UserPreferencesRegistry userPreferencesRegistry = null;

    @Requires
    private MappingSce mappingBSce = null;

    @Bind
    public void bindRestResourceRegistry(RestResourceRegistry r) {
        log.debug("Bound to rest resource registry...");
        restResourceRegistry = r;
    }

    @Unbind
    public void unbindRestResourceRegistry() {
        log.debug("Unbound from rest resource registry...");
        restResourceRegistry = null;
    }

    @Bind
    public void bindMainMenuEntityRegistry(MainMenuEntityRegistry r) {
        log.debug("Bound to main menu item registry...");
        mainMenuEntityRegistry = r;
    }

    @Unbind
    public void unbindMainMenuEntityRegistry() {
        log.debug("Unbound from main menu item registry...");
        mainMenuEntityRegistry = null;
    }

    @Bind(from="ArianePortalFacesMBeanRegistry")
    public void bindPortalPluginFacesMBeanRegistry(FacesMBeanRegistry r) {
        log.debug("Bound to portal plugin faces managed bean registry...");
        portalPluginFacesMBeanRegistry = r;
    }

    @Unbind
    public void unbindPortalPluginFacesMBeanRegistry() {
        log.debug("Unbound from portal plugin faces managed bean registry...");
        portalPluginFacesMBeanRegistry = null;
    }

    @Bind
    public void bindUserPreferencesRegistry(UserPreferencesRegistry r) {
        log.debug("Bound to user preferences registry...");
        userPreferencesRegistry = r;
    }

    @Unbind
    public void unbindUserPreferencesRegistry() {
        log.debug("Unbound from user preferences registry...");
        userPreferencesRegistry = null;
    }

    private static MappingSce mappingSce = null;

    @Bind
    public void bindMappingBSce(MappingSce s) {
        log.debug("Bound to mapping service...");
        mappingBSce = s;
        mappingSce = s;
    }

    @Unbind
    public void unbindMappingBSce() {
        log.debug("Unbound from mapping service...");
        mappingBSce = null;
        mappingSce = null;
    }

    public static final String MAPPING_USER_PREF_LAYOUT = "mappingDisplayLayout";
    public static final String MAPPING_USER_PREF_VIEW   = "mappingDisplayView";
    public static final String MAPPING_USER_PREF_MODE   = "mappingDisplayMode";
    public static final String MAPPING_USER_PREF_EPH    = "mappingDisplayEPH";

    @Validate
    public void validate() throws Exception {
        restResourceRegistry.registerPluginRestEndpoints(MappingBootstrap.class.getResource(REST_EP_FILE_PATH));
        portalPluginFacesMBeanRegistry.registerPluginFacesMBeanConfig(MappingBootstrap.class.getResource(FACES_CONFIG_FILE_PATH));
        MAIN_MENU_MAPPING_CONTEXT = portalPluginFacesMBeanRegistry.getRegisteredServletContext().getContextPath()+"/";

        try {
            MainMenuEntity entity = new MainMenuEntity("mappingMItem", "Mapping", MAIN_MENU_MAPPING_CONTEXT+"views/mapping2.jsf", MenuEntityType.TYPE_MENU_ITEM, MAIN_MENU_MAP_RANK, "icon-mapping-ariane icon-large");
            entity.getDisplayRoles().add("mappingreader");
            entity.getDisplayPermissions().add("mappingDB:read");
            mappingMainMenuEntityList.add(entity);
            mainMenuEntityRegistry.registerMainLeftMenuEntity(entity);

            log.debug("{} has registered its main menu items", new Object[]{MAPPING_COMPONENT});
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        UserPreferenceSection mappingDisplay = new UserPreferenceSection("mappingDisplay",
                                                                         "Define your mapping preferences",
                                                                         UserPreferenceSectionType.TYPE_USR_PREF_SECTION_MAP).
                                                                         addEntity(new UserPreferenceEntity(MAPPING_USER_PREF_LAYOUT,
                                                                                                            UserPreferenceEntityType.TYPE_USR_PREF_ENTITY_ONEBUTTON_SELECT,
                                                                                                            "Define your prefered layout").
                                                                                                            addSelectValue("BBTree").
                                                                                                            addSelectValue("Tree").
                                                                                                            addSelectValue("Network")./*addSelectValue("Random").*/
                                                                                                            setFieldDefault("Tree")).
                                                                         addEntity(new UserPreferenceEntity(MAPPING_USER_PREF_MODE,
                                                                                                            UserPreferenceEntityType.TYPE_USR_PREF_ENTITY_ONEBUTTON_SELECT,
                                                                                                            "Define your prefered mode").
                                                                                                            addSelectValue("Navigation").
                                                                                                            addSelectValue("Edition").
                                                                                                            setFieldDefault("Navigation")).
                                                                         addEntity(new UserPreferenceEntity(MAPPING_USER_PREF_EPH,
                                                                                                            UserPreferenceEntityType.TYPE_USR_PREF_ENTITY_ONEBUTTON_SELECT,
                                                                                                            "Define endpoint helper status").
                                                                                                            addSelectValue("ON").addSelectValue("OFF").
                                                                                                            setFieldDefault("OFF"));
        /*.
                                                                         addEntity(new UserPreferenceEntity(MAPPING_USER_PREF_VIEW,
                                                                                                            UserPreferenceEntityType.TYPE_USR_PREF_ENTITY_ONEBUTTON_SELECT,
                                                                                                            "Define your prefered view").
                                                                                                            addSelectValue("Component").
                                                                                                            addSelectValue("Cluster").
                                                                                                            addSelectValue("Application").
                                                                                                            setFieldDefault("Component")).*/

        userPreferencesRegistry.registerUserPreferenceSection(mappingDisplay);
        /*
        userPreferencesRegistry.registerUserPreferenceSection(new UserPreferenceSection("bookmarkedDSL",
                                                                                        "Manage your bookmarked DSL requests",
                                                                                        UserPreferenceSectionType.TYPE_USR_PREF_SECTION_MAP));
                                                                                        */
        log.debug("{} has registered its user properties entities", new Object[]{MAPPING_COMPONENT});
        log.info("{} is started", MAPPING_COMPONENT);
    }

    @Invalidate
    public void invalidate() throws Exception {
        if (mainMenuEntityRegistry!=null) {
            for(MainMenuEntity entity : mappingMainMenuEntityList) {
                mainMenuEntityRegistry.unregisterMainLeftMenuEntity(entity);
            }
        }
        mappingMainMenuEntityList.clear();

        if (userPreferencesRegistry!=null) {
            for(UserPreferenceSection section : userPreferenceSectionList) {
                userPreferencesRegistry.unregisterUserPreferenceSection(section);
            }
        }
        userPreferenceSectionList.clear();
        log.info("{} is stopped", MAPPING_COMPONENT);
    }

    public static MappingSce getMappingSce() {
        return mappingSce;
    }

    @Override
    public URL resolveURL(String path) {
        log.debug("Resolve {} from mapping wat...", new Object[]{path});
        return MappingBootstrap.class.getResource(basePath + path);
    }
}