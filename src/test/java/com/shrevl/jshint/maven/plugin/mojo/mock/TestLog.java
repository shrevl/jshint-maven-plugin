package com.shrevl.jshint.maven.plugin.mojo.mock;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.logging.Log;

public class TestLog implements Log {

	private List<CharSequence> logging = new ArrayList<CharSequence>();
	
    /**
     * @see org.apache.maven.plugin.logging.Log#debug(java.lang.CharSequence)
     */
    public void debug( CharSequence content )
    {
        print( "debug", content );
    }

    /**
     * @see org.apache.maven.plugin.logging.Log#debug(java.lang.CharSequence, java.lang.Throwable)
     */
    public void debug( CharSequence content, Throwable error )
    {
        print( "debug", content, error );
    }

    /**
     * @see org.apache.maven.plugin.logging.Log#debug(java.lang.Throwable)
     */
    public void debug( Throwable error )
    {
        print( "debug", error );
    }

    /**
     * @see org.apache.maven.plugin.logging.Log#info(java.lang.CharSequence)
     */
    public void info( CharSequence content )
    {
        print( "info", content );
    }

    /**
     * @see org.apache.maven.plugin.logging.Log#info(java.lang.CharSequence, java.lang.Throwable)
     */
    public void info( CharSequence content, Throwable error )
    {
        print( "info", content, error );
    }

    /**
     * @see org.apache.maven.plugin.logging.Log#info(java.lang.Throwable)
     */
    public void info( Throwable error )
    {
        print( "info", error );
    }

    /**
     * @see org.apache.maven.plugin.logging.Log#warn(java.lang.CharSequence)
     */
    public void warn( CharSequence content )
    {
        print( "warn", content );
    }

    /**
     * @see org.apache.maven.plugin.logging.Log#warn(java.lang.CharSequence, java.lang.Throwable)
     */
    public void warn( CharSequence content, Throwable error )
    {
        print( "warn", content, error );
    }

    /**
     * @see org.apache.maven.plugin.logging.Log#warn(java.lang.Throwable)
     */
    public void warn( Throwable error )
    {
        print( "warn", error );
    }

    /**
     * @see org.apache.maven.plugin.logging.Log#error(java.lang.CharSequence)
     */
    public void error( CharSequence content )
    {
        logging.add( "[error] " + content.toString() );
    }

    /**
     * @see org.apache.maven.plugin.logging.Log#error(java.lang.CharSequence, java.lang.Throwable)
     */
    public void error( CharSequence content, Throwable error )
    {
        StringWriter sWriter = new StringWriter();
        PrintWriter pWriter = new PrintWriter( sWriter );

        error.printStackTrace( pWriter );

        logging.add( "[error] " + content.toString() + "\n\n" + sWriter.toString() );
    }

    /**
     * @see org.apache.maven.plugin.logging.Log#error(java.lang.Throwable)
     */
    public void error( Throwable error )
    {
        StringWriter sWriter = new StringWriter();
        PrintWriter pWriter = new PrintWriter( sWriter );

        error.printStackTrace( pWriter );

        logging.add( "[error] " + sWriter.toString() );
    }

    /**
     * @see org.apache.maven.plugin.logging.Log#isDebugEnabled()
     */
    public boolean isDebugEnabled()
    {
        // TODO: Not sure how best to set these for this implementation...
        return false;
    }

    /**
     * @see org.apache.maven.plugin.logging.Log#isInfoEnabled()
     */
    public boolean isInfoEnabled()
    {
        return true;
    }

    /**
     * @see org.apache.maven.plugin.logging.Log#isWarnEnabled()
     */
    public boolean isWarnEnabled()
    {
        return true;
    }

    /**
     * @see org.apache.maven.plugin.logging.Log#isErrorEnabled()
     */
    public boolean isErrorEnabled()
    {
        return true;
    }

    private void print( String prefix, CharSequence content )
    {
    	logging.add( "[" + prefix + "] " + content.toString() );
    }

    private void print( String prefix, Throwable error )
    {
        StringWriter sWriter = new StringWriter();
        PrintWriter pWriter = new PrintWriter( sWriter );

        error.printStackTrace( pWriter );

        logging.add( "[" + prefix + "] " + sWriter.toString() );
    }

    private void print( String prefix, CharSequence content, Throwable error )
    {
        StringWriter sWriter = new StringWriter();
        PrintWriter pWriter = new PrintWriter( sWriter );

        error.printStackTrace( pWriter );

        logging.add( "[" + prefix + "] " + content.toString() + "\n\n" + sWriter.toString() );
    }

	public List<CharSequence> getLogging() {
		return logging;
	}
    
    
}
