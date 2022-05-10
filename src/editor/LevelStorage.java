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
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.MessageFormat;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import common.Lang;
import game.Game;

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
            final String textTemplate = Lang.get(Lang.Res.DIALOG_GAME_VALIDATION_TEXT);
            final String text = MessageFormat.format(textTemplate, issue);
            if (JOptionPane.showConfirmDialog(dialogParent, text, Lang.get(Lang.Res.DIALOG_OVERWRITE_CONFIRM_TITLE),
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

    static public Game load(Component dialogParent) throws IOException {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Wise Mole file", gameExtension));
        fileChooser.setAcceptAllFileFilterUsed(false);
        if (fileChooser.showOpenDialog(dialogParent) != JFileChooser.APPROVE_OPTION) {
            return null;
        }

        final File file = fileChooser.getSelectedFile();
        if (file == null) {
            return null;
        }

        List<String> lines = Files.readAllLines(Paths.get(file.getAbsolutePath()));
        if (lines == null) {
            return null;
        }
        Game game = new Game();
        final boolean success = game.loadGame(lines);
        return success ? game : null;
    }

    static private String validateGame(Game game) {
        if (game.getMolePosition() == null) {
            return Lang.get(Lang.Res.ERROR_THERE_IS_NO_MOLE);
        }
        if (game.getBoxes().size() < game.getTargetPoints().size()) {
            return Lang.get(Lang.Res.ERROR_TARGET_POINTS_MORE_THAN_BOXES);
        }

        return null;
    }

    static private File getSelectedFile(Component dialogParent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Wise Mole file", gameExtension));
        fileChooser.setAcceptAllFileFilterUsed(false);
        if (fileChooser.showSaveDialog(dialogParent) != JFileChooser.APPROVE_OPTION) {
            return null;
        }

        return fileChooser.getSelectedFile();
    }

    static private Path getApprovedPath(Component parentComponent, File file) throws IOException {
        String absolutePath = file.getAbsolutePath();
        if (!absolutePath.endsWith("." + gameExtension)) {
            absolutePath += "." + gameExtension;
        }
        final Path filePath = Paths.get(absolutePath);
        if (Files.exists(filePath)) {
            if (JOptionPane.showConfirmDialog(parentComponent, Lang.get(Lang.Res.DIALOG_OVERWRITE_CONFIRM_TEXT),
                    Lang.get(Lang.Res.DIALOG_OVERWRITE_CONFIRM_TITLE),
                    JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) {
                return null;
            }
            Files.delete(filePath);
        }
        return filePath;
    }

    final static private String gameExtension = "wmgame";
}
