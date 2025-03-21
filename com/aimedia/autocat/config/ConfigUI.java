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

package com.aimedia.autocat.config;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import com.biglybt.pif.PluginInterface;
import com.biglybt.pif.logging.LoggerChannel;
import com.biglybt.pif.torrent.TorrentAttribute;
import com.biglybt.pif.utils.LocaleUtilities;
import com.biglybt.ui.swt.pif.UISWTConfigSection;

import com.biglybt.core.tag.Tag;
import com.biglybt.core.tag.TagManagerFactory;
import com.biglybt.core.tag.TagType;
import com.aimedia.autocat.AutoCatPlugin;
import com.aimedia.autocat2.matching.AdvancedRuleSet;
import com.aimedia.autocat2.matching.IRuleSetListener;
import com.aimedia.autocat2.matching.OrderedRuleSet;
import com.aimedia.autocat2.matching.TorrentFieldType;
import com.aimedia.autocat2.matching.TorrentMatcher;

/**
 * @author Chris Rose
 */
public class ConfigUI implements UISWTConfigSection, IRuleSetListener {

    private Label                 lblEnabled                   = null;

    private Label                 lblField                     = null;

    private Button                chkEnabled                   = null;
   
    private Label                 lblUseTags                   = null;

    private Button                chkUseTags                   = null;

    private Table                 tblRules                     = null;

    private Label                 lblTrigger                   = null;

    private Text                  txtTrigger                   = null;

    private Button                btnAdd                       = null;

    private Button                btnDel                       = null;

    private Label                 lblCategory                  = null;

    private Combo                 cmbCategory                  = null;

    private Combo                 cmbField                     = null;

    private final PluginInterface pi;

    private final LoggerChannel   log;

    private final LocaleUtilities locale;

    private Map                   typeMap;

    private Config config;

    private Label lblModExisting;

    private Button chkModExisting;

    public ConfigUI (Config config) {
        this.pi = AutoCatPlugin.getPlugin ().getPluginInterface ();
        this.log = AutoCatPlugin.getPlugin ().getLog ();
        locale = pi.getUtilities ().getLocaleUtilities ();
        /* Populate the section with the configuration values */
        log.log (LoggerChannel.LT_INFORMATION, locale.getLocalisedMessageText ("autocat.info.populating"));
        this.config = config;
    }

    @Override
    public int maxUserMode() {
    	return 0;
    }
    /*
     * (non-Javadoc)
     *
     * @see com.biglybt.pif.ui.config.ConfigSectionSWT#configSectionCreate(org.eclipse.swt.widgets.Composite)
     */

