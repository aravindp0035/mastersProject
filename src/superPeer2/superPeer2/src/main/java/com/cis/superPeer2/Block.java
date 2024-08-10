package com.cis.superPeer2;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.*;
import java.time.format.DateTimeFormatter;

public class Block {
    private int blockId;
    private String previousBlockHash;
    private String hashCurrentBlock;
    private String timestamp;
    private String modelName;
    private String modelVersion;
    private String modelId;
    private ModelFile modelFile;
    private Review review;

    public Block(int blockId, String previousBlockHash, String modelName, String modelVersion, ModelFile modelFile, Review review) {
        this.blockId = blockId;
        this.previousBlockHash = previousBlockHash;
        this.modelName = modelName;
        this.modelVersion = modelVersion;
        this.timestamp = generateTimestamp();
        this.hashCurrentBlock = generateHashCurrentBlock(previousBlockHash, timestamp, modelName, modelVersion);
        this.modelId = generateModelId(modelName, modelVersion);
        this.modelFile = modelFile;
        this.review = review;
    }

   

    private String generateTimestamp() {
    	 return DateTimeFormatter.ISO_INSTANT
    	            .withZone(ZoneOffset.UTC)
    	            .format(Instant.now());
    }
    
    private String generateHashCurrentBlock(String previousBlockHash, String timestamp, String modelName, String modelVersion) {
        try {
            
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            String input = previousBlockHash + timestamp + modelName + modelVersion;

            byte[] hashBytes = digest.digest(input.getBytes());

            StringBuilder hashString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hashString.append('0');
                hashString.append(hex);
            }
            return hashString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating hash for block", e);
        }
    }

    private String generateModelId(String modelName, String modelVersion) {
        return modelName.substring(0, 4) + modelVersion;
    }

    public int getBlockId() {
        return blockId;
    }

    public String getPreviousBlockHash() {
        return previousBlockHash;
    }

    public String getHashCurrentBlock() {
        return hashCurrentBlock;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getModelName() {
        return modelName;
    }

    public String getModelVersion() {
        return modelVersion;
    }

    public String getModelId() {
        return modelId;
    }

    public ModelFile getModelFile() {
        return modelFile;
    }

    public Review getReview() {
        return review;
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        JSONObject header = new JSONObject();
        header.put("BID", blockId);
        header.put("HPB", previousBlockHash);
        header.put("HCB", hashCurrentBlock);
        header.put("BTS", timestamp);
        header.put("model_name", modelName);
        header.put("model_version", modelVersion);
        header.put("modelID", modelId);
        json.put("blockHeader", header);

        JSONObject modelFileJson = new JSONObject();
        modelFileJson.put("DATA", modelFile.data.modelFile);
        json.put("model_file", new JSONObject[]{modelFileJson});

        JSONObject reviewJson = new JSONObject();
        reviewJson.put("DATA", review.data.modelReviews);
        json.put("reviews", new JSONObject[]{reviewJson});

        return json;
    }

    public static class ModelFile {
        ModelData data;

        public ModelFile(ModelData data) {
            this.data = data;
        }
    }

    public static class Review {
        ReviewData data;

        public Review(ReviewData data) {
            this.data = data;
        }
    }

    public static class ModelData {
        String modelFile;

        public ModelData(String modelFile) {
            this.modelFile = modelFile;
        }
    }

    public static class ReviewData {
        String modelReviews;

        public ReviewData(String modelReviews) {
            this.modelReviews = modelReviews;
        }
    }
}
