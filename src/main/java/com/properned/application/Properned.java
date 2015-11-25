package com.properned.application;

import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.properned.application.preferences.Preferences;
import com.properned.application.preferences.PropernedProperties;
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
public class Properned extends Application {

	private Stage primaryStage;
	private SystemController controller;
	private static Properned instance;

	private Logger logger = LogManager.getLogger(this.getClass());

	@Override
	public void start(Stage primaryStage) {
		try {
			logger.info("Launching "
					+ PropernedProperties.getInstance()
							.getApplicationPresentation());

			Platform.setImplicitExit(false);
			instance = this;
			Properned.initializePreference();
			this.primaryStage = primaryStage;

			FXMLLoader loader = new FXMLLoader();
			loader.setResources(MessageReader.getInstance().getBundle());
			loader.setLocation(Properned.class
					.getResource("/com/properned/gui/mainFrame.fxml"));
			BorderPane root = (BorderPane) loader.load();

			Scene scene = new Scene(root, 1024, 768);
			scene.getStylesheets().add("/com/properned/style/application.css");
			primaryStage.setScene(scene);
			primaryStage.getIcons().add(
					new Image("/com/properned/style/icon/icon_16.png"));
			controller = loader.getController();
			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

				@Override
				public void handle(WindowEvent event) {
					logger.info("Properned close required");
					if (MultiLanguageProperties.getInstance().getIsDirty()) {
						ButtonType result = controller.askForSave();
						if (result.getButtonData() == ButtonData.CANCEL_CLOSE) {
							// The software must not be closed
							event.consume();
							return;
						}
					}
					Preferences.getInstance().save();
					Platform.exit();
					System.exit(0);

				}

			});

			scene.setOnDragOver(new EventHandler<DragEvent>() {
				@Override
				public void handle(DragEvent event) {
					System.out.println("dragover");
					if (event.getDragboard().hasFiles()) {
						event.acceptTransferModes(TransferMode.ANY);
					}

					event.consume();

				}
			});

			scene.setOnDragDropped(new EventHandler<DragEvent>() {
				@Override
				public void handle(DragEvent event) {
					controller.loadFileList(event.getDragboard().getFiles()
							.get(0));

				}
			});

			primaryStage.show();
		} catch (IOException e) {
			showError(
					MessageReader.getInstance().getMessage(
							"error.initialisation"), e);
		}
	}

	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public static Properned getInstance() {
		return instance;
	}

	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * Initialize the preference
	 */
	private static void initializePreference() {
		// init the properties
		Preferences.getInstance();
	}

	public void showError(String error, Exception e) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle(MessageReader.getInstance().getMessage(
						"window.error.title"));
				alert.setHeaderText(MessageReader.getInstance().getMessage(
						"window.error.header"));
				alert.setContentText(error);
				logger.error(error, e);
				alert.showAndWait();

			}
		});
	}
}
