package com.plantuml.stdlibencoder;

import java.io.IOException;

public class Main {

	public static void main(String[] args) throws IOException {
		int diff = 45;
		if (args.length > 0)
			diff = Integer.parseInt(args[0]);

		StdlibBuilder6.HOME.delete();
		new Xuml6MakeRepAndRepx("archimate", diff);
		new Xuml6MakeRepAndRepx("awslib10", diff);
		new Xuml6MakeRepAndRepx("awslib14", diff);
		new Xuml6MakeRepAndRepx("awslib", diff);
		new Xuml6MakeRepAndRepx("aws", diff);
		new Xuml6MakeRepAndRepx("azure", diff);
		new Xuml6MakeRepAndRepx("C4", diff);
		new Xuml6MakeRepAndRepx("classy", diff);
		new Xuml6MakeRepAndRepx("classy-c4", diff);
		new Xuml6MakeRepAndRepx("cloudinsight", diff);
		new Xuml6MakeRepAndRepx("cloudogu", diff);
		new Xuml6MakeRepAndRepx("DomainStory", diff);
		new Xuml6MakeRepAndRepx("elastic", diff);
		new Xuml6MakeRepAndRepx("edgy", diff);
		new Xuml6MakeRepAndRepx("kubernetes", diff);
		new Xuml6MakeRepAndRepx("logos", diff);
		new Xuml6MakeRepAndRepx("material", diff);
		new Xuml6MakeRepAndRepx("office", diff);
		new Xuml6MakeRepAndRepx("osa", diff);
		new Xuml6MakeRepAndRepx("osa2", diff);
		new Xuml6MakeRepAndRepx("tupadr3", diff);
	}


}
