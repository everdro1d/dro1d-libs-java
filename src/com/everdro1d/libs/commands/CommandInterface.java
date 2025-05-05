// dro1dDev - created: 2024-03-06

package com.everdro1d.libs.commands;

/** Interface for defining commands that can be executed by the {@link CommandManager}.
 * <p> Commonly used for handling CLI arguments in applications.
 * @see CommandManager
 */
public interface CommandInterface {
    /**
     * Executes the command using the provided {@link CommandManager}.
     * @param commandManager the CommandManager instance managing the command
     */
    void execute(CommandManager commandManager);
}

