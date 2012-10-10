package com.shrevl.jshint.maven.plugin.mojo;

import java.net.URL;

import org.junit.Test;

import com.shrevl.jshint.maven.plugin.format.OutputFormat;

public class JSHintMojoTest
{
	@Test
	public void test() throws Exception
	{
		URL url = getClass().getClassLoader().getResource("js");
		
		JSHintMojo mojo = new JSHintMojo();
		mojo.setJsSourceDirectory(url.getPath());
		mojo.setOutputFile("target/test.xml");
		mojo.setOutputFormat(OutputFormat.jslint);
		mojo.execute();
	}
}
