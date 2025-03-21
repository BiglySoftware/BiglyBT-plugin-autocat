/*
 * Copyright (C) 2005  Chris Rose
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */

package com.aimedia.autocat;

import java.util.Date;

import com.biglybt.pif.Plugin;
import com.biglybt.pif.PluginException;
import com.biglybt.pif.PluginInterface;
import com.biglybt.pif.logging.LoggerChannel;
import com.biglybt.pif.logging.LoggerChannelListener;
import com.biglybt.pif.ui.model.BasicPluginViewModel;
import com.biglybt.pif.utils.LocaleUtilities;

import com.aimedia.autocat.config.Config;
import com.aimedia.autocat.config.ConfigUI;

/**
 * Plugin class. Implements the <code>Plugin</code> interface and provided
 * global services to the rest of the plugin.
 *
 * @author Chris Rose
 */
public class AutoCatPlugin implements Plugin {

    private static AutoCatPlugin instance = null;

    public static AutoCatPlugin getPlugin () {
        return instance;
    }

    private PluginInterface    pluginInterface;

    private LoggerChannel      log;

    private Config config;

    /**
     * The plugin's name. This <strong>MUST MATCH THE DIRECTORY AND JAR NAMES</strong>
     */
    public static final String NAME = "autocat";

    /*
     * (non-Javadoc)
     *
     * @see com.biglybt.pif.Plugin#initialize(com.biglybt.pif.PluginInterface)
     */
    @Override
    public void initialize (final PluginInterface pluginInterface) throws PluginException {
        instance = this;
        this.pluginInterface = pluginInterface;

        this.log = pluginInterface.getLogger ().getChannel (NAME);

        final BasicPluginViewModel view = pluginInterface.getUIManager ().createBasicPluginViewModel (
                pluginInterface.getUtilities ().getLocaleUtilities ().getLocalisedMessageText ("autocat.name"));
        view.getActivity ().setVisible (false);
        view.getProgress ().setVisible (false);
        view.getStatus ().setVisible (false);

        log.addListener (new LoggerChannelListener () {

            @Override
            public void messageLogged (int type, String content) {
                view.getLogArea ().appendText ("[" + new Date ().toString () + "] " + content + "\n");
            }

            @Override
            public void messageLogged (String str, Throwable error) {
                view.getLogArea ().appendText ("[" + new Date ().toString () + "] " + str + "\n");
                view.getLogArea ().appendText (error.getLocalizedMessage () + "\n");
            }
        });

        this.config = new Config (pluginInterface);

        final ConfigUI cfg = new ConfigUI (config);

        pluginInterface.addConfigSection (cfg);

        AutoCategorizer categorizer = new AutoCategorizer (config); // NOPMD
        // categorizer.ruleSetUpdated(EditableRuleSetImpl.getInstance());

    }

    /**
     * @return Returns the log.
     */
    public final LoggerChannel getLog () {
        return log;
    }

    /**
     * @return Returns the pluginInterface.
     */
    public final PluginInterface getPluginInterface () {
        return pluginInterface;
    }

    public final LocaleUtilities getLocale () {
        return pluginInterface.getUtilities ().getLocaleUtilities ();
    }
}