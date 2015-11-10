package com.properned.model;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

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
public class MultiLanguageProperties {
	private BooleanProperty isDirty = new SimpleBooleanProperty(false);
	private StringProperty baseName = new SimpleStringProperty(MessageReader
			.getInstance().getMessage("mainFrame.defaultFileName"));

	private Map<Locale, Properties> mapPropertiesByLocale = new HashMap<Locale, Properties>();
	private Map<Locale, PropertiesFile> mapPropertiesFileByLocale = new HashMap<Locale, PropertiesFile>();
	private ObservableList<String> listMessageKey = FXCollections
			.observableArrayList();

	public BooleanProperty isDirtyProperty() {
		return isDirty;
	}

	public StringProperty baseNameProperty() {
		return baseName;
	}

	public Map<Locale, Properties> getMapPropertiesByLocale() {
		return mapPropertiesByLocale;
	}

	public void setMapPropertiesByLocale(
			Map<Locale, Properties> mapPropertiesByLocale) {
		this.mapPropertiesByLocale = mapPropertiesByLocale;
	}

	public Map<Locale, PropertiesFile> getMapPropertiesFileByLocale() {
		return mapPropertiesFileByLocale;
	}

	public void setMapPropertiesFileByLocale(
			Map<Locale, PropertiesFile> mapPropertiesFileByLocale) {
		this.mapPropertiesFileByLocale = mapPropertiesFileByLocale;
	}

	public void addProperties(Properties properties, Locale locale,
			PropertiesFile file) {
		this.mapPropertiesByLocale.put(locale, properties);
		this.mapPropertiesFileByLocale.put(locale, file);
		Set<String> stringPropertyNames = properties.stringPropertyNames();

		// use of a set to prevent duplicated values into the list
		Set<String> setPropertyTotal = new HashSet<>(listMessageKey);
		setPropertyTotal.addAll(stringPropertyNames);
		listMessageKey.clear();
		listMessageKey.addAll(setPropertyTotal);
		baseName.set(file.getBaseName());
	}

	public void clear() {
		this.listMessageKey.clear();
		this.mapPropertiesByLocale.clear();
		this.mapPropertiesFileByLocale.clear();
	}

	public ObservableList<String> getListMessageKey() {
		return listMessageKey;
	}

	public void setListMessageKey(ObservableList<String> listMessageKey) {
		this.listMessageKey = listMessageKey;
	}

	public boolean getIsDirty() {
		return isDirty.get();
	}

	public void setIsDirty(boolean isDirty) {
		this.isDirty.set(isDirty);
	}

	public String getBaseName() {
		return baseName.get();
	}

	public void setBaseName(String baseName) {
		this.baseName.set(baseName);
	}

	public void setProperty(String messageKey, String value, Locale locale) {
		if (StringUtils.isNotEmpty(value)) {
			this.getMapPropertiesByLocale().get(locale)
					.setProperty(messageKey, value);
		} else {
			this.getMapPropertiesByLocale().get(locale).remove(messageKey);
		}
		this.setIsDirty(true);

	}

	public void save() throws IOException {
		Set<Locale> setLocale = mapPropertiesFileByLocale.keySet();
		for (Locale locale : setLocale) {
			PropertiesFile propertiesFile = mapPropertiesFileByLocale
					.get(locale);
			Properties properties = mapPropertiesByLocale.get(locale);
			if (properties == null) {
				continue;
			}

			OutputStream fileOutputStream = null;
			try {
				fileOutputStream = new BufferedOutputStream(
						new FileOutputStream(propertiesFile));
				properties.store(fileOutputStream, "");
			} finally {
				IOUtils.closeQuietly(fileOutputStream);
			}
		}
		this.setIsDirty(false);
	}

	public static MultiLanguageProperties loadFileList(String baseName,
			List<PropertiesFile> fileList) throws IOException {
		MultiLanguageProperties multiLanguageProperties = new MultiLanguageProperties();
		BufferedInputStream inStream = null;
		try {
			for (PropertiesFile file : fileList) {
				if (!file.exists()) {
					throw new IOException("Missing file "
							+ file.getAbsolutePath());
				}
				Properties properties = new Properties();
				inStream = new BufferedInputStream(new FileInputStream(file));
				properties.load(inStream);

				multiLanguageProperties.addProperties(properties,
						file.getLocale(), file);
			}

		} finally {
			IOUtils.closeQuietly(inStream);

		}
		return multiLanguageProperties;
	}

	public void deleteMessageKey(String messageKey) {
		listMessageKey.remove(messageKey);
		Set<Locale> setLocale = mapPropertiesFileByLocale.keySet();
		for (Locale locale : setLocale) {
			Properties properties = mapPropertiesByLocale.get(locale);
			properties.remove(messageKey);
			this.setIsDirty(true);
		}
	}
}
