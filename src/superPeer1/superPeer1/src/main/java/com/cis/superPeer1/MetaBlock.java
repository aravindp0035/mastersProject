package com.cis.superPeer1;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class MetaBlock {
    private int blockId;
    private String previousBlockHash;
    private String hashCurrentBlock;
    private String timestamp;
    private String modelId;
    public static final String fileName = "metablock.json";
    
    public MetaBlock() {
    	
    }

    public MetaBlock(int blockId, String previousBlockHash, String hashCurrentBlock, String timestamp, String modelId) {
        this.blockId = blockId;
        this.previousBlockHash = previousBlockHash;
        this.hashCurrentBlock = hashCurrentBlock;
        this.timestamp = timestamp;
        this.modelId = modelId;
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("blockId", blockId);
        json.put("previousBlockHash", previousBlockHash);
        json.put("modelId", modelId);
        json.put("hashCurrentBlock", hashCurrentBlock);
        json.put("timestamp", timestamp);
        
        return json;
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

    public String getModelId() {
        return modelId;
    }
    
    public List<MetaBlock> loadMetaBlock(String fileName) {
    	ObjectMapper mapper = new ObjectMapper();
        List<MetaBlock> mb = new ArrayList<>();
        try {
            File file = new File(fileName);
            if (file.exists() && file.length() > 0) {
                mb = mapper.readValue(file, new TypeReference<List<MetaBlock>>() {});
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mb;
    }
    
}
