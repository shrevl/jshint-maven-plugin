package com.shrevl.jshint.maven.plugin.mojo;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.shrevl.jshint.maven.plugin.JSHint;
import com.shrevl.jshint.maven.plugin.js.JSFile;

/**
 * Runs JSHint.
 * 
 * @goal jshint
 */
public class JSHintGoal extends AbstractMojo
{
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException
	{
		try
		{
			JSHint jsHint = new JSHint();
			List<JSFile> files = new ArrayList<JSFile>();
			files.add(JSFile.getResource("/com/shrevl/jshint/test.js"));
			jsHint.run(files);
		}
		catch (Exception e)
		{
			throw new MojoExecutionException("Failed to execute goal", e);
		}
	}
}
