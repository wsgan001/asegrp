using System;
using Dsa.Algorithms;
using Microsoft.Pex.Framework;
using NUnit.Framework;
using Microsoft.Pex.Framework.Goals;
using Microsoft.Pex.Framework.Validation;

namespace Dsa.PUTs.Algorithms
{
    /// <summary>
    /// Numbers tests.
    /// </summary>
    [TestFixture]
    [PexClass]
    public sealed partial class NumbersTest
    {
        /**
       * Generalize FibonacciTest, FibonacciNumberLessThanZeroTest
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

        /**
        * Generalize FactorialTest, FactorialNumberLessThanZeroTest
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

        /**
         * Generalize PowerExponentLessThanZeroTest, PowerNotZeroTest
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

        /**
        * Generalize ToBaseTwoNegativeIntTest, ToBaseTwoTest
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

        /**
         * Generalize ToOctalNegativeIntTest, ToOctalTest()
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
                    actual.ToHex();
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
         * (SHOULD NOT BE THE CASE) Failing test cases = 1 - there is no guard against negative integers
         * Comments: Forced stop
         **/
        [PexMethod]
        public void IsPrimePUT(int value)
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
        [PexMethod]
        public void MaxValueHexadecimalTest()
        {
            Assert.AreEqual(255, Numbers.MaxValue(Base.Hexadecimal, 2));
        }

        [PexMethod]
        public void MaxValueBinaryTest()
        {
            Assert.AreEqual(1023, Numbers.MaxValue(Base.Binary, 10));
        }

        [PexMethod]
        public void MaxValueOctalTest()
        {
            Assert.AreEqual(32767, Numbers.MaxValue(Base.Octal, 5));
        }
        
        [PexMethod]
        public void MaxValueDecimalTest()
        {
            Assert.AreEqual(9999, Numbers.MaxValue(Base.Decimal, 4));
        }

    }
}