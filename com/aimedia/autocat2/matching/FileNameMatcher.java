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

package com.aimedia.autocat2.matching;

import java.util.regex.Matcher;

import com.biglybt.pif.torrent.Torrent;

/**
 * , created 9-Apr-2005
 * 
 * @author Chris Rose
 */
public class FileNameMatcher extends TorrentMatcher {

    protected FileNameMatcher(String triggerText, String category) {
        super (TorrentFieldType.FILE_NAME, triggerText, category);
    }

    /*
     * @see com.aimedia.autocat2.matching.IMatcher#match(com.biglybt.pif.torrent.Torrent)
     */
    @Override
    public boolean match (final Torrent torrent) {
        final Matcher m = getTriggerPattern ().matcher (torrent.getName ());
        return m.find ();
    }

}
