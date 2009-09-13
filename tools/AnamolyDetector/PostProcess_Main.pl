
# Parent perl file

# This perl script works only with frequent itemset miner

unlink("AssocMinerData_MinedConsolidated.csv");

#open(OUTPUT, ">AssocMinerData_MinedConsolidated.csv");
#close OUTPUT;

open(FINALOUTPUT, ">Alternative_ConsolidatedOutput.txt");
open(TOTALOUTPUT, ">ConsolidatedOutput.csv");
print TOTALOUTPUT "ID, APIName, Position, Balanced(0:yes#1:no), Pattern, GTransactionCnt, GlobSupport, LocSupport, SortingSupport\n";

#Two cut-off threshold values used in the evaluation.
$MIN_SUP = 0.4;
$ALT_SUP = 0.2;

#Additional Counters for summarizing results
$glob_TotalPatCnt = 0;
$glob_SingleChkCnt = 0;
$glob_BalancedPattern = 0;
$glob_ImbalancedPattern = 0;

open(METHODIDS, "<AssocMethodIds.txt");
while($line = <METHODIDS>) {
	chop($line);
	#Each line is of the format "5 : java.sql.PreparedStatement:close():void"
	if($line =~ /([^ ]+)( : )(.*)$/) {
		$keyElem = $1;	
		%assocMethodIdMapper = ();
		$assocMethodIdMapper{$1}{"mname"} = $3;

		#Computing the total number of elements used for mining for each method invocation
		open(LINECOUNT, "<AssocMiner_Data/".$keyElem.".txt");
		@lineCountArr = <LINECOUNT>;
		$globalTransactionCount = @lineCountArr;
		close LINECOUNT;
		#End of computing the total number of elements

		#Skipping the patterns with only one element
		if($globalTransactionCount == 1) {
			next;
		}		

				
		#Run PostProcess.pl for each method ID. It generates a file called "$keyElem_output.txt"
		$command = "perl PostProcess.pl $keyElem.txt $keyElem"."_output.txt ".$MIN_SUP;
		system($command);

		
		#Capturing the dummy method id in the elements of this method invocation	
		$dummy_method_invocation_id = 0;

		open(SUBMETHODIDS, "<AssocMiner_IDs/$keyElem.txt");		
		while($innerM = <SUBMETHODIDS>) {			
			chop($innerM);
			chomp($innerM);
			if($innerM  =~ /([^ ]+)( : )(.*)$/) {
				$assocMethodName_temp = $3;
				$assocMethodID_temp = $1;
				$assocMethodIdMapper{$keyElem}{$assocMethodID_temp}{"mname"} = $assocMethodName_temp;
				
				if($assocMethodName_temp =~ /DUMMY_MINING_ENTRY/) {
					$dummy_method_invocation_id = $assocMethodID_temp;
				}
			}
		}
		close SUBMETHODIDS;		
		
		#Post process the key element
		postProcess($keyElem);
	}
}
close METHODIDS;
close FINALOUTPUT;
close TOTALOUTPUT;

#Outputting the consolidated report
open(STATS, ">Statistics.txt");
print STATS "Total number of patterns: ".$glob_TotalPatCnt."\n";
print STATS "Total number of patterns with single check: ".$glob_SingleChkCnt."\n";
print STATS "Total number of balanced patterns: ".$glob_BalancedPattern."\n";
print STATS "Total number of imbalanced patterns: ".$glob_ImbalancedPattern."\n";
close (STATS);

