package TypingPracticeAndTest;

import javafx.fxml.FXML;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import java.util.regex.Pattern;
import javafx.scene.paint.Color;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.application.Platform;
import javafx.scene.text.FontWeight;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

import javafx.scene.Node;
import javafx.scene.text.TextFlow;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.ScrollPane;

import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Timer;
import java.util.TimerTask;
import java.util.ArrayList;
import javafx.util.Duration;
import javafx.concurrent.Task;
import javafx.application.Platform;
import javafx.animation.PauseTransition;

/* Copyright 2021 Melwyn Francis Carlo */

public class Controller
{
    /* Public class variables */
        public static enum TypingMode
        {
            PRACTICE, 
            TEST 
        }

        public static int windowNumber = 0;

        public static final double    SMALL_WAIT_TIME_IN_SECONDS = 0.05;
        public static final double   NORMAL_WAIT_TIME_IN_SECONDS = 0.25;
        public static final double SPLASH_SCREEN_TIME_IN_SECONDS = 2.00;


    /* Private class variables */
        private static final Color    ERROR_CHARACTER_FONT_COLOR = Color.RED;
        private static final Color    TYPED_CHARACTER_FONT_COLOR = Color.BLACK;
        private static final Color  UNTYPED_CHARACTER_FONT_COLOR = Color.GRAY;
        private static final Color SELECTED_CHARACTER_FONT_COLOR = Color.GREEN;

        private static final FontWeight    ERROR_CHARACTER_FONT_WEIGHT = FontWeight.BOLD;
        private static final FontWeight    TYPED_CHARACTER_FONT_WEIGHT = FontWeight.NORMAL;
        private static final FontWeight  UNTYPED_CHARACTER_FONT_WEIGHT = FontWeight.NORMAL;
        private static final FontWeight SELECTED_CHARACTER_FONT_WEIGHT = FontWeight.BOLD;

        private static final String       TEXTFLOW_FONT_FAMILY = "Monospace";
        private static final String SPACE_CHARACTER_SUBSTITUTE = "█";

        private static final int                  WPM_MAX_CAP = 200;
        private static final int           TEXTFLOW_FONT_SIZE = 20;
        private static final int        NET_SCORES_LIST_MAX_N = 100;
        private static final int        PARAGRAPH_LINES_MAX_N = 12;
        private static final int     PARAGRAPH_LINE_MAX_WIDTH = 42;
        private static final double  INCREMENTAL_SCROLL_VALUE = 0.01;
        private static final long ONE_SECOND_IN_MILLI_SECONDS = 1000L;

        private static final double  PARAGRAPH_VIEWPORT_MIN_Y_POS = 40;
        private static final double  PARAGRAPH_VIEWPORT_MAX_Y_POS = 310;

        private static final double  PARAGRAPH_VIEWPORT_MIN_Y_POS_TOLERANCE = 10;

        private static Timer testTimer = null;

        private static TypingMode typingMode;

        private static int[] testTimerCounter = new int[3];

        private static int     cursorIndex;
        private static String  paragraphData;
        private static String  timerLabelData;
        private static boolean testHasBegun;
        private static boolean testHasFinished;
        private static boolean sessionHasBegun;
        private static boolean lastCharacterIncorrect;
        private static int     numberOfIncorrectCharacters;
        private static int     numberOfParagraphCharacters;

        private ArrayList<Text> paragraphCharacters = new ArrayList<Text>();


    /* Private FXML variables */
        @FXML
        private Label timerLabel;

        @FXML
        private TextArea inputTextArea;

        @FXML
        private ScrollPane typingWindowScrollPane;

        @FXML
        private TextFlow typingWindowTextflow;

        @FXML
        private TableView<Model.ScoresData> scoresDataTable;

        @FXML
        private TableColumn<Model.ScoresData, String> column1;

        @FXML
        private TableColumn<Model.ScoresData, String> column2;

        @FXML
        private TableColumn<Model.ScoresData, String> column3;


    /* Private class functions */
        private void incrementTestTimerCount()
        {
            testTimerCounter[2]++;

            if (testTimerCounter[2] > 59)
            {
                testTimerCounter[1]++;
                testTimerCounter[2] = 0;
            }

            if (testTimerCounter[1] > 59)
            {
                testTimerCounter[0]++;
                testTimerCounter[1] = 0;
            }

            Platform.runLater(() ->
            {
                timerLabel.setText( String.format("%02d", testTimerCounter[0]) + " : " + 
                                    String.format("%02d", testTimerCounter[1]) + " : " + 
                                    String.format("%02d", testTimerCounter[2]));
            });
        }

