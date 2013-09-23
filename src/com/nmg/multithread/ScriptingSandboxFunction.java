package com.nmg.multithread;

import com.ibm.pim.extensionpoints.ScriptingSandboxFunctionArguments;

public interface ScriptingSandboxFunction

{
     public static final String copyright = com.ibm.pim.utils.Copyright.copyright;

     public void scriptingSandbox(ScriptingSandboxFunctionArguments inArgs);
     
}