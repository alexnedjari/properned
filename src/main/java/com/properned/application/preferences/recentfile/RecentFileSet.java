/**
 *
 */
package com.properned.application.preferences.recentfile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Properned is a software that can be used to edit java properties files 2015
 * Alexandre NEDJARI
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * @author Alexandre NEDJARI
 * @since 28 october 2015
 */
public class RecentFileSet {

	/**
	 * The recent file list
	 */
	private ArrayList<RecentFile> recentFileList = new ArrayList<RecentFile>();

	/**
	 * The recent file list max size
	 */
	private int recentFileListSize;

	/**
	 * Constructor
	 * 
	 * @param newRecentFileListSize
	 *            the max size of the set
	 */
	public RecentFileSet(int newRecentFileListSize) {
		this.recentFileListSize = newRecentFileListSize;
	}

	/**
	 * Add a new recent file to the list If the recent file is already in the
	 * list, just update The last access date
	 * 
	 * @param recentFile
	 *            the recent file to add/update
	 */
	public void addRecentFile(RecentFile recentFile) {
		if (!recentFile.getFile().exists()) {
			return;
		}
		for (RecentFile recentFileToCheck : this.recentFileList) {
			if (recentFileToCheck.getFile().equals(recentFile.getFile())) {
				// File is already in the recent files, just update the last
				// access date
				recentFileToCheck.setLastAccessDate(recentFile
						.getLastAccessDate());
				return;
			}
		}
		// File is not already in the list, add it
		this.recentFileList.add(recentFile);
		Collections.sort(this.recentFileList,
				new RecentFileComparatorByLastAccessDate());
		while (this.recentFileList.size() > this.recentFileListSize) {
			this.recentFileList.remove(0);
		}
	}

	/**
	 * Get a copy of the complete list (sorted by last access date
	 * 
	 * @return a copy of the complete list
	 */
	public List<RecentFile> getRecentFileListCopy() {
		Collections.sort(this.recentFileList,
				new RecentFileComparatorByLastAccessDate());
		return new ArrayList<RecentFile>(this.recentFileList);
	}

}
