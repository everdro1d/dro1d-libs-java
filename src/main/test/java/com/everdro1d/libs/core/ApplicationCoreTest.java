// dro1dDev - updated: 2025-05-17

package com.everdro1d.libs.core;

import com.everdro1d.libs.commands.CommandInterface;
import com.everdro1d.libs.commands.CommandManager;
import org.junit.jupiter.api.Test;

import java.net.http.HttpClient;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationCoreTest {

    @Test
    void checkCLIArgs_ValidCommandWithoutArgs() {
        CommandManager commandManager = new CommandManager();
        commandManager.registerCommand("--test", new CommandInterface() {
            @Override
            public int getExpectedArguments() {
                return 0;
            }

            @Override
            public void execute(CommandManager commandManager) {
                System.out.println("Command executed without arguments.");
            }

            @Override
            public void execute(CommandManager commandManager, String[] args) {
                fail("This method should not be called.");
            }

            @Override
            public String getDescription() {
                return "Test command without arguments.";
            }

            @Override
            public void setDescription(String description) {
                // No-op
            }
        });

        ApplicationCore.checkCLIArgs(new String[]{"--test"}, commandManager);
    }

    @Test
    void checkCLIArgs_ValidCommandWithArgs() {
        CommandManager commandManager = new CommandManager();
        commandManager.registerCommand("--test", new CommandInterface() {
            @Override
            public int getExpectedArguments() {
                return 2;
            }

            @Override
            public void execute(CommandManager commandManager) {
                fail("This method should not be called.");
            }

            @Override
            public void execute(CommandManager commandManager, String[] args) {
                assertEquals(2, args.length);
                assertEquals("arg1", args[0]);
                assertEquals("arg2", args[1]);
            }

            @Override
            public String getDescription() {
                return "Test command with arguments.";
            }

            @Override
            public void setDescription(String description) {
                // No-op
            }
        });

        ApplicationCore.checkCLIArgs(new String[]{"--test", "arg1", "arg2"}, commandManager);
    }

    @Test
    void checkCLIArgs_InvalidCommand() {
        CommandManager commandManager = new CommandManager();

        ApplicationCore.checkCLIArgs(new String[]{"--invalid"}, commandManager);
        assertNull(commandManager.getCommand("--invalid"));
    }

    @Test
    void checkCLIArgs_TooFewArguments() {
        CommandManager commandManager = new CommandManager();
        commandManager.registerCommand("--test", new CommandInterface() {
            @Override
            public int getExpectedArguments() {
                return 2;
            }

            @Override
            public void execute(CommandManager commandManager) {
                fail("This method should not be called.");
            }

            @Override
            public void execute(CommandManager commandManager, String[] args) {
                fail("This method should not be called with insufficient arguments.");
            }

            @Override
            public String getDescription() {
                return "Test command with arguments.";
            }

            @Override
            public void setDescription(String description) {
                // No-op
            }
        });

        ApplicationCore.checkCLIArgs(new String[]{"--test", "arg1"}, commandManager);
    }

    @Test
    void checkCLIArgs_TooManyArguments() {
        CommandManager commandManager = new CommandManager();
        commandManager.registerCommand("--test", new CommandInterface() {
            @Override
            public int getExpectedArguments() {
                return 2;
            }

            @Override
            public void execute(CommandManager commandManager) {
                fail("This method should not be called.");
            }

            @Override
            public void execute(CommandManager commandManager, String[] args) {
                assertEquals(2, args.length);
                assertEquals("arg1", args[0]);
                assertEquals("arg2", args[1]);
            }

            @Override
            public String getDescription() {
                return "Test command with arguments.";
            }

            @Override
            public void setDescription(String description) {
                // No-op
            }
        });

        ApplicationCore.checkCLIArgs(new String[]{"--test", "arg1", "arg2", "arg3"}, commandManager);
    }

    @Test
    void getLatestVersion_ValidRedirect() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        String mockRedirectUrl = "https://httpbin.org/redirect-to?url=https://example.com/everdro1d/TestV/releases/tag/v1.2.3";

        String version = ApplicationCore.getLatestVersion(mockRedirectUrl, "/v", "");
        assertEquals("1.2.3", version);
    }

    @Test
    void getLatestVersion_NoRedirect() {
        HttpClient client = HttpClient.newHttpClient();
        String url = "https://httpbin.org/status/200";

        String version = ApplicationCore.getLatestVersion(url, "v", "");
        assertNull(version);
    }

    @Test
    void getLatestVersion_InvalidVersionFormat() {
        HttpClient client = HttpClient.newHttpClient();
        String mockRedirectUrl = "https://httpbin.org/redirect-to?url=https://example.com/releases/invalid-version";

        String version = ApplicationCore.getLatestVersion(mockRedirectUrl, "v", "");
        assertNull(version);
    }

    @Test
    void getLatestVersion_ExceptionThrown() {
        String invalidUrl = "invalid-url";

        String version = ApplicationCore.getLatestVersion(invalidUrl, "v", "");
        assertNull(version);
    }
}