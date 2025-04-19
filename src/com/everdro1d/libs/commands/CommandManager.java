/**************************************************************************************************
 * dro1dDev 2024.                                                                                 *
 **************************************************************************************************/

package com.everdro1d.libs.commands;

import com.everdro1d.libs.commands.defaultcommands.HelpCommand;

import java.util.HashMap;
import java.util.Map;

public class CommandManager {
    public CommandManager() {
        appendCommandMap(COMMANDS_MAP);
    }

    public CommandManager(Map<String, CommandInterface> commandMap) {
        appendCommandMap(commandMap);
    }

    private static final Map<String, CommandInterface> COMMANDS_MAP = new HashMap<>();

    static { // Default commands
        COMMANDS_MAP.put("-help", new HelpCommand());
    }

    /**
     * Retrieves the matching command from the map.
     * @param commandString the key of the CommandInterface to retrieve
     * @return the CommandInterface related to the key
     */
    public CommandInterface getCommand(String commandString) {
        return COMMANDS_MAP.get(commandString);
    }

    /**
     * @return the command map
     */
    public Map<String, CommandInterface> getCommandsMap() {
        return COMMANDS_MAP;
    }

    /**
     * Add a command to the map.
     * @param commandString trigger key for the command
     * @param commandToExecute CommandInterface class
     * @see #executeCommand(String) 
     * @see HelpCommand#execute(CommandManager) 
     */
    public void appendCommand(String commandString, CommandInterface commandToExecute) {
        COMMANDS_MAP.put(commandString, commandToExecute);
    }

    public void appendCommandMap(Map<String, CommandInterface> commandMap) {
        COMMANDS_MAP.putAll(commandMap);
    }

    /**
     * Execute a command from the map.
     * @param commandString the key of the CommandInterface to execute
     * @see #appendCommand(String, CommandInterface)
     * @see HelpCommand#execute(CommandManager)
     */
    public void executeCommand(String commandString) {
        CommandInterface commandToExecute = getCommand(commandString);
        if (commandToExecute != null) {
            commandToExecute.execute(this);
        } else {
            System.err.println(
                    "Unknown command: [" + commandString + "] Skipping." +
                            "\n    Use \"-help\" to list valid commands in console."
            );
        }
    }
}
