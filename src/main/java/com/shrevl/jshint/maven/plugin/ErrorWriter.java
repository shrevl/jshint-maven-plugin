package com.shrevl.jshint.maven.plugin;

import java.util.List;
import java.util.Map;

import com.shrevl.jshint.maven.plugin.js.JSFile;

public interface ErrorWriter
{
	void write(Map<JSFile, List<Error>> errors, OutputFormat format, String outputFile);
}
