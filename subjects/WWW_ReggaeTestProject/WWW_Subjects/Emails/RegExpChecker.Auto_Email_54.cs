
using System;
namespace RegExpChecker
{

  public class Auto_Email_54
  {
    public enum States
    {
STATE0,
STATE1,
STATE2,
STATE3,
STATE4,

    }
    States _state = States.STATE3;
    
    public void Reset()
    {
      _state = States.STATE3;
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
      switch(_state)
      {

      case States.STATE0:

       if (-1 != (".").IndexOf(c))
       {
         _state = States.STATE1;
         return true;
       }       

       if (-1 != ("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ-").IndexOf(c))
       {
         _state = States.STATE0;
         return true;
       }       

        //throw new Exception("Invalid input character");
	return false;

      case States.STATE1:

       if (-1 != ("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ-").IndexOf(c))
       {
         _state = States.STATE2;
         return true;
       }       

        //throw new Exception("Invalid input character");
	return false;

      case States.STATE2:

       if (-1 != ("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ-").IndexOf(c))
       {
         _state = States.STATE2;
         return true;
       }       

        //throw new Exception("Invalid input character");
	return false;

      case States.STATE3:

       if (-1 != ("@").IndexOf(c))
       {
         _state = States.STATE4;
         return true;
       }       

        //throw new Exception("Invalid input character");
	return false;

      case States.STATE4:

       if (-1 != ("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ-").IndexOf(c))
       {
         _state = States.STATE0;
         return true;
       }       

        //throw new Exception("Invalid input character");
	return false;

      default:
        //throw new Exception("Invalid state value");
	return false;
      }
    }
  }

}

