package com.properned.application;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.properned.application.preferences.Preferences;
import com.properned.application.preferences.recentfile.RecentFile;
import com.properned.model.MultiLanguageProperties;
import com.properned.model.PropertiesFile;

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
public class SystemController {

	private Logger logger = LogManager.getLogger(this.getClass());

	private MultiLanguageProperties multiLanguageProperties = MultiLanguageProperties
			.getInstance();

	@FXML
	private Menu recentFileMenu;

	@FXML
	private ListView<String> messageKeyList;

	@FXML
	private ListView<Locale> valueList;

	@FXML
	private TextField filterText;

	@FXML
	private Button saveButton;

	@FXML
	private Button addButton;

	@FXML
	private Button localeButton;

	public void initialize() {
		logger.info("Initialize System controller");
		localeButton.disableProperty().bind(
				multiLanguageProperties.isLoadedProperty().not());
		saveButton.disableProperty().bind(
				multiLanguageProperties.isDirtyProperty().not()
						.or(multiLanguageProperties.isLoadedProperty().not()));
		Stage primaryStage = Properned.getInstance().getPrimaryStage();
		primaryStage
				.titleProperty()
				.bind(multiLanguageProperties
						.baseNameProperty()
						.concat(Bindings
								.when(multiLanguageProperties
										.isLoadedProperty())
								.then(new SimpleStringProperty(" (").concat(
										multiLanguageProperties
												.parentDirectoryPathProperty())
										.concat(")")).otherwise(""))
						.concat(Bindings
								.when(multiLanguageProperties.isDirtyProperty())
								.then(" *").otherwise("")));

		FilteredList<String> filteredList = new FilteredList<>(
				multiLanguageProperties.getListMessageKey(),
				new Predicate<String>() {
					@Override
					public boolean test(String t) {
						String filter = filterText.getText();
						if (filter == null || filter.equals("")) {
							return true;
						}
						return t.contains(filter);
					}
				});
		SortedList<String> sortedList = new SortedList<>(filteredList,
				new Comparator<String>() {
					@Override
					public int compare(String o1, String o2) {
						return o1.compareTo(o2);
					}
				});
		messageKeyList.setItems(sortedList);
		filterText.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable,
					String oldValue, String newValue) {
				// Filter the list
				filteredList.setPredicate(new Predicate<String>() {
					@Override
					public boolean test(String t) {
						String filter = filterText.getText();
						if (filter == null || filter.equals("")) {
							return true;
						}
						return t.contains(filter);
					}
				});

				// check the add button disabled status
				if (isKeyCanBeAdded(newValue)) {
					addButton.setDisable(false);
				} else {
					addButton.setDisable(true);
				}

			}

		});
		ChangeListener<String> changeMessageListener = new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable,
					String oldValue, String newValue) {
				logger.info("Message key selection changed : " + newValue);
				valueList.setItems(FXCollections.observableArrayList());

				valueList.setItems(FXCollections
						.observableArrayList(multiLanguageProperties
								.getMapPropertiesByLocale().keySet()));
			}
		};
		messageKeyList.getSelectionModel().selectedItemProperty()
				.addListener(changeMessageListener);
		messageKeyList.setCellFactory(c -> new MessageKeyListCell(
				multiLanguageProperties));

		valueList.setCellFactory(c -> new ValueListCell(
				multiLanguageProperties, messageKeyList));

		filterText.setOnKeyReleased(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.DOWN) {
					messageKeyList.requestFocus();
					event.consume();
				} else if (event.getCode() == KeyCode.ENTER) {
					addKey();
					event.consume();
				}
			}
		});
	}

	private boolean isKeyCanBeAdded(String newValue) {
		return !(StringUtils.isEmpty(newValue)
				|| !multiLanguageProperties.getIsLoaded() || multiLanguageProperties
				.getListMessageKey().contains(newValue));
	}

	@FXML
	public void addKey() {
		String newMessageKey = filterText.getText();
		if (isKeyCanBeAdded(newMessageKey)) {
			logger.info("Add the messagekey" + newMessageKey);
			multiLanguageProperties.getListMessageKey().add(newMessageKey);
			messageKeyList.getSelectionModel().select(newMessageKey);
		}
	}

	@FXML
	public void save() {
		// Save only if a file is loaded
		if (multiLanguageProperties.getIsLoaded()) {
			logger.info("Save the multi language properties");
			try {
				multiLanguageProperties.save();
			} catch (IOException e) {
				Properned.getInstance()
						.showError(
								MessageReader.getInstance().getMessage(
										"error.save"), e);
			}
		}
	}

	@FXML
	public void openAboutDialog() {
		logger.info("Open the about dialog");
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource(
				"/com/properned/gui/aboutFrame.fxml"));
		loader.setResources(MessageReader.getInstance().getBundle());

		try {
			loader.load();

			Parent root = loader.getRoot();

			Stage modalDialog = new Stage(StageStyle.UTILITY);
			modalDialog.initOwner(Properned.getInstance().getPrimaryStage());
			modalDialog.setTitle(MessageReader.getInstance().getMessage(
					"menu.help.about"));
			modalDialog.setResizable(false);

			Scene scene = new Scene(root);
			scene.getStylesheets().add("/com/properned/style/application.css");

			modalDialog.setScene(scene);

			modalDialog.showAndWait();
		} catch (IOException e) {
			Properned.getInstance().showError(
					MessageReader.getInstance().getMessage("error.openFrame"),
					e);
		}
	}

	@FXML
	public void openHelpDialog() {
		logger.info("Open the help dialog");
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource(
				"/com/properned/gui/helpFrame.fxml"));
		loader.setResources(MessageReader.getInstance().getBundle());

		try {
			loader.load();
			Parent root = loader.getRoot();
			Stage modalDialog = new Stage();
			modalDialog.setTitle(MessageReader.getInstance().getMessage(
					"menu.help.help"));
			modalDialog.setResizable(true);
			modalDialog.getIcons().add(
					new Image("/com/properned/style/icon/icon_16.png"));

			Scene scene = new Scene(root);
			scene.getStylesheets().add("/com/properned/style/application.css");
			modalDialog.setScene(scene);
			modalDialog.show();
		} catch (IOException e) {
			Properned.getInstance().showError(
					MessageReader.getInstance().getMessage("error.openFrame"),
					e);
		}
	}

	@FXML
	public void openLocaleDialog() {
		logger.info("Open the locale dialog");

		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource(
				"/com/properned/gui/localeFrame.fxml"));
		loader.setResources(MessageReader.getInstance().getBundle());

		try {
			loader.load();

			Parent root = loader.getRoot();

			Stage modalDialog = new Stage(StageStyle.UNIFIED);
			modalDialog.initModality(Modality.APPLICATION_MODAL);
			modalDialog.initOwner(Properned.getInstance().getPrimaryStage());
			modalDialog.setTitle(MessageReader.getInstance().getMessage(
					"manageLocale.title"));
			modalDialog.setResizable(true);
			modalDialog.getIcons().add(
					new Image("/com/properned/style/icon/icon_16.png"));

			Scene scene = new Scene(root);
			scene.getStylesheets().add("/com/properned/style/application.css");

			modalDialog.setScene(scene);

			modalDialog.showAndWait();
		} catch (IOException e) {
			Properned.getInstance().showError(
					MessageReader.getInstance().getMessage("error.openFrame"),
					e);
		}
	}

	public void openPropertiesFile() {
		logger.info("Open the 'Open file' dialog");
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle(MessageReader.getInstance().getMessage(
				"window.openFile.title"));
		String lastPathUsed = Preferences.getInstance().getLastPathUsed();
		File lastSelectedFile = new File(lastPathUsed);
		if (StringUtils.isNotEmpty(lastPathUsed) && lastSelectedFile != null
				&& lastSelectedFile.getParentFile() != null
				&& lastSelectedFile.getParentFile().exists()) {
			fileChooser.setInitialDirectory(lastSelectedFile.getParentFile());
		}
		File selectedFile = fileChooser.showOpenDialog(Properned.getInstance()
				.getPrimaryStage().getScene().getWindow());
		if (selectedFile != null) {
			logger.info("Selected file : " + selectedFile.getAbsolutePath());
			Task<Void> loadTask = new Task<Void>() {
				@Override
				protected Void call() throws Exception {
					loadFileList(selectedFile);
					return null;
				}
			};
			loadTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

				@Override
				public void handle(WorkerStateEvent event) {
					logger.info("load successfull");
					Properned.getInstance().getPrimaryStage().getScene()
							.setOnKeyReleased(new EventHandler<KeyEvent>() {
								@Override
								public void handle(KeyEvent event) {
									if (event.getCode() == KeyCode.S
											&& event.isControlDown()) {
										logger.info("CTRL-S detected");
										save();
										event.consume();
									}
								}
							});
					Preferences.getInstance().addFileToRecentFileList(
							selectedFile.getAbsolutePath());
				}
			});
			Executors.newSingleThreadExecutor().submit(loadTask);
		}
	}

	public void loadFileList(File selectedFile) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				logger.info("Load the file list associated to '"
						+ selectedFile.getAbsolutePath() + "'");

				if (MultiLanguageProperties.getInstance().getIsDirty()) {
					ButtonType result = askForSave();
					if (result.getButtonData() == ButtonData.CANCEL_CLOSE) {
						// The file must not be loaded
						return;
					}
				}
				Preferences.getInstance().setLastPathUsed(
						selectedFile.getAbsolutePath());
				String fileName = selectedFile.getName();
				String baseNameTemp = fileName.substring(0,
						fileName.lastIndexOf("."));

				if (fileName.contains("_")) {
					baseNameTemp = fileName.substring(0, fileName.indexOf("_"));
				}
				final String baseName = baseNameTemp;

				List<PropertiesFile> fileList = Arrays
						.asList(selectedFile.getParentFile().listFiles(
								new FileFilter() {
									@Override
									public boolean accept(File pathname) {
										return pathname.isFile()
												&& pathname.getName()
														.startsWith(baseName)
												&& pathname.getName().endsWith(
														".properties");
									}
								})).stream()
						.map(new Function<File, PropertiesFile>() {
							@Override
							public PropertiesFile apply(File t) {
								String language = "";
								if (t.getName().contains("_")) {
									language = t.getName().substring(
											t.getName().indexOf("_") + 1,
											t.getName().lastIndexOf("."));
								}
								return new PropertiesFile(t.getAbsolutePath(),
										baseName, new Locale(language));
							}
						}).collect(Collectors.<PropertiesFile> toList());

				try {
					multiLanguageProperties.loadFileList(baseName, fileList);
				} catch (IOException e) {
					Properned.getInstance().showError(
							MessageReader.getInstance()
									.getMessage("error.load"), e);
				}
			}
		});
	}

	@FXML
	public void close() {
		logger.info("closed by menu");
		Properned
				.getInstance()
				.getPrimaryStage()
				.getOnCloseRequest()
				.handle(new WindowEvent(Properned.getInstance()
						.getPrimaryStage(), WindowEvent.WINDOW_CLOSE_REQUEST));
		Properned.getInstance().getPrimaryStage().close();
	}

	public ButtonType askForSave() {
		logger.info("Save alert open");
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle(MessageReader.getInstance().getMessage(
				"popup.confirmation.warning"));
		alert.setHeaderText(MessageReader.getInstance().getMessage(
				"popup.confirmation.close.title"));
		alert.setContentText(MessageReader.getInstance().getMessage(
				"popup.confirmation.close.body"));

		ButtonType buttonTypeYes = new ButtonType(MessageReader.getInstance()
				.getMessage("yes"));
		ButtonType buttonTypeNo = new ButtonType(MessageReader.getInstance()
				.getMessage("no"));
		ButtonType buttonTypeCancel = new ButtonType("cancel",
				ButtonData.CANCEL_CLOSE);
		alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo,
				buttonTypeCancel);

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == buttonTypeYes) {
			logger.info("the user want to save current files");
			this.save();
		} else if (result.get() == buttonTypeNo) {
			// Nothing to do here
			logger.info("the user doesn't want to save current files");
		} else {
			logger.info("Properned close cancelled");
		}
		return result.get();

	}

	public void populateRecentFileMenu() {
		logger.info("Loading recent files");

		recentFileMenu.getItems().clear();
		List<RecentFile> recentFileList = Preferences.getInstance()
				.getRecentFileList();
		for (final RecentFile recentFile : recentFileList) {
			if (recentFile.getFile().exists()) {
				MenuItem menuItemRecentFile = new MenuItem(recentFile.getFile()
						.getAbsolutePath());
				menuItemRecentFile.setMnemonicParsing(false);
				menuItemRecentFile.setOnAction(new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent event) {
						loadFileList(recentFile.getFile());
					}
				});
				recentFileMenu.getItems().add(menuItemRecentFile);
			}
		}
	}
}
