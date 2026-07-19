// Characterization tests for the XXE fixtures (default package).
// These prove the CURRENT vulnerable behavior: the DocumentBuilderFactory
// resolves external entities, so an entity pointing at a LOCAL file (file://,
// fully offline) is expanded into the parsed document. No network access.
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.w3c.dom.Document;

class XxeTest {

    private static final String SECRET = "XXE_LEAKED_SECRET_9d1f";

    private ByteArrayInputStream xmlReferencing(Path secretFile) {
        String xml =
                "<?xml version=\"1.0\"?>"
                        + "<!DOCTYPE data ["
                        + "<!ENTITY xxe SYSTEM \"" + secretFile.toUri() + "\">"
                        + "]>"
                        + "<data>&xxe;</data>";
        return new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
    }

    private Path writeSecret(Path dir) throws Exception {
        Path secret = dir.resolve("secret.txt");
        Files.writeString(secret, SECRET);
        return secret;
    }

    @Test
    void invoiceParserResolvesExternalEntity(@TempDir Path dir) throws Exception {
        Path secret = writeSecret(dir);
        Document doc = new xxe_invoice_parser().parse(xmlReferencing(secret));
        assertTrue(doc.getDocumentElement().getTextContent().contains(SECRET),
                "external entity was resolved -> XXE present");
    }

    @Test
    void partnerFeedResolvesExternalEntity(@TempDir Path dir) throws Exception {
        Path secret = writeSecret(dir);
        Document doc = new xxe_partner_feed().parse(xmlReferencing(secret));
        assertTrue(doc.getDocumentElement().getTextContent().contains(SECRET));
    }

    @Test
    void profileImportResolvesExternalEntity(@TempDir Path dir) throws Exception {
        Path secret = writeSecret(dir);
        Document doc = new xxe_profile_import().parse(xmlReferencing(secret));
        assertTrue(doc.getDocumentElement().getTextContent().contains(SECRET));
    }
}
