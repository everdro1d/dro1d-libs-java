// dro1dDev - updated: 2025-05-17

package com.everdro1d.libs.commands;

import com.everdro1d.libs.commands.included.HelpCommand;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CommandManagerTest {

    @Test
    void testRegisterAndRetrieveCommand() {
        CommandManager commandManager = new CommandManager();
        CommandInterface testCommand = new HelpCommand("Test command");

        commandManager.registerCommand("-test", testCommand);

        assertNotNull(commandManager.getCommand("-test"));
        assertEquals(testCommand, commandManager.getCommand("-test"));
    }

    @Test
    void testExecuteCommandWithoutArgs() {
        CommandManager commandManager = new CommandManager();
        CommandInterface testCommand = new HelpCommand("Test command");

        commandManager.registerCommand("-test", testCommand);

        // Capture output
        commandManager.executeCommand("-test");
        // Verify no exceptions are thrown
    }

    @Test
    void testExecuteCommandWithArgs() {
        CommandManager commandManager = new CommandManager();
        CommandInterface testCommand = new CommandInterface() {
            @Override
            public int getExpectedArguments() {
                return 2;
            }

            @Override
            public void execute(CommandManager commandManager) {
                fail("This method should not be called for commands with arguments.");
            }

            @Override
            public void execute(CommandManager commandManager, String[] args) {
                assertEquals(2, args.length);
                assertEquals("arg1", args[0]);
                assertEquals("arg2", args[1]);
            }

            @Override
            public String getDescription() {
                return "Test command with args";
            }

            @Override
            public void setDescription(String description) {
                // No-op
            }
        };

        commandManager.registerCommand("-test", testCommand);

        commandManager.executeCommand("-test", new String[]{"arg1", "arg2"});
    }

    @Test
    void testInvalidCommandExecution() {
        CommandManager commandManager = new CommandManager();

        assertNull(commandManager.getCommand("-invalid"));
        // Ensure no exceptions are thrown for invalid commands
        commandManager.executeCommand("-invalid");
    }

    @Test
    void testGetValidCommands() {
        CommandManager commandManager = new CommandManager(Map.of(
                "-test1", new HelpCommand("Test command 1"),
                "-test2", new HelpCommand("Test command 2")
        ));

        assertTrue(commandManager.getValidCommands().contains("-test1"));
        assertTrue(commandManager.getValidCommands().contains("-test2"));
    }

    @Test
    void testExecuteCommandWithTooFewArgs() {
        CommandManager commandManager = new CommandManager();
        CommandInterface testCommand = new CommandInterface() {
            @Override
            public int getExpectedArguments() {
                return 2;
            }

            @Override
            public void execute(CommandManager commandManager) {
                fail("This method should not be called for commands with arguments.");
            }

            @Override
            public void execute(CommandManager commandManager, String[] args) {
                fail("This method should not be called with insufficient arguments.");
            }

            @Override
            public String getDescription() {
                return "Test command with args";
            }

            @Override
            public void setDescription(String description) {
                // No-op
            }
        };

        commandManager.registerCommand("-test", testCommand);

        commandManager.executeCommand("-test", new String[]{"arg1"});
    }

    @Test
    void testExecuteCommandWithTooManyArgs() {
        CommandManager commandManager = new CommandManager();
        CommandInterface testCommand = new CommandInterface() {
            @Override
            public int getExpectedArguments() {
                return 2;
            }

            @Override
            public void execute(CommandManager commandManager) {
                fail("This method should not be called for commands with arguments.");
            }

            @Override
            public void execute(CommandManager commandManager, String[] args) {
                assertEquals(2, args.length);
                assertEquals("arg1", args[0]);
                assertEquals("arg2", args[1]);
            }

            @Override
            public String getDescription() {
                return "Test command with args";
            }

            @Override
            public void setDescription(String description) {
                // No-op
            }
        };

        commandManager.registerCommand("-test", testCommand);

        commandManager.executeCommand("-test", new String[]{"arg1", "arg2", "arg3"});
    }
}