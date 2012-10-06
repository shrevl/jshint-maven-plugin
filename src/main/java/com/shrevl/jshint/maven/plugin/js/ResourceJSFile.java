package com.shrevl.jshint.maven.plugin.js;

import java.io.InputStream;

public class ResourceJSFile extends JSFile
{

	public ResourceJSFile(String path)
	{
		super(path);
	}

	@Override
	protected InputStream getInputStream()
	{
		return ClassLoader.getSystemResourceAsStream(getPath());
	}
}
