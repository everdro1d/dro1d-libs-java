/**************************************************************************************************
 * dro1dDev 2024.                                                                                 *
 **************************************************************************************************/

package com.everdro1d.libs.commands.defaultcommands;

import com.everdro1d.libs.commands.*;

public class HelpCommand implements CommandInterface {
    @Override
    public void execute(CommandManager commandManager) {
        System.out.println("List of valid commands: " + commandManager.getCommandsMap().keySet());
    }
}
