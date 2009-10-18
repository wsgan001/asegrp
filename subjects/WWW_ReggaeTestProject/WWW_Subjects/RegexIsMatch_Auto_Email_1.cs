using System;
namespace RegExpChecker
{
    public partial class Regex
    {
        public static bool IsMatch(string msg, string regex)
        {
            bool flag = false;
            int length = msg.Length;
            switch (regex)
            {
                case @"^[0-9a-zA-Z\-_\.\+]+\@([0-9a-zA-Z-_]+\.)+[a-zA-Z]+$":
                    {
                        Auto_Email_1 auto_Auto_Email_1 = new Auto_Email_1();
                        for (int i = 0; i < length; ++i)
                        {
                            char c = msg[i];
                            flag = auto_Auto_Email_1.AcceptChar(c);
                            if (!flag)
                                return false;
                            else
                                continue;
                        }
                        if (auto_Auto_Email_1.State.ToString().Equals("STATE1"))
                            return true;
                        else
                            return false;
                    }
                case @"^[a-zA-Z]+(([\'\,\.\- ][a-zA-Z])?[a-zA-Z]*)*\s+<(\w[\-._\w]*\w@\w[\-._\w]*\w\.\w{2,4})>$|^(\w[\-._\w]*\w@\w[\-._\w]*\w\.\w{2,4})$":
                    {
                        Auto_Email_2 auto_Auto_Email_4 = new Auto_Email_2();
                        for (int i = 0; i < length; ++i)
                        {
                            char c = msg[i];
                            flag = auto_Auto_Email_4.AcceptChar(c);
                            if (!flag)
                                return false;
                            else
                                continue;
                        }
                        if (auto_Auto_Email_4.State.ToString().Equals("STATE20") || auto_Auto_Email_4.State.ToString().Equals("STATE29") || auto_Auto_Email_4.State.ToString().Equals("STATE31"))
                            return true;
                        else
                            return false;
                    }
                case @"^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]" + "{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$":
                    {
                        Auto_Email_4 auto_Auto_Email_4 = new Auto_Email_4();
                        for (int i = 0; i < length; ++i)
                        {
                            char c = msg[i];
                            flag = auto_Auto_Email_4.AcceptChar(c);
                            if (!flag)
                                return false;
                            else
                                continue;
                        }
                        if (auto_Auto_Email_4.State.ToString().Equals("STATE1") || auto_Auto_Email_4.State.ToString().Equals("STATE2") || auto_Auto_Email_4.State.ToString().Equals("STATE5") || auto_Auto_Email_4.State.ToString().Equals("STATE11") || auto_Auto_Email_4.State.ToString().Equals("STATE12") || auto_Auto_Email_4.State.ToString().Equals("STATE20") || auto_Auto_Email_4.State.ToString().Equals("STATE21") || auto_Auto_Email_4.State.ToString().Equals("STATE22") || auto_Auto_Email_4.State.ToString().Equals("STATE23") || auto_Auto_Email_4.State.ToString().Equals("STATE24") || auto_Auto_Email_4.State.ToString().Equals("STATE30"))
                            return true;
                        else
                            return false;
                    }
                case "^\\.|^\\@":
                    {
                        Auto_Email_51 auto_Auto_Email_5 = new Auto_Email_51();
                        for (int i = 0; i < length; ++i)
                        {
                            char c = msg[i];
                            flag = auto_Auto_Email_5.AcceptChar(c);
                            if (!flag)
                                return false;
                            else
                                continue;
                        }
                        if (auto_Auto_Email_5.State.ToString().Equals("STATE1"))
                            return true;
                        else
                            return false;
                    }
                case @"\@$":
                    {
                        Auto_Email_52 auto_Auto_Email_6 = new Auto_Email_52();
                        for (int i = 0; i < length; ++i)
                        {
                            char c = msg[i];
                            flag = auto_Auto_Email_6.AcceptChar(c);
                            if (!flag)
                                return false;
                            else
                                continue;
                        }
                        return false;
                    }
                case @"[^A-Za-z0-9\.\@_\-~#]+":
                    {
                        Auto_Email_53 auto_Auto_Email_53 = new Auto_Email_53();
                        for (int i = 0; i < length; ++i)
                        {
                            char c = msg[i];
                            flag = auto_Auto_Email_53.AcceptChar(c);
                            if (!flag)
                                return false;
                            else
                                continue;
                        }
                        if (auto_Auto_Email_53.State.ToString().Equals("STATE1"))
                            return true;
                        else
                            return false;
                    }
                case "@[A-Za-z0-9\\-]+\\.[A-Za-z0-9\\-]+":
                    {
                        Auto_Email_54 auto_Auto_Email_8 = new Auto_Email_54();
                        for (int i = 0; i < length; ++i)
                        {
                            char c = msg[i];
                            flag = auto_Auto_Email_8.AcceptChar(c);
                            if (!flag)
                                return false;
                            else
                                continue;
                        }
                        if (auto_Auto_Email_8.State.ToString().Equals("STATE2"))
                            return true;
                        else
                            return false;
                    }
                case "^[_a-zA-Z0-9-]+(\\.[_a-zA-Z0-9-]+)*@[a-zA-Z0-9]+[a-zA-Z0-9_-]*(\\.[a-zA-Z0-9_-]+)*(\\.[a-zA-Z0-9][a-zA-Z0-9-]{0,10}[a-zA-Z0-9])$":
                    {
                        Auto_Email_6 auto_Auto_Email_10 = new Auto_Email_6();
                        for (int i = 0; i < length; ++i)
                        {
                            char c = msg[i];
                            flag = auto_Auto_Email_10.AcceptChar(c);
                            if (!flag)
                                return false;
                            else
                                continue;
                        }
                        if (auto_Auto_Email_10.State.ToString().Equals("STATE1") || auto_Auto_Email_10.State.ToString().Equals("STATE5") || auto_Auto_Email_10.State.ToString().Equals("STATE8") || auto_Auto_Email_10.State.ToString().Equals("STATE10") || auto_Auto_Email_10.State.ToString().Equals("STATE14") || auto_Auto_Email_10.State.ToString().Equals("STATE15") || auto_Auto_Email_10.State.ToString().Equals("STATE16") || auto_Auto_Email_10.State.ToString().Equals("STATE17") || auto_Auto_Email_10.State.ToString().Equals("STATE18") || auto_Auto_Email_10.State.ToString().Equals("STATE25") || auto_Auto_Email_10.State.ToString().Equals("STATE26"))
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

