// ****************************************************************
// This is free software licensed under the NUnit license. You
// may obtain a copy of the license as well as information regarding
// copyright ownership at http://nunit.org/?p=license&r=2.4.
// ****************************************************************

namespace NUnit.Util.PUTs
{
	using System;
	using System.Collections;
	using Microsoft.Win32;
	//using NUnit.Framework;
    using Microsoft.Pex.Framework;
    using Microsoft.VisualStudio.TestTools.UnitTesting;
    using System.Collections.Generic;
using NUnit.Framework;

	[TestFixture]
	/// <summary>
	/// This fixture is used to test both RecentProjects and
	/// its base class RecentFiles.  If we add any other derived
	/// classes, the tests should be refactored.
	/// </summary>
    [PexClass]
    public partial class RecentFilesTests
	{
		static readonly int MAX = RecentFilesService.MaxSize;
		static readonly int MIN = RecentFilesService.MinSize;

		RecentFilesService recentFiles;

		[TestInitialize] //[SetUp]
		public void SetUp()
		{
			recentFiles = new RecentFilesService( new SettingsGroup( new MemorySettingsStorage() ) );
		}

		#region Helper Methods
		// Set RecentFiles to a list of known values up
		// to a maximum. Most recent will be "1", next 
		// "2", and so on...
		private void SetMockValues( int count )
		{
			for( int num = count; num > 0; --num )
				recentFiles.SetMostRecent( num.ToString() );			
		}

		// Check that the list is set right: 1, 2, ...
		private void CheckMockValues( int count )
		{
			RecentFilesCollection files = recentFiles.Entries;
			PexAssert.AreEqual( count, files.Count, "Count" );
			
			for( int index = 0; index < count; index++ )
				PexAssert.AreEqual( (index + 1).ToString(), files[index].Path, "Item" ); 
		}

		// Check that we can add count items correctly
		private void CheckAddItems( int count )
		{
			SetMockValues( count );
			PexAssert.AreEqual( "1", recentFiles.Entries[0].Path, "RecentFile" );

			CheckMockValues( Math.Min( count, recentFiles.MaxFiles ) );
		}

		// Check that the list contains a set of entries
		// in the order given and nothing else.
		private void CheckListContains( params int[] item )
		{
			RecentFilesCollection files = recentFiles.Entries;
			PexAssert.AreEqual( item.Length, files.Count, "Count" );

			for( int index = 0; index < files.Count; index++ )
				PexAssert.AreEqual( item[index].ToString(), files[index].Path, "Item" );
		}
		#endregion

        [PexMethod]
        public void CountPUT1(int MaxValue)
        {
            recentFiles.MaxFiles = MaxValue;
            PexAssert
                .Case(MaxValue < MIN)
                .Implies(() => MIN == recentFiles.MaxFiles)
                .Case(MaxValue == MIN)
                .Implies(() => MaxValue == recentFiles.MaxFiles)
                .Case(MaxValue > MIN && MaxValue < MAX)
                .Implies(() => MaxValue == recentFiles.MaxFiles)
                .Case(MaxValue == MAX)
                .Implies(() => MaxValue == recentFiles.MaxFiles)
                .Case(MaxValue > MAX)
                .Implies(() => MAX == recentFiles.MaxFiles);                 
        }
        
		//This PUT subsumes all the four previous conventional unit tests
        [PexMethod]
        public void AddItemsPUT1(int numItems)
        {
            PexAssume.IsTrue(numItems >= MIN && numItems <= MAX);
            PexAssert.IsNotNull(recentFiles.Entries);
            PexAssert.AreEqual(0, recentFiles.Count);
            PexAssert.AreEqual(0, recentFiles.Entries.Count);

            for (int num = numItems; num > 0; --num)
                recentFiles.SetMostRecent(num.ToString());

            int numItemsChanged = 0;
            if (numItems >= MIN && numItems <= recentFiles.MaxFiles)
            {
            }
            else
            {
                numItemsChanged = recentFiles.MaxFiles;
            }

            //Asserting the behavior
            for (int num = 1; num <= numItemsChanged; num++)
                PexAssert.AreEqual(recentFiles.Entries[num - 1].Path, num + "");

            //CheckAddItems(1);
        }

        //Additional test cases for achieving 100% coverage
     /*   [PexMethod]
        public void AddItemsPUT2(int numItems)
        {
            PexAssume.IsTrue(numItems >= MIN && numItems <= MAX);
            PexAssert.IsNotNull(recentFiles.Entries);
            PexAssert.AreEqual(0, recentFiles.Count);
            PexAssert.AreEqual(0, recentFiles.Entries.Count);
            
            for (int num = numItems; num > 0; --num)
            {
                recentFiles.SetMostRecent(num.ToString());
                recentFiles.SetMostRecent(num.ToString());
            }

            int numItemsChanged = 0;
            if (numItems >= MIN && numItems <= recentFiles.MaxFiles)
            {
            }
            else
            {
                numItemsChanged = recentFiles.MaxFiles;
            }

            //Asserting the behavior
            for (int num = 1; num <= numItemsChanged; num++)
                PexAssert.AreEqual(recentFiles.Entries[num - 1].Path, num + "");

            //CheckAddItems(1);
        }*/