#SUBROUTINES start from here
sub postProcess
{
	my($keyElem) = @_;

	open(MINEDPATTERNS, "<AssocMiner_Data/".$keyElem."_output.txt");
	@minedPatterns = <MINEDPATTERNS>;
	close MINEDPATTERNS;
	
	print FINALOUTPUT "*****************************\n";	
	print FINALOUTPUT "All patterns of ".$assocMethodIdMapper{$keyElem}{"mname"}."\n";

	$bPrev_glob_ImbalancedPattern = $glob_ImbalancedPattern;
	$numberOfMinedPatterns = 0;
	$supportForSortingPurpose = 0;	#A temporary variable introduced mainly for sorting the final patterns for manual analysis
	
	foreach $pattern (@minedPatterns) {
		#No patterns exist in mined elements
		if($pattern =~ /^[ \t]*:[ \t]*$/) {
			next;
		}

		chop($pattern);		
		@parts = split(" : ", $pattern);
		@allElemArr = split(" ", $parts[0]);

		#chop($parts[1]);
		if($globalTransactionCount != 0) {
			$mainPatternSupport = $parts[1] / $globalTransactionCount;
		} else {
			$mainPatternSupport  = 0;
		}

		#printing to the output
		$totalStringOutput = "";
		$bDummyMethodInvocation = 0;
		
		foreach $allElemContent (@allElemArr) {
			if($allElemContent =~ /^([0-9]+)(\.)([0-9]+)(\.)([0-9]+)$/) {
				$MIId = $1;
				$SubMIId = $3;	
				$before_after = $5;
				
				$t_mname = $assocMethodIdMapper{$MIId}{$SubMIId}{"mname"};
				chop($t_mname); #UNIX

				if($t_mname eq "DUMMY_MINING_ENTR" || $t_mname eq "DUMMY_MINING_ENTRY") {
					$bDummyMethodInvocation = 1;
					last;
				}

				print FINALOUTPUT "(".$allElemContent.") ".$t_mname."\n";

				$t_mname =~ s/,/:/g;
				$totalStringOutput = $totalStringOutput.$t_mname."(".$before_after.")"."&&";
			}
		}

		if($bDummyMethodInvocation == 1) {
			next;
		}
		
		print FINALOUTPUT $pattern." (".$mainPatternSupport.")\n";
		if($supportForSortingPurpose < $mainPatternSupport) {
			$supportForSortingPurpose = $mainPatternSupport;	
		}

		$numberOfMinedPatterns++;

		#Printing to consolidated output mainly to compare with previous results
		$t_name = $assocMethodIdMapper{$keyElem}{"mname"};
		chop($t_name); #UNIX
		$t_name =~ s/,/:/g;
		$endPrintStr = $keyElem.",".$t_name.",".$before_after.","."0".",".$totalStringOutput.",".$globalTransactionCount.",".$mainPatternSupport.",0,".$supportForSortingPurpose."\n";
		$endPrintStr =~ s/\#MULTICURRTYPES\#/MULTICURRTYPES/g;
		$endPrintStr =~ s/\#UNKNOWN\#/UNKNOWN/g;
		print TOTALOUTPUT $endPrintStr;

		#Read all elements and populate into different arrays
		@positiveElem = ();	#Those elements that support the given rule. If the rule contains multiple entries, then all the elements
							#should be present in the input data to classify that line into positive elements
		@negativeElem = (); #Those elements that donot support the given rule. If the rule contains multiple entries, then none of the elements
							#should present in the input data to classify that line into negative elements

		open(INPUT, "<AssocMiner_Data/".$keyElem.".txt");
		while ($line = <INPUT>) {
			#Ignoring the dummy elements while classifying the elements into negatives or positives. As
			#patterns are very sensitive, these dummys are making a lot of difference
			if($line =~ /$dummy_method_invocation_id/) {
				next;
			}

			$bAllElementsExist = 1;
			$bNoneElemExists = 0;

			foreach $rule_elem (@allElemArr) {
				if($line =~ /$rule_elem/) {
					$bNoneElemExists = 1;
				} else { 
					$bAllElementsExist = 0;
				}
			}

			if($bAllElementsExist == 1) {
				#Every element of the rule is available in the data. So this can be classified as a positive element
				push(@positiveElem, $line);
			} 
			
			if($bNoneElemExists == 0) {
				#None of the elements in the rule exists. So, this can be treated as a negative example
				push(@negativeElem, $line);
			}
		}
		close INPUT;

		#Keeping the counts for future
		$negativeElemCount = @negativeElem;
		$positiveElemCount = @positiveElem;

		#Now we have positive and negative elements properly arranged in arrays.

		#Finding the most frequent ones in the negative set.
		
		open (TEMPOUTPUT, ">AssocMiner_Data/".$keyElem."_negatives.txt");
		print TEMPOUTPUT @negativeElem;
		close TEMPOUTPUT;

		#Mining the negative examples
		$command = "perl PostProcess.pl $keyElem"."_negatives.txt $keyElem"."_negatives_output.txt 0.01";
		system($command);

		#patterns among the negative entries				
		open(TEMPINPUT, "<AssocMiner_Data/".$keyElem."_negatives_output.txt");
		@negativeMinedPatterns = <TEMPINPUT>;	
		close TEMPINPUT;
		
		@glob_inp_arr = @positiveElem;
		$bTotalNegativePatterns = 0;
		foreach $negativeElem (@negativeMinedPatterns) {

			#No patterns exist in mined elements
			if($negativeElem =~ /^[ \t]*:[ \t]*$/) {
				next;
			}

			@parts_local = split(" : ", $negativeElem);
			@glob_pat = split(" ", $parts_local[0]);

			#Ignore those patterns that have only a frequency of one as it is 
			#difficult to trust them
			if($parts_local[1] == 1) {
				next;
			}

			#Ignore if this is a negative entry. But there won't be any dummy entries in the negative entries as we don't
			#include them among the patterns entered into negative element database.
#			$bDummyMethodInvocation = 0;
#			foreach $glob_pat_elem (@glob_pat) {
#				if($glob_pat_elem =~ /^([0-9]+)(\.)([0-9]+)(\.)([0-9]+)$/) {
#					$MIId = $1;
#					$SubMIId = $3;	
#
#					$t_mname = $assocMethodIdMapper{$MIId}{$SubMIId}{"mname"};
#					chop($t_mname);
#					if($t_mname eq "DUMMY_MINING_ENTR" || $t_mname eq "DUMMY_MINING_ENTRY") {
#						$bDummyMethodInvocation = 1;
#						last;
#					}				
#				}
#			}
#
#			if($bDummyMethodInvocation == 1) {
#				next;
#			}
			#End of the ignoring part
			
			$negativeFrequency = $parts_local[1];
			$positiveFrequency = getSupportInArrPat();

			#Here we compute the support for the additional pattern in both
			#positive and negative elements. and compute the absolute support as "NegativeSupport - PositiveSupport"
			$negativeSupport = $negativeFrequency / $negativeElemCount;
			$positiveSupport = $positiveFrequency / $positiveElemCount;
			$absoluteSupport = $negativeSupport - $positiveSupport;						
						
			if($absoluteSupport > $ALT_SUP) {
				print FINALOUTPUT "\t".$negativeElem;
				print FINALOUTPUT "\t\t Total negative elements: ".$negativeElemCount.", -ve frequency:".$negativeFrequency;
				print FINALOUTPUT "\t\t Total positive elements: ".$positiveElemCount.", +ve frequency:".$positiveFrequency."\n";
	
				print FINALOUTPUT "\t\t Absolute Support: ".$absoluteSupport."\n";
				$globalSupport_negPattern = ($negativeFrequency + $positiveFrequency)/ $globalTransactionCount;
				print FINALOUTPUT "\t\t Global Support: ".$globalSupport_negPattern."\n";

				$totalStringOutput = "";
				foreach $glob_pat_elem (@glob_pat) {
					if($glob_pat_elem =~ /^([0-9]+)(\.)([0-9]+)(\.)([0-9]+)$/) {
						$MIId = $1;
						$SubMIId = $3;	
						$before_after = $5;
						$t_name = $assocMethodIdMapper{$MIId}{$SubMIId}{"mname"};
						chop($t_name);	#UNIX

						$t_name =~ s/,/:/g;
						print FINALOUTPUT "\t\t(".$glob_pat_elem.") ".$t_name."\n";
						$totalStringOutput = $totalStringOutput.$t_name."(".$before_after.")"."&&";
					}
				}

				#Printing to total output from here
				$t_name = $assocMethodIdMapper{$keyElem}{"mname"};
				chop($t_name);	  #UNIX
				$t_name =~ s/,/:/g;
				$endPrintStr = $keyElem.",".$t_name.",".$before_after.","."1,".$totalStringOutput.",".$globalTransactionCount.",".$globalSupport_negPattern.",".$absoluteSupport.",".$supportForSortingPurpose."\n";
				$endPrintStr =~ s/\#MULTICURRTYPES\#/MULTICURRTYPES/g;
				$endPrintStr =~ s/\#UNKNOWN\#/UNKNOWN/g;
				print TOTALOUTPUT $endPrintStr;

				$bTotalNegativePatterns++;
			}
		}

		#Counting the alternative patterns
		if($bTotalNegativePatterns > 0 && $bPrev_glob_ImbalancedPattern == $glob_ImbalancedPattern) {
			$glob_ImbalancedPattern++;
		}

		print FINALOUTPUT "********************************************************************\n"; 	
	}

	if($numberOfMinedPatterns > 0) {
		$glob_TotalPatCnt++;
	}

	#No negative patterns. Check for balanced or single check patterns
	if($numberOfMinedPatterns == 1 && $bPrev_glob_ImbalancedPattern == $glob_ImbalancedPattern) {
		$glob_SingleChkCnt++;
	} 
	
	if($numberOfMinedPatterns > 1) {
		$glob_BalancedPattern++;
	}
}

