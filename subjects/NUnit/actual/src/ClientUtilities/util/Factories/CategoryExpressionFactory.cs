using Microsoft.Pex.Framework;
using Microsoft.Pex.Framework.Factories;
using System;
using NUnit.Util;
using System.Text.RegularExpressions;

namespace NUnit.Util
{
    [PexFactoryClass]
    public partial class CategoryExpressionFactory
    {
        //[PexFactoryMethod(typeof(CategoryExpression))]
        public static CategoryExpression Create([PexAssumeUnderTest]String s)
        {
            Regex ex = new Regex("*;*");
            //a;b -
            
            PexAssume.IsTrue(ex.IsMatch(s));// && (s.Contains(",") && s.Contains(";")));
            CategoryExpression categoryExpression = new CategoryExpression(s);

            return categoryExpression;
   
        }

    }
}
