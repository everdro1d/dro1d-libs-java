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
import java.util.Map;
import java.util.TreeMap;
import java.util.prefs.Preferences;

import static com.everdro1d.libs.swing.SwingGUI.setLookAndFeel;

public class FileChooser extends JFileChooser {
    // UI text
    private LocaleManager localeManager;
    private String lookInLabelText = "Look In:";
    private String saveInLabelText = "Save In:";
    private String upFolderToolTipText = "Up One Level";
    private String homeFolderToolTipText = "Home";
    private String newFolderToolTipText = "Create New Folder";
    private String listViewButtonToolTipText = "List";
    private String detailsViewButtonToolTipText = "Details";
    private String fileNameHeaderText = "Name";
    private String fileSizeHeaderText = "Size";
    private String fileTypeHeaderText = "Type";
    private String fileDateHeaderText = "Date";
    private String fileAttrHeaderText = "Attributes";
    private String fileNameLabelText = "File Name:";
    private String filesOfTypeLabelText = "Files of Type:";
    private String openButtonText = "Open";
    private String openButtonToolTipText = "Open selected file";
    private String saveButtonText = "Save";
    private String saveButtonToolTipText = "Save selected file";
    private String directoryOpenButtonText = "Open";
    private String directoryOpenButtonToolTipText = "Open selected directory";
    private String approveButtonText = "Select";
    private String cancelButtonText = "Cancel";
    private String cancelButtonToolTipText = "Cancel";
    private String newFolderErrorText = "Error creating new folder";
    private String extensionFilterDescription = "Files";
    private String directoryOnlyDescription = "Directory (Folder)";
    private String filesOnlyDescription = "Any File";
    private String anyFileOrDirectory = "Any File or Directory (Folder)";
    private final Font font = this.getParent() != null
            ? this.getParent().getFont()
            : new Font("Tahoma", Font.PLAIN,16);


    /**
     * Create a generic file chooser dialog allowing selection of both files and directories.
     * @param path The directory to open the dialog in.
     * @param dialogTitle The title of the dialog.
     * @param localeManager the locale manager to use for the file chooser
     */
    public FileChooser(String path, String dialogTitle, LocaleManager localeManager) {
        this(path, dialogTitle, true, true, false,
                null, false, null, localeManager);
    }

    /**
     * Create a file chooser dialog for selecting files or directories.
     * @param path The directory to open the dialog in.
     * @param dialogTitle The title of the dialog.
     * @param selectFiles Whether to show files in the dialog. If false, only directories will be shown.
     * @param localeManager the locale manager to use for the file chooser
     */
    public FileChooser(String path, String dialogTitle, boolean selectFiles, LocaleManager localeManager) {
        this(path, dialogTitle, selectFiles, !selectFiles, false, null,
                false, null, localeManager);
    }

    /**
     * Create a file chooser dialog for selecting files matching a file extension.
     * @param path The directory to open the dialog in.
     * @param dialogTitle The title of the dialog.
     * @param extension The extension to filter files by. ex. "txt"
     * @param localeManager the locale manager to use for the file chooser
     */
    public FileChooser(String path, String dialogTitle, String extension, LocaleManager localeManager) {
        this(path, dialogTitle, true, false, true, extension,
                false, null, localeManager);
    }

    /**
     * Create a file chooser dialog for selecting files or directories that uses a custom description message.
     * @param path The directory to open the dialog in.
     * @param dialogTitle The title of the dialog.
     * @param selectFiles Whether to show files in the dialog. If false, only directories will be shown.
     * @param customMessage Custom message to show in the description area.
     * @param localeManager the locale manager to use for the file chooser
     */
    public FileChooser(String path, String dialogTitle, boolean selectFiles, String customMessage,
                       LocaleManager localeManager) {
        this(path, dialogTitle, selectFiles, !selectFiles, false, null,
                true, customMessage, localeManager);
    }

