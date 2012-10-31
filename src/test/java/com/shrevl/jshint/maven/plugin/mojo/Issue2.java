package com.shrevl.jshint.maven.plugin.mojo;

import java.net.URL;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.shrevl.jshint.maven.plugin.format.OutputFormat;
import com.shrevl.jshint.maven.plugin.mojo.mock.TestLog;

public class Issue2 {

	@Test(expected = Exception.class)
	public void doFail() throws Exception
	{
		URL url = getClass().getClassLoader().getResource("issue-2 /js");
		TestLog log = new TestLog();
		
		JSHintMojo mojo = new JSHintMojo();
		mojo.setJsSourceDirectory(url.getPath());
		mojo.setOutputFile("target/issue-1.xml");
		mojo.setOutputFormat(OutputFormat.jshint);
		mojo.setFailOnError("true");
		mojo.execute();
		
		Assert.fail();
	}

	@Test
	public void dontFail() throws Exception
	{
		URL url = getClass().getClassLoader().getResource("issue-1/js");
		TestLog log = new TestLog();
		
		JSHintMojo mojo = new JSHintMojo();
		mojo.setJsSourceDirectory(url.getPath());
		mojo.setOutputFile("target/issue-1.xml");
		mojo.setOutputFormat(OutputFormat.jshint);
		mojo.setFailOnError("false");
		mojo.setLog(log);
		mojo.execute();
		
		List<CharSequence> logging = log.getLogging();
		
		Assert.assertEquals(1, logging.size());
	}

}
