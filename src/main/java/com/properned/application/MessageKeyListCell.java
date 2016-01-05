package com.properned.application;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCode;

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
public class MessageKeyListCell extends ListCell<String> {

	private Logger logger = LogManager.getLogger(this.getClass());

	MultiLanguageProperties properties;

	public MessageKeyListCell(MultiLanguageProperties properties) {
		super();
		this.properties = properties;
	}

	@Override
	protected void updateItem(String messageKey, boolean empty) {
		super.updateItem(messageKey, empty);
		setGraphic(null);
		setText(null);
		if (messageKey != null) {
			setText(messageKey);

			MenuItem deleteMenu = getDeleteMenu(messageKey);
			MenuItem copyMenu = getCopyMenu(messageKey);

			this.setContextMenu(new ContextMenu(copyMenu,
					new SeparatorMenuItem(), deleteMenu));

			this.getListView().setOnKeyPressed(
					event -> {
						if (event.getCode() == KeyCode.C
								&& event.isControlDown()) {
							String selectedKey = getListView()
									.getSelectionModel().getSelectedItem();
							copy(selectedKey);
						}
					});
		}
	}

	private MenuItem getDeleteMenu(String messageKey) {
		MenuItem deleteMenu = new MenuItem(MessageReader.getInstance()
				.getMessage("action.deleteMessageKey"));
		deleteMenu.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				properties.deleteMessageKey(messageKey);
			}
		});
		return deleteMenu;
	}

	private MenuItem getCopyMenu(String messageKey) {
		MenuItem copyMenu = new MenuItem(MessageReader.getInstance()
				.getMessage("action.copyMessageKey"));
		copyMenu.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				copy(messageKey);
			}

		});
		return copyMenu;
	}

	private void copy(String messageKey) {
		logger.info("Copy key : " + messageKey);
		Toolkit.getDefaultToolkit().getSystemClipboard()
				.setContents(new StringSelection(messageKey), null);
	}
}
