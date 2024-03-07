package com.everdro1d.libs.commands;

/** Interface for commands to do something.
 * <p> Common use for CLI Args
 * @see CommandManager
 */
public interface CommandInterface {
    void execute(CommandManager commandManager);
}

