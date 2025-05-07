// dro1dDev - created: 2025-04-28

package com.everdro1d.libs.locale;

/**
 * The {@code LocaleChangeListener} interface defines a contract for classes that need to respond to locale changes.
 * Implementing classes can register themselves with a {@link LocaleManager} to be notified when the application's locale changes.
 *
 * <p><strong>Usage:</strong></p>
 * <ol>
 *   <li>Implement this interface in the class where locale updates are required.</li>
 *   <li>Register the class as a listener using {@link LocaleManager#addLocaleChangeListener(LocaleChangeListener) localeManager.addLocaleChangeListener(this);}.</li>
 *   <li>Override the {@link #onLocaleChange()} method to handle locale updates.</li>
 *   <li>Ensure proper cleanup by removing the listener in the {@link #dispose()} method using {@link LocaleManager#removeLocaleChangeListener(LocaleChangeListener)  localeManager.removeLocaleChangeListener(this);}.</li>
 *   <li>Call {@link LocaleManager#reloadLocaleInProgram(String) localeManager.reloadLocaleInProgram("newLocale");} to trigger the locale change.</li>
 * </ol>
 *
 * <p><strong>Example:</strong></p>
 * <blockquote><pre>
 * public class MyComponent implements LocaleChangeListener {
 *     private LocaleManager localeManager;
 *
 *     public MyComponent(LocaleManager localeManager) {
 *         this.localeManager = localeManager;
 *         this.localeManager.addLocaleChangeListener(this);
 *     }
 *
 *     {@code @Override}
 *     public void onLocaleChange() {
 *         // Update UI or perform actions based on the new locale
 *         System.out.println("Locale has changed!");
 *     }
 *
 *     {@code @Override}
 *     public void dispose() {
 *         if (localeManager != null) {
 *             localeManager.removeLocaleChangeListener(this);
 *         }
 *         super.dispose();
 *     }
 * }
 * </pre></blockquote>
 */
public interface LocaleChangeListener {

    /**
     * Called when the locale changes.
     * Implementing classes should override this method to perform actions based on the new locale.
     * <p>See: {@link LocaleChangeListener} for usage example.</p>
     */
    void onLocaleChange();

    /**
     * Called when the listener is no longer needed.
     * Implementing classes should override this method to perform cleanup actions.
     * <p>See: {@link LocaleChangeListener} for usage example.</p>
     */
    void dispose();
}
