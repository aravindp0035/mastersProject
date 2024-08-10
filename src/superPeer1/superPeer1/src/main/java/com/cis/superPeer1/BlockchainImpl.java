package com.cis.superPeer1;

import rmi.common.Interface.BlockchainInterface;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.stereotype.Component;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

@Component
public class BlockchainImpl extends UnicastRemoteObject implements BlockchainInterface {
    private static final long serialVersionUID = 1L;
    
    
    
    
    
    private List<Block> blockchain;
  
    private static final String tfileName = "transaction.json";
    
    Transaction tr = new Transaction();
    
    
    
    public BlockchainImpl() throws RemoteException {
        super();
        
        initializeBlockchain(new String[]{});
    }
    
    
    
    @Override
    public int bcSize(){
    	
    	try {
			initializeBlockchain(null);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return blockchain.size();
    }

    @Override
    public void initializeBlockchain(String[] s) throws RemoteException {
        File blockchainFolder = new File("Blockchain");

        if (!blockchainFolder.exists() || !blockchainFolder.isDirectory()) {
            System.out.println("Blockchain folder does not exist or is not a directory.");
            return;
        }

        File[] blockFiles = blockchainFolder.listFiles();

        if (blockFiles == null || blockFiles.length == 0) {
            System.out.println("No block files to initialize.");
            return;
        }

        List<String> blockFilePaths = new ArrayList<>();

        for (File blockFile : blockFiles) {
            if (blockFile.isFile() && blockFile.getName().endsWith(".json")) {
                blockFilePaths.add(blockFile.getAbsolutePath());
            }
        }
        blockchain = new ArrayList<>();
        for (String filePath : blockFilePaths) {
            try {
                FileInputStream fis = new FileInputStream(new File(filePath));
                JSONTokener tokener = new JSONTokener(fis);
                JSONObject blockJson = new JSONObject(tokener);
                Block block = jsonToBlock(blockJson);
                blockchain.add(block);
                fis.close();
            } catch (IOException e) {
                throw new RemoteException("Error reading file: " + filePath, e);
            }
        }
    }
    
    public Block latestBlock() {
    	
    	try {
			initializeBlockchain(null);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	if(blockchain.size()==0) return null;
    	return blockchain.get(blockchain.size()-1);
    	
    }
    
   

    @Override
    public void createBlock(String modelName, String modelVersion, String modelFileBase64, String modelReviewsBase64) throws RemoteException {
        Block.ModelData modelData = new Block.ModelData(modelFileBase64);
        Block.ModelFile modelFile = new Block.ModelFile(modelData);

        Block.ReviewData reviewData = new Block.ReviewData(modelReviewsBase64);
        Block.Review review = new Block.Review(reviewData);
        
        
        int blockId=0;
    	String newPrevHash="0";
    	if(latestBlock()!=null) {
    	blockId = latestBlock().getBlockId() + 1;
    	newPrevHash = latestBlock().getHashCurrentBlock();

    	}
        

        Block newBlock = new Block(blockId, newPrevHash, modelName, modelVersion, modelFile, review);
        blockchain.add(newBlock);

        JSONObject blockJson = newBlock.toJson();
        String modelID = modelName.substring(0, 4) + modelVersion;
        Path tempDir = Paths.get("temp/");
        
       
        
        Path blockFilePath = tempDir.resolve(modelID + ".json");
        File blockFile = blockFilePath.toFile();
        try (FileWriter file = new FileWriter(blockFile)) {
            file.write(blockJson.toString(2));
            System.out.println("New block saved to: " + blockFile);
            tr = new Transaction("superPeer1" ,"superPeer1","Temp Block Created",modelID);
    		tr.saveToJSON(tfileName);
    		
    		
        
        } catch (IOException e) {
            throw new RemoteException("Error writing block to file: " + blockFile, e);
        } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        MetaBlock metaBlock = new MetaBlock(blockId, newPrevHash, newBlock.getHashCurrentBlock(), newBlock.getTimestamp(), newBlock.getModelId());
        appendMetaBlock(metaBlock);
    }

    private void appendMetaBlock(MetaBlock metaBlock) throws RemoteException {
        String metaBlockFileName = "metablock.json";
        JSONArray metaBlocksArray = new JSONArray();

        
        try (FileReader reader = new FileReader(metaBlockFileName)) {
            
            BufferedReader bufferedReader = new BufferedReader(reader);
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line);
            }

            
            if (content.length() > 0) {
                metaBlocksArray = new JSONArray(content.toString());
            }
        } catch (FileNotFoundException e) {
            
            System.out.println("Meta block file not found. Creating a new file.");
        } catch (IOException e) {
            throw new RemoteException("Error reading meta block file: " + metaBlockFileName, e);
        } catch (JSONException e) {
            throw new RemoteException("Invalid JSON in meta block file: " + metaBlockFileName, e);
        }

        
        metaBlocksArray.put(metaBlock.toJson());

        
        try (FileWriter writer = new FileWriter(metaBlockFileName)) {
            writer.write(metaBlocksArray.toString(2)); 
            System.out.println("Meta block appended to: " + metaBlockFileName);
        } catch (IOException e) {
            throw new RemoteException("Error writing meta block to file: " + metaBlockFileName, e);
        }
    }

   
    private Block jsonToBlock(JSONObject blockJson) {
        JSONObject headerJson = blockJson.getJSONObject("blockHeader");
        int blockId = headerJson.getInt("BID");
       
        String previousBlockHash = headerJson.getString("HPB");
        String modelName = headerJson.getString("model_name");
        String modelVersion = headerJson.getString("model_version");

        JSONObject modelFileJson = blockJson.getJSONArray("model_file").getJSONObject(0);
        String modelFileBase64 = modelFileJson.getString("DATA");
        Block.ModelData modelData = new Block.ModelData(modelFileBase64);
        Block.ModelFile modelFile = new Block.ModelFile(modelData);

        JSONObject reviewJson = blockJson.getJSONArray("reviews").getJSONObject(0);
        String modelReviewsBase64 = reviewJson.getString("DATA");
        Block.ReviewData reviewData = new Block.ReviewData(modelReviewsBase64);
        Block.Review review = new Block.Review(reviewData);

        return new Block(blockId, previousBlockHash, modelName, modelVersion, modelFile, review);
    }

