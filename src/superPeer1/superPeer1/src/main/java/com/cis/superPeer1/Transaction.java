package com.cis.superPeer1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class Transaction {
    private String transactionID;
    private String requester;
    private String provider;
    private String DES;
    private String modelID;
    public static final String fileName = "transaction.json";

    public Transaction() {
    }

    public Transaction(String requester, String provider, String DES, String modelID) {
        
        this.requester = requester;
        this.provider = provider;
        this.DES = DES;
        this.modelID = modelID;
        this.transactionID = setTransactionID();
    }

   
    public String getTransactionID() {
        return transactionID;
    }

    public String setTransactionID() {
       
    	 int s = getSize(fileName);
         return "Trans" + (s + 1);
    }

    public String getRequester() {
        return requester;
    }

    public void setRequester(String requester) {
        this.requester = requester;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getDES() {
        return DES;
    }

    public void setDES(String DES) {
        this.DES = DES;
    }

    public String getModelID() {
        return modelID;
    }

    public void setModelID(String modelID) {
        this.modelID = modelID;
    }

    public int getSize(String fileName) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            File file = new File(fileName);
            if (file.length() == 0) {
                return 0; 
            }
            List<Transaction> blocks = mapper.readValue(file,
                    mapper.getTypeFactory().constructCollectionType(List.class, Transaction.class));
            return blocks.size();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0; 
    }
    
    public List<Transaction> loadAllTransactions(String fileName) {
    	ObjectMapper mapper = new ObjectMapper();
        List<Transaction> transactions = new ArrayList<>();
        try {
            File file = new File(fileName);
            if (file.exists() && file.length() > 0) {
                transactions = mapper.readValue(file, new TypeReference<List<Transaction>>() {});
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    
    public void saveToJSON(String fileName) {
        ObjectMapper mapper = new ObjectMapper();
        List<Transaction> transactions = new ArrayList<>();
        try {
           
            File file = new File(fileName);
            if (file.exists() && file.length() > 0) {
                transactions = mapper.readValue(file, new TypeReference<List<Transaction>>() {});
            }
            
            transactions.add(this);

           
            mapper.writeValue(file, transactions);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

   

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionID='" + transactionID + '\'' +
                ", requester='" + requester + '\'' +
                ", provider='" + provider + '\'' +
                ", DES='" + DES + '\'' +
                ", modelID='" + modelID + '\'' +
                '}';
    }
    
}
