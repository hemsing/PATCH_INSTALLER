/***********************************************************************
* BMC Software, Inc.
* Confidential and Proprietary
* Copyright (c) BMC Software, Inc. 2003-2015
* All Rights Reserved.
***********************************************************************/

package com.bmc.util.misc;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;

import com.bmc.util.misc.ParsingHelper;

/**
 * StringHelper provides helper methods for Strings
 *
 * @author Patrick Malloy
 */
public final class StringHelper
{

  /**
   * Makes a string from a set of bytes
   *
   * @param data        data bytes
   * @param charsetName charset to encode bytes
   *
   * @return encoded bytes
   *
   * @throws UnsupportedEncodingException if encoding is unsupported
   */
  public static String makeString( byte[] data, String charsetName )
    throws UnsupportedEncodingException
  {
    String result = null;
    if ( data != null )
    {
      if ( charsetName == null )
      {
        result = new String( data );
      }
      else
      {
        result = new String( data, charsetName );
      }
    }
    return result;
  }

  /**
   * Gets bytes from a string
   *
   * @param data        data string
   * @param charsetName charset to decode string
   *
   * @return decoded string
   *
   * @throws UnsupportedEncodingException if encoding is unsupported
   */
  public static byte[] getBytes( String data, String charsetName )
    throws UnsupportedEncodingException
  {
    byte[] result = null;
    if ( data != null )
    {
      if ( charsetName == null )
      {
        result = data.getBytes();
      }
      else
      {
        result = data.getBytes( charsetName );
      }
    }
    return result;
  }

  /**
   * Decodes a double from a String
   *
   * @param value                 value to parse
   * @param defaultOnNull         double to return if value is null
   * @param defaultOnNumberFormat double to return if parsing value results
   *                              in a NumberFormatException
   *
   * @return parsed double
   */
  public static double decodeDouble( String value, double defaultOnNull,
    double defaultOnNumberFormat )
  {
    double result = defaultOnNull;
    if ( value != null )
    {
      try
      {
        result = Double.parseDouble( value );
      }
      catch ( NumberFormatException nfe )
      {
        result = defaultOnNumberFormat;
      }
    }
    return result;
  }

  /**
   * Encodes a double
   *
   * @param value double to encode
   *
   * @return encoded double
   */
  public static String encodeDouble( double value )
  {
    return Double.toString( value );
  }

  /**
   * Decodes a float from a String
   *
   * @param value                 value to parse
   * @param defaultOnNull         float to return if value is null
   * @param defaultOnNumberFormat float to return if parsing value results
   *                              in a NumberFormatException
   *
   * @return parsed float
   */
  public static float decodeFloat( String value, float defaultOnNull,
    float defaultOnNumberFormat )
  {
    float result = defaultOnNull;
    if ( value != null )
    {
      try
      {
        result = Float.parseFloat( value );
      }
      catch ( NumberFormatException nfe )
      {
        result = defaultOnNumberFormat;
      }
    }
    return result;
  }

  /**
   * Encodes a float
   *
   * @param value float to encode
   *
   * @return encoded float
   */
  public static String encodeFloat( float value )
  {
    return Float.toString( value );
  }

  /**
   * Decodes a long from a String
   *
   * @param value                 value to parse
   * @param defaultOnNull         long to return if value is null
   * @param defaultOnNumberFormat long to return if parsing value results
   *                              in a NumberFormatException
   *
   * @return parsed long
   */
  public static long decodeLong( String value, long defaultOnNull,
    long defaultOnNumberFormat )
  {
    long result = defaultOnNull;
    if ( value != null )
    {
      try
      {
        result = Long.parseLong( value );
      }
      catch ( NumberFormatException nfe )
      {
        result = defaultOnNumberFormat;
      }
    }
    return result;
  }

  /**
   * Encodes a long
   *
   * @param value long to encode
   *
   * @return encoded long
   */
  public static String encodeLong( long value )
  {
    return Long.toString( value );
  }

  /**
   * Decodes an int from a String
   *
   * @param value                 value to parse
   * @param defaultOnNull         int to return if value is null
   * @param defaultOnNumberFormat int to return if parsing value results
   *                              in a NumberFormatException
   *
   * @return parsed int
   */
  public static int decodeInt( String value, int defaultOnNull,
    int defaultOnNumberFormat )
  {
    int result = defaultOnNull;
    if ( value != null )
    {
      try
      {
        result = Integer.parseInt( value );
      }
      catch ( NumberFormatException nfe )
      {
        result = defaultOnNumberFormat;
      }
    }
    return result;
  }

  /**
   * Encodes an int
   *
   * @param value int to encode
   *
   * @return encoded int
   */
  public static String encodeInt( int value )
  {
    return Integer.toString( value );
  }

  /**
   * Decodes a short from a String
   *
   * @param value                 value to parse
   * @param defaultOnNull         short to return if value is null
   * @param defaultOnNumberFormat short to return if parsing value results
   *                              in a NumberFormatException
   *
   * @return parsed short
   */
  public static short decodeShort( String value, short defaultOnNull,
    short defaultOnNumberFormat )
  {
    short result = defaultOnNull;
    if ( value != null )
    {
      try
      {
        result = Short.parseShort( value );
      }
      catch ( NumberFormatException nfe )
      {
        result = defaultOnNumberFormat;
      }
    }
    return result;
  }

