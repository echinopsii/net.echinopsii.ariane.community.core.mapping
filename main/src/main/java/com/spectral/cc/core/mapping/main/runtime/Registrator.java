/**
 * Mapping Web Service :
 * provide a mapping DS Web Service and REST Service
 *
 * Copyright (C) 2013  Mathilde Ffrench
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
package com.spectral.cc.core.mapping.main.runtime;

import com.spectral.cc.core.portal.commons.consumer.MainMenuRegistryConsumer;
import com.spectral.cc.core.portal.commons.consumer.UserPreferencesRegistryConsumer;
import com.spectral.cc.core.portal.commons.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Registrator implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(Registrator.class);

    private static String MAIN_MENU_MAPPING_CONTEXT = "/CCmapping/";
    private static int MAIN_MENU_MAP_RANK = 4;

    @Override
    public void run() {
        MainMenuEntity entity;

        //TODO : remove this uuugly sleep
        //TODO : check a better way to start war after OSGI layer
        while(MainMenuRegistryConsumer.getInstance().getMainMenuEntityRegistry()==null || UserPreferencesRegistryConsumer.getInstance().getUserPreferencesRegistry()==null)
            try {
                log.info("Portal main menu registry and/or portal user preference registry are missing to load {}. Sleep some times...", OsgiActivator.TOPO_WS_SERVICE_NAME);
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

        try {
            entity = new MainMenuEntity("mappingMItem", "Mapping", MAIN_MENU_MAPPING_CONTEXT+"views/mapping.jsf", MenuEntityType.TYPE_MENU_ITEM, MAIN_MENU_MAP_RANK, "icon-sitemap icon-large");
            OsgiActivator.mainPortalMainMenuEntityList.add(entity);
            MainMenuRegistryConsumer.getInstance().getMainMenuEntityRegistry().registerMainMenuEntity(entity);

            log.debug("{} has registered its main menu items", new Object[]{OsgiActivator.TOPO_WS_SERVICE_NAME});

        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        UserPreferenceSection mappingDisplay = new UserPreferenceSection("mappingDisplay", "Define your mapping preferences", UserPreferenceSectionType.TYPE_USR_PREF_SECTION_MAP).
            addEntity(
                new UserPreferenceEntity(
                    "mappingDisplayLayout",
                    UserPreferenceEntityType.TYPE_USR_PREF_ENTITY_ONEBUTTON_SELECT,
                    "Define your prefered layout").addSelectValue("Tree").addSelectValue("Network")./*addSelectValue("Random").*/setFieldDefault("Tree")).
            addEntity(
                new UserPreferenceEntity(
                    "mappingDisplayView",
                    UserPreferenceEntityType.TYPE_USR_PREF_ENTITY_ONEBUTTON_SELECT,
                    "Define your prefered view").addSelectValue("Infrastructure").addSelectValue("Component").addSelectValue("Application").setFieldDefault("Infrastructure")).
            addEntity(
                new UserPreferenceEntity(
                    "mappingDisplayMode",
                    UserPreferenceEntityType.TYPE_USR_PREF_ENTITY_ONEBUTTON_SELECT,
                    "Define your prefered mode").addSelectValue("Navigation").addSelectValue("Edition").setFieldDefault("Navigation")
            );
        UserPreferencesRegistryConsumer.getInstance().getUserPreferencesRegistry().registerUserPreferenceSection(
                                        new UserPreferenceSection("bookmarkedDSL", "Manage your bookmarked DSL requests", UserPreferenceSectionType.TYPE_USR_PREF_SECTION_MAP));
        UserPreferencesRegistryConsumer.getInstance().getUserPreferencesRegistry().registerUserPreferenceSection(mappingDisplay);

        log.debug("{} has registered its user properties entities", new Object[]{OsgiActivator.TOPO_WS_SERVICE_NAME});
    }
}
