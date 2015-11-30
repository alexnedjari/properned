package com.properned.application;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

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
public class MessageReader {

	private static MessageReader instance;

	private Map<Locale, ResourceBundle> cache = new HashMap<Locale, ResourceBundle>();

	private Locale locale = Locale.FRENCH;

	private MessageReader() {
		// to avoid construction
	}

	public static MessageReader getInstance() {
		if (instance == null) {
			instance = new MessageReader();
		}
		return instance;
	}

	public ResourceBundle getBundle() {
		if (cache.get(locale) == null) {
			cache.put(locale, ResourceBundle.getBundle(
					"com/properned/properties/messages", locale));
		}
		return cache.get(locale);
	}

	public String getMessage(String key, String... args) {
		String value = getBundle().getString(key);
		if (args.length == 0) {
			return value;
		}
		MessageFormat formatter = new MessageFormat("");
		formatter.setLocale(locale);

		value = value.replaceAll("'", "''");
		formatter.applyPattern(value);
		return formatter.format(args);
	}
}
