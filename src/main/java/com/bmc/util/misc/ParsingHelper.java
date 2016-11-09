/***********************************************************************
* BMC Software, Inc.
* Confidential and Proprietary
* Copyright (c) BMC Software, Inc. 2003-2015
* All Rights Reserved.
***********************************************************************/

package com.bmc.util.misc;

import java.util.ArrayList;
import java.util.List;

/**
 * ParsingHelper provides assistant methods for parsing
 *
 * @author Patrick Malloy
 */
public final class ParsingHelper
{

  /**
   * Gets the first n words from a String
   *
   * @param text                original text
   * @param extraWordDelimiters characters beyond whitespace that
   *                            can denote word boundaries
   * @param n                   number of words to fetch
   *
   * @return array of n Strings denoting the first n words from the original
   *         String. If there were not n words, then any words found will be
   *         returned.
   */
  public static String[] getFirstNWords( String text,
    String extraWordDelimiters, int n )
  {
    String[] result = new String[n];
    StringBuilder current = new StringBuilder();
    char[] c = text.toCharArray();
    int i = 0;
    int j = 0;
    boolean wordStarted = false;
    while ( ( i < c.length ) && ( j < n ) )
    {
      if ( ( Character.isWhitespace( c[i] ) ) ||
        ( extraWordDelimiters.indexOf( c[i] ) >= 0 ) )
      {
        if ( wordStarted )
        {
          result[j] = current.toString();
          current = new StringBuilder();
          ++j;
          wordStarted = false;
        }
      }
      else
      {
        current.append( c[i] );
        wordStarted = true;
      }
      ++i;
    }
    if ( ( wordStarted ) && ( j < n ) )
    {
      result[j] = current.toString();
    }
    return result;
  }

  /**
   * Replaces all occurances of test with replace
   *
   * @param input   input String
   * @param test    String to look for
   * @param replace String to replace with
   *
   * @return String with replacements
   */
  public static String replaceAll( String input, String test, String replace )
  {
    String replace2 = replace;
    String result = input;
    if ( ( input != null ) && ( test != null ) &&
      ( !test.equals( replace2 ) ) )
    {
      if ( replace2 == null )
      {
        replace2 = "";
      }
      int ix = result.indexOf( test );
      int iy = test.length();
      int iz = replace2.length();
      String head;
      String tail;
      while ( ix >= 0 )
      {
        head = result.substring( 0, ix );
        tail = result.substring( ix + iy );
        result = head + replace2 + tail;
        ix = result.indexOf( test, ix + iz );
      }
    }
    return result;
  }

  /**
   * Determines if a String is null or empty
   *
   * @param text text to examine
   * @param trim whether to trim the text before testing it's content
   *
   * @return true if text is null or empty, false otherwise
   */
  public static boolean isNullOrEmpty( String text, boolean trim )
  {
    boolean result = true;
    if ( text != null )
    {
      String data = text;
      if ( trim )
      {
        data = data.trim();
      }
      if ( data.length() > 0 )
      {
        result = false;
      }
    }
    return result;
  }

  /**
   * Determines if a String contains whitespace
   *
   * @param text sting to examine
   *
   * @return true if text contains whitespace, false otherwise
   */
  public static boolean containsWhitespace( String text )
  {
    boolean result = false;
    char[] c = text.toCharArray();
    int i = 0;
    while ( ( i < c.length ) && ( !result ) )
    {
      if ( Character.isWhitespace( c[i] ) )
      {
        result = true;
      }
      ++i;
    }
    return result;
  }

  /**
   * Determine if a string contains digit(s) only
   *
   * @param text String to parse
   *
   * @return true if text contains only digit(s), false otherwise
   */
  public static boolean digitsOnly( String text )
  {
    boolean result = true;
    if ( isNullOrEmpty( text, true ) )
    {
      result = false;
    }
    else
    {
      char[] c = text.toCharArray();
      int j = 0;
      while ( ( result ) && ( j < c.length ) )
      {
        if ( !Character.isDigit( c[j] ) )
        {
          result = false;
        }
        ++j;
      }
    }
    return result;
  }

