/*
 * Copyright (C) 2022 The Java Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package editor;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import game.Game;
import localization.L10n;

/**
 * The {@LevelStorage} is responsible for saving a level
 *
 * @author olegshchepilov
 *
 */

public class LevelStorage {

    static public void save(Component dialogParent, Game game) throws IOException {
        final String issue = validateGame(game);
        if ((issue != null) && !issue.isEmpty()) {
            final String text = L10n.get(L10n.Id.DIALOG_GAME_VALIDATION_TEXT, issue);
            if (JOptionPane.showConfirmDialog(dialogParent, text, L10n.get(L10n.Id.DIALOG_OVERWRITE_CONFIRM_TITLE),
                    JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) {
                return;
            }
        }

        final File file = getSelectedFile(dialogParent);
        if (file == null) {
            return;
        }

        final Path filePath = getApprovedPath(dialogParent, file);
        if (filePath == null) {
            return;
        }

        final byte[] data = game.getBinaryData();
        Files.write(filePath, data, StandardOpenOption.CREATE_NEW);
    }

    static public Game loadFromFile(Component dialogParent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(
                new FileNameExtensionFilter(L10n.get(L10n.Id.WISE_MOLE_LEVEL_FILE_DESCRIPTION), LEVEL_FILE_EXTENSION));
        fileChooser.setAcceptAllFileFilterUsed(false);
        if (fileChooser.showOpenDialog(dialogParent) != JFileChooser.APPROVE_OPTION) {
            return null;
        }

        final File file = fileChooser.getSelectedFile();
        if (file == null) {
            return null;
        }

        try {
            List<String> lines = Files.readAllLines(Paths.get(file.getAbsolutePath()));
            if (lines == null) {
                return null;
            }
            Game game = new Game(file.getName());
            if (game.loadGame(lines)) {
                return game;
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public static boolean hasPredefined(int index) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return classLoader.getResource(getPredefinedResourceId(index)) != null;
    }

    public static int predefinedLevelCount() {
        if (levelCount == null) {
            levelCount = 0;
            for (int i = 1; hasPredefined(i); ++i) {
                levelCount = i;
            }
        }
        return levelCount;
    }

    static public Game loadPredefined(int index) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream input = classLoader.getResourceAsStream(getPredefinedResourceId(index))) {
            if (input == null) {
                return null;
            }
            List<String> lines = new ArrayList<String>();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
                Game game = new Game(Integer.toString(index));
                if (game.loadGame(lines)) {
                    return game;
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        return null;
    }

    static private String validateGame(Game game) {
        if (game.getMolePosition() == null) {
            return L10n.get(L10n.Id.ERROR_THERE_IS_NO_MOLE);
        }
        if (game.getBoxes().size() < game.getTargetPoints().size()) {
            return L10n.get(L10n.Id.ERROR_TARGET_POINTS_MORE_THAN_BOXES);
        }

        return null;
    }

    static private File getSelectedFile(Component dialogParent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Wise Mole file", LEVEL_FILE_EXTENSION));
        fileChooser.setAcceptAllFileFilterUsed(false);
        if (fileChooser.showSaveDialog(dialogParent) != JFileChooser.APPROVE_OPTION) {
            return null;
        }

        return fileChooser.getSelectedFile();
    }

    static private Path getApprovedPath(Component parentComponent, File file) throws IOException {
        String absolutePath = file.getAbsolutePath();
        if (!absolutePath.endsWith("." + LEVEL_FILE_EXTENSION)) {
            absolutePath += "." + LEVEL_FILE_EXTENSION;
        }
        final Path filePath = Paths.get(absolutePath);
        if (Files.exists(filePath)) {
            if (JOptionPane.showConfirmDialog(parentComponent, L10n.get(L10n.Id.DIALOG_OVERWRITE_CONFIRM_TEXT),
                    L10n.get(L10n.Id.DIALOG_OVERWRITE_CONFIRM_TITLE),
                    JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) {
                return null;
            }
            Files.delete(filePath);
        }
        return filePath;
    }

    private static String getPredefinedResourceId(Integer index) {
        return "game/level" + index.toString() + "." + LEVEL_FILE_EXTENSION;
    }

    static private Integer levelCount;

    final static private String LEVEL_FILE_EXTENSION = "wmgame";
}