        private void forceFocus(Node node)
        {
            PauseTransition waitTimedTask = new PauseTransition(Duration.seconds(SMALL_WAIT_TIME_IN_SECONDS));

            waitTimedTask.setOnFinished(event -> 
            {
                if (!node.isFocused())
                    {
                        node.requestFocus();
                        forceFocus(node);
                    }
            });

            waitTimedTask.play();
        }


    /* Public class functions */
        public static void launchTheApp(String args[])
        {
            View.launchSplashScreen(args);
        }

        public static String getParagraphFromTempFile()
        {
            return Model.getFileContents(Model.TEMP_DIR_PATH + Model.INPUT_PARAGRAPH_TEMP_FILE);
        }

        public static void saveParagraphInTempFile(String paragraph)
        {
            Model.setFileContents(Model.TEMP_DIR_PATH + Model.INPUT_PARAGRAPH_TEMP_FILE, paragraph, true);
        }

        public void removeTypingWindowEventFilters()
        {
            typingWindowScrollPane.removeEventFilter(KeyEvent.KEY_PRESSED, keyPressedHandler);
        }

        public static void dataDirectoryCheck()
        {
            Model.checkOrCreateDataDirectory();
        }


    /* Private FXML functions */
        @FXML
        private void enterRandomParagraph(ActionEvent event)
        {
            inputTextArea.setText(Model.getRandomParagraph());

            forceFocus(inputTextArea);
        }

        @FXML
        private void restoreTypingWindowFocus(MouseEvent event)
        {
            forceFocus(typingWindowScrollPane);
        }

        @FXML
        private void startTypingPractice(ActionEvent event)
        {
            event.consume();

            View.setMouseCursorToWait();

            Task<Boolean> paragraphValidationTask = new Task<Boolean>() {
                @Override
                public Boolean call()
                {
                    return Model.validateParagraph(inputTextArea.getText());
                }
            };
            paragraphValidationTask.setOnSucceeded(e ->
            {
                if (paragraphValidationTask.getValue() == true)
                {
                    saveParagraphInTempFile(inputTextArea.getText());

                    testHasBegun    = true;
                    testHasFinished = false;
                    sessionHasBegun = false;

                    typingMode = TypingMode.PRACTICE;

                    View.launchTypingWindow(typingMode);
                }
                else
                {
                    View.setMouseCursorToDefault();

                    View.showNotificationAlert(Model.MINIMUM_INPUT_PARAGRAPH_TEXT_LENGTH_MESSAGE);
                }
            });
            new Thread(paragraphValidationTask).start();
        }

        @FXML
        private void startTypingTest(ActionEvent event)
        {
            event.consume();

            if (Model.validateParagraph(inputTextArea.getText()))
            {
                saveParagraphInTempFile(inputTextArea.getText());

                testHasBegun    = false;
                testHasFinished = false;
                sessionHasBegun = false;

                typingMode = TypingMode.TEST;

                View.launchTypingWindow(typingMode);
            }
            else
            {
                View.showNotificationAlert(Model.MINIMUM_INPUT_PARAGRAPH_TEXT_LENGTH_MESSAGE);
            }
        }

        @FXML
        private void goBack(ActionEvent event)
        {
            event.consume();

            if (testTimer != null)
            {
                testTimer.cancel();
                testTimer.purge();

                testTimer = null;
            }

            typingWindowScrollPane.removeEventFilter(KeyEvent.KEY_PRESSED, keyPressedHandler);

            View.launchTextInputWindow(NORMAL_WAIT_TIME_IN_SECONDS);
        }

        @FXML
        private void exitResultsList(ActionEvent event)
        {
            event.consume();

            View.launchTextInputWindow(NORMAL_WAIT_TIME_IN_SECONDS);
        }