  /**
   * Determine if a string starts with capital letter(s) and ends with
   * digit(s).
   *
   * @param text String to parse
   *
   * @return true if name starts with capital letter(s) and ends with
   *         digit(s), false otherwise
   */
  public static boolean startsWithCapLettersEndsWithDigits( String text )
  {
    boolean result = false;
    if ( !isNullOrEmpty( text, true ) )
    {
      char[] cs = text.toCharArray();
      int j = 0;
      while ( ( j < cs.length ) && ( Character.isUpperCase( cs[j] ) ) )
      {
        j++;
      }
      if ( ( j == 0 ) || ( j == cs.length ) )
      {
        result = false;
      }
      else
      {
        result = digitsOnly( text.substring( j ) );
      }
    }
    return result;
  }

  /**
   * Determines if a String is quoted
   *
   * @param data String to examine
   *
   * @return true if data is quoted, false otherwise
   */
  public static boolean isQuoted( String data )
  {
    return ( ( isSingleQuoted( data ) ) || ( isDoubleQuoted( data ) ) );
  }

  /**
   * Determines if a String is single quoted
   *
   * @param data String to examine
   *
   * @return true if data is single quoted, false otherwise
   */
  public static boolean isSingleQuoted( String data )
  {
    boolean result = false;
    if ( data != null )
    {
      if ( data.length() >= 2 )
      {
        if ( ( data.startsWith( "\'" ) ) && ( data.endsWith( "\'" ) ) )
        {
          result = true;
        }
      }
    }
    return result;
  }

  /**
   * Determines if a String is double quoted
   *
   * @param data String to examine
   *
   * @return true if data is double quoted, false otherwise
   */
  public static boolean isDoubleQuoted( String data )
  {
    boolean result = false;
    if ( data != null )
    {
      if ( data.length() >= 2 )
      {
        if ( ( data.startsWith( "\"" ) ) && ( data.endsWith( "\"" ) ) )
        {
          result = true;
        }
      }
    }
    return result;
  }

  /**
   * Strips quotes and extra spaces from a String
   *
   * @param data original data
   *
   * @return data with quotes and extra spaces striped
   */
  public static String stripQuotes( String data )
  {
    String result = null;
    if ( data != null )
    {
      result = data.trim();
      if ( isQuoted( result ) )
      {
        result = result.substring( 1, result.length() - 1 );
        result = result.trim();
      }
    }
    return result;
  }

  /**
   * Single quotes a string
   *
   * @param data string to quote
   *
   * @return single quoted string
   */
  public static String singleQuote( String data )
  {
    return "\'" + data + "\'";
  }

  /**
   * Double quotes a string
   *
   * @param data string to quote
   *
   * @return double quoted string
   */
  public static String doubleQuote( String data )
  {
    return "\"" + data + "\"";
  }

  /**
   * Splits a single string containing multiple command line arguments
   * into separate arguments
   *
   * @param commandLine           original line
   * @param backslashesAreLiteral whether backslashes are to be treated as
   *                              literals (true) or as escape characters
   *                              (false)
   *
   * @return separated arguments
   */
  public static List<String> splitArguments( String commandLine,
    boolean backslashesAreLiteral )
  {
    List<String> result = null;
    if ( backslashesAreLiteral )
    {
      result = splitArgumentsBackslashesAreLiteral( commandLine );
    }
    else
    {
      result = splitArgumentsBackslashesAreNotLiteral( commandLine );
    }
    return result;
  }

