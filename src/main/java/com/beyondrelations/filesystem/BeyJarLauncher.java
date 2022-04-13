
package com.beyondrelations.filesystem;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.stream.Stream;
import java.util.List;

/**
Picks the latest jar from Z:\data\bey\releases and runs workflo with default.json properties
*/
public class BeyJarLauncher
{

	private Path workfloRoot;


	public static void main(
			String[] args
	) {
		final String beyRootPathWindowsStr = "Z:\\data\\bey";
		new BeyJarLauncher( beyRootPathWindowsStr )
				.launchLatestJar();
	}


	public BeyJarLauncher(
			String beyRootPathStr
	) {
		workfloRoot = Paths.get( beyRootPathStr ).toAbsolutePath();
	}


	private void launchLatestJar(
	) {
		Path latestBeyJar = chooseJar();
		runJar( latestBeyJar );
	}


	private Path chooseJar(
	) {
		
		FileTime newestFiletime = FileTime.fromMillis( 1_000L ); // decades before any of our jars were created
		Path jarWithNewestFiletime = null;
		try
		{
			Path releaseFolderPath = workfloRoot.resolve( "releases" );
			DirectoryStream<Path> releaseFolders = Files.newDirectoryStream( releaseFolderPath );
			for ( Path directory : releaseFolders )
			{
				DirectoryStream<Path> releaseFiles = Files.newDirectoryStream( directory );
				for ( Path someFilePath : releaseFiles )
				{
					File someFile = someFilePath.toFile();
					if ( ! someFile.isFile()
							|| ! someFile.getName().endsWith( "jar" ) )
						continue;
					FileTime jarModified = (FileTime)Files.getAttribute( someFilePath, "lastModifiedTime" );
					if ( newestFiletime.compareTo( jarModified ) < 0 )
					{
						jarWithNewestFiletime = someFilePath.toAbsolutePath();
						newestFiletime = jarModified;
					}
				}
			}
		}
		catch ( IOException ie )
		{
			System.err.println( "Couldn't find jar because "+ ie );
		}
		if ( jarWithNewestFiletime == null )
			throw new IllegalArgumentException( "bjl.cj found no jars in "+ workfloRoot );
		else
		{
			System.out.println( newestFiletime +"\n"+ jarWithNewestFiletime );
			return jarWithNewestFiletime;
		}
	}


	private void runJar(
			Path jarFile
	) {
		List<String> commandComponents = new LinkedList<>();
		commandComponents.add( "java" );
		commandComponents.add( "-jar" );
		commandComponents.add( jarFile.toString() );
		commandComponents.add( "-p" );
		commandComponents.add( "config\\default.json" );
		ProcessBuilder yourJar = new ProcessBuilder( commandComponents );
		yourJar.directory( workfloRoot.toFile() );
		yourJar.inheritIO();
		try
		{
			yourJar.start().waitFor();
		}
		catch ( IOException ie )
		{
			System.err.println( "Couldn't launch jar because "+ ie );
		}
		catch ( InterruptedException ie )
		{
			System.err.println( "Programmatically told to quit via "+ ie );
			return;
		}
	}

}








