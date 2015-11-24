package com.properned.application;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

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
public class ManageLocaleController {

	private Logger logger = LogManager.getLogger(this.getClass());

	private MultiLanguageProperties multiLanguageProperties = MultiLanguageProperties
			.getInstance();

	private ObservableList<Locale> list = FXCollections.observableArrayList();

	@FXML
	private ListView<Locale> localeList;

	@FXML
	private TextField localeTextField;

	public void addLocale(ActionEvent event) {
		String text = localeTextField.getText();
		if (StringUtils.isEmpty(text)) {
			// nothing to add
			return;
		}
		if (text.contains("_")) {
			// There is a language code and a country code
			String[] split = text.split("\\_");
			Locale locale = new Locale(split[0], split[1]);
			list.add(locale);
		} else {
			// there is only a language code
			Locale locale = new Locale(text);
			list.add(locale);
		}

	}

	public void initialize() {
		initializeList();
	}

	public void initializeList() {
		list = FXCollections.observableArrayList(multiLanguageProperties
				.getMapPropertiesByLocale().keySet());

		SortedList<Locale> sortedList = new SortedList<>(list,
				new Comparator<Locale>() {
					@Override
					public int compare(Locale o1, Locale o2) {
						return o1.toString().compareTo(o2.toString());
					}
				});

		localeList.setItems(sortedList);

		localeList.setCellFactory(c -> new LocaleListCell(
				multiLanguageProperties, this));

		list.addListener(new ListChangeListener<Locale>() {
			@Override
			public void onChanged(Change<? extends Locale> c) {
				if (c.next()) {
					if (c.wasAdded()) {
						List<? extends Locale> addedSubList = c
								.getAddedSubList();
						for (Locale locale : addedSubList) {
							try {
								MultiLanguageProperties.getInstance()
										.addLocale(locale);
							} catch (IOException e) {
								Properned
										.getInstance()
										.showError(
												MessageReader
														.getInstance()
														.getMessage(
																"manageLocale.error.errorAddLocale"),
												e);
							}
						}

					}
				}
			}
		});
	}
}
