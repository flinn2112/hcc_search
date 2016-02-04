#/bin/bash
#RENAME SCRIPT 
#Renaming multiple files by using a pattern
#Example: rename.sh html inforecord pmdtrans
echo $1
echo $2
echo $3

EXT=$1
PATTERN=$2
RPLC=$3

REGX="s/$PATTERN/$RPLC/g"

for f in *.$EXT; 
  do 
    echo $f
    mv $f $(echo $f | sed $REGX); 
  done

