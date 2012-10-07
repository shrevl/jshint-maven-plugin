package com.shrevl.jshint.maven.plugin.js;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;

public abstract class JSFile
{
	private final String path;

	public JSFile(String path)
	{
		this.path = path;
	}

	public static JSFile getResource(String path)
	{
		return new ResourceJSFile(path);
	}

	public static JSFile getFile(String path)
	{
		return new FileJSFile(path);
	}

	public String getPath()
	{
		return path;
	}

	protected abstract InputStream getInputStream() throws IOException;

	public Reader getReader() throws IOException
	{
		return new InputStreamReader(getInputStream());
	}

	public String getSource() throws IOException
	{
		Reader reader = getReader();
		BufferedReader br = new BufferedReader(reader);
		StringWriter sw = new StringWriter();
		char[] buffer = new char[1024];
		int read = 0;
		while ((read = br.read(buffer)) >= 0)
		{
			sw.write(buffer, 0, read);
		}
		return sw.toString();
	}
}