    @Override
    public Composite configSectionCreate (final Composite parent) {
        config.getRules ().addRuleSetListener (this);
        final Composite panel = new Composite (parent, SWT.NONE);
        final GridData gridData5 = new org.eclipse.swt.layout.GridData ();
        final GridData gridData4 = new org.eclipse.swt.layout.GridData ();
        final GridData gridData1 = new org.eclipse.swt.layout.GridData ();
        final GridData gridData3 = new org.eclipse.swt.layout.GridData ();
        final GridData gridData2 = new org.eclipse.swt.layout.GridData ();
        final GridData gridData6 = new org.eclipse.swt.layout.GridData ();
        final GridData gridData7 = new org.eclipse.swt.layout.GridData ();
        final GridLayout gridLayout1 = new GridLayout ();
        
        lblEnabled = new Label (panel, SWT.NONE);
        chkEnabled = new Button (panel, SWT.CHECK);
        
        lblUseTags = new Label (panel, SWT.NONE);
        chkUseTags = new Button (panel, SWT.CHECK);
        
        lblModExisting = new Label (panel, SWT.NONE);
        chkModExisting = new Button (panel, SWT.CHECK);
        
        createTable (panel);
        lblTrigger = new Label (panel, SWT.NONE);
        txtTrigger = new Text (panel, SWT.BORDER);
        lblCategory = new Label (panel, SWT.NONE);
        createCategoryCombo (panel);
        lblField = new Label (panel, SWT.NONE);
        createFieldCombo (panel);
        createControlPanel (panel);
        
        gridData3.horizontalSpan = 4;
        gridData3.horizontalAlignment = org.eclipse.swt.layout.GridData.BEGINNING;
        gridData3.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
        gridData3.grabExcessHorizontalSpace = true;
        chkEnabled.setLayoutData (gridData3);
        
        gridData6.horizontalSpan = 4;
        gridData6.horizontalAlignment = org.eclipse.swt.layout.GridData.BEGINNING;
        gridData6.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
        gridData6.grabExcessHorizontalSpace = true;
        chkUseTags.setLayoutData (gridData6);

        gridData2.horizontalSpan = 4;
        gridData2.horizontalAlignment = org.eclipse.swt.layout.GridData.BEGINNING;
        gridData2.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
        gridData2.grabExcessHorizontalSpace = true;
        chkModExisting.setLayoutData (gridData2);
        
        gridData4.grabExcessHorizontalSpace = false;
        gridData4.horizontalSpan = 2;
        gridData1.grabExcessHorizontalSpace = false;
        gridData1.horizontalSpan = 2;
        gridData7.grabExcessHorizontalSpace = false;
        gridData7.horizontalSpan = 2;
        
        lblEnabled.setLayoutData (gridData4);  
        lblUseTags.setLayoutData (gridData7);  
        lblModExisting.setLayoutData (gridData1);
        lblTrigger.setText (locale.getLocalisedMessageText ("autocat.trigger"));
        lblTrigger.setToolTipText (locale.getLocalisedMessageText ("autocat.trigger.tooltip"));
        gridData5.horizontalSpan = 5;
        gridData5.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        gridData5.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
        txtTrigger.setLayoutData (gridData5);
        txtTrigger.setToolTipText (locale.getLocalisedMessageText ("autocat.trigger.tooltip"));
        txtTrigger.addVerifyListener (new VerifyListener () {

            @Override
            public void verifyText (VerifyEvent e) {
                // TODO Implement any verification of the trigger input.
                e.doit = true;
            }
        });
        lblCategory.setText (locale.getLocalisedMessageText ("autocat.category"));
        lblEnabled.setText (locale.getLocalisedMessageText ("autocat.enabled"));
        chkEnabled.setToolTipText (locale.getLocalisedMessageText ("autocat.enabled.tooltip"));
        lblUseTags.setText (locale.getLocalisedMessageText ("autocat.use_tags"));
        chkUseTags.setToolTipText (locale.getLocalisedMessageText ("autocat.use_tags.tooltip"));
        lblModExisting.setText (locale.getLocalisedMessageText ("autocat.modify_existing"));
        chkModExisting.setToolTipText (locale.getLocalisedMessageText ("autocat.modify_existing.tooltip"));
        panel.setLayout (gridLayout1);
        gridLayout1.numColumns = 6;
        lblField.setText (locale.getLocalisedMessageText ("autocat.config.field"));
        panel.setSize (new org.eclipse.swt.graphics.Point (635, 456));
        chkEnabled.addSelectionListener (new SelectionAdapter () {
            /*
             * (non-Javadoc)
             *
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected (SelectionEvent e) {
                final Button b = (Button) e.widget;
                boolean enablement = b.getSelection ();
                config.setEnabled (enablement);

                chkModExisting.setEnabled (enablement);
                chkUseTags.setEnabled (enablement);
                txtTrigger.setEnabled (enablement);
                cmbCategory.setEnabled (enablement);
                tblRules.setEnabled (enablement);
                btnAdd.setEnabled (enablement);
                btnDel.setEnabled (enablement);

                if (b.getSelection ()) {
                    log.log (locale.getLocalisedMessageText ("autocat.logenabled"));
                }
                else {
                    log.log (locale.getLocalisedMessageText ("autocat.logdisabled"));
                }
            }
        });

        chkUseTags.addSelectionListener (new SelectionAdapter () {
            @Override
            public void widgetSelected (SelectionEvent e) {
                final Button b = (Button) e.widget;
                config.setUseTags (b.getSelection ());
                loadCategoriesOrTags(cmbCategory);
            }
        });
        
        chkModExisting.addSelectionListener (new SelectionAdapter () {
            @Override
            public void widgetSelected (SelectionEvent e) {
                final Button b = (Button) e.widget;
                config.setModifyExistingCategories (b.getSelection ());
            }
        });

        boolean enablement = config.isEnabled ();
        chkEnabled.setSelection (enablement);
        chkUseTags.setEnabled (enablement);
        chkUseTags.setSelection (config.isUseTags());
        chkModExisting.setEnabled (enablement);
        chkModExisting.setSelection (config.isModifyExistingCategories ());
        txtTrigger.setEnabled (enablement);
        cmbCategory.setEnabled (enablement);
        tblRules.setEnabled (enablement);
        btnAdd.setEnabled (enablement);
        btnDel.setEnabled (enablement);

        log.log ("configSectionGetParentSection() end");
        return panel;
    }

    void loadCategoriesOrTags (final Combo cmb) {
    	if ( config.isUseTags()){
	       	TagType tt = TagManagerFactory.getTagManager().getTagType( TagType.TT_DOWNLOAD_MANUAL );

	       	List<Tag> tags = tt.getTags();
	       	
	       	String[] tag_names = new String[tags.size()];
	       	
	       	for ( int i=0;i<tags.size();i++){
	       		tag_names[i] = tags.get(i).getTagName( true );
	       	}
	        cmb.removeAll ();
	        cmb.setItems (tag_names );
    	}else{
	        final TorrentAttribute ta = pi.getTorrentManager ().getAttribute (TorrentAttribute.TA_CATEGORY);
	        cmb.removeAll ();
	        cmb.setItems (ta.getDefinedValues ());
    	}
    }

    void loadFields (final Combo cmb) {
        final List fields = TorrentFieldType.getSupportedFields ();
        final String[] flist = new String[fields.size ()];
        for (int i = 0; i < flist.length; i++) {
            flist[i] = ((TorrentFieldType) fields.get (i)).getName ();
        }
        cmb.removeAll ();
        cmb.setItems (flist);
    }

    void loadRules (final OrderedRuleSet set, final Table t) {
        final TableItem[] items = t.getItems ();
        for (int i = 0; i < items.length; i++) {
            items[i].dispose ();
        }
        final List<TorrentMatcher> rules = set.getRules ();
        for (int i = 0; i < rules.size (); i++) {
            final TableItem ti = new TableItem (t, SWT.NONE);
            ti.setText (0, i + "");
            TorrentMatcher rule = rules.get (i);
            ti.setText (1, rule.getTrigger ());
            ti.setText (2, rule.getCategory ());
            ti.setText (3, rule.getMatchField ().toString ());
            log.log ("Added rule " + rule.getTrigger () + ":" + rule.getMatchField ().toString () + "===>"
                    + rule.getCategory ());
        }
        t.setSelection (0);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.aimedia.autocat.config.RuleSetListener#ruleSetUpdated()
     */
    public void ruleSetUpdated (final AdvancedRuleSet rules) {
        loadRules (rules, tblRules);
    }

    /**
     * This method initializes cmbField
     *
     * @param parent
     *                TODO
     *
     */
    private void createCategoryCombo (final Composite parent) {
        final GridData gridData13 = new org.eclipse.swt.layout.GridData ();
        cmbCategory = new Combo (parent, SWT.NONE);
        gridData13.horizontalSpan = 5;
        gridData13.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        gridData13.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
        cmbCategory.setLayoutData (gridData13);
        cmbCategory.setToolTipText (locale.getLocalisedMessageText ("autocat.category.tooltip"));
        loadCategoriesOrTags (cmbCategory);
    }

    /**
     * This method initializes composite1
     *
     * @param parent
     *                TODO
     *
     */
    private void createControlPanel (final Composite parent) {
        final GridData gridData12 = new org.eclipse.swt.layout.GridData ();
        final Composite cControls = new Composite (parent, SWT.NONE);
        btnAdd = new Button (cControls, SWT.NONE);
        btnDel = new Button (cControls, SWT.NONE);
        cControls.setLayout (new RowLayout ());
        cControls.setLayoutData (gridData12);
        gridData12.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        gridData12.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
        gridData12.horizontalSpan = 6;
        btnAdd.setText (locale.getLocalisedMessageText ("autocat.config.add"));
        btnAdd.addSelectionListener (new SelectionAdapter () {
            /*
             * (non-Javadoc)
             *
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected (SelectionEvent e) {
                // log.log(LoggerChannel.LT_WARNING, "Add needs to be
                // implemented");
                try {
                    Pattern.compile (txtTrigger.getText ());
                    if (cmbCategory.getText ().length () == 0 || cmbField.getSelectionIndex () < 0) {
                        // log.log("Cannot set 0-length categories");
                        log.log (locale.getLocalisedMessageText ("autocat.err.nocat"));
                    }
                    else {
                        // TODO Make this work
                        final TorrentFieldType type = (TorrentFieldType) getTypeMap ().get (
                                cmbField.getItem (cmbField.getSelectionIndex ()));
                        config.getRules ().addRule (type, txtTrigger.getText (), cmbCategory.getText ());
                    }
                }
                catch (PatternSyntaxException pse) {
                    // log.log("Could not compile regex", pse);
                    log.log (locale.getLocalisedMessageText ("autocat.err.badregex", new String[] { txtTrigger
                            .getText () }));
                }
                loadCategoriesOrTags (cmbCategory);
            }
        });
        btnDel.setText (locale.getLocalisedMessageText ("autocat.config.remove"));
        btnDel.addSelectionListener (new SelectionAdapter () {
            /*
             * (non-Javadoc)
             *
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected (SelectionEvent e) {
                // log.log(LoggerChannel.LT_WARNING, "Del needs to be
                // implemented");
                if (txtTrigger.getText ().length () > 0) {
                    for (int ruleIndex : tblRules.getSelectionIndices ()) {
                        config.getRules ().removeRule (ruleIndex);
                    }
                }
            }
        });
    }

    /**
     * @return
     */
    protected Map getTypeMap () {
        if (null == typeMap) {
            typeMap = new HashMap ();
            final List f = TorrentFieldType.getSupportedFields ();
            for (final Iterator iter = f.iterator (); iter.hasNext ();) {
                final TorrentFieldType field = (TorrentFieldType) iter.next ();
                typeMap.put (field.toString (), field);
            }
        }
        return typeMap;
    }

    /**
     * This method initializes cmbField
     *
     * @param parent
     *                TODO
     *
     */
    private void createFieldCombo (final Composite parent) {
        final GridData gridData1 = new org.eclipse.swt.layout.GridData ();
        cmbField = new Combo (parent, SWT.READ_ONLY);
        gridData1.horizontalSpan = 5;
        gridData1.grabExcessHorizontalSpace = false;
        gridData1.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        cmbField.setLayoutData (gridData1);
        cmbField.setToolTipText (locale.getLocalisedMessageText ("autocat.config.field.tooltip"));
        loadFields (cmbField);
    }

    /**
     * This method initializes table
     *
     * @param parent
     *                TODO
     *
     */
    private void createTable (final Composite parent) {
        final GridData gridData2 = new org.eclipse.swt.layout.GridData ();
        tblRules = new Table (parent, SWT.FULL_SELECTION | SWT.BORDER);
        final TableColumn colRuleIndex = new TableColumn (tblRules, SWT.NONE);
        final TableColumn colTrigger = new TableColumn (tblRules, SWT.NONE);
        final TableColumn colCategory = new TableColumn (tblRules, SWT.NONE);
        final TableColumn colField = new TableColumn (tblRules, SWT.NONE);
        colTrigger.setText (locale.getLocalisedMessageText ("autocat.trigger"));
        colTrigger.setWidth (200);
        colCategory.setText (locale.getLocalisedMessageText ("autocat.category"));
        colCategory.setWidth (200);
        colField.setText (locale.getLocalisedMessageText ("autocat.config.field"));
        colField.setWidth (200);
        colRuleIndex.setResizable (false);
        gridData2.verticalSpan = 1;
        tblRules.setHeaderVisible (true);
        gridData2.grabExcessHorizontalSpace = true;
        gridData2.grabExcessVerticalSpace = true;
        gridData2.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        gridData2.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
        gridData2.horizontalSpan = 6;
        tblRules.setLayoutData (gridData2);
        tblRules.setLinesVisible (true);
        loadRules (config.getRules (), tblRules);
        tblRules.addSelectionListener (new SelectionAdapter () {
            /*
             * (non-Javadoc)
             *
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected (SelectionEvent e) {
                e.doit = true;
                final int i = tblRules.getSelectionIndex ();
                if (i >= 0) {
                    final TableItem t = tblRules.getItem (i);
                    txtTrigger.setText (t.getText (1));
                    cmbCategory.setText (t.getText (2));
                    cmbField.select (cmbField.indexOf (t.getText (3)));
                }
            }
        });
        tblRules.pack ();
    }

    @Override
    public void ruleSetUpdated (OrderedRuleSet set) {
        loadRules (set, tblRules);
    }

    @Override
    public void configSectionDelete () {
        config.getRules ().removeRuleSetListener (this);
        config.configSectionDelete ();
    }

    @Override
    public String configSectionGetName () {
        return config.configSectionGetName ();
    }

    @Override
    public String configSectionGetParentSection () {
        return config.configSectionGetParentSection ();
    }

    @Override
    public void configSectionSave () {
        config.configSectionSave ();
    }

}