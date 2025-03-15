package com.aimedia.autocat.config;

import java.io.File;
import java.io.IOException;

import com.biglybt.core.util.FileUtil;
import com.biglybt.pif.PluginConfig;
import com.biglybt.pif.PluginException;
import com.biglybt.pif.PluginInterface;
import com.biglybt.pif.logging.LoggerChannel;
import com.biglybt.pif.ui.config.ConfigSection;
import com.biglybt.pif.utils.LocaleUtilities;

import com.aimedia.autocat.AutoCatPlugin;
import com.aimedia.autocat2.matching.AdvancedRuleSet;
import com.aimedia.autocat2.matching.OrderedRuleSet;

public class Config implements ConfigSection {

    private static final String   MATCHER_CONFIG_FILE          = AutoCatPlugin.NAME + "_rules.properties";

    private static final String   OBSOLETE_MATCHER_CONFIG_FILE = AutoCatPlugin.NAME + ".config";

    private final AdvancedRuleSet rules;

    private boolean               enabled;

    private boolean               useTags;
    
    private boolean               modifyExistingCategories;

    private PluginInterface pi;

    private LoggerChannel log;

    private LocaleUtilities locale;

    public Config (PluginInterface pluginInterface) {
        this.pi = pluginInterface;
        this.log = AutoCatPlugin.getPlugin ().getLog ();
        locale = pi.getUtilities ().getLocaleUtilities ();
        /* Populate the section with the configuration values */
        log.log (LoggerChannel.LT_INFORMATION, locale.getLocalisedMessageText ("autocat.info.populating"));

        rules = new AdvancedRuleSet ();
        // This occurs before the listener setup because we don't
        // have a table yet.

        // : look for a new-style property config file, first:
        File propertyFile = FileUtil.getUserFile (MATCHER_CONFIG_FILE);
        if (!propertyFile.exists ()) {

            // : upgrade an obsolete bencoded file if it is present.
            File mapFile = FileUtil.getUserFile (OBSOLETE_MATCHER_CONFIG_FILE);

            if (mapFile.exists ()) {
                AdvancedRuleSet.transferSettings (mapFile, propertyFile);
            }
            else {
                try {
                    propertyFile.createNewFile ();
                }
                catch (IOException e) {
                    AutoCatPlugin.getPlugin ().getLog ().log ("Unable to create a new configuration file", e);
                }
            }

            rules.load (propertyFile);
        }
        else {
            rules.load (propertyFile);
        }

        enabled = pi.getPluginconfig ().getPluginBooleanParameter ("enabled", false);
        modifyExistingCategories = pi.getPluginconfig ().getPluginBooleanParameter ("modifyExistingCategories", true);
        useTags = pi.getPluginconfig ().getPluginBooleanParameter ("useTags", false);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.biglybt.pif.ui.config.ConfigSection#configSectionGetParentSection()
     */
    @Override
    public String configSectionGetParentSection () {
        return ConfigSection.SECTION_PLUGINS;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.biglybt.pif.ui.config.ConfigSection#configSectionGetName()
     */
    @Override
    public String configSectionGetName () {
        log.log ("configSectionGetName() start/end");
        return AutoCatPlugin.NAME;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.biglybt.pif.ui.config.ConfigSection#configSectionSave()
     */
    @Override
    public void configSectionSave () {
        // Save the rules to their file.
        log.log ("configSectionSave() start");
        try {
            File saveFile = FileUtil.getUserFile (MATCHER_CONFIG_FILE);
            if (!saveFile.exists ()) {
                if (!saveFile.createNewFile ()) {
                    log.log (locale.getLocalisedMessageText ("autocat.err.failRuleCreate"));
                }
            }
            if (!rules.save (FileUtil.getUserFile (MATCHER_CONFIG_FILE))) {
                log.log (locale.getLocalisedMessageText ("autocat.err.failRuleWrite"));
            }
        }
        catch (IOException e) {
            log.log (e);
            log.log ("autocat.err.IO");
        }

        // Save all extra config information.
        final PluginConfig cfg = pi.getPluginconfig ();
        cfg.setPluginParameter ("enabled", enabled);
        cfg.setPluginParameter ("modifyExistingCategories", modifyExistingCategories);
        cfg.setPluginParameter ("useTags", useTags);
        try {
            cfg.save ();
            log.log ("configSectionSave() clean exit");
        }
        catch (PluginException e1) {
            log.log (e1);
            log.log ("configSectionSave() dirty exit");
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.biglybt.pif.ui.config.ConfigSection#configSectionDelete()
     */
    @Override
    public void configSectionDelete () {
        // TODO Anything that doesn't have a parent, I have to dispose() it
        // here.
        log.log (LoggerChannel.LT_INFORMATION, locale.getLocalisedMessageText ("autocat.info.configDelete"));
    }

    public boolean isEnabled () {
        return enabled;
    }

    public void setEnabled (boolean enablement) {
        if (this.enabled != enablement) {
            this.enabled = enablement;
        }
    }

    public OrderedRuleSet getRules () {
        return rules;
    }

    public boolean isUseTags () {
        return useTags;
    }

    public void setUseTags (boolean set) {
        if (this.useTags != set) {
            this.useTags = set;
        }
    }
    
    public boolean isModifyExistingCategories () {
        return modifyExistingCategories;
    }

    public void setModifyExistingCategories (boolean modifyExistingCategories) {
        if (this.modifyExistingCategories != modifyExistingCategories) {
            this.modifyExistingCategories = modifyExistingCategories;
        }
    }

}