        [PexMethod]
        public void increasePUT1(int initialSize, int changedSize)
        {
            PexAssume.IsTrue(initialSize >= MIN && initialSize <= MAX);
            PexAssume.IsTrue(changedSize >= MIN && changedSize <= MAX);

            recentFiles.MaxFiles = initialSize;
            for (int num = 1; num <= initialSize; ++num)
                recentFiles.SetMostRecent(num.ToString());

            if (changedSize > initialSize)
            {
                recentFiles.MaxFiles = changedSize;
                for (int num = initialSize + 1; num <= changedSize; num++)
                {
                    recentFiles.SetMostRecent(num.ToString());
                }               
            }
            else
            {
                recentFiles.MaxFiles = changedSize;                
            }

            //Asserting the behavior
            int elementsSize = (initialSize > changedSize) ? initialSize : changedSize;
            for (int count = 0; count < recentFiles.MaxFiles; count++)
            {
                PexAssert.AreEqual(recentFiles.Entries[count].Path, elementsSize + "");
                elementsSize--;
            }
        }

        [PexMethod]
        public void increasePUT2(int initialSize, int changedSize)
        {
            PexAssume.IsTrue((initialSize >= MIN && initialSize <= MAX) || (initialSize < MIN) || (initialSize > MAX));
            PexAssume.IsTrue(changedSize >= MIN && changedSize <= MAX);

            recentFiles.MaxFiles = initialSize;
            if (recentFiles.MaxFiles != initialSize)
            {
                initialSize = recentFiles.MaxFiles;
            }

            for (int num = 1; num <= initialSize; ++num)
                recentFiles.SetMostRecent(num.ToString());

            if (changedSize > initialSize)
            {
                recentFiles.MaxFiles = changedSize;
                for (int num = initialSize + 1; num <= changedSize; num++)
                {
                    recentFiles.SetMostRecent(num.ToString());
                }
            }
            else
            {
                recentFiles.MaxFiles = changedSize;
            }

            //Asserting the behavior
            int elementsSize = (initialSize > changedSize) ? initialSize : changedSize;
            for (int count = 0; count < recentFiles.MaxFiles; count++)
            {
                PexAssert.AreEqual(recentFiles.Entries[count].Path, elementsSize + "");
                elementsSize--;
            }
        }

        [PexMethod]
        public void increasePUT3(int initialSize, int changedSize)
        {
            PexAssume.IsTrue((initialSize >= MIN && initialSize <= MAX) || (initialSize < MIN) || (initialSize > MAX));
            PexAssume.IsTrue(changedSize >= MIN && changedSize <= MAX);

            recentFiles.MaxFiles = initialSize;
            if (recentFiles.MaxFiles != initialSize)
            {
                initialSize = recentFiles.MaxFiles;
            }

            //Adding duplicates
            for (int num = 1; num <= initialSize; ++num)
            {
                recentFiles.SetMostRecent(num.ToString());
                recentFiles.SetMostRecent(num.ToString());
            }
    
            if (changedSize > initialSize)
            {
                recentFiles.MaxFiles = changedSize;
                for (int num = initialSize + 1; num <= changedSize; num++)
                {
                    recentFiles.SetMostRecent(num.ToString());
                }
            }
            else
            {
                recentFiles.MaxFiles = changedSize;
            }

            //Asserting the behavior
            int elementsSize = (initialSize > changedSize) ? initialSize : changedSize;
            for (int count = 0; count < recentFiles.MaxFiles; count++)
            {
                PexAssert.AreEqual(recentFiles.Entries[count].Path, elementsSize + "");
                elementsSize--;
            }
        }

        [PexMethod]
        public void increasePUT4(int initialSize)
        {
            int initialMaxSize = recentFiles.MaxFiles;
            for (int num = 1; num <= initialSize; ++num)
            {                
                recentFiles.SetMostRecent(num.ToString());
            }

            PexAssert.AreEqual(initialMaxSize, recentFiles.MaxFiles);
        }
        