    /* Public FXML functions */
        @FXML
        public void initialize()
        {
            if (windowNumber == 1)
            {
                inputTextArea.setText(Controller.getParagraphFromTempFile());

                forceFocus(inputTextArea);
            }
            else if (windowNumber == 2)
            {
                Task<Void> paragraphRenderingTask = new Task<Void>() {
                    @Override
                    public Void call()
                    {
                        testTimerCounter[0] = 0;
                        testTimerCounter[1] = 0;
                        testTimerCounter[2] = 0;

                        cursorIndex = 0;

                        int paragraphDataTotalLines = 0;

                        String[] paragraphWordsDataArray = getParagraphFromTempFile().trim()
                                                                                     .replaceAll("\n", " ")
                                                                                     .replaceAll("  ", " ")
                                                                                     .split(" ");

                        paragraphData = paragraphWordsDataArray[0];

                        int occupiedWidthOnLine = paragraphWordsDataArray[0].length();

                        for (int i = 1; i < paragraphWordsDataArray.length; i++)
                        {
                            /* 
                                Test to see if all the characters in the word, including 
                                a preceding space and a following space, can fit within 
                                the available slot on the given line. If they cannot, 
                                then add a space character, followed by a new line 
                                character, then discard the next word's preceding 
                                space character, and then add the word to the 
                                beginning of the new line, and continue. 
                            */
                            if ((paragraphWordsDataArray[i].length() + 2) < (PARAGRAPH_LINE_MAX_WIDTH - occupiedWidthOnLine))
                            {
                                paragraphData += " " + paragraphWordsDataArray[i];

                                occupiedWidthOnLine += paragraphWordsDataArray[i].length() + 1;
                            }
                            else
                            {
                                paragraphDataTotalLines++;

                                paragraphData += " \n" + paragraphWordsDataArray[i];

                                occupiedWidthOnLine = paragraphWordsDataArray[i].length();
                            }
                        }

                        paragraphDataTotalLines++;

                        lastCharacterIncorrect = false;

                        numberOfIncorrectCharacters = 0;
                        numberOfParagraphCharacters = paragraphData.length();

                        paragraphCharacters.clear();

                        Platform.runLater(() ->
                        {
                            typingWindowTextflow.getChildren().clear();
                        });

                        for (int i = 0; i < numberOfParagraphCharacters; i++)
                        {
                            Text temp_text_object = new Text(paragraphData.substring(i, i + 1));

                            if (i == 0)
                            {
                                temp_text_object.setFont(Font.font(TEXTFLOW_FONT_FAMILY, SELECTED_CHARACTER_FONT_WEIGHT, TEXTFLOW_FONT_SIZE));
                                temp_text_object.setFill(SELECTED_CHARACTER_FONT_COLOR);
                            }
                            else
                            {
                                temp_text_object.setFont(Font.font(TEXTFLOW_FONT_FAMILY, UNTYPED_CHARACTER_FONT_WEIGHT, TEXTFLOW_FONT_SIZE));
                                temp_text_object.setFill(UNTYPED_CHARACTER_FONT_COLOR);
                            }

                            paragraphCharacters.add(temp_text_object);

                            temp_text_object = null;

                            /* Because the runLater function allows final variables only. */
                            final int index = i;

                            Platform.runLater(() ->
                            {
                                typingWindowTextflow.getChildren().add(paragraphCharacters.get(index));
                            });

                            final double dummyValue = paragraphCharacters.get(i).getBoundsInLocal().getMaxY();
                            final double textYPos   = paragraphCharacters.get(i).getBoundsInParent().getMaxY();

                            paragraphCharacters.get(i).setY(textYPos);
                        }

                        int i_max = PARAGRAPH_LINES_MAX_N - (paragraphDataTotalLines % PARAGRAPH_LINES_MAX_N);

                        if (i_max == PARAGRAPH_LINES_MAX_N)
                        {
                            i_max = 0;
                        }

                        i_max += 10;

                        for (int i = 0; i < i_max; i++)
                        {
                            Platform.runLater(() ->
                            {
                                typingWindowTextflow.getChildren().add(new Text("\n "));
                            });
                        }

                        forceFocus(typingWindowScrollPane);

                        typingWindowScrollPane.addEventFilter(KeyEvent.KEY_PRESSED, keyPressedHandler);

                        typingWindowScrollPane.addEventFilter(ScrollEvent.ANY, scrolledHandler);

                        return null;
                    }
                };
                paragraphRenderingTask.setOnSucceeded(e ->
                {
                    View.setMouseCursorToDefault();

                    sessionHasBegun = true;
                });
                new Thread(paragraphRenderingTask).start();
            }
            else if (windowNumber == 3)
            {
                final double testTimeInMinutes = ((double) testTimerCounter[0] * 60.0) 
                                               +  (double) testTimerCounter[1] 
                                               + ((double) testTimerCounter[2] / 60.0);

                final String highestNetScore_Str = Model.getFileContents(Model.HIGHEST_NET_SCORE_FILE);

                final String[] tempArray = Model.getFileContents(Model.HIGHEST_NET_SCORE_FILE).split(Model.STRING_LIST_DELIMITER);

                int netScoresListN = tempArray.length;   

                int listOfNetScores_Str_Len = tempArray.length + 1;

                if (listOfNetScores_Str_Len > NET_SCORES_LIST_MAX_N)
                {
                    listOfNetScores_Str_Len = NET_SCORES_LIST_MAX_N;
                }

                String[] listOfNetScores_Str = new String[listOfNetScores_Str_Len];

                System.arraycopy(tempArray, 0, listOfNetScores_Str, 0, netScoresListN);

                final int currentAccuracy = (int) ((100.0 * ((double) numberOfParagraphCharacters - (double) numberOfIncorrectCharacters)) 
                                                            /  (double) numberOfParagraphCharacters);

                int currentGrossScore = (int) ((double) numberOfParagraphCharacters / (5.0 * testTimeInMinutes));

                if (currentGrossScore > WPM_MAX_CAP)
                {
                    currentGrossScore = WPM_MAX_CAP;
                }

                int currentNetScore = (int) ((double) currentGrossScore - ((double) numberOfIncorrectCharacters / testTimeInMinutes));

                if (currentNetScore < 0)
                {
                    currentNetScore = 0;
                }

                int highestNetScore;

                if (highestNetScore_Str.equals(""))
                {
                    highestNetScore = 0;
                }
                else
                {
                    highestNetScore = Integer.parseInt(highestNetScore_Str);
                }

                if (currentNetScore > highestNetScore)
                {
                    highestNetScore = currentNetScore;
                }

                if (!listOfNetScores_Str[0].equals(""))
                {
                    int shiftElements_N;

                    if (netScoresListN == NET_SCORES_LIST_MAX_N)
                    {
                        shiftElements_N = NET_SCORES_LIST_MAX_N - 1;
                    }
                    else
                    {
                        shiftElements_N = netScoresListN;
                    }
                    System.arraycopy(listOfNetScores_Str, 0, listOfNetScores_Str, 1, shiftElements_N);
                }
                else
                {
                    netScoresListN = 0;
                }
                listOfNetScores_Str[0] = String.valueOf(currentNetScore);

                int netScoresListSum = 0;

                netScoresListN++;

                if (netScoresListN > NET_SCORES_LIST_MAX_N)
                {
                    netScoresListN = NET_SCORES_LIST_MAX_N;
                }

                for (int i = 0; i < netScoresListN; i++)
                {
                    netScoresListSum += Integer.parseInt(listOfNetScores_Str[i]);
                }
                final int netScoresListAverage = netScoresListSum / netScoresListN;

                Model.setFileContents(Model.HIGHEST_NET_SCORE_FILE,  String.valueOf(highestNetScore), true);
                Model.setFileContents(Model.LIST_OF_NET_SCORES_FILE, String.join(Model.STRING_LIST_DELIMITER, listOfNetScores_Str), true);

                column1.setCellValueFactory(new PropertyValueFactory<Model.ScoresData, String>("title"));
                column2.setCellValueFactory(new PropertyValueFactory<Model.ScoresData, String>("separator"));
                column3.setCellValueFactory(new PropertyValueFactory<Model.ScoresData, String>("value"));

                scoresDataTable.getItems().add(new Model.ScoresData(" ", "", ""));
                scoresDataTable.getItems().add(new Model.ScoresData(" Current Net Score",   ":", String.valueOf(currentNetScore)));
                scoresDataTable.getItems().add(new Model.ScoresData(" Highest Net Score",   ":", String.valueOf(highestNetScore)));
                scoresDataTable.getItems().add(new Model.ScoresData(" Average Net Score",   ":", String.valueOf(netScoresListAverage)));
                scoresDataTable.getItems().add(new Model.ScoresData(" ", "", ""));
                scoresDataTable.getItems().add(new Model.ScoresData(" Current Gross Score", ":", String.valueOf(currentGrossScore)));
                scoresDataTable.getItems().add(new Model.ScoresData(" ", "", ""));
                scoresDataTable.getItems().add(new Model.ScoresData(" Current Accuracy",    ":", String.valueOf(currentAccuracy) + " %"));
                scoresDataTable.getItems().add(new Model.ScoresData(" ", "", ""));
                scoresDataTable.getItems().add(new Model.ScoresData(" Current Time Taken",  ":", timerLabelData));

                scoresDataTable.setSelectionModel(null);
            }
        }


