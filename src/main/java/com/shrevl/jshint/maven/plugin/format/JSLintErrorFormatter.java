package com.shrevl.jshint.maven.plugin.format;

import java.util.List;
import java.util.Map;

import com.shrevl.jshint.maven.plugin.Error;
import com.shrevl.jshint.maven.plugin.jaxb.jslint.File;
import com.shrevl.jshint.maven.plugin.jaxb.jslint.Issue;
import com.shrevl.jshint.maven.plugin.jaxb.jslint.Jslint;
import com.shrevl.jshint.maven.plugin.jaxb.jslint.ObjectFactory;
import com.shrevl.jshint.maven.plugin.js.JSFile;

public class JSLintErrorFormatter implements ErrorFormatter<Jslint>
{
	@Override
	public Jslint format(Map<JSFile, List<Error>> errors)
	{
		ObjectFactory factory = new ObjectFactory();
		Jslint jslint = factory.createJslint();
		List<File> files = jslint.getFile();
		for (JSFile jsFile : errors.keySet())
		{
			File file = factory.createFile();
			file.setName(jsFile.getPath());
			files.add(file);

			List<Issue> issues = file.getIssue();
			List<Error> jsErrors = errors.get(jsFile);
			for (Error error : jsErrors)
			{
				Issue issue = factory.createIssue();
				issue.setChar(error.getCharacter());
				issue.setEvidence(error.getEvidence());
				issue.setLine(error.getLine());
				issue.setReason(error.getReason());
				issues.add(issue);
			}
		}
		return jslint;
	}
}
