package com.linsheng.FATJS.utils;

public class ExitException extends Exception
{
	String taskName;
	public ExitException(String taskName)    //构造方法
	{
		super("任务销毁-->" + taskName);	    //调用Exception(message:String)
	}
}