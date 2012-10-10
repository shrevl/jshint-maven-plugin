package com.shrevl.jshint.maven.plugin.format;


public class ErrorFormatterFactory
{
	public ErrorFormatter<?> getErrorFormatter(OutputFormat format)
	{
		switch (format)
		{
			case jshint:
			{
				return new JSHintErrorFormatter();
			}
			case jslint:
			{
				return new JSLintErrorFormatter();
			}
		}
		
		throw new IllegalArgumentException("Unexpected OutputFormat: " + format);
	}
}
