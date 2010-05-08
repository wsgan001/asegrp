
#Reads the Association Miner input file of the following form:

#12.70777.0 12.70778.0 12.70779.1 
#12.70780.0 12.70778.0 12.70781.1 
#12.70782.0 12.70777.0 12.70778.0 12.70781.1 

#performs following steps

#a. Tranforms the data as suitable to Sequence Miner
#b. Runs the sequence miner on the transformed data
#c. Postprocesses the transformed data

# Types of miner: 
# 0 : Frequent Itemset Miner
# 1 : Frequent Subsequence Miner
$MINER_TYPE = 0;

$input_mid = $ARGV[0];
$input_filename = "AssocMiner_Data/".$input_mid.".txt";
open(INPUT, "<$input_filename");
open(OUTPUT, ">AssocMinerDataNum_Transformed.txt");

$LOWER_THRESHOLD = 0.1;

%IDMapper = ();
@revMapper = ();
$UNIQUE_ID_GEN = 1;

$numSequences = 0;
$maxLengthSeq = 0;
$totalSeqLen = 0;

print "Processing: ".$input_mid."\n";

while ($line = <INPUT>) {
	
	#Each line is of the format: 12.70777.0 12.70778.0 12.70779.1
	chop($line);
	chomp($line);
	@parts = split(" ", $line);
	$numParts = @parts;
	
	$totalSeqLen += $numParts;
	if($maxLengthSeq < $numParts) {
		$maxLengthSeq = $numParts;
	}

	foreach $elem (@parts)  {

		$elem = trim($elem);
			
		if($IDMapper{$elem}{"ID"} == 0) {
			$revMapper[$UNIQUE_ID_GEN] = $elem;
			$IDMapper{$elem}{"ID"} = $UNIQUE_ID_GEN++;
		}

		print OUTPUT $IDMapper{$elem}{"ID"}." ";
	}
	
	if($MINER_TYPE == 1) {
		print OUTPUT "-1 \n";
	} else {
		print OUTPUT "\n";
	}	
	$numSequences++;
}

#Additional file required for BIDE
if($MINER_TYPE == 1) {
	print OUTPUT "-2";
	$avglen = $totalSeqLen / $numSequences;

	open (SPEC, ">AssocMiner.spec");
	print SPEC "AssocMinerDataNum_Transformed.txt\n";
	print SPEC "$UNIQUE_ID_GEN\n";
	print SPEC "$numSequences\n";
	print SPEC "$maxLengthSeq\n";
	print SPEC "$avglen\n";

	print SPEC "# $UNIQUE_ID_GEN : number of unique items \n";
	print SPEC "# $numSequences : number of sequences\n";
	print SPEC "# $maxLengthSeq : maximal length of a sequence\n";
	print SPEC "# $avglen : average length of a sequence\n";
	close SPEC;
}

close INPUT;
close OUTPUT;

if($MINER_TYPE == 1) {
	#Running BIDE on the generated files. BIDE generates frequency.dat file
	system("bide_with_output.exe AssocMiner.spec 0.4");
} else {
	#Mafia is run with frequent closed itemsets here.
	#This option is very important for computing back the support values for each element
	system("mafia.exe -fci .2 -ascii AssocMinerDataNum_Transformed.txt frequent.dat");
}

open(INPUT, "<frequent.dat");
@freqItemsets = <INPUT>;
close INPUT;


#unlink("AssocMinerDataNum_Transformed.txt");
#unlink("frequent.dat");

@sortedFreqItemSets = ();
$totalFrequency = 0;
foreach $freqElem (@freqItemsets) {
	chop($freqElem);
	chop($freqElem);
	if($MINER_TYPE == 1) {
		#Each entry is of the form "14 13 3 1 2 : 1"
		@parts = split(" : ", $freqElem);	
	} else {
		@parts = ();
		#Each entry is of the form "14 13 3 1 2 (1)"
		if($freqElem =~ /^([^\(]+)(\()([^\)]+)(\))([ \t]*)$/) {
			$parts[0] = $1;
			$parts[1] = $3;
		}
	}
	@subparts = split(" ", $parts[0]);

	if($MINER_TYPE == 0) {
		#As frequent itemset miner does not preserve any order among elements
		@subparts = sort @subparts;	
	}

	$formattedStr = "";
	if($MINER_TYPE == 1) {
		$formattedStr = join(" ", @subparts);
		$formattedStr = $formattedStr." : ".$parts[1];
	} else {
		$formattedStr = join(" ", @subparts);
		$formattedStr = $formattedStr." (".$parts[1].")";
	}

	push @sortedFreqItemSets, $formattedStr;
	$totalFrequency = $totalFrequency + $parts[1];
}


$output_file = "AssocMiner_Data/".$input_mid."_output.txt";
#open(OUTPUT, ">$output_file");

#Reading the out from BIDE and storing frequencies
%FreqMapper = ();									# Captures frequency for each itemset
%aloneLeftPartFreqMapper = ();						# Captures maximum occuring frequency for a left part	(VV IMP in computing support values)
@leftPartArr = ();
@rightPartArr = ();
$minePatternCnt = 0;

