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
import com.shrevl.jshint.maven.plugin.format.OutputFormat;
import com.shrevl.jshint.maven.plugin.js.JSFile;

/**
 * Runs JSHint.
 * 
 * @goal jshint
 */
public class JSHintMojo extends AbstractMojo
{
	/**
	 * The source path to search.
	 * 
	 * @parameter property="jshint.jsSourceDirectory" default-value="${basedir}/src/main/webapp/js"
	 */
	private String jsSourceDirectory;

	/**
	 * The source path to search.
	 * 
	 * @parameter property="jshint.failOnError" default-value="false"
	 */
	private String failOnError;

	/**
	 * The path of the output file.
	 * 
	 * @parameter property="jshint.outputFile" default-value="${project.build.directory}/jshint.xml"
	 */
	private String outputFile;

	/**
	 * The format of the output.
	 * 
	 * @parameter property="jshint.outputFormat" default-value="jshint"
	 */
	private OutputFormat outputFormat;

	/**
	 * Options to be sent to JSHint.
	 * 
	 * @parameter
	 */
	private Map<String, String> options;

	/**
	 * Globals to be declared to JSHint.
	 * 
	 * @parameter
	 */
	private Map<String, String> globals;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException
	{
		File sourceDirectory = new File(jsSourceDirectory);

		if (!sourceDirectory.exists())
		{
			return;
		}

		IOFileFilter fileFilter = FileFilterUtils.and(FileFilterUtils.suffixFileFilter(".js"), FileFilterUtils
				.notFileFilter(FileFilterUtils.suffixFileFilter(".min.js")));

		Collection<File> files = FileUtils
				.listFiles(sourceDirectory, fileFilter, FileFilterUtils.directoryFileFilter());

		try
		{
			JSHint jsHint = new JSHint();

			List<JSFile> jsFiles = new ArrayList<JSFile>();
			for (File file : files)
			{
				jsFiles.add(JSFile.getFile(file.getAbsolutePath()));
			}

			Map<JSFile, List<Error>> errors = jsHint.run(jsFiles, options, globals);
			
			for (JSFile jsFile : errors.keySet()) {
				getLog().error(String.format("There are jshint errors in '%s' for more details see '%s'", jsFile.getPath(), outputFile));
				if (getLog().isDebugEnabled()) {
					
					for (Error error : errors.get(jsFile)) {
						getLog().error(String.format("Jshint error '%s'", error));
					}
					
				}
			}

			ErrorWriter writer = new ErrorWriter();
			writer.write(errors, outputFormat, outputFile);

			if (!errors.isEmpty() & "true".equals(failOnError)) {
				throw new MojoExecutionException("There are jshint errors!");
			}

		}
		catch (Exception e)
		{
			throw new MojoExecutionException("Failed to execute goal: jshint", e);
		}
	}

	public void setJsSourceDirectory(String jsSourceDirectory)
	{
		this.jsSourceDirectory = jsSourceDirectory;
	}

	public void setOutputFile(String outputFile)
	{
		this.outputFile = outputFile;
	}

	public void setOutputFormat(OutputFormat outputFormat)
	{
		this.outputFormat = outputFormat;
	}

	public void setFailOnError(String failOnError) 
	{
		this.failOnError = failOnError;
	}
	
}
