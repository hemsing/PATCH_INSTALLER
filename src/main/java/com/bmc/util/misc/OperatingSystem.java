package com.bmc.util.misc;

public class OperatingSystem {
  
	
	  public OperatingSystem()
	  {
	    architecture_ = SystemProperties.getOsArch();
	    name_ = SystemProperties.getOsName();
	    version_ = SystemProperties.getOsVersion();
	  }

	  /**
	   * Constructor for OperatingSystem
	   *
	   * @param architecture operating system architecture
	   * @param name         operating system name
	   * @param version      operating system version
	   */
	  public OperatingSystem( String architecture, String name, String version )
	  {
	    architecture_ = architecture;
	    name_ = name;
	    version_ = version;
	  }

	  /**
	   * Gets the operating system architecture
	   *
	   * @return operating system architecture
	   */
	  public String getArchitecture()
	  {
	    return architecture_;
	  }

	  /**
	   * Gets the operating system name
	   *
	   * @return operating system name
	   */
	  public String getName()
	  {
	    return name_;
	  }

	  /**
	   * Gets the operating system version
	   *
	   * @return operating system version
	   */
	  public String getVersion()
	  {
	    return version_;
	  }

	  /**
	   * Converts a DataStructure to a specific class object
	   *
	   * @param dataStructure DataStructure
	   *
	   * @return specific class object
	   *
	   * @throws IncompatibleDataStructureException when the DataStructure being
	   *                                            converted is for an
	   *                                            incompatible datatype
	   
	  public OperatingSystem convertFromDataStructure(
	    DataStructure dataStructure )
	    throws IncompatibleDataStructureException
	  {
	    String datatype = dataStructure.getDatatype();
	    if ( !datatype.equals( getClass().getName() ) )
	    {
	      throw new IncompatibleDataStructureException(
	        "Datastructure datatype: " + datatype + " is not compatible with "
	          + getClass().getName() );
	    }
	    String architecture =
	      dataStructure.getDataStructurePrimitiveValue( "architecture", null );
	    String name = dataStructure.getDataStructurePrimitiveValue( "name",
	      null );
	    String version = dataStructure.getDataStructurePrimitiveValue( "version",
	      null );
	    return new OperatingSystem( architecture, name, version );
	  }

	  /**
	   * Converts a specific class object into a DataStructure
	   *
	   * @param id  id key for DataStructure
	   * @param obj specific class object
	   *
	   * @return DataStructure representation of object
	   
	  public DataStructure convertToDataStructure( String id,
	    OperatingSystem obj )
	  {
	    DataStructure result = new DataStructure( id, obj.getClass() );
	    String architecture = obj.getArchitecture();
	    if ( architecture != null )
	    {
	      result.add( new DataStructurePrimitive( "architecture",
	        DataStructurePrimitive.STRING_DATATYPE, architecture ) );
	    }
	    String name = obj.getName();
	    if ( name != null )
	    {
	      result.add( new DataStructurePrimitive( "name",
	        DataStructurePrimitive.STRING_DATATYPE, name ) );
	    }
	    String version = obj.getVersion();
	    if ( version != null )
	    {
	      result.add( new DataStructurePrimitive( "version",
	        DataStructurePrimitive.STRING_DATATYPE, version ) );
	    }
	    return result;
	  }

	  /**
	   * Clones this Object
	   *
	   * @return cloned copy of this Object
	   
	  public OperatingSystem clone()
	  {
	    String architecture = CloneHelper.nullableClone( getArchitecture() );
	    String name = CloneHelper.nullableClone( getName() );
	    String version = CloneHelper.nullableClone( getVersion() );
	    return new OperatingSystem( architecture, name, version );
	  }

	  /**
	   * Determines if another Object is equal to this one
	   *
	   * @param obj Object to compare
	   *
	   * @return true if Objects are equal, false otherwise
	   
	  public boolean equals( Object obj )
	  {
	    boolean result = false;
	    if ( ( obj != null ) && ( obj instanceof OperatingSystem ) )
	    {
	      OperatingSystem os = ( OperatingSystem )obj;
	      String a = getArchitecture();
	      String aa = os.getArchitecture();
	      String b = getName();
	      String bb = os.getName();
	      String c = getVersion();
	      String cc = os.getVersion();
	      if ( ( EqualsHelper.nullableEquals( a, aa ) ) &&
	        ( EqualsHelper.nullableEquals( b, bb ) ) &&
	          ( EqualsHelper.nullableEquals( c, cc ) ) )
	      {
	        result = true;
	      }
	    }
	    return result;
	  } 

	  /**
	   * Gets the hash code value of this Object
	   *
	   * @return hash code value of this Object
	   */
	  public int hashCode()
	  {
	    return toString().hashCode();
	  }

	  /**
	   * Gets a String representation of this Object
	   *
	   * @return String representation of this Object
	   */
	  public String toString()
	  {
	    StringBuilder result = new StringBuilder();
	    result.append( "[" );
	    result.append( "class=" + getClass().getName() );
	    result.append( ",architecture=" + getArchitecture() );
	    result.append( ",name=" + getName() );
	    result.append( ",version=" + getVersion() );
	    result.append( "]" );
	    return result.toString();
	  }

	  /**
	   * Gets an encoded String representation of this Object
	   *
	   * @return encoded String representation of this Object
	   */
	  public String toEncodedString()
	  {
	    StringBuilder result = new StringBuilder();
	    result.append( "architecture=" + getArchitecture() + "\t" );
	    result.append( "name=" + getName() + "\t" );
	    result.append( "version=" + getVersion() );
	    return result.toString();
	  }

	  /**
	   * Given an encoded String representation, return an OperatingSystem object
	   *
	   * @param encodedString Encoded object string
	   *
	   * @return OperatingSystem Object or null if encodedString is invalid
	   */
	  public static OperatingSystem fromEncodedString( String encodedString )
	  {
	    OperatingSystem result = null;
	    String[] tempStrs = encodedString.split( "\t" );
	    if ( tempStrs.length == 3 )
	    {
	      String arch = null;
	      String name = null;
	      String version = null;
	      try
	      {
	        int index = tempStrs[0].indexOf( "=" );
	        if ( index > 0 )
	        {
	          arch = tempStrs[0].substring( index + 1 );
	        }
	        index = tempStrs[1].indexOf( "=" );
	        if ( index > 0 )
	        {
	          name = tempStrs[1].substring( index + 1 );
	        }
	        index = tempStrs[2].indexOf( "=" );
	        if ( index > 0 )
	        {
	          version = tempStrs[2].substring( index + 1 );
	        }
	        result = new OperatingSystem( arch, name, version );
	      }
	      catch ( Exception e )
	      {

	        // return null
	      }
	    }
	    return result;
	  }

	  /**
	   * serial version UID
	   */
	  private static final long serialVersionUID = 1L;

	  /**
	   * operating system architecture
	   */
	  private String architecture_;

	  /**
	   * operating system name
	   */
	  private String name_;

	  /**
	   * operating system version
	   */
	  private String version_;
	
	
}