foreach $line (@sortedFreqItemSets) {
	
	if($MINER_TYPE == 1) {
		#Each entry is of the form "14 13 3 1 2 : 1"
		@parts = split(" : ", $line);	
	} else {
		@parts = ();
		#Each entry is of the form "14 13 3 1 2 (1)"
		if($line =~ /^([^\(]+)(\()([^\)]+)(\))([ \t]*)$/) {
			$parts[0] = $1;
			$parts[1] = $3;
		}
	}
	@subparts = split(" ", $parts[0]);

	$leftPart = "";
	$rightPart = "";
	foreach $subpart (@subparts) {
		$equiId = $revMapper[$subpart];

		if($equiId =~ /\.0[ ]*$/) {
			$leftPart = $leftPart.$equiId." ";
		} else {
			if($equiId =~ /\.1[ ]*$/) {
				$rightPart = $rightPart.$equiId." ";
			} else {
				print "Should not occur!!!\n";
			}
		}
	}

	if($leftPart ne "") {
		$leftPart = substr($leftPart, 0, length($leftPart) - 1);
	}
	if($rightPart ne "") {
		$rightPart = substr($rightPart, 0, length($rightPart) - 1);
	}

	#$leftPart = trim($leftPart);
	#$rightPart = trim($rightPart);
	#print OUTPUT $leftPart." -> ".$rightPart." : ".$parts[1]."\n";	
	
	#Ignore those elements with no left parts
	if($leftPart eq "") {
		next;	
	}		

	$leftPartArr[$minePatternCnt] = $leftPart;
	$rightPartArr[$minePatternCnt] = $rightPart;
	$minePatternCnt++;	

	if($rightPart ne "") {
		$FreqMapper{$leftPart." ".$rightPart}{"frequency"} = $parts[1];
	} else {	
		$FreqMapper{$leftPart}{"frequency"} = $parts[1];
	}

	$existingVal = $aloneLeftPartFreqMapper{$leftPart}{"frequency"};
	if($existingVal < $parts[1]) {
		$aloneLeftPartFreqMapper{$leftPart}{"frequency"} = $parts[1];
	}
}


#Computing support values for each transaction


#Transforming back into patterns using ID information

open(METHODIDS, "<AssocMethodIds.txt");
%assocMethodIdMapper = ();
@midArr = ();
while($line = <METHODIDS>) {
	chop($line);
	chop($line);
	#Each line is of the format "5 : java.sql.PreparedStatement:close():void"
	if($line =~ /([^ ]+)( : )(.*)$/) {
		$keyElem = $1;	
		$assocMethodIdMapper{$1}{"mname"} = $3;
			
		#As we are doing method by method mining, we only need submethod ids of this particular method-invocation
		if($keyElem != $input_mid) {
			next;
		}

		open(SUBMETHODIDS, "<AssocMiner_IDs/$keyElem.txt");
		
		push @midArr,$keyElem;
		while($innerM = <SUBMETHODIDS>) {
			chop($innerM);
			chop($innerM);
			if($innerM  =~ /([^ ]+)( : )(.*)$/) {
				$assocMethodIdMapper{$keyElem}{$1}{"mname"} = $3;
			}
		}
		close SUBMETHODIDS;
	}	
}
close METHODIDS;

