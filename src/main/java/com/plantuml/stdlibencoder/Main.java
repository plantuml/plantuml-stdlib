package com.plantuml.stdlibencoder;

import java.io.IOException;

public class Main {

	public static void main(String[] args) throws IOException {
		StdlibBuilder6.deleteHomeRepx();
		new StdlibBuilder6("archimate").buildNow();
		new StdlibBuilder6("awslib10").buildNow();
		new StdlibBuilder6("awslib14").buildNow();
		new StdlibBuilder6("awslib").buildNow();
		new StdlibBuilder6("aws").buildNow();
		new StdlibBuilder6("azure").buildNow();
		new StdlibBuilder6("C4").buildNow();
		new StdlibBuilder6("classy").buildNow();
		new StdlibBuilder6("classy-c4").buildNow();
		new StdlibBuilder6("cloudinsight").buildNow();
		new StdlibBuilder6("cloudogu").buildNow();
		new StdlibBuilder6("DomainStory").buildNow();
		new StdlibBuilder6("elastic").buildNow();
		new StdlibBuilder6("edgy").buildNow();
		new StdlibBuilder6("kubernetes").buildNow();
		new StdlibBuilder6("logos").buildNow();
		new StdlibBuilder6("material").buildNow();
		new StdlibBuilder6("office").buildNow();
		new StdlibBuilder6("osa").buildNow();
		new StdlibBuilder6("osa2").buildNow();
		new StdlibBuilder6("tupadr3").buildNow();
	}


}
