package com.newrelic.agent.android;
final class NewRelicConfig {
	static final String VERSION = "6.0.0";
	static final String BUILD_ID = "8fe3746b-aeca-4b44-9053-89097ed0923d";
	static final Boolean OBFUSCATED = false;
	static final String MAP_PROVIDER = "r8";
	public static String getBuildId() {
		return BUILD_ID;
	}
}
