package com.shrevl.jshint.maven.plugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import com.shrevl.jshint.maven.plugin.js.JSFile;

public class JSHint
{
	private final Context context;
	private final ScriptableObject global;

	private static final String DEFAULT_JS_HINT_PATH = "/com/shrevl/jshint/jshint.js";
	private static final String JS_HINT_SCRIPT_PATH = "/com/shrevl/jshint/jshint-script.js";

	public JSHint() throws IOException
	{
		context = Context.enter();
		context.setLanguageVersion(Context.VERSION_1_6);
		global = context.initStandardObjects();

		defineFunctions();
		JSFile jshint = JSFile.getResource(DEFAULT_JS_HINT_PATH);
		context.evaluateReader(global, jshint.getReader(), jshint.getPath(), 0, null);
	}

	private void defineFunctions()
	{
		global.defineFunctionProperties(new String[] { "print" }, JSHint.class, ScriptableObject.DONTENUM);
	}

	public void run(List<JSFile> files) throws Exception
	{
		JSFile jshintScript = JSFile.getResource(JS_HINT_SCRIPT_PATH);

		global.defineProperty("source", "", ScriptableObject.DONTENUM);
		global.defineProperty("errors", context.newArray(global, 0), ScriptableObject.DONTENUM);

		for (JSFile file : files)
		{
			global.put("errors", global, context.newArray(global, 0));
			global.put("source", global, file.getSource());
			context.evaluateReader(global, jshintScript.getReader(), jshintScript.getPath(), 0, null);
			List<Error> errors = getErrors();
			System.out.println(errors.size());
		}
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
		err.setReason((String) error.get("reason", global));
		err.setEvidence(((String) error.get("evidence", global)).trim());
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