    @Override
    public byte[] extractReviews(String modelID) throws RemoteException {
    	
    	initializeBlockchain(null);
        for (Block block : blockchain) {
            Block.Review review = block.getReview();
            if (block.getModelId().equals(modelID)) {
                String base64Reviews = review.data.modelReviews;
                
                tr = new Transaction("SuperPeer1" ,"SuperPeer1","Reviews Extracted",modelID);
        		tr.saveToJSON(tfileName);
                return base64Reviews.getBytes();
            }
        }
        System.out.println("No block found with modelID: " + modelID);
        return null;
    }

    
    @Override
    public String[] search(String modelID) {
        String[] values = {"", ""};
        try {
			initializeBlockchain(null);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        for (Block block : blockchain) {
            Block.ModelFile modelFile = block.getModelFile();
            Block.Review review = block.getReview();
            if (block.getModelId().equals(modelID)) {
                String base64ModelFile = modelFile.data.modelFile;
                String Reviews = review.data.modelReviews;
                values[0] = new String(java.util.Base64.getDecoder().decode(base64ModelFile));
                values[1] = new String(Reviews);
            }
        }

       
         
        tr = new Transaction("superPeer1" ,"superPeer1","Search Result",modelID);
		tr.saveToJSON(tfileName);
        
        return values;
    }

    @Override
    public void addReviews(String modelID, String reviewText) {
        Path tempFolder = Paths.get("temp/");
        Path targetPath = tempFolder.resolve("reviews-" + modelID + ".json");

        try {
            
            if (Files.notExists(tempFolder)) {
                Files.createDirectories(tempFolder);
            }

           
            JSONArray reviewsArray;

            
            if (Files.exists(targetPath)) {
                try (FileInputStream fis = new FileInputStream(targetPath.toFile())) {
                    reviewsArray = new JSONArray(new JSONTokener(fis));
                }
            } else {
                reviewsArray = new JSONArray();
            }

           
            reviewsArray.put(reviewText);

            
            try (FileWriter fileWriter = new FileWriter(targetPath.toFile())) {
                fileWriter.write(reviewsArray.toString(4)); 
            }
            
            tr = new Transaction("Regular Peer" ,"superPeer1","Reviews received for",modelID);
    		tr.saveToJSON(tfileName);
            System.out.println("Review from regular peer added to: " + targetPath);

        } catch (IOException e) {
            e.printStackTrace();
            
        }
    }


    @Override
    public void receiveFile(byte[] file, String fileType, String modelID) throws RemoteException {
    	
        File blockchainFolder = new File("Blockchain/");
        File targetFile = new File(blockchainFolder, modelID + ".json");
        

        try {
            if (fileType.equals("Block")) {
                
                if (!blockchainFolder.exists()) {
                    blockchainFolder.mkdirs();
                }

               
                try (FileOutputStream fos = new FileOutputStream(targetFile)) {
                    fos.write(file);
                    System.out.println("Block Saved at Blockchain Folder: " + modelID + ".json");
                }
            } else if (fileType.equals("Reviews")) {
                if (targetFile.exists()) {
                    
                    JSONObject blockJson;
                    try (FileInputStream fis = new FileInputStream(targetFile)) {
                        blockJson = new JSONObject(new JSONTokener(fis));
                    }

                    JSONArray reviewsArray = blockJson.optJSONArray("reviews");
                    if (reviewsArray == null) {
                        reviewsArray = new JSONArray();
                        blockJson.put("reviews", reviewsArray);
                    }

                   
                    StringBuilder allReviews = new StringBuilder();

                 
                    if (reviewsArray.length() > 0) {
                        JSONObject existingReviewsObject = reviewsArray.getJSONObject(0);
                        allReviews.append(existingReviewsObject.optString("DATA"));
                    }

                    
                    try (ByteArrayInputStream bais = new ByteArrayInputStream(file)) {
                        JSONArray newReviews = new JSONArray(new JSONTokener(bais));
                        for (int i = 0; i < newReviews.length(); i++) {
                            String review = newReviews.getString(i);
                            String indexedReview = "Review " + (allReviews.toString().split("Review ").length) + ": " + review + "\n";
                            allReviews.append(indexedReview);
                        }
                    }

                    
                    if (reviewsArray.length() > 0) {
                        reviewsArray.getJSONObject(0).put("DATA", allReviews.toString());
                    } else {
                        JSONObject reviewObject = new JSONObject();
                        reviewObject.put("DATA", allReviews.toString());
                        reviewsArray.put(reviewObject);
                    }

                 
                    try (FileWriter writer = new FileWriter(targetFile)) {
                        writer.write(blockJson.toString(4)); 
                    }
                    System.out.println("Reviews appended to block: " + targetFile.getPath());
                } else {
                    System.err.println("Block with modelID " + modelID + " not found.");
                }
            }
        } catch (IOException e) {
            throw new RemoteException("Error handling file: " + file.length, e);
        }
    }




	@Override
	public byte[] extractMLFile(String modelID) throws RemoteException {
		
		initializeBlockchain(null);
		for (Block block : blockchain) {
	        if (block.getModelId().equals(modelID)) {
	            Block.ModelFile modelFile = block.getModelFile();
	            String base64ModelFile = modelFile.data.modelFile;
	            tr = new Transaction("superPeer1" ,"superPeer1","ML file Extracted",modelID);
	    		tr.saveToJSON(tfileName);
	            return java.util.Base64.getDecoder().decode(base64ModelFile);
	        }
	    }
	    System.out.println("No block found with modelID: " + modelID);
	    return null;
	}
	public static void main(String[] args) {
        try {
            
            LocateRegistry.createRegistry(1099);
            BlockchainImpl server = new BlockchainImpl();
            Naming.rebind("rmi://localhost:1099/superPeer1", server);
            
            
            System.out.println("Blockchain Server for superpeer 1 is ready.");
        } catch (Exception e) {
            System.err.println("Blockchain Server failed: " + e.getMessage());
            e.printStackTrace();
        }
    }



}
