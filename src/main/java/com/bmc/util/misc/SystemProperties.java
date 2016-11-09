/***********************************************************************
* BMC Software, Inc.
* Confidential and Proprietary
* Copyright (c) BMC Software, Inc. 2003-2015
* All Rights Reserved.
***********************************************************************/

package com.bmc.util.misc;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;

//import com.bmc.util.io.FileHelper;
//import com.bmc.util.logging.BaseLogManager;
//import com.bmc.util.logging.Log;

/**
 * SystemProperties holds accessor methods for all the Java system properties
 *
 * @author Patrick Malloy
 */
public final class SystemProperties
{

  /**
   * Main method
   *
   * @param args command line arguments
   */
	/*
  public static void main( String[] args )
  {
    try
    {
      if ( ( args.length > 0 ) && ( args[0] != null ) )
      {
        BaseLogManager.makeLogWritable();
        File outputFile = new File( args[0] );
        if ( outputFile.exists() )
        {
          FileHelper.deleteFile( outputFile );
        }
        boolean result = FileHelper.createFile( outputFile );
        if ( result )
        {
          Properties properties = System.getProperties();
          FileOutputStream fos = null;
          try
          {
            fos = new FileOutputStream( outputFile );
            properties.store( fos, "Java VM Properties" );
          }
          finally
          {
            if ( fos != null )
            {
              try
              {
                fos.close();
              }
              catch ( Throwable t )
              {

                // ignore
              }
            }
          }
        }
      }
    }
    catch ( Throwable t )
    {
      Log.getInstance().logThrowable( SystemProperties.class, "Error", null,
        t );
    }
  }
*/
  /**
   * Gets the java.version
   *
   * @return java.version
   */
  public static String getJavaVersion()
  {
    return System.getProperty( "java.version" );
  }

  /**
   * Gets the java.vendor
   *
   * @return java.vendor
   */
  public static String getJavaVendor()
  {
    return System.getProperty( "java.vendor" );
  }

  /**
   * Gets the java.vendor.url
   *
   * @return java.vendor.url
   */
  public static String getJavaVendorUrl()
  {
    return System.getProperty( "java.vendor.url" );
  }

  /**
   * Gets the java.home
   *
   * @return java.home
   */
  public static String getJavaHome()
  {
    return System.getProperty( "java.home" );
  }

  /**
   * Gets the java.vm.specification.version
   *
   * @return java.vm.specification.version
   */
  public static String getJavaVmSpecificationVersion()
  {
    return System.getProperty( "java.vm.specification.version" );
  }

  /**
   * Gets the java.vm.specification.vendor
   *
   * @return java.vm.specification.vendor
   */
  public static String getJavaVmSpecificationVendor()
  {
    return System.getProperty( "java.vm.specification.vendor" );
  }

  /**
   * Gets the java.vm.specification.name
   *
   * @return java.vm.specification.name
   */
  public static String getJavaVmSpecificationName()
  {
    return System.getProperty( "java.vm.specification.name" );
  }

  /**
   * Gets the java.vm.version
   *
   * @return java.vm.version
   */
  public static String getJavaVmVersion()
  {
    return System.getProperty( "java.vm.version" );
  }

  /**
   * Gets the java.vm.vendor
   *
   * @return java.vm.vendor
   */
  public static String getJavaVmVendor()
  {
    return System.getProperty( "java.vm.vendor" );
  }

  /**
   * Gets the java.vm.name
   *
   * @return java.vm.name
   */
  public static String getJavaVmName()
  {
    return System.getProperty( "java.vm.name" );
  }

  /**
   * Gets the java.specification.version
   *
   * @return java.specification.version
   */
  public static String getJavaSpecificationVersion()
  {
    return System.getProperty( "java.specification.version" );
  }

  /**
   * Gets the java.specification.vendor
   *
   * @return java.specification.vendor
   */
  public static String getJavaSpecificationVendor()
  {
    return System.getProperty( "java.specification.vendor" );
  }

  /**
   * Gets the java.specification.name
   *
   * @return java.specification.name
   */
  public static String getJavaSpecificationName()
  {
    return System.getProperty( "java.specification.name" );
  }

  /**
   * Gets the java.class.version
   *
   * @return java.class.version
   */
  public static String getJavaClassVersion()
  {
    return System.getProperty( "java.class.version" );
  }

  /**
   * Gets the java.class.path
   *
   * @return java.class.path
   */
  public static String getJavaClassPath()
  {
    return System.getProperty( "java.class.path" );
  }

  /**
   * Gets the java.library.path
   *
   * @return java.library.path
   */
  public static String getJavaLibraryPath()
  {
    return System.getProperty( "java.library.path" );
  }

  /**
   * Gets the java.io.tmpdir
   *
   * @return java.io.tmpdir
   */
  public static String getJavaIOTmpdir()
  {
    return System.getProperty( "java.io.tmpdir" );
  }

  /**
   * Gets the java.compiler
   *
   * @return java.compiler
   */
  public static String getJavaCompiler()
  {
    return System.getProperty( "java.compiler" );
  }

  /**
   * Gets the java.ext.dirs
   *
   * @return java.ext.dirs
   */
  public static String getJavaExtDirs()
  {
    return System.getProperty( "java.ext.dirs" );
  }

  /**
   * Gets the os.name
   *
   * @return os.name
   */
  public static String getOsName()
  {
    return System.getProperty( "os.name" );
  }

  /**
   * Gets the os.arch
   *
   * @return os.arch
   */
  public static String getOsArch()
  {
    return System.getProperty( "os.arch" );
  }

  /**
   * Gets the os.version
   *
   * @return os.version
   */
  public static String getOsVersion()
  {
    return System.getProperty( "os.version" );
  }

  /**
   * Gets the file.separator
   *
   * @return file.separator
   */
  public static String getFileSeparator()
  {
    return System.getProperty( "file.separator" );
  }

  /**
   * Gets the path.separator
   *
   * @return path.separator
   */
  public static String getPathSeparator()
  {
    return System.getProperty( "path.separator" );
  }

  /**
   * Gets the line.separator
   *
   * @return line.separator
   */
  public static String getLineSeparator()
  {
    return System.getProperty( "line.separator" );
  }

  /**
   * Gets the user.name
   *
   * @return user.name
   */
  public static String getUserName()
  {
    return System.getProperty( "user.name" );
  }

  /**
   * Gets the user.home
   *
   * @return user.home
   */
  public static String getUserHome()
  {
    return System.getProperty( "user.home" );
  }

  /**
   * Gets the user.dir
   *
   * @return user.dir
   */
  public static String getUserDir()
  {
    return System.getProperty( "user.dir" );
  }

  /**
   * Gets the user.language
   *
   * @return user.language
   */
  public static String getUserLanguage()
  {
    return System.getProperty( "user.language" );
  }

  /**
   * Gets the user.country.
   * <p>
   * Note this used to be "user.region" Java 1.2 and 1.3, but
   * was renamed in Java 1.4 to "user.country".
   * </p>
   *
   * @return user.country
   */
  public static String getUserCountry()
  {
    return System.getProperty( "user.country" );
  }

  /**
   * Gets the user.variant
   *
   * @return user.variant
   */
  public static String getUserVariant()
  {
    return System.getProperty( "user.variant" );
  }

  /**
   * Gets the user.timezone
   *
   * @return user.timezone
   */
  public static String getUserTimezone()
  {
    return System.getProperty( "user.timezone" );
  }

  /**
   * Constructor for SystemProperties
   */
  private SystemProperties()
  {
    super();
  }
}
