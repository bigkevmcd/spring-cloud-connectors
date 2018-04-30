package org.springframework.cloud.heroku;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.Test;

/**
 * @author Ramnivas Laddad
 */
public class HerokuConnectorApplicationTest extends AbstractHerokuConnectorTest {

	@Test
	public void isInMatchingEnvironment() {
		when(mockEnvironment.getEnvValue("DYNO")).thenReturn("web.1");
		assertTrue(testCloudConnector.isInMatchingCloud());

		when(mockEnvironment.getEnvValue("DYNO")).thenReturn(null);
		assertFalse(testCloudConnector.isInMatchingCloud());
	}

	@Test
	public void applicationInstanceInfo() {
		when(mockEnvironment.getEnvValue("SPRING_CLOUD_APP_NAME")).thenReturn("myapp");
		when(mockEnvironment.getEnvValue("DYNO")).thenReturn("web.1");
		when(mockEnvironment.getEnvValue("PORT")).thenReturn(Integer.toString(port));
		when(mockEnvironment.getHost()).thenReturn(hostname);

		assertEquals("myapp", testCloudConnector.getApplicationInstanceInfo().getAppId());
		assertEquals("web.1", testCloudConnector.getApplicationInstanceInfo().getInstanceId());
		Map<String, Object> appProps = testCloudConnector.getApplicationInstanceInfo().getProperties();
		assertEquals(hostname, appProps.get("host"));
		assertEquals(Integer.toString(port), appProps.get("port"));
	}

	@Test
	public void applicationInstanceInfoNoSpringCloudAppName() {
		when(mockEnvironment.getEnvValue("DYNO")).thenReturn("web.1");
		when(mockEnvironment.getEnvValue("PORT")).thenReturn(Integer.toString(port));
		when(mockEnvironment.getHost()).thenReturn(hostname);
		assertEquals("<unknown>", testCloudConnector.getApplicationInstanceInfo().getAppId());
		assertEquals("web.1", testCloudConnector.getApplicationInstanceInfo().getInstanceId());
	}

	@Test
	public void applicationInstanceInfoWithHerokuDynoMetadata() {
		when(mockEnvironment.getEnvValue("HEROKU_APP_NAME")).thenReturn("myapp");
		when(mockEnvironment.getEnvValue("HEROKU_APP_ID")).thenReturn("9daa2797-e49b-4624-932f-ec3f9688e3da");
		when(mockEnvironment.getEnvValue("HEROKU_DYNO_ID")).thenReturn("1vac4117-c29f-4312-521e-ba4d8638c1ac");
		when(mockEnvironment.getEnvValue("HEROKU_RELEASE_CREATED_AT")).thenReturn("2015-04-02T18:00:42Z");
		when(mockEnvironment.getEnvValue("HEROKU_RELEASE_VERSION")).thenReturn("v42");
		when(mockEnvironment.getEnvValue("HEROKU_SLUG_COMMIT")).thenReturn("2c3a0b24069af49b3de35b8e8c26765c1dba9ff0");
		when(mockEnvironment.getEnvValue("HEROKU_SLUG_DESCRIPTION")).thenReturn("Deploy 2c3a0b2");

		assertEquals("myapp", testCloudConnector.getApplicationInstanceInfo().getAppId());
		Map<String, Object> appProps = testCloudConnector.getApplicationInstanceInfo().getProperties();
		assertEquals("9daa2797-e49b-4624-932f-ec3f9688e3da", appProps.get("heroku.app.id"));
		assertEquals("1vac4117-c29f-4312-521e-ba4d8638c1ac", appProps.get("heroku.dyno.id"));
		assertEquals("2015-04-02T18:00:42Z", appProps.get("heroku.release.created_at"));
		assertEquals("v42", appProps.get("heroku.release.version"));
		assertEquals("2c3a0b24069af49b3de35b8e8c26765c1dba9ff0", appProps.get("heroku.slug.commit"));
		assertEquals("Deploy 2c3a0b2", appProps.get("heroku.slug.description"));
	}
}
