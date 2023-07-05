package com.newrelic.agent.android;
final class NewRelicConfig {
	static final String VERSION = "6.0.0";
	static final String BUILD_ID = "f5b03c80-8cfb-4320-8950-e814901363ed";
	static final Boolean OBFUSCATED = false;
	static final String MAP_PROVIDER = "r8";
	public static String getBuildId() {
		return BUILD_ID;
	}
}
