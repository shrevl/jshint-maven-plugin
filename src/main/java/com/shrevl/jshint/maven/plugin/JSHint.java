package com.shrevl.jshint.maven.plugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import com.shrevl.jshint.maven.plugin.js.JSFile;

public class JSHint
{
	private final Context context;
	private final ScriptableObject global;

	private static final String DEFAULT_JS_HINT_PATH = "/com/shrevl/jshint/maven/plugin/jshint.js";
	private static final String JS_HINT_SCRIPT_PATH = "/com/shrevl/jshint/maven/plugin/jshint-script.js";

	public JSHint() throws IOException
	{
		context = Context.enter();
		context.setLanguageVersion(Context.VERSION_1_6);
		global = context.initStandardObjects();

		defineFunctions();
		JSFile jshint = JSFile.getResource(DEFAULT_JS_HINT_PATH);
		context.evaluateReader(global, jshint.getReader(), jshint.getPath(), 0, null);

		global.defineProperty("source", "", ScriptableObject.DONTENUM);
		global.defineProperty("errors", context.newArray(global, 0), ScriptableObject.DONTENUM);
		global.defineProperty("options", context.newObject(global), ScriptableObject.DONTENUM);
		global.defineProperty("globals", context.newObject(global), ScriptableObject.DONTENUM);
	}

	private void defineFunctions()
	{
		global.defineFunctionProperties(new String[] { "print" }, JSHint.class, ScriptableObject.DONTENUM);
	}

	public Map<JSFile, List<Error>> run(List<JSFile> files, Map<String, String> options, Map<String, String> globals)
			throws Exception
	{
		Map<JSFile, List<Error>> errors = new HashMap<JSFile, List<Error>>();
		JSFile jshintScript = JSFile.getResource(JS_HINT_SCRIPT_PATH);

		global.put("options", global, convert(options));
		global.put("globals", global, convert(globals));

		for (JSFile file : files)
		{
			global.put("errors", global, context.newArray(global, 0));
			global.put("source", global, file.getSource());
			context.evaluateReader(global, jshintScript.getReader(), jshintScript.getPath(), 0, null);
			errors.put(file, getErrors());
		}

		return errors;
	}

	private ScriptableObject convert(Map<String, String> properties)
	{
		ScriptableObject scriptable = (ScriptableObject) context.newObject(global);

		if (properties == null)
		{
			return scriptable;
		}

		for (String key : properties.keySet())
		{
			String value = properties.get(key);
			scriptable.put(key, scriptable, value);
		}
		return scriptable;
	}

	private List<Error> getErrors()
	{
		List<Error> errs = new ArrayList<Error>();
		Scriptable errors = (Scriptable) global.get("errors", global);
		int numErrors = ((Number) errors.get("length", global)).intValue();

		for (int i = 0; i < numErrors; i++)
		{
			Error error = map((Scriptable) errors.get(i, global));
			errs.add(error);
		}

		return errs;
	}

	private Error map(Scriptable error)
	{
		Error err = new Error();
		err.setLine(((Number) error.get("line", global)).intValue());
		err.setCharacter(((Number) error.get("character", global)).intValue());
		err.setReason(error.get("reason", global).toString());
		err.setEvidence(error.get("evidence", global).toString().trim());
		return err;
	}

	public static void print(Context context, Scriptable object, Object[] args, Function function)
	{
		String delim = "";
		for (Object arg : args)
		{
			System.out.println(delim);
			System.out.println(Context.toString(arg));
			delim = " ";
		}
		System.out.println();
	}
}