    /**
     * Create a file chooser dialog.
     * @param path The directory to open the dialog in.
     * @param dialogTitle The title of the dialog.
     * @param selectFiles Whether to show files in the dialog.
     * @param selectDirectories Whether to show directories in the dialog.
     * @param filterByExtension Whether to filter files by extension.
     * @param extension The extension to filter files by. Dependent on filterByExtension. ex. "txt"
     * @param useCustomMessage whether to use a custom message for the description
     * @param customMessage the message to use for custom message
     * @param localeManager the locale manager to use for the file chooser
     */
    public FileChooser(String path ,String dialogTitle, boolean selectFiles, boolean selectDirectories,
                       boolean filterByExtension, String extension, boolean useCustomMessage,
                       String customMessage, LocaleManager localeManager
    ) {
        super();
        // Set file chooser gui properties
        setDialogTitle(dialogTitle);

        if (localeManager != null) {
            this.localeManager = localeManager;

            // if the locale does not contain the class, add it and it's components
            if (!localeManager.getClassesInLocaleMap().contains("FileChooser")) {
                addClassToLocale();
            }
            useLocale();
        } else System.out.println("LocaleManager is null. FileChooser will launch without localization.");

        setPreferredSize(new Dimension(600, 450));
        setFileChooserFont(this.getComponents());
        SwingGUI.setHandCursorToClickableComponents(this);

        // Set the file chooser properties
        int selectionMode = selectFiles ?
                ( selectDirectories
                        ? JFileChooser.FILES_AND_DIRECTORIES
                        : JFileChooser.FILES_ONLY ) : JFileChooser.DIRECTORIES_ONLY;

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
                if (filterByExtension) return extension.toUpperCase() + extensionFilterDescription + " (*." + extension + ")";

                if (selectionMode == JFileChooser.DIRECTORIES_ONLY) return directoryOnlyDescription;
                if (selectionMode == JFileChooser.FILES_ONLY) return filesOnlyDescription;
                return anyFileOrDirectory;
            }
        });
    }

    private void addClassToLocale() {
        Map<String, Map<String, String>> map = new TreeMap<>();
            map.put("Main", new TreeMap<>());

        Map<String, String> fileChooserMap = map.get("Main");
            fileChooserMap.put("lookInLabelText", lookInLabelText);
            fileChooserMap.put("saveInLabelText", saveInLabelText);
            fileChooserMap.put("upFolderToolTipText", upFolderToolTipText);
            fileChooserMap.put("homeFolderToolTipText", homeFolderToolTipText);
            fileChooserMap.put("newFolderToolTipText", newFolderToolTipText);
            fileChooserMap.put("listViewButtonToolTipText", listViewButtonToolTipText);
            fileChooserMap.put("detailsViewButtonToolTipText", detailsViewButtonToolTipText);
            fileChooserMap.put("fileNameHeaderText", fileNameHeaderText);
            fileChooserMap.put("fileSizeHeaderText", fileSizeHeaderText);
            fileChooserMap.put("fileTypeHeaderText", fileTypeHeaderText);
            fileChooserMap.put("fileDateHeaderText", fileDateHeaderText);
            fileChooserMap.put("fileAttrHeaderText", fileAttrHeaderText);
            fileChooserMap.put("fileNameLabelText", fileNameLabelText);
            fileChooserMap.put("filesOfTypeLabelText", filesOfTypeLabelText);
            fileChooserMap.put("openButtonText", openButtonText);
            fileChooserMap.put("openButtonToolTipText", openButtonToolTipText);
            fileChooserMap.put("saveButtonText", saveButtonText);
            fileChooserMap.put("saveButtonToolTipText", saveButtonToolTipText);
            fileChooserMap.put("directoryOpenButtonText", directoryOpenButtonText);
            fileChooserMap.put("directoryOpenButtonToolTipText", directoryOpenButtonToolTipText);
            fileChooserMap.put("approveButtonText", approveButtonText);
            fileChooserMap.put("cancelButtonText", cancelButtonText);
            fileChooserMap.put("cancelButtonToolTipText", cancelButtonToolTipText);
            fileChooserMap.put("newFolderErrorText", newFolderErrorText);
            fileChooserMap.put("extFilterDescription", extensionFilterDescription);
            fileChooserMap.put("dirOnlyDescription", directoryOnlyDescription);
            fileChooserMap.put("filesOnlyDescription", filesOnlyDescription);
            fileChooserMap.put("anyFileOrDirectory", anyFileOrDirectory);

        localeManager.addClassSpecificMap("FileChooser", map);
    }

    private void useLocale() {
        Map<String, String> varMap = localeManager.getAllVariablesWithinClassSpecificMap("FileChooser");

        lookInLabelText = varMap.getOrDefault("lookInLabelText", lookInLabelText);
        saveInLabelText = varMap.getOrDefault("saveInLabelText", saveInLabelText);
        upFolderToolTipText = varMap.getOrDefault("upFolderToolTipText", upFolderToolTipText);
        homeFolderToolTipText = varMap.getOrDefault("homeFolderToolTipText", homeFolderToolTipText);
        newFolderToolTipText = varMap.getOrDefault("newFolderToolTipText", newFolderToolTipText);
        listViewButtonToolTipText = varMap.getOrDefault("listViewButtonToolTipText", listViewButtonToolTipText);
        detailsViewButtonToolTipText = varMap.getOrDefault("detailsViewButtonToolTipText", detailsViewButtonToolTipText);
        fileNameHeaderText = varMap.getOrDefault("fileNameHeaderText", fileNameHeaderText);
        fileSizeHeaderText = varMap.getOrDefault("fileSizeHeaderText", fileSizeHeaderText);
        fileTypeHeaderText = varMap.getOrDefault("fileTypeHeaderText", fileTypeHeaderText);
        fileDateHeaderText = varMap.getOrDefault("fileDateHeaderText", fileDateHeaderText);
        fileAttrHeaderText = varMap.getOrDefault("fileAttrHeaderText", fileAttrHeaderText);
        fileNameLabelText = varMap.getOrDefault("fileNameLabelText", fileNameLabelText);
        filesOfTypeLabelText = varMap.getOrDefault("filesOfTypeLabelText", filesOfTypeLabelText);
        openButtonText = varMap.getOrDefault("openButtonText", openButtonText);
        openButtonToolTipText = varMap.getOrDefault("openButtonToolTipText", openButtonToolTipText);
        saveButtonText = varMap.getOrDefault("saveButtonText", saveButtonText);
        saveButtonToolTipText = varMap.getOrDefault("saveButtonToolTipText", saveButtonToolTipText);
        directoryOpenButtonText = varMap.getOrDefault("directoryOpenButtonText", directoryOpenButtonText);
        directoryOpenButtonToolTipText = varMap.getOrDefault("directoryOpenButtonToolTipText", directoryOpenButtonToolTipText);
        approveButtonText = varMap.getOrDefault("approveButtonText", approveButtonText);
        cancelButtonText = varMap.getOrDefault("cancelButtonText", cancelButtonText);
        cancelButtonToolTipText = varMap.getOrDefault("cancelButtonToolTipText", cancelButtonToolTipText);
        newFolderErrorText = varMap.getOrDefault("newFolderErrorText", newFolderErrorText);
        extensionFilterDescription = varMap.getOrDefault("extFilterDescription", extensionFilterDescription);
        directoryOnlyDescription = varMap.getOrDefault("dirOnlyDescription", directoryOnlyDescription);
        filesOnlyDescription = varMap.getOrDefault("filesOnlyDescription", filesOnlyDescription);
        anyFileOrDirectory = varMap.getOrDefault("anyFileOrDirectory", anyFileOrDirectory);

        setLookAndFeel(true, false);
        setApproveButtonText(approveButtonText);
        UIManager.put("FileChooser.lookInLabelText", lookInLabelText);
        UIManager.put("FileChooser.saveInLabelText", saveInLabelText);
        UIManager.put("FileChooser.upFolderToolTipText", upFolderToolTipText);
        UIManager.put("FileChooser.homeFolderToolTipText", homeFolderToolTipText);
        UIManager.put("FileChooser.newFolderToolTipText", newFolderToolTipText);
        UIManager.put("FileChooser.listViewButtonToolTipText", listViewButtonToolTipText);
        UIManager.put("FileChooser.detailsViewButtonToolTipText", detailsViewButtonToolTipText);
        UIManager.put("FileChooser.fileNameHeaderText", fileNameHeaderText);
        UIManager.put("FileChooser.fileSizeHeaderText", fileSizeHeaderText);
        UIManager.put("FileChooser.fileTypeHeaderText", fileTypeHeaderText);
        UIManager.put("FileChooser.fileDateHeaderText", fileDateHeaderText);
        UIManager.put("FileChooser.fileAttrHeaderText", fileAttrHeaderText);
        UIManager.put("FileChooser.fileNameLabelText", fileNameLabelText);
        UIManager.put("FileChooser.filesOfTypeLabelText", filesOfTypeLabelText);
        UIManager.put("FileChooser.openButtonText", openButtonText);
        UIManager.put("FileChooser.openButtonToolTipText", openButtonToolTipText);
        UIManager.put("FileChooser.saveButtonText", saveButtonText);
        UIManager.put("FileChooser.saveButtonToolTipText", saveButtonToolTipText);
        UIManager.put("FileChooser.directoryOpenButtonText", directoryOpenButtonText);
        UIManager.put("FileChooser.directoryOpenButtonToolTipText", directoryOpenButtonToolTipText);
        UIManager.put("FileChooser.cancelButtonText", cancelButtonText);
        UIManager.put("FileChooser.cancelButtonToolTipText", cancelButtonToolTipText);
        UIManager.put("FileChooser.newFolderErrorText", newFolderErrorText);

        SwingUtilities.updateComponentTreeUI(this);

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
