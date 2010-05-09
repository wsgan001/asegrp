package xweb.assocminer;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds the entire data for a single group. Each group includes multiple sequences
 * @author Suresh
 */
public class AssocMinerGroupDataHolder {

	public static int MINER_GROUP_KEY_GEN = 0;
	
	//Stores the key value that is representative of the entire group
	String representativeKey;	
	
	//A unique ID for identifying this group
	int ID = MINER_GROUP_KEY_GEN++;
	
	//List of pattern candidates in this group
	List<String> patternCandidates;
		
	public AssocMinerGroupDataHolder(String representativeKey)
	{
		this.representativeKey = representativeKey;
		patternCandidates = new ArrayList<String>();
	}
	
	public void addPatternCandidate(String candidate)
	{
		this.patternCandidates.add(candidate);
	}

	public int getID() {
		return ID;
	}

	public String getRepresentativeKey() {
		return representativeKey;
	}

	public List<String> getPatternCandidates() {
		return patternCandidates;
	}
}
