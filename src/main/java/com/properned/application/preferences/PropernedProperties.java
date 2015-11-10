/**
 *
 */
package com.properned.application.preferences;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
 * @since 30 november 2015
 */
public class PropernedProperties {

	/**
	 * The instance
	 */
	private static PropernedProperties instance;

	/**
	 * The property file key for application name
	 */
	public static String KEY_APPLICATION_NAME = "application.name";

	/**
	 * The property file key for major version key
	 */
	public static String KEY_VERSION = "version";

	/**
	 * The property file key for author
	 */
	public static String KEY_AUTHOR = "author";

	/**
	 * The used properties
	 */
	private Properties properties = new Properties();

	/**
	 * The property file
	 */
	private File propertyFile;

	/**
	 * The logger for this class
	 */
	private Logger logger = LogManager.getLogger(this.getClass());

	/**
	 * Constructor
	 */
	private PropernedProperties() {
		try {
			propertyFile = new File("Properned.properties");

			if (!this.propertyFile.exists()) {
				this.propertyFile.createNewFile();
				this.properties.setProperty(PropernedProperties.KEY_VERSION,
						"0");
				this.properties.setProperty(
						PropernedProperties.KEY_APPLICATION_NAME, "Properned");
				this.properties.store(new FileOutputStream(this.propertyFile),
						"");
			}
			this.properties.load(new FileInputStream(this.propertyFile));
		} catch (IOException e) {
			this.logger.error("Impossible d'accéder au fichier '"
					+ this.propertyFile.getAbsolutePath()
					+ "', utilisation des valeurs par défaut");
		}
	}

	/**
	 * Save the properties
	 * 
	 * @throws IOException
	 *             If save failed
	 */
	public void save() throws IOException {
		this.properties.store(new BufferedOutputStream(new FileOutputStream(
				this.propertyFile)), "");
	}

	/**
	 * Get the instance
	 * 
	 * @return the instance
	 */
	synchronized public static PropernedProperties getInstance() {
		if (PropernedProperties.instance == null) {
			PropernedProperties.instance = new PropernedProperties();
		}
		return PropernedProperties.instance;
	}

	/**
	 * get the version
	 * 
	 * @return the major version
	 */
	public String getVersion() {
		return this.properties
				.getProperty(PropernedProperties.KEY_VERSION, "0");
	}

	/**
	 * get the application name
	 * 
	 * @return the application name
	 */
	public String getApplicationName() {
		return this.properties.getProperty(
				PropernedProperties.KEY_APPLICATION_NAME, "UNKNOW");
	}

	/**
	 * Get the application name and version
	 * 
	 * @return the application name and version
	 */
	public String getApplicationPresentation() {
		return this.getApplicationName() + " " + this.getVersion();
	}

	public String getAuthor() {
		return this.properties.getProperty(PropernedProperties.KEY_AUTHOR,
				"UNKNOW");
	}
}
