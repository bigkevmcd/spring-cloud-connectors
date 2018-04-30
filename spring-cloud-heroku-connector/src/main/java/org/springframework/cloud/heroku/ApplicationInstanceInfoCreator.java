package org.springframework.cloud.heroku;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.springframework.cloud.app.ApplicationInstanceInfo;
import org.springframework.cloud.app.BasicApplicationInstanceInfo;
import org.springframework.cloud.util.EnvironmentAccessor;

/**
 * Application instance info creator.
 * <p>
 * Relies on SPRING_CLOUD_APP_NAME environment being set (using commands such as
 * <code>heroku config:add SPRING_CLOUD_APP_NAME=myappname --app myappname</code>
 * Alternatively, enable the dyno-metadata labs feature.
 * <code>heroku labs:enable runtime-dyno-metadata --app myappname</code>
 *  
 * @author Ramnivas Laddad
 *
 */
public class ApplicationInstanceInfoCreator {
	public static final String NO_APP_NAME_MSG = "Environment variable SPRING_CLOUD_APP_NAME not set and no HEROKU_APP_NAME. App name set to <unknown>";
	private static Logger logger = Logger.getLogger(ApplicationInstanceInfoCreator.class.getName());
	
	private EnvironmentAccessor environment;

	public ApplicationInstanceInfoCreator(EnvironmentAccessor environmentAccessor) {
		this.environment = environmentAccessor;
	}
	
	public ApplicationInstanceInfo createApplicationInstanceInfo() {
		String appname = getEnvValue("SPRING_CLOUD_APP_NAME");
		if (appname == null) {
			appname = getEnvValue("HEROKU_APP_NAME");
			if (appname == null) {
				logger.warning(NO_APP_NAME_MSG);
				appname = "<unknown>";
			}
		}
		
		String dyno = getEnvValue("DYNO");
		Map<String, Object> appProperties = getDynoMetadata();

		return new BasicApplicationInstanceInfo(dyno, appname, appProperties);
	}

	private Map<String, Object> getDynoMetadata() {
		Map<String,Object> appProperties = new HashMap<String, Object>();
		appProperties.put("port", getEnvValue("PORT"));
		appProperties.put("host", environment.getHost());
		appProperties.put("heroku.app.id", getEnvValue("HEROKU_APP_ID"));
		appProperties.put("heroku.dyno.id", getEnvValue("HEROKU_DYNO_ID"));
		appProperties.put("heroku.release.version", getEnvValue("HEROKU_RELEASE_VERSION"));
		appProperties.put("heroku.release.created_at", getEnvValue("HEROKU_RELEASE_CREATED_AT"));
		appProperties.put("heroku.slug.commit", getEnvValue("HEROKU_SLUG_COMMIT"));
		appProperties.put("heroku.slug.description", getEnvValue("HEROKU_SLUG_DESCRIPTION"));
		return appProperties;
	}

	private String getEnvValue(String name) {
		return environment.getEnvValue(name);
	}
}
