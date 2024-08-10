package com.cis.regularPeer;

import rmi.common.Interface.BlockchainInterface;

import java.rmi.RemoteException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class rpClientService {

    @Autowired
    private BlockchainInterface blockchain;
    
    

    public String[] Search(String modelID) {
        try {
            return blockchain.search(modelID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
	public void updateReviewFile(String modelID, String text) {
		// TODO Auto-generated method stub
		try {
            blockchain.addReviews(modelID,text);
        } catch (Exception e) {
            e.printStackTrace();
        }
		
	}

	public byte[] ExtractMLfile(String modelID) {
		// TODO Auto-generated method stub
		try {
            return blockchain.extractMLFile(modelID);
        } catch (Exception e) {
            e.printStackTrace();
        }
		return null;
	}

	public byte[] ExtractReviewsfile(String modelID) {
		// TODO Auto-generated method stub
		try {
            return blockchain.extractReviews(modelID);
        } catch (Exception e) {
            e.printStackTrace();
        }
		
		return null;
	}
	public int bcSize() {
		// TODO Auto-generated method stub
		try {
			return blockchain.bcSize();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

}