        [PexMethod]
        public void ReorderPUT1(int initialSize, int reorderPosition)
        {
            PexAssume.IsTrue(initialSize >= MIN && initialSize <= MAX);
            PexAssume.IsTrue(reorderPosition >= MIN && reorderPosition <= initialSize);

            recentFiles.MaxFiles = initialSize;
            List<int> oracleList = new List<int>();
            for (int num = 1; num <= initialSize; ++num)
            {
                recentFiles.SetMostRecent(num.ToString());
                oracleList.Add(num);
            }

            oracleList.Reverse();
            recentFiles.SetMostRecent(reorderPosition.ToString());
            oracleList.Remove(reorderPosition);
            oracleList.Insert(0, reorderPosition);

            for (int num = 1; num <= initialSize; ++num)
            {
                Console.WriteLine(recentFiles.Entries[num - 1].Path);
            }

            Console.WriteLine("Oracle elements");
            for (int num = 0; num < oracleList.Count; ++num)
            {
                Console.WriteLine(oracleList[num]);
            }

            //Asserting the behavior
            for (int count = 0; count < initialSize; count++)
            {
                PexAssert.AreEqual(recentFiles.Entries[count].Path, oracleList[count] + "");                
            }
        }

        [PexMethod]
        public void ReorderPUT2(int initialSize, int reorderPosition)
        {
            PexAssume.IsTrue((initialSize >= MIN && initialSize <= MAX) || (initialSize < MIN) || (initialSize > MAX));
            PexAssume.IsTrue(reorderPosition >= MIN && reorderPosition <= initialSize);

            if ((initialSize >= MIN && initialSize <= MAX))
            {
                recentFiles.MaxFiles = initialSize;
            }
            else if (initialSize < MIN)
            {
                recentFiles.MaxFiles = initialSize;
            }
            else
            {
                recentFiles.MaxFiles = initialSize;
            }
            
            List<int> oracleList = new List<int>();
            for (int num = 1; num <= initialSize; ++num)
            {
                recentFiles.SetMostRecent(num.ToString());
                recentFiles.SetMostRecent(num.ToString());
                oracleList.Add(num);
            }

            oracleList.Reverse();
            recentFiles.SetMostRecent(reorderPosition.ToString());
            oracleList.Remove(reorderPosition);
            oracleList.Insert(0, reorderPosition);

            for (int num = 1; num < initialSize; ++num)
            {
                Console.WriteLine(recentFiles.Entries[num - 1].Path);
            }

            Console.WriteLine("Oracle elements");
            for (int num = 0; num < oracleList.Count; ++num)
            {
                Console.WriteLine(oracleList[num]);
            }

            //Asserting the behavior
            for (int count = 0; count < initialSize; count++)
            {
                PexAssert.AreEqual(recentFiles.Entries[count].Path, oracleList[count] + "");
            }
        }

        //In this test case, removePosition indicates the number of 
        //elements that needs to be removed
        [PexMethod]
        public void RemovePUT1(int initialSize, int removePosition)
        {
            PexAssume.IsTrue(initialSize >= MIN && initialSize <= MAX);
            PexAssume.IsTrue(removePosition >= 1 && removePosition < initialSize);

            recentFiles.MaxFiles = initialSize;
            List<int> oracleList = new List<int>();
            for (int num = 1; num <= initialSize; ++num)
            {
                recentFiles.SetMostRecent(num.ToString());
                oracleList.Add(num);
            }

            oracleList.Reverse();
            Random random = new Random();
            for (int num = 0; num < removePosition; num++)
            {
                int randomNo = random.Next(0, initialSize);
                int elemToRemove = oracleList[randomNo];
                oracleList.Remove(elemToRemove);
                recentFiles.Remove(elemToRemove.ToString());                
                initialSize--;
            }
            
            //Asserting the behavior
            for (int count = 0; count < initialSize - 1; count++)
            {
                PexAssert.AreEqual(recentFiles.Entries[count].Path, oracleList[count] + "");
            }
        }

        [PexMethod]
        public void RemovePUT2(int initialSize, int removePosition)
        {
            PexAssume.IsTrue((initialSize >= MIN && initialSize <= MAX) || (initialSize < MIN) || (initialSize > MAX));
            PexAssume.IsTrue(initialSize >= MIN && initialSize <= MAX);
            PexAssume.IsTrue(removePosition >= 1 && removePosition < initialSize);

            if ((initialSize >= MIN && initialSize <= MAX))
            {
                recentFiles.MaxFiles = initialSize;
            }
            else if (initialSize < MIN)
            {
                recentFiles.MaxFiles = initialSize;
            }
            else
            {
                recentFiles.MaxFiles = initialSize;
            }

            List<int> oracleList = new List<int>();
            for (int num = 1; num <= initialSize; ++num)
            {
                recentFiles.SetMostRecent(num.ToString());
                recentFiles.SetMostRecent(num.ToString());
                oracleList.Add(num);
            }

            oracleList.Reverse();
            Random random = new Random();
            for (int num = 0; num < removePosition; num++)
            {
                int randomNo = random.Next(0, initialSize);
                int elemToRemove = oracleList[randomNo];
                oracleList.Remove(elemToRemove);
                recentFiles.Remove(elemToRemove.ToString());
                initialSize--;
            }

            //Asserting the behavior
            for (int count = 0; count < initialSize - 1; count++)
            {
                PexAssert.AreEqual(recentFiles.Entries[count].Path, oracleList[count] + "");
            }
        }
        
	}
}
