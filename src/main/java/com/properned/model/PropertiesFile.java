package com.properned.model;

import java.io.File;
import java.util.Locale;

import com.properned.application.MessageReader;

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
public class PropertiesFile extends File {

	private static final long serialVersionUID = 1L;
	private String baseName;
	private Locale locale;

	public PropertiesFile(String pathname, String baseName, Locale locale) {
		super(pathname);
		this.baseName = baseName;
		this.locale = locale;
	}

	public String getBaseName() {
		return baseName;
	}

	public void setBaseName(String baseName) {
		this.baseName = baseName;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public String getLocaleString() {
		if (locale.getLanguage() == null || locale.getLanguage().equals("")) {
			return MessageReader.getInstance().getMessage("locale.default");
		}
		return locale.toString();
	}

}
