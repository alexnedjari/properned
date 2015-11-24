package com.properned.application;

import java.util.Locale;
import java.util.Optional;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.MenuItem;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.properned.model.MultiLanguageProperties;

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
public class LocaleListCell extends ListCell<Locale> {
	private Logger logger = LogManager.getLogger(this.getClass());
	MultiLanguageProperties properties;
	private ManageLocaleController controller;

	public LocaleListCell(MultiLanguageProperties properties,
			ManageLocaleController controller) {
		super();
		this.properties = properties;
		this.controller = controller;
	}

	@Override
	protected void updateItem(Locale locale, boolean empty) {
		super.updateItem(locale, empty);
		setGraphic(null);
		setText(null);
		if (locale != null) {
			String localeToString = locale.toString();
			if (localeToString.isEmpty()) {
				localeToString = MessageReader.getInstance().getMessage(
						"locale.default");
			}
			if (StringUtils.isNotEmpty(locale.getDisplayName())) {
				localeToString += " (" + locale.getDisplayName() + ")";
			}

			setText(localeToString);

			MenuItem deleteMenu = getDeleteMenu(locale);

			this.setContextMenu(new ContextMenu(deleteMenu));
		}
	}

	private MenuItem getDeleteMenu(Locale locale) {
		MenuItem deleteMenu = new MenuItem(MessageReader.getInstance()
				.getMessage("action.deleteMessageKey"));
		deleteMenu.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				logger.info("Clic on delete button for locale '"
						+ locale.toString() + "'");

				String fileName = properties.getMapPropertiesFileByLocale()
						.get(locale).getAbsolutePath();

				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle(MessageReader.getInstance().getMessage(
						"manageLocale.confirmDelete.title"));
				alert.setHeaderText(MessageReader.getInstance().getMessage(
						"manageLocale.confirmDelete.header"));
				alert.setContentText(MessageReader.getInstance().getMessage(
						"manageLocale.confirmDelete.content", fileName));
				Optional<ButtonType> result = alert.showAndWait();
				if (result.isPresent() && result.get() == ButtonType.OK) {
					logger.info("User say OK to the confirm");
					properties.deleteLocale(locale);

					// Reload list
					controller.initializeList();
				}

			}
		});
		return deleteMenu;
	}
}
