using Xunit;

namespace FixtureTests;

// Characterization tests for the intentionally-vulnerable CodeQL SAST fixtures.
// These tests DOCUMENT the existing (insecure) behavior so it stays stable.
// They must NOT be treated as an endorsement of the behavior; the fixtures are
// deliberately vulnerable and must remain so for SAST baseline testing.
// All tests are offline and deterministic: SqlCommand is only constructed,
// never executed against a database.
public class FixtureTests
{
    // --- SQL Injection (CWE-89) ------------------------------------------
    // Characterization: raw input is concatenated straight into the SQL text.

    [Fact]
    public void SqlInjectionCustomer_ConcatenatesRawInputIntoCommandText()
    {
        var cmd = new SqlInjectionCustomer().Build("x' OR 1=1");
        Assert.Contains("x' OR 1=1", cmd.CommandText);
    }

    [Fact]
    public void SqlInjectionAudit_ConcatenatesRawInputIntoCommandText()
    {
        var cmd = new SqlInjectionAudit().Build("x' OR 1=1");
        Assert.Contains("x' OR 1=1", cmd.CommandText);
    }

    [Fact]
    public void SqlInjectionInvoice_ConcatenatesRawInputIntoCommandText()
    {
        var cmd = new SqlInjectionInvoice().Build("x' OR 1=1");
        Assert.Contains("x' OR 1=1", cmd.CommandText);
    }

    // --- Hardcoded Credentials (CWE-798) ---------------------------------
    // Characterization: the secret/token is a hardcoded literal in source.

    [Fact]
    public void HardcodedSecretApi_ReturnsHardcodedSecret()
    {
        Assert.Equal("P@ssw0rd-Do-Not-Use-12345", new HardcodedSecretApi().GetSecret());
    }

    [Fact]
    public void HardcodedSecretDatabase_ReturnsHardcodedSecret()
    {
        Assert.Equal("P@ssw0rd-Do-Not-Use-12345", new HardcodedSecretDatabase().GetSecret());
    }

    [Fact]
    public void HardcodedSecretStorage_ReturnsHardcodedSecret()
    {
        Assert.Equal("P@ssw0rd-Do-Not-Use-12345", new HardcodedSecretStorage().GetSecret());
    }

    [Fact]
    public void HardcodedSecretPipeline_ReturnsHardcodedToken()
    {
        Assert.Equal("ghp_example_static_pipeline_token_123456", new HardcodedSecretPipeline().GetToken());
    }

    // --- Weak Cryptography (CWE-327) -------------------------------------
    // Characterization: a weak hash (MD5 / SHA1) is computed over the input.

    [Fact]
    public void WeakCryptoMd5_ProducesNonEmptyDigest()
    {
        var digest = new WeakCryptoMd5().Digest("sample-input");
        Assert.NotNull(digest);
        Assert.Equal(16, digest.Length); // MD5 => 128-bit / 16-byte digest
    }

    [Fact]
    public void WeakCryptoSha1_ProducesNonEmptyDigest()
    {
        var digest = new WeakCryptoSha1().Digest("sample-input");
        Assert.NotNull(digest);
        Assert.Equal(20, digest.Length); // SHA1 => 160-bit / 20-byte digest
    }
}