  /**
   * Splits a single string containing multiple command line arguments
   * into separate arguments
   *
   * @param commandLine original line
   *
   * @return separated arguments
   */
  private static List<String> splitArgumentsBackslashesAreLiteral(
    String commandLine )
  {
    List<String> result = null;
    if ( commandLine != null )
    {
      result = new ArrayList<String>();
      char[] c = commandLine.toCharArray();
      boolean readingQuotes = false;
      StringBuilder buffer = new StringBuilder();
      for ( int i = 0; i < c.length; i++ )
      {
        char cc = c[i];
        if ( cc == '\"' )
        {
          buffer.append( cc );
          boolean endOfString = false;

          // the if and else if below are to detect we are really out
          // of the current double quote region and not an inner quote
          // for example "foo="abc"" should not end at the quote after
          // the equals sign, but rather after the second quote after
          // the c character. To ensure we are out of a double quote
          // region, see if we are either at the end of the total string
          // or the next character after this "second" double quote
          // is whitespace or more text.
          if ( i == ( c.length - 1 ) )
          {
            endOfString = true;
            readingQuotes = false;
          }
          else if ( ( readingQuotes ) &&
            ( Character.isWhitespace( c[i + 1] ) ) )
          {
            endOfString = true;
            readingQuotes = false;
          }
          else                         // start of quoting or internal quote
          {
            readingQuotes = true;
          }
          if ( endOfString )
          {
            result.add( buffer.toString() );
            buffer.delete( 0, buffer.length() );
          }
        }
        else if ( ( !readingQuotes ) && ( Character.isWhitespace( cc ) ) )
        {
          if ( buffer.length() > 0 )   // end of normal argument
          {
            result.add( buffer.toString() );
            buffer.delete( 0, buffer.length() );
          }
        }
        else
        {
          buffer.append( cc );
        }
      }
      if ( buffer.length() > 0 )
      {
        result.add( buffer.toString() );
        buffer.delete( 0, buffer.length() );
      }
    }
    return result;
  }

  /**
   * Splits a single string containing multiple command line arguments
   * into separate arguments
   *
   * @param commandLine original line
   *
   * @return separated arguments
   */
  private static List<String> splitArgumentsBackslashesAreNotLiteral(
    String commandLine )
  {
    List<String> result = null;
    if ( commandLine != null )
    {
      result = new ArrayList<String>();
      char[] c = commandLine.toCharArray();
      boolean readingQuotes = false;
      boolean readingSlash = false;
      StringBuilder buffer = new StringBuilder();
      for ( int i = 0; i < c.length; i++ )
      {
        char cc = c[i];
        if ( cc == '\\' )
        {
          readingSlash = !readingSlash;
          buffer.append( cc );
        }
        else
        {
          if ( ( !readingSlash ) && ( cc == '\"' ) )
          {
            buffer.append( cc );
            boolean endOfString = false;

            // the if and else if below are to detect we are really out
            // of the current double quote region and not an inner quote
            // for example "foo="abc"" should not end at the quote after
            // the equals sign, but rather after the second quote after
            // the c character. To ensure we are out of a double quote
            // region, see if we are either at the end of the total string
            // or the next character after this "second" double quote
            // is whitespace or more text.
            if ( i == ( c.length - 1 ) )
            {
              endOfString = true;
              readingQuotes = false;
            }
            else if ( ( readingQuotes ) &&
              ( Character.isWhitespace( c[i + 1] ) ) )
            {
              endOfString = true;
              readingQuotes = false;
            }
            else                         // start of quoting or internal quote
            {
              readingQuotes = true;
            }
            if ( endOfString )
            {
              result.add( buffer.toString() );
              buffer.delete( 0, buffer.length() );
            }
          }
          else if ( ( !readingQuotes ) && ( Character.isWhitespace( cc ) ) )
          {
            if ( buffer.length() > 0 )   // end of normal argument
            {
              result.add( buffer.toString() );
              buffer.delete( 0, buffer.length() );
            }
          }
          else
          {
            buffer.append( cc );
          }
          readingSlash = false;
        }
      }
      if ( buffer.length() > 0 )
      {
        result.add( buffer.toString() );
        buffer.delete( 0, buffer.length() );
      }
    }
    return result;
  }

  /**
   * Constructor for ParsingHelper
   */
  private ParsingHelper()
  {
    super();
  }
}
