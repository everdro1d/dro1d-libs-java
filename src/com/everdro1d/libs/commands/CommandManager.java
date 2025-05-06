// dro1dDev - 2024-03-06

package com.everdro1d.libs.commands;

import com.everdro1d.libs.commands.included.HelpCommand;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * CommandManager is a tool for managing CLI arguments and their associated commands.
 * <p>
 * This class allows you to register commands, retrieve them, and execute them based on CLI input.
 * <h2>Setup</h2>
 * <ol>
 *     <li>
 *         Create a new CommandManager instance during application startup.
 *         <p>
 *         It is recommended to define a {@code Map} of commands and
 *         pass it to the CommandManager constructor. For example:
 *         <blockquote><pre>
 * private static final Map&lt;String, CommandInterface&gt; CUSTOM_COMMANDS_MAP = Map.of(
 *     "-debug", new DebugCommand()
 * );
 * public static CommandManager commandManager = new CommandManager(CUSTOM_COMMANDS_MAP);
 *        </pre></blockquote>
 *     </li>
 *     <li>
 *         Use the CommandManager to handle CLI arguments. You can process
 *         arguments using the {@code ApplicationCore.checkCLIArgs()} method (see also: #1),
 *         or manually, as shown below:
 *         <blockquote><pre>
 * for(String arg : args) {
 *     commandManager.executeCommand(arg);
 * }
 *         </pre></blockquote>
 *     </li>
 *     <li>
 *         Create new command classes for each command and add them to the
 *         CommandManager. For example, in the code above, we added
 *         the {@code DebugCommand}. Below is
 *         an example implementation of the {@code DebugCommand} class:
 *         <blockquote><pre>
 * public class DebugCommand implements CommandInterface {
 *     &#64;Override
 *     public void execute(CommandManager commandManager) {
 *         MainWorker.debug = true;
 *         System.out.println("Debug mode enabled.");
 *     }
 * }
 *         </pre></blockquote>
 *     </li>
 * </ol>
 *
 * @see com.everdro1d.libs.core.ApplicationCore#checkCLIArgs(String[], CommandManager)
 */
public class CommandManager {
    private static final Map<String, CommandInterface> COMMANDS_MAP = new HashMap<>();

    static { // Default commands
        COMMANDS_MAP.put("-help", new HelpCommand());
    }

    /**
     * Creates a new CommandManager with only the default {@code -help} command.
     */
    public CommandManager() {
        appendCommandMap(COMMANDS_MAP);
    }

    /**
     * Creates a new CommandManager containing the default commands and any custom commands defined by the map.
     * @param commandMap map of commands
     * @see #CommandManager()
     */
    public CommandManager(Map<String, CommandInterface> commandMap) {
        appendCommandMap(commandMap);
    }

    /**
     * Retrieves the matching command from the map.
     * @param commandString the key of the CommandInterface to retrieve
     * @return the CommandInterface related to the key, or {@code null} if no matching command is found
     */
    public CommandInterface getCommand(String commandString) {
        return COMMANDS_MAP.get(commandString);
    }

    /**
     * Retrieves the command map.
     * @return Map containing all existing arg keys and their command objects.
     */
    public Map<String, CommandInterface> getCommandMap() {
        return COMMANDS_MAP;
    }

    /**
     * Gets a list of existing keys in the Command Map. Useful to know what commands are available.
     * @return A list of valid CLI args for use.
     */
    public Set<String> getValidCommands() {
        return COMMANDS_MAP.keySet();
    }

    /**
     * Add a command to the map. If the key already exists, the command will be replaced.
     * <p><strong>Example:</strong></p>
     * <blockquote><pre>
     * appendCommand("-help", new HelpCommand());
     * </pre></blockquote>
     * @param commandString trigger key for the command
     * @param commandToExecute CommandInterface class
     * @see #executeCommand(String) 
     * @see HelpCommand#execute(CommandManager) 
     */
    public void appendCommand(String commandString, CommandInterface commandToExecute) {
        COMMANDS_MAP.put(commandString, commandToExecute);
    }

    /**
     * Add a map of custom commands to the command map.
     * <p><strong>Example:</strong></p>
     * <blockquote><pre>
     * yourCommandMap.put("-help", new HelpCommand());
     * </pre></blockquote>
     * @param commandMap map of key-value pairs where the key is the CLI arg to listen for and the value is a new Command.
     * @see #appendCommand(String, CommandInterface)
     */
    public void appendCommandMap(Map<String, CommandInterface> commandMap) {
        COMMANDS_MAP.putAll(commandMap);
    }

    /**
     * Execute a command from the map. If the command is not found, an error message is printed to {@code System.err}.
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
