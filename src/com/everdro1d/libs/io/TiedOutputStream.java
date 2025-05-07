// dro1dDev - created: 2024-03-04

package com.everdro1d.libs.io;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * TiedOutputStream is a class that ties the standard output and error stream to a
 * single output stream. This is useful for when you need the output to be printed
 * to the console and to another output stream at the same time.
 *
 * <p><strong>Usage example:</strong></p>
 * <blockquote><pre>
 * PrintStream debugPrintStream = new PrintStream(new OutputStream() {
 *     *@Override
 *     public void write(int b) {
 *         debugTextArea.append(String.valueOf((char)b));
 *         debugTextArea.setCaretPosition(debugTextArea.getDocument().getLength());
 *     }
 * });
 *
 * TiedOutputStream tiedOutputStream = new TiedOutputStream(debugPrintStream);
 * TiedOutputStream.tieOutputStreams(tiedOutputStream);
 * debugFrame.addWindowListener(new java.awt.event.WindowAdapter() {
 *     *@Override
 *         public void windowClosed(java.awt.event.WindowEvent windowEvent) {
 *             TiedOutputStream.resetOutputStreams(tiedOutputStream);
 *         }
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
     *     <p>This needs to call tieOutputStreams and then
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

    public PrintStream getOriginalOutputStream() {
        return originalOutputStream;
    }

    public PrintStream getOriginalErrorStream() {
        return originalErrorStream;
    }

    /**
     * Ties the output stream to the standard output and error stream.
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
     * Resets the System.out and System.err to the standard output and error stream.
     */
    public void resetOutputStreams() {
        this.close();
        System.setOut(originalOutputStream);
        System.setErr(originalErrorStream);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

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