    /* Event handlers */
        EventHandler<KeyEvent> keyPressedHandler = new EventHandler<KeyEvent>()
        {
            @Override
            public void handle(KeyEvent event)
            {
                event.consume();

                if (sessionHasBegun && !testHasFinished)
                {
                    KeyCode keyPressedCode = event.getCode();

                    if ((keyPressedCode.isLetterKey() 
                    ||   keyPressedCode.isDigitKey() 
                    ||   keyPressedCode == KeyCode.SPACE 
                    ||   Pattern.matches("\\p{Punct}", event.getText())) 
                    &&   !event.isControlDown() 
                    &&   !event.isMetaDown() 
                    &&   !event.isAltDown())
                    {
                        paragraphCharacters.get(cursorIndex).setText(event.getText());

                        if (event.getText().equals(paragraphData.substring(cursorIndex, cursorIndex + 1)))
                        {
                            lastCharacterIncorrect = false;

                            paragraphCharacters.get(cursorIndex).setFont(Font.font(TEXTFLOW_FONT_FAMILY, TYPED_CHARACTER_FONT_WEIGHT, TEXTFLOW_FONT_SIZE));
                            paragraphCharacters.get(cursorIndex).setFill(TYPED_CHARACTER_FONT_COLOR);
                            paragraphCharacters.get(cursorIndex).setUnderline(false);
                        }
                        else
                        {
                            numberOfIncorrectCharacters++;

                            if ((cursorIndex + 1) == numberOfParagraphCharacters)
                            {
                                if (lastCharacterIncorrect)
                                {
                                    numberOfIncorrectCharacters--;
                                }
                                else
                                {
                                    lastCharacterIncorrect = true;
                                }
                            }

                            if (event.getText().equals(" "))
                            {
                                paragraphCharacters.get(cursorIndex).setText(SPACE_CHARACTER_SUBSTITUTE);
                            }

                            paragraphCharacters.get(cursorIndex).setFont(Font.font(TEXTFLOW_FONT_FAMILY, ERROR_CHARACTER_FONT_WEIGHT, TEXTFLOW_FONT_SIZE));
                            paragraphCharacters.get(cursorIndex).setFill(ERROR_CHARACTER_FONT_COLOR);
                            paragraphCharacters.get(cursorIndex).setUnderline(true);
                        }

                        cursorIndex++;

                        if (cursorIndex != numberOfParagraphCharacters)
                        {
                            if (paragraphCharacters.get(cursorIndex).getText().equals("\n"))
                            {
                                final double currentLineTextYPos = paragraphCharacters.get(cursorIndex + 1)
                                                                                      .localToScene(paragraphCharacters.get(cursorIndex + 1)
                                                                                                                       .getBoundsInLocal()
                                                                                                   ).getMinY();

                                if (currentLineTextYPos > PARAGRAPH_VIEWPORT_MAX_Y_POS)
                                {
                                    while (true)
                                    {
                                        final double vValue = typingWindowScrollPane.getVvalue();

                                        final double nextLineTextYPos = paragraphCharacters.get(cursorIndex + 1)
                                                                                           .localToScene(paragraphCharacters.get(cursorIndex + 1)
                                                                                                                            .getBoundsInLocal()
                                                                                                        ).getMaxY();

                                        if ((vValue < 1.0) && (nextLineTextYPos > PARAGRAPH_VIEWPORT_MIN_Y_POS))
                                        {
                                            typingWindowScrollPane.setVvalue(vValue + INCREMENTAL_SCROLL_VALUE);
                                        }
                                        else
                                        {
                                            break;
                                        }
                                    }
                                }

                                cursorIndex++;
                            }

                            if (paragraphCharacters.get(cursorIndex).getText().equals(" "))
                            {
                                paragraphCharacters.get(cursorIndex).setText(SPACE_CHARACTER_SUBSTITUTE);
                            }

                            paragraphCharacters.get(cursorIndex).setFont(Font.font(TEXTFLOW_FONT_FAMILY, SELECTED_CHARACTER_FONT_WEIGHT, TEXTFLOW_FONT_SIZE));
                            paragraphCharacters.get(cursorIndex).setFill(SELECTED_CHARACTER_FONT_COLOR);
                            paragraphCharacters.get(cursorIndex).setUnderline(false);
                        }
                        else
                        {
                            if (typingMode == TypingMode.TEST)
                            {
                                testTimer.cancel();
                                testTimer.purge();

                                testHasFinished = true;

                                timerLabelData = timerLabel.getText();

                                View.launchTestEndWindow();
                            }
                            else /* if (typingMode == TypingMode.PRACTICE) */
                            {
                                cursorIndex--;
                            }
                        }

                        if (!testHasBegun)
                        {
                            testHasBegun = true;

                            testTimer = new Timer();

                            testTimer.scheduleAtFixedRate(new TimerTask()
                            {
                                @Override
                                public void run()
                                {
                                    incrementTestTimerCount();
                                }

                            }, ONE_SECOND_IN_MILLI_SECONDS, ONE_SECOND_IN_MILLI_SECONDS);
                        }
                    }
                    else if (event.getCode() == KeyCode.BACK_SPACE)
                    {
                        if (cursorIndex > 0)
                        {
                            lastCharacterIncorrect = false;

                            boolean canGoBack = true;

                            if (cursorIndex == (numberOfParagraphCharacters - 1) 
                            &&  paragraphCharacters.get(cursorIndex).getFill() != SELECTED_CHARACTER_FONT_COLOR)
                            {
                                canGoBack = false;
                            }

                            if (canGoBack)
                            {
                                paragraphCharacters.get(cursorIndex).setFont(Font.font(TEXTFLOW_FONT_FAMILY, UNTYPED_CHARACTER_FONT_WEIGHT, TEXTFLOW_FONT_SIZE));
                                paragraphCharacters.get(cursorIndex).setFill(UNTYPED_CHARACTER_FONT_COLOR);
                                paragraphCharacters.get(cursorIndex).setText(paragraphData.substring(cursorIndex, cursorIndex + 1));
                                paragraphCharacters.get(cursorIndex).setUnderline(false);

                                cursorIndex--;

                                if (paragraphCharacters.get(cursorIndex).getText().equals("\n"))
                                {
                                    final double previousLineTextOldYPos = paragraphCharacters.get(cursorIndex - 1)
                                                                                              .localToScene(paragraphCharacters.get(cursorIndex - 1)
                                                                                                                               .getBoundsInLocal()
                                                                                                           ).getMaxY();

                                    if (previousLineTextOldYPos < (PARAGRAPH_VIEWPORT_MIN_Y_POS - PARAGRAPH_VIEWPORT_MIN_Y_POS_TOLERANCE))
                                    {
                                        while (true)
                                        {
                                            final double vValue = typingWindowScrollPane.getVvalue();

                                            final double previousLineTextNewYPos = paragraphCharacters.get(cursorIndex - 1)
                                                                                                      .localToScene(paragraphCharacters.get(cursorIndex - 1)
                                                                                                                                       .getBoundsInLocal()
                                                                                                                   ).getMaxY();

                                            if ((vValue > 0.0) && (previousLineTextNewYPos < PARAGRAPH_VIEWPORT_MAX_Y_POS))
                                            {
                                                typingWindowScrollPane.setVvalue(vValue - INCREMENTAL_SCROLL_VALUE);
                                            }
                                            else
                                            {
                                                break;
                                            }
                                        }
                                    }

                                    cursorIndex--;
                                }
                            }

                            if (paragraphCharacters.get(cursorIndex).getFill() == ERROR_CHARACTER_FONT_COLOR)
                            {
                                numberOfIncorrectCharacters--;
                            }

                            paragraphCharacters.get(cursorIndex).setFont(Font.font(TEXTFLOW_FONT_FAMILY, SELECTED_CHARACTER_FONT_WEIGHT, TEXTFLOW_FONT_SIZE));
                            paragraphCharacters.get(cursorIndex).setFill(SELECTED_CHARACTER_FONT_COLOR);
                            paragraphCharacters.get(cursorIndex).setText(paragraphData.substring(cursorIndex, cursorIndex + 1));
                            paragraphCharacters.get(cursorIndex).setUnderline(false);

                            if (paragraphCharacters.get(cursorIndex).getText().equals(" "))
                            {
                                paragraphCharacters.get(cursorIndex).setText(SPACE_CHARACTER_SUBSTITUTE);
                            }
                        }
                    }
                }
            }
        };

        EventHandler<ScrollEvent> scrolledHandler = new EventHandler<ScrollEvent>()
        {
            @Override
            public void handle(ScrollEvent event)
            {
                event.consume();
            }
        };
}

