// dro1dDev - created: 2025-05-06

    package com.everdro1d.libs.io;

    import org.junit.jupiter.api.AfterEach;
    import org.junit.jupiter.api.BeforeEach;
    import org.junit.jupiter.api.Test;

    import java.io.ByteArrayOutputStream;
    import java.io.PrintStream;

    import static org.junit.jupiter.api.Assertions.*;

    class TiedOutputStreamTest {

        private TiedOutputStream tiedOutputStream;
        private ByteArrayOutputStream tiedStreamOutput;
        private ByteArrayOutputStream originalOutContent;
        private ByteArrayOutputStream originalErrContent;
        private PrintStream out;
        private PrintStream err;

        @BeforeEach
        void setUp() {
            originalOutContent = new ByteArrayOutputStream();
            originalErrContent = new ByteArrayOutputStream();

            // Set System.out and System.err to custom streams
            out = new PrintStream(originalOutContent);
            err = new PrintStream(originalErrContent);
            System.setOut(out);
            System.setErr(err);

            // Tie the output streams with copy enabled
            tiedStreamOutput = new ByteArrayOutputStream();
            tiedOutputStream = new TiedOutputStream(new PrintStream(tiedStreamOutput));
            tiedOutputStream.tieOutputStreams(true);
        }

        @AfterEach
        void tearDown() {
            tiedOutputStream.resetOutputStreams();
        }

        @Test
        void testOutputToBothStreams() {
            System.out.println("Test output to System.out");
            System.err.println("Test output to System.err");

            String tiedOutput = tiedStreamOutput.toString();
            assertTrue(tiedOutput.contains("Test output to System.out"));
            assertTrue(tiedOutput.contains("Test output to System.err"));

            String originalOutOutput = originalOutContent.toString();
            assertTrue(originalOutOutput.contains("Test output to System.out"));
        }

        @Test
        void testNullInput() {
            System.out.println((String) null);

            String tiedOutput = tiedStreamOutput.toString();
            assertTrue(tiedOutput.contains("null"));

            String originalOutOutput = originalOutContent.toString();
            assertTrue(originalOutOutput.contains("null"));
        }

        @Test
        void testLargeDataPrint() {
            String largeData = "A".repeat(10_000);
            System.out.print(largeData);

            String tiedOutput = tiedStreamOutput.toString();
            assertEquals(largeData, tiedOutput);

            String originalOutOutput = originalOutContent.toString();
            assertEquals(largeData, originalOutOutput);
        }

        @Test
        void testLargeDataPrintln() {
            String largeData = "A".repeat(10_000);
            System.out.println(largeData);

            String tiedOutput = tiedStreamOutput.toString();
            assertEquals(largeData + System.lineSeparator(), tiedOutput);

            String originalOutOutput = originalOutContent.toString();
            assertEquals(largeData + System.lineSeparator(), originalOutOutput);

        }

        @Test
        void testLargeDataPrintf() {
            String format = "Data: %s";
            String largeData = "A".repeat(10_000);
            System.out.printf(format, largeData);

            String tiedOutput = tiedStreamOutput.toString();
            assertEquals(String.format(format, largeData), tiedOutput);

            String originalOutOutput = originalOutContent.toString();
            assertEquals(String.format(format, largeData), originalOutOutput);
        }

        @Test
        void testLargeDataWrite() {
            byte[] largeData = "A".repeat(10_000).getBytes();
            System.out.write(largeData, 0, largeData.length);

            String tiedOutput = tiedStreamOutput.toString();
            assertEquals(new String(largeData), tiedOutput);

            String originalOutOutput = originalOutContent.toString();
            assertEquals(new String(largeData), originalOutOutput);
        }

        @Test
        void testToggleEnabled() {
            tiedOutputStream.setEnabled(false);
            System.out.println("This should only appear in the original stream.");

            String tiedOutput = tiedStreamOutput.toString();
            assertFalse(tiedOutput.contains("This should only appear in the original stream."));

            String originalOutOutput = originalOutContent.toString();
            assertTrue(originalOutOutput.contains("This should only appear in the original stream."));

            tiedOutputStream.setEnabled(true);
            System.out.println("This should appear in both streams.");

            tiedOutput = tiedStreamOutput.toString();
            assertTrue(tiedOutput.contains("This should appear in both streams."));

            originalOutOutput = originalOutContent.toString();
            assertTrue(originalOutOutput.contains("This should appear in both streams."));
        }

        @Test
        void testStreamReset() {
            tiedOutputStream.resetOutputStreams();
            System.out.println("This should only appear in the original stream after close.");

            String tiedOutput = tiedStreamOutput.toString();
            assertFalse(tiedOutput.contains("This should only appear in the original stream after close."));

            String originalOutOutput = originalOutContent.toString();
            assertTrue(originalOutOutput.contains("This should only appear in the original stream after close."));
        }

        @Test
        void testResetOutputStreams() {
            tiedOutputStream.resetOutputStreams();
            System.out.println("This should only appear in the original stream after reset.");

            String tiedOutput = tiedStreamOutput.toString();
            assertFalse(tiedOutput.contains("This should only appear in the original stream after reset."));

            String originalOutOutput = originalOutContent.toString();
            assertTrue(originalOutOutput.contains("This should only appear in the original stream after reset."));
        }

        @Test
        void testGetOriginalOutputStream() {
            assertNotNull(tiedOutputStream.getOriginalOutputStream());
            assertEquals(out, tiedOutputStream.getOriginalOutputStream());
        }

        @Test
        void testGetOriginalErrorStream() {
            assertNotNull(tiedOutputStream.getOriginalErrorStream());
            assertEquals(err, tiedOutputStream.getOriginalErrorStream());
        }

        @Test
        void testSetEnabled() {
            tiedOutputStream.setEnabled(false);
            assertFalse(tiedOutputStream.isEnabled());

            tiedOutputStream.setEnabled(true);
            assertTrue(tiedOutputStream.isEnabled());
        }

        @Test
        void testIsEnabled() {
            assertTrue(tiedOutputStream.isEnabled());

            tiedOutputStream.setEnabled(false);
            assertFalse(tiedOutputStream.isEnabled());
        }

        @Test
        void testCopyEnabled() {
            tiedOutputStream.setCopyEnabled(true); // Enable copy
            System.out.println("This should appear in both streams.");

            String tiedOutput = tiedStreamOutput.toString();
            assertTrue(tiedOutput.contains("This should appear in both streams."));

            String originalOutOutput = originalOutContent.toString();
            assertTrue(originalOutOutput.contains("This should appear in both streams."));
        }

        @Test
        void testCopyDisabled() {
            tiedOutputStream.setCopyEnabled(false); // Disable copy
            System.out.println("This should only appear in the tied stream.");

            String tiedOutput = tiedStreamOutput.toString();
            assertTrue(tiedOutput.contains("This should only appear in the tied stream."));

            String originalOutOutput = originalOutContent.toString();
            assertFalse(originalOutOutput.contains("This should only appear in the tied stream."));
        }
    }
