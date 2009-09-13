<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="html"/>
  <!--  -->
<xsl:template match="/parser-descriptor">
using System;
namespace <xsl:value-of select="@namespace"/>
{
<xsl:apply-templates select="class-descriptor"/>
}

</xsl:template>

<!-- create class -->
<xsl:template match="class-descriptor">
  public class <xsl:value-of select="@classname" />
  {
    public enum States
    {
<xsl:for-each select=".//states//state" >
      <xsl:value-of select="@name" />,
</xsl:for-each>
    }
    States <xsl:value-of select=".//states//@varname" /> = States.<xsl:value-of select=".//states//@initstate" />;
    
    public void Reset()
    {
      <xsl:value-of select=".//states//@varname" /> = States.<xsl:value-of select=".//states//@initstate" />;
    }
    
    public States State
    {
      get
      {
        return <xsl:value-of select=".//states//@varname" />;
      }
    }

    public bool AcceptChar(char c)
    {
      switch(<xsl:value-of select=".//states//@varname" />)
      {
<xsl:for-each select=".//states//state" >
      case States.<xsl:value-of select="@name" />:
<xsl:apply-templates select="input"/>
        //throw new Exception("Invalid input character");
	return false;
</xsl:for-each>
      default:
        //throw new Exception("Invalid state value");
	return false;
      }
    }
  }
</xsl:template>

<!-- processing of input character -->
<xsl:template match="input" >
       if (-1 != ("<xsl:value-of select="@val" />").IndexOf(c))
       {
         <xsl:value-of select="..//..//@varname" /> = States.<xsl:value-of select="@newstate" />;
         return true;
       }       
</xsl:template>

</xsl:stylesheet>
