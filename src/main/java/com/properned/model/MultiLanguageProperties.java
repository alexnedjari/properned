package com.properned.model;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
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

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

	private BooleanProperty isLoaded = new SimpleBooleanProperty(false);
	private BooleanProperty isDirty = new SimpleBooleanProperty(false);
	private StringProperty baseName = new SimpleStringProperty(MessageReader
			.getInstance().getMessage("mainFrame.defaultFileName"));
	private StringProperty parentDirectoryPath = new SimpleStringProperty();

	private Map<Locale, Properties> mapPropertiesByLocale = new HashMap<Locale, Properties>();
	private Map<Locale, PropertiesFile> mapPropertiesFileByLocale = new HashMap<Locale, PropertiesFile>();
	private ObservableList<String> listMessageKey = FXCollections
			.observableArrayList();

	private static MultiLanguageProperties instance;

	private Logger logger = LogManager.getLogger(this.getClass());

	public static synchronized MultiLanguageProperties getInstance() {
		if (instance == null) {
			instance = new MultiLanguageProperties();
			instance.init();
		}
		return instance;
	}

	private void init() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				logger.info("Initializing the multi language properties instance");
				isDirty.set(false);
				isLoaded.set(false);
				mapPropertiesByLocale.clear();
				mapPropertiesFileByLocale.clear();
				listMessageKey.clear();
			}
		});
	}

	private MultiLanguageProperties() {
		// avoid construction
	}

	public BooleanProperty isLoadedProperty() {
		return isLoaded;
	}

	public BooleanProperty isDirtyProperty() {
		return isDirty;
	}

	public StringProperty baseNameProperty() {
		return baseName;
	}

	public Map<Locale, Properties> getMapPropertiesByLocale() {
		return mapPropertiesByLocale;
	}

	public Map<Locale, PropertiesFile> getMapPropertiesFileByLocale() {
		return mapPropertiesFileByLocale;
	}

	private void addProperties(Properties properties, Locale locale,
			PropertiesFile file) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				logger.info("Add a propertie file for the locale '" + locale
						+ "'");
				mapPropertiesByLocale.put(locale, properties);
				mapPropertiesFileByLocale.put(locale, file);
				Set<String> stringPropertyNames = properties
						.stringPropertyNames();

				// use of a set to prevent duplicated values into the list
				Set<String> setPropertyTotal = new HashSet<>(listMessageKey);
				setPropertyTotal.addAll(stringPropertyNames);
				listMessageKey.clear();
				listMessageKey.addAll(setPropertyTotal);
				baseName.set(file.getBaseName());
				parentDirectoryPath.set(file.getParent() + File.separator);
				isLoaded.set(true);
			}
		});

	}

	public ObservableList<String> getListMessageKey() {
		return listMessageKey;
	}

	public boolean getIsLoaded() {
		return isLoaded.get();
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
		logger.info("new baseName : " + baseName);
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				MultiLanguageProperties.this.baseName.set(baseName);
			}
		});
	}

	public void setProperty(String messageKey, String value, Locale locale) {
		if (StringUtils.isNotEmpty(value)) {
			logger.info("new property value : " + messageKey + " (" + locale
					+ ") : " + value);
			this.getMapPropertiesByLocale().get(locale)
					.setProperty(messageKey, value);
		} else {
			logger.info("remove property : " + messageKey + " (" + locale + ")");
			this.getMapPropertiesByLocale().get(locale).remove(messageKey);
		}
		this.setIsDirty(true);

	}

	public void save() throws IOException {
		logger.info("Save the multi language properties instance");
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

	public void loadFileList(String baseName, List<PropertiesFile> fileList)
			throws IOException {
		logger.info("load a multi language properties (" + baseName + ") with "
				+ fileList.size() + " files");
		// delete current values
		init();

		BufferedInputStream inStream = null;
		for (PropertiesFile file : fileList) {
			logger.info("load the file " + file.getAbsolutePath());
			try {
				if (!file.exists()) {
					throw new IOException("Missing file "
							+ file.getAbsolutePath());
				}
				Properties properties = new Properties();
				inStream = new BufferedInputStream(new FileInputStream(file));
				properties.load(inStream);

				addProperties(properties, file.getLocale(), file);
			} finally {
				IOUtils.closeQuietly(inStream);
			}
		}
	}

	public void deleteMessageKey(String messageKey) {
		logger.info("Delete the message key '" + messageKey + "'");
		listMessageKey.remove(messageKey);
		Set<Locale> setLocale = mapPropertiesFileByLocale.keySet();
		for (Locale locale : setLocale) {
			Properties properties = mapPropertiesByLocale.get(locale);
			properties.remove(messageKey);
			this.setIsDirty(true);
		}
	}

	public void addLocale(Locale locale) throws IOException {
		logger.info("Ask the creation of a propertie file for locale :"
				+ locale);
		Properties properties = new Properties();
		String newFileAbsolutePath = this.parentDirectoryPath.get()
				+ getFileName(locale);
		logger.info("the new file name is '" + newFileAbsolutePath + "'");
		PropertiesFile propertiesFile = new PropertiesFile(newFileAbsolutePath,
				baseName.get(), locale);
		propertiesFile.createNewFile();
		this.addProperties(properties, locale, propertiesFile);
	}

	private String getFileName(Locale locale) {
		String fileName = baseName.get();
		if (StringUtils.isNotEmpty(locale.getLanguage())) {
			fileName += "_" + locale.getLanguage();
		}
		if (StringUtils.isNotEmpty(locale.getCountry())) {
			fileName += "_" + locale.getCountry();
		}
		fileName += ".properties";
		return fileName;
	}

	public void deleteLocale(Locale locale) {
		logger.info("Remove locale " + locale.toString());

		PropertiesFile propertiesFile = mapPropertiesFileByLocale.get(locale);
		if (propertiesFile != null) {
			propertiesFile.delete();
		}

		mapPropertiesByLocale.remove(locale);
		mapPropertiesFileByLocale.remove(locale);
	}
}
