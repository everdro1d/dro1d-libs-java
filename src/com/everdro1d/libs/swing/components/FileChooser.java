/**************************************************************************************************
 * Copyright (c) dro1dDev 2024.                                                                   *
 **************************************************************************************************/

package com.everdro1d.libs.swing.components;

import com.everdro1d.libs.swing.SwingGUI;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;

public class FileChooser extends JFileChooser {
    private final Font font = this.getParent() != null
            ? this.getParent().getFont()
            : new Font("Tahoma", Font.PLAIN,16);

    /**
     * Create a generic file chooser dialog allowing selection of both files and directories.
     */
    public FileChooser(String path, String dialogTitle) {
        this(path, dialogTitle, true, true, false, null, false, null);
    }

    /**
     * Create a file chooser dialog for selecting files or directories.
     * @param path The directory to open the dialog in.
     * @param dialogTitle The title of the dialog.
     * @param showFiles Whether to show files in the dialog. If false, only directories will be shown.
     */
    public FileChooser(String path, String dialogTitle, boolean showFiles) {
        this(path, dialogTitle, showFiles, !showFiles, false, null, false, null);
    }

    /**
     * Create a file chooser dialog for selecting files matching a file extension.
     * @param path The directory to open the dialog in.
     * @param dialogTitle The title of the dialog.
     * @param extension The extension to filter files by. ex. "txt"
     */
    public FileChooser(String path, String dialogTitle, String extension) {
        this(path, dialogTitle, true, false, true, extension, false, null);
    }

    /**
     * Create a file chooser dialog for selecting files or directories that uses a custom description message.
     * @param path The directory to open the dialog in.
     * @param dialogTitle The title of the dialog.
     * @param showFiles Whether to show files in the dialog. If false, only directories will be shown.
     * @param customMessage Custom message to show in the description area.
     */
    public FileChooser(String path, String dialogTitle, boolean showFiles, String customMessage) {
        this(path, dialogTitle, showFiles, !showFiles, false, null, true, customMessage);
    }

    /**
     * Create a file chooser dialog.
     * @param path The directory to open the dialog in.
     * @param showFiles Whether to show files in the dialog.
     * @param showDirectories Whether to show directories in the dialog.
     * @param filterByExtension Whether to filter files by extension.
     * @param extension The extension to filter files by. Dependent on filterByExtension. ex. "txt"
     * @param dialogTitle The title of the dialog.
     * @param useCustomMessage whether to use a custom message for the description
     * @param customMessage the message to use for custom message
     */
    public FileChooser(String path ,String dialogTitle, boolean showFiles, boolean showDirectories,
                       boolean filterByExtension, String extension, boolean useCustomMessage, String customMessage
    ) {
        super();
        // Set file chooser gui properties
        setDialogTitle(dialogTitle);
        setApproveButtonText("Select");
        setPreferredSize(new Dimension(600, 450));
        setFileChooserFont(this.getComponents());
        SwingGUI.setHandCursorToClickableComponents(this);

        // Set the file chooser properties
        int selectionMode
                = showFiles ? (showDirectories ? JFileChooser.FILES_AND_DIRECTORIES : JFileChooser.FILES_ONLY)
                : JFileChooser.DIRECTORIES_ONLY;

        setFileSelectionMode(selectionMode);
        setAcceptAllFileFilterUsed(false);
        setMultiSelectionEnabled(false);
        setFileHidingEnabled(true);
        setCurrentDirectory(new File(path.isEmpty() ? System.getProperty("user.home") : path));
        this.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                if (filterByExtension) return f.getName().toLowerCase().endsWith(extension);

                if (selectionMode == JFileChooser.DIRECTORIES_ONLY) return f.isDirectory();
                if (selectionMode == JFileChooser.FILES_ONLY) return f.isFile();
                return f.isFile() || f.isDirectory();
            }

            @Override
            public String getDescription() {
                if (useCustomMessage) return customMessage;
                if (filterByExtension) return extension.toUpperCase() + " Files (*." + extension + ")";

                if (selectionMode == JFileChooser.DIRECTORIES_ONLY) return "Directory (Folder)";
                if (selectionMode == JFileChooser.FILES_ONLY) return "Any File";
                return "Any File or Directory (Folder)";
            }
        });
    }

    private void setFileChooserFont(Component[] comp) {
        for (Component component : comp) {
            if (component instanceof Container) setFileChooserFont(((Container) component).getComponents());
            try {
                component.setFont(font);
            } catch (Exception ignored) {}
        }
    }
}
