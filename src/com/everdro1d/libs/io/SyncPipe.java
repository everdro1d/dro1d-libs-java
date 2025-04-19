/**************************************************************************************************
 * dro1dDev 2024.                                                                                 *
 **************************************************************************************************/

package com.everdro1d.libs.io;

import java.io.InputStream;
import java.io.OutputStream;

public class SyncPipe implements Runnable
{
    public SyncPipe(InputStream iStream, OutputStream oStream) {
        iStream_ = iStream;
        oStream_ = oStream;
    }
    public void run() {
        try
        {
            final byte[] buffer = new byte[1024];
            for (int length; (length = iStream_.read(buffer)) != -1; )
            {
                oStream_.write(buffer, 0, length);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace(System.out);
            System.err.println("Error in SyncPipe: " + e.getMessage());
        }
    }
    private final OutputStream oStream_;
    private final InputStream iStream_;
}