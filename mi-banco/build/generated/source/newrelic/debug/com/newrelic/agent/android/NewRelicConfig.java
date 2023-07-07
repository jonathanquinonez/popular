package com.newrelic.agent.android;
final class NewRelicConfig {
	static final String VERSION = "6.0.0";
	static final String BUILD_ID = "979fe49a-6f77-43e2-91ba-3afefa3afc59";
	static final Boolean OBFUSCATED = false;
	static final String MAP_PROVIDER = "r8";
	public static String getBuildId() {
		return BUILD_ID;
	}
}
