package com.properned.application;

import java.util.Locale;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;

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
public class ValueListCell extends ListCell<Locale> {
	MultiLanguageProperties properties;
	private ListView<String> messageKeyList;

	public ValueListCell(MultiLanguageProperties properties,
			ListView<String> messageKeyList) {
		super();
		this.properties = properties;
		this.messageKeyList = messageKeyList;

	}

	@Override
	protected void updateItem(Locale locale, boolean empty) {
		super.updateItem(locale, empty);
		setGraphic(null);
		setText(null);
		if (locale != null
				&& messageKeyList.getSelectionModel().getSelectedItem() != null) {

			TextArea textArea = new TextArea();
			final String selectedMessageKey = messageKeyList
					.getSelectionModel().getSelectedItem();
			textArea.setText(properties.getMapPropertiesByLocale().get(locale)
					.getProperty(selectedMessageKey));
			textArea.textProperty().addListener(new ChangeListener<String>() {
				@Override
				public void changed(
						ObservableValue<? extends String> observable,
						String oldValue, String newValue) {
					properties
							.setProperty(selectedMessageKey, newValue, locale);

				}
			});
			textArea.setPrefHeight(100);
			Label label = new Label();
			label.setText(properties.getMapPropertiesFileByLocale().get(locale)
					.getLocaleString());
			BorderPane borderPane = new BorderPane(textArea, label, null, null,
					null);
			setGraphic(borderPane);
		}
	}
}
