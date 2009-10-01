using System;
using Dsa.Utility;
using Microsoft.Pex.Framework;
using Microsoft.Pex.Framework.Validation;
using Microsoft.Pex.Framework.Goals;
using NUnit.Framework;

namespace Dsa.PUTs.Utility
{
    /// <summary>
    /// Test for the Guard family of methods.
    /// </summary>
    [TestFixture]
    [PexClass]
    public sealed partial class GuardTest
    {
        /// <summary>
        /// Check to see that the correct exception is thrown when the argument being verified is null.
        /// </summary>
        [PexMethod]
        [PexAllowedException(typeof(ArgumentNullException))]
        [PexExpectedGoals]
        /// Summary
        /// Time: 4 min 7 sec
        /// Pattern: AAA, AllowedException, State relation, Reachablility
        /// Combines three unit tests into one        
        public void ArgumentNullParamNameIsNotTest(string s1, string s2)
        {
            try
            {
                Guard.ArgumentNull(s1, s2);
            }
            catch (ArgumentNullException e)
            {
                PexGoal.Reached();  //Makes sure that the goal is reached
                if(s2 == null)
                    PexAssert.AreEqual("parameterName", e.ParamName);
                throw;
            }
        }

        /// <summary>
        /// Check to see that the correct exception is thrown when the paramName is null.
        /// </summary>
        //[Test]
        //[ExpectedException(typeof(ArgumentNullException))]
        //public void ArgumentNullParamNameNullTest()
        //{
        //    const string s = null;

        //    try
        //    {
        //        Guard.ArgumentNull(s, null);
        //    }
        //    catch (ArgumentNullException e)
        //    {
        //        Assert.AreEqual("parameterName", e.ParamName);
        //        throw;
        //    }
        //}

        /// <summary>
        /// Check to see that no exceptions are thrown when the argument is not null.
        /// </summary>
        //[Test]
        //public void ArgumentIsNotNullTest()
        //{
        //    const string s = "Granville";

        //    Guard.ArgumentNull(s, "s");
        //}

        /// <summary>
        /// Check to see that the correct exception is thrown.
        /// </summary>
        [PexMethod]
        [PexAllowedException(typeof(InvalidOperationException))]
        [PexAllowedException(typeof(ArgumentNullException))]
        [PexExpectedGoals("InvalidOperation;ArgumentNull;normalgoal")]
        /// Summary
        /// Time: 5 min 2 sec
        /// Pattern: AAA, AllowedException, Reachablility
        /// Combines three unit tests into one. Also uses the feature of multiple goals
        public void InvalidOperationConditionTrueTest(int x1, int x2, string message)
        {
            try
            {
                Guard.InvalidOperation(x1 < x2, message);
            }
            catch (InvalidOperationException)
            {
                PexGoal.Reached("InvalidOperation");
                throw;
            }
            catch (ArgumentNullException)
            {
                PexGoal.Reached("ArgumentNull");
                throw;
            }
            PexGoal.Reached("normalgoal");
        }

        /// <summary>
        /// Check to see that the correct exception is thrown when the message is null.
        /// </summary>
        //[Test]
        //[ExpectedException(typeof(ArgumentNullException))]
        //public void InvalidOperationMessageNullTest()
        //{
        //    Guard.InvalidOperation(2 < 3, null);
        //}

        /// <summary>
        /// Check to make sure no exception is raised when the condition is false.
        /// </summary>
        //[Test]
        //public void InvalidOperationConditionFalseTest()
        //{
        //    Guard.InvalidOperation(2 > 4, "test");
        //}

        /// <summary>
        /// Check to see that the correct exception is raised when the condition is true.
        /// </summary>
        [PexMethod]
        [PexAllowedException(typeof(ArgumentOutOfRangeException))]
        [PexAllowedException(typeof(ArgumentNullException))]
        [PexExpectedGoals("ArgumentOut;ArgumentNull;Normal")]
        /// Summary
        /// Time: 3 min 30 sec
        /// Pattern: AAA, AllowedException, Reachablility
        /// Combines three unit tests into one. Also uses the feature of multiple goals
        public void ArgumentOutOfRangeConditionTrueTest(int x1, int x2, string message1, string message2)
        {
            try
            {
                Guard.OutOfRange(x1 < x2, message1, message2);
            }
            catch (ArgumentOutOfRangeException)
            {
                PexGoal.Reached("ArgumentOut");
                throw;
            }
            catch (ArgumentNullException)
            {
                PexGoal.Reached("ArgumentNull");
                throw;
            }
            PexGoal.Reached("Normal");
        }

        /// <summary>
        /// Check to make sure no expception is raised.
        /// </summary>
        //[Test]
        //public void ArgumentOutOfRangeConditionFalseTest()
        //{
        //    Guard.OutOfRange(10 < 3, "param", "Oh dear");
        //}

        ///// <summary>
        ///// Check to see that the correct exception is thrown when the paramName is null.
        ///// </summary>
        //[Test]
        //[ExpectedException(typeof(ArgumentNullException))]
        //public void ArgumentOutOfRangeParamNameNullTest()
        //{
        //    Guard.OutOfRange(3 > 4, null, "Test");
        //}

        ///// <summary>
        ///// Check to see that the correct exception is thrown when the message is null. 
        ///// </summary>
        //[Test]
        //[ExpectedException(typeof(ArgumentNullException))]
        //public void ArgumentOutOfRangeMessageNullTest()
        //{
        //    Guard.OutOfRange(3 > 4, "myParam", null);
        //}
    }
}