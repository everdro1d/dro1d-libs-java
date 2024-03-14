/**************************************************************************************************
 * Copyright (c) dro1dDev 2024.                                                                   *
 **************************************************************************************************/

package com.everdro1d.libs.swing.components;

import com.everdro1d.libs.core.LocaleManager;
import com.everdro1d.libs.swing.SwingGUI;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;

public class FileChooser extends JFileChooser {
    // UI text
    private String selectButtonText = "Select";
    private String cancelButtonText = "Cancel";
    private final Font font = this.getParent() != null
            ? this.getParent().getFont()
            : new Font("Tahoma", Font.PLAIN,16);

    /**
     * Create a generic file chooser dialog allowing selection of both files and directories.
     */
    public FileChooser(String path, String dialogTitle, LocaleManager localeManager) {
        this(path, dialogTitle, true, true, false, null, false, null, localeManager);
    }

    /**
     * Create a file chooser dialog for selecting files or directories.
     * @param path The directory to open the dialog in.
     * @param dialogTitle The title of the dialog.
     * @param selectFiles Whether to show files in the dialog. If false, only directories will be shown.
     */
    public FileChooser(String path, String dialogTitle, boolean selectFiles, LocaleManager localeManager) {
        this(path, dialogTitle, selectFiles, !selectFiles, false, null, false, null, localeManager);
    }

    /**
     * Create a file chooser dialog for selecting files matching a file extension.
     * @param path The directory to open the dialog in.
     * @param dialogTitle The title of the dialog.
     * @param extension The extension to filter files by. ex. "txt"
     */
    public FileChooser(String path, String dialogTitle, String extension, LocaleManager localeManager) {
        this(path, dialogTitle, true, false, true, extension, false, null, localeManager);
    }

    /**
     * Create a file chooser dialog for selecting files or directories that uses a custom description message.
     * @param path The directory to open the dialog in.
     * @param dialogTitle The title of the dialog.
     * @param selectFiles Whether to show files in the dialog. If false, only directories will be shown.
     * @param customMessage Custom message to show in the description area.
     */
    public FileChooser(String path, String dialogTitle, boolean selectFiles, String customMessage, LocaleManager localeManager) {
        this(path, dialogTitle, selectFiles, !selectFiles, false, null, true, customMessage, localeManager);
    }

    /**
     * Create a file chooser dialog.
     * @param path The directory to open the dialog in.
     * @param selectFiles Whether to show files in the dialog.
     * @param selectDirectories Whether to show directories in the dialog.
     * @param filterByExtension Whether to filter files by extension.
     * @param extension The extension to filter files by. Dependent on filterByExtension. ex. "txt"
     * @param dialogTitle The title of the dialog.
     * @param useCustomMessage whether to use a custom message for the description
     * @param customMessage the message to use for custom message
     * @param localeManager the locale manager to use for the file chooser
     */
    public FileChooser(String path ,String dialogTitle, boolean selectFiles, boolean selectDirectories,
                       boolean filterByExtension, String extension, boolean useCustomMessage, String customMessage, LocaleManager localeManager
    ) {
        super();
        // Set file chooser gui properties
        setDialogTitle(dialogTitle);
        setOpenDialogTitleText(dialogTitle);
        setApproveButtonText(selectButtonText);
        setCancelButtonText(cancelButtonText);
        setPreferredSize(new Dimension(600, 450));
        setFileChooserFont(this.getComponents());
        SwingGUI.setHandCursorToClickableComponents(this);

        // Set the file chooser properties
        int selectionMode
                = selectFiles ? (selectDirectories ? JFileChooser.FILES_AND_DIRECTORIES : JFileChooser.FILES_ONLY)
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

    private void setOpenDialogTitleText(String openDialogTitleText) {
        UIManager.put("FileChooser.openDialogTitleText", openDialogTitleText);
    }

    //TODO https://beradrian.wordpress.com/2007/07/30/internationalization-for-swing-standard-components/

    private void setCancelButtonText(String cancelButtonText) {
        UIManager.put("FileChooser.cancelButtonText", cancelButtonText);
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
