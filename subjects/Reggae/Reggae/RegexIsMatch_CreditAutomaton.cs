using System;
namespace AutomatonToCode.example
{
    public partial class Regex
    {
        public static bool IsMatch(string msg, string regex)
        {
            bool flag = false;
            int length = msg.Length;
            switch (regex)
            {
                case @"^[\w\-]+@([\w\-]+\.)+[\w\-]+$":
                    {
                        CreditAutomaton auto_CreditAutomaton = new CreditAutomaton();
                        for (int i = 0; i < length; ++i)
                        {
                            char c = msg[i];
                            flag = auto_CreditAutomaton.AcceptChar(c);
                            if (!flag)
                                return false;
                            else
                                continue;
                        }
                        if (auto_CreditAutomaton.State.ToString().Equals("STATE2"))
                            return true;
                        else
                            return false;
                    }
                case @"^[\d]{4}\-[\d]{4}\-[\d]{4}\-[\d]{4}$":
                    {
                        EmailAutomaton auto_EmailAutomaton = new EmailAutomaton();
                        for (int i = 0; i < length; ++i)
                        {
                            char c = msg[i];
                            flag = auto_EmailAutomaton.AcceptChar(c);
                            if (!flag)
                                return false;
                            else
                                continue;
                        }
                        if (auto_EmailAutomaton.State.ToString().Equals("STATE19"))
                            return true;
                        else
                            return false;
                    }
                case @"^[0-9a-zA-Z-_\\.\\+]+@([0-9a-zA-Z-_]+\\.)+[a-zA-Z]+$":
                    {
                        Reggae.EMail_WWW auto_EmailAutomaton = new Reggae.EMail_WWW();
                        for (int i = 0; i < length; ++i)
                        {
                            char c = msg[i];
                            flag = auto_EmailAutomaton.AcceptChar(c);
                            if (!flag)
                                return false;
                            else
                                continue;
                        }
                        if (auto_EmailAutomaton.State.ToString().Equals("STATE19"))
                            return true;
                        else
                            return false;
                    }
            }
            System.Console.WriteLine(regex + " isn't in IsMatch");
            return false;
        }
    }
}

