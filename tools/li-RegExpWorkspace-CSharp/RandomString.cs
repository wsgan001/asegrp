using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using CloneFinder;
using Microsoft.Pex.Framework.Suppression;
using Microsoft.Pex.Framework.Explorable;

namespace AutomatonToCode
{
    
    class RandomString
    {
        [PexIgnore("ignore explanation")]
        public static void RandomStrings_settable(string outputfile, int total, int minL, int maxL)
        {
            Random _random = new Random();                 
            StringBuilder printer = new StringBuilder();

            for (int j = 0; j < total; j++)
            {
                StringBuilder builder = new StringBuilder();   
                for (int i = 0; i < _random.Next(_random.Next(minL, maxL)); i++)
                {                    
                    //26 letters in the alfabet, ascii + 65 for the capital letters
                    builder.Append(Convert.ToChar(Convert.ToInt32(Math.Floor(26 * _random.NextDouble() + 65))));
                }                
                printer.Append(builder.ToString() + "\n");
            }

            FileUtils.WriteTxt(printer.ToString(), outputfile);
        }

        [PexRandomExplorableStrategy]
        public static string[] RandomStrings()
        {
            Random _random = new Random();            
            String[] strings = new String[100];
            StringBuilder printer = new StringBuilder();

            for (int j = 0; j < strings.Length; j++)
            {
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < _random.Next(_random.Next(1, 15)); i++)
                {
                    //26 letters in the alfabet, ascii + 65 for the capital letters
                    builder.Append(Convert.ToChar(Convert.ToInt32(Math.Floor(26 * _random.NextDouble() + 65))));
                }
                strings[j] = builder.ToString();
                printer.Append(strings[j] + "\n");
            }

            return strings;

        }

    }
}
