// dro1dDev - created: 2024-03-04

package com.everdro1d.libs.io;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * TiedOutputStream is a class that ties the standard output and error stream to a
 * single output stream and writes to both this stream and the original standard output.
 * This is useful for when you need the output to be printed to the console and to
 * another output stream at the same time.
 *
 * <p><strong>Usage example:</strong></p>
 * <blockquote><pre>
 * PrintStream debugPrintStream = new PrintStream(new OutputStream() {
 *     {@code @Override}
 *     public void write(int b) {
 *         debugTextArea.append(String.valueOf((char)b));
 *         debugTextArea.setCaretPosition(debugTextArea.getDocument().getLength());
 *     }
 * });
 *
 * TiedOutputStream tiedOutputStream = new TiedOutputStream(debugPrintStream);
 * tiedOutputStream.tieOutputStreams();
 * debugFrame.addWindowListener(new java.awt.event.WindowAdapter() {
 *     {@code @Override}
 *     public void windowClosed(java.awt.event.WindowEvent windowEvent) {
 *         tiedOutputStream.resetOutputStreams();
 *     }
 * });
 * </pre></blockquote>
 */
public class TiedOutputStream extends PrintStream {
    private boolean enabled = true;

    private final PrintStream originalOutputStream;
    private final PrintStream originalErrorStream;

    /**
     * Creates a new TiedOutputStream.
     * @param outputStream the output stream to tie to
     * <p>Note:</p>
     *     <p>This needs to call tieOutputStreams to start and then
     *        resetOutputStreams when finished. </p>
     * @see #tieOutputStreams()
     * @see #resetOutputStreams()
     */
    public TiedOutputStream(OutputStream outputStream) {
        super(outputStream);
        //save standard output
        originalOutputStream = System.out;
        originalErrorStream = System.err;
    }

    /**
     * Retrieves the original standard output stream (System.out) before it was tied.
     *
     * @return the original PrintStream for standard output
     */
    public PrintStream getOriginalOutputStream() {
        return originalOutputStream;
    }

    /**
     * Retrieves the original standard error stream (System.err) before it was tied.
     *
     * @return the original PrintStream for standard error
     */
    public PrintStream getOriginalErrorStream() {
        return originalErrorStream;
    }

    /**
     * Ties the current output stream to both System.out and System.err.
     *
     * <p>This method replaces the standard output and error streams with this instance,
     * allowing all output to be redirected to the tied output stream.</p>
     *
     * @see #resetOutputStreams()
     */
    public void tieOutputStreams() {
        try {
            System.setOut(this);
            System.setErr(this);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    /**
     * Resets System.out and System.err to their original streams.
     *
     * <p>This method should be called when the tied output stream is no longer needed,
     * to restore the original behavior of the standard output and error streams.</p>
     *
     * @see #tieOutputStreams()
     */
    public void resetOutputStreams() {
        this.close();
        System.setOut(originalOutputStream);
        System.setErr(originalErrorStream);
    }

    /**
     * Enables or disables the tied output stream.
     *
     * <p>When disabled, the tied output stream will not output anything,
     * and the original output stream will handle all output alone.</p>
     *
     * @param enabled true to enable the tied output stream, false to disable it
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Checks whether the tied output stream is currently enabled.
     *
     * @return true if the tied output stream is enabled, false otherwise
     */
    public boolean isEnabled() {
        return this.enabled;
    }

    //Override all the print methods
    @Override
    public void print(Object obj) {
        if (enabled) super.print(obj);
        originalOutputStream.print(obj);
    }

    @Override
    public void println(String obj) {
        if (enabled) super.println(obj);
        originalOutputStream.println(obj);
    }

    @Override
    public PrintStream printf(String format, Object... args) {
        if (enabled) super.printf(format, args);
        originalOutputStream.printf(format, args);
        return this;
    }

    @Override
    public void println(Object args) {
        if (enabled) super.println(args);
        originalOutputStream.println(args);
    }
}