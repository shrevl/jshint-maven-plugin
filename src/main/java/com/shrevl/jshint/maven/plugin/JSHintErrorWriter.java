package com.shrevl.jshint.maven.plugin;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.shrevl.jshint.maven.plugin.jaxb.jslint.File;
import com.shrevl.jshint.maven.plugin.jaxb.jslint.Issue;
import com.shrevl.jshint.maven.plugin.jaxb.jslint.Jslint;
import com.shrevl.jshint.maven.plugin.jaxb.jslint.ObjectFactory;
import com.shrevl.jshint.maven.plugin.js.JSFile;

public class JSHintErrorWriter implements ErrorWriter
{
	@Override
	public void write(Map<JSFile, List<Error>> errors, String outputFile)
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

		try
		{
			JAXBContext context = JAXBContext.newInstance(Jslint.class);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			java.io.File file = new java.io.File(outputFile);
			if (!file.exists())
			{
				file.createNewFile();
			}
			marshaller.marshal(jslint, new FileOutputStream(file));
		}
		catch (JAXBException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
