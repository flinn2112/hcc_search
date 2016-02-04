<?php


print("Starting\n") ;
   

		//prepare POST
        
	 //POST now by using Sn
	 //

$newName = '' ;
$iPosted = 0 ;
$iProcessed  = 0 ;
$iLines = 0 ;
$d = dir(".");
echo "Handle: " . $d->handle . "\n";
echo "Path: " . $d->path . "\n";
while (false !== ($entry = $d->read())) {
    echo $entry."\n";
    $iLines = 0 ;
    if( preg_match("/trdir\.txt/", $entry) ){
      continue ;
    }
    $iProcessed++ ;
    if( false == is_file($entry)){
        continue;
    }
    $file_handle = fopen($entry, 'r');
    while (!feof($file_handle)) {
      $line = fgets($file_handle);
      $iLines++;
    }
    fclose($file_handle);

    $fOut = fopen("lineCount.csv", "a");
    if($fOut)
    {
        $strOut = sprintf("%s;%s\r\n", $entry, $iLines) ;
        fwrite($fOut, $strOut ) ;
        fclose($fOut) ;
    }
}
$d->close();

printf("Finished. [%s/%s] files processed/posted.\n", $iProcessed, $iPosted) ;
?>