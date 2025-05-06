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
    private final PrintStream sout;
    private final PrintStream serr;

    /**
     * Creates a new TiedOutputStream.
     * @param outputStream the output stream to tie to
     * <p>Note:</p>
     *     <p>This needs to call tieOutputStreams and then
     *        resetOutputStreams when finished. </p>
     * @see #tieOutputStreams(TiedOutputStream)
     * @see #resetOutputStreams(TiedOutputStream)
     */
    public TiedOutputStream(OutputStream outputStream) {
        super(outputStream);
        //save standard output
        sout = System.out;
        serr = System.err;
    }

    public PrintStream getOriginalOutputStream() {
        return sout;
    }

    public PrintStream getOriginalErrorStream() {
        return serr;
    }

    /**
     * Ties the output stream to the standard output and error stream.
     * @param tiedOutputStream the output stream to tie to
     */
    public static void tieOutputStreams(TiedOutputStream tiedOutputStream) {
        try {
            System.setErr(tiedOutputStream);
            System.setOut(tiedOutputStream);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    /**
     * Resets the System.out and System.err to the standard output and error stream.
     * @param tiedOutputStream the output stream to reset
     */
    public static void resetOutputStreams(TiedOutputStream tiedOutputStream) {
        tiedOutputStream.close();
        System.setOut(tiedOutputStream.getOriginalOutputStream());
        System.setErr(tiedOutputStream.getOriginalErrorStream());
    }

    //Override all the print methods
    @Override
    public void print(Object obj) {
        super.print(obj);
        sout.print(obj);
    }

    @Override
    public void println(String obj) {
        super.println(obj);
        sout.println(obj);
    }

    @Override
    public PrintStream printf(String format, Object... args) {
        super.printf(format, args);
        sout.printf(format, args);
        return this;
    }

    @Override
    public void println(Object args) {
        super.println(args);
        sout.println(args);
    }
}