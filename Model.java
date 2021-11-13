package TypingPracticeAndTest;

import java.io.File;
import java.util.Random;
import java.nio.file.Path;
import java.io.FileWriter;
import java.io.InputStream;
import java.nio.file.Paths;
import java.nio.file.Files;

/* Copyright 2021 Melwyn Francis Carlo */

public class Model
{
    /* Private class variables */
        private static Random random_generator_object = new Random();

        /* private static String APP_DIR_PATH = System.getProperty("filepath"); */

        private static String APP_DIR_PATH = "TypingPracticeAndTest";

        private static String APP_NAME = "TypingPracticeAndTest";

        private static final String APP_DATA_DIR = APP_DIR_PATH + File.separator 
                                                 + "data"       + File.separator;

        private static final int MINIMUM_INPUT_PARAGRAPH_TEXT_LENGTH = 100;


    /* Public class variables */
        public static final int NUMBER_OF_READYMADE_RANDOM_PARAGRAPHS = 283;

        public static final String FILE_SEPARATOR = File.separator;

        public static final String STRING_LIST_DELIMITER = ";";

        public static final String TEMP_DIR_PATH = APP_DATA_DIR + "tmp" + FILE_SEPARATOR;

        public static final String INPUT_PARAGRAPH_TEMP_FILE = "inputText.tmp";

        public static final String HIGHEST_NET_SCORE_FILE  = APP_DATA_DIR + "highestNetScore";
        public static final String LIST_OF_NET_SCORES_FILE = APP_DATA_DIR + "listOfNetScores";

        public static final String MINIMUM_INPUT_PARAGRAPH_TEXT_LENGTH_MESSAGE = 
        "The input paragraph text must have a minimum length of " + 
         MINIMUM_INPUT_PARAGRAPH_TEXT_LENGTH + " characters.";


    /* Private class functions */
        private static void checkOrCreateDir(String dirPathString)
        {
            try
            {
                if (!Files.isDirectory(Paths.get(dirPathString)))
                {
                    Files.createDirectory(Paths.get(dirPathString));
                }
            }
            catch (Exception exception_object)
            {
                System.out.println(exception_object.getMessage());
                System.exit(1);
            }
        }


    /* Public class functions */
        public static void checkOrCreateDataDirectory()
        {
            checkOrCreateDir(APP_DATA_DIR);
            checkOrCreateDir(TEMP_DIR_PATH);
        }

        public static String getRandomParagraph()
        {
            final int randomNumber = random_generator_object.nextInt(NUMBER_OF_READYMADE_RANDOM_PARAGRAPHS) + 1;

            String paragraphString = "";

            try
            {
               final InputStream paragraphInputStream = Model.class.getResourceAsStream("data/paragraphs/" + String.valueOf(randomNumber));

               while (true)
               {
                  final int paragraphCharacterCode = paragraphInputStream.read();

                  if (paragraphCharacterCode == -1)
                  {
                     break;
                  }

                  paragraphString += (char) paragraphCharacterCode;
               }

               paragraphInputStream.close();
            }
            catch (Exception exception_object)
            {
                System.out.println(exception_object.getMessage());
                System.exit(1);
            }

            return paragraphString;
        }

        public static boolean validateParagraph(String paragraph)
        {
            final int MINIMUM_INPUT_PARAGRAPH_TEXT_LENGTH = 10;

            if (paragraph.trim().length() < MINIMUM_INPUT_PARAGRAPH_TEXT_LENGTH)
            {
                return false;
            }

            return true;
        }

        public static void setFileContents(String filePathString, String fileContents, boolean mayCreateFile)
        {
            try
            {
                final File outFile = new File(filePathString);

                if (!outFile.exists())
                {
                    if (mayCreateFile)
                    {
                        if (!outFile.createNewFile())
                        {
                            System.out.println("Error: Cannot create file '" + filePathString + "'.");
                            System.exit(1);
                        }
                    }
                    else
                    {
                        System.out.println("Error: File '" + filePathString + "' does not exist.");
                        System.exit(1);
                    }
                }

                final FileWriter writer = new FileWriter(outFile);

                writer.write(fileContents);
                writer.close();
            }
            catch (Exception exception_object)
            {
                System.out.println(exception_object.getMessage());
                System.exit(1);
            }
        }

        public static String getFileContents(String filePathString)
        {
            String fileContents = "";

            try
            {
                final Path filePath = Paths.get(filePathString);

                fileContents = new String(Files.readAllBytes(filePath.toAbsolutePath()));
            }
            catch (Exception exception_object)
            {
                fileContents = "";
            }
            finally
            {
                return fileContents;
            }
        }


    /* Auxiliary (inner nested) public classes */
        public static class ScoresData
        {
            private String title = null;
            private String value = null;
            private String separator = null;
         
            public ScoresData(String title, String separator, String value)
            {
                this.title = title;
                this.value = value;
                this.separator = separator;
            }

            public String getTitle()
            {
                return title;
            }

            public String getValue()
            {
                return value;
            }

            public String getSeparator()
            {
                return separator;
            }
        }
}

