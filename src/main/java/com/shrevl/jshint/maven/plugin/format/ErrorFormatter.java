package com.shrevl.jshint.maven.plugin.format;

import java.util.List;
import java.util.Map;

import com.shrevl.jshint.maven.plugin.Error;
import com.shrevl.jshint.maven.plugin.js.JSFile;

/**
 * @author Lee Shreve, Estenda Solutions, Inc.
 *
 */
public interface ErrorFormatter<T>
{
	public T format(Map<JSFile, List<Error>> errors);
}
