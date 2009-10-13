import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import com.sun.xml.internal.stream.writers.XMLWriter;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.RegExp;
import dk.brics.automaton.State;
import dk.brics.automaton.Transition;

public class Regex2Automaton {

	
	static HashMap<Integer,Integer> idMap;
	static int id = 0; 
	static HashSet<Integer> visitedStates;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		idMap = new HashMap<Integer, Integer>();
		String transRegEx = args[0];
		RegExp exp = new RegExp(transRegEx);
		String nameSpace = args[1];
		String className = args[2];
		String directory = args[3];
		Automaton genAutomata = exp.toAutomaton();
		File tempFile = new File(directory,nameSpace+"."+className+".xml");
		LinkedList<State> exploredStates = new LinkedList<State>();
		visitedStates = new HashSet<Integer>();
		try {
			FileWriter writer = new FileWriter(tempFile, false);
			BufferedWriter buffWriter = new BufferedWriter(writer);
			/**Start of XML file**/
			buffWriter.append("<?xml version=\"1.0\" ?>");
			buffWriter.append("\n");
			buffWriter.append("<parser-descriptor namespace=\"" +nameSpace+"\">");
			buffWriter.append("\n");
			buffWriter.append("<class-descriptor classname=\"" + className+"\">");
			buffWriter.append("\n");
			buffWriter.append("<states varname=\"_state\" initstate=\"");
						
			State currState = genAutomata.getInitialState();
			exploredStates.add(currState);
			
			buffWriter.append("STATE"+ id + "\" >");
			
			idMap.put(new Integer(currState.hashCode()),new Integer(id++));
			
			/**States and Transitions**/
			while(!exploredStates.isEmpty())
				exploredStates = appendState(exploredStates, buffWriter);
			/**End of XML file**/
			buffWriter.append("\n");
			buffWriter.append("</states>");
			buffWriter.append("\n");
			buffWriter.append("</class-descriptor>");
			buffWriter.append("\n");
			buffWriter.append("</parser-descriptor>");
			buffWriter.close();
			System.out.println("Finished writing XML file");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static LinkedList<State> appendState(
			LinkedList<State> exploredStates,BufferedWriter buffWriter) throws IOException {
		
		State currState = exploredStates.pop();
		if(!visitedStates.isEmpty() && visitedStates.contains(currState.hashCode()))
		{
			return exploredStates;
		}
		String nextStateName = idMap.get(currState.hashCode()).toString();
		char[] arrayofChars;
		String toAppend = "\n";
		toAppend = toAppend.concat("<state name=\"");
		toAppend = toAppend.concat("STATE"+ nextStateName);
		toAppend = toAppend.concat("\">");
		nextStateName = null;
		arrayofChars = new char[currState.getTransitions().size()];
		int count = 0;
		for (Transition trans : currState.getTransitions()) {
			toAppend = toAppend.concat("\n");
			toAppend = toAppend.concat("<input val=");
			toAppend = toAppend.concat("\"");
			char temp = trans.getMax();
			arrayofChars[count] = temp;
			if(idMap.get(trans.getDest().hashCode()) == null)
			{
				idMap.put(new Integer(trans.getDest().hashCode()), new Integer(id++));
			}
			exploredStates.add(trans.getDest());
			toAppend = toAppend.concat(new String(new char[] { temp }));
			toAppend = toAppend.concat("\" newstate=\"");
			toAppend = toAppend.concat("STATE"+idMap.get(trans.getDest().hashCode()).toString());
			toAppend = toAppend.concat("\" />");
			count = count + 1;
		}
		toAppend = toAppend.concat("\n </state>");
		visitedStates.add(currState.hashCode());
		
		buffWriter.append(toAppend);
		return exploredStates;
	}
}
