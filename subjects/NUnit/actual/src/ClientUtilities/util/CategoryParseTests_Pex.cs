using System;
using NUnit.Core;
using Microsoft.Pex.Framework;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using NUnit.Core.Filters;
using System.Collections.Generic;
using System.Text.RegularExpressions;

namespace NUnit.Util.Tests
{
    [PexClass(typeof(CategoryExpression))]
    public partial class CategoryParseTests
    {
        //Combined to ParseCategoryPUT
        [PexMethod]
        public void EmptyStringReturnsEmptyFilter()
        {
            CategoryExpression expr = new CategoryExpression("");
            PexAssert.IsTrue(expr.Filter.IsEmpty);
        }

        /*  [PexMethod]
          public void EmptyStringReturnsEmptyFilterPUT([PexAssumeUnderTest]CategoryExpression expr)
          {
              //CategoryExpression expr = new CategoryExpression("");
              PexAssert.IsTrue(expr.Filter.IsEmpty);
          }
          */
        //Combined to ParseCategoryPUT
        [PexMethod]
        public void CanParseSimpleCategory()
        {
            CategoryExpression expr = new CategoryExpression("Data");
            CategoryFilter filter = (CategoryFilter)expr.Filter;
            String[] arr = new String[filter.Categories.Count];
            filter.Categories.CopyTo(arr, 0);
            PexAssert.AreEqual("Data", arr[0]);
        }

        /*  [PexMethod]
          public void CanParseSimpleCategoryPUT([PexAssumeUnderTest]CategoryExpression expr)
          {
              CategoryFilter filter = (CategoryFilter)expr.Filter;
              String token = expr.GetToken();
              Console.WriteLine(token);
              PexAssert.AreEqual(filter.Categories, new string[]{token});
          }*/

        //Combined to ParseCategoryPUT
        [PexMethod]
        public void CanParseCompoundCategory()
        {
            CategoryExpression expr = new CategoryExpression("One , Two; Three,Four");
            CategoryFilter filter = (CategoryFilter)expr.Filter;
            String[] arr = new String[filter.Categories.Count];
            filter.Categories.CopyTo(arr,0);
            PexAssert.AreEqual("One", arr[0]);
            PexAssert.AreEqual("Two", arr[1]);
            PexAssert.AreEqual("Three", arr[2]);
            PexAssert.AreEqual("Four", arr[3]);
        }



        /* String token = "";
         int count = s.Split(seperator, 10, StringSplitOptions.None).Length;
         count = count - 1;
         String _dummy = expr.GetToken();
         while (_dummy != null)
         {
             //Console.WriteLine(_dummy);
             if (_dummy != null && !_dummy.Equals(";") && !_dummy.Equals(","))
                 token = token + _dummy ;
             _dummy = expr.GetToken();
         }
         //Console.WriteLine(token);

         */


        [PexMethod]
        public void ParseCategoryPUT([PexAssumeUnderTest]String s)
        {
            PexAssume.IsTrue(s.Length > 0 && !s.Contains("\n"));
            PexAssume.IsTrue(Regex.IsMatch(s, "^(a[;,]b[;,])*$")); //|| Regex.IsMatch(s, "a,b"));
            CategoryExpression expr = new CategoryExpression(s);

            String[] seperator = { ",", ";" };

            s = s.Replace("\n", "");
            if (s.EndsWith(";") || s.EndsWith(","))
            {
                s = s.Remove(s.Length - 1);
            }
            String[] tokens_split = s.Split(seperator, 10, StringSplitOptions.None);
           
            CategoryFilter filter = (CategoryFilter)expr.Filter;
            String[] arr = new String[filter.Categories.Count];
            filter.Categories.CopyTo(arr, 0);
            for (int i = 0; i < tokens_split.Length; i++)
            {
                //Console.WriteLine(arr[i]);
                PexAssert.AreEqual(tokens_split[i],arr[i]);
            }
            
            if (tokens_split.Length == 0)
            {
                PexAssert.IsTrue(expr.Filter.IsEmpty);
            }
        }

