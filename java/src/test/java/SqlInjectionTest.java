// Characterization tests (no package -> default package, so the default-package
// fixture classes can be referenced directly). These document the CURRENT
// (intentionally vulnerable) behavior; they do NOT fix the fixtures.
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.Statement;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class SqlInjectionTest {

    private static final String PAYLOAD = "x' OR '1'='1";

    @Test
    void customerConcatenatesTaintedValueIntoSql() throws Exception {
        javax.servlet.http.HttpServletRequest req =
                mock(javax.servlet.http.HttpServletRequest.class);
        Connection connection = mock(Connection.class);
        Statement statement = mock(Statement.class);
        when(req.getParameter("value")).thenReturn(PAYLOAD);
        when(connection.createStatement()).thenReturn(statement);

        new sql_injection_customer().find(connection, req);

        ArgumentCaptor<String> sql = ArgumentCaptor.forClass(String.class);
        org.mockito.Mockito.verify(statement).executeQuery(sql.capture());
        assertTrue(sql.getValue().contains(PAYLOAD),
                "tainted value should flow unsanitized into the SQL string");
    }

    @Test
    void auditConcatenatesTaintedValueIntoSql() throws Exception {
        javax.servlet.http.HttpServletRequest req =
                mock(javax.servlet.http.HttpServletRequest.class);
        Connection connection = mock(Connection.class);
        Statement statement = mock(Statement.class);
        when(req.getParameter("value")).thenReturn(PAYLOAD);
        when(connection.createStatement()).thenReturn(statement);

        new sql_injection_audit().find(connection, req);

        ArgumentCaptor<String> sql = ArgumentCaptor.forClass(String.class);
        org.mockito.Mockito.verify(statement).executeQuery(sql.capture());
        assertTrue(sql.getValue().contains(PAYLOAD));
    }

    @Test
    void orderConcatenatesTaintedValueIntoSql() throws Exception {
        javax.servlet.http.HttpServletRequest req =
                mock(javax.servlet.http.HttpServletRequest.class);
        Connection connection = mock(Connection.class);
        Statement statement = mock(Statement.class);
        when(req.getParameter("value")).thenReturn(PAYLOAD);
        when(connection.createStatement()).thenReturn(statement);

        new sql_injection_order().find(connection, req);

        ArgumentCaptor<String> sql = ArgumentCaptor.forClass(String.class);
        org.mockito.Mockito.verify(statement).executeQuery(sql.capture());
        assertTrue(sql.getValue().contains(PAYLOAD));
    }

    @Test
    void billingConcatenatesTaintedAccountIntoSql() throws Exception {
        javax.servlet.http.HttpServletRequest req =
                mock(javax.servlet.http.HttpServletRequest.class);
        Connection connection = mock(Connection.class);
        Statement statement = mock(Statement.class);
        // Billing reads the "account" parameter, not "value".
        when(req.getParameter("account")).thenReturn(PAYLOAD);
        when(connection.createStatement()).thenReturn(statement);

        new sql_injection_billing().find(connection, req);

        ArgumentCaptor<String> sql = ArgumentCaptor.forClass(String.class);
        org.mockito.Mockito.verify(statement).executeQuery(sql.capture());
        assertTrue(sql.getValue().contains(PAYLOAD));
        assertTrue(sql.getValue().contains("invoices"));
    }
}
