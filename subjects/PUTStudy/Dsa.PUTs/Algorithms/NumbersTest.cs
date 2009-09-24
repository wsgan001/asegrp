using System;
using Dsa.Algorithms;
using Microsoft.Pex.Framework;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Microsoft.Pex.Framework.Goals;
using Microsoft.Pex.Framework.Validation;

namespace Dsa.PUTs.Algorithms
{
    /// <summary>
    /// Numbers tests.
    /// </summary>
    [TestClass]
    [PexClass]
    public sealed partial class NumbersTest
    {
        /**
       * Generalize FibonacciTest
       * Time: 00:06:25
       * Instrumentation issue = 0
       * Patterns: Manual output, AllowedException
       * Pex Limitations = none
       * Failing test = 1 - when actual=int.MaxValue, an OverflowException is thrown
       **/
        [PexMethod]
        [PexAllowedException(typeof(ArgumentOutOfRangeException))]
        public void FibonacciPUT(int actual)
        {
            if (actual <= 1)
            {
                PexAssert.AreEqual(actual, actual.Fibonacci());
            }
            actual.Fibonacci();
        }

        /** FibonacciNumberLessThanZeroTest - generalization not required, the previous PUT covered this case for positive numbers*/

        /**
        * Generalize FactorialTest
        * Time: 00:05:19
        * Instrumentation issue = 0
        * Patterns: AAA, AllowedException
        * Pex Limitations = none
        **/
        [PexMethod]
        [PexAllowedException(typeof(ArgumentOutOfRangeException))]
        public void FactorialPUT(int actual)
        {
            if (actual < 0)
                actual.Factorial();
            else if (actual < 2)
            {
                PexAssert.AreEqual(1,actual.Factorial());
            }
            else
            {
                int expected = 1;
                for (int i = 2; i <= actual; i++)
                {
                    expected = expected * i;
                }
                PexAssert.AreEqual(expected, actual.Factorial());
            }
        }

        /**FactorialNumberLessThanZeroTest - generalization not required, the previous PUT covered this case for positive numbers*/
        
        /**PowerNotZeroTest - generalization not required, the following PUT covered this case for positive numbers */

        /**
         * Generalize PowerExponentLessThanZeroTest
         * Time: 00:03:20
         * Instrumentation issue = 0
         * Patterns: AAAA, AllowedException
         * Pex Limitations = none
         * Comment - did not test for negative integers
         **/
        [PexMethod]
        [PexAllowedException(typeof(ArgumentOutOfRangeException))]
        public void PowerExponentLessThanZeroPUT(int baseVal, int exponent)
        {
            PexAssume.IsFalse(exponent > 0);
            if (exponent == 0)
                PexAssert.AreEqual(1, Numbers.Power(baseVal, exponent));
            else
            {
                Numbers.Power(baseVal, exponent);
            }
        }

        /**
         * Generalize GcdTest
         * Time: 00:10:43
         * Instrumentation issue = 0
         * Patterns: AAAA, CD
         * Pex Limitations = none
         * Comment - did not test for negative integers
         **/

        [PexMethod]
        public void GcdPUT(int first, int second)
        {
            PexAssume.IsFalse(first < 0 || second < 0);
            if (first == 0 || second == 0)
            {
                first = (first == 0) ? second : first;
            }
            else if(first == 1 || second == 1)
            {
                first = (first == 1) ? first : second;
            }
            else{
                while (first != second)
                {
                    if (first > second)
                    {
                        first = first - second;
                    }
                    else
                    {
                        first = first - second;
                    }
                }
            }
            PexAssert.AreEqual(first, Numbers.GreatestCommonDenominator(first, second));
        }

        /* ToBaseTwoTest - generalization not required, the following PUT covered this case for positive numbers */

        /**
        * Generalize ToBaseTwoNegativeIntTest
        * Time: 00:07:34
        * Instrumentation issue = 0
        * Patterns: AAA, Reachability, CD
        * Pex Limitations = none
        * Failing test cases = 1 (FormatException) - the code under test does not handle the input value 0
        **/
        [PexMethod]
        [PexExpectedGoals]
        public void ToBaseTwoNegativeIntPUT(int actual)
        {
            if (actual < 0)
            {
                try
                {
                    actual.ToBinary();
                }
                catch (ArgumentOutOfRangeException ex)
                {
                    PexGoal.Reached();
                }
            }
            else
            {
                int result = actual.ToBinary();
                int expected = Convert.ToInt32(Convert.ToString(actual, 2));
                PexAssert.AreEqual(expected, result);
            }
        }

