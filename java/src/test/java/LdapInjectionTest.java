// Characterization tests for the LDAP-injection fixtures (default package).
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class LdapInjectionTest {

    private static final String PAYLOAD = "*)(uid=*))(|(uid=*";

    @Test
    void peopleBuildsFilterFromTaintedValue() throws Exception {
        javax.servlet.http.HttpServletRequest req =
                mock(javax.servlet.http.HttpServletRequest.class);
        DirContext context = mock(DirContext.class);
        when(req.getParameter("value")).thenReturn(PAYLOAD);
        when(context.search(anyString(), anyString(), any(SearchControls.class))).thenReturn(null);

        new ldap_injection_people().search(context, req);

        ArgumentCaptor<String> filter = ArgumentCaptor.forClass(String.class);
        verify(context).search(anyString(), filter.capture(), any(SearchControls.class));
        assertTrue(filter.getValue().contains(PAYLOAD),
                "tainted value should flow unsanitized into the LDAP filter");
        assertTrue(filter.getValue().startsWith("(uid="));
    }

    @Test
    void groupsBuildsFilterFromTaintedValue() throws Exception {
        javax.servlet.http.HttpServletRequest req =
                mock(javax.servlet.http.HttpServletRequest.class);
        DirContext context = mock(DirContext.class);
        when(req.getParameter("value")).thenReturn(PAYLOAD);
        when(context.search(anyString(), anyString(), any(SearchControls.class))).thenReturn(null);

        new ldap_injection_groups().search(context, req);

        ArgumentCaptor<String> filter = ArgumentCaptor.forClass(String.class);
        verify(context).search(anyString(), filter.capture(), any(SearchControls.class));
        assertTrue(filter.getValue().contains(PAYLOAD));
    }
}
