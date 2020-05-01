/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hcc_search.indexer; 

import hcc_search.hResult;
import hcc_search.hcc_utils;
import hcc_search.myClassLoader;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
//import org.apache.lucene.document.NumericField;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;


import java.io.File;

import java.io.IOException;
import java.nio.file.Paths;

import java.util.Date;




/** Index all text files under a directory.
 * <p>
 * This is a command-line application demonstrating simple Lucene indexing.
 * Run it with no command-line arguments for usage information.
 */
public class IndexFiles {
  
  private IndexFiles() {}

  /** Index all text files under a directory. */
  public static void main(String[] args) {
    String usage = "java org.apache.lucene.demo.IndexFiles"
                 + " [-index INDEX_PATH] [-docs DOCS_PATH] [-update]\n\n"
                 + "This indexes the documents in DOCS_PATH, creating a Lucene index"
                 + "in INDEX_PATH that can be searched with SearchFiles";
    String indexPath = "/Users/frankkempf/Documents/index";
    String docsPath = null;
    boolean create = false ; //true;
    
    
    
    
    System.err.println(" This module [indexFiles] is obsolete - use IndexFilesApp instead.");
      System.exit(2112);
    
    
    
    
    for(int i=0;i<args.length;i++) {
      if ("-index".equals(args[i])) {
        indexPath = args[i+1];
        i++;
      } else if ("-docs".equals(args[i])) {
        docsPath = args[i+1];
        i++;
      } else if ("-update".equals(args[i])) {
        create = false;
      }
    }
    //nur test
    docsPath = "/Users/frankkempf/NetBeansProjects" ;
    //docsPath = "/Users/frankkempf/Documents/Volume_2/SharedDownloads/Dokus" ;
    if (docsPath == null) {
      System.err.println("Usage: " + usage);
      System.exit(1);
    }

    final File docDir = new File(docsPath);
    if (!docDir.exists() || !docDir.canRead()) {
      System.out.println("Document directory '" + docDir.getAbsolutePath()+ "' does not exist or is not readable, please check the path");
      System.exit(1);
    }
    
    Date start = new Date();
    try {
      System.out.println("Indexing to directory '" + indexPath + "'...");
      
      Directory dir = FSDirectory.open( Paths.get(indexPath));
      Analyzer analyzer = new StandardAnalyzer();
      IndexWriterConfig iwc = new IndexWriterConfig(analyzer);

      if (create) {
        // Create a new index in the directory, removing any
        // previously indexed documents:
        iwc.setOpenMode(OpenMode.CREATE);
      } else {
        // Add new documents to an existing index:
        iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
      }

      // Optional: for better indexing performance, if you
      // are indexing many documents, increase the RAM
      // buffer.  But if you do this, increase the max heap
      // size to the JVM (eg add -Xmx512m or -Xmx1g):
      //
      // iwc.setRAMBufferSizeMB(256.0);

      IndexWriter writer = new IndexWriter(dir, iwc);
      //writer.setRAMBufferSizeMB(384);
      //writer.setMaxBufferedDocs(50);
      indexDocs(writer, docDir, 5);

      // NOTE: if you want to maximize search performance,
      // you can optionally call optimize here.  This can be
      // a costly operation, so generally it's only worth
      // it when your index is relatively static (ie you're
      // done adding documents to it):
      //
      // writer.optimize();

      writer.close();

      Date end = new Date();
      System.out.println(end.getTime() - start.getTime() + " total milliseconds");

    } catch (IOException e) {
      System.out.println(" caught a " + e.getClass() +
       "\n with message: " + e.getMessage());
    }
  }

