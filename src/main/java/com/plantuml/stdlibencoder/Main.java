package com.plantuml.stdlibencoder;

import java.io.IOException;

public class Main {

	public static void main(String[] args) throws IOException {
		StdlibFolderBuilder.deleteHomeRepx();
		new StdlibFolderBuilder("archimate");
		new StdlibFolderBuilder("awslib10");
		new StdlibFolderBuilder("awslib14");
		new StdlibFolderBuilder("awslib");
		new StdlibFolderBuilder("aws");
		new StdlibFolderBuilder("azure");
		new StdlibFolderBuilder("C4");
		new StdlibFolderBuilder("classy");
		new StdlibFolderBuilder("classy-c4");
		new StdlibFolderBuilder("cloudinsight");
		new StdlibFolderBuilder("cloudogu");
		new StdlibFolderBuilder("DomainStory");
		new StdlibFolderBuilder("elastic");
		new StdlibFolderBuilder("edgy");
		new StdlibFolderBuilder("kubernetes");
		new StdlibFolderBuilder("logos");
		new StdlibFolderBuilder("material");
		new StdlibFolderBuilder("office");
		new StdlibFolderBuilder("osa");
		new StdlibFolderBuilder("osa2");
		new StdlibFolderBuilder("tupadr3");
	}

}
