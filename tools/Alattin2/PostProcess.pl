
#Reads the Association Miner input file of the following form:

#13 14 15

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

#Types of modes
#0 : Maximal frequent itemset / sequences
#1 : Closed frequence itemset / sequences
$miner_mode = $ARGV[3];

$input_filename = "AssocMiner_Data/".$input_filename;
open(INPUT, "<$input_filename");
open(OUTPUT, ">AssocMinerDataNum_Transformed.txt");

$UNIQUE_ID_GEN = 1;
@revmapper = ();
%IDMapper = ();
$LOWER_THRESHOLD = 0.1;

$numSequences = 0;
$maxLengthSeq = 0;
$totalSeqLen = 0;

print "Processing: ".$input_filename."\n";

while ($line = <INPUT>) {
	
	#Each line is of the format: 13 14 16
	chomp($line);
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
	if($miner_mode == 0)
	{
		$command = "mafia.exe -mfi $support_value -ascii AssocMinerDataNum_Transformed.txt frequent.dat";
	}
	else
	{
		$command = "mafia.exe -mfi $support_value -ascii AssocMinerDataNum_Transformed.txt frequent.dat";
	}
	system($command);
}

open(INPUT, "<frequent.dat");
@freqItemsets = <INPUT>;
close INPUT;


#unlink("AssocMinerDataNum_Transformed.txt");
#unlink("frequent.dat");

#Mainly to sort the ordering of the case of item-set miner
@sortedFreqItemSets = ();
$totalFrequency = 0;
$output_file = "AssocMiner_Data/".$output_filename;
open(OUTPUT_TEMP, ">$output_file");
foreach $freqElem (@freqItemsets) {
	chomp($freqElem);
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
	@reversedsubparts = ();
	foreach $suhpart (@subparts) {
		push @reversedsubparts, $revMapper[$suhpart];
	}

	if($MINER_TYPE == 0) {
		#As frequent itemset miner does not preserve any order among elements
		@subparts_temp = sort { $b <=> $a } @reversedsubparts;	
		@reversedsubparts = @subparts_temp;
	}

	#Print in the common format understood by others.
	$formattedStr = "";
	$formattedStr = join(" ", @reversedsubparts);
	$formattedStr = $formattedStr." : ".$parts[1];
	print OUTPUT_TEMP $formattedStr."\n";
}

close OUTPUT_TEMP;

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
