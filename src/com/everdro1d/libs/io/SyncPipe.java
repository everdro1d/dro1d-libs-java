// dro1dDev - created: 2024-03-03

package com.everdro1d.libs.io;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * A utility class that facilitates the transfer of data between an InputStream and an OutputStream.
 * <p>This class is designed to be used in scenarios where data needs to be piped from one stream to another,
 * such as redirecting process output or handling inter-thread communication.</p>
 *
 * <p><strong>Usage:</strong> Create an instance of the class with the desired InputStream and OutputStream,
 * and run it in a separate thread to handle the data transfer asynchronously.</p>
 * <p>Example from {@link com.everdro1d.libs.core.Utils#runCommand(List, String, boolean, boolean) Utils.runCommand()}:</p>
 * <blockquote><pre>
 * ProcessBuilder pb = new ProcessBuilder(List.of("echo", "SyncPipe Example Text"));
 * Process p;
 * try {
 *     p = pb.start();
 *     new Thread(new SyncPipe(p.getErrorStream(), System.err)).start();
 *     try (Scanner scanner = new Scanner(p.getInputStream())) {
 *         while (scanner.hasNextLine()) {
 *             String line = scanner.nextLine();
 *             if (!line.isEmpty()) {
 *                 System.out.println(line);
 *             }
 *         }
 *     }
 *     p.waitFor();
 *     System.out.println(p.exitValue());
 * } catch (Exception e) {
 *     e.printStackTrace(System.err);
 * }
 * </pre></blockquote>
 *
 * <h2>Features:</h2>
 * <ul>
 *     <li>Efficiently transfers data using a buffer.</li>
 *     <li>Handles exceptions and logs errors to {@code System.err}.</li>
 * </ul>
 */
public class SyncPipe implements Runnable {
    private final OutputStream oStream;
    private final InputStream iStream;

    /**
     * Constructs a SyncPipe instance with the specified InputStream and OutputStream.
     * @param iStream the InputStream to read data from
     * @param oStream the OutputStream to write data to
     */
    public SyncPipe(InputStream iStream, OutputStream oStream) {
        this.iStream = iStream;
        this.oStream = oStream;
    }

    /**
     * Transfers data from the InputStream to the OutputStream.
     * <p>This method reads data in chunks using a buffer and writes it to the OutputStream.
     * It runs in a loop until the InputStream is fully read.</p>
     * <p>Any exceptions encountered during the process are logged to {@code System.err}.</p>
     */
    public void run() {
        try {
            final byte[] buffer = new byte[1024];
            for (int length; (length = iStream.read(buffer)) != -1; ) {
                oStream.write(buffer, 0, length);
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
            System.err.println("Error in SyncPipe: " + e.getMessage());
        }
    }
}