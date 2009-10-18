
using System;
namespace RegExpChecker
{

    public class Auto_Email_2
    {
        public enum States
        {
            STATE0,
            STATE1,
            STATE2,
            STATE3,
            STATE4,
            STATE5,
            STATE6,
            STATE7,
            STATE8,
            STATE9,
            STATE10,
            STATE11,
            STATE12,
            STATE13,
            STATE14,
            STATE15,
            STATE16,
            STATE17,
            STATE18,
            STATE19,
            STATE20,
            STATE21,
            STATE22,
            STATE23,
            STATE24,
            STATE25,
            STATE26,
            STATE27,
            STATE28,
            STATE29,
            STATE30,
            STATE31
        }
        States _state = States.STATE0;

        public void Reset()
        {
            _state = States.STATE0;
        }

        public States State
        {
            get
            {
                return _state;
            }
        }

        public bool AcceptChar(char c)
        {
            switch (_state)
            {
                case States.STATE0:
                    if (-1 != ("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ").IndexOf(c))
                    {
                        _state = States.STATE1;
                        return true;
                    }
                    return false;
                case States.STATE1:
                    if (-1 != ("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ").IndexOf(c))
                    {
                        _state = States.STATE1;
                        return true;
                    }
                    if (-1 != (",.-' ").IndexOf(c))
                    {
                        _state = States.STATE2;
                        return true;
                    }
                    if (-1 != ("\n\000d\000c ").IndexOf(c))
                    {
                        _state = States.STATE6;
                        return true;
                    }
                    //throw new Exception("Invalid input character");
                    return false;

                case States.STATE2:
                    if (-1 != ("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ").IndexOf(c))
                    {
                        _state = States.STATE3;
                        return true;
                    }
                    //throw new Exception("Invalid input character");
                    return false;

                case States.STATE3:
                    if (-1 != ("\n\000d\000c ").IndexOf(c))
                    {
                        _state = States.STATE6;
                        return true;
                    }
                    if (-1 != ("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ").IndexOf(c))
                    {
                        _state = States.STATE3;
                        return true;
                    }
                    //throw new Exception("Invalid input character");
                    return false;

                case States.STATE6:
                    if (-1 != ("\n\000d\000c ").IndexOf(c))
                    {
                        _state = States.STATE6;
                        return true;
                    }
                    if (-1 != ("<").IndexOf(c))
                    {
                        _state = States.STATE7;
                        return true;
                    }
                    //throw new Exception("Invalid input character");
                    return false;
                case States.STATE7:
                    if (-1 != ("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_").IndexOf(c))
                    {
                        _state = States.STATE8;
                        return true;
                    }
                    //throw new Exception("Invalid input character");
                    return false;

                case States.STATE8:

                    if (-1 != (".-_abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789").IndexOf(c))
                    {
                        _state = States.STATE8;
                        return true;
                    }
                    if (-1 != ("_abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789").IndexOf(c))
                    {
                        _state = States.STATE9;
                        return true;
                    }
                    //throw new Exception("Invalid input character");
                    return false;

                case States.STATE9:

                    if (-1 != ("@").IndexOf(c))
                    {
                        _state = States.STATE11;
                        return true;
                    }
                    //throw new Exception("Invalid input character");
                    return false;

                case States.STATE11:
                    if (-1 != ("_abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789").IndexOf(c))
                    {
                        _state = States.STATE12;
                        return true;
                    }
                    return false;

                case States.STATE12:
                    if (-1 != (".-_abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789").IndexOf(c))
                    {
                        _state = States.STATE12;
                        return true;
                    }
                    if (-1 != ("_abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789").IndexOf(c))
                    {
                        _state = States.STATE14;
                        return true;
                    }
                    return false;

               case States.STATE14:
                    if (-1 != (".").IndexOf(c))
                    {
                        _state = States.STATE15;
                        return true;
                    }
                    return false;
             
               case States.STATE15:
                    if (-1 != ("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_").IndexOf(c))
                    {
                        _state = States.STATE16;
                        return true;
                    }
                    return false;

               case States.STATE16:
                    if (-1 != ("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_").IndexOf(c))
                    {
                        _state = States.STATE17;
                        return true;
                    }
                    
                    return false;
               case States.STATE17:
                    if (-1 != ("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_").IndexOf(c))
                    {
                        _state = States.STATE18;
                        return true;
                    }
                    if (-1 != (">").IndexOf(c))
                    {
                        _state = States.STATE20;
                        return true;
                    }
                    return false;
               case States.STATE18:
                    if (-1 != ("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_").IndexOf(c))
                    {
                        _state = States.STATE19;
                        return true;
                    }
                    return false;
               case States.STATE19:
                    if (-1 != (">").IndexOf(c))
                    {
                        _state = States.STATE20;
                        return true;
                    }
                    return false;
              
               case States.STATE21:
                    if (-1 != ("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_").IndexOf(c))
                    {
                        _state = States.STATE22;
                        return true;
                    }
                  
                    return false;
               case States.STATE22:
                    if (-1 != ("-.abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_").IndexOf(c))
                    {
                        _state = States.STATE22;
                        return true;
                    }
                    if (-1 != ("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_").IndexOf(c))
                    {
                        _state = States.STATE23;
                        return true;
                    }
                    return false;
                case States.STATE23:
                    if (-1 != ("@").IndexOf(c))
                    {
                        _state = States.STATE25;
                        return true;
                    }
                    return false;
                case States.STATE24:
                    if (-1 != ("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_").IndexOf(c))
                    {
                        _state = States.STATE25;
                        return true;
                    }
                    return false;
                case States.STATE25:
                    if (-1 != ("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_.-").IndexOf(c))
                    {
                        _state = States.STATE25;
                        return true;
                    }
                    if (-1 != ("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_").IndexOf(c))
                    {
                        _state = States.STATE26;
                        return true;
                    }
                    return false;
                case States.STATE26:
                    if (-1 != (".").IndexOf(c))
                    {
                        _state = States.STATE27;
                        return true;
                    }
                    
                    return false;
                case States.STATE27:
                    if (-1 != ("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_").IndexOf(c))
                    {
                        _state = States.STATE28;
                        return true;
                    }

                    return false;
                case States.STATE28:
                    if (-1 != ("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_").IndexOf(c))
                    {
                        _state = States.STATE29;
                        return true;
                    }

                    return false;
                case States.STATE29:
                    if (-1 != ("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_").IndexOf(c))
                    {
                        _state = States.STATE30;
                        return true;
                    }

                    return false;
                case States.STATE30:
                    if (-1 != ("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_").IndexOf(c))
                    {
                        _state = States.STATE31;
                        return true;
                    }

                    return false;
                default:
                    //throw new Exception("Invalid state value");
                    return false;
            }
        }
    }

}