        /*
         *   while (charArray[i] != '@' && i<charArray.Length)
            {
                if (charArray[i] < '0' && charArray[i] != '-')
                    flag = false;
                else if (charArray[i] > '9' && charArray[i] < 'A')
                    flag = false;
                else if (charArray[i] > 'Z' && charArray[i] < 'a')
                    flag = false;
                else if (charArray[i] > 'z')
                    flag = false;

                i++;
            }

            if (charArray[i] != '@' || i == 0)
                flag = false;
            else
            {
                i++;
                while (i < charArray.Length)
                {
                    if (charArray[i] < '0' && charArray[i] != '-' && charArray[i] != '.')
                        flag = false;
                    else if (charArray[i] > '9' && charArray[i] < 'A')
                        flag = false;
                    else if (charArray[i] > 'Z' && charArray[i] < 'a')
                        flag = false;
                    else if (charArray[i] > 'z')
                        flag = false;

                    i++;
                }
            }
         * */


        [PexMethod]
        public void CanParseExcludedCategories()
        {
            CategoryExpression expr = new CategoryExpression("-One,Two,Three");
            NotFilter notFilter = (NotFilter)expr.Filter;
            CategoryFilter catFilter = (CategoryFilter)notFilter.BaseFilter;
            Assert.AreEqual(catFilter.Categories, new string[] { "One", "Two", "Three" });
        }


    /*    [PexMethod]
        public void CanParseExcludedCategoriesPUT([PexAssumeUnderTest]CategoryExpression expr)
        {
            //needs to generate Strings[] with atleast one of them satisfying the condition of containing -"
            NotFilter notFilter = (NotFilter)expr.Filter;
            CategoryFilter catFilter = (CategoryFilter)notFilter.BaseFilter;
            String token = expr.GetToken();
            token.Replace("-", "");
            String[] seperator = { ",", ";" };
            String[] tokens_split = token.Split(seperator, 10, StringSplitOptions.None);
            String[] arr = new String[catFilter.Categories.Count];
            catFilter.Categories.CopyTo(arr, 0);
            PexAssert.AreEqual(tokens_split,arr);
        }*/


        [PexMethod]
        public void CanParseMultipleCategoriesWithAnd()
        {
            CategoryExpression expr = new CategoryExpression("One + Two+Three");
            AndFilter andFilter = (AndFilter)expr.Filter;
            Assert.AreEqual(andFilter.Filters.Length, 3);
            CategoryFilter catFilter = (CategoryFilter)andFilter.Filters[0];
            Assert.AreEqual(catFilter.Categories, new string[] { "One" });
            catFilter = (CategoryFilter)andFilter.Filters[1];
            Assert.AreEqual(catFilter.Categories, new string[] { "Two" });
            catFilter = (CategoryFilter)andFilter.Filters[2];
            Assert.AreEqual(catFilter.Categories, new string[] { "Three" });
        }

        [PexMethod]
        public void CanParseMultipleCategoriesWithAndPUT([PexAssumeUnderTest]CategoryExpression expr)
        {
            //CategoryExpression expr = new CategoryExpression("One + Two+Three");
            AndFilter andFilter = (AndFilter)expr.Filter;
            String[] tokens = expr.GetToken().Split('|');
            PexAssert.AreEqual(tokens.Length, andFilter.Filters.Length);
            for (int i = 0; i < andFilter.Filters.Length; i++)
            {
                CategoryFilter catFilter = (CategoryFilter)andFilter.Filters[i];
                PexAssert.AreEqual(catFilter.Categories, tokens[i]);
            }
        }


