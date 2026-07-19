// Characterization tests for unsafe-deserialization fixtures (default package).
// They prove the CURRENT vulnerable behavior: request-controlled bytes are
// handed straight to ObjectInputStream.readObject(). Fully offline: we feed a
// benign serialized payload and confirm it is deserialized without validation.
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import org.junit.jupiter.api.Test;

class UnsafeDeserializationTest {

    private static byte[] serialize(Object o) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(o);
        }
        return bos.toByteArray();
    }

    @Test
    void cookieDeserializesBase64RequestParameter() throws Exception {
        String marker = "attacker-controlled-object";
        String encoded = Base64.getEncoder().encodeToString(serialize(marker));

        javax.servlet.http.HttpServletRequest req =
                mock(javax.servlet.http.HttpServletRequest.class);
        when(req.getParameter("session")).thenReturn(encoded);

        Object result = new unsafe_deserialization_cookie().deserialize(req);
        // The request-supplied bytes were deserialized with no allow-list.
        assertEquals(marker, result);
    }

    @Test
    void requestDeserializesBodyInputStream() throws Exception {
        String marker = "body-controlled-object";
        byte[] bytes = serialize(marker);

        javax.servlet.http.HttpServletRequest req =
                mock(javax.servlet.http.HttpServletRequest.class);
        when(req.getInputStream()).thenReturn(new ByteArrayServletInputStream(bytes));

        Object result = new unsafe_deserialization_request().deserialize(req);
        assertTrue(result instanceof String);
        assertEquals(marker, result);
    }

    /** Minimal offline ServletInputStream over an in-memory byte array. */
    private static final class ByteArrayServletInputStream extends ServletInputStream {
        private final ByteArrayInputStream delegate;

        ByteArrayServletInputStream(byte[] data) {
            this.delegate = new ByteArrayInputStream(data);
        }

        @Override
        public int read() {
            return delegate.read();
        }

        @Override
        public boolean isFinished() {
            return delegate.available() == 0;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
            // no-op: synchronous reads only
        }
    }
}
