using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Text.RegularExpressions;

namespace AutomatonToCode.example
{
    class Validator
    {
        public bool InputsChecker(string cardNum, string email)
        {
            if (!Regex.IsMatch(email,
            @"^[\w\-]+@([\w\-]+\.)+[\w\-]+$"))
                return false;
            if (!Regex.IsMatch(cardNum,
            @"^[\d]{4}\-[\d]{4}\-[\d]{4}\-[\d]{4}$"))
                return false;
            string number = ExtractNumbers(cardNum);
            if (!IsLuhnValid(number))
                return false;
            return true;
        }
        private string ExtractNumbers(string cardNum)
        {
            String s = "";
            char[] chars = cardNum.ToCharArray();
            for (int i = 0; i < chars.Length; i++)
            {
                if (Char.IsNumber(chars[i]))
                    s = s + chars[i];
            }
            return s;
        }
        private bool IsLuhnValid(string number)
        {
            int length = number.Length;
            int sum = 0;
            int offset = length % 3;
            byte[] digits = new ASCIIEncoding().GetBytes(number);
            for (int i = 0; i < length; i++)
            {
                digits[i] -= 47;//Should be digits[i] -= 48;
                if (((i + offset) % 2) == 0)
                    digits[i] *= 2;
                sum += (digits[i] > 9) ? digits[i] - 9 : digits[i];
            }
            return (sum % 10 == 0);
        }
    }
}
