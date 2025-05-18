// dro1dDev - 2024-03-06

package com.everdro1d.libs.commands;

import com.everdro1d.libs.commands.included.HelpCommand;

import java.util.*;

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
 *         pass it to the CommandManager constructor.
 *         <p><strong>For example:</strong></p>
 * <blockquote><pre>
 * private static final Map&lt;String, CommandInterface&gt; CUSTOM_COMMANDS_MAP = Map.of(
 *     "-debug", new DebugCommand()
 * );
 * private static CommandManager commandManager = new CommandManager(CUSTOM_COMMANDS_MAP);
 * </pre></blockquote>
 *         However, you can also create a CommandManager without any commands and add them later.
 *         <p><strong>For example:</strong></p>
 * <blockquote><pre>
 * private static CommandManager commandManager = new CommandManager();
 * commandManager.registerCommand("-debug", new DebugCommand());
 * </pre></blockquote>
 *     </li>
 *     <li>
 *         Use the CommandManager to handle CLI arguments. You can process
 *         arguments using the {@link com.everdro1d.libs.core.ApplicationCore#checkCLIArgs(String[], CommandManager) ApplicationCore.checkCLIArgs()}
 *         method, or manually.
 *         <p><strong>For example:</strong></p>
 * <blockquote><pre>
 * for(String arg : args) {
 *     commandManager.executeCommand(arg);
 * }
 * </pre></blockquote>
 *     </li>
 *     <li>
 *         Create new command classes for each command and add them to the
 *         CommandManager. For example, in the code above, we added
 *         the {@code DebugCommand}.
 *         <p><strong>Example implementation of the {@code DebugCommand} class:</strong></p>
 * <blockquote><pre>
 * public class DebugCommand implements CommandInterface {
 *     &#64;Override
 *     public void execute(CommandManager commandManager) {
 *         MainWorker.debug = true;
 *         System.out.println("Debug mode enabled.");
 *     }
 * }
 * </pre></blockquote>
 *     </li>
 * </ol>
 */
public class CommandManager {
    private static final Map<String, CommandInterface> COMMANDS_MAP = new HashMap<>();

    static { // Default commands
        COMMANDS_MAP.put("-help", new HelpCommand("displays a list of valid commands"));
    }

    /**
     * Creates a new CommandManager with only the default {@code -help} command.
     */
    public CommandManager() {
        registerCommands(COMMANDS_MAP);
    }

    /**
     * Creates a new CommandManager containing the default commands and any custom commands defined by the map.
     * @param commandMap map of commands
     * @see #CommandManager()
     */
    public CommandManager(Map<String, CommandInterface> commandMap) {
        registerCommands(commandMap);
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
     * Retrieves a list of existing keys in the Command Map. Useful to know what commands are available.
     * @return A list of valid CLI args for use.
     */
    public Set<String> getValidCommands() {
        return COMMANDS_MAP.keySet();
    }

    /**
     * Retrieves a list of valid commands along with their descriptions.
     * <p>
     * Each entry in the returned list is formatted as:
     * {@code commandKey: commandDescription}.
     * </p>
     * <p>
     * This method is useful for displaying all available commands and their purposes
     * in a user-friendly format, such as in a help menu or CLI output.
     * </p>
     *
     * @return a {@code List<String>} where each element represents a command and its description
     *         in the format {@code commandKey: commandDescription}.
     * <p><strong>Example:</strong></p>
     * <blockquote><pre>
     * // Assuming the CommandManager has the following commands:
     * // -help: displays a list of valid commands
     * // -debug: enables debug mode
     *
     * List&lt;String&gt; commandsWithDescriptions = commandManager.getValidCommandsWithDescription();
     * System.out.println(commandsWithDescriptions);
     * // Output:
     * // [-help: displays a list of valid commands, -debug: enables debug mode]
     * </pre></blockquote>
     */
    public List<String> getValidCommandsWithDescription() {
        List<String> s = new ArrayList<>();
        getCommandMap().forEach((key, command) ->
                s.add(key + ": " + command.getDescription())
        );

        return s;
    }

    /**
     * Add a command to the map. If the key already exists, the command will be replaced.
     * <p><strong>Example:</strong></p>
     * <blockquote><pre>
     * registerCommand("-help", new HelpCommand());
     * </pre></blockquote>
     * @param commandString key for the command (what to listen for)
     * @param commandToExecute CommandInterface class
     * @see #executeCommand(String) 
     * @see HelpCommand#execute(CommandManager) 
     */
    public void registerCommand(String commandString, CommandInterface commandToExecute) {
        COMMANDS_MAP.put(commandString, commandToExecute);
    }

    /**
     * Add a map of custom commands to the command map.
     * <p><strong>Example:</strong></p>
     * <blockquote><pre>
     * yourCommandMap.put("-help", new HelpCommand());
     * yourCommandMap.put("-debug", new DebugCommand());
     *
     * commandManager.registerCommands(yourCommandMap);
     * </pre></blockquote>
     * @param commandMap map of key-value pairs where the key is the CLI arg to listen for and the value is a new Command.
     * @see #registerCommand(String, CommandInterface)
     */
    public void registerCommands(Map<String, CommandInterface> commandMap) {
        COMMANDS_MAP.putAll(commandMap);
    }

    /**
     * Execute a command from the map. If the command is not found, an error message is printed to {@code System.err}.
     * @param commandString the key of the CommandInterface to execute
     * @see #registerCommand(String, CommandInterface)
     * @see HelpCommand#execute(CommandManager)
     */
    public void executeCommand(String commandString) {
        executeCommand(commandString, null);
    }

    /**
     * Execute a command from the map with arguments. If the command is not
     * found or the number of arguments is different from expected, an error
     * message is printed to {@code System.err}.
     *
     * @param commandString the key of the CommandInterface to execute
     * @param args          an array of {@code String} arguments passed to the command
     */
    public void executeCommand(String commandString, String[] args) {
        CommandInterface commandToExecute = getCommand(commandString);

        if (commandToExecute == null) {
            System.err.printf("Unknown command: [%s] Skipping.%nUse \"-help\" to list valid commands.%n", commandString);
            return;
        }

        int expectedArgs = commandToExecute.getExpectedArguments();
        int providedArgs = (args == null) ? 0 : args.length;

        if (expectedArgs == 0) {
            commandToExecute.execute(this);

        } else if (providedArgs != expectedArgs) {
            System.err.printf(
                "Invalid number of arguments for command: [%s] Skipping.%nExpected: %d%nProvided: %d%n",
                commandString, expectedArgs, providedArgs
            );

        } else {
            commandToExecute.execute(this, args);

        }

    }
}
