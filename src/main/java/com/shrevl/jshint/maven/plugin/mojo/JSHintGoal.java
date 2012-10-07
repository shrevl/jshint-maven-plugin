package com.shrevl.jshint.maven.plugin.mojo;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.shrevl.jshint.maven.plugin.Error;
import com.shrevl.jshint.maven.plugin.ErrorWriter;
import com.shrevl.jshint.maven.plugin.JSHint;
import com.shrevl.jshint.maven.plugin.JSHintErrorWriter;
import com.shrevl.jshint.maven.plugin.js.JSFile;

/**
 * Runs JSHint.
 * 
 * @goal jshint
 */
public class JSHintGoal extends AbstractMojo
{
	/**
	 * The source path to search.
	 * 
	 * @parameter expression="${jshint.jsSourceDirectory}" default-value="${basedir}/src/main/webapp/js"
	 */
	private String jsSourceDirectory;

	/**
	 * The path of the output file.
	 * 
	 * @parameter expression="${jshint.outputFile}" default-value="${project.build.directory}/jslint.xml"
	 */
	private String outputFile;

	/**
	 * @parameter
	 */
	private Map<String, String> options;
	
	/**
	 * @parameter
	 */
	private Map<String, String> globals;
	
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException
	{
		File sourceDirectory = new File(jsSourceDirectory);

		IOFileFilter fileFilter = FileFilterUtils.and(FileFilterUtils.suffixFileFilter(".js"), FileFilterUtils.notFileFilter(FileFilterUtils
				.suffixFileFilter(".min.js")));

		Collection<File> files = FileUtils.listFiles(sourceDirectory, fileFilter, FileFilterUtils.directoryFileFilter());

		try
		{
			JSHint jsHint = new JSHint();

			List<JSFile> jsFiles = new ArrayList<JSFile>();
			for (File file : files)
			{
				jsFiles.add(JSFile.getFile(file.getAbsolutePath()));
			}

			Map<JSFile, List<Error>> errors = jsHint.run(jsFiles, options, globals);
			ErrorWriter writer = new JSHintErrorWriter();
			writer.write(errors, outputFile);
		}
		catch (Exception e)
		{
			throw new MojoExecutionException("Failed to execute goal: jshint", e);
		}
	}
}
