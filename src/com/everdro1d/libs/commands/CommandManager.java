/**************************************************************************************************
 * dro1dDev 2024.                                                                                 *
 **************************************************************************************************/

package com.everdro1d.libs.commands;

import com.everdro1d.libs.commands.defaultcommands.HelpCommand;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * CommandManager is a CLI arg management tool.
 * <p>
 * <h2>Setup</h2>
 * <ol>
 *     <li>
 *         Create a new CommandManager on application startup.
 *         <p>Note: it is recommended to create a Map for the
 *         commands and then pass the map into the CommandManager
 *         constructor. Ex:
 *         <blockquote><pre>
 *            private static final Map<String, CommandInterface> CUSTOM_COMMANDS_MAP = Map.of(
 *                "-debug", new DebugCommand()
 *            );
 *            public static CommandManager commandManager = new CommandManager(CUSTOM_COMMANDS_MAP);
 *        </pre></blockquote>
 *     </li>
 *     <li>
 *         You can then put the CommandManager to use, handling
 *         the args using checkCLIArgs() method (see also: #1),
 *         or manually, using something like below:
 *         <blockquote><pre>
 *             for(String arg : args) {
 *                 commandManager.executeCommand(arg);
 *             }
 *         </pre></blockquote>
 *     </li>
 *     <li>
 *         Now create a new command class, and add it to the 
 *         CommandManager. In our example above, we've already
 *         added the DebugCommand to the CommandManager, so now
 *         we will create that command class.
 *         <blockquote><pre>
 *             public class DebugCommand implements CommandInterface {
 *                 &#64;Override
 *                 public void execute(CommandManager commandManager) {
 *                     MainWorker.debug = true;
 *                     System.out.println("Debug mode enabled.");
 *                 }
 *             }
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
     * @return the CommandInterface related to the key
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
     * @return A list of valid CLI args for use.
     */
    public Set<String> getValidCommands() {
        return COMMANDS_MAP.keySet();
    }

    /**
     * Add a command to the map.
     * Ex:<blockquote><pre>
     *     appendCommand("-help", new HelpCommand());
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
     * Ex:<blockquote><pre>
     *     yourCommandMap.put("-help", new HelpCommand());
     * </pre></blockquote>
     * @param commandMap map of key-value pairs where the key is the CLI arg to listen for and the value is a new Command.
     * @see #appendCommand(String, CommandInterface)
     */
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
