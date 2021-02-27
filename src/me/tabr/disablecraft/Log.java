package me.tabr.disablecraft;

import java.util.logging.Logger;

public class Log
	{
	static final byte LOGLEVEL_NONE				= 0;
	static final byte LOGLEVEL_ERROR			= 1;
	static final byte LOGLEVEL_WARNING			= 2;
	static final byte LOGLEVEL_INFO				= 3;
	static final byte LOGLEVEL_DETAILED			= 4;
	static final byte LOGLEVEL_DEBUG			= 5;
	static final byte LOGLEVEL_DEFAULT			= Log.LOGLEVEL_DEBUG;
	static public Logger log 					= Logger.getLogger("minecraft");
	static public byte logLevel					= Log.LOGLEVEL_DEFAULT;
	static public final String[] LOGLEVELS_STR	= {"[NONE]","[ERROR]","[WARNING]","[INFO]","[DETAIL]","[DEBUG]"};
	static public void setLogLevel(byte level)
		{
		Log.logLevel	= level;
		}
	static public void log(byte level, String str)
		{
		if (Log.logLevel >= level)
			{
			Log.log.info(Log.LOGLEVELS_STR[level]+"[DC] "+str);
			}
		}
	static public void error(String str)
		{
		Log.log(Log.LOGLEVEL_ERROR,str);
		}
	static public void warning(String str)
		{
		Log.log(Log.LOGLEVEL_WARNING,str);
		}
	static public void info(String str)
		{
		Log.log(Log.LOGLEVEL_INFO,str);
		}
	static public void detailed(String str)
		{
		Log.log(Log.LOGLEVEL_DETAILED,str);
		}
	static public void debug(String str)
		{
		Log.log(Log.LOGLEVEL_DEBUG,str);
		}
	}
