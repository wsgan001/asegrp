import java.util.Vector;
import java.util.Iterator;
...
    public void execute() {
        if (message!=null) log(message);
        for (Iterator it=messages.iterator(); it.hasNext(); ) {      // 4
            Message msg = (Message)it.next();
            log(msg.getMsg());
        }
    }


    Vector messages = new Vector();                                  // 2

    public Message createMessage() {                                 // 3
        Message msg = new Message();
        messages.add(msg);
        return msg;
    }

    public class Message {                                           // 1
        public Message() {}

        String msg;
        public void setMsg(String msg) { this.msg = msg; }
        public String getMsg() { return msg; }
    }
...
