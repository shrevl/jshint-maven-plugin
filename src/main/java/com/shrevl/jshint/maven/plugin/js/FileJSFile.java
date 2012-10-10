package com.shrevl.jshint.maven.plugin.js;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileJSFile extends JSFile
{
	public FileJSFile(String path)
	{
		super(path);
	}

	@Override
	protected InputStream getInputStream() throws IOException
	{
		return new FileInputStream(new File(getPath()));
	}
}
