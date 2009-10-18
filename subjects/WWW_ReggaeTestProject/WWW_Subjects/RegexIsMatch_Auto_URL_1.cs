using System;
namespace RegExpCheckerForUrls
{
    public partial class Regex
    {
        public static bool IsMatch(string msg, string regex)
        {
            bool flag = false;
            int length = msg.Length;
            switch (regex)
            {
                case @"http\\:\\/\\/\\S+\\.\\S+":
                    {
                        Auto_URL_1 auto_Auto_URL_1 = new Auto_URL_1();
                        for (int i = 0; i < length; ++i)
                        {
                            char c = msg[i];
                            flag = auto_Auto_URL_1.AcceptChar(c);
                            if (!flag)
                                return false;
                            else
                                continue;
                        }
                        if (auto_Auto_URL_1.State.ToString().Equals("STATE10"))
                            return true;
                        else
                            return false;
                    }
                case @"^(((ht|f)tp(s?))\\://)?(www.|[a-zA-Z0-9].)[a-zA-Z0-9\\-\\.]+\\.[a-zA-Z]{2,6}(\\:[0-9]+)*(/($|[a-zA-Z0-9\\.\\,\\;\\?\\'\\\\\\+&%\\$#\\=~_\\-]+))*$":
                    {
                        Auto_URL_3 auto_Auto_URL_3 = new Auto_URL_3();
                        for (int i = 0; i < length; ++i)
                        {
                            char c = msg[i];
                            flag = auto_Auto_URL_3.AcceptChar(c);
                            if (!flag)
                                return false;
                            else
                                continue;
                        }
                        if (auto_Auto_URL_3.State.ToString().Equals("STATE17") || auto_Auto_URL_3.State.ToString().Equals("STATE23") || auto_Auto_URL_3.State.ToString().Equals("STATE24") || auto_Auto_URL_3.State.ToString().Equals("STATE25"))
                            return true;
                        else
                            return false;
                    }
                case "(http:[/][/]|www.)([a-z]|[A-Z]|[0-9]|[/.]|[~])*":
                    {
                        Auto_URL_4 auto_Auto_URL_4 = new Auto_URL_4();
                        for (int i = 0; i < length; ++i)
                        {
                            char c = msg[i];
                            flag = auto_Auto_URL_4.AcceptChar(c);
                            if (!flag)
                                return false;
                            else
                                continue;
                        }
                        if (auto_Auto_URL_4.State.ToString().Equals("STATE2"))
                            return true;
                        else
                            return false;
                    }
                case @"^(http|ftp)://[-!#$%&'*+\\0-9=?A-Z^_`a-z{|}~\./:]+$":
                    {
                        Auto_URL_5 auto_Auto_URL_5 = new Auto_URL_5();
                        for (int i = 0; i < length; ++i)
                        {
                            char c = msg[i];
                            flag = auto_Auto_URL_5.AcceptChar(c);
                            if (!flag)
                                return false;
                            else
                                continue;
                        }
                        if (auto_Auto_URL_5.State.ToString().Equals("STATE10"))
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

