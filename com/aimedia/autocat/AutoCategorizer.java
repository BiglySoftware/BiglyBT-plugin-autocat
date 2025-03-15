/*
 * Copyright (C) 2005  Chris Rose
 *
 * AutoCatPlugin is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * AutoCatPlugin is distributed in the hope that it will be useful,
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

import java.util.List;

import com.biglybt.core.util.Debug;
import com.biglybt.pif.download.Download;
import com.biglybt.pif.download.DownloadListener;
import com.biglybt.pif.download.DownloadManager;
import com.biglybt.pif.download.DownloadManagerListener;
import com.biglybt.pif.logging.LoggerChannel;
import com.biglybt.pif.torrent.Torrent;
import com.biglybt.pif.utils.LocaleUtilities;
import com.biglybt.pifimpl.local.PluginCoreUtils;

import com.biglybt.core.tag.Tag;
import com.biglybt.core.tag.TagManager;
import com.biglybt.core.tag.TagManagerFactory;
import com.biglybt.core.tag.TagType;
import com.aimedia.autocat.config.Config;
import com.aimedia.autocat2.matching.IRuleSetListener;
import com.aimedia.autocat2.matching.OrderedRuleSet;

/**
 * , created 8-Jan-2005
 *
 * @author Chris Rose
 */
public class AutoCategorizer implements IRuleSetListener, DownloadManagerListener {

    final transient private DownloadManager dlm;

    final transient private LoggerChannel   log;

    final transient private LocaleUtilities locale;

    private Config config;

    public AutoCategorizer(Config config) {
        super ();
        this.config = config;
        if (config == null) { throw new IllegalArgumentException ("Null rulesets are not permitted"); }
        dlm = AutoCatPlugin.getPlugin ().getPluginInterface ().getDownloadManager ();
        log = AutoCatPlugin.getPlugin ().getLog ();
        locale = AutoCatPlugin.getPlugin ().getPluginInterface ().getUtilities ().getLocaleUtilities ();
        config.getRules ().addRuleSetListener (this);
        dlm.addListener (this);
    }

    /**
     * @see com.aimedia.autocat.matching.IRuleSetListener#ruleSetUpdated(com.aimedia.autocat.matching.RuleSet)
     */
    @Override
    public void ruleSetUpdated (final OrderedRuleSet rules) {
        final Download[] dls = dlm.getDownloads ();
        for (int i = 0; i < dls.length; i++) {
            match (dls[i]);
        }
    }

    /**
     * @see com.biglybt.pif.download.DownloadManagerListener#downloadAdded(com.biglybt.pif.download.Download)
     */
    @Override
    public void downloadAdded (final Download download) {
        // ** FIXED **
        download.addListener (new DownloadListener () {

            @Override
            public void stateChanged (Download download, int old_state, int new_state) {
                if (old_state == Download.ST_READY
                        && (new_state == Download.ST_DOWNLOADING || new_state == Download.ST_SEEDING)) {
                    match (download);
                }
            }

            @Override
            public void positionChanged (Download download, int oldPosition, int newPosition) {
                // Nothing
            }
        });
        // ** END FIXED **
        // ** BROKEN (Fails to allow the torrent to be added)
        // match(download);
        // ** END BROKEN
    }

    /**
     * @see com.biglybt.pif.download.DownloadManagerListener#downloadRemoved(com.biglybt.pif.download.Download)
     */
    @Override
    public void downloadRemoved (final Download download) {
        // Nothing here.
    }

    private void match (final Download download) {
        final Torrent torrent = download.getTorrent ();
        if ( config.isUseTags()){
        	TagManager tm = TagManagerFactory.getTagManager();
        	
        	List<Tag> existing_tags = tm.getTagsForTaggable( TagType.TT_DOWNLOAD_MANUAL, PluginCoreUtils.unwrap( download ));
        	
        	if ( existing_tags.size() > 0 ){
        		 if (! config.isModifyExistingCategories ()){
 	                return;
 	            }
        	}
        	
        	final String tag_name = config.getRules ().match (torrent);
        	
 	        if (tag_name == null) { 
 	        	// as below
 	        }else{
 	        	TagType tt = tm.getTagType( TagType.TT_DOWNLOAD_MANUAL );
 	        	 
 	        	Tag tag = tt.getTag( tag_name, true );
				
				if ( tag == null ){
					
					try{
						tag = tt.createTag( tag_name, true );
						
					}catch( Throwable e ){
						
						Debug.out( e );
					}
				}

				if ( tag != null ){
					
					tag.addTaggable( PluginCoreUtils.unwrap( download ));
				}
				
	            log.log (LoggerChannel.LT_INFORMATION, locale.getLocalisedMessageText ("autocat.matcher.matched",
	                    new String[] { tag_name, torrent.getName () }));
 	        }
        }else{
	        String categoryName = download.getCategoryName ();
	
	        // : if so configured, ignore already-categorized torrents.
	        if (! "Categories.uncategorized".equals (categoryName)) {
	            if (! config.isModifyExistingCategories ()) {
	                return;
	            }
	        }
	
	        final String cat = config.getRules ().match (torrent);
	        if (cat == null) { // NOPMD
	            // TODO Not too sure why I disabled this...
	            // d.setCategory("Categories.uncategorized");
	            // log.log(LoggerChannel.LT_INFORMATION, locale
	            // .getLocalisedMessageText("autocat.matcher.nomatch",
	            // new String[] { cat, t.getName() }));
	        	
	        }else{
	        	
	            download.setCategory (cat);
	            
	            log.log (LoggerChannel.LT_INFORMATION, locale.getLocalisedMessageText ("autocat.matcher.matched",
	                    new String[] { cat, torrent.getName () }));
	        }
        }
    }
}