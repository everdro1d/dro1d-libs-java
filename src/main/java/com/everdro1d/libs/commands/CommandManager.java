// dro1dDev - 2024-03-06

package com.everdro1d.libs.commands;

import com.everdro1d.libs.commands.included.HelpCommand;

import java.util.*;

import static com.everdro1d.libs.core.Utils.reverseKeysFromValueInMap;

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
 *     "--debug", new DebugCommand()
 * );
 * private static CommandManager commandManager = new CommandManager(CUSTOM_COMMANDS_MAP);
 * </pre></blockquote>
 *         However, you can also create a CommandManager without any commands and add them later.
 *         <p><strong>For example:</strong></p>
 * <blockquote><pre>
 * private static CommandManager commandManager = new CommandManager();
 * commandManager.registerCommand("--debug", new DebugCommand());
 * </pre></blockquote>
 *     </li>
 *     <li>
 *         Use the CommandManager to handle CLI arguments. You can process
 *         arguments using the {@link com.everdro1d.libs.core.ApplicationCore#checkCLIArgs(String[], CommandManager) ApplicationCore.checkCLIArgs()}
 *         method, or manually.
 *         <p>Note: if you plan to implement this manually, I suggest taking a look at the {@code checkCLIArgs()} method's implementation.</p>
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
    private static final Map<String, String> ALIAS_MAP = new HashMap<>();

    static { // Default commands
        COMMANDS_MAP.put("--help", new HelpCommand("displays a list of valid commands"));
        ALIAS_MAP.put("-h", "--help");
    }

    /**
     * Creates a new CommandManager with only the default {@code -help} command.
     * @see #CommandManager(Map, Map)
     * @see #CommandManager(Map)
     * @see #registerCommands(Map)
     * @see #registerAliases(Map)
     */
    public CommandManager() {
        // Default constructor with only help command
    }

    /**
     * Creates a new CommandManager containing the default commands and any custom commands defined by the map.
     * @param commandMap map of commands
     * @see #CommandManager()
     * @see #CommandManager(Map, Map)
     * @see #registerCommands(Map)
     * @see #registerAliases(Map)
     */
    public CommandManager(Map<String, CommandInterface> commandMap) {
        registerCommands(commandMap);
    }

    /**
     * Creates a new CommandManager containing the default commands and any custom commands defined by the map.
     * @param commandMap map of commands
     * @param aliasMap map of aliases
     * @see #CommandManager()
     * @see #CommandManager(Map)
     * @see #registerCommands(Map)
     * @see #registerAliases(Map)
     */
    public CommandManager(Map<String, CommandInterface> commandMap, Map<String, String> aliasMap) {
        registerCommands(commandMap);
        registerAliases(aliasMap);
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
     * Retrieves the command string associated with a given alias.
     * @param alias the alias for which to retrieve the command string
     * @return the command string related to the alias, or {@code null} if no matching alias is found
     */
    public String getCommandStringMatchingAlias(String alias) {
        return ALIAS_MAP.get(alias);
    }

    /**
     * Retrieves all the valid aliases for a given command.
     * @param commandString the command for which to retrieve aliases
     * @return the aliases related to the command, or {@code null} if no matching command is found
     */
    public String[] getAliases(String commandString) {
        return reverseKeysFromValueInMap(commandString, ALIAS_MAP);
    }

    /**
     * Retrieves the command map.
     * @return Map containing all existing arg keys and their command objects.
     */
    public Map<String, CommandInterface> getCommandMap() {
        return COMMANDS_MAP;
    }

    /**
     * Retrieves the alias map.
     * @return Map containing all existing aliases and their command keys.
     */
    public Map<String, String> getAliasMap() {
        return ALIAS_MAP;
    }

    /**
     * Retrieves a list of existing keys in the Command Map. Useful to know what commands are available.
     * @return A list of valid CLI args for use.
     */
    public Set<String> getValidCommands() {
        return COMMANDS_MAP.keySet();
    }

    /**
     * Retrieves a list of valid commands along with their descriptions and aliases.
     * <p>
     * Each entry in the returned list is formatted as:
     * {@code commandKey (alias, alias...): commandDescription}.
     * </p>
     * <p>
     * This method is useful for displaying all available commands, their aliases,
     * and purposes in a user-friendly format, such as in a help menu or CLI output.
     * </p>
     *
     * @return a {@code List<String>} where each element represents a command and its description
     *         in the format {@code commandKey: commandDescription}.
     * <p><strong>Example:</strong></p>
     * <blockquote><pre>
     * // Assuming the CommandManager has the following commands:
     * // --help (-h): displays a list of valid commands
     * // --debug (-d): enables debug mode
     *
     * List&lt;String&gt; commandsWithDescriptions = commandManager.getValidCommandsWithInfo();
     * System.out.println(commandsWithDescriptions);
     * // Output:
     * // [--help (-h): displays a list of valid commands, --debug (-d): enables debug mode]
     * </pre></blockquote>
     */
    public List<String> getValidCommandsWithInfo() {
        List<String> commandInfoList = new ArrayList<>();

        getCommandMap().forEach((key, command) -> {
            String[] aliases = getAliases(key);

            String aliasPart = (aliases != null) ? " (" + String.join(", ", aliases) + ")" : "";

            commandInfoList.add(key + aliasPart + ": " + command.getDescription());

        });

        return commandInfoList;
    }

    /**
     * Add a command to the map. If the key already exists, the command will be replaced.
     * <p><strong>Example:</strong></p>
     * <blockquote><pre>
     * registerCommand("--help", new HelpCommand());
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
     * Add a command to the map with an alias. If the key already exists, the command will be replaced.
     * <p>To add an alias for an existing command, use {@link #registerAlias(String, String)}.</p>
     * <p><strong>Example:</strong></p>
     * <blockquote><pre>
     * registerCommand("--help", "-h", new HelpCommand());
     * </pre></blockquote>
     * @param commandString key for the command (what to listen for)
     * @param alias the alias for the command
     * @param commandToExecute CommandInterface class
     * @see #executeCommand(String)
     * @see HelpCommand#execute(CommandManager)
     */
    public void registerCommand(String commandString, String alias, CommandInterface commandToExecute) {
        COMMANDS_MAP.put(commandString, commandToExecute);
        ALIAS_MAP.put(alias, commandString);
    }

    /**
     * Add an alias for a command. If the command does not exist, an error message is printed to {@code System.err}.
     * Commands can have multiple aliases.
     * <p><strong>Example:</strong></p>
     * <blockquote><pre>
     * registerAlias("-h", "--help");
     * registerAlias("-d", "--debug");
     * </pre></blockquote>
     * @see #getAliases(String)
     * @see #getCommand(String)
     * @see #executeCommand(String)
     * @param alias the alias to register
     * @param commandString the command to which the alias refers
     */
    public void registerAlias(String alias, String commandString) {
        if (COMMANDS_MAP.containsKey(commandString)) {
            ALIAS_MAP.put(alias, commandString);
        } else {
            System.err.printf("Command [%s] not found. Alias [%s] not registered.%n", commandString, alias);
        }
    }

    /**
     * Add a map of custom commands to the command map.
     * <p><strong>Example:</strong></p>
     * <blockquote><pre>
     * yourCommandMap.put("--help", new HelpCommand());
     * yourCommandMap.put("--debug", new DebugCommand());
     *
     * commandManager.registerCommands(yourCommandMap);
     * </pre></blockquote>
     * @param commandMap map of key-value pairs where the key is the CLI arg to
     *                   listen for and the value is a new Command.
     * @see #registerCommandsWithAliases(Map)
     * @see #registerCommand(String, CommandInterface)
     */
    public void registerCommands(Map<String, CommandInterface> commandMap) {
        COMMANDS_MAP.putAll(commandMap);
    }

    /**
     * Add a map of custom commands to the command map with aliases.
     * <p><strong>Example:</strong></p>
     * <blockquote><pre>
     * yourCommandMap.put(new String[]{"--help", "-h"}, new HelpCommand());
     * yourCommandMap.put(new String[]{"--debug", "-d"}, new DebugCommand());
     *
     * commandManager.registerCommandsWithAliases(yourCommandMap);
     * </pre></blockquote>
     * @param commandMap map of key-value pairs where the key is the CLI arg to listen for and the value is a new Command.
     * @see #registerCommands(Map)
     */
    public void registerCommandsWithAliases(Map<String[], CommandInterface> commandMap) {
        for (Map.Entry<String[], CommandInterface> entry : commandMap.entrySet()) {
            String key = entry.getKey()[0];
            String[] aliases = entry.getKey();

            CommandInterface command = entry.getValue();

            registerCommand(key, command);
            for (int i = 1; i < aliases.length; i++) {
                registerAlias(aliases[i], key);
            }
        }

    }

    /**
     * Add a map of aliases to the alias map.
     * <p><strong>Example:</strong></p>
     * <blockquote><pre>
     * yourAliasMap.put("-h", "--help");
     * yourAliasMap.put("-d", "--debug");
     *
     * commandManager.registerAliases(yourAliasMap);
     * </pre></blockquote>
     * @param aliasMap map of key-value pairs where the key is the alias and the value is the command it refers to.
     */
    public void registerAliases(Map<String, String> aliasMap) {
        ALIAS_MAP.putAll(aliasMap);
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
        if (getAliasMap().containsKey(commandString)) {
            commandString = getCommandStringMatchingAlias(commandString);
        }

        CommandInterface commandToExecute = getCommand(commandString);

        if (commandToExecute == null) {
            System.err.printf(
                    "Unknown command: [%s] Skipping.%nUse \"--help\" or \"-h\" to list valid commands.%n",
                    commandString
            );
            return;
        }

        int expectedArgs = commandToExecute.getExpectedArguments();
        int providedArgs = (args == null) ? 0 : args.length;

        if (expectedArgs == 0) {
            commandToExecute.execute(this);

            if (providedArgs > 0) {
                System.err.printf(
                    "Command [%s] does not accept arguments. Ignoring args.%n",
                    commandString
                );
            }

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
