/*
   ____  ____ _____ ____  __    ____________________
  / __ \/ __ ) ___// __ \/ /   / ____/_  __/ ____/ /
 / / / / __  \__ \/ / / / /   / __/   / / / __/ / / 
/ /_/ / /_/ /__/ / /_/ / /___/ /___  / / / /___/_/  
\____/_____/____/\____/_____/_____/ /_/ /_____(_)   
                                                    

    ____  __________  __    ___   ________________     ______  __
   / __ \/ ____/ __ \/ /   /   | / ____/ ____/ __ \   / __ ) \/ /
  / /_/ / __/ / /_/ / /   / /| |/ /   / __/ / / / /  / __  |\  / 
 / _, _/ /___/ ____/ /___/ ___ / /___/ /___/ /_/ /  / /_/ / / /  
/_/ |_/_____/_/   /_____/_/  |_\____/_____/_____/  /_____/ /_/   
                                                                 
  ____________ __ ___ 
 /_  __/  _/ //_//   |
  / /  / // ,<  / /| |
 / / _/ // /| |/ ___ |
/_/ /___/_/ |_/_/  |_|
                      
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hcc_search;

//package fulltext.common.processing.helpers.poi;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

/**
* Wraps around the POI stuff to read an Excel (XLS) file from disk
*/
public class ExcelFileWrapper
{
        private POIFSFileSystem _fileSystem;
        private HSSFWorkbook _workbook;

    public ExcelFileWrapper()
    {
        System.out.println("TEST CONSTRUCTOR") ;
    }
        /**
         * Initialize the object - does not read yet
         * @throws IOException
         */
       
    
        public ExcelFileWrapper(java.io.InputStream stream) throws IOException
        {
                if (stream == null)
                        throw new NullPointerException (
            "in ExcelFileWrapper: ctor parameter 'stream' is null.");
                //
        _fileSystem = new POIFSFileSystem(stream);
        _workbook = new HSSFWorkbook (_fileSystem) ;
        }

        /**
         * Return the contents of all sheets as string.
         * Every textual cell's content is added here.
         */
        public String readContents ()
        {
                // return this
                StringBuilder builder = new StringBuilder();

                // for each sheet
                for (int numSheets = 0; numSheets <
                    _workbook.getNumberOfSheets(); numSheets++)
                {
                HSSFSheet sheet = _workbook.getSheetAt(numSheets);

                // Iterate over each row in the sheet
                Iterator rows = sheet.rowIterator();
                while( rows.hasNext() )
                {
                    HSSFRow row = (HSSFRow) rows.next();

                 // Iterate over each cell in the row and add the cell's content
                    Iterator cells = row.cellIterator();
                    while( cells.hasNext() )
                    {
                        // get cell..
                        HSSFCell cell = (HSSFCell) cells.next();
                        // .. add to stringbuilder
                        processCell (cell, builder);
                    }

                }

        } // for numSheets ..

                //
                return builder.toString();
        }

        /**
         * Add the cells's content to the stringbuilder (if appropiate
content, i.e. text - no numbers)
         */
        private void processCell (HSSFCell cell, StringBuilder builder)
        {
        switch ( cell.getCellType() )
        {
        /*
            case HSSFCell.CELL_TYPE_NUMERIC:
                System.out.println( cell.getNumericCellValue() );
                break;
        */
            case HSSFCell.CELL_TYPE_STRING:
                builder.append (cell.getStringCellValue());
                builder.append (" ");
                break;

            default:
                break;
        }
        }

}