  /**
   * Encodes a short
   *
   * @param value short to encode
   *
   * @return encoded short
   */
  public static String encodeShort( short value )
  {
    return Short.toString( value );
  }

  /**
   * Decodes a byte from a String
   *
   * @param value                 value to parse
   * @param defaultOnNull         byte to return if value is null
   * @param defaultOnNumberFormat byte to return if parsing value results
   *                              in a NumberFormatException
   *
   * @return parsed byte
   */
  public static byte decodeByte( String value, byte defaultOnNull,
    byte defaultOnNumberFormat )
  {
    byte result = defaultOnNull;
    if ( value != null )
    {
      try
      {
        result = Byte.parseByte( value );
      }
      catch ( NumberFormatException nfe )
      {
        result = defaultOnNumberFormat;
      }
    }
    return result;
  }

  /**
   * Decodes a byte from a String
   *
   * @param value                 value to parse
   * @param radix                 radix used in parsing String
   * @param defaultOnNull         byte to return if value is null
   * @param defaultOnNumberFormat byte to return if parsing value results
   *                              in a NumberFormatException
   *
   * @return parsed byte
   */
  public static byte decodeByte( String value, int radix, byte defaultOnNull,
    byte defaultOnNumberFormat )
  {
    byte result = defaultOnNull;
    if ( value != null )
    {
      try
      {
        result = Byte.parseByte( value, radix );
      }
      catch ( NumberFormatException nfe )
      {
        result = defaultOnNumberFormat;
      }
    }
    return result;
  }

  /**
   * Encodes a byte
   *
   * @param value byte to encode
   *
   * @return encoded byte
   */
  public static String encodeByte( byte value )
  {
    return Byte.toString( value );
  }

  /**
   * Decodes a boolean from a String
   *
   * @param value         value to parse
   * @param defaultOnNull boolean to return if value is null
   *
   * @return parsed boolean
   */
  public static boolean decodeBoolean( String value, boolean defaultOnNull )
  {
    boolean result = defaultOnNull;
    if ( value != null )
    {
      result = Boolean.valueOf( value ).booleanValue();
    }
    return result;
  }

  /**
   * Encodes a boolean
   *
   * @param value value to encode
   *
   * @return encoded boolean
   */
  public static String encodeBoolean( boolean value )
  {
    return Boolean.toString( value );
  }

  /**
   * Encodes command line arguments
   *
   * @param args arguments to encode
   *
   * @return encoded command line
   */
  public static String encodeCommandLine( String[] args )
  {
    StringBuilder result = new StringBuilder();
    String arg;
    for ( int i = 0; i < args.length; i++ )
    {
      arg = args[i];
      if ( needsQuotes( arg ) )
      {
        result.append( '\"' );
        result.append( arg );
        result.append( '\"' );
      }
      else
      {
        result.append( arg );
      }
      if ( i != ( args.length - 1 ) )
      {
        result.append( ' ' );
      }
    }
    return result.toString();
  }

  /**
   * Encodes command line arguments
   *
   * @param args arguments to encode
   *
   * @return encoded command line
   */
  public static String encodeCommandLine( List<String> args )
  {
    StringBuilder result = new StringBuilder();
    String arg;
    Iterator<String> iter = args.iterator();
    while ( iter.hasNext() )
    {
      arg = iter.next();
      if ( needsQuotes( arg ) )
      {
        result.append( '\"' );
        result.append( arg );
        result.append( '\"' );
      }
      else
      {
        result.append( arg );
      }
      if ( iter.hasNext() )
      {
        result.append( ' ' );
      }
    }
    return result.toString();
  }

  /**
   * Removes beginning and trailing single byte and double byte white space
   * characters
   *
   * @param value string to be trimmed
   *
   * @return trimmed string
   */
  public static String trim( String value )
  {
    String result = value;
    if ( value != null )
    {
      result = value.trim();
      if ( result.length() > 0 )
      {
        int j = 0;
        while ( ( j < result.length() ) &&
          ( Character.isWhitespace( result.charAt( j ) ) ) )
        {
          j++;
        }
        if ( j < result.length() )
        {
          int k = result.length() - 1;
          while ( ( k >= j ) &&
            ( Character.isWhitespace( result.charAt( k ) ) ) )
          {
            k--;
          }
          result = result.substring( j, k + 1 );
        }
        else
        {
          result = "";
        }
      }
    }
    return result;
  }

  /**
   * Determines if a given command line argument needs quotes
   *
   * @param arg command line argument
   *
   * @return true if argument needs quotes, false otherwise
   */
  private static boolean needsQuotes( String arg )
  {
    boolean result = false;
    if ( !ParsingHelper.isQuoted( arg ) )
    {
      char[] c = arg.toCharArray();
      int i = 0;
      while ( ( !result ) && ( i < c.length ) )
      {
        if ( Character.isWhitespace( c[i] ) )
        {
          result = true;
        }
        ++i;
      }
    }
    else if ( arg.length() == 0 )
    {
      result = true;
    }
    return result;
  }

  /**
   * Constructor for StringHelper
   */
  private StringHelper()
  {
    super();
  }
}