$anchor_method = $assocMethodIdMapper{$input_mid}{"mname"};
$numPatterns = 0;
%result_mapper = ();			#Stores the end results
for($tcnt = 0; $tcnt < $minePatternCnt; $tcnt++) {
	#As we interested in association rules, ignore sequences / itemsets with no right side
	if($rightPartArr[$tcnt] eq "") {
		next;
	}			

	$freq = $FreqMapper{$leftPartArr[$tcnt]." ".$rightPartArr[$tcnt]}{"frequency"};	
	if($freq == 1) {
		next;		# No need to consider patterns of frequency as such patterns won't help for bug-finding.
	}


	$pattern = "";
	
	if($MINER_TYPE == 1) {
		#For frequent subsequence mining, the last element in the left part should be anchor	
		@leftPartArrSub = split(" ", $leftPartArr[$tcnt]);
		$leftPartArrSub_Cnt = @leftPartArrSub;
		foreach ($left_cnt = 0; $left_cnt < ($leftPartArrSub_Cnt - 1); $left_cnt ++) {
			$leftElem = $leftPartArrSub[$left_cnt];
			if($leftElem =~ /^([0-9]+)(\.)([0-9]+)(\.)([0-9]+)$/) {
				$MIId = $1;
				$SubMIId = $3;	
				$pattern = $pattern.$assocMethodIdMapper{$MIId}{$SubMIId}{"mname"}."#";
			}
		}

		if($leftPartArrSub_Cnt == 1) {
			$pattern = $pattern." ";
		}

		#Check whether the last element in the left element is same as the anchor method.
		#If not that pattern is of no use for us.
		$leftElem = $leftPartArrSub[$leftPartArrSub_Cnt - 1];
		if($leftElem =~ /^([0-9]+)(\.)([0-9]+)(\.)([0-9]+)$/) {
			$MIId = $1;
			$SubMIId = $3;	
			$lastMethod = $assocMethodIdMapper{$MIId}{$SubMIId}{"mname"};
		}

		$retVal = compareMethodInvocations($lastMethod, $anchor_method);
		if($retVal == 0) {
			next;
		}	
	} else {
		#For itemset mining, anchor can present anywhere in the left part
		@leftPartArrSub = split(" ", $leftPartArr[$tcnt]);
		$leftPartArrSub_Cnt = @leftPartArrSub;
		$bAnchor_found = 0;
		foreach ($left_cnt = 0; $left_cnt < ($leftPartArrSub_Cnt); $left_cnt ++) {
			$leftElem = $leftPartArrSub[$left_cnt];
			if($leftElem =~ /^([0-9]+)(\.)([0-9]+)(\.)([0-9]+)$/) {
				$MIId = $1;
				$SubMIId = $3;	
				
				#Checking whether this is an anchor method
				$curr_method = $assocMethodIdMapper{$MIId}{$SubMIId}{"mname"};	
				$retVal = compareMethodInvocations($curr_method, $anchor_method);
				if($retVal == 0) {
					#Current method not an anchor method, so append to the pattern
					$pattern = $pattern.$curr_method."#";
				} else {
					#Current method is an anchor method. So no need to append to the pattern
					$bAnchor_found = 1;
				}		
			}
		}

		if($leftPartArrSub_Cnt == 1) {
			$pattern = $pattern." ";
		}

		if($bAnchor_found == 0) {
			#Ignore this pattern, if anchor is not found.
			next;
		}
	}

	$pattern = $pattern.",".$anchor_method.",";

	@rightPartArrSub = split(" ", $rightPartArr[$tcnt]);
	$numRightElem = @rightPartArrSub;
	$t_right_elem = 0;
	foreach $rightElem (@rightPartArrSub) {
		if($rightElem =~ /^([0-9]+)(\.)([0-9]+)(\.)([0-9]+)$/) {
			$t_right_elem++;
			$MIId = $1;
			$SubMIId = $3;
			if($t_right_elem == $numRightElem) {
				$pattern = $pattern.$assocMethodIdMapper{$MIId}{$SubMIId}{"mname"};
			} else {
				$pattern = $pattern.$assocMethodIdMapper{$MIId}{$SubMIId}{"mname"}."#";
			}
		}
	}
	
	$pattern = $pattern.",".$leftPartArr[$tcnt]." -> ".$rightPartArr[$tcnt].",".$freq." ";		

	$leftPartSupport = $FreqMapper{$leftPartArr[$tcnt]}{"frequency"};
	if($leftPartSupport == 0) {
		$leftPartSupport = $aloneLeftPartFreqMapper{$leftPartArr[$tcnt]}{"frequency"};
	}
	
	if($leftPartSupport != 0) {
		$supportVal = $freq / $leftPartSupport;
	} else {
		$supportVal = 1;
	}		

	$pattern = $pattern.",".$supportVal;

	#Decide whether to consider this pattern. Ignore those patterns with lowest relative frequency
	$relative_support = $freq / $totalFrequency;
	if($relative_support >= $LOWER_THRESHOLD) {	
		$result_mapper{$numPatterns}{"PatternList"} = $pattern.",".$relative_support;
		$result_mapper{$numPatterns}{"frequency"} = $freq;
		$numPatterns = $numPatterns + 1;
 	}
}

#Printing the final output as patterns to a file
open(CONSOLIDATED, ">>AssocMinerData_MinedConsolidated.csv");
for ($tcnt = 0; $tcnt < $numPatterns; $tcnt++) {
	print CONSOLIDATED ($tcnt + 1).",".$result_mapper{$tcnt}{"PatternList"}."\n";		 
}
close CONSOLIDATED;

# ********************* Assisting Subroutines ***********************
sub trim() {
	my($param) = @_;

	if($param =~ /^[ ]*$/) {
		return "";	
	}

	if($param =~ /^([ ]*)(.*)$/) {
		$param = $2;
	}

	if($param =~ /^(.*)([ ]*)$/) {
		$param = $1;
	}
	
	return $param;
}	

#Compares two method invocations by ignoring their arguments
# OUTPUT: 0 -> Not equal
#		  1 -> Equal
sub compareMethodInvocations
{
		my $lastMethod = shift;
		my $anchor_method = shift;

		#Compare lastMethod with anchor_method ignoring arguments
		#Format: java.util.Map:get(java.lang.Object::):java.lang.Object
		if($lastMethod =~ /^([^:]+)(:)([^(]+)(.*)$/) {
			$lastMethodClass = $1;
			$lastMethodName = $3;
		}

		if($anchor_method =~ /^([^:]+)(:)([^(]+)(.*)$/) {
			$anchorMethodClass = $1;
			$anchorMethodName = $3;
		}	

		if($lastMethodClass ne $anchorMethodClass || $lastMethodName ne $anchorMethodName) {
			return 0;
		}

		return 1;
}