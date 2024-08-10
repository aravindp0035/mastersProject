package com.cis.superPeer1;

import rmi.common.Interface.BlockchainInterface;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.remoting.rmi.RmiProxyFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
public class ClientService {

	
    private RmiProxyFactoryBean superPeerFactoryBean;
    
    private BlockchainInterface blockchain;
    
    private static final String tfileName = "transaction.json";
    Transaction tr = new Transaction();
    
    private static final String BLOCKCHAIN_FOLDER = "Blockchain/";
    private static final String METABLOCK_FILE = MetaBlock.fileName; 
    
    @Autowired
    public ClientService(@Qualifier("initialSuperPeerFactoryBean") RmiProxyFactoryBean initialSuperPeerFactoryBean,
            @Qualifier("superPeerFactoryBean") RmiProxyFactoryBean superPeerFactoryBean) {
    	this.blockchain = (BlockchainInterface) initialSuperPeerFactoryBean.getObject();
        this.superPeerFactoryBean = superPeerFactoryBean;
        createMetaBlock();
    }
    
    public void synchronize(byte[] fileData, String type, String modelID) {
         
    	
    	try {
			blockchain.receiveFile(fileData, type, modelID);
		} catch (RemoteException e) {
			
			e.printStackTrace();
		}
    	
        
       
            try {
            	
                BlockchainInterface superPeer = (BlockchainInterface) superPeerFactoryBean.getObject();
                
               
              
                superPeer.receiveFile(fileData, type, modelID);
            } catch (RemoteException e) {
                System.err.println("Failed to connect to super peer: " + e);
            }
        
    }

    public String[] Search(String modelID) {
        try {
            return blockchain.search(modelID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

	public void createNewBlock(String modelName, String modelVersion, MultipartFile mlfile) {
		
		
		
		 try {
			 	byte[] fileContent = mlfile.getBytes();
			 	String modelFileBase64= Base64.getEncoder().encodeToString(fileContent);
	            
				blockchain.createBlock(modelName, modelVersion,modelFileBase64, "");
				
				String modelID = modelName.substring(0,4)+modelVersion;
				Path temp = Paths.get("temp/");
				Path tfile = temp.resolve(modelID+".json");
				File file = tfile.toFile();
				System.out.println(file.getName());
				
				byte[] fileData = new byte[(int) file.length()];
	            try (FileInputStream fis = new FileInputStream(file)) {
	                fis.read(fileData);
	            }
	            
	            tr = new Transaction("superPeer1" ,"All peers","New Block is added to Blockchain",modelID);
	    		tr.saveToJSON(tfileName);
	            
				synchronize(fileData, "Block", modelID);
				
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
		 	
	}
	
	 public void syncReviews() {
	        Path blockchainFolder = Paths.get("temp/");
	        System.out.println(blockchainFolder);

	        
	        if (!Files.exists(blockchainFolder) || !Files.isDirectory(blockchainFolder)) {
	            System.out.println("Directory not found: " + blockchainFolder);
	            return;
	        }

	        try (Stream<Path> paths = Files.list(blockchainFolder)) {
	            paths.filter(path -> path.getFileName().toString().startsWith("reviews-"))
	                 .forEach(path -> {
	                     File file = path.toFile();
	                     String fileName = file.getName();
	                     String modelID = fileName.substring(fileName.indexOf('-') + 1, fileName.lastIndexOf('.'));

	                     byte[] reviewFile = null;
	                    
	                     try {
	                         reviewFile = Files.readAllBytes(file.toPath());

	                         
	                         try (ByteArrayInputStream bais = new ByteArrayInputStream(reviewFile)) {
	                             byte[] buffer = new byte[bais.available()];
	                             bais.read(buffer);

	                             tr = new Transaction("superPeer1" ,"All peers","New reviews Synced",modelID);
	             	    		 tr.saveToJSON(tfileName);
	             	    		 
	                             synchronize(reviewFile, "Reviews", modelID);
	                             
	                             if (file.delete()) {
	                                 System.out.println("File deleted: " + file.getName());
	                             } else {
	                                 System.out.println("Failed to delete file: " + file.getName());
	                             }
	                         }
	                     } catch (IOException e) {
	                         e.printStackTrace();
	                        
	                     }
	                 });
	        } catch (IOException e) {
	            e.printStackTrace();
	            
	        }
	    }
	 
	 public void createMetaBlock() {
	        List<MetaBlock> metaBlocks = new ArrayList<>();
	        File blockchainFolder = new File(BLOCKCHAIN_FOLDER);

	        
	        if (!blockchainFolder.exists() || !blockchainFolder.isDirectory()) {
	            System.err.println("Blockchain folder does not exist or is not a directory.");
	            return;
	        }

	       
	        File[] blockFiles = blockchainFolder.listFiles((dir, name) -> name.endsWith(".json"));
	        if (blockFiles != null) {
	            for (File blockFile : blockFiles) {
	                try {
	                   
	                    String content = new String(Files.readAllBytes(blockFile.toPath()));
	                    JSONObject blockJson = new JSONObject(content);

	                   
	                    JSONObject blockHeader = blockJson.optJSONObject("blockHeader");
	                    if (blockHeader != null) {
	                        int blockId = blockHeader.optInt("BID", 0);
	                        String previousBlockHash = blockHeader.optString("HPB", "");
	                        String hashCurrentBlock = blockHeader.optString("HCB", "");
	                        String timestamp = blockHeader.optString("BTS", "");
	                        String modelId = blockHeader.optString("modelID", "");

	                       
	                        MetaBlock metaBlock = new MetaBlock(blockId, previousBlockHash, hashCurrentBlock, timestamp, modelId);
	                        metaBlocks.add(metaBlock);
	                    }
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	            }
	        }

	        try (FileWriter fileWriter = new FileWriter(METABLOCK_FILE)) {
	            
	            JSONArray jsonArray = new JSONArray(metaBlocks);
	            fileWriter.write(jsonArray.toString(4)); 
	            System.out.println("MetaBlock file created: " + METABLOCK_FILE);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }


	

	public byte[] ExtractMLfile(String modelID) {
		
		try {
            return blockchain.extractMLFile(modelID);
        } catch (Exception e) {
            e.printStackTrace();
        }
		return null;
	}

	public byte[] ExtractReviewsfile(String modelID) {
		
		try {
            return blockchain.extractReviews(modelID);
        } catch (Exception e) {
            e.printStackTrace();
        }
		
		return null;
	}

	public int bcSize() {
		try {
            return blockchain.bcSize();
        } catch (Exception e) {
            e.printStackTrace();
        }
		return 0;
	}

	
	

}
