package com.shrevl.jshint.maven.plugin;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.shrevl.jshint.maven.plugin.format.ErrorFormatter;
import com.shrevl.jshint.maven.plugin.format.ErrorFormatterFactory;
import com.shrevl.jshint.maven.plugin.format.OutputFormat;
import com.shrevl.jshint.maven.plugin.js.JSFile;

public class ErrorWriter
{
	public void write(Map<JSFile, List<Error>> errors, OutputFormat format, String outputFile)
	{
		ErrorFormatterFactory factory = new ErrorFormatterFactory();
		ErrorFormatter<?> formatter = factory.getErrorFormatter(format);
		Object output = formatter.format(errors);

		try
		{
			JAXBContext context = JAXBContext.newInstance(output.getClass());
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			java.io.File file = new java.io.File(outputFile);
			if (!file.exists())
			{
				file.createNewFile();
			}
			marshaller.marshal(output, new FileOutputStream(file));
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