#Function to get support given an array and a pattern.
#array is accepted as "glob_inp_arr" and pattern is accepted as "glob_pat"
sub getSupportInArrPat
{
	my($retFrequency) = 0;
	my($rule_elem) = 0;
	foreach $GSIP_line (@glob_inp_arr) {
		my($bAllElementsExist) = 1;

		foreach $rule_elem (@glob_pat) {
			if($GSIP_line !~ /$rule_elem/) {
				$bAllElementsExist = 0;
			}
		}

		if($bAllElementsExist == 1) {
			#Every element of the rule is available in the data. So this can be classified as a positive element
			$retFrequency = $retFrequency + 1;
		} 		
	}

	return $retFrequency;
}


#Sorting the final patterns based on frequency 

# *** Temporarily commented out this section ***

#%assoc_miner_data = ();
#open(INPUT, "<AssocMinerData_MinedConsolidated.csv");
#$numRecords = 0;
#while($line = <INPUT>) {
#	@parts = split(",", $line);
#
#	$assoc_miner_data{$numRecords}{1} = $line;
#	$assoc_miner_data{$numRecords}{2} = $parts[5];
#	$assoc_miner_data{$numRecords}{3} = $parts[6];
#	$numRecords++;
#}
#close INPUT;
#
#open(OUTPUT, ">AssocMinerData_MinedConsolidated.csv");
#print OUTPUT "S.No., Context, NM, EM, ID Rep, Frequency, Confidence, Relative Support\n";
#foreach $key(sort {$assoc_miner_data{$b}{2} <=> $assoc_miner_data{$a}{2}} keys %assoc_miner_data) {
#	print OUTPUT $assoc_miner_data{$key}{1};
#}
#
#close OUTPUT;





