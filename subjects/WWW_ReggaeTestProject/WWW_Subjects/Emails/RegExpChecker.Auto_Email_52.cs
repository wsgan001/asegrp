
using System;
namespace RegExpChecker
{

  public class Auto_Email_52
  {
    public enum States
    {
        STATE1
    }
    States _state = States.STATE1;
    
    public void Reset()
    {
      _state = States.STATE1;
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

      default:
        //throw new Exception("Invalid state value");
	return false;
      }
    }
  }

}