        [PexMethod]
        public void CanParseMultipleAlternatives()
        {
            CategoryExpression expr = new CategoryExpression("One|Two|Three");
            OrFilter orFilter = (OrFilter)expr.Filter;
            Assert.AreEqual(orFilter.Filters.Length, 3);
            CategoryFilter catFilter = (CategoryFilter)orFilter.Filters[0];
            Assert.AreEqual(catFilter.Categories, new string[] { "One" });
            catFilter = (CategoryFilter)orFilter.Filters[1];
            Assert.AreEqual(catFilter.Categories, new string[] { "Two" });
            catFilter = (CategoryFilter)orFilter.Filters[2];
            Assert.AreEqual(catFilter.Categories, new string[] { "Three" });
        }

        [PexMethod]
        public void CanParseMultipleAlternativesPUT([PexAssumeUnderTest]CategoryExpression expr)
        {
           // CategoryExpression expr = new CategoryExpression("One|Two|Three");
            OrFilter orFilter = (OrFilter)expr.Filter;
            char[] orSeperator = { '|' };
            String[] tokens = expr.GetToken().Split(orSeperator);
            PexAssert.AreEqual(tokens.Length, orFilter.Filters.Length);

            for (int i = 0; i < orFilter.Filters.Length; i++)
            {
                CategoryFilter catFilter = (CategoryFilter)orFilter.Filters[i];
                PexAssert.AreEqual(catFilter.Categories, tokens[i]);
            }
        }

        //Not amenable to test generalization
        [PexMethod]
        public void PrecedenceTest()
        {
            CategoryExpression expr = new CategoryExpression("A + B | C + -D,E,F");
            OrFilter orFilter = (OrFilter)expr.Filter;

            AndFilter andFilter = (AndFilter)orFilter.Filters[0];
            CategoryFilter catFilter = (CategoryFilter)andFilter.Filters[0];
            Assert.AreEqual(catFilter.Categories, new string[] { "A" });
            catFilter = (CategoryFilter)andFilter.Filters[1];
            Assert.AreEqual(catFilter.Categories, new string[] { "B" });

            andFilter = (AndFilter)orFilter.Filters[1];
            catFilter = (CategoryFilter)andFilter.Filters[0];
            Assert.AreEqual(catFilter.Categories, new string[] { "C" });
            NotFilter notFilter = (NotFilter)andFilter.Filters[1];
            catFilter = (CategoryFilter)notFilter.BaseFilter;
            Assert.AreEqual(catFilter.Categories, new string[] { "D", "E", "F" });
        }

        //Not amenable to test generalization
        [PexMethod]
        public void PrecedenceTestWithParentheses()
        {
            CategoryExpression expr = new CategoryExpression("A + (B | C) - D,E,F");
            AndFilter andFilter = (AndFilter)expr.Filter;
            Assert.AreEqual(andFilter.Filters.Length, 3);

            CategoryFilter catFilter = (CategoryFilter)andFilter.Filters[0];
            Assert.AreEqual(catFilter.Categories, new string[] { "A" });

            OrFilter orFilter = (OrFilter)andFilter.Filters[1];
            catFilter = (CategoryFilter)orFilter.Filters[0];
            Assert.AreEqual(catFilter.Categories, new string[] { "B" });
            catFilter = (CategoryFilter)orFilter.Filters[1];
            Assert.AreEqual(catFilter.Categories, new string[] { "C" });

            NotFilter notFilter = (NotFilter)andFilter.Filters[2];
            catFilter = (CategoryFilter)notFilter.BaseFilter;
            Assert.AreEqual(catFilter.Categories, new string[] { "D", "E", "F" });
        }

        
        /*[PexMethod]
        public void PrecedenceTestWithParenthesesPUT(CategoryExpression expr)
        {
            //A + (B | C) - D,E,F
            Stack<int> openParentheses = new Stack<int>();
            Stack<int> closedParentheses = new Stack<int>();
            String token = expr.GetToken();
            char[] tokens = token.ToCharArray();
            for (int i = 0; i < expr.GetToken().Length; i++)
            {
                if (tokens[i] == '(')
                {
                    openParentheses.Push(tokens[i]);
                }
                else if (tokens[i] == ')')
                {
                    closedParentheses.Push(tokens[i]);
                }
            }

            PexAssert.AreEqual();

            AndFilter andFilter = (AndFilter)expr.Filter;
            Assert.AreEqual(andFilter.Filters.Length, 3);

            CategoryFilter catFilter = (CategoryFilter)andFilter.Filters[0];
            Assert.AreEqual(catFilter.Categories, new string[] { "A" });

            OrFilter orFilter = (OrFilter)andFilter.Filters[1];
            catFilter = (CategoryFilter)orFilter.Filters[0];
            Assert.AreEqual(catFilter.Categories, new string[] { "B" });
            catFilter = (CategoryFilter)orFilter.Filters[1];
            Assert.AreEqual(catFilter.Categories, new string[] { "C" });

            NotFilter notFilter = (NotFilter)andFilter.Filters[2];
            catFilter = (CategoryFilter)notFilter.BaseFilter;
            Assert.AreEqual(catFilter.Categories, new string[] { "D", "E", "F" });
        }*/