  /**
   * Indexes the given file using the given writer, or if a directory is given,
   * recurses over files and directories found under the given directory.
   * 
   * NOTE: This method indexes one document per input file.  This is slow.  For good
   * throughput, put multiple documents into your input file(s).  An example of this is
   * in the benchmark module, which can create "line doc" files, one document per line,
   * using the
   * <a href="../../../../../contrib-benchmark/org/apache/lucene/benchmark/byTask/tasks/WriteLineDocTask.html"
   * >WriteLineDocTask</a>.
   *  
   * @param writer Writer to the index where the given file/dir info will be stored
   * @param file The file to index, or the directory to recurse into to find files to index
   * @throws IOException
   */
  static void indexDocs(IndexWriter writer, File file,  int iMaxFileSizeMB)
    throws IOException {
      
    /*  
      
        String strExt = null ;
        String strClassName = null ;
        Integer iID ;
        String strDate = new String("") ;
        myClassLoader ccl = new myClassLoader(0) ;
        hResult   oResult = new hResult() ; //search Result object 
        Document doc = null ;
    // do not try to index files that cannot be read
    if (false == file.canRead()) {
      return ;
    }
    
    hcc_utils.ht_build(System.getProperty("user.dir") + "/includeExt.txt");
      if (file.isDirectory()) {
          
        String[] files = file.list();
        // an IO error could occur
        if (files != null) {
          for (int i = 0; i < files.length; i++) {  
            if(file.length() > (iMaxFileSizeMB * 1024 * 1024) ){
                hcc_utils.reportMessage("Warning", "indexDocs",  "Maximum Size exceeded.",
                        "File  [" + file.getName() + "/" + file.length() + "]") ;
                continue ;
            }
            indexDocs(writer, new File(file, files[i]), iMaxFileSizeMB  );           
          }
        }
        return ;  //directory processing finished here
      } 
      else { //it is a file - get extension and call extractor
          
         strExt = hcc_utils.getExtensionUpper(file) ;
         if( null == strExt || 0 == strExt.length()  ){
             return ;
         }
         
         if( false == hcc_utils.isContained(strExt)){
             System.out.println("no file extension configured [" + file.getName() + "]" ) ;
             return ;
         }
       
            System.out.println("Processing [" + file.getName() + "]" ) ;
         //compile classname
         strClassName = "hcc_search." + strExt + "Extractor" ;
         
         
         
         //do  we have such a class?
         if( null == ccl.findClass(strClassName) ){
          strClassName = "hcc_search.BINExtractor" ;
         }
         
         try{
                 oResult = (hResult)ccl.invokeClassMethod(strClassName, "processDocument", file) ;
                 switch (oResult.m_eRetType){
                     case hResult.DOCUMENT :
                         doc = (Document)oResult.m_oPayLoad ;
                         break ;
                     case hResult.CONTENT: 
                     case hResult.UNKNOWN : //a string
                          // make a new, empty document
                         
                         //TXTExtractor t = new TXTExtractor() ;
                        // TXTExtractor.processDocument(file) ;
                         
                          doc = new Document();
                          doc.add(new Field("contents", (String)oResult.m_oPayLoad,
                                    Field.Store.YES, Field.Index.ANALYZED));
                          break ; 
                     default:                         
                          return ; //nothing to do
                 }
                 
            }
            catch(Exception ex){
                Throwable t = ex.getCause() ;
                System.out.println("Failed [" + ex.toString() + "]" ) ;
                return ;
            }
      }
        

        try {   

          // Add the path of the file as a field named "path".  Use a
          // field that is indexed (i.e. searchable), but don't tokenize 
          // the field into separate words and don't index term frequency
          // or positional information:
          org.apache.lucene.document.Field pathField 
                        = new org.apache.lucene.document.Field("path", file.getPath(), 
                                Field.Store.YES,  Field.Index.ANALYZED_NO_NORMS);
          
          //pathField.(org.apache.lucene.index.IndexOptions.DOCS);
          doc.add(pathField);
          strDate = hcc_utils.getLastModifiedString(file) ;
          Field dateField = new Field("date",  strDate, 
                                Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS);
          //dateField.set(org.apache.lucene.index.IndexOptions.DOCS);
          doc.add(dateField);
   
            System.out.println("adding field lastModified, value [" + strDate + "]");
          

          // Add the last modified date of the file a field named "modified".
          // Use a NumericField that is indexed (i.e. efficiently filterable with
          // NumericRangeFilter).  This indexes to milli-second resolution, which
          // is often too fine.  You could instead create a number based on
          // year/month/day/hour/minutes/seconds, down the resolution you require.
          // For example the long value 2011021714 would mean
          // February 17, 2011, 2-3 PM.
            Long lLastMod = file.lastModified() ;
          org.apache.lucene.document.LongField modifiedField = 
                  new org.apache.lucene.document.LongField("lastModified", lLastMod, 
                                           org.apache.lucene.document.Field.Store.YES) ;
          //modifiedField.setLongValue(file.lastModified());
          doc.add(modifiedField);
 
          // Add the contents of the file to a field named "contents".  Specify a Reader,
          // so that the text of the file is tokenized and indexed, but not stored.
          // Note that FileReader expects the file to be in UTF-8 encoding.
          // If that's not the case searching for special characters will fail.
            

          if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
            // New index, so we just add the document (no old document can be there):
            System.out.println("adding file [" + file + "]");
                try{
                    writer.addDocument(doc);
                }catch (java.lang.OutOfMemoryError ex){
                    
                    System.out.println("Ex " + ex.toString());
                    
                }
            }
            
            
            
           else {
            // Existing index (an old copy of this document may have been indexed) so 
            // we use updateDocument instead to replace the old one matching the exact 
            // path, if present:
            System.out.println("updating " + file);
            writer.updateDocument(new Term("path", file.getPath()), doc);
          }
          
          
        } finally {
          
        }     
        */
  }
}
