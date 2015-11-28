package com.properned.application;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXML;
import javafx.scene.web.WebView;

import org.apache.commons.io.IOUtils;
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
 * @since 28 october 2015
 */
public class HelpController {

	private Logger logger = LogManager.getLogger(this.getClass());

	@FXML
	private WebView webView;

	public void initialize() {

		URL url = this.getClass().getResource("/com/properned/help/help.html");
		String content;
		try {
			content = IOUtils.toString(new FileInputStream(url.getFile()));
			webView.getEngine().loadContent(content);
			// webView.getEngine().loadContent("bonjour");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
