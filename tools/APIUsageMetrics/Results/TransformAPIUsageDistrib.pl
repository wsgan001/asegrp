
#Perl script for transforming the APIUsage distributions from column to row

$ARGV[0] || die("Syntax: perl $0 <input> <output> <UseLogScale(0/1)>");
$ARGV[1] || die("Syntax: perl $0 <input> <output> <UseLogScale(0/1)>");

open (OUTPUT, ">$ARGV[1]");

open (INPUT, "<$ARGV[0]");
@inpArr = <INPUT>;
close INPUT;

$bUseLogScale = $ARGV[2];

@UsageMatrix = ();
@subjectArr = ();

$counter = 0;
$maximumCnt = 0;

foreach $line (@inpArr) {
	
	chomp($line);

	
	if($counter == 0)
	{
		$counter++;
		next;
	}
	@parts = split(',', $line);
	$subjectArr[$counter] = $parts[0];

	
	$comp_area = 0;
	for($ii = 1; $ii < @parts; $ii ++)
	{
		$UsageMatrix[$counter][$ii - 1] = $parts[$ii];	
		$comp_area += ($ii - 1) * $parts[$ii];
	}

	if((@parts - 1) > $maximumCnt)
	{
		$maximumCnt = @parts - 1;
	}

	$counter++;

	print "Area of $parts[0] : $comp_area\n";
}

for($ii = 0; $ii <= $maximumCnt; $ii++)
{
	if($ii == 0)
	{
		#print subjects info
		$subjectStr = "subject,";
		for($tmp = 1; $tmp < $counter;$tmp++)
		{
			$subjectStr .= $subjectArr[$tmp].",";
		}

		print OUTPUT $subjectStr."\n";
		next;
	}

	$usageStr = getScaledValue($ii - 1).",";
	for($tmp = 1; $tmp < $counter;$tmp++)
	{
		if($UsageMatrix[$tmp][$ii - 1] eq "")
		{
			$UsageMatrix[$tmp][$ii - 1] = 0;
		}

		$logValue = getScaledValue($UsageMatrix[$tmp][$ii - 1]);
		$usageStr .= $logValue.",";
	}

	print OUTPUT $usageStr."\n";
}

close OUTPUT;



sub getScaledValue
{
	my $inp = shift;

	if($bUseLogScale == 0)
	{
		return $inp;
	}

	if($inp == 0)
	{
		return $inp;
	}
	elsif($inp == 1)
	{
		return 0.21;
	}
	else
	{
		$value = log($inp) / log(5);
		return $value;
	}
}