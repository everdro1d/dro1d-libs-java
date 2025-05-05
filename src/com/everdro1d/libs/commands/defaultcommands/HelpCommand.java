/**************************************************************************************************
 * dro1dDev 2024.                                                                                 *
 **************************************************************************************************/

package com.everdro1d.libs.commands.defaultcommands;

import com.everdro1d.libs.commands.*;

/**
 * A {@code -help} command that prints all valid CLI arg commands when executed.
 */
public class HelpCommand implements CommandInterface {
    @Override
    public void execute(CommandManager commandManager) {
        System.out.println("List of valid commands: " + commandManager.getValidCommands());
    }
}
