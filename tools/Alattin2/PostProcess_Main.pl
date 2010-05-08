
# Parent perl file

unlink("AssocMinerData_MinedConsolidated.csv");

open(OUTPUT, ">AssocMinerData_MinedConsolidated.csv");
close OUTPUT;

open(METHODIDS, "<AssocMethodIds.txt");
while($line = <METHODIDS>) {
	chop($line);
	#Each line is of the format "5 : java.sql.PreparedStatement:close():void"
	if($line =~ /([^ ]+)( : )(.*)$/) {
		$keyElem = $1;	
		system("perl PostProcess.pl $keyElem");
	}	
}
close METHODIDS;


#Sorting the final patterns based on frequency 
%assoc_miner_data = ();
open(INPUT, "<AssocMinerData_MinedConsolidated.csv");
$numRecords = 0;
while($line = <INPUT>) {
	@parts = split(",", $line);

	$assoc_miner_data{$numRecords}{1} = $line;
	$assoc_miner_data{$numRecords}{2} = $parts[5];
	$assoc_miner_data{$numRecords}{3} = $parts[6];
	$numRecords++;
}
close INPUT;

open(OUTPUT, ">AssocMinerData_MinedConsolidated.csv");
print OUTPUT "S.No., Context, NM, EM, ID Rep, Frequency, Confidence, Relative Support\n";
foreach $key(sort {$assoc_miner_data{$b}{2} <=> $assoc_miner_data{$a}{2}} keys %assoc_miner_data) {
	print OUTPUT $assoc_miner_data{$key}{1};
}

close OUTPUT;