// dro1dDev - created: 2024-03-06

package com.everdro1d.libs.commands;

/** Interface for defining commands that can be executed by the {@link CommandManager}.
 * <p> Commonly used for handling CLI arguments in applications.
 * @see CommandManager
 */
public interface CommandInterface {
    /**
     * Executes the command using the provided {@link CommandManager}.
     * <p>
     * This method contains the logic for what the command should do when triggered.
     * It is invoked by the {@link CommandManager} when the associated command key is executed.
     * </p>
     *
     * @param commandManager the {@link CommandManager} instance managing the command
     * <p><strong>Example:</strong></p>
     * <blockquote><pre>
     * CommandManager commandManager = new CommandManager();
     * commandManager.executeCommand("-help");
     * </pre></blockquote>
     */
    void execute(CommandManager commandManager);

    /**
     * Returns the description of the command.
     * <p>
     * This description provides a brief explanation of the command's purpose
     * and is typically used in help menus or documentation.
     * </p>
     *
     * @return a {@code String} describing the command
     * <p><strong>Example:</strong></p>
     * <blockquote><pre>
     * HelpCommand helpCommand = new HelpCommand("Displays a list of valid commands");
     * System.out.println(helpCommand.getDescription());
     * // Output: "Displays a list of valid commands"
     * </pre></blockquote>
     */
    String getDescription();

    /**
     * Sets the description of the command.
     * <p>
     * This description provides a brief explanation of the command's purpose
     * and is typically used in help menus or documentation.
     * </p>
     *
     * @param description a {@code String} describing the command
     * <p><strong>Example:</strong></p>
     * <blockquote><pre>
     * HelpCommand helpCommand = new HelpCommand("Change Me!");
     * System.out.println(helpCommand.getDescription());
     * // Output: "Change Me!
     * helpCommand.setDescription("Displays a list of valid commands");
     * System.out.println(helpCommand.getDescription());
     * // Output: "Displays a list of valid commands"
     * </pre></blockquote>
     * @see #getDescription()
     */
    void setDescription(String description);
}