#Backup code


#Transforming back into patterns using ID information
#open(OUTPUT_TEMP, ">$output_file");
#open(METHODIDS, "<AssocMethodIds.txt");
#%assocMethodIdMapper = ();
#@midArr = ();
#while($line = <METHODIDS>) {
#	chop($line);
#	chop($line);
#	#Each line is of the format "5 : java.sql.PreparedStatement:close():void"
#	if($line =~ /([^ ]+)( : )(.*)$/) {
#		$keyElem = $1;	
#		$assocMethodIdMapper{$1}{"mname"} = $3;
#			
#		#As we are doing method by method mining, we only need submethod ids of this particular method-invocation
#		if($keyElem != $input_mid) {
#			next;
#		}
#
#		open(SUBMETHODIDS, "<AssocMiner_IDs/$keyElem.txt");
#		
#		push @midArr,$keyElem;
#		while($innerM = <SUBMETHODIDS>) {
#			chop($innerM);
#			chop($innerM);
#			if($innerM  =~ /([^ ]+)( : )(.*)$/) {
#				$assocMethodIdMapper{$keyElem}{$1}{"mname"} = $3;
#			}
#		}
#		close SUBMETHODIDS;
#	}	
#}
#close METHODIDS;
#
#$anchor_method = $assocMethodIdMapper{$input_mid}{"mname"};
#$numPatterns = 0;
#%result_mapper = ();			#Stores the end results
#for($tcnt = 0; $tcnt < $minePatternCnt; $tcnt++) 
#{
#
#	$freq = $FreqMapper{$leftPartArr[$tcnt]." ".$rightPartArr[$tcnt]}{"frequency"};	
#	
#	$pattern = "";
#	@leftPartArrSub = split(" ", $leftPartArr[$tcnt]);
#	$leftPartArrSub_Cnt = @leftPartArrSub;
#	foreach ($left_cnt = 0; $left_cnt < $leftPartArrSub_Cnt; $left_cnt ++) {
#		$leftElem = $leftPartArrSub[$left_cnt];
#		if($leftElem =~ /^([0-9]+)(\.)([0-9]+)(\.)([0-9]+)$/) {
#			$MIId = $1;
#			$SubMIId = $3;	
#			$pattern = $pattern.$assocMethodIdMapper{$MIId}{$SubMIId}{"mname"}."\n";
#		}
#	}
#
#	$pattern = $pattern."\n->".$anchor_method."->\n";
#
#	@rightPartArrSub = split(" ", $rightPartArr[$tcnt]);
#	$numRightElem = @rightPartArrSub;
#	$t_right_elem = 0;
#	foreach $rightElem (@rightPartArrSub) {
#		if($rightElem =~ /^([0-9]+)(\.)([0-9]+)(\.)([0-9]+)$/) {
#			$t_right_elem++;
#			$MIId = $1;
#			$SubMIId = $3;
#			if($t_right_elem == $numRightElem) {
#				$pattern = $pattern.$assocMethodIdMapper{$MIId}{$SubMIId}{"mname"};
#			} else {
#				$pattern = $pattern.$assocMethodIdMapper{$MIId}{$SubMIId}{"mname"}."\n";
#			}
#		}
#	}
#	
#	$pattern = $pattern.$leftPartArr[$tcnt]." -> ".$rightPartArr[$tcnt].",".$freq."\n";	
#
#	print OUTPUT_TEMP $pattern;
#	print OUTPUT_TEMP "\n***********************************\n";
#
#	#Generating positive and negative examples
#	generatePositiveNegatives($leftPartArr[$tcnt], $rightPartArr[$tcnt]);
#
#}
#
#close OUTPUT_TEMP;
#
##Printing the final output as patterns to a file
##open(CONSOLIDATED, ">>AssocMinerData_MinedConsolidated.csv");
##for ($tcnt = 0; $tcnt < $numPatterns; $tcnt++) {
##	print CONSOLIDATED ($tcnt + 1).",".$result_mapper{$tcnt}{"PatternList"}."\n";		 
##}
##close CONSOLIDATED;
#
## ********************* Assisting Subroutines ***********************
#sub generatePositiveNegatives()
#{
#	my($leftPart) = $_[0];
#	my($rightPart) = $_[1];
#
#	my(@allElemArr) = ();
#	@allElemArr = split(" ", $leftPart);
#	foreach $temp_right_elem (split(" ", $rightPart)) {
#		push(@allElemArr, $temp_right_elem);
#	}
#
#
#	#Read all elements and populate into different arrays
#	@positiveElem = ();	#Those elements that support the given rule. If the rule contains multiple entries, then all the elements
#						#should be present in the input data to classify that line into positive elements
#	@negativeElem = (); #Those elements that donot support the given rule. If the rule contains multiple entries, then none of the elements
#						#should present in the input data to classify that line into negative elements
#
#	open(INPUT, "<$input_filename");
#
#	while ($line = <INPUT>) {
#
#		$bAllElementsExist = 1;
#		$bNoneElemExists = 0;
#
#		foreach $rule_elem (@allElemArr) {
#			if($line =~ /$rule_elem/) {
#				$bNoneElemExists = 1;
#			} else { 
#				$bAllElementsExist = 0;
#			}
#		}
#
#		if($bAllElementsExist == 1) {
#			#Every element of the rule is available in the data. So this can be classified as a positive element
#			push(@positiveElem, $line);
#		} 
#		
#		if($bNoneElemExists == 0) {
#			#None of the elements in the rule exists. So, this can be treated as a negative example
#			push(@negativeElem, $line);
#		}
#	}
#
#	close INPUT;
#
#	#Outputting the contents to the file
#	$leftPart =~ s/ /_/g;
#	$rightPart =~ s/ /_/g;
#	if(@positiveElem != 0) {
#		open(POSITIVES, ">AssocMiner_Data/".$input_mid."_".$leftPart."_".$rightPart."_Positives.txt");
#		print POSITIVES @positiveElem;
#		close POSITIVES;
#	}		
#
#	if(@negativeElem != 0) {
#		open(NEGATIVES, ">AssocMiner_Data/".$input_mid."_".$leftPart."_".$rightPart."_Negatives.txt");
#		print NEGATIVES @negativeElem;
#		close NEGATIVES;
#	}
#}