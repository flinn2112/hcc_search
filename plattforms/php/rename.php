<?php
include "Snoopy.class.php";

print("Starting\n") ;
   

		//prepare POST
        
	 //POST now by using Sn
	 //


$d = dir(".");
echo "Handle: " . $d->handle . "\n";
echo "Path: " . $d->path . "\n";
while (false !== ($entry = $d->read())) {
   echo $entry."\n";
    //is it a pdf?
    if( 0 == preg_match("/.*\.pdf/i", $entry)  ){
            continue ;
    }
    $submit_vars['FILE'] = $entry ;
    $submit_vars['FILETIME'] = filemtime($entry) ;
    $submit_vars['FILESIZE'] = filesize($entry) ;
    print_r($submit_vars) ;
    $newName = preg_replace("/[0-9]*_/", "", $entry) ; 
    printf("----------------->%s\n", $newName) ;
    //rename($entry, date("Ymd-H:i:s_").  $entry) ;
     rename($entry, $newName) ;
}
$d->close();

print("Finished\n") ;
?>
