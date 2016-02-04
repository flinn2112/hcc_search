<?php
include "Snoopy.class.php";
$snoopy = new Snoopy;

print("Starting\n") ;
   

		//prepare POST
        
	 //POST now by using Sn
	 //

$newName = '' ;
$iPosted = 0 ;
$iProcessed  = 0 ;
$d = dir(".");
echo "Handle: " . $d->handle . "\n";
echo "Path: " . $d->path . "\n";
while (false !== ($entry = $d->read())) {
   echo $entry."\n";
    //is it a pdf?
    //PDF only
    $iProcessed++ ;
    $bIsPDF = preg_match("/\.pdf$/i", $entry) ;
    if( false == $bIsPDF ){
      printf("[%s] skipped - no pdf\n", $entry) ;
      continue ;
    }
    
    $newName = time(). "_" .  $entry ; //neuen Namen bilden
    //rename($entry, $newName) ;
    $submit_vars = array () ;
    $submit_vars['MANDT'] = '0002' ;
    $submit_vars['FILE'] = $entry ; 
    $submit_vars['BASE_URN'] = "docs2013" ;
    $submit_vars['DESCRIPTION'] = "new" ;
    $submit_vars['UNIVERSE_ID'] = "uid" ;
    $submit_vars['PERSON_ID'] = "auto" ;
    $submit_vars['DOC_TIMESTAMP'] = filemtime($entry) ;
    $submit_vars['FILESIZE'] = filesize($entry) ;
    $submit_vars['RECEIPTTYPE'] = 'NEWDOC' ;
    $submit_vars['DEBUG'] = '1' ;
    print_r($submit_vars) ;
    print("<br>") ;
    //do not re-process...
    $bIsProcessed = preg_match("/^[0-9]{8}-\d\d:\d\d:\d\d_/i", $entry) ;
    //"Pattern 20120803-15:05:28_
    if( true == $bIsProcessed ){
      printf("[%s] skipped - already processed.\n", $entry) ;
      continue ;
    }
    $iPosted++ ;
    
    //if( true == copy($entry, "./processed/". $entry ) ){
//        unlink($entry) ;
        
        $result = $snoopy->submit("http://allis1.com/xmlProviders/xmlPostNewDoc.php", $submit_vars);
        printf("%s\n", $snoopy->results) ;
    //}
}
$d->close();

printf("Finished. [%s/%s] files processed/posted.\n", $iProcessed, $iPosted) ;
?>
