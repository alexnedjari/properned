/**
 *
 */
package com.properned.application.preferences.recentfile;

import java.util.Comparator;

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
public class RecentFileComparatorByLastAccessDate implements
		Comparator<RecentFile> {

	@Override
	public int compare(RecentFile o1, RecentFile o2) {
		return o1.getLastAccessDate().compareTo(o2.getLastAccessDate());
	}

}
