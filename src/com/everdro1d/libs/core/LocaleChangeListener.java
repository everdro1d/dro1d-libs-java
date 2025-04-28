package com.everdro1d.libs.core;

public interface LocaleChangeListener {
    void onLocaleChange();
}

/*
    Let the class where locale is used implement this interface
    see #1, add #2 to the if (localeManager != null) check & put
    the methods under #3 in the class as well. Finally, where the
    locale switch is placed, call #4 to actually update the locale.

    #1: class x implements LocaleChangeListener

    #2: localeManager.addLocaleChangeListener(this);

    #3:
    @Override
    public void onLocaleChange() {
        useLocale();
    }

    @Override
    public void dispose() {
        if (localeManager != null) {
            localeManager.removeLocaleChangeListener(this);
        }
        super.dispose();
    }

    #4: localeManager.reloadLocaleInProgram("newLocale");
 */
