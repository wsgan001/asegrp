
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

$input_filename = $ARGV[0];
$output_filename = $ARGV[1];
$support_value = $ARGV[2];

$input_filename = "AssocMiner_Data/".$input_filename;
open(INPUT, "<$input_filename");
open(OUTPUT, ">AssocMinerDataNum_Transformed.txt");

$LOWER_THRESHOLD = 0.1;

%IDMapper = ();
@revMapper = ();
$UNIQUE_ID_GEN = 1;

$numSequences = 0;
$maxLengthSeq = 0;
$totalSeqLen = 0;

print "Processing: ".$input_filename."\n";

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
	$command = "mafia.exe -fci $support_value -ascii AssocMinerDataNum_Transformed.txt frequent.dat";
	system($command);
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
		@subparts_temp = sort { $b <=> $a } @subparts;	
		@subparts = @subparts_temp;
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


$output_file = "AssocMiner_Data/".$output_filename;

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
	#print OUTPUT_TEMP $leftPart." -> ".$rightPart." : ".$parts[1]."\n";	
	
	#Ignore those elements with no left parts
	#if($leftPart eq "") {
	#	next;	
	#}		

	$leftPartArr[$minePatternCnt] = $leftPart;
	$rightPartArr[$minePatternCnt] = $rightPart;
	$minePatternCnt++;	

	$FreqMapper{$leftPart." ".$rightPart}{"frequency"} = $parts[1];

	$existingVal = $aloneLeftPartFreqMapper{$leftPart}{"frequency"};
	if($existingVal < $parts[1]) {
		$aloneLeftPartFreqMapper{$leftPart}{"frequency"} = $parts[1];
	}
}

#Transforming back into patterns using ID information
open(OUTPUT_TEMP, ">$output_file");
for($tcnt = $minePatternCnt - 1; $tcnt >= 0 ; $tcnt--) 
{
	$freq = $FreqMapper{$leftPartArr[$tcnt]." ".$rightPartArr[$tcnt]}{"frequency"};		

	if($leftPartArr[$tcnt] ne "") {
		print OUTPUT_TEMP $leftPartArr[$tcnt]." ".$rightPartArr[$tcnt];
	} else {
		print OUTPUT_TEMP $rightPartArr[$tcnt];
	}

	if($MINER_TYPE == 1) {
		#Each entry is of the form "14 13 3 1 2 (1)"
		print OUTPUT_TEMP " (".$freq.")";
	} else {
		#Each entry is of the form "14 13 3 1 2 : 1"
		print OUTPUT_TEMP " : ".$freq;

	}
	print OUTPUT_TEMP "\n";
}

close OUTPUT_TEMP;

#Printing the final output as patterns to a file
#open(CONSOLIDATED, ">>AssocMinerData_MinedConsolidated.csv");
#for ($tcnt = 0; $tcnt < $numPatterns; $tcnt++) {
#	print CONSOLIDATED ($tcnt + 1).",".$result_mapper{$tcnt}{"PatternList"}."\n";		 
#}
#close CONSOLIDATED;

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