        /**ToOctalTest() - generalization not required, the previous PUT covered this case for positive numbers*/

        /**
         * Generalize ToOctalNegativeIntTest
         * Time: 00:09:08
         * Instrumentation issue = 0
         * Patterns: AAA, Reachability,CD
         * Pex Limitations = none
         * Failing test cases = 1 (FormatException) - the code under test does not handle the input value 0
         **/
        [PexMethod]
        [PexExpectedGoals]
        public void ToOctalNegativeIntPUT(int actual)
        {
            if (actual < 0)
            {
                try
                {
                    actual.ToOctal();
                }
                catch (ArgumentOutOfRangeException ex)
                {
                    PexGoal.Reached();
                }
            }
            else
            {
                int expected = Convert.ToInt32(Convert.ToString(actual, 8));
                PexAssert.AreEqual(expected, actual.ToOctal());
            }
        }

        /**
         * Generalize ToHexNegativeIntTest
         * Time: 00:07:09
         * Instrumentation issue = 0
         * Patterns: AAA, Reachability,CD
         * Pex Limitations = none
         * Failing test cases = 1 (FormatException) - the code under test does not handle the input value 0
         **/
        [PexMethod]
        [PexExpectedGoals]
        public void ToHexPUT(int actual)
        {
            if (actual < 0)
                try
                {
                    actual.ToString("X");
                }
                catch (ArgumentOutOfRangeException ex)
                {
                    PexGoal.Reached();
                }
            else
            {
                string hexValue = actual.ToString("X");
                PexAssert.AreEqual(hexValue, actual.ToHex());
            }
        }

        /**
         * Technically generalization of this test is not required, the PUT above covers the test case.
         * Generalize ToHexNegativeIntTest
         * Time: 00:02:02
         * Instrumentation issue = 0
         * Patterns: AAAA, Reachability
         * Pex Limitations = none
         * Failing test cases = none
         **/
        [PexMethod]
        [PexExpectedGoals]
        public void ToHexNegativeIntPUT(int actual)
        {
            PexAssume.IsTrue(actual < 0);
            try
            {
               actual.ToHex();
               PexAssert.IsFalse(true);
            }
            catch (ArgumentOutOfRangeException ex)
            {
                PexGoal.Reached();
            }
        }

        /**
         * Generalize IsPrimeTest()
         * Time: 00:11:54
         * Instrumentation issue = 0
         * Patterns: AAA, StateRelation
         * Pex Limitations = 4 - related to floating point and Math functions
         * Failing test cases = 1 - there is no guard against negative integers
         * Comments: Forced stop
         **/
        [PexMethod]
        public void IsPrimeTest(int value)
        {
            bool expected = true;
            if (value == 0 || value == 1)
                expected = false;
            else
            {
                for (int i = 2; i < value; i++)
                {
                    double temp = i;
                    if (!((value/temp).ToString()).Contains("."))
                    {
                        expected = false;
                        break;
                    }
                }
            }
            PexAssert.AreEqual(expected, value.IsPrime());
        }

        /**
         * Generalize MaxValueDigitsLessThanZeroTest()
         * time taken = 00:09:34
         * Instrumentation issue = 1
         * Patterns: AAAA, Reachability
         * Pex Limitations = none
         * Failing Test cases = 0
         **/
        [PexMethod]
        [PexExpectedGoals]
        public void MaxValueDigitsLessThanZeroPUT(int exponentVal)
        {
            PexAssume.IsFalse(exponentVal > 0);
            if(exponentVal < 0)
                try
                {
                    Numbers.MaxValue(Base.Binary, exponentVal);
                }
                catch (ArgumentOutOfRangeException ex)
                {
                    PexGoal.Reached();
                }
            else
                PexAssert.AreEqual(0, Numbers.MaxValue(Base.Binary, exponentVal));
        }

        // cannot generalize the following
        /**
         * Dsa.Test.Algorithms.NumbersTest.MaxValueBinaryTest() 
         * Dsa.Test.Algorithms.NumbersTest.MaxValueDecimalTest() 
         * Dsa.Test.Algorithms.NumbersTest.MaxValueHexadecimalTest() 
         * Dsa.Test.Algorithms.NumbersTest.MaxValueOctalTest()
         **/

    }
}