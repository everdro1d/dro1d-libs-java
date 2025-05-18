// dro1dDev - created: 2024-03-06

package com.everdro1d.libs.commands.included;

import com.everdro1d.libs.commands.*;
import com.everdro1d.libs.core.Utils;

/**
 * A {@code -help} command that prints all valid CLI argument commands when executed.
 * <p>
 * This command is designed to provide users with a list of all available commands
 * managed by the {@link CommandManager}. It is particularly useful for providing
 * guidance to users of a CLI application.
 * </p>
 * <h2>Usage</h2>
 * <p>
 * To use this command, include {@code -help} as an argument when running the application.
 * The command will output a list of valid commands to the console.
 * </p>
 * <p><strong>Example:</strong></p>
 * <blockquote><pre>
 * CommandManager commandManager = new CommandManager();
 * commandManager.executeCommand("-help");
 * </pre></blockquote>
 * @see CommandManager
 */
public class HelpCommand implements CommandInterface {

    private String description;

    /**
     * Constructs a new {@code HelpCommand} instance without a description.
     * This command, when executed, prints all valid CLI arguments and their descriptions.
     * managed by the {@link CommandManager}.
     * @see #setDescription(String) 
     */
    public HelpCommand() {}

    /**
     * Constructs a new {@code HelpCommand} instance.
     * This command, when executed, prints all valid CLI arguments and their descriptions.
     * managed by the {@link CommandManager}.
     *
     * @param description description of what the command does
     */
    public HelpCommand(String description) {
        this.description = description;
    }

    @Override
    public int getExpectedArguments() {
        return 0;
    }

    @Override
    public void execute(CommandManager commandManager) {
        System.out.println("List of valid commands: ");
        Utils.printlnList(
                commandManager.getValidCommandsWithDescription()
        );
    }

    @Override // no args for this command
    public void execute(CommandManager commandManager, String[] args) {}

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }
}
