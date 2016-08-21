package com.github.trb.configure;

public interface Configurable {

	public void configure(Configuration conf);

	public Configuration getConfiguration();
}
