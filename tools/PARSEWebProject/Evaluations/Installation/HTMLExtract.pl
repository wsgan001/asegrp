use LWP::Simple;
use HTML::TableExtract;


#IMPORTANT: THIS FILE SHOULD NOT GENERATE ANY OUTPUT. IF NOT THE JAVA PROCESS EXECUTING THIS WILL HANG

#Input: A directory where the HTML files extracted are stored
#Output: Changes the HTML files to Java files

if(@ARGV != 1)
{
	print ("Usage : perl HTMLExtract.pl <inputdir>");
	die(" ");
}


$dirtoget = $ARGV[0];
opendir DIRECTORY, $dirtoget;


@thefiles= readdir(DIRECTORY);

$tempFile = "temp.html";
open( TEMPFile, ">$tempFile") || die("Failed to open file");

#Iterate through files in directory and convert them to Java
foreach $fileInDir (@thefiles) {

	

	#Translate only HTML files. Ignore others
	if($fileInDir =~ /^([^\.]+)(\.)(htm[l]?)$/)
	{
		#print $fileInDir."\n";

		$fullFilePath = $dirtoget."\\".$fileInDir;
		
		open( HTMLFile, "<$fullFilePath") || die("Failed to open file");
		$HtmlStr = "";
		

		while( $line = <HTMLFile> )
		{
			$HtmlStr = $HtmlStr.$line;
		}
		
		#Suppress the content between <span ..> and </span> to prevent the comments to appear at the end
		#$HtmlStr =~ s/<span class=cc>[^<]*[^\/]*[^s]*[^p]*[^a]*[^n]*[^>]*<\/span>//g;

		$fileNameWithOutExtension  = "";
		if($fileInDir =~ /^([^\.]+)(\.)(htm[l]?)$/)
		{
			$fileNameWithOutExtension = $dirtoget."\\".$1."."."java";
		}

		#print $fileNameWithOutExtension."\n";

		open(JAVAFile, ">$fileNameWithOutExtension")  || die("Failed to open file to write");

		$te = new HTML::TableExtract ( depth => 0 );
		$te->parse($HtmlStr);

		@ts = $te->table_states;
		
		$contentWritten = 0;
		if(@ts > 1)
		{
			#In each HTML the second table and row 2  and column 4 contains the required java code that has to be extracted
			#Please note that this implementation is done only for the feasibility study. It needs to be modified
			$table2 = $ts[1];

			@rows = $table2->rows();
			@cols = $table2->columns();

			if(@rows > 1 && @cols > 3)
			{
				$javaContent = $table2->cell(1,3);
				$contentWritten = 1;
			}
			
			print JAVAFile $javaContent;
		}
		close(JAVAFile);
		close(HTMLFile);

		if($contentWritten == 0)
		{
			unlink(JAVAFile);
		}
		unlink($fullFilePath);
	}	
}


close(DIRECTORY);
#print "Program terminated successfully...";