        //Combined PUT to CombinedPUT
        [PexMethod]
        public void OrAndMinusCombined()
        {
            CategoryExpression expr = new CategoryExpression("A|B-C-D|E");
            OrFilter orFilter = (OrFilter)expr.Filter;
            Assert.AreEqual(orFilter.Filters.Length, 3);
            AndFilter andFilter = (AndFilter)orFilter.Filters[1];
            Assert.AreEqual(andFilter.Filters.Length, 3);
            Assert.AreEqual(andFilter.Filters[0], typeof(CategoryFilter));
            Assert.AreEqual(andFilter.Filters[1], typeof(NotFilter));
            Assert.AreEqual(andFilter.Filters[2], typeof(NotFilter));
        }

        //Combined PUT to CombinedPUT
        [PexMethod]
        public void PlusAndMinusCombined()
        {
            CategoryExpression expr = new CategoryExpression("A+B-C-D+E");
            AndFilter andFilter = (AndFilter)expr.Filter;
            Assert.AreEqual(andFilter.Filters.Length, 5);
            Assert.AreEqual(andFilter.Filters[0], typeof(CategoryFilter));
            Assert.AreEqual(andFilter.Filters[1], typeof(CategoryFilter));
            Assert.AreEqual(andFilter.Filters[2], typeof(NotFilter));
            Assert.AreEqual(andFilter.Filters[3], typeof(NotFilter));
            Assert.AreEqual(andFilter.Filters[4], typeof(CategoryFilter));
        }


        [PexMethod]
        public void CombinedPUT([PexAssumeUnderTest]CategoryExpression expr)
        {
            AndFilter andFilter = (AndFilter)expr.Filter;
            String token = expr.GetToken();
            String[] seperator = { "+", "-"};
            String[] tokens_split = token.Split(seperator, 10, StringSplitOptions.None);
            PexAssert.AreEqual(tokens_split.Length, andFilter.Filters.Length);
            List<String> list = new List<String>();
            char[] plus = { '+' };
            String[] tokens_withMinus = token.Split(plus);
            char[] minus = { '-' };
            for (int i = 0; i < tokens_withMinus.Length; i++)
            {
                //"A+B-C"
                if (tokens_withMinus[i].Contains("-"))
                {
                    String[] subTokens = tokens_withMinus[i].Split(minus);
                    for (int j = 1; j < subTokens.Length; j++)
                    {
                        PexAssert.AreEqual(subTokens[j], typeof(NotFilter));
                    }
                }
            }

            String[] tokens_withPlus = token.Split(minus);
            for (int i = 0; i < tokens_withMinus.Length; i++)
            {
                //"A+B-C"
                if (tokens_withMinus[i].Contains("+"))
                {
                    String[] subTokens = tokens_withMinus[i].Split(plus);
                    for (int j = 1; j < subTokens.Length; j++)
                    {
                        PexAssert.AreEqual(subTokens[j], typeof(CategoryFilter));
                    }
                }
            }
        }

    }
}
