// dro1dDev - created: 2025-05-09

package com.everdro1d.libs.io;

import com.everdro1d.libs.io.SyncPipe;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SyncPipeTest {

    @Test
    void testSyncPipeWithByteArrayStreams() throws Exception {
        String input = "Test data for SyncPipe";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // Create and run SyncPipe
        SyncPipe syncPipe = new SyncPipe(inputStream, outputStream);
        Thread thread = new Thread(syncPipe);
        thread.start();
        thread.join();

        // Verify the output matches the input
        assertEquals(input, outputStream.toString());
    }
}