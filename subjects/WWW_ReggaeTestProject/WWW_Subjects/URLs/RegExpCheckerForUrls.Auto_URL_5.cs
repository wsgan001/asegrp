
using System;
namespace RegExpCheckerForUrls
{

  public class Auto_URL_5
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

       if (-1 != ("/").IndexOf(c))
       {
         _state = States.STATE1;
         return true;
       }       

        //throw new Exception("Invalid input character");
	return false;

      case States.STATE1:

    if (-1 != ("-!#$%&'*+0123456789=?ABCDEFGHIJKLMNOPQRSTUVWXYZ^_`abcdefghijklmnopqrstuvwxyz{|}~./:\\").IndexOf(c))
       {
         _state = States.STATE8;
         return true;
       }       

        //throw new Exception("Invalid input character");
	return false;

      case States.STATE2:

       if (-1 != ("t").IndexOf(c))
       {
         _state = States.STATE5;
         return true;
       }       

        //throw new Exception("Invalid input character");
	return false;

      case States.STATE3:

       if (-1 != ("f").IndexOf(c))
       {
         _state = States.STATE2;
         return true;
       }       

       if (-1 != ("h").IndexOf(c))
       {
         _state = States.STATE7;
         return true;
       }       

        //throw new Exception("Invalid input character");
	return false;

      case States.STATE4:

       if (-1 != ("/").IndexOf(c))
       {
         _state = States.STATE0;
         return true;
       }       

        //throw new Exception("Invalid input character");
	return false;

      case States.STATE5:

       if (-1 != ("p").IndexOf(c))
       {
         _state = States.STATE6;
         return true;
       }       

        //throw new Exception("Invalid input character");
	return false;

      case States.STATE6:

       if (-1 != (":").IndexOf(c))
       {
         _state = States.STATE4;
         return true;
       }       

        //throw new Exception("Invalid input character");
	return false;

      case States.STATE7:

       if (-1 != ("t").IndexOf(c))
       {
         _state = States.STATE2;
         return true;
       }       

        //throw new Exception("Invalid input character");
	return false;

      case States.STATE8:

    if (-1 != ("-!#$%&'*+0123456789=?ABCDEFGHIJKLMNOPQRSTUVWXYZ^_`abcdefghijklmnopqrstuvwxyz{|}~./:\\").IndexOf(c))
       {
         _state = States.STATE8;
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

