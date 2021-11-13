package TypingPracticeAndTest;

import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.Cursor;
import javafx.util.Duration;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.application.Application;
import javafx.animation.PauseTransition;
import javafx.scene.control.Alert.AlertType;

/*
   Copyright 2021 Melwyn Francis Carlo <carlo.melwyn@outlook.com>
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
       http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

public class View extends Application
{
    private static Stage primaryStage;

    @Override
    public void start(Stage inputPrimaryStage) throws Exception
    {
        primaryStage = inputPrimaryStage;

        final Parent rootNode = FXMLLoader.load(getClass().getResource("ui" + Model.FILE_SEPARATOR + "SplashScreen.fxml"));

        /* Prepare the window, and display the splash screen. */

        Controller.dataDirectoryCheck();

        Controller.saveParagraphInTempFile("");

        final Scene primaryScene = new Scene(rootNode);

        primaryStage.getIcons().add(new Image("TypingPracticeAndTest/ui/res/img/icon.png"));
        primaryStage.setTitle("Typing Practice and Test App");
        primaryStage.setScene(primaryScene);
        primaryStage.setResizable(false);
        primaryStage.show();

        launchTextInputWindow(Controller.SPLASH_SCREEN_TIME_IN_SECONDS);
    }

    @Override
    public void stop()
    {
        Controller.saveParagraphInTempFile("");
        System.exit(0);
    }

    public static void launchSplashScreen(String args[])
    {
        Application.launch(args);
    }

    public static void launchTextInputWindow(double waitTimeInSeconds)
    {
        Controller.windowNumber = 1;

        PauseTransition waitTimedTask = new PauseTransition(Duration.seconds(waitTimeInSeconds));

        waitTimedTask.setOnFinished(event -> 
        {
            try
            {
                final Parent rootNode = FXMLLoader.load(View.class.getResource("ui" + Model.FILE_SEPARATOR + "TextInput.fxml"));

                primaryStage.getScene().setRoot(rootNode);
            }
            catch (Exception exception_object)
            {
                System.out.println(exception_object.getMessage());
                System.exit(1);
            }
        });

        waitTimedTask.play();
    }

    public static void launchTypingWindow(Controller.TypingMode typingMode)
    {
        Controller.windowNumber = 2;

        PauseTransition waitTimedTask = new PauseTransition(Duration.seconds(Controller.NORMAL_WAIT_TIME_IN_SECONDS));

        waitTimedTask.setOnFinished(event -> 
        {
            try
            {
                final Parent rootNode = FXMLLoader.load(View.class.getResource("ui" + Model.FILE_SEPARATOR + "TypingWindow.fxml"));

                primaryStage.getScene().setRoot(rootNode);

                if (typingMode == Controller.TypingMode.PRACTICE)
                {
                    final Label     timerLabel = (Label)     rootNode.lookup("#timer-label");
                    final ImageView timerIcon  = (ImageView) rootNode.lookup("#timer-icon");

                    timerIcon.setVisible(false);
                    timerLabel.setVisible(false);
                }
                else /* if (typingMode == Controller.TypingMode.TEST) */
                {

                }
            }
            catch (Exception exception_object)
            {
                System.out.println(exception_object.getMessage());
                System.exit(1);
            }
        });

        waitTimedTask.play();
    }

    public static void launchTestEndWindow()
    {
        Controller.windowNumber = 3;

        PauseTransition waitTimedTask = new PauseTransition(Duration.seconds(Controller.NORMAL_WAIT_TIME_IN_SECONDS));

        waitTimedTask.setOnFinished(event -> 
        {
            try
            {
                final Parent rootNode = FXMLLoader.load(View.class.getResource("ui" + Model.FILE_SEPARATOR + "TestEndWindow.fxml"));

                primaryStage.getScene().setRoot(rootNode);
            }
            catch (Exception exception_object)
            {
                System.out.println(exception_object.getMessage());
                System.exit(1);
            }
        });

        waitTimedTask.play();
    }

    public static void showNotificationAlert(String notificationMessage)
    {
        Alert notificationAlert = new Alert(AlertType.INFORMATION, "");
        notificationAlert.setTitle       ("Notification Alert");
        notificationAlert.setHeaderText  ("");
        notificationAlert.setContentText (notificationMessage);
        notificationAlert.showAndWait();
    }

    public static void setMouseCursorToWait()
    {
        primaryStage.getScene().setCursor(Cursor.WAIT);
    }

    public static void setMouseCursorToDefault()
    {
        primaryStage.getScene().setCursor(Cursor.DEFAULT);
    }